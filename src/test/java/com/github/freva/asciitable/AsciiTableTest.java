package com.github.freva.asciitable;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.github.freva.asciitable.HorizontalAlign.*;
import static org.junit.Assert.assertEquals;

public class AsciiTableTest {
    private static final List<Planet> planets = Arrays.asList(
            new Planet(1, "Mercury", 0.382, 0.06, "minimal"),
            new Planet(2, "Venus", 0.949, 0.82, "Carbon dioxide, Nitrogen"),
            new Planet(3, "Earth", 1.0, 1.0, "Nitrogen, Oxygen, Argon"),
            new Planet(4, "Mars", 0.532, 0.11, "Carbon dioxide, Nitrogen, Argon"));

    @Test
    public void testTableDefault() {
        String[] headers = {"", "Name", "Diameter", "Mass", "Atmosphere"};
        String[][] data = {{"1", "Mercury", "0.382", "0.06", "minimal"},
                {"2", "Venus", "0.949", "0.82", "Carbon dioxide, Nitrogen"},
                {"3", "Earth", "1.000", "1.00", "Nitrogen, Oxygen, Argon"},
                {"4", "Mars", "0.532", "0.11", "Carbon dioxide, Nitrogen, Argon"}};

        String actualArray = AsciiTable.getTable(headers, data);
        String actualObjects = AsciiTable.getTable(planets, Arrays.asList(
                new Column("").with(planet -> Integer.toString(planet.num)),
                new Column("Name").with(planet -> planet.name),
                new Column("Diameter").with(planet -> String.format("%.03f", planet.diameter)),
                new Column("Mass").with(planet -> String.format("%.02f", planet.mass)),
                new Column("Atmosphere").with(planet -> planet.atmosphere)));

        String expected = String.join(System.lineSeparator(),
                "+---+---------+----------+------+---------------------------------+",
                "|   | Name    | Diameter | Mass | Atmosphere                      |",
                "+---+---------+----------+------+---------------------------------+",
                "| 1 | Mercury |    0.382 | 0.06 |                         minimal |",
                "+---+---------+----------+------+---------------------------------+",
                "| 2 |   Venus |    0.949 | 0.82 |        Carbon dioxide, Nitrogen |",
                "+---+---------+----------+------+---------------------------------+",
                "| 3 |   Earth |    1.000 | 1.00 |         Nitrogen, Oxygen, Argon |",
                "+---+---------+----------+------+---------------------------------+",
                "| 4 |    Mars |    0.532 | 0.11 | Carbon dioxide, Nitrogen, Argon |",
                "+---+---------+----------+------+---------------------------------+");
        assertEquals(expected, actualArray);
        assertEquals(expected, actualObjects);
    }

    @Test
    public void testTableDefaultWithoutOuterBorder() {
        String actual = AsciiTable.getTable(AsciiTable.BASIC_ASCII_WITHOUT_OUTSIDE_BORDER, planets, Arrays.asList(
                new Column("").with(planet -> Integer.toString(planet.num)),
                new Column("Name").with(planet -> planet.name),
                new Column("Diameter").with(planet -> String.format("%.03f", planet.diameter)),
                new Column("Mass").with(planet -> String.format("%.02f", planet.mass)),
                new Column("Atmosphere").with(planet -> planet.atmosphere)));

        String expected = String.join(System.lineSeparator(),
                "   | Name    | Diameter | Mass | Atmosphere                      ",
                "---+---------+----------+------+---------------------------------",
                " 1 | Mercury |    0.382 | 0.06 |                         minimal ",
                "---+---------+----------+------+---------------------------------",
                " 2 |   Venus |    0.949 | 0.82 |        Carbon dioxide, Nitrogen ",
                "---+---------+----------+------+---------------------------------",
                " 3 |   Earth |    1.000 | 1.00 |         Nitrogen, Oxygen, Argon ",
                "---+---------+----------+------+---------------------------------",
                " 4 |    Mars |    0.532 | 0.11 | Carbon dioxide, Nitrogen, Argon ");
        assertEquals(expected, actual);
    }

    @Test
    public void testTableDefaultWithoutBorders() {
        String actual = AsciiTable.getTable(AsciiTable.NO_BORDERS, planets, Arrays.asList(
                new Column("").with(planet -> Integer.toString(planet.num)),
                new Column("Name").with(planet -> planet.name),
                new Column("Diameter").with(planet -> String.format("%.03f", planet.diameter)),
                new Column("Mass").with(planet -> String.format("%.02f", planet.mass)),
                new Column("Atmosphere").with(planet -> planet.atmosphere)));

        String expected = String.join(System.lineSeparator(),
                "    Name     Diameter  Mass  Atmosphere                      ",
                " 1  Mercury     0.382  0.06                          minimal ",
                " 2    Venus     0.949  0.82         Carbon dioxide, Nitrogen ",
                " 3    Earth     1.000  1.00          Nitrogen, Oxygen, Argon ",
                " 4     Mars     0.532  0.11  Carbon dioxide, Nitrogen, Argon ");
        assertEquals(expected, actual);
    }

    @Test
    public void testTableDefaultFancyBorders() {
        String actual = AsciiTable.getTable(AsciiTable.FANCY_ASCII_WITH_OUTSIDE_BORDER, planets, Arrays.asList(
                new Column("").with(planet -> Integer.toString(planet.num)),
                new Column("Name").with(planet -> planet.name),
                new Column("Diameter").with(planet -> String.format("%.03f", planet.diameter)),
                new Column("Mass").with(planet -> String.format("%.02f", planet.mass)),
                new Column("Atmosphere").with(planet -> planet.atmosphere)));

        String expected = String.join(System.lineSeparator(),
                "╔═══╤═════════╤══════════╤══════╤═════════════════════════════════╗",
                "║   │ Name    │ Diameter │ Mass │ Atmosphere                      ║",
                "╠═══╪═════════╪══════════╪══════╪═════════════════════════════════╣",
                "║ 1 │ Mercury │    0.382 │ 0.06 │                         minimal ║",
                "╟───┼─────────┼──────────┼──────┼─────────────────────────────────╢",
                "║ 2 │   Venus │    0.949 │ 0.82 │        Carbon dioxide, Nitrogen ║",
                "╟───┼─────────┼──────────┼──────┼─────────────────────────────────╢",
                "║ 3 │   Earth │    1.000 │ 1.00 │         Nitrogen, Oxygen, Argon ║",
                "╟───┼─────────┼──────────┼──────┼─────────────────────────────────╢",
                "║ 4 │    Mars │    0.532 │ 0.11 │ Carbon dioxide, Nitrogen, Argon ║",
                "╚═══╧═════════╧══════════╧══════╧═════════════════════════════════╝");
        assertEquals(expected, actual);
    }

    @Test
    public void testTableWithAlignments() {
        String actual = AsciiTable.getTable(planets, Arrays.asList(
                new Column("").with(planet -> Integer.toString(planet.num)),
                new Column("Name").headerAlign(CENTER).dataAlign(RIGHT).with(planet -> planet.name),
                new Column("Diameter").headerAlign(RIGHT).dataAlign(CENTER).with(planet -> String.format("%.03f", planet.diameter)),
                new Column("Mass").headerAlign(RIGHT).dataAlign(LEFT).with(planet -> String.format("%.02f", planet.mass)),
                new Column("Atmosphere").headerAlign(LEFT).dataAlign(CENTER).with(planet -> planet.atmosphere)));

        String expected = String.join(System.lineSeparator(),
                "+---+---------+----------+------+---------------------------------+",
                "|   |  Name   | Diameter | Mass | Atmosphere                      |",
                "+---+---------+----------+------+---------------------------------+",
                "| 1 | Mercury |  0.382   | 0.06 |             minimal             |",
                "+---+---------+----------+------+---------------------------------+",
                "| 2 |   Venus |  0.949   | 0.82 |    Carbon dioxide, Nitrogen     |",
                "+---+---------+----------+------+---------------------------------+",
                "| 3 |   Earth |  1.000   | 1.00 |     Nitrogen, Oxygen, Argon     |",
                "+---+---------+----------+------+---------------------------------+",
                "| 4 |    Mars |  0.532   | 0.11 | Carbon dioxide, Nitrogen, Argon |",
                "+---+---------+----------+------+---------------------------------+");
        assertEquals(expected, actual);
    }

    @Test
    public void testTableWithMaxWidth() {
        String actual = AsciiTable.getTable(planets, Arrays.asList(
                new Column("").with(planet -> Integer.toString(planet.num)),
                new Column("Name").with(planet -> planet.name),
                new Column("Diameter").with(planet -> String.format("%.03f", planet.diameter)),
                new Column("Mass").with(planet -> String.format("%.02f", planet.mass)),
                new Column("Atmosphere").maxColumnWidth(8).with(planet -> planet.atmosphere)));

        String expected = String.join(System.lineSeparator(),
                "+---+---------+----------+------+--------+",
                "|   | Name    | Diameter | Mass | Atmosp |",
                "|   |         |          |      | here   |",
                "+---+---------+----------+------+--------+",
                "| 1 | Mercury |    0.382 | 0.06 | minima |",
                "|   |         |          |      |      l |",
                "+---+---------+----------+------+--------+",
                "| 2 |   Venus |    0.949 | 0.82 | Carbon |",
                "|   |         |          |      | dioxid |",
                "|   |         |          |      |     e, |",
                "|   |         |          |      | Nitrog |",
                "|   |         |          |      |     en |",
                "+---+---------+----------+------+--------+",
                "| 3 |   Earth |    1.000 | 1.00 | Nitrog |",
                "|   |         |          |      |    en, |",
                "|   |         |          |      | Oxygen |",
                "|   |         |          |      |      , |",
                "|   |         |          |      |  Argon |",
                "+---+---------+----------+------+--------+",
                "| 4 |    Mars |    0.532 | 0.11 | Carbon |",
                "|   |         |          |      | dioxid |",
                "|   |         |          |      |     e, |",
                "|   |         |          |      | Nitrog |",
                "|   |         |          |      |    en, |",
                "|   |         |          |      |  Argon |",
                "+---+---------+----------+------+--------+");
        assertEquals(expected, actual);
    }

    @Test
    public void testTableWithParagraphs() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Ut sagittis facilisis", String.join(System.lineSeparator(),
                "Duis nec urna magna. Pellentesque accumsan metus vel metus convallis, a tempus enim pretium.",
                "Integer hendrerit enim tellus, et fermentum diam sollicitudin eleifend.",
                "Cras condimentum magna non leo mattis posuere."));
        map.put("Nulla ac scelerisque", String.join(System.lineSeparator(),
                "Nullam vitae nisl vel turpis commodo ultrices.",
                "Fusce hendrerit lobortis nibh a finibus.",
                "In faucibus arcu at odio commodo facilisis."));
        map.put("Nullam ante erat", "In tincidunt pretium dui, ut sagittis sem tincidunt vitae. Nam sed convallis purus, id porttitor arcu.");

        String actual = AsciiTable.getTable(map.entrySet(), Arrays.asList(
                new Column("Key").with(Map.Entry::getKey),
                new Column("Value").dataAlign(LEFT).maxColumnWidth(30).with(Map.Entry::getValue)));

        String expected = String.join(System.lineSeparator(),
                "+-----------------------+------------------------------+",
                "| Key                   | Value                        |",
                "+-----------------------+------------------------------+",
                "| Ut sagittis facilisis | Duis nec urna magna.         |",
                "|                       | Pellentesque accumsan metus  |",
                "|                       | vel metus convallis, a       |",
                "|                       | tempus enim pretium.         |",
                "|                       | Integer hendrerit enim       |",
                "|                       | tellus, et fermentum diam    |",
                "|                       | sollicitudin eleifend.       |",
                "|                       | Cras condimentum magna non   |",
                "|                       | leo mattis posuere.          |",
                "+-----------------------+------------------------------+",
                "|  Nulla ac scelerisque | Nullam vitae nisl vel turpis |",
                "|                       | commodo ultrices.            |",
                "|                       | Fusce hendrerit lobortis     |",
                "|                       | nibh a finibus.              |",
                "|                       | In faucibus arcu at odio     |",
                "|                       | commodo facilisis.           |",
                "+-----------------------+------------------------------+",
                "|      Nullam ante erat | In tincidunt pretium dui, ut |",
                "|                       | sagittis sem tincidunt       |",
                "|                       | vitae. Nam sed convallis     |",
                "|                       | purus, id porttitor arcu.    |",
                "+-----------------------+------------------------------+");
        assertEquals(expected, actual);
    }

    @Test
    public void testValidateNullInData() {
        String[] headers = {"Lorem", "Ipsum", "Dolor"};
        String[][] data = {{"11", "12", "13"}, {"21", null, "23"}};
        String actual = AsciiTable.getTable(headers, data);
        String expected = String.join(System.lineSeparator(),
                "+-------+-------+-------+",
                "| Lorem | Ipsum | Dolor |",
                "+-------+-------+-------+",
                "|    11 |    12 |    13 |",
                "+-------+-------+-------+",
                "|    21 |       |    23 |",
                "+-------+-------+-------+");
        assertEquals(expected, actual);
    }

    @Test
    public void testValidateDifferentSizedHeaderDataColumns() {
        String[] headers = {"Lorem", "Ipsum", "Dolor", "Sit"};

        // Multiple different sized data columns, but the widest is as wide as the header
        String[][] data = {{"11", "12", "13"}, {"21", "22"}, {"31", "32", "33", "34"}};
        String actual = AsciiTable.getTable(headers, data);
        String expected = String.join(System.lineSeparator(),
                "+-------+-------+-------+-----+",
                "| Lorem | Ipsum | Dolor | Sit |",
                "+-------+-------+-------+-----+",
                "|    11 |    12 |    13 |     |",
                "+-------+-------+-------+-----+",
                "|    21 |    22 |       |     |",
                "+-------+-------+-------+-----+",
                "|    31 |    32 |    33 |  34 |",
                "+-------+-------+-------+-----+");
        assertEquals(expected, actual);

        // The widest data row is now shorter than header
        data = new String[][]{{"11", "12", "13"}, {"21", "22"}, {"31", "32", "33"}};
        actual = AsciiTable.getTable(headers, data);
        expected = String.join(System.lineSeparator(),
                "+-------+-------+-------+-----+",
                "| Lorem | Ipsum | Dolor | Sit |",
                "+-------+-------+-------+-----+",
                "|    11 |    12 |    13 |     |",
                "+-------+-------+-------+-----+",
                "|    21 |    22 |       |     |",
                "+-------+-------+-------+-----+",
                "|    31 |    32 |    33 |     |",
                "+-------+-------+-------+-----+");
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateTooFewHeaderColumns() {
        String[] headers = {"Lorem", "Ipsum", "Dolor"};
        String[][] data = {{"11", "12", "13"}, {"21", "22"}, {"31", "32", "33", "34"}};
        AsciiTable.getTable(headers, data);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateTooFewBorderChars() {
        String[] headers = {"Lorem", "Ipsum", "Dolor", "Sit"};
        String[][] data = {{"11", "12", "13"}, {"21", "22"}, {"31", "32", "33", "34"}};
        AsciiTable.getTable(new Character[10], headers, data);
    }

    @Test
    public void testTextSplitting() {
        String str = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam pretium eu dolor sodales rutrum. " +
                "Also here is a very long link: http://www.example.tld/some/resource/file.ext a few final words";
        List<String> expected = Arrays.asList(
                "Lorem ipsum", "dolor sit", "amet,", "consectetur", "adipiscing", "elit. Nam", "pretium eu", "dolor",
                "sodales", "rutrum. Also", "here is a", "very long", "link:", "http://www.e", "xample.tld/s",
                "ome/resource", "/file.ext a", "few final", "words");

        assertEquals(expected, AsciiTable.splitTextIntoLinesOfMaxLength(str, 12));
    }

    @Test
    public void testJustify() {
        String string = "test";
        String[] expected = {string + "          ", "     " + string + "     ", "          " + string};
        String[] expectedWithPadding = {"   " + string + "       ", "     " + string + "     ", "       " + string + "   "};
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], new String(AsciiTable.justify(string, values()[i], 14, 0)));
            assertEquals(expectedWithPadding[i], new String(AsciiTable.justify(string, values()[i], 14, 3)));
        }

        String expectedOddLengthCenter = "  " + string + "   ";
        assertEquals(expectedOddLengthCenter, new String(AsciiTable.justify(string, CENTER, 9, 0)));

        // Justifying to same length or less is a no-op
        assertEquals(string, new String(AsciiTable.justify(string, CENTER, string.length(), 0)));
        assertEquals(string, new String(AsciiTable.justify(string, CENTER, string.length() - 1, 0)));

        // Since padding is included in length, justifying to same length with padding should be no-op
        assertEquals(string, new String(AsciiTable.justify(string, CENTER, string.length(), 3)));
    }

    private static class Planet {
        final int num;
        final String name;
        final double diameter;
        final double mass;
        final String atmosphere;
        
        private Planet(int num, String name, double diameter, double mass, String atmosphere) {
            this.num = num;
            this.name = name;
            this.diameter = diameter;
            this.mass = mass;
            this.atmosphere = atmosphere;
        }
    }
}