package com.github.freva.asciitable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class AsciiTable {
    /**
     * 0111121111211113
     * 4    5    5    6
     * 788889888898888A
     * B    C    C    D
     * EFFFFGFFFFGFFFFH
     * B    C    C    D
     * IJJJJKJJJJKJJJJL
     */
    private static final Character[] BASIC_ASCII_WITH_OUTSIDE_BORDER = {
            '+', '-', '+', '+',
            '|', '|', '|',
            '+', '-', '+', '+',
            '|', '|', '|',
            '+', '-', '+', '+',
            '+', '-', '+', '+'};

    private static final Character[] BASIC_ASCII_WITHOUT_OUTSIDE_BORDER = {
            null, null, null, null,
            null, '|', null,
            null, '-', '+', null,
            null, '|', null,
            null, '-', '+', null,
            null, null, null, null};

    private static final Character[] FANCY_ASCII_WITH_OUTSIDE_BORDER = {
            '╔', '═', '╤', '╗',
            '║', '│', '║',
            '╠', '═', '╪', '╣',
            '║', '│', '║',
            '╟', '─', '┼', '╢',
            '╚', '═', '╧', '╝'};

    private final LinkedList<String> headerCols = new LinkedList<>();
    private final LinkedList<String> contentCols = new LinkedList<>();
    private final HorizontalOrientation[] headerHorizontalOrientations;
    private final HorizontalOrientation[] contentHorizontalOrientations;
    private final int numColumns;

    private Character[] borderChars = BASIC_ASCII_WITH_OUTSIDE_BORDER;
    private int maxColumnWidth = 80;
    private int minPadding = 1;

    public AsciiTable(int numColumns) {
        this.numColumns = numColumns;
        this.headerHorizontalOrientations = new HorizontalOrientation[numColumns];
        this.contentHorizontalOrientations = new HorizontalOrientation[numColumns];

        Arrays.fill(headerHorizontalOrientations, HorizontalOrientation.LEFT);
        Arrays.fill(contentHorizontalOrientations, HorizontalOrientation.LEFT);
    }

    public void setBorderChars(Character[] borderChars) {
        if (borderChars.length != 21) {
            throw new IllegalArgumentException("Border characters array must be exactly 22 elements long");
        }
        this.borderChars = borderChars;
    }

    public void setMaxColumnWidth(int maxColumnWidth) {
        if (maxColumnWidth < 1) {
            throw new IllegalArgumentException("Maximum column width must be a positive integer");
        }
        this.maxColumnWidth = maxColumnWidth;
    }

    public void setMinPadding(int minPadding) {
        if (minPadding < 0) {
            throw new IllegalArgumentException("Minimum padding cannot be negative");
        }
        this.minPadding = minPadding;
    }

    public void setHeaderHorizontalOrientations(HorizontalOrientation[] headerHorizontalOrientations) {
        System.arraycopy(headerHorizontalOrientations, 0, this.headerHorizontalOrientations, 0, numColumns);
    }

    public void setContentHorizontalOrientations(HorizontalOrientation[] contentHorizontalOrientations) {
        System.arraycopy(contentHorizontalOrientations, 0, this.contentHorizontalOrientations, 0, numColumns);
    }
    public void addHeaderCols(Iterable<Object> headerCols) {
        for (Object headerCol : headerCols) {
            this.headerCols.add(headerCol == null ? "" : headerCol.toString());
        }
    }

    public void addHeaderCols(Object... headerCols) {
        addHeaderCols(Arrays.asList(headerCols));
    }

    public void add(Iterable<Object> contentCols) {
        for (Object contentCol : contentCols) {
            this.contentCols.add(contentCol == null ? "" : contentCol.toString());
        }
    }

    public void add(Object... contentCols) {
        add(Arrays.asList(contentCols));
    }

    public String asString() {
        if (headerCols.size() != numColumns) {
            throw new IllegalArgumentException("Expected " + numColumns + " header columns, got " + headerCols.size());
        }

        if (contentCols.size() % numColumns != 0) {
            throw new IllegalArgumentException("Expected " + numColumns + " columns, but there are " + contentCols.size() +
                    " content cells. Last row is not full (" + (contentCols.size() % numColumns) + "/" + numColumns + ")");
        }

        final int[] colWidths = getColWidths();
        final LinkedList<String> lines = new LinkedList<>();
        lines.add(borderRow(colWidths, borderChars[0], borderChars[1], borderChars[2], borderChars[3]));
        if (! headerCols.isEmpty()) {
            lines.addAll(row(colWidths, headerHorizontalOrientations, headerCols, borderChars[4], borderChars[5], borderChars[6]));
            lines.add(borderRow(colWidths, borderChars[7], borderChars[8], borderChars[9], borderChars[10]));
        }

        String contentRowBorder = borderRow(colWidths, borderChars[14], borderChars[15], borderChars[16], borderChars[17]);
        for (int col = 0; col < contentCols.size(); col += numColumns) {
            lines.addAll(row(colWidths, contentHorizontalOrientations, contentCols.subList(col, col + numColumns),
                    borderChars[11], borderChars[12], borderChars[13]));
            lines.add(contentRowBorder);
        }
        if (! contentCols.isEmpty()) lines.removeLast();
        lines.add(borderRow(colWidths, borderChars[18], borderChars[19], borderChars[20], borderChars[21]));

        return lines.stream()
                .filter(line -> !line.isEmpty())
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String borderRow(int[] colWidths, Character left, Character middle, Character columnSeparator, Character right) {
        StringBuilder row = new StringBuilder(getTableWidth(colWidths));
        if (left != null) row.append((char) left);
        for (int col = 0; col < numColumns; col++) {
            if (middle != null) row.append(repeat(middle, colWidths[col] + 2 * minPadding));
            if (columnSeparator != null && col != numColumns - 1) row.append((char) columnSeparator);
        }
        if (right != null) row.append((char) right);
        return row.toString();
    }

    private List<String> row(int[] colWidths, HorizontalOrientation[] horizontalOrientations, List<String> contents,
                             Character left, Character columnSeparator, Character right) {
        List<List<String>> linesContents = contents.stream()
                .map(string -> splitTextIntoLinesOfMaxLength(string, maxColumnWidth))
                .collect(Collectors.toList());
        int numLines = linesContents.stream()
                .mapToInt(List::size)
                .max().orElse(0);

        StringBuilder row = new StringBuilder(getTableWidth(colWidths));
        List<String> lines = new LinkedList<>();
        for (int line = 0; line < numLines; line++) {
            if (left != null) row.append((char) left);
            for (int col = 0; col < numColumns; col++) {
                String item = linesContents.get(col).size() <= line ? "" : linesContents.get(col).get(line);
                row.append(justify(item, horizontalOrientations[col], colWidths[col], minPadding));
                if (columnSeparator != null && col != numColumns - 1) row.append((char) columnSeparator);
            }
            if (right != null) row.append((char) right);
            lines.add(row.toString());
            row.setLength(0);
        }
        return lines;
    }

    private int[] getColWidths() {
        int[] result = new int[numColumns];
        for (int col = 0; col < headerCols.size(); col++) {
            int length = headerCols.get(col).length();
            result[col] = Math.min(maxColumnWidth, length);
        }

        for (int index = 0; index < contentCols.size(); index++) {
            int col = index % numColumns;
            int colLength = contentCols.get(index).length();
            if (colLength > result[col]) {
                int length = contentCols.get(index).length();
                result[col] = Math.min(maxColumnWidth, length);
            }
        }
        return result;
    }

    private int getTableWidth(int[] colWidths) {
        return Arrays.stream(colWidths).sum() + minPadding * (numColumns + 1) - 1;
    }

    static List<String> splitTextIntoLinesOfMaxLength(String str, int maxCharInLine) {
        List<String> lines = new LinkedList<>();
        String[] tokens = str.split(" ");
        StringBuilder sb = new StringBuilder(maxCharInLine);

        for (String word : tokens) {
            while(word.length() > maxCharInLine){
                sb.append(word.substring(0, maxCharInLine - sb.length()));
                word = word.substring(maxCharInLine - sb.length());
                lines.add(sb.toString());
                sb.setLength(0);
            }

            if (sb.length() + word.length() > maxCharInLine) {
                lines.add(sb.toString());
                sb.setLength(0);
            }
            if (sb.length() > 0) sb.append(" ");
            sb.append(word);
        }
        lines.add(sb.toString());
        return lines;
    }

    static char[] justify(String str, HorizontalOrientation orientation, int length, int minPadding) {
        length += 2 * minPadding;
        if (str.length() < length) {
            char[] justified = new char[length];
            Arrays.fill(justified, ' ');
            switch (orientation) {
                case LEFT:
                    System.arraycopy(str.toCharArray(), 0, justified, minPadding, str.length());
                    break;

                case CENTER:
                    System.arraycopy(str.toCharArray(), 0, justified, (length - str.length()) / 2, str.length());
                    break;

                case RIGHT:
                    System.arraycopy(str.toCharArray(), 0, justified, length - str.length() - minPadding, str.length());
                    break;
            }

            return justified;
        }
        return str.toCharArray();
    }

    private static char[] repeat(char c, int num) {
        char[] repeat = new char[num];
        Arrays.fill(repeat, c);
        return repeat;
    }

    public enum HorizontalOrientation {
        LEFT, CENTER, RIGHT
    }
}