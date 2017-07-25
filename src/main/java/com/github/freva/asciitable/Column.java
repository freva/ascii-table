package com.github.freva.asciitable;

import java.util.function.Function;

public class Column {
    private String header;
    private String footer;
    private HorizontalAlign headerAlign = HorizontalAlign.LEFT;
    private HorizontalAlign dataAlign = HorizontalAlign.RIGHT;
    private HorizontalAlign footerAlign = HorizontalAlign.LEFT;
    private int maxColumnWidth = 80;

    public Column() { }

    public Column(String header, String footer, HorizontalAlign headerAlign, HorizontalAlign dataAlign,
                  HorizontalAlign footerAlign, int maxColumnWidth) {
        this.header = header;
        this.footer = footer;
        this.headerAlign = headerAlign;
        this.dataAlign = dataAlign;
        this.footerAlign = footerAlign;
        this.maxColumnWidth = maxColumnWidth;
    }

    public String getHeader() {
        return header;
    }

    public String getFooter() {
        return footer;
    }

    public HorizontalAlign getHeaderAlign() {
        return headerAlign;
    }

    public HorizontalAlign getDataAlign() {
        return dataAlign;
    }

    public HorizontalAlign getFooterAlign() {
        return footerAlign;
    }

    public int getMaxColumnWidth() {
        return maxColumnWidth;
    }

    public int getHeaderWidth() {
        return header != null ? header.length() : 0;
    }

    public int getFooterWidth() {
        return footer != null ? footer.length() : 0;
    }


    public Column header(String header) {
        this.header = header;
        return this;
    }

    public Column footer(String footer) {
        this.footer = footer;
        return this;
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
     * Sets horizontal alignment of the footer cells for this column
     */
    public Column footerAlign(HorizontalAlign footerAlign) {
        this.footerAlign = footerAlign;
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