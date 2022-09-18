package com.github.freva.asciitable;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.github.freva.asciitable.HorizontalAlign.*;
import static org.junit.Assert.assertEquals;

public class AsciiTableTest {
    private static final List<Planet> planets = Arrays.asList(
            new Planet(1, "Mercury", 0.382, 0.06, "minimal"),
            new Planet(2, "Venus", 0.949, 0.82, "Carbon dioxide, Nitrogen"),
            new Planet(3, "Earth", 1.0, 1.0, "Nitrogen, Oxygen, Argon"),
            new Planet(4, "Mars", 0.532, 0.11, "Carbon dioxide, Nitrogen, Argon"));
    private static final Map<String, String> paragraphs = new LinkedHashMap<String, String>(){{
        put("Ut sagittis facilisis", String.join("\r\n",
                "Duis nec urna magna. Pellentesque accumsan metus vel metus convallis, a tempus enim pretium.",
                "Integer hendrerit enim tellus, et fermentum diam sollicitudin eleifend.",
                "Cras condimentum magna non leo mattis posuere."));
        put("Nulla ac scelerisque", String.join("\n",
                "Nullam vitae nisl vel turpis commodo ultrices.",
                "Fusce hendrerit lobortis nibh a finibus.",
                "In faucibus arcu at odio commodo facilisis."));
        put("Nullam ante erat", "Nam sed convallis purus arcu");
    }};

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
    public void tableWithParagraphsNewlineOverflow() {
        assertParagraphs(OverflowBehaviour.NEWLINE,
                "+------------------+------------------------------+",
                "| Long first       | An even                      |",
                "| header           | longer second super header   |",
                "|                  | with line breaks             |",
                "+------------------+------------------------------+",
                "|      Ut sagittis | Duis nec urna magna.         |",
                "|        facilisis | Pellentesque accumsan metus  |",
                "|                  | vel metus convallis, a       |",
                "|                  | tempus enim pretium.         |",
                "|                  | Integer hendrerit enim       |",
                "|                  | tellus, et fermentum diam    |",
                "|                  | sollicitudin eleifend.       |",
                "|                  | Cras condimentum magna non   |",
                "|                  | leo mattis posuere.          |",
                "+------------------+------------------------------+",
                "|         Nulla ac | Nullam vitae nisl vel turpis |",
                "|      scelerisque | commodo ultrices.            |",
                "|                  | Fusce hendrerit lobortis     |",
                "|                  | nibh a finibus.              |",
                "|                  | In faucibus arcu at odio     |",
                "|                  | commodo facilisis.           |",
                "+------------------+------------------------------+",
                "| Nullam ante erat | Nam sed convallis purus arcu |",
                "+------------------+------------------------------+");
    }

    @Test
    public void tableWithParagraphsClipLeftOverflow() {
        assertParagraphs(OverflowBehaviour.CLIP_LEFT,
                "+------------------+------------------------------+",
                "| ong first header | An even                      |",
                "|                  | uper header with line breaks |",
                "+------------------+------------------------------+",
                "| gittis facilisis | llis, a tempus enim pretium. |",
                "|                  |  diam sollicitudin eleifend. |",
                "|                  | agna non leo mattis posuere. |",
                "+------------------+------------------------------+",
                "| a ac scelerisque | vel turpis commodo ultrices. |",
                "|                  | rit lobortis nibh a finibus. |",
                "|                  | u at odio commodo facilisis. |",
                "+------------------+------------------------------+",
                "| Nullam ante erat | Nam sed convallis purus arcu |",
                "+------------------+------------------------------+");
    }

    @Test
    public void tableWithParagraphsClipRightOverflow() throws IOException {
        assertParagraphs(OverflowBehaviour.CLIP_RIGHT,
                "+------------------+------------------------------+",
                "| Long first heade | An even                      |",
                "|                  | longer second super header w |",
                "+------------------+------------------------------+",
                "| Ut sagittis faci | Duis nec urna magna. Pellent |",
                "|                  | Integer hendrerit enim tellu |",
                "|                  | Cras condimentum magna non l |",
                "+------------------+------------------------------+",
                "| Nulla ac sceleri | Nullam vitae nisl vel turpis |",
                "|                  | Fusce hendrerit lobortis nib |",
                "|                  | In faucibus arcu at odio com |",
                "+------------------+------------------------------+",
                "| Nullam ante erat | Nam sed convallis purus arcu |",
                "+------------------+------------------------------+");
    }

    @Test
    public void tableWithParagraphsEllipsisLeftOverflow() {
        assertParagraphs(OverflowBehaviour.ELLIPSIS_LEFT,
                "+------------------+------------------------------+",
                "| …ng first header | An even                      |",
                "|                  | …per header with line breaks |",
                "+------------------+------------------------------+",
                "| …ittis facilisis | …lis, a tempus enim pretium. |",
                "|                  | …diam sollicitudin eleifend. |",
                "|                  | …gna non leo mattis posuere. |",
                "+------------------+------------------------------+",
                "| … ac scelerisque | …el turpis commodo ultrices. |",
                "|                  | …it lobortis nibh a finibus. |",
                "|                  | … at odio commodo facilisis. |",
                "+------------------+------------------------------+",
                "| Nullam ante erat | Nam sed convallis purus arcu |",
                "+------------------+------------------------------+");
    }

    @Test
    public void tableWithParagraphsEllipsisRightOverflow() {
        assertParagraphs(OverflowBehaviour.ELLIPSIS_RIGHT,
                "+------------------+------------------------------+",
                "| Long first head… | An even                      |",
                "|                  | longer second super header … |",
                "+------------------+------------------------------+",
                "| Ut sagittis fac… | Duis nec urna magna. Pellen… |",
                "|                  | Integer hendrerit enim tell… |",
                "|                  | Cras condimentum magna non … |",
                "+------------------+------------------------------+",
                "| Nulla ac sceler… | Nullam vitae nisl vel turpi… |",
                "|                  | Fusce hendrerit lobortis ni… |",
                "|                  | In faucibus arcu at odio co… |",
                "+------------------+------------------------------+",
                "| Nullam ante erat | Nam sed convallis purus arcu |",
                "+------------------+------------------------------+");
    }

    @Test
    public void invisibleColumns() {
        String[][] data = {{"11", "12", "13", "14"}, {"21", "22"}};
        BiConsumer<Boolean[], String[]> asserter = (visibleColumns, expectedRows) -> {
            Column[] columns = Arrays.stream(visibleColumns).map(visible -> new Column().visible(visible)).toArray(Column[]::new);
            assertEquals(String.join(System.lineSeparator(), expectedRows), AsciiTable.getTable(AsciiTable.NO_BORDERS, columns, data));
        };

        asserter.accept(new Boolean[]{true, false}, new String[]{" 11  13  14 ", " 21         "});
        asserter.accept(new Boolean[]{true, false, true}, new String[]{" 11  13  14 ", " 21         "});
        asserter.accept(new Boolean[]{true, false, true, false}, new String[]{" 11  13 ", " 21     "});
        asserter.accept(new Boolean[]{false, false, true, false}, new String[]{" 13 ", "    "});
        asserter.accept(new Boolean[]{}, new String[]{" 11  12  13  14 ", " 21  22         "});
        asserter.accept(new Boolean[]{true, true, true, true}, new String[]{" 11  12  13  14 ", " 21  22         "});
        asserter.accept(new Boolean[]{true, true, true, true, false}, new String[]{" 11  12  13  14 ", " 21  22         "});
        asserter.accept(new Boolean[]{true, true, true, true, true}, new String[]{" 11  12  13  14   ", " 21  22           "});
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
    public void customLineSeparator() {
        String[][] data = {{"11", "12", "13"}, {"21", "22"}};
        String expected = "+----+----+----+\n\n" +
                          "| 11 | 12 | 13 |\n\n" +
                          "+----+----+----+\n\n" +
                          "| 21 | 22 |    |\n\n" +
                          "+----+----+----+";
        assertEquals(expected, AsciiTable.builder().data(data).lineSeparator("\n\n").asString());
    }

    @Test
    public void writesToOutputStream() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String[][] data = {{"11", "12", "13"}, {"21", "22"}};
        AsciiTable.builder().data(data).writeTo(baos);
        String expected = String.join(System.lineSeparator(),
                "+----+----+----+",
                "| 11 | 12 | 13 |",
                "+----+----+----+",
                "| 21 | 22 |    |",
                "+----+----+----+");
        assertEquals(expected, baos.toString());
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
                "+--------+-------------+",
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
    public void justify() throws IOException {
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

    private static void assertParagraphs(OverflowBehaviour overflowBehaviour, String... expectedLines) {
        String actual = AsciiTable.getTable(paragraphs.entrySet(), Arrays.asList(
                new Column().header("Long first header").maxWidth(18, overflowBehaviour).with(Map.Entry::getKey),
                new Column().header("An even\nlonger second super header with line breaks").dataAlign(LEFT).maxWidth(30, overflowBehaviour).with(Map.Entry::getValue)));

        String expected = String.join(System.lineSeparator(), expectedLines);
        assertEquals(expected, actual);
    }

    private static void assertJustify(String expected, String str, HorizontalAlign align, int length, int minPadding) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(baos);
        AsciiTable.writeJustified(osw, str, align, length, minPadding);
        osw.flush();
        assertEquals(expected, baos.toString());
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
