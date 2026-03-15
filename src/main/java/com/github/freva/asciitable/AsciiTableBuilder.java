package com.github.freva.asciitable;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@NullMarked
public class AsciiTableBuilder {

    private String lineSeparator = System.lineSeparator();
    private @Nullable Character[] border = AsciiTable.BASIC_ASCII;
    private @Nullable Styler styler;
    private @Nullable String @Nullable[] header;
    private @Nullable String @Nullable[] footer;
    private Column @Nullable[] columns;
    private @Nullable Object @Nullable[][] data;
    private @Nullable Integer maxTableWidth;

    /** Set the line separator to use between table rows. Default is {@link System#lineSeparator()}. */
    public AsciiTableBuilder lineSeparator(String lineSeparator) {
        this.lineSeparator = Objects.requireNonNull(lineSeparator, "line separator cannot be null");
        return this;
    }

    /** Set the table border style. Default is {@link AsciiTable#BASIC_ASCII}. */
    public AsciiTableBuilder border(@Nullable Character[] border) {
        this.border = Objects.requireNonNull(border, "border cannot be null");
        return this;
    }

    /** Set the table styler, default is noop */
    public AsciiTableBuilder styler(Styler styler) {
        this.styler = Objects.requireNonNull(styler, "styler cannot be null");
        return this;
    }

    /**
     * Set the table header cells, cannot be combined with setting column in
     * {@link AsciiTableBuilder#data(Collection, List)} or {@link AsciiTableBuilder#data(Column[], Object[][])} */
    public AsciiTableBuilder header(@Nullable String @Nullable... header) {
        this.header = header;
        return this;
    }

    /**
     * Set the table footer cells, cannot be combined with setting column in
     * {@link AsciiTableBuilder#data(Collection, List)} or {@link AsciiTableBuilder#data(Column[], Object[][])} */
    public AsciiTableBuilder footer(@Nullable String @Nullable... footer) {
        this.footer = footer;
        return this;
    }

    public AsciiTableBuilder data(@Nullable Object[][] data) {
        this.data = data;
        return this;
    }

    public AsciiTableBuilder data(Column @Nullable[] columns, @Nullable Object[][] data) {
        this.columns = columns;
        this.data = data;
        return this;
    }

    public <T extends @Nullable Object> AsciiTableBuilder data(Collection<T> objects, List<ColumnData<T>> columns) {
        Column[] rawColumns = columns.toArray(new Column[0]);

        @Nullable String[][] data = new String[objects.size()][];
        int i = 0;
        for (T object: objects) {
            @Nullable String[] current = new String[columns.size()];
            for (int j = 0; j < columns.size(); j++)
                current[j] = columns.get(j).getCellValue(object);
            data[i++] = current;
        }

        return data(rawColumns, data);
    }


    /** Set the maximum width for the entire table (including borders). */
    public AsciiTableBuilder maxTableWidth(int maxTableWidth) {
        this.maxTableWidth = maxTableWidth;
        return this;
    }

    /** Render the table and return it as String */
    public String asString() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeTo(baos);
        return baos.toString();
    }

    /** Write the table to the give output stream. The output stream must be closed by the caller. */
    public void writeTo(OutputStream os) {
        Column[] columns = this.columns;
        if (columns == null) {
            @Nullable String[] nonNullHeader = header != null ? header : new String[0];
            @Nullable String[] nonNullFooter = footer != null ? footer : new String[0];

            columns = IntStream.range(0, Math.max(nonNullHeader.length, nonNullFooter.length))
                    .mapToObj(index -> new Column()
                            .header(index < nonNullHeader.length ? nonNullHeader[index] : null)
                            .footer(index < nonNullFooter.length ? nonNullFooter[index] : null))
                    .toArray(Column[]::new);
        } else if (header != null || footer != null)
            throw new IllegalArgumentException("Cannot set both header/footer and columns");

        if (data == null)
            throw new IllegalArgumentException("Data must be set");

        try {
            OutputStreamWriter osw = new OutputStreamWriter(os);
            AsciiTable.writeTable(osw, lineSeparator, border, columns, data, styler, maxTableWidth);
            osw.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override public String toString() { return asString(); }
}
