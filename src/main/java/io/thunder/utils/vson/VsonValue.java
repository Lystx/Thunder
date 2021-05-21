
package io.thunder.utils.vson;

import io.thunder.utils.vson.elements.VsonArray;
import io.thunder.utils.vson.elements.VsonLiteral;
import io.thunder.utils.vson.elements.VsonNumber;
import io.thunder.utils.vson.elements.VsonString;
import io.thunder.utils.vson.elements.object.VsonObject;
import io.thunder.utils.vson.enums.FileFormat;
import io.thunder.utils.vson.enums.VsonType;
import io.thunder.utils.vson.manage.WritingBuffer;
import io.thunder.utils.vson.manage.json.JsonWriter;
import io.thunder.utils.vson.manage.vson.VsonParser;
import io.thunder.utils.vson.manage.vson.VsonWriter;
import io.thunder.utils.vson.other.TempVsonOptions;
import lombok.SneakyThrows;

import javax.swing.*;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;

public abstract class VsonValue implements Serializable {

    public static VsonValue valueOf(int value) {
        return new VsonNumber(value);
    }

    public static VsonValue valueOf(long value) {
        return new VsonNumber(value);
    }

    public static VsonValue valueOf(float value) {
        return new VsonNumber(value);
    }

    public static VsonValue valueOf(double value) {
        return new VsonNumber(value);
    }

    public static VsonValue valueOf(String string) {
        return string == null ? VsonLiteral.NULL :  new VsonString(string);
    }

    public static VsonValue valueOf(boolean value) {
        return value ? VsonLiteral.TRUE : VsonLiteral.FALSE;
    }

    @SneakyThrows
    public static String format(String s) {
        return new VsonParser().parse(s).toString(FileFormat.JSON);
    }


    public abstract VsonType getType();

    public boolean isObject() {
        return false;
    }

    public boolean isArray() {
        return false;
    }

    public boolean isNumber() {
        return false;
    }

    public boolean isShort() {
        return false;
    }

    public boolean isDouble() {
        return false;
    }

    public boolean isLong() {
        return false;
    }

    public boolean isByte() {
        return false;
    }

    public boolean isInt() {
        return false;
    }
    public boolean isFloat() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public boolean isTrue() {
        return false;
    }

    public boolean isFalse() {
        return false;
    }

    public boolean isNull() {
        return false;
    }

    public VsonObject asVsonObject() {
        throw new UnsupportedOperationException("Not an object: "+toString());
    }

    public VsonArray asArray() {
        throw new UnsupportedOperationException("Not an array: "+toString());
    }

    public short asShort() {
        throw new UnsupportedOperationException("Not a short: "+toString());
    }

    public byte asByte() {
        throw new UnsupportedOperationException("Not a byte: "+toString());
    }

    public int asInt() {
        throw new UnsupportedOperationException("Not a number: "+toString());
    }

    public long asLong() {
        throw new UnsupportedOperationException("Not a number: "+toString());
    }

    public float asFloat() {
        throw new UnsupportedOperationException("Not a number: "+toString());
    }

    public double asDouble() {
        throw new UnsupportedOperationException("Not a number: "+toString());
    }

    public String asString() {
        throw new UnsupportedOperationException("Not a string: "+toString());
    }

    public Boolean asBoolean() {
        throw new UnsupportedOperationException("Not a boolean: "+toString());
    }

    public Object asObject() {
        throw new UnsupportedOperationException("Not a DSF");
    }

    public void writeTo(Writer writer, FileFormat format) throws IOException {
        WritingBuffer buffer=new WritingBuffer(writer, 128);
        switch (format) {
            case RAW_JSON: new JsonWriter(false).save(this, buffer, 0); break;
            case JSON: new JsonWriter(true).save(this, buffer, 0); break;
            case VSON: new VsonWriter(null).save(null, this, buffer, 0, null, "", true); break;
        }
        buffer.flush();
    }

    public void writeTo(Writer writer, TempVsonOptions options) throws IOException {
        if (options==null) throw new NullPointerException("options is null");
        WritingBuffer buffer=new WritingBuffer(writer, 128);
        new VsonWriter(options).save(null, this, buffer, 0, null, "", true);
        buffer.flush();
    }

    @Override
    public String toString() {
        return this.toString(FileFormat.VSON);
    }

    public String toString(FileFormat format) {
        StringWriter writer = new StringWriter();
        try {
            writeTo(writer, format);
        } catch(IOException exception) {
            throw new RuntimeException(exception);
        }
        return writer.toString();
    }


    public String toString(TempVsonOptions options) {
        StringWriter writer=new StringWriter();
        try {
            writeTo(writer, options);
        } catch(IOException exception) {
            throw new RuntimeException(exception);
        }
        return writer.toString();
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(object);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static boolean isPunctuatorChar(int c) {
        return c == '{' || c == '}' || c == '[' || c == ']' || c == ',' || c == ':';
    }
}
