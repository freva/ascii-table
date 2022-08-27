package com.github.freva.asciitable;

import org.junit.Test;

import java.io.ByteArrayOutputStream;

public class AsciiTableBuilderTest {

    @Test(expected = IllegalArgumentException.class)
    public void doesNotAllowHeaderWithColumns() {
        AsciiTable.builder().header("header").data(new Column[0], new Object[0][0]).asString();
    }
    @Test(expected = IllegalArgumentException.class)
    public void doesNotAllowFooterWithColumns() {
        AsciiTable.builder().footer("footer").data(new Column[0], new Object[0][0]).asString();
    }
}
