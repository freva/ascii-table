package com.github.freva.asciitable;

import java.util.function.Function;

public class ColumnData<T> extends Column {
    private final Function<T, String> getter;

    ColumnData(Column column, Function<T, String> getter) {
        super(column);
        this.getter = getter;
    }

    public String getCellValue(T object) {
        return getter.apply(object);
    }
}