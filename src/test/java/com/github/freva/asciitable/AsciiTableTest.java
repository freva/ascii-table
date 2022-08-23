package com.github.freva.asciitable;

import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
    public void tableDefault() {
        String[][] data = {{"1", "Mercury", "0.382", "0.06", "minimal"},
                {"2", "Venus", "0.949", "0.82", "Carbon dioxide, Nitrogen"},
                {"3", "Earth", "1.000", "1.00", "Nitrogen, Oxygen, Argon"},
                {"4", "Mars", "0.532", "0.11", "Carbon dioxide, Nitrogen, Argon"}};

        String actualArray = AsciiTable.getTable(data);
        String actualObjects = AsciiTable.getTable(planets, Arrays.asList(
                new Column().with(planet -> Integer.toString(planet.num)),
                new Column().with(planet -> planet.name),
                new Column().with(planet -> String.format(Locale.US, "%.03f", planet.diameter)),
                new Column().with(planet -> String.format(Locale.US, "%.02f", planet.mass)),
                new Column().with(planet -> planet.atmosphere)));

        String expected = String.join(System.lineSeparator(),
                "+---+---------+-------+------+---------------------------------+",
                "| 1 | Mercury | 0.382 | 0.06 |                         minimal |",
                "+---+---------+-------+------+---------------------------------+",
                "| 2 |   Venus | 0.949 | 0.82 |        Carbon dioxide, Nitrogen |",
                "+---+---------+-------+------+---------------------------------+",
                "| 3 |   Earth | 1.000 | 1.00 |         Nitrogen, Oxygen, Argon |",
                "+---+---------+-------+------+---------------------------------+",
                "| 4 |    Mars | 0.532 | 0.11 | Carbon dioxide, Nitrogen, Argon |",
                "+---+---------+-------+------+---------------------------------+");
        assertEquals(expected, actualArray);
        assertEquals(expected, actualObjects);
    }

    @Test
    public void tableWithHeader() {
        String[] headers = {"", "Name", "Diameter", "Mass", "Atmosphere"};
        String[][] data = {{"1", "Mercury", "0.382", "0.06", "minimal"},
                {"2", "Venus", "0.949", "0.82", "Carbon dioxide, Nitrogen"},
                {"3", "Earth", "1.000", "1.00", "Nitrogen, Oxygen, Argon"},
                {"4", "Mars", "0.532", "0.11", "Carbon dioxide, Nitrogen, Argon"}};

        String actualArray = AsciiTable.getTable(headers, null, data);
        String actualObjects = AsciiTable.getTable(planets, Arrays.asList(
                new Column().with(planet -> Integer.toString(planet.num)),
                new Column().header("Name").with(planet -> planet.name),
                new Column().header("Diameter").with(planet -> String.format(Locale.US, "%.03f", planet.diameter)),
                new Column().header("Mass").with(planet -> String.format(Locale.US, "%.02f", planet.mass)),
                new Column().header("Atmosphere").with(planet -> planet.atmosphere)));

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
    public void tableWithHeaderAndFooter() {
        String[] headers = {"", "Name", "Diameter", "Mass", "Atmosphere"};
        String[][] data = {{"1", "Mercury", "0.382", "0.06", "minimal"},
                {"2", "Venus", "0.949", "0.82", "Carbon dioxide, Nitrogen"},
                {"3", "Earth", "1.000", "1.00", "Nitrogen, Oxygen, Argon"},
                {"4", "Mars", "0.532", "0.11", "Carbon dioxide, Nitrogen, Argon"}};

        String actualArray = AsciiTable.getTable(headers, headers, data);
        String actualObjects = AsciiTable.getTable(planets, Arrays.asList(
                new Column().with(planet -> Integer.toString(planet.num)),
                new Column().header("Name").footer("Name").with(planet -> planet.name),
                new Column().header("Diameter").footer("Diameter").with(planet -> String.format(Locale.US, "%.03f", planet.diameter)),
                new Column().header("Mass").footer("Mass").with(planet -> String.format(Locale.US, "%.02f", planet.mass)),
                new Column().header("Atmosphere").footer("Atmosphere").with(planet -> planet.atmosphere)));

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
                "+---+---------+----------+------+---------------------------------+",
                "|   | Name    | Diameter | Mass | Atmosphere                      |",
                "+---+---------+----------+------+---------------------------------+");
        assertEquals(expected, actualArray);
        assertEquals(expected, actualObjects);
    }

    @Test
    public void tableDefaultNoOutsideBorder() {
        String actual = AsciiTable.getTable(AsciiTable.BASIC_ASCII_NO_OUTSIDE_BORDER, planets, Arrays.asList(
                new Column().with(planet -> Integer.toString(planet.num)),
                new Column().header("Name").footer("Average").with(planet -> planet.name),
                new Column().header("Diameter")
                        .footer(String.format(Locale.US, "%.03f", planets.stream().mapToDouble(planet -> planet.diameter).average().orElse(0)))
                        .with(planet -> String.format(Locale.US, "%.03f", planet.diameter)),
                new Column().header("Mass")
                        .footer(String.format(Locale.US, "%.02f", planets.stream().mapToDouble(planet -> planet.mass).average().orElse(0)))
                        .with(planet -> String.format(Locale.US, "%.02f", planet.mass)),
                new Column().header("Atmosphere").with(planet -> planet.atmosphere)));

        String expected = String.join(System.lineSeparator(),
                "   | Name    | Diameter | Mass | Atmosphere                      ",
                "---+---------+----------+------+---------------------------------",
                " 1 | Mercury |    0.382 | 0.06 |                         minimal ",
                "---+---------+----------+------+---------------------------------",
                " 2 |   Venus |    0.949 | 0.82 |        Carbon dioxide, Nitrogen ",
                "---+---------+----------+------+---------------------------------",
                " 3 |   Earth |    1.000 | 1.00 |         Nitrogen, Oxygen, Argon ",
                "---+---------+----------+------+---------------------------------",
                " 4 |    Mars |    0.532 | 0.11 | Carbon dioxide, Nitrogen, Argon ",
                "---+---------+----------+------+---------------------------------",
                "   | Average | 0.716    | 0.50 |                                 ");
        assertEquals(expected, actual);
    }

    @Test
    public void testTableDefaultNoDataSeparators() {
        String actual = AsciiTable.getTable(AsciiTable.BASIC_ASCII_NO_DATA_SEPARATORS, planets, Arrays.asList(
                new Column().with(planet -> Integer.toString(planet.num)),
                new Column().header("Name").footer("Average").with(planet -> planet.name),
                new Column().header("Diameter")
                        .footer(String.format(Locale.US, "%.03f", planets.stream().mapToDouble(planet -> planet.diameter).average().orElse(0)))
                        .with(planet -> String.format(Locale.US, "%.03f", planet.diameter)),
                new Column().header("Mass")
                        .footer(String.format(Locale.US, "%.02f", planets.stream().mapToDouble(planet -> planet.mass).average().orElse(0)))
                        .with(planet -> String.format(Locale.US, "%.02f", planet.mass)),
                new Column().header("Atmosphere").with(planet -> planet.atmosphere)));

        String expected = String.join(System.lineSeparator(),
                "+---+---------+----------+------+---------------------------------+",
                "|   | Name    | Diameter | Mass | Atmosphere                      |",
                "+---+---------+----------+------+---------------------------------+",
                "| 1 | Mercury |    0.382 | 0.06 |                         minimal |",
                "| 2 |   Venus |    0.949 | 0.82 |        Carbon dioxide, Nitrogen |",
                "| 3 |   Earth |    1.000 | 1.00 |         Nitrogen, Oxygen, Argon |",
                "| 4 |    Mars |    0.532 | 0.11 | Carbon dioxide, Nitrogen, Argon |",
                "+---+---------+----------+------+---------------------------------+",
                "|   | Average | 0.716    | 0.50 |                                 |",
                "+---+---------+----------+------+---------------------------------+");
        assertEquals(expected, actual);
    }

    @Test
    public void tableDefaultNoDataSeparatorsNoOutsideBorders() {
        String actual = AsciiTable.getTable(AsciiTable.BASIC_ASCII_NO_DATA_SEPARATORS_NO_OUTSIDE_BORDER, planets, Arrays.asList(
                new Column().with(planet -> Integer.toString(planet.num)),
                new Column().header("Name").footer("Average").with(planet -> planet.name),
                new Column().header("Diameter")
                        .footer(String.format(Locale.US, "%.03f", planets.stream().mapToDouble(planet -> planet.diameter).average().orElse(0)))
                        .with(planet -> String.format(Locale.US, "%.03f", planet.diameter)),
                new Column().header("Mass")
                        .footer(String.format(Locale.US, "%.02f", planets.stream().mapToDouble(planet -> planet.mass).average().orElse(0)))
                        .with(planet -> String.format(Locale.US, "%.02f", planet.mass)),
                new Column().header("Atmosphere").with(planet -> planet.atmosphere)));

        String expected = String.join(System.lineSeparator(),
                "   | Name    | Diameter | Mass | Atmosphere                      ",
                "---+---------+----------+------+---------------------------------",
                " 1 | Mercury |    0.382 | 0.06 |                         minimal ",
                " 2 |   Venus |    0.949 | 0.82 |        Carbon dioxide, Nitrogen ",
                " 3 |   Earth |    1.000 | 1.00 |         Nitrogen, Oxygen, Argon ",
                " 4 |    Mars |    0.532 | 0.11 | Carbon dioxide, Nitrogen, Argon ",
                "---+---------+----------+------+---------------------------------",
                "   | Average | 0.716    | 0.50 |                                 ");
        assertEquals(expected, actual);
    }

    @Test
    public void tableDefaultNoBorders() {
        String actual = AsciiTable.getTable(AsciiTable.NO_BORDERS, planets, Arrays.asList(
                new Column().with(planet -> Integer.toString(planet.num)),
                new Column().header("Name").footer("Average").with(planet -> planet.name),
                new Column().header("Diameter")
                        .footer(String.format(Locale.US, "%.03f", planets.stream().mapToDouble(planet -> planet.diameter).average().orElse(0)))
                        .with(planet -> String.format(Locale.US, "%.03f", planet.diameter)),
                new Column().header("Mass")
                        .footer(String.format(Locale.US, "%.02f", planets.stream().mapToDouble(planet -> planet.mass).average().orElse(0)))
                        .with(planet -> String.format(Locale.US, "%.02f", planet.mass)),
                new Column().header("Atmosphere").with(planet -> planet.atmosphere)));

        String expected = String.join(System.lineSeparator(),
                "    Name     Diameter  Mass  Atmosphere                      ",
                " 1  Mercury     0.382  0.06                          minimal ",
                " 2    Venus     0.949  0.82         Carbon dioxide, Nitrogen ",
                " 3    Earth     1.000  1.00          Nitrogen, Oxygen, Argon ",
                " 4     Mars     0.532  0.11  Carbon dioxide, Nitrogen, Argon ",
                "    Average  0.716     0.50                                  ");
        assertEquals(expected, actual);
    }

    @Test
    public void tableDefaultFancyBorders() {
        String actual = AsciiTable.getTable(AsciiTable.FANCY_ASCII, planets, Arrays.asList(
                new Column().with(planet -> Integer.toString(planet.num)),
                new Column().header("Name").footer("Average").with(planet -> planet.name),
                new Column().header("Diameter")
                        .footer(String.format(Locale.US, "%.03f", planets.stream().mapToDouble(planet -> planet.diameter).average().orElse(0)))
                        .with(planet -> String.format(Locale.US, "%.03f", planet.diameter)),
                new Column().header("Mass")
                        .footer(String.format(Locale.US, "%.02f", planets.stream().mapToDouble(planet -> planet.mass).average().orElse(0)))
                        .with(planet -> String.format(Locale.US, "%.02f", planet.mass)),
                new Column().header("Atmosphere").with(planet -> planet.atmosphere)));

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
                "╠═══╪═════════╪══════════╪══════╪═════════════════════════════════╣",
                "║   │ Average │ 0.716    │ 0.50 │                                 ║",
                "╚═══╧═════════╧══════════╧══════╧═════════════════════════════════╝");
        assertEquals(expected, actual);
    }

    @Test
    public void tableWithAlignments() {
        String actual = AsciiTable.getTable(planets, Arrays.asList(
                new Column().with(planet -> Integer.toString(planet.num)),
                new Column().header("Name").footer("Average").headerAlign(CENTER).dataAlign(RIGHT).with(planet -> planet.name),
                new Column().header("Diameter").headerAlign(RIGHT).dataAlign(CENTER).footerAlign(CENTER)
                        .footer(String.format(Locale.US, "%.03f", planets.stream().mapToDouble(planet -> planet.diameter).average().orElse(0)))
                        .with(planet -> String.format(Locale.US, "%.03f", planet.diameter)),
                new Column().header("Mass").headerAlign(RIGHT).dataAlign(LEFT)
                        .footer(String.format(Locale.US, "%.02f", planets.stream().mapToDouble(planet -> planet.mass).average().orElse(0)))
                        .with(planet -> String.format(Locale.US, "%.02f", planet.mass)),
                new Column().header("Atmosphere").headerAlign(LEFT).dataAlign(CENTER).with(planet -> planet.atmosphere)));

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
                "+---+---------+----------+------+---------------------------------+",
                "|   | Average |  0.716   | 0.50 |                                 |",
                "+---+---------+----------+------+---------------------------------+");
        assertEquals(expected, actual);
    }

    @Test
    public void tableWithMinMaxWidth() {
        String actual = AsciiTable.getTable(planets, Arrays.asList(
                new Column().minWidth(4).with(planet -> Integer.toString(planet.num)),
                new Column().header("Name").minWidth(2).with(planet -> planet.name),
                new Column().header("Diameter").with(planet -> String.format(Locale.US, "%.03f", planet.diameter)),
                new Column().header("Mass").with(planet -> String.format(Locale.US, "%.02f", planet.mass)),
                new Column().header("Atmosphere").maxWidth(8).with(planet -> planet.atmosphere)));

        String expected = String.join(System.lineSeparator(),
                "+----+---------+----------+------+--------+",
                "|    | Name    | Diameter | Mass | Atmosp |",
                "|    |         |          |      | here   |",
                "+----+---------+----------+------+--------+",
                "|  1 | Mercury |    0.382 | 0.06 | minima |",
                "|    |         |          |      |      l |",
                "+----+---------+----------+------+--------+",
                "|  2 |   Venus |    0.949 | 0.82 | Carbon |",
                "|    |         |          |      | dioxid |",
                "|    |         |          |      |     e, |",
                "|    |         |          |      | Nitrog |",
                "|    |         |          |      |     en |",
                "+----+---------+----------+------+--------+",
                "|  3 |   Earth |    1.000 | 1.00 | Nitrog |",
                "|    |         |          |      |    en, |",
                "|    |         |          |      | Oxygen |",
                "|    |         |          |      |      , |",
                "|    |         |          |      |  Argon |",
                "+----+---------+----------+------+--------+",
                "|  4 |    Mars |    0.532 | 0.11 | Carbon |",
                "|    |         |          |      | dioxid |",
                "|    |         |          |      |     e, |",
                "|    |         |          |      | Nitrog |",
                "|    |         |          |      |    en, |",
                "|    |         |          |      |  Argon |",
                "+----+---------+----------+------+--------+");
        assertEquals(expected, actual);
    }

    @Test
    public void tableWithParagraphs() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Ut sagittis facilisis", String.join("\r\n",
                "Duis nec urna magna. Pellentesque accumsan metus vel metus convallis, a tempus enim pretium.",
                "Integer hendrerit enim tellus, et fermentum diam sollicitudin eleifend.",
                "Cras condimentum magna non leo mattis posuere."));
        map.put("Nulla ac scelerisque", String.join("\n",
                "Nullam vitae nisl vel turpis commodo ultrices.",
                "Fusce hendrerit lobortis nibh a finibus.",
                "In faucibus arcu at odio commodo facilisis."));
        map.put("Nullam ante erat", "In tincidunt pretium dui, ut sagittis sem tincidunt vitae. Nam sed convallis purus, id porttitor arcu.");

        String actual = AsciiTable.getTable(map.entrySet(), Arrays.asList(
                new Column().header("Key").with(Map.Entry::getKey),
                new Column().header("Value").dataAlign(LEFT).maxWidth(30).with(Map.Entry::getValue)));

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
    public void validateNullInHeader() {
        String[] headers = {"Lorem", null, "Dolor"};
        String[][] data = {{"11", "12", "13"}, {"21", null, "23"}};
        String actual = AsciiTable.getTable(headers, null, data);
        String expected = String.join(System.lineSeparator(),
                "+-------+----+-------+",
                "| Lorem |    | Dolor |",
                "+-------+----+-------+",
                "|    11 | 12 |    13 |",
                "+-------+----+-------+",
                "|    21 |    |    23 |",
                "+-------+----+-------+");
        assertEquals(expected, actual);
    }

    @Test
    public void validateNullInData() {
        String[] headers = {"Lorem", "Ipsum", "Dolor"};
        String[][] data = {{"11", "12", "13"}, {"21", null, "23"}};
        String actual = AsciiTable.getTable(headers, null, data);
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
    public void validateDifferentSizedHeaderDataColumns() {
        String[] headers = {"Lorem", "Ipsum", "Dolor", "Sit"};

        // Multiple different sized data columns, but the widest is as wide as the header
        String[][] data = {{"11", "12", "13"}, {"21", "22"}, {"31", "32", "33", "34"}};
        String actual = AsciiTable.getTable(headers, null, data);
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
        actual = AsciiTable.getTable(headers, null, data);
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

    @Test
    public void validateTooFewHeaderColumns() {
        String[] headers = {"Lorem", "Ipsum", "Dolor"};
        String[][] data = {{"11", "12", "13"}, {"21", "22"}, {"31", "32", "33", "34"}};
        String actual = AsciiTable.getTable(headers, null, data);
        String expected = String.join(System.lineSeparator(),
                "+-------+-------+-------+----+",
                "| Lorem | Ipsum | Dolor |    |",
                "+-------+-------+-------+----+",
                "|    11 |    12 |    13 |    |",
                "+-------+-------+-------+----+",
                "|    21 |    22 |       |    |",
                "+-------+-------+-------+----+",
                "|    31 |    32 |    33 | 34 |",
                "+-------+-------+-------+----+");
        assertEquals(expected, actual);
    }

    @Test
    public void objectDataArray() {
        Object[][] data = {{"String", 123, Instant.ofEpochSecond(1621152246)}};
        String actual = AsciiTable.getTable(data);
        String expected = String.join(System.lineSeparator(),
                "+--------+-----+----------------------+",
                "| String | 123 | 2021-05-16T08:04:06Z |",
                "+--------+-----+----------------------+");
        assertEquals(expected, actual);
    }

    @Test
    public void calculatesCorrectColumnWidthWithLineBreak() {
        String[][] data = {{"String", "First line\nSecond line"}};
        String actual = AsciiTable.getTable(data);
        String expected = String.join(System.lineSeparator(),
                "+--------+-------------+\n" +
                "| String |  First line |",
                "|        | Second line |",
                "+--------+-------------+");
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateTooFewBorderChars() {
        String[] headers = {"Lorem", "Ipsum", "Dolor", "Sit"};
        String[][] data = {{"11", "12", "13"}, {"21", "22"}, {"31", "32", "33", "34"}};
        AsciiTable.getTable(new Character[10], headers, null, data);
    }

    @Test
    public void textSplitting() {
        String str = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam pretium eu dolor sodales rutrum. " +
                "Also here is a very long link: http://www.example.tld/some/resource/file.ext a few final words";
        List<String> expected = Arrays.asList(
                "Lorem ipsum", "dolor sit", "amet,", "consectetur", "adipiscing", "elit. Nam", "pretium eu", "dolor",
                "sodales", "rutrum. Also", "here is a", "very long", "link:", "http://www.e", "xample.tld/s",
                "ome/resource", "/file.ext a", "few final", "words");

        assertEquals(expected, AsciiTable.splitTextIntoLinesOfMaxLength(str, 12));
    }

    @Test
    public void justify() {
        String string = "test";
        String[] expected = {string + "          ", "     " + string + "     ", "          " + string};
        String[] expectedWithPadding = {"   " + string + "       ", "     " + string + "     ", "       " + string + "   "};
        for (int i = 0; i < expected.length; i++) {
            assertJustify(expected[i], string, values()[i], 14, 0);
            assertJustify(expectedWithPadding[i], string, values()[i], 14, 3);
        }

        String expectedOddLengthCenter = "  " + string + "   ";
        assertJustify(expectedOddLengthCenter, string, CENTER, 9, 0);

        // Justifying to same length or less is a no-op
        assertJustify(string, string, CENTER, string.length(), 0);
        assertJustify(string, string, CENTER, string.length() - 1, 0);

        // Since padding is included in length, justifying to same length with padding should be no-op
        assertJustify(string, string, CENTER, string.length(), 3);
    }

    private static void assertJustify(String expected, String str, HorizontalAlign align, int length, int minPadding) {
        StringBuilder sb = new StringBuilder();
        AsciiTable.appendJustified(sb, str, align, length, minPadding);
        assertEquals(expected, sb.toString());
    }

    private static class Planet {
        private final int num;
        private final String name;
        private final double diameter;
        private final double mass;
        private final String atmosphere;
        
        private Planet(int num, String name, double diameter, double mass, String atmosphere) {
            this.num = num;
            this.name = name;
            this.diameter = diameter;
            this.mass = mass;
            this.atmosphere = atmosphere;
        }
    }
}
