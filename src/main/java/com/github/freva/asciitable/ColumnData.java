package com.github.freva.asciitable;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

@NullMarked
public class ColumnData<T extends @Nullable Object> extends Column {
    private final Function<T, String> getter;

    ColumnData(Column column, Function<T, String> getter) {
        super(column.getHeader(), column.getFooter(), column.getHeaderAlign(), column.getDataAlign(), column.getFooterAlign(),
                column.getMinWidth(), column.getMaxWidth(), column.getOverflowBehaviour(), column.isVisible());
        this.getter = getter;
    }

    public String getCellValue(T object) {
        return getter.apply(object);
    }
}