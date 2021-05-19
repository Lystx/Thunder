package io.thunder.utils.vson.manage.vson;

import io.thunder.utils.vson.VsonValue;
import io.thunder.utils.vson.elements.VsonArray;
import io.thunder.utils.vson.elements.object.VsonMember;
import io.thunder.utils.vson.elements.object.VsonObject;
import io.thunder.utils.vson.elements.other.Dsf;
import io.thunder.utils.vson.other.Pair;
import io.thunder.utils.vson.enums.VsonComment;
import io.thunder.utils.vson.manage.json.JsonWriter;
import io.thunder.utils.vson.other.IVsonProvider;
import io.thunder.utils.vson.other.TempVsonOptions;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

public class VsonWriter {

    private IVsonProvider[] dsfProviders;

    public static Pattern needsEscapeName=Pattern.compile("[,\\{\\[\\}\\]\\s:#\"']|//|/\\*");

    public VsonWriter(TempVsonOptions options) {
        if (options!=null) {
            this.dsfProviders = options.getDsfProviders();
        } else {
            dsfProviders = new IVsonProvider[0];
        }
    }

    public void nl(Writer tw, int level) throws IOException {
        tw.write(System.getProperty("line.separator"));
        for (int i=0; i<level; i++) tw.write("  ");
    }

    public void save(VsonObject parent, VsonValue value, Writer tw, int level, VsonMember member, String separator, boolean noIndent) throws IOException {
        if (value==null) {
            tw.write(separator);
            tw.write("null");
            return;
        }

        String dsfValue= Dsf.stringify(dsfProviders, value);
        if (dsfValue!=null) {
            tw.write(separator);
            tw.write(dsfValue);
            return;
        }

        Pair<String[], VsonComment> comment = comment(parent, member);
        switch (value.getType()) {
            case OBJECT:
                VsonObject obj= value.asVsonObject();
                if (!noIndent) { if (obj.size()>0) nl(tw, level); else tw.write(separator); }
                tw.write('{');

                for (VsonMember pair : obj) {
                    nl(tw, level+1);
                    tw.write(escapeName(pair.getName()));
                    tw.write(":");
                    save(obj, pair.getValue(), tw, level+1, pair," ", false);
                }

                if (obj.size()>0) nl(tw, level);
                tw.write('}');
                break;
            case ARRAY:
                VsonArray arr=value.asArray();
                int n=arr.size();
                if (!noIndent) { if (n>0) nl(tw, level); else tw.write(separator); }
                tw.write('[');
                for (int i=0; i<n; i++) {
                    nl(tw, level+1);
                    save(parent, arr.get(i), tw, level+1, member, "", true);
                }
                if (n>0) nl(tw, level);
                tw.write(']');
                break;
            case BOOLEAN:
                tw.write(separator);
                String val = value.isTrue() ? "true" : "false";
                if (comment != null) {
                    if (comment.getValue().equals(VsonComment.BEHIND_VALUE)) {
                        tw.write(val + " //" + comment.getKey()[0]);
                        break;
                    } else if (comment.getValue().equals(VsonComment.UNDER_VALUE)) {
                        tw.write(val);
                        tw.write("\n");
                        tw.write("  //" + comment.getKey()[0]);
                        break;
                    } else if (comment.getValue().equals(VsonComment.MULTI_LINE)) {
                        tw.write(val);
                        tw.write("  \n");
                        tw.write("  /*\n");
                        for (String s : comment.getKey()) {
                            tw.write("     " + s + "\n");
                        }
                        tw.write("  */");
                        break;
                    }
                }
                tw.write(val);
                break;
            case STRING:
                if (comment != null) {
                    if (comment.getValue().equals(VsonComment.BEHIND_VALUE)) {
                        writeString(value.asString() + " //" + comment.getKey()[0], tw, level, separator);
                        break;
                    } else if (comment.getValue().equals(VsonComment.UNDER_VALUE)) {
                        writeString(value.asString(), tw, level, separator);
                        tw.write("\n");
                        tw.write("  //" + comment.getKey()[0]);
                        break;
                    } else if (comment.getValue().equals(VsonComment.MULTI_LINE)) {
                        writeString(value.asString() , tw, level, separator);
                        tw.write("  \n");
                        tw.write("  /*\n");
                        for (String s : comment.getKey()) {
                            tw.write("     " + s + "\n");
                        }
                        tw.write("  */");
                        break;
                    }
                }
                writeString(value.asString() , tw, level, separator);
                break;
            default:
                tw.write(separator);
                if (comment != null) {
                    if (comment.getValue().equals(VsonComment.BEHIND_VALUE)) {
                        tw.write(value.toString()  + " //" + comment.getKey()[0]);
                        break;
                    } else if (comment.getValue().equals(VsonComment.UNDER_VALUE)) {
                        tw.write(value.toString());
                        tw.write("\n");
                        tw.write("  //" + comment.getKey()[0]);
                        break;
                    } else if (comment.getValue().equals(VsonComment.MULTI_LINE)) {
                        tw.write(value.toString());
                        tw.write("  \n");
                        tw.write("  /*\n");
                        for (String s : comment.getKey()) {
                            tw.write("     " + s + "\n");
                        }
                        tw.write("  */");
                        break;
                    }
                }
                tw.write(value.toString());
                break;
        }
    }



    public Pair<String[], VsonComment> comment(VsonObject parent, VsonMember member) {
        if (member != null && parent != null && parent.getComments() != null) {
            int level = parent.indexOf(member.getName());
            if (parent.getComments().get(level) == null || parent.getComments().get(level).getValue() == null) {
                return null;
            }
            return parent.getComments().get(level);
        }
        return null;
    }


    public static String escapeName(String name) {
        if (name.length()==0 || needsEscapeName.matcher(name).find())
            return "\""+ JsonWriter.escapeString(name)+"\"";
        else
            return name;
    }

    public void writeString(String value, Writer tw, int level, String separator) throws IOException {
        if (value.length()==0) { tw.write(separator+"\"\""); return; }

        char left=value.charAt(0), right=value.charAt(value.length()-1);
        char left1=value.length()>1?value.charAt(1):'\0', left2=value.length()>2?value.charAt(2):'\0';
        boolean doEscape=false;
        char[] valuec=value.toCharArray();
        for(char ch : valuec) {
            if (needsQuotes(ch)) { doEscape=true; break; }
        }

        if (doEscape ||
                VsonParser.isWhiteSpace(left) || VsonParser.isWhiteSpace(right) ||
                left=='"' ||
                left=='\'' ||
                left=='#' ||
                left=='/' && (left1=='*' || left1=='/') ||
                VsonValue.isPunctuatorChar(left) ||
                VsonParser.tryParseNumber(value, true)!=null ||
                startsWithKeyword(value)) {
            boolean noEscape=true;
            for(char ch : valuec) { if (needsEscape(ch)) { noEscape=false; break; } }
            if (noEscape) { tw.write(separator+"\""+value+"\""); return; }

            boolean noEscapeML=true, allWhite=true;
            for(char ch : valuec) {
                if (needsEscapeML(ch)) { noEscapeML=false; break; }
                else if (!VsonParser.isWhiteSpace(ch)) allWhite=false;
            }
            if (noEscapeML && !allWhite && !value.contains("'''")) writeMLString(value, tw, level, separator);
            else tw.write(separator+"\""+JsonWriter.escapeString(value)+"\"");
        }
        else tw.write(separator+value);
    }

    public void writeMLString(String value, Writer tw, int level, String separator) throws IOException {
        String[] lines=value.replace("\r", "").split("\n", -1);

        if (lines.length==1) {
            tw.write(separator+"'''");
            tw.write(lines[0]);
            tw.write("'''");
        }
        else {
            level++;
            nl(tw, level);
            tw.write("'''");

            for (String line : lines) {
                nl(tw, line.length()>0?level:0);
                tw.write(line);
            }
            nl(tw, level);
            tw.write("'''");
        }
    }

    public static boolean startsWithKeyword(String text) {
        int p;
        if (text.startsWith("true") || text.startsWith("null")) p=4;
        else if (text.startsWith("false")) p=5;
        else return false;
        while (p<text.length() && VsonParser.isWhiteSpace(text.charAt(p))) p++;
        if (p==text.length()) return true;
        char ch=text.charAt(p);
        return ch==',' || ch=='}' || ch==']' || ch=='#' || ch=='/' && (text.length()>p+1 && (text.charAt(p+1)=='/' || text.charAt(p+1)=='*'));
    }

    public static boolean needsQuotes(char c) {
        switch (c) {
            case '\t':
            case '\f':
            case '\b':
            case '\n':
            case '\r':
                return true;
            default:
                return false;
        }
    }

    public static boolean needsEscape(char c) {
        switch (c) {
            case '\"':
            case '\\':
                return true;
            default:
                return needsQuotes(c);
        }
    }

    public static boolean needsEscapeML(char c) {
        switch (c) {
            case '\n':
            case '\r':
            case '\t':
                return false;
            default:
                return needsQuotes(c);
        }
    }
}
