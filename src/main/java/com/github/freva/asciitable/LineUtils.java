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

    public static int maxLineLength(String str) {
        int max = 0;
        LineIterator lineIterator = new LineIterator(str);
        while (lineIterator.hasNext()) {
            int start = lineIterator.getPosition();
            max = Math.max(max, lineIterator.getLineEndPositionAndAdvanceToNextLine() - start);
        }
        return max;
    }

    private static class LineIterator implements Iterator<String> {
        private final String str;
        private int position = 0;
        private boolean newlineLast = true;

        private LineIterator(String str) {
            this.str = str;
        }

        @Override
        public boolean hasNext() {
            return newlineLast || position < str.length();
        }

        @Override
        public String next() {
            int start = position;
            return str.substring(start, getLineEndPositionAndAdvanceToNextLine());
        }

        public int getLineEndPositionAndAdvanceToNextLine() {
            newlineLast = false;
            for (; position < str.length(); position++) {
                char ch = str.charAt(position);
                if (ch == '\n') {
                    newlineLast = true;
                    return position++;
                }
                if (ch == '\r') {
                    newlineLast = true;
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
