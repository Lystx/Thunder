
package io.thunder.utils.vson.manage.json;

import io.thunder.utils.vson.VsonValue;
import io.thunder.utils.vson.elements.VsonArray;
import io.thunder.utils.vson.elements.VsonLiteral;
import io.thunder.utils.vson.elements.VsonNumber;
import io.thunder.utils.vson.elements.VsonString;
import io.thunder.utils.vson.elements.object.VsonObject;
import io.thunder.utils.vson.other.VsonException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;


public class JsonParser {

    private static final int MIN_BUFFER_SIZE=10;
    private static final int DEFAULT_BUFFER_SIZE=1024;

    private final Reader reader;
    private final char[] buffer;
    private int bufferOffset;
    private int index;
    private int fill;
    private int line;
    private int lineOffset;
    private int current;
    private StringBuilder captureBuffer;
    private int captureStart;

    public JsonParser(String string) {
        this(new StringReader(string),
                Math.max(MIN_BUFFER_SIZE, Math.min(DEFAULT_BUFFER_SIZE, string.length())));
    }

    public JsonParser(Reader reader) {
        this(reader, DEFAULT_BUFFER_SIZE);
    }

    public JsonParser(Reader reader, int buffersize) {
        this.reader=reader;
        buffer=new char[buffersize];
        line=1;
        captureStart=-1;
    }

    public VsonValue parse() throws IOException {
        this.read();
        this.skipWhiteSpace();
        VsonValue result=readValue();
        this.skipWhiteSpace();
        if (!isEndOfText()) throw error("Unexpected character");
        return result;
    }

    private VsonValue readValue() throws IOException {
        switch(current) {
            case 'n':
                return readNull();
            case 't':
                return readTrue();
            case 'f':
                return readFalse();
            case '"':
                return readString();
            case '[':
                return readArray();
            case '{':
                return readObject();
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return readNumber();
            default:
                throw expected("value");
        }
    }

    private VsonArray readArray() throws IOException {
        read();
        VsonArray array=new VsonArray();
        skipWhiteSpace();
        if (readIf(']')) {
            return array;
        }
        do {
            skipWhiteSpace();
            array.append(readValue());
            skipWhiteSpace();
        } while (readIf(','));
        if (!readIf(']')) {
            throw expected("',' or ']'");
        }
        return array;
    }

    private VsonObject readObject() throws IOException {
        read();
        VsonObject object = new VsonObject();
        skipWhiteSpace();
        if (readIf('}')) {
            return object;
        }
        do {
            skipWhiteSpace();
            String name=readName();
            skipWhiteSpace();
            if (!readIf(':')) {
                throw expected("':'");
            }
            skipWhiteSpace();
            object.submit(name, readValue());
            skipWhiteSpace();
        } while (readIf(','));
        if (!readIf('}')) {
            throw expected("',' or '}'");
        }
        return object;
    }

    private String readName() throws IOException {
        if (current!='"') {
            throw expected("name");
        }
        return readStringInternal();
    }

    private VsonValue readNull() throws IOException {
        read();
        readRequiredChar('u');
        readRequiredChar('l');
        readRequiredChar('l');
        return VsonLiteral.NULL;
    }

    private VsonValue readTrue() throws IOException {
        read();
        readRequiredChar('r');
        readRequiredChar('u');
        readRequiredChar('e');
        return VsonLiteral.TRUE;
    }

    private VsonValue readFalse() throws IOException {
        read();
        readRequiredChar('a');
        readRequiredChar('l');
        readRequiredChar('s');
        readRequiredChar('e');
        return VsonLiteral.FALSE;
    }

    private void readRequiredChar(char ch) throws IOException {
        if (!readIf(ch)) {
            throw expected("'"+ch+"'");
        }
    }

    private VsonValue readString() throws IOException {
        return new VsonString(readStringInternal());
    }

    private String readStringInternal() throws IOException {
        read();
        startCapture();
        while (current!='"') {
            if (current=='\\') {
                pauseCapture();
                readEscape();
                startCapture();
            } else if (current<0x20) {
                throw expected("valid string character");
            } else {
                read();
            }
        }
        String string=endCapture();
        read();
        return string;
    }

    private void readEscape() throws IOException {
        read();
        switch(current) {
            case '"':
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
                for(int i=0; i<4; i++) {
                    read();
                    if (!isHexDigit()) {
                        throw expected("hexadecimal digit");
                    }
                    hexChars[i]=(char)current;
                }
                captureBuffer.append((char)Integer.parseInt(new String(hexChars), 16));
                break;
            default:
                throw expected("valid escape sequence");
        }
        read();
    }

    private VsonValue readNumber() throws IOException {
        startCapture();
        readIf('-');
        int firstDigit=current;
        if (!readDigit()) {
            throw expected("digit");
        }
        if (firstDigit!='0') {
            while (readDigit()) {
            }
        }
        readFraction();
        readExponent();
        return new VsonNumber(Double.parseDouble(endCapture()));
    }

    private boolean readFraction() throws IOException {
        if (!readIf('.')) {
            return false;
        }
        if (!readDigit()) {
            throw expected("digit");
        }
        while (readDigit()) {
        }
        return true;
    }

    private boolean readExponent() throws IOException {
        if (!readIf('e') && !readIf('E')) {
            return false;
        }
        if (!readIf('+')) {
            readIf('-');
        }
        if (!readDigit()) {
            throw expected("digit");
        }
        while (readDigit()) {
        }
        return true;
    }

    private boolean readIf(char ch) throws IOException {
        if (current!=ch) {
            return false;
        }
        read();
        return true;
    }

    private boolean readDigit() throws IOException {
        if (!isDigit()) {
            return false;
        }
        read();
        return true;
    }

    private void skipWhiteSpace() throws IOException {
        while (isWhiteSpace()) {
            read();
        }
    }

    private void read() throws IOException {
        if (index==fill) {
            if (captureStart!=-1) {
                captureBuffer.append(buffer, captureStart, fill-captureStart);
                captureStart=0;
            }
            bufferOffset += fill;
            fill=reader.read(buffer, 0, buffer.length);
            index=0;
            if (fill==-1) {
                current=-1;
                return;
            }
        }
        if (current=='\n') {
            line++;
            lineOffset=bufferOffset+index;
        }
        current=buffer[index++];
    }

    private void startCapture() {
        if (captureBuffer==null) {
            captureBuffer=new StringBuilder();
        }
        captureStart=index-1;
    }

    private void pauseCapture() {
        int end=current==-1 ? index : index-1;
        captureBuffer.append(buffer, captureStart, end-captureStart);
        captureStart=-1;
    }

    private String endCapture() {
        int end=current==-1 ? index : index-1;
        String captured;
        if (captureBuffer.length()>0) {
            captureBuffer.append(buffer, captureStart, end-captureStart);
            captured=captureBuffer.toString();
            captureBuffer.setLength(0);
        } else {
            captured=new String(buffer, captureStart, end-captureStart);
        }
        captureStart=-1;
        return captured;
    }

    private VsonException expected(String expected) {
        if (isEndOfText()) {
            return error("Unexpected end of input");
        }
        return error("Expected " +expected);
    }

    private VsonException error(String message) {
        int absIndex=bufferOffset+index;
        int column=absIndex-lineOffset;
        int offset=isEndOfText() ? absIndex : absIndex-1;
        return new VsonException(message, offset, line, column-1);
    }

    private boolean isWhiteSpace() {
        return current==' ' || current=='\t' || current=='\n' || current=='\r';
    }

    private boolean isDigit() {
        return current>='0' && current<='9';
    }

    private boolean isHexDigit() {
        return current>='0' && current<='9'
                || current>='a' && current<='f'
                || current>='A' && current<='F';
    }

    private boolean isEndOfText() {
        return current==-1;
    }

}
