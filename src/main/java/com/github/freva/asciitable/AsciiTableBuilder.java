package com.github.freva.asciitable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class AsciiTableBuilder {

    private String lineSeparator = System.lineSeparator();
    private Character[] border = AsciiTable.BASIC_ASCII;
    private Styler styler;
    private String[] header;
    private String[] footer;
    private Column[] columns;
    private Object[][] data;

    /** Set the line separator to use between table rows. Default is {@link System#lineSeparator()}. */
    public AsciiTableBuilder lineSeparator(String lineSeparator) {
        this.lineSeparator = Objects.requireNonNull(lineSeparator, "line separator cannot be null");
        return this;
    }

    /** Set the table border style. Default is {@link AsciiTable#BASIC_ASCII}. */
    public AsciiTableBuilder border(Character[] border) {
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
    public AsciiTableBuilder header(String... header) {
        this.header = header;
        return this;
    }

    /**
     * Set the table footer cells, cannot be combined with setting column in
     * {@link AsciiTableBuilder#data(Collection, List)} or {@link AsciiTableBuilder#data(Column[], Object[][])} */
    public AsciiTableBuilder footer(String... footer) {
        this.footer = footer;
        return this;
    }

    public AsciiTableBuilder data(Object[][] data) {
        this.data = data;
        return this;
    }

    public AsciiTableBuilder data(Column[] columns, Object[][] data) {
        this.columns = columns;
        this.data = data;
        return this;
    }

    public <T> AsciiTableBuilder data(Collection<T> objects, List<ColumnData<T>> columns) {
        Column[] rawColumns = columns.toArray(new Column[0]);
        String[][] data = objects.stream()
                .map(object ->  columns.stream()
                        .map(dataColumn ->  dataColumn.getCellValue(object))
                        .toArray(String[]::new))
                .toArray(String[][]::new);

        return data(rawColumns, data);
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
            String[] nonNullHeader = header != null ? header : new String[0];
            String[] nonNullFooter = footer != null ? footer : new String[0];

            columns = IntStream.range(0, Math.max(nonNullHeader.length, nonNullFooter.length))
                    .mapToObj(index -> new Column()
                            .header(index < nonNullHeader.length ? nonNullHeader[index] : null)
                            .footer(index < nonNullFooter.length ? nonNullFooter[index] : null))
                    .toArray(Column[]::new);
        } else if (header != null || footer != null)
            throw new IllegalArgumentException("Cannot set both header/footer and columns");

        try {
            OutputStreamWriter osw = new OutputStreamWriter(os);
            AsciiTable.writeTable(osw, lineSeparator, border, columns, data, styler);
            osw.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override public String toString() { return asString(); }
}
