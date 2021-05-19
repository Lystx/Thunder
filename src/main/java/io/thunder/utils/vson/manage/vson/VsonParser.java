
package io.thunder.utils.vson.manage.vson;

import io.thunder.utils.vson.VsonValue;
import io.thunder.utils.vson.elements.VsonArray;
import io.thunder.utils.vson.elements.VsonLiteral;
import io.thunder.utils.vson.elements.VsonNumber;
import io.thunder.utils.vson.elements.VsonString;
import io.thunder.utils.vson.elements.object.VsonObject;
import io.thunder.utils.vson.elements.other.Dsf;
import io.thunder.utils.vson.other.IVsonProvider;
import io.thunder.utils.vson.other.TempVsonOptions;
import io.thunder.utils.vson.other.VsonException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class VsonParser {

    private Reader reader;
    private int index;
    private int line;
    private int lineOffset;
    private int current;
    private StringBuilder captureBuffer, peek;
    private boolean capture;

    private boolean legacyRoot;
    private String buffer;
    private IVsonProvider[] dsfProviders;

    public VsonParser() {

    }
    public VsonParser(TempVsonOptions options) {
        this("null", options);
    }

    public VsonParser(String string, TempVsonOptions options) {
        this.buffer = string;
        this.reset();
        this.dsfProviders = (options != null) ? options.getDsfProviders() : new IVsonProvider[0];
        this.legacyRoot = options == null || options.getParseLegacyRoot();
    }

    public VsonParser(String input) {
        this(input, new TempVsonOptions());
    }

    public VsonParser(Reader reader, TempVsonOptions options) throws IOException {
        this(readToEnd(reader), options);
    }

    public static String readToEnd(Reader reader) throws IOException {
        int n;
        char[] part = new char[8*1024];
        StringBuilder sb = new StringBuilder();
        while ((n = reader.read(part, 0, part.length)) != -1) {
            sb.append(part, 0, n);
        }
        return sb.toString();
    }

    public void reset() {
        this.index = lineOffset=current=0;
        this.line = 1;
        this.peek = new StringBuilder();
        this.reader = new StringReader(buffer);
        this.capture = false;
        this.captureBuffer = null;
    }

    public VsonValue parse() throws IOException {
        return this.parse(this.buffer);
    }
    public VsonValue parse(String input) throws IOException {

        if (this.dsfProviders == null) {
            this.legacyRoot = true;
            this.dsfProviders = new IVsonProvider[0];
        }
        this.buffer = input;

        this.reset();
        this.read();
        this.skip();

        if (legacyRoot) {
            switch (current) {
                case '[':
                case '{':
                    return checkTrailing(readValue());
                default:
                    try {
                        return checkTrailing(readObject(true));
                    } catch (Exception exception) {
                        this.reset();
                        this.read();
                        this.skip();
                        try {
                            return this.checkTrailing(readValue());
                        } catch (Exception e) {
                            throw exception;
                        }
                    }
            }
        } else {
            return this.checkTrailing(readValue());
        }
    }

    public VsonValue checkTrailing(VsonValue v) throws VsonException, IOException {
        this.skip();
        if (!isEndOfText()) {
            throw error("Extra characters in input: " + this.current);
        }
        return v;
    }

    private VsonValue readValue() throws IOException {
        switch (current) {
            case '\'':

                //TODO: READING COMMENTS
            case '"':
                return readString();
            case '[':
                return readArray();
            case '{':
                return readObject(false);
            default:
                return readDefaultValue();
        }
    }

    private VsonValue readDefaultValue() throws IOException {
        StringBuilder value = new StringBuilder();
        int first=current;
        if (VsonValue.isPunctuatorChar(first)) {
            throw error("Found a punctuator character '" + (char) first + "' when expecting a quoteless string (check your syntax)");
        }
        value.append((char)current);
        for (;;) {
            read();
            boolean isEol = this.current < 0 || this.current == '\r' || this.current == '\n';
            if (isEol || this.current == ',' ||
                    this.current == '}' || current == ']' ||
                    this.current == '#' ||
                    this.current == '/' && (this.peek() == '/' || this.peek() == '*')
            ) {
                switch (first) {
                    case 'f':
                    case 'n':
                    case 't':
                        String svalue = value.toString().trim();
                        switch (svalue) {
                            case "false":
                                return VsonLiteral.FALSE;
                            case "null":
                                return VsonLiteral.NULL;
                            case "true":
                                return VsonLiteral.TRUE;
                        }
                        break;
                    default:
                        if (first == '-' || first >= '0' && first <= '9') {
                            VsonValue n = tryParseNumber(value, false);
                            if (n != null) {
                                return n;
                            }
                        }
                }
                if (isEol) {
                    return Dsf.parse(dsfProviders, value.toString().trim());
                }
            }
            value.append((char)current);
        }
    }

    private VsonArray readArray() throws IOException {
        this.read();
        VsonArray array = new VsonArray();
        this.skip();
        if (this.readIf(']')) {
            return array;
        }
        while (true) {
            this.skip();
            array.append(this.readValue());
            this.skip();
            if (this.readIf(',')) {
                this.skip();
            }
            if (readIf(']')) {
                break;
            } else if (this.isEndOfText()) {
                throw error("End of input while parsing an array (did you forget a closing ']'?)");
            }
        }
        return array;
    }

    private VsonObject readObject(boolean objectWithoutBraces) throws IOException {
        if (!objectWithoutBraces) {
            this.read();
        }
        VsonObject object = new VsonObject();
        this.skip();
        while (true) {
            if (objectWithoutBraces) {
                if (this.isEndOfText()) {
                    break;
                }
            } else {
                if (this.isEndOfText()) {
                    throw error("End of input while parsing an object (did you forget a closing '}'?)");
                }
                if (this.readIf('}')) {
                    break;
                }
            }
            String name = this.readName();
            this.skip();
            if (!this.readIf(':')) {
                throw this.expected("':'");
            }
            this.skip();
            object.submit(name, this.readValue());
            this.skip();
            if (this.readIf(',')) {
                this.skip();
            }
        }
        return object;
    }

    private String readName() throws IOException {
        if (this.current == '"' || this.current == '\'') {
            return this.readStringInternal(false);
        }

        StringBuilder name = new StringBuilder();
        int space =- 1, start = index;
        while (true) {
            if (this.current == ':') {
                if (name.length() == 0) {
                    throw error("Found ':' but no key name (for an empty key name use quotes)");
                }
                else if (space >= 0 && space != name.length()) {
                     index = start + space;
                     throw error("Found whitespace in your key name (use quotes to include)");
                }
                return name.toString();
            } else if (isWhiteSpace(current)) {
                if (space < 0) {
                    space = name.length();
                }
            } else if (current < ' ') {
                throw error("Name is not closed");
            } else if (VsonValue.isPunctuatorChar(current)) {
                throw error("Found '" + (char)current + "' where a key name was expected (check your syntax or use quotes if the key name includes {}[],: or whitespace)");
            } else {
                name.append((char)current);
            }
            this.read();
        }
    }

    private String readMultiLineString() throws IOException {

        StringBuilder sb = new StringBuilder();
        int triple=0;

        int indent = index - lineOffset - 4;

        for (; ; ) {
            if (isWhiteSpace(current) && this.current != '\n') {
                this.read();
            } else {
                break;
            }
        }
        if (this.current == '\n') {
            this.read();
            this.skipIndent(indent);
        }

        while (true) {
            if (this.current < 0) {
                throw error("Bad multiline string");
            }
            else if (this.current == '\'') {
                triple++;
                this.read();
                if (triple == 3) {
                    if (sb.charAt(sb.length() - 1) == '\n') {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    return sb.toString();
                } else {
                    continue;
                }
            } else {
                while (triple > 0) {
                    sb.append('\'');
                    triple--;
                }
            }
            if (this.current == '\n') {
                sb.append('\n');
                this.read();
                this.skipIndent(indent);
            } else {
                if (this.current != '\r') {
                    sb.append((char)current);
                }
                this.read();
            }
        }
    }

    private void skipIndent(int indent) throws IOException {
        while (indent-->0) {
            if (isWhiteSpace(current) && current!='\n') {
                this.read();
            } else {
                break;
            }
        }
    }

    private VsonValue readString() throws IOException {
        return new VsonString(this.readStringInternal(true));
    }

    private String readStringInternal(boolean allowML) throws IOException {
        int exitCh = current;
        this.read();
        this.startCapture();
        while (this.current != exitCh) {
            if (this.current =='\\') {
                this.readEscape();
            } else if (current < 0x20) {
                throw expected("valid string character");
            } else {
                this.read();
            }
        }
        String string = this.endCapture();
        this.read();

        if (allowML && exitCh == '\'' && this.current == '\'' && string.length() == 0) {
            this.read();
            return this.readMultiLineString();
        } else {
            return string;
        }
    }

    private void readEscape() throws IOException {
        this.pauseCapture();
        this.read();
        switch(current) {
            case '"':
            case '\'':
            case '/':
            case '\\':
                captureBuffer.append((char)current);
                break;
            case 'b':
                captureBuffer.append('\b');
                break;
            case 'f':
                captureBuffer.append('\f');
                break;
            case 'n':
                captureBuffer.append('\n');
                break;
            case 'r':
                captureBuffer.append('\r');
                break;
            case 't':
                captureBuffer.append('\t');
                break;
            case 'u':
                char[] hexChars=new char[4];
                for (int i=0; i<4; i++) {
                    this.read();
                    if (!this.isHexDigit()) {
                        throw expected("hexadecimal digit");
                    }
                    hexChars[i]=(char)current;
                }
                captureBuffer.append((char)Integer.parseInt(new String(hexChars), 16));
                break;
            default:
                throw expected("valid escape sequence");
        }
        capture=true;
        read();
    }

    private static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    public static VsonValue tryParseNumber(StringBuilder value, boolean stopAtNext) throws IOException {
        int idx = 0, len = value.length();
        if (idx < len && value.charAt(idx) == '-') {
            idx++;
        }

        if (idx >= len) {
            return null;
        }
        char first = value.charAt(idx++);
        if (!isDigit(first)) {
            return null;
        }

        if (first == '0' && idx < len && isDigit(value.charAt(idx))) {
            return null;
        }

        while (idx < len && isDigit(value.charAt(idx))) {
            idx++;
        }

        if (idx < len && value.charAt(idx) == '.') {
            idx++;
            if (idx >= len || !isDigit(value.charAt(idx++))) {
                return null;
            }
            while (idx < len && isDigit(value.charAt(idx))){
                idx++;
            }
        }

        if (idx < len && Character.toLowerCase(value.charAt(idx)) == 'e') {
            idx++;
            if (idx < len && (value.charAt(idx) == '+' || value.charAt(idx) == '-')) {
                idx++;
            }

            if (idx >= len || !isDigit(value.charAt(idx++))) {
                return null;
            }
            while (idx < len && isDigit(value.charAt(idx))) {
                idx++;
            }
        }

        int last = idx;
        while (idx < len && isWhiteSpace(value.charAt(idx))) {
            idx++;
        }

        boolean foundStop = false;
        if (idx < len && stopAtNext) {
            char ch = value.charAt(idx);
            if (ch == ',' || ch == '}' || ch == ']' || ch == '#' || ch == '/' && (len > idx + 1 && (value.charAt(idx + 1) == '/' || value.charAt(idx + 1) == '*')))
                foundStop = true;
        }

        if (idx < len && !foundStop) {
            return null;
        }
        return new VsonNumber(Double.parseDouble(value.substring(0, last)));
    }

    public static VsonValue tryParseNumber(String value, boolean stopAtNext) throws IOException {
        return tryParseNumber(new StringBuilder(value), stopAtNext);
    }

    private boolean readIf(char ch) throws IOException {
        if (current != ch) {
            return false;
        }
        this.read();
        return true;
    }

    private void skip() throws IOException {
        while (!this.isEndOfText()) {
            while (this.isWhiteSpace()) {
                this.read();
            }
            if (this.current == '#' || this.current == '/' && this.peek() == '/') {
                do {
                    this.read();
                } while (this.current >= 0 && this.current != '\n');
            }
            else if (this.current == '/' && this.peek() == '*') {
                read();
                do {
                    read();
                } while (current>=0 && !(current=='*' && peek()=='/'));
                this.read();
                this.read();

                //TODO: CHECKING LINE ABOVE
            } else {
                break;
            }
        }
    }

    private int peek(int idx) throws IOException {
        while (idx >= peek.length()) {
            int c = reader.read();
            if (c < 0) {
                return c;
            }
            peek.append((char)c);
        }
        return peek.charAt(idx);
    }

    private int peek() throws IOException {
        return this.peek(0);
    }

    private void read() throws IOException {
        if (this.current == '\n') {
            this.line++;
            this.lineOffset=index;
        }

        if (peek.length() > 0) {
            this.current = peek.charAt(0);
            peek.deleteCharAt(0);
        } else {
            this.current = reader.read();
        }

        if (this.current < 0) {
            return;
        }

        this.index++;
        if (this.capture) {
            this.captureBuffer.append((char)current);
        }
    }

    private void startCapture() {
        if (this.captureBuffer == null) {
            this.captureBuffer = new StringBuilder();
        }
        this.capture = true;
        this.captureBuffer.append((char)current);
    }

    private void pauseCapture() {
        int len = this.captureBuffer.length();
        if (len > 0) {
            this.captureBuffer.deleteCharAt(len-1);
        }
        this.capture = false;
    }

    private String endCapture() {
        this.pauseCapture();
        String captured;
        if (captureBuffer.length() > 0) {
            captured = captureBuffer.toString();
            this.captureBuffer.setLength(0);
        } else {
            captured = "";
        }
        this.capture = false;
        return captured;
    }

    private VsonException expected(String expected) {
        if (this.isEndOfText()) {
            return error("Unexpected end of input");
        }
        return error("Expected "+expected);
    }

    private VsonException error(String message) {
        int column = index-lineOffset;
        int offset = isEndOfText() ? index : index - 1;
        return new VsonException(message, offset, line, column-1);
    }

    public static boolean isWhiteSpace(int ch) {
        return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
    }

    private boolean isWhiteSpace() {
        return isWhiteSpace((char)current);
    }

    private boolean isHexDigit() {
        return this.current >= '0' && this.current <= '9'
                || this.current >= 'a' && this.current <= 'f'
                || this.current >= 'A' && this.current <= 'F';
    }

    private boolean isEndOfText() {
        return this.current == -1;
    }
}
