package com.github.freva.asciitable;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@NullMarked
public class AsciiTable {
    private static final int PADDING = 1;
    private static final char ELLIPSIS = '…';

    public static final @Nullable Character[] NO_BORDERS = new Character[29];

    public static final Character[] BASIC_ASCII = {'+', '-', '+', '+', '|', '|', '|', '+', '-',
            '+', '+', '|', '|', '|', '+', '-', '+', '+', '+', '-', '+', '+', '|', '|', '|', '+', '-', '+', '+'};

    public static final @Nullable Character[] BASIC_ASCII_NO_DATA_SEPARATORS = {'+', '-', '+', '+', '|', '|', '|', '+', '-',
            '+', '+', '|', '|', '|', null, null, null, null, '+', '-', '+', '+', '|', '|', '|', '+', '-', '+', '+'};

    public static final @Nullable Character[] BASIC_ASCII_NO_DATA_SEPARATORS_NO_OUTSIDE_BORDER = {null, null, null, null, null,
            '|', null, null, '-', '+', null, null, '|', null, null, null, null, null, null, '-', '+', null, null, '|', null, null, null, null, null};

    public static final @Nullable Character[] BASIC_ASCII_NO_OUTSIDE_BORDER = {null, null, null, null, null, '|', null,
            null, '-', '+', null, null, '|', null, null, '-', '+', null, null, '-', '+', null, null, '|', null, null, null, null, null};

    public static final Character[] FANCY_ASCII = {'╔', '═', '╤', '╗', '║', '│', '║',  '╠', '═',
            '╪', '╣', '║', '│', '║', '╟', '─', '┼', '╢', '╠', '═', '╪', '╣', '║', '│', '║', '╚', '═', '╧', '╝'};


    static void writeTable(OutputStreamWriter osw, String lineSeparator, @Nullable Character[] border, Column[] rawColumns, @Nullable Object[][] data, @Nullable Styler styler, @Nullable Integer maxTableWidth) throws IOException {
        if (border.length != NO_BORDERS.length)
            throw new IllegalArgumentException("Border characters array must be exactly " + NO_BORDERS.length + " elements long");

        String[][] stringData = objectArrayToString(rawColumns, data);
        int numColumns = getNumColumns(rawColumns, data);
        Column[] columns = IntStream.range(0, numColumns)
                .mapToObj(index -> index < rawColumns.length ? rawColumns[index] : new Column())
                .filter(Column::isVisible)
                .toArray(Column[]::new);

        writeTable(osw, lineSeparator, border, columns, stringData, styler, maxTableWidth);
    }

    private static void writeTable(OutputStreamWriter osw, String lineSeparator, @Nullable Character[] border, Column[] columns, @Nullable String[][] data, @Nullable Styler styler, @Nullable Integer maxTableWidth) throws IOException {
        int[] colWidths = getColWidths(columns, data, border, maxTableWidth);
        OverflowBehaviour[] overflows = Arrays.stream(columns).map(Column::getOverflowBehaviour).toArray(OverflowBehaviour[]::new);
        boolean insertNewline = writeLine(osw, colWidths, border[0], border[1], border[2], border[3]);

        if (Arrays.stream(columns).map(Column::getHeader).anyMatch(Objects::nonNull)) {
            HorizontalAlign[] aligns = Arrays.stream(columns).map(Column::getHeaderAlign).toArray(HorizontalAlign[]::new);
            @Nullable String[] header = Arrays.stream(columns).map(Column::getHeader).toArray(String[]::new);
            if (insertNewline) osw.write(lineSeparator);
            writeData(osw, colWidths, overflows, aligns, header, border[4], border[5], border[6], lineSeparator,
                    styler == null ? null : (col, rows) -> styler.styleHeader(columns[col], col, rows));
            osw.write(lineSeparator);
            insertNewline = writeLine(osw, colWidths, border[7], border[8], border[9], border[10]);
        }

        HorizontalAlign[] dataAligns = Arrays.stream(columns).map(Column::getDataAlign).toArray(HorizontalAlign[]::new);
        for (int i = 0; i < data.length; i++) {
            if (insertNewline) osw.write(lineSeparator);
            int row = i;
            writeData(osw, colWidths, overflows, dataAligns, data[i], border[11], border[12], border[13], lineSeparator,
                    styler == null ? null : (col, rows) -> styler.styleCell(columns[col], row, col, rows));
            if (i < data.length - 1) {
                osw.write(lineSeparator);
                insertNewline = writeLine(osw, colWidths, border[14], border[15], border[16], border[17]);
            }
        }

        if (Arrays.stream(columns).map(Column::getFooter).anyMatch(Objects::nonNull)) {
            osw.write(lineSeparator);
            HorizontalAlign[] aligns = Arrays.stream(columns).map(Column::getFooterAlign).toArray(HorizontalAlign[]::new);
            @Nullable String[] footer = Arrays.stream(columns).map(Column::getFooter).toArray(String[]::new);
            insertNewline = writeLine(osw, colWidths, border[18], border[19], border[20], border[21]);
            if (insertNewline) osw.write(lineSeparator);
            writeData(osw, colWidths, overflows, aligns, footer, border[22], border[23], border[24], lineSeparator,
                    styler == null ? null : (col, rows) -> styler.styleFooter(columns[col], col, rows));
        }

        if (border[26] != null) osw.write(lineSeparator);
        writeLine(osw, colWidths, border[25], border[26], border[27], border[28]);
    }

    /** Returns a line/border row in the resulting table */
    private static boolean writeLine(OutputStreamWriter osw, int[] colWidths, @Nullable Character left, @Nullable Character middle, @Nullable Character columnSeparator, @Nullable Character right) throws IOException {
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
                                   @Nullable String[] contents, @Nullable Character left, @Nullable Character columnSeparator, @Nullable Character right, String lineSeparator,
                                  @Nullable BiFunction<Integer, List<String>, List<String>> styler) throws IOException {
        List<List<String>> linesContents = IntStream.range(0, colWidths.length)
                .mapToObj(i -> {
                    String text = i < contents.length ? contents[i]: "";
                           text = text != null ? text: "";
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
                                    case ELLIPSIS_CENTER:
                                        int prefixLen = (limit - 1) / 2;
                                        int suffixLen = limit - 1 - prefixLen;
                                        return Stream.of(paragraph.substring(0, prefixLen) + ELLIPSIS + paragraph.substring(paragraph.length() - suffixLen));
                                    default:
                                    case NEWLINE: return LineUtils.splitTextIntoLinesOfMaxLength(paragraph, limit).stream();
                                }
                            })
                            .collect(Collectors.toList());
                })
                .collect(Collectors.toList());
        int numLines = linesContents.stream()
                .mapToInt(List::size)
                .max().orElse(0);

        List<List<String>> justifiedLinesContents = styler == null ? null : IntStream.range(0, colWidths.length)
                .mapToObj(col -> styler.apply(col, IntStream.range(0, numLines)
                        .mapToObj(i -> justify(i < linesContents.get(col).size() ? linesContents.get(col).get(i) : "", horizontalAligns[col], colWidths[col], PADDING))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());

        for (int line = 0; line < numLines; line++) {
            if (left != null) osw.append(left);
            for (int col = 0; col < colWidths.length; col++) {
                if (justifiedLinesContents == null) {
                    String item = linesContents.get(col).size() <= line ? "" : linesContents.get(col).get(line);
                    writeJustified(osw, item, horizontalAligns[col], colWidths[col], PADDING);
                } else osw.write(justifiedLinesContents.get(col).get(line));
                if (columnSeparator != null && col != colWidths.length - 1) osw.write(columnSeparator);
            }
            if (right != null) osw.append(right);
            if (line < numLines - 1) osw.write(lineSeparator);
        }
    }

    /** Returns the width of each column in the resulting table */
    static int[] getColWidths(Column[] columns, @Nullable String[][] data, @Nullable Character[] border, @Nullable Integer maxTableWidth) {
        int[] result = new int[columns.length];
        String current;

        for (@Nullable String[] dataRow : data) {
            for (int col = 0; col < dataRow.length; col++) {
                current = dataRow[col];
                if (current == null || current.length() <= result[col]) continue;
                result[col] = Math.max(result[col], LineUtils.maxLineLength(current));
            }
        }

        for (int col = 0; col < columns.length; col++) {
            int length = result[col];
            current = columns[col].getHeader();
            if (current != null && current.length() > length)
                length = Math.max(length, LineUtils.maxLineLength(current));

            current = columns[col].getFooter();
            if (current != null && current.length() > length)
                length = Math.max(length, LineUtils.maxLineLength(current));
            result[col] = Math.max(Math.min(columns[col].getMaxWidth(), length + 2 * PADDING), columns[col].getMinWidth());
        }

        if (maxTableWidth == null) return result;

        int[] minWidths = new int[columns.length];
        int totalMinWidth = 0;
        int totalCurrentWidth = 0;
        for (int i = 0; i < result.length; i++) {
            minWidths[i] = Math.max(columns[i].getMinWidth(), 2 * PADDING + Math.min(result[i], 1));
            totalMinWidth += minWidths[i];
            totalCurrentWidth += result[i];
        }

        int borderWidth = (border[4] != null ? 1 : 0) + (border[6] != null ? 1 : 0) + (border[5] != null ? columns.length - 1 : 0);
        if (totalCurrentWidth + borderWidth <= maxTableWidth)
            return result;

        int totalSlack = totalCurrentWidth - totalMinWidth;
        if (totalSlack < totalCurrentWidth + borderWidth - maxTableWidth)
            throw new IllegalArgumentException("Max table width " + maxTableWidth +
                    " is too small to fit the minimum column widths totaling " + (totalMinWidth + borderWidth) + ".");

        int slackDeficit = maxTableWidth - borderWidth - totalMinWidth;
        double ratio = slackDeficit / (double) totalSlack;
        double[] remainders = new double[result.length];
        for (int i = 0; i < result.length; i++) {
            double exactShare = (result[i] - minWidths[i]) * ratio;
            int flooredShare = (int) exactShare;
            remainders[i] = exactShare - flooredShare;
            result[i] = minWidths[i] + flooredShare;
            slackDeficit -= flooredShare;
        }

        for (int i = 0; i < slackDeficit; i++) {
            int maxAt = 0;
            for (int j = 1; j < remainders.length; j++)
                maxAt = remainders[j] > remainders[maxAt] ? j : maxAt;
            remainders[maxAt] = -1;
            result[maxAt]++;
        }

        return result;
    }

    /** Returns maximum number of columns between the header or any of the data rows */
    private static int getNumColumns(@Nullable Column[] columns, @Nullable Object[][] data) {
        return Arrays.stream(data)
                .mapToInt(cols -> cols.length)
                .reduce(columns.length, Math::max);
    }

    static String justify(String str, HorizontalAlign align, int length, int minPadding) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(length);
        try (OutputStreamWriter osw = new OutputStreamWriter(baos)) {
            writeJustified(osw, str, align, length, minPadding);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return baos.toString();
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

    private static @Nullable String[][] objectArrayToString(Column[] columns, @Nullable Object [][] array) {
        int[] numInvisible = new int[Math.max(1, columns.length)];
        for (int i = 0; i < columns.length; i++)
            numInvisible[i] = (i == 0 ? 0 : numInvisible[i - 1]) + (columns[i].isVisible() ? 0 : 1);

        if (numInvisible[numInvisible.length - 1] == 0 && array instanceof String[][])
            return (String[][]) array;

        @Nullable String[][] stringArray = new String[array.length][];
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

    public static <T extends @Nullable Object> String getTable(Collection<T> objects, List<ColumnData<T>> columns) {
        return builder().data(objects, columns).asString();
    }

    public static <T extends @Nullable Object> String getTable(@Nullable Character[] border, Collection<T> objects, List<ColumnData<T>> columns) {
        return builder().data(objects, columns).border(border).asString();
    }

    public static String getTable(@Nullable Object[][] data) {
        return builder().data(data).asString();
    }

    public static String getTable(@Nullable String @Nullable[] header, @Nullable Object[][] data) {
        return builder().header(header).data(data).asString();
    }

    public static String getTable(@Nullable String @Nullable[] header, @Nullable String @Nullable[] footer, @Nullable Object[][] data) {
        return builder().header(header).footer(footer).data(data).asString();
    }

    public static String getTable(@Nullable Character[] border, @Nullable String @Nullable[] header, @Nullable String @Nullable[] footer, @Nullable Object[][] data) {
        return builder().header(header).footer(footer).border(border).data(data).asString();
    }

    public static String getTable(Column[] columns, @Nullable Object[][] data) {
        return builder().data(columns, data).asString();
    }

    public static String getTable(@Nullable Character[] border, Column @Nullable[] rawColumns, @Nullable Object[][] data) {
        return builder().data(rawColumns, data).border(border).asString();
    }

    public static AsciiTableBuilder builder() {
        return new AsciiTableBuilder();
    }
}
