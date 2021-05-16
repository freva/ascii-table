package com.github.freva.asciitable;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LineUtils {

    public static Stream<String> lines(String str) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(new LineIterator(str), Spliterator.NONNULL),
                false);
    }

    private static class LineIterator implements Iterator<String> {
        private final String str;
        private int position = 0;

        private LineIterator(String str) {
            this.str = str;
        }

        @Override
        public boolean hasNext() {
            return position < str.length();
        }

        @Override
        public String next() {
            int start = position;
            return str.substring(start, getLineEndPositionAndAdvanceToNextLine());
        }

        public int getLineEndPositionAndAdvanceToNextLine() {
            for (; position < str.length(); position++) {
                char ch = str.charAt(position);
                if (ch == '\n') return position++;
                if (ch == '\r') {
                    if (position + 1 == str.length() || str.charAt(position + 1) != '\n') return position++;
                    position += 2;
                    return position - 2;
                }
            }
            return position;
        }

        public int getPosition() {
            return position;
        }
    }

}
