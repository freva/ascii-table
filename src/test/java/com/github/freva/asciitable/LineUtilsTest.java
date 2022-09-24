package com.github.freva.asciitable;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class LineUtilsTest {

    @Test
    public void ensureTrailingEmptyLineIsReturned() {
        assertEquals(Arrays.asList(""),
                LineUtils.lines("").collect(Collectors.toList()));

        assertEquals(Arrays.asList("", ""),
                LineUtils.lines("\n").collect(Collectors.toList()));
    }

    @Test
    public void linesIteratorTest() {
        assertEquals(Arrays.asList("", "", "Some text", "", "more text", "text", "end"),
                LineUtils.lines("\n\nSome text\r\n\rmore text\rtext\nend").collect(Collectors.toList()));
    }

    @Test
    public void textSplitting() {
        String str = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam pretium eu dolor sodales rutrum. " +
                "Also here is a very long link: http://www.example.tld/some/resource/file.ext a few final words";
        List<String> expected = Arrays.asList(
                "Lorem ipsum", "dolor sit", "amet,", "consectetur", "adipiscing", "elit. Nam", "pretium eu", "dolor",
                "sodales", "rutrum. Also", "here is a", "very long", "link:", "http://www.e", "xample.tld/s",
                "ome/resource", "/file.ext a", "few final", "words");

        assertEquals(expected, LineUtils.splitTextIntoLinesOfMaxLength(str, 12));
    }
}
