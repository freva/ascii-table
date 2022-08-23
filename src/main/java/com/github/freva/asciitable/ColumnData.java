package com.github.freva.asciitable;

import java.util.function.Function;

public class ColumnData<T> extends Column {
    private final Function<T, String> getter;

    ColumnData(Column column, Function<T, String> getter) {
        super(column.getHeader(), column.getFooter(), column.getHeaderAlign(), column.getDataAlign(), column.getFooterAlign(),
                column.getMinWidth(), column.getMaxWidth());
        this.getter = getter;
    }

    public String getCellValue(T object) {
        return getter.apply(object);
    }
}