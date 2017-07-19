package com.github.freva.asciitable;

import java.util.function.Function;

public class Column {
    private final String header;
    private HorizontalAlign headerAlign;
    private HorizontalAlign dataAlign;
    private int maxColumnWidth;

    public Column(String header) {
        this(header, HorizontalAlign.LEFT, HorizontalAlign.RIGHT, 80);
    }

    public Column(String header, HorizontalAlign headerAlign, HorizontalAlign dataAlign, int maxColumnWidth) {
        this.header = header;
        this.headerAlign = headerAlign;
        this.dataAlign = dataAlign;
        this.maxColumnWidth = maxColumnWidth;
    }

    public String getHeader() {
        return header;
    }

    public HorizontalAlign getHeaderAlign() {
        return headerAlign;
    }

    public HorizontalAlign getDataAlign() {
        return dataAlign;
    }

    public int getMaxColumnWidth() {
        return maxColumnWidth;
    }

    /**
     * Sets horizontal alignment of the header cell for this column
     */
    public Column headerAlign(HorizontalAlign headerAlign) {
        this.headerAlign = headerAlign;
        return this;
    }

    /**
     * Sets horizontal alignment of all the data cells for this column
     */
    public Column dataAlign(HorizontalAlign dataAlign) {
        this.dataAlign = dataAlign;
        return this;
    }

    /**
     * Max width of this column, if data exceeds this length, it will be broken into multiple lines
     */
    public Column maxColumnWidth(int maxColumnWidth) {
        this.maxColumnWidth = maxColumnWidth;
        return this;
    }

    public <T> ColumnData<T> with(Function<T, String> getter) {
        return new ColumnData<T>(this, getter);
    }
}