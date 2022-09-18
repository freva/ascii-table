package com.github.freva.asciitable;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AsciiTable {
    private static final int PADDING = 1;
    private static final char ELLIPSIS = '…';

    public static final Character[] NO_BORDERS = new Character[29];

    public static final Character[] BASIC_ASCII = {'+', '-', '+', '+', '|', '|', '|', '+', '-',
            '+', '+', '|', '|', '|', '+', '-', '+', '+', '+', '-', '+', '+', '|', '|', '|', '+', '-', '+', '+'};

    public static final Character[] BASIC_ASCII_NO_DATA_SEPARATORS = {'+', '-', '+', '+', '|', '|', '|', '+', '-',
            '+', '+', '|', '|', '|', null, null, null, null, '+', '-', '+', '+', '|', '|', '|', '+', '-', '+', '+'};

    public static final Character[] BASIC_ASCII_NO_DATA_SEPARATORS_NO_OUTSIDE_BORDER = {null, null, null, null, null,
            '|', null, null, '-', '+', null, null, '|', null, null, null, null, null, null, '-', '+', null, null, '|', null, null, null, null, null};

    public static final Character[] BASIC_ASCII_NO_OUTSIDE_BORDER = {null, null, null, null, null, '|', null,
            null, '-', '+', null, null, '|', null, null, '-', '+', null, null, '-', '+', null, null, '|', null, null, null, null, null};

    public static final Character[] FANCY_ASCII = {'╔', '═', '╤', '╗', '║', '│', '║',  '╠', '═',
            '╪', '╣', '║', '│', '║', '╟', '─', '┼', '╢', '╠', '═', '╪', '╣', '║', '│', '║', '╚', '═', '╧', '╝'};


    static void writeTable(OutputStreamWriter osw, String lineSeparator, Character[] border, Column[] rawColumns, Object[][] data) throws IOException {
        if (border.length != NO_BORDERS.length)
            throw new IllegalArgumentException("Border characters array must be exactly " + NO_BORDERS.length + " elements long");

        String[][] stringData = objectArrayToString(rawColumns, data);
        int numColumns = getNumColumns(rawColumns, data);
        Column[] columns = IntStream.range(0, numColumns)
                .mapToObj(index -> index < rawColumns.length ? rawColumns[index] : new Column())
                .filter(Column::isVisible)
                .toArray(Column[]::new);

        writeTable(osw, lineSeparator, border, columns, stringData);
    }

    private static void writeTable(OutputStreamWriter osw, String lineSeparator, Character[] border, Column[] columns, String[][] data) throws IOException {
        int[] colWidths = getColWidths(columns, data);
        OverflowBehaviour[] overflows = Arrays.stream(columns).map(Column::getOverflowBehaviour).toArray(OverflowBehaviour[]::new);
        boolean insertNewline = writeLine(osw, colWidths, border[0], border[1], border[2], border[3]);

        if (Arrays.stream(columns).map(Column::getHeader).anyMatch(Objects::nonNull)) {
            HorizontalAlign[] aligns = Arrays.stream(columns).map(Column::getHeaderAlign).toArray(HorizontalAlign[]::new);
            String[] header = Arrays.stream(columns).map(Column::getHeader).toArray(String[]::new);
            if (insertNewline) osw.write(lineSeparator);
            writeData(osw, colWidths, overflows, aligns, header, border[4], border[5], border[6], lineSeparator);
            osw.write(lineSeparator);
            insertNewline = writeLine(osw, colWidths, border[7], border[8], border[9], border[10]);
        }

        HorizontalAlign[] dataAligns = Arrays.stream(columns).map(Column::getDataAlign).toArray(HorizontalAlign[]::new);
        for (int i = 0; i < data.length; i++) {
            if (insertNewline) osw.write(lineSeparator);
            writeData(osw, colWidths, overflows, dataAligns, data[i], border[11], border[12], border[13], lineSeparator);
            if (i < data.length - 1) {
                osw.write(lineSeparator);
                insertNewline = writeLine(osw, colWidths, border[14], border[15], border[16], border[17]);
            }
        }

        if (Arrays.stream(columns).map(Column::getFooter).anyMatch(Objects::nonNull)) {
            osw.write(lineSeparator);
            HorizontalAlign[] aligns = Arrays.stream(columns).map(Column::getFooterAlign).toArray(HorizontalAlign[]::new);
            String[] footer = Arrays.stream(columns).map(Column::getFooter).toArray(String[]::new);
            insertNewline = writeLine(osw, colWidths, border[18], border[19], border[20], border[21]);
            if (insertNewline) osw.write(lineSeparator);
            writeData(osw, colWidths, overflows, aligns, footer, border[22], border[23], border[24], lineSeparator);
        }

        if (border[26] != null) osw.write(lineSeparator);
        writeLine(osw, colWidths, border[25], border[26], border[27], border[28]);
    }

    /** Returns a line/border row in the resulting table */
    private static boolean writeLine(OutputStreamWriter osw, int[] colWidths, Character left, Character middle, Character columnSeparator, Character right) throws IOException {
        if (middle == null) return false;
        if (left != null) osw.append(left);
        for (int col = 0; col < colWidths.length; col++) {
            writeRepeated(osw, middle, colWidths[col]);
            if (columnSeparator != null && col != colWidths.length - 1) osw.write(columnSeparator);
        }
        if (right != null) osw.append(right);
        return true;
    }

    /**
     * Returns list of rows in resulting table for a given header/data row. A single header/data row may produce
     * multiple rows in the resulting table if:
     *  - Contents of a row exceed maxCharInLine for that row
     *  - Contents of a row we're already multiline
     */
    @SuppressWarnings("deprecated")
    private static void writeData(OutputStreamWriter osw, int[] colWidths, OverflowBehaviour[] overflows, HorizontalAlign[] horizontalAligns,
                                  String[] contents, Character left, Character columnSeparator, Character right, String lineSeparator) throws IOException {
        List<List<String>> linesContents = IntStream.range(0, colWidths.length)
                .mapToObj(i -> {
                    String text = i < contents.length && contents[i] != null ? contents[i] : "";
                    return LineUtils.lines(text)
                            .flatMap(paragraph -> {
                                int limit = colWidths[i] - 2 * PADDING;
                                if (paragraph.length() <= limit) return Stream.of(paragraph);

                                switch (overflows[i]) {
                                    case CLIP_LEFT: return Stream.of(paragraph.substring(paragraph.length() - limit));
                                    case CLIP:
                                    case CLIP_RIGHT: return Stream.of(paragraph.substring(0, limit));
                                    case ELLIPSIS_LEFT: return Stream.of(ELLIPSIS + paragraph.substring(paragraph.length() - limit + 1));
                                    case ELLIPSIS:
                                    case ELLIPSIS_RIGHT: return Stream.of(paragraph.substring(0, limit - 1) + ELLIPSIS);
                                    default:
                                    case NEWLINE: return splitTextIntoLinesOfMaxLength(paragraph, limit).stream();
                                }
                            })
                            .collect(Collectors.toList());
                })
                .collect(Collectors.toList());
        int numLines = linesContents.stream()
                .mapToInt(List::size)
                .max().orElse(0);

        for (int line = 0; line < numLines; line++) {
            if (left != null) osw.append(left);
            for (int col = 0; col < colWidths.length; col++) {
                String item = linesContents.get(col).size() <= line ? "" : linesContents.get(col).get(line);
                writeJustified(osw, item, horizontalAligns[col], colWidths[col], PADDING);
                if (columnSeparator != null && col != colWidths.length - 1) osw.write(columnSeparator);
            }
            if (right != null) osw.append(right);
            if (line < numLines - 1) osw.write(lineSeparator);
        }
    }

    /** Returns the width of each column in the resulting table */
    private static int[] getColWidths(Column[] columns, String[][] data) {
        int[] result = new int[columns.length];

        for (String[] dataRow : data) {
            for (int col = 0; col < dataRow.length; col++) {
                result[col] = Math.max(result[col], dataRow[col] == null ? 0 : LineUtils.maxLineLength(dataRow[col]));
            }
        }

        for (int col = 0; col < columns.length; col++) {
            int length = Math.max(Math.max(columns[col].getHeaderWidth(), columns[col].getFooterWidth()), result[col]);
            result[col] = Math.max(Math.min(columns[col].getMaxWidth(), length + 2 * PADDING), columns[col].getMinWidth());
        }
        return result;
    }

    /** Returns maximum number of columns between the header or any of the data rows */
    private static int getNumColumns(Column[] columns, Object[][] data) {
        return Arrays.stream(data)
                .mapToInt(cols -> cols.length)
                .reduce(columns.length, Math::max);
    }

    /** Returns the width of each line in resulting table not counting the line break character(s) */
    private static int getTableWidth(int[] colWidths) {
        return Arrays.stream(colWidths).sum() + PADDING * (colWidths.length + 1) - 1;
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

    /**
     * Justify the string to a given horizontal alignment by padding it with spaces. Before justifying the string,
     * a minimum padding is applied to both sides. The new length is the total length, including the min padding. If
     * passed string is already of length or longer, the string is returned unaltered.
     *
     * @param str String to justify
     * @param align Horizontal alignment
     * @param length Total new length
     * @param minPadding Length of padding to apply from both left and right before justifying
     */
    static void writeJustified(OutputStreamWriter osw, String str, HorizontalAlign align, int length, int minPadding) throws IOException {
        if (str.length() < length) {
            int leftPadding = align == HorizontalAlign.LEFT ?   minPadding :
                              align == HorizontalAlign.CENTER ? (length - str.length()) / 2 :
                                                                length - str.length() - minPadding;

            writeRepeated(osw, ' ', leftPadding);
            osw.write(str);
            writeRepeated(osw, ' ', length - str.length() - leftPadding);
        } else osw.write(str);
    }

    private static void writeRepeated(OutputStreamWriter osw, char c, int num) throws IOException {
        for (int i = 0; i < num; i++) osw.append(c);
    }

    private static String[][] objectArrayToString(Column[] columns, Object[][] array) {
        int[] numInvisible = new int[Math.max(1, columns.length)];
        for (int i = 0; i < columns.length; i++)
            numInvisible[i] = (i == 0 ? 0 : numInvisible[i - 1]) + (columns[i].isVisible() ? 0 : 1);

        if (numInvisible[numInvisible.length - 1] == 0 && array instanceof String[][])
            return (String[][]) array;

        String[][] stringArray = new String[array.length][];
        for (int i = 0; i < stringArray.length; i++) {
            stringArray[i] = new String[array[i].length - numInvisible[Math.min(numInvisible.length, array[i].length) - 1]];
            for (int j = 0, k = 0; k < stringArray[i].length; j++) {
                if (j < columns.length && !columns[j].isVisible()) continue;
                stringArray[i][k++] = array[i][j] == null ? null : array[i][j].toString();
            }
        }
        return stringArray;
    }

    // ===== Public API =====

    public static <T> String getTable(Collection<T> objects, List<ColumnData<T>> columns) {
        return builder().data(objects, columns).asString();
    }

    public static <T> String getTable(Character[] border, Collection<T> objects, List<ColumnData<T>> columns) {
        return builder().data(objects, columns).border(border).asString();
    }

    public static String getTable(Object[][] data) {
        return builder().data(data).asString();
    }

    public static String getTable(String[] header, Object[][] data) {
        return builder().header(header).data(data).asString();
    }

    public static String getTable(String[] header, String[] footer, Object[][] data) {
        return builder().header(header).footer(footer).data(data).asString();
    }

    public static String getTable(Character[] border, String[] header, String[] footer, Object[][] data) {
        return builder().header(header).footer(footer).border(border).data(data).asString();
    }

    public static String getTable(Column[] columns, Object[][] data) {
        return builder().data(columns, data).asString();
    }

    public static String getTable(Character[] border, Column[] rawColumns, Object[][] data) {
        return builder().data(rawColumns, data).border(border).asString();
    }

    public static AsciiTableBuilder builder() {
        return new AsciiTableBuilder();
    }
}
