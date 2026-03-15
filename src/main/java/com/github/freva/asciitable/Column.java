package com.github.freva.asciitable;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

@NullMarked
public class Column {
    private @Nullable String header;
    private @Nullable String footer;
    private HorizontalAlign headerAlign;
    private HorizontalAlign dataAlign;
    private HorizontalAlign footerAlign;
    private int minWidth;
    private int maxWidth;
    private OverflowBehaviour overflowBehaviour;
    private boolean visible;

    public Column() {
        this(null, null, HorizontalAlign.LEFT, HorizontalAlign.RIGHT, HorizontalAlign.LEFT, 0, 80, OverflowBehaviour.NEWLINE, true);
    }

    @Deprecated
    public Column(@Nullable String header, @Nullable String footer, HorizontalAlign headerAlign, HorizontalAlign dataAlign,
                  HorizontalAlign footerAlign, int maxWidth) {
        this(header, footer, headerAlign, dataAlign, footerAlign, 0, maxWidth, OverflowBehaviour.NEWLINE, true);
    }

    Column(@Nullable String header, @Nullable String footer, HorizontalAlign headerAlign, HorizontalAlign dataAlign,
           HorizontalAlign footerAlign, int minWidth, int maxWidth, OverflowBehaviour overflowBehaviour, boolean visible) {
        this.header = header;
        this.footer = footer;
        this.headerAlign = headerAlign;
        this.dataAlign = dataAlign;
        this.footerAlign = footerAlign;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.overflowBehaviour = overflowBehaviour;
        this.visible = visible;
    }

    public @Nullable String getHeader() {
        return header;
    }

    public @Nullable String getFooter() {
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

    public int getMinWidth() {
        return minWidth;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    /** Use {@link Column#getMaxWidth()} instead */
    @Deprecated
    public int getMaxColumnWidth() {
        return maxWidth;
    }

    public OverflowBehaviour getOverflowBehaviour() {
        return overflowBehaviour;
    }

    public boolean isVisible() {
        return visible;
    }


    public Column header(@Nullable String header) {
        this.header = header;
        return this;
    }

    public Column footer(@Nullable String footer) {
        this.footer = footer;
        return this;
    }

    /** Sets horizontal alignment of the header cell for this column */
    public Column headerAlign(HorizontalAlign headerAlign) {
        this.headerAlign = headerAlign;
        return this;
    }

    /** Sets horizontal alignment of all the data cells for this column */
    public Column dataAlign(HorizontalAlign dataAlign) {
        this.dataAlign = dataAlign;
        return this;
    }

    /** Sets horizontal alignment of the footer cells for this column */
    public Column footerAlign(HorizontalAlign footerAlign) {
        this.footerAlign = footerAlign;
        return this;
    }

    /** Min width of this column */
    public Column minWidth(int minWidth) {
        this.minWidth = minWidth;
        return this;
    }

    /** Max width of this column, if data exceeds this length, it will be broken into multiple lines */
    public Column maxWidth(int maxWidth) {
        return maxWidth(maxWidth, OverflowBehaviour.NEWLINE);
    }

    /** Max width of this column, if data exceeds this length, it will be broken into multiple lines */
    public Column maxWidth(int maxWidth, OverflowBehaviour overflowBehaviour) {
        this.maxWidth = maxWidth;
        this.overflowBehaviour = overflowBehaviour;
        return this;
    }

    /** Use {@link Column#maxWidth(int)} instead */
    @Deprecated
    public Column maxColumnWidth(int maxWidth) { return maxWidth(maxWidth); }

    /** Sets whether this column should be rendered. Default is true */
    public Column visible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public <T extends @Nullable Object> ColumnData<T> with(Function<T, @Nullable String> getter) {
        return new ColumnData<>(this, getter);
    }
}