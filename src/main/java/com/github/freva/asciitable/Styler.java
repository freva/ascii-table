package com.github.freva.asciitable;

import java.util.List;

/**
 * Allows styling the table by adding zero-width characters (e.g. ANSI escape codes)
 * to the content. This interface will be invoked just before the data is written,
 * the inputs are therefore already split into the lines and the text is justified.
 *
 * WARNING: If any of the methods add any non-zero-width characters or remove any characters,
 * the table will be misaligned.
 */
public interface Styler {

    /**
     * Style the cell value, e.g. by adding ANSI escape codes.
     *
     * @param column Column of the cell
     * @param row row number of the cell (0-indexed)
     * @param col column number of the cell (0-indexed)
     * @param data List of strings (lines) in this cell. Guaranteed to be at least 1 element. Unless the original
     *             cell data was longer than the max column width AND text overflow behavior is NEWLINE, this will
     *             contain exactly 1 element.
     * @return List of strings (lines) in this cell.
     */
    default List<String> styleCell(Column column, int row, int col, List<String> data) {
        return data;
    }

    /**
     * Style the header value, e.g. by adding ANSI escape codes.
     *
     * @param column Column of the header
     * @param col column number of the cell (0-indexed)
     * @param data {@link Column#getHeader()}, but split into lines to satisfy {@link Column#getMaxWidth()} and
     * {@link Column#getOverflowBehaviour()}, and justified per {@link Column#headerAlign(HorizontalAlign)}
     * @return List of strings (lines) in this header cell.
     */
    default List<String> styleHeader(Column column, int col, List<String> data) {
        return data;
    }

    /**
     * Style the footer value, e.g. by adding ANSI escape codes.
     *
     * @param column Column of the footer
     * @param col column number of the cell (0-indexed)
     * @param data {@link Column#getFooter()} ()}, but split into lines to satisfy {@link Column#getMaxWidth()} and
     * {@link Column#getOverflowBehaviour()}, and justified per {@link Column#footerAlign(HorizontalAlign)}
     * @return List of strings (lines) in this footer cell.
     */
    default List<String> styleFooter(Column column, int col, List<String> data) {
        return data;
    }

}
