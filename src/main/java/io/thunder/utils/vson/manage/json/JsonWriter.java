
package io.thunder.utils.vson.manage.json;

import io.thunder.utils.vson.VsonValue;
import io.thunder.utils.vson.elements.VsonArray;
import io.thunder.utils.vson.elements.object.VsonMember;
import io.thunder.utils.vson.elements.object.VsonObject;
import io.thunder.utils.vson.enums.VsonType;
import io.thunder.utils.vson.manage.vson.VsonParser;

import java.io.IOException;
import java.io.Writer;


public class JsonWriter {

    public boolean format;

    public JsonWriter(boolean format) {
        this.format = format;
    }

    public void nl(Writer tw, int level) throws IOException {
        if (format) {
            tw.write(System.getProperty("line.separator"));
            for (int i=0; i<level; i++) tw.write("  ");
        }
    }

    public void save(VsonValue value, Writer tw, int level) throws IOException {
        boolean following=false;
        switch (value.getType()) {
            case OBJECT:
                VsonObject obj=value.asVsonObject();
                if (obj.size()>0) nl(tw, level);
                tw.write('{');
                for (VsonMember pair : obj) {
                    if (following) tw.write(",");
                    nl(tw, level+1);
                    tw.write('\"');
                    tw.write(escapeString(pair.getName()));
                    tw.write("\":");
                    //save(, tw, level+1, " ", false);
                    VsonValue v=pair.getValue();
                    VsonType vType=v.getType();
                    if (format && vType!= VsonType.ARRAY && vType!= VsonType.OBJECT) tw.write(" ");
                    if (v==null) tw.write("null");
                    else save(v, tw, level+1);
                    following=true;
                }
                if (following) nl(tw, level);
                tw.write('}');
                break;
            case ARRAY:
                VsonArray arr=value.asArray();
                int n=arr.size();
                if (n>0) nl(tw, level);
                tw.write('[');
                for (int i=0; i<n; i++) {
                    if (following) tw.write(",");
                    VsonValue v=arr.get(i);
                    VsonType vType=v.getType();
                    if (vType!= VsonType.ARRAY && vType!= VsonType.OBJECT) nl(tw, level+1);
                    save(v, tw, level+1);
                    following=true;
                }
                if (following) nl(tw, level);
                tw.write(']');
                break;
            case BOOLEAN:
                tw.write(value.isTrue()?"true":"false");
                break;
            case STRING:
                tw.write('"');
                tw.write(escapeString(value.asString()));
                tw.write('"');
                break;
            default:
                tw.write(value.toString());
                break;
        }
    }

    public static String escapeName(String name) {
        boolean needsEscape=name.length()==0;
        for(char ch : name.toCharArray()) {
            if (VsonParser.isWhiteSpace(ch) || ch=='{' || ch=='}' || ch=='[' || ch==']' || ch==',' || ch==':') {
                needsEscape=true;
                break;
            }
        }
        if (needsEscape) return "\""+ JsonWriter.escapeString(name)+"\"";
        else return name;
    }

    public static String escapeString(String src) {
        if (src==null) return null;

        for (int i=0; i<src.length(); i++) {
            if (getEscapedChar(src.charAt(i))!=null) {
                StringBuilder sb=new StringBuilder();
                if (i>0) sb.append(src, 0, i);
                return doEscapeString(sb, src, i);
            }
        }
        return src;
    }

    private static String doEscapeString(StringBuilder sb, String src, int cur) {
        int start=cur;
        for (int i=cur; i<src.length(); i++) {
            String escaped=getEscapedChar(src.charAt(i));
            if (escaped!=null) {
                sb.append(src, start, i);
                sb.append(escaped);
                start=i+1;
            }
        }
        sb.append(src, start, src.length());
        return sb.toString();
    }

    private static String getEscapedChar(char c) {
        switch (c) {
            case '\"': return "\\\"";
            case '\t': return "\\t";
            case '\n': return "\\n";
            case '\r': return "\\r";
            case '\f': return "\\f";
            case '\b': return "\\b";
            case '\\': return "\\\\";
            default: return null;
        }
    }
}
