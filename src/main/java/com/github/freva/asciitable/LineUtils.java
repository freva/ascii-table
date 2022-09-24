package com.github.freva.asciitable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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

    /**
     * Splits a string into multiple strings each of which length is <= maxCharInLine. The splitting is done by
     * space character if possible, otherwise a word is broken at exactly maxCharInLine.
     * This method preserves all spaces except the one it splits on, for example:
     * string "here is    a  strange string" split on length 8 gives: ["here is ", "  a ", "strange", "string"]
     *
     * @param str String to split
     * @param maxCharInLine Max length of each split
     * @return List of string that form original string, but each string is as-short-or-shorter than maxCharInLine
     */
    static List<String> splitTextIntoLinesOfMaxLength(String str, int maxCharInLine) {
        List<String> lines = new LinkedList<>();
        StringBuilder line = new StringBuilder(maxCharInLine);
        int offset = 0;

        while (offset < str.length() && maxCharInLine < str.length() - offset) {
            int spaceToWrapAt = str.lastIndexOf(' ', offset + maxCharInLine);

            if (offset < spaceToWrapAt) {
                line.append(str, offset, spaceToWrapAt);
                offset = spaceToWrapAt + 1;
            } else {
                line.append(str, offset, offset + maxCharInLine);
                offset += maxCharInLine;
            }

            lines.add(line.toString());
            line.setLength(0);
        }

        line.append(str.substring(offset));
        lines.add(line.toString());

        return lines;
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
