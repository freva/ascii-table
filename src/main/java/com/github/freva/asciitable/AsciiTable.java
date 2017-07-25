package com.github.freva.asciitable;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AsciiTable {
    private static final int MIN_PADDING = 1;

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


    public static <T> String getTable(Collection<T> objects, List<ColumnData<T>> columns) {
        return getTable(BASIC_ASCII, objects, columns);
    }

    public static <T> String getTable(Character[] borderChars, Collection<T> objects, List<ColumnData<T>> columns) {
        Column[] rawColumns = columns.toArray(new Column[columns.size()]);
        String[][] data = objects.stream()
                .map(object ->  columns.stream()
                        .map(dataColumn ->  dataColumn.getCellValue(object))
                        .toArray(String[]::new))
                .toArray(String[][]::new);

        return getTable(borderChars, rawColumns, data);
    }

    public static String getTable(String[][] data) {
        return getTable((String[]) null, data);
    }

    public static String getTable(String[] header, String[][] data) {
        return getTable(BASIC_ASCII, header, null, data);
    }

    public static String getTable(String[] header, String[] footer, String[][] data) {
        return getTable(BASIC_ASCII, header, footer, data);
    }

    public static String getTable(Character[] borderChars, String[] header, String[] footer, String[][] data) {
        String[] nonNullHeader = header != null ? header : new String[0];
        String[] nonNullFooter = footer != null ? footer : new String[0];

        Column[] headerCol = IntStream.range(0, Math.max(nonNullHeader.length, nonNullFooter.length))
                .mapToObj(index -> new Column()
                        .header(index < nonNullHeader.length ? nonNullHeader[index] : null)
                        .footer(index < nonNullFooter.length ? nonNullFooter[index] : null))
                .toArray(Column[]::new);

        return getTable(borderChars, headerCol, data);
    }

    public static String getTable(Column[] columns, String[][] data) {
        return getTable(BASIC_ASCII, columns, data);
    }

    public static String getTable(Character[] borderChars, Column[] rawColumns, String[][] data) {
        if (borderChars.length != NO_BORDERS.length) {
            throw new IllegalArgumentException("Border characters array must be exactly " + NO_BORDERS.length + " elements long");
        }

        final int numColumns = getNumColumns(rawColumns, data);
        final Column[] columns = IntStream.range(0, numColumns)
                .mapToObj(index -> index < rawColumns.length ? rawColumns[index] : new Column())
                .toArray(Column[]::new);
        final int[] colWidths = getColWidths(columns, data);

        final HorizontalAlign[] headerAligns = Arrays.stream(columns).map(Column::getHeaderAlign).toArray(HorizontalAlign[]::new);
        final HorizontalAlign[] dataAligns = Arrays.stream(columns).map(Column::getDataAlign).toArray(HorizontalAlign[]::new);
        final HorizontalAlign[] footerAligns = Arrays.stream(columns).map(Column::getFooterAlign).toArray(HorizontalAlign[]::new);

        final String[] header = Arrays.stream(columns).map(Column::getHeader).toArray(String[]::new);
        final String[] footer = Arrays.stream(columns).map(Column::getFooter).toArray(String[]::new);

        List<String> tableRows = getTableRows(colWidths, headerAligns, dataAligns, footerAligns, borderChars, header, data, footer);

        return tableRows.stream()
                .filter(line -> !line.isEmpty())
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private static List<String> getTableRows(int[] colWidths, HorizontalAlign[] headerAligns,
                                             HorizontalAlign[] dataAligns, HorizontalAlign[] footerAligns,
                                             Character[] borderChars, String[] header, String[][] data, String[] footer) {
        final LinkedList<String> lines = new LinkedList<>();
        lines.add(lineRow(colWidths, borderChars[0], borderChars[1], borderChars[2], borderChars[3]));

        if (! Arrays.stream(header).allMatch(Objects::isNull)) {
            lines.addAll(dataRow(colWidths, headerAligns, header, borderChars[4], borderChars[5], borderChars[6]));
            lines.add(lineRow(colWidths, borderChars[7], borderChars[8], borderChars[9], borderChars[10]));
        }

        String contentRowBorder = lineRow(colWidths, borderChars[14], borderChars[15], borderChars[16], borderChars[17]);
        for (String[] dataRow : data) {
            lines.addAll(dataRow(colWidths, dataAligns, dataRow, borderChars[11], borderChars[12], borderChars[13]));
            lines.add(contentRowBorder);
        }
        if (data.length > 0) lines.removeLast();
        if (! Arrays.stream(footer).allMatch(Objects::isNull)) {
            lines.add(lineRow(colWidths, borderChars[18], borderChars[19], borderChars[20], borderChars[21]));
            lines.addAll(dataRow(colWidths, footerAligns, footer, borderChars[22], borderChars[23], borderChars[24]));
        }

        lines.add(lineRow(colWidths, borderChars[25], borderChars[26], borderChars[27], borderChars[28]));

        return lines;
    }

    /**
     * Returns a line/border row in the resulting table
     */
    private static String lineRow(int[] colWidths, Character left, Character middle, Character columnSeparator, Character right) {
        final StringBuilder row = new StringBuilder(getTableWidth(colWidths));
        if (left != null) row.append((char) left);
        for (int col = 0; col < colWidths.length; col++) {
            if (middle != null) row.append(repeat(middle, colWidths[col]));
            if (columnSeparator != null && col != colWidths.length - 1) row.append((char) columnSeparator);
        }
        if (right != null) row.append((char) right);
        return row.toString();
    }

    /**
     * Returns list of rows in resulting table for a given header/data row. A single header/data row may produce
     * multiple rows in the resulting table if:
     *  - Contents of a row exceed maxCharInLine for that row
     *  - Contents of a row we're already multiline
     */
    private static List<String> dataRow(int[] colWidths, HorizontalAlign[] horizontalAligns, String[] contents,
                                 Character left, Character columnSeparator, Character right) {
        final List<List<String>> linesContents = IntStream.range(0, colWidths.length)
                .mapToObj(i -> {
                    String text = i < contents.length && contents[i] != null ? contents[i] : "";
                    String[] paragraphs = text.split(System.lineSeparator());
                    return Arrays.stream(paragraphs)
                            .flatMap(paragraph -> splitTextIntoLinesOfMaxLength(paragraph, colWidths[i] - 2* MIN_PADDING).stream())
                            .collect(Collectors.toList());
                })
                .collect(Collectors.toList());
        final int numLines = linesContents.stream()
                .mapToInt(List::size)
                .max().orElse(0);

        final StringBuilder row = new StringBuilder(getTableWidth(colWidths));
        final List<String> lines = new LinkedList<>();
        for (int line = 0; line < numLines; line++) {
            if (left != null) row.append((char) left);
            for (int col = 0; col < colWidths.length; col++) {
                String item = linesContents.get(col).size() <= line ? "" : linesContents.get(col).get(line);
                row.append(justify(item, horizontalAligns[col], colWidths[col], MIN_PADDING));
                if (columnSeparator != null && col != colWidths.length - 1) row.append((char) columnSeparator);
            }
            if (right != null) row.append((char) right);
            lines.add(row.toString());
            row.setLength(0);
        }
        return lines;
    }

    /**
     * Returns the width of each column in the resulting table.
     */
    private static int[] getColWidths(Column[] columns, String[][] data) {
        final int[] result = new int[columns.length];

        for (String[] dataRow : data) {
            for (int col = 0; col < dataRow.length; col++) {
                result[col] = Math.max(result[col], dataRow[col] == null ? 0 : dataRow[col].length());
            }
        }

        for (int col = 0; col < columns.length; col++) {
            int length = Math.max(Math.max(columns[col].getHeaderWidth(), columns[col].getFooterWidth()), result[col]);
            result[col] = Math.min(columns[col].getMaxColumnWidth(), length + 2 * MIN_PADDING);
        }
        return result;
    }

    /**
     * Returns maximum number of columns between the header or any of the data rows.
     */
    private static int getNumColumns(Column[] columns, String[][] data) {
        final int maxDataColumns = Arrays.stream(data)
                .mapToInt(cols -> cols.length)
                .max().orElse(0);

        return Math.max(columns.length, maxDataColumns);
    }

    /**
     * Returns the width of each line in resulting table not counting the line break character(s).
     */
    private static int getTableWidth(int[] colWidths) {
        return Arrays.stream(colWidths).sum() + MIN_PADDING * (colWidths.length + 1) - 1;
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
        final List<String> lines = new LinkedList<>();
        final StringBuilder line = new StringBuilder(maxCharInLine);
        int offset = 0;

        while (offset < str.length() && maxCharInLine < str.length() - offset) {
            final int spaceToWrapAt = str.lastIndexOf(' ', offset + maxCharInLine);

            if (offset < spaceToWrapAt) {
                line.append(str.substring(offset, spaceToWrapAt));
                offset = spaceToWrapAt + 1;
            } else {
                line.append(str.substring(offset, offset + maxCharInLine));
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
     * @return Justified string's char array
     */
    static char[] justify(String str, HorizontalAlign align, int length, int minPadding) {
        if (str.length() < length) {
            final char[] justified = new char[length];
            Arrays.fill(justified, ' ');
            switch (align) {
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
}