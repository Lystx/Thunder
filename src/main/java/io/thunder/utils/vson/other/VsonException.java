
package io.thunder.utils.vson.other;



public class VsonException extends RuntimeException {

    private final int offset;
    private final int line;
    private final int column;

    public VsonException(String message, int offset, int line, int column) {
        super(message + " at " + line + ":" + column);
        this.offset = offset;
        this.line = line;
        this.column = column;
    }

    public int getOffset() {
        return offset;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
