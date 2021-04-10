package io.lightning.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Getter @Setter
public class StringCreator {

    private final List<String> strings;
    private boolean newLine;

    public StringCreator() {
        this(new LinkedList<>());
    }


    public StringCreator(List<String> strings) {
        this.strings = strings;
        this.newLine = true;
    }

    public StringCreator append(String line) {
        strings.add(line);
        return this;
    }

    public void clear() {
        this.strings.clear();
    }

    public String toString() {
        String s = null;

        final Iterator<String> iterator = this.strings.iterator();

        while (iterator.hasNext()) {
            final String next = iterator.next();
            s = (s != null ? s : "") + next;
            if (iterator.hasNext()) {
                if (this.newLine) {
                    s = s + "\n";
                }
            }
        }
        return s;
    }
}
