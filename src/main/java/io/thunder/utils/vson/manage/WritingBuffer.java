
package io.thunder.utils.vson.manage;

import java.io.IOException;
import java.io.Writer;


public class WritingBuffer extends Writer {

    private final Writer writer;
    private final char[] buffer;
    private int fill=0;

    public WritingBuffer(Writer writer) {
        this(writer, 16);
    }

    public WritingBuffer(Writer writer, int bufferSize) {
        this.writer=writer;
        buffer=new char[bufferSize];
    }

    @Override
    public void write(int c) throws IOException {
        if (fill>buffer.length-1) {
            flush();
        }
        buffer[fill++]=(char)c;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (fill>buffer.length-len) {
            flush();
            if (len>buffer.length) {
                writer.write(cbuf, off, len);
                return;
            }
        }
        System.arraycopy(cbuf, off, buffer, fill, len);
        fill += len;
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        if (fill>buffer.length-len) {
            flush();
            if (len>buffer.length) {
                writer.write(str, off, len);
                return;
            }
        }
        str.getChars(off, off+len, buffer, fill);
        fill += len;
    }


    @Override
    public void flush() throws IOException {
        writer.write(buffer, 0, fill);
        fill=0;
    }

    @Override
    public void close() throws IOException {
    }
}
