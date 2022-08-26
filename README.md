# ASCII Tables for Java
Easily create and customize simple ASCII tables in Java. Based off 
[nedtwigg/asciitable](https://github.com/nedtwigg/asciitable) and 
[klaus31/ascii-art-table](https://github.com/klaus31/ascii-art-table).

## How to get it?
Maven:
```xml
<dependency>
  <groupId>com.github.freva</groupId>
  <artifactId>ascii-table</artifactId>
  <version>1.5.0</version>
</dependency>
```
Gradle:
```
compile 'com.github.freva:ascii-table:1.5.0'
```

## Basic case
```java
String[] headers = {"", "Name", "Diameter", "Mass", "Atmosphere"};
String[][] data = {
        {"1", "Mercury", "0.382", "0.06", "minimal"},
        {"2", "Venus", "0.949", "0.82", "Carbon dioxide, Nitrogen"},
        {"3", "Earth", "1.000", "1.00", "Nitrogen, Oxygen, Argon"},
        {"4", "Mars", "0.532", "0.11", "Carbon dioxide, Nitrogen, Argon"}};

System.out.println(AsciiTable.getTable(headers, data));
```
Will print
```
+---+---------+----------+------+---------------------------------+
|   | Name    | Diameter | Mass | Atmosphere                      |
+---+---------+----------+------+---------------------------------+
| 1 | Mercury |    0.382 | 0.06 |                         minimal |
+---+---------+----------+------+---------------------------------+
| 2 |   Venus |    0.949 | 0.82 |        Carbon dioxide, Nitrogen |
+---+---------+----------+------+---------------------------------+
| 3 |   Earth |    1.000 | 1.00 |         Nitrogen, Oxygen, Argon |
+---+---------+----------+------+---------------------------------+
| 4 |    Mars |    0.532 | 0.11 | Carbon dioxide, Nitrogen, Argon |
+---+---------+----------+------+---------------------------------+
```

## Table from Collections
```java
List<Planet> planets = Arrays.asList(
        new Planet(1, "Mercury", 0.382, 0.06, "minimal"),
        new Planet(2, "Venus", 0.949, 0.82, "Carbon dioxide, Nitrogen"),
        new Planet(3, "Earth", 1.0, 1.0, "Nitrogen, Oxygen, Argon"),
        new Planet(4, "Mars", 0.532, 0.11, "Carbon dioxide, Nitrogen, Argon"));

System.out.println(AsciiTable.getTable(planets, Arrays.asList(
        new Column().with(planet -> Integer.toString(planet.num)),
        new Column().header("Name").with(planet -> planet.name),
        new Column().header("Diameter").with(planet -> String.format("%.03f", planet.diameter)),
        new Column().header("Mass").with(planet -> String.format("%.02f", planet.mass)),
        new Column().header("Atmosphere").with(planet -> planet.atmosphere))));
```
Prints the same table as above.

## Column alignments
Horizontally align header and data columns independently to left, right and center:
```java
System.out.println(AsciiTable.getTable(planets, Arrays.asList(
        new Column().with(planet -> Integer.toString(planet.num)),
        new Column().header("Name").headerAlign(HorizontalAlign.CENTER).dataAlign(HorizontalAlign.LEFT).with(planet -> planet.name),
        new Column().header("Diameter").with(planet -> String.format("%.03f", planet.diameter)),
        new Column().header("Mass").with(planet -> String.format("%.02f", planet.mass)),
        new Column().header("Atmosphere").headerAlign(HorizontalAlign.RIGHT).dataAlign(HorizontalAlign.CENTER).with(planet -> planet.atmosphere))));
```
Prints
```
+---+---------+----------+------+---------------------------------+
|   |  Name   | Diameter | Mass |                      Atmosphere |
+---+---------+----------+------+---------------------------------+
| 1 | Mercury |    0.382 | 0.06 |             minimal             |
+---+---------+----------+------+---------------------------------+
| 2 | Venus   |    0.949 | 0.82 |    Carbon dioxide, Nitrogen     |
+---+---------+----------+------+---------------------------------+
| 3 | Earth   |    1.000 | 1.00 |     Nitrogen, Oxygen, Argon     |
+---+---------+----------+------+---------------------------------+
| 4 | Mars    |    0.532 | 0.11 | Carbon dioxide, Nitrogen, Argon |
+---+---------+----------+------+---------------------------------+
```

## Min/max column width
Limit any column to certain width:
```java
System.out.println(AsciiTable.getTable(planets, Arrays.asList(
        new Column().minWidth(5).with(planet -> Integer.toString(planet.num)),
        new Column().header("Name").with(planet -> planet.name),
        new Column().header("Diameter").with(planet -> String.format("%.03f", planet.diameter)),
        new Column().header("Mass").with(planet -> String.format("%.02f", planet.mass)),
        new Column().header("Atmosphere Composition").maxWidth(12).with(planet -> planet.atmosphere))));
```
Prints
```
+-----+---------+----------+------+--------+
|     | Name    | Diameter | Mass | Atmosp |
|     |         |          |      | here   |
+-----+---------+----------+------+--------+
|   1 | Mercury |    0.382 | 0.06 | minima |
|     |         |          |      |      l |
+-----+---------+----------+------+--------+
|   2 |   Venus |    0.949 | 0.82 | Carbon |
|     |         |          |      | dioxid |
|     |         |          |      |     e, |
|     |         |          |      | Nitrog |
|     |         |          |      |     en |
+-----+---------+----------+------+--------+
|   3 |   Earth |    1.000 | 1.00 | Nitrog |
|     |         |          |      |    en, |
|     |         |          |      | Oxygen |
|     |         |          |      |      , |
|     |         |          |      |  Argon |
+-----+---------+----------+------+--------+
|   4 |    Mars |    0.532 | 0.11 | Carbon |
|     |         |          |      | dioxid |
|     |         |          |      |     e, |
|     |         |          |      | Nitrog |
|     |         |          |      |    en, |
|     |         |          |      |  Argon |
+-----+---------+----------+------+--------+
```

## Controlling overflow behaviour
By default when a cell reaches max column width, a newline is inserted before remainder of the text. Other possible
behaviours are clipping and ellipsis:
```java
System.out.println(AsciiTable.getTable(planets, Arrays.asList(
        new Column().header("Atmosphere Composition").maxWidth(12, OverflowBehaviour.NEWLINE).with(planet -> planet.atmosphere),
        new Column().header("Atmosphere Composition").maxWidth(12, OverflowBehaviour.CLIP_LEFT).with(planet -> planet.atmosphere),
        new Column().header("Atmosphere Composition").maxWidth(12, OverflowBehaviour.CLIP_RIGHT).with(planet -> planet.atmosphere),
        new Column().header("Atmosphere Composition").maxWidth(12, OverflowBehaviour.ELLIPSIS_LEFT).with(planet -> planet.atmosphere),
        new Column().header("Atmosphere Composition").maxWidth(12, OverflowBehaviour.ELLIPSIS_RIGHT).with(planet -> planet.atmosphere))));
```
Prints
```
+------------+------------+------------+------------+------------+
| Atmosphere | omposition | Atmosphere | …mposition | Atmospher… |
| Compositio |            |            |            |            |
| n          |            |            |            |            |
+------------+------------+------------+------------+------------+
|    minimal |    minimal |    minimal |    minimal |    minimal |
+------------+------------+------------+------------+------------+
|     Carbon | , Nitrogen | Carbon dio | … Nitrogen | Carbon di… |
|   dioxide, |            |            |            |            |
|   Nitrogen |            |            |            |            |
+------------+------------+------------+------------+------------+
|  Nitrogen, | gen, Argon | Nitrogen,  | …en, Argon | Nitrogen,… |
|    Oxygen, |            |            |            |            |
|      Argon |            |            |            |            |
+------------+------------+------------+------------+------------+
|     Carbon | gen, Argon | Carbon dio | …en, Argon | Carbon di… |
|   dioxide, |            |            |            |            |
|  Nitrogen, |            |            |            |            |
|      Argon |            |            |            |            |
+------------+------------+------------+------------+------------+
```

**Bonus:** The original line breaks (`System.lineSeparator()`) are always preserved,
so you can split your text however you want before making the table.

## Footer
Special row at the end of the table that can be used to for example repeat the 
header if the table is really long, or display some summary information such as
average or totals.

```java
System.out.println(AsciiTable.getTable(planets, Arrays.asList(
        new Column().with(planet -> Integer.toString(planet.num)),
        new Column().header("Name").footer("Average").headerAlign(CENTER).dataAlign(RIGHT).with(planet -> planet.name),
        new Column().header("Diameter").headerAlign(RIGHT).dataAlign(CENTER).footerAlign(CENTER)
                .footer(String.format("%.03f", planets.stream().mapToDouble(planet -> planet.diameter).average().orElse(0)))
                .with(planet -> String.format("%.03f", planet.diameter)),
        new Column().header("Mass").headerAlign(RIGHT).dataAlign(LEFT)
                .footer(String.format("%.02f", planets.stream().mapToDouble(planet -> planet.mass).average().orElse(0)))
                .with(planet -> String.format("%.02f", planet.mass)),
        new Column().header("Atmosphere").headerAlign(LEFT).dataAlign(CENTER).with(planet -> planet.atmosphere))));
```
Prints
```
+---+---------+----------+------+---------------------------------+
|   |  Name   | Diameter | Mass | Atmosphere                      |
+---+---------+----------+------+---------------------------------+
| 1 | Mercury |  0.382   | 0.06 |             minimal             |
+---+---------+----------+------+---------------------------------+
| 2 |   Venus |  0.949   | 0.82 |    Carbon dioxide, Nitrogen     |
+---+---------+----------+------+---------------------------------+
| 3 |   Earth |  1.000   | 1.00 |     Nitrogen, Oxygen, Argon     |
+---+---------+----------+------+---------------------------------+
| 4 |    Mars |  0.532   | 0.11 | Carbon dioxide, Nitrogen, Argon |
+---+---------+----------+------+---------------------------------+
|   | Average |  0.716   | 0.50 |                                 |
+---+---------+----------+------+---------------------------------+
```

## Border styles
```java
Character[] borderStyle = ...;
System.out.println(AsciiTable.getTable(borderStyles, planets, Arrays.asList(
        new Column().with(planet -> Integer.toString(planet.num)),
        new Column().header("Name").footer("Average").headerAlign(CENTER).dataAlign(RIGHT).with(planet -> planet.name),
        new Column().header("Diameter").headerAlign(RIGHT).dataAlign(CENTER).footerAlign(CENTER)
                .footer(String.format("%.03f", planets.stream().mapToDouble(planet -> planet.diameter).average().orElse(0)))
                .with(planet -> String.format("%.03f", planet.diameter)),
        new Column().header("Mass").headerAlign(RIGHT).dataAlign(LEFT)
                .footer(String.format("%.02f", planets.stream().mapToDouble(planet -> planet.mass).average().orElse(0)))
                .with(planet -> String.format("%.02f", planet.mass)),
        new Column().header("Atmosphere").headerAlign(LEFT).dataAlign(CENTER).with(planet -> planet.atmosphere))));
```
Default style is `AsciiTable.BASIC_ASCII`

___
With `borderStyle = AsciiTable.BASIC_ASCII_NO_OUTSIDE_BORDER`, it prints
```
   |  Name   | Diameter | Mass | Atmosphere                      
---+---------+----------+------+---------------------------------
 1 | Mercury |  0.382   | 0.06 |             minimal             
---+---------+----------+------+---------------------------------
 2 |   Venus |  0.949   | 0.82 |    Carbon dioxide, Nitrogen     
---+---------+----------+------+---------------------------------
 3 |   Earth |  1.000   | 1.00 |     Nitrogen, Oxygen, Argon     
---+---------+----------+------+---------------------------------
 4 |    Mars |  0.532   | 0.11 | Carbon dioxide, Nitrogen, Argon 
---+---------+----------+------+---------------------------------
   | Average |  0.716   | 0.50 |                                 
```

____
With `borderStyle = AsciiTable.BASIC_ASCII_NO_DATA_SEPARATORS`, it prints
```
+---+---------+----------+------+---------------------------------+
|   |  Name   | Diameter | Mass | Atmosphere                      |
+---+---------+----------+------+---------------------------------+
| 1 | Mercury |  0.382   | 0.06 |             minimal             |
| 2 |   Venus |  0.949   | 0.82 |    Carbon dioxide, Nitrogen     |
| 3 |   Earth |  1.000   | 1.00 |     Nitrogen, Oxygen, Argon     |
| 4 |    Mars |  0.532   | 0.11 | Carbon dioxide, Nitrogen, Argon |
+---+---------+----------+------+---------------------------------+
|   | Average |  0.716   | 0.50 |                                 |
+---+---------+----------+------+---------------------------------+
```

____
With `borderStyle = AsciiTable.BASIC_ASCII_NO_DATA_SEPARATORS_NO_OUTSIDE_BORDER`, it prints
```
   |  Name   | Diameter | Mass | Atmosphere                      
---+---------+----------+------+---------------------------------
 1 | Mercury |  0.382   | 0.06 |             minimal             
 2 |   Venus |  0.949   | 0.82 |    Carbon dioxide, Nitrogen     
 3 |   Earth |  1.000   | 1.00 |     Nitrogen, Oxygen, Argon     
 4 |    Mars |  0.532   | 0.11 | Carbon dioxide, Nitrogen, Argon 
---+---------+----------+------+---------------------------------
   | Average |  0.716   | 0.50 |                                 
```

____
With `borderStyle = AsciiTable.NO_BORDERS`, it prints
```
     Name    Diameter  Mass  Atmosphere                      
 1  Mercury   0.382    0.06              minimal             
 2    Venus   0.949    0.82     Carbon dioxide, Nitrogen     
 3    Earth   1.000    1.00      Nitrogen, Oxygen, Argon     
 4     Mars   0.532    0.11  Carbon dioxide, Nitrogen, Argon 
    Average   0.716    0.50                                  
```

___
With `borderStyle = AsciiTable.FANCY_ASCII`, it prints
```
╔═══╤═════════╤══════════╤══════╤═════════════════════════════════╗
║   │  Name   │ Diameter │ Mass │ Atmosphere                      ║
╠═══╪═════════╪══════════╪══════╪═════════════════════════════════╣
║ 1 │ Mercury │  0.382   │ 0.06 │             minimal             ║
╟───┼─────────┼──────────┼──────┼─────────────────────────────────╢
║ 2 │   Venus │  0.949   │ 0.82 │    Carbon dioxide, Nitrogen     ║
╟───┼─────────┼──────────┼──────┼─────────────────────────────────╢
║ 3 │   Earth │  1.000   │ 1.00 │     Nitrogen, Oxygen, Argon     ║
╟───┼─────────┼──────────┼──────┼─────────────────────────────────╢
║ 4 │    Mars │  0.532   │ 0.11 │ Carbon dioxide, Nitrogen, Argon ║
╠═══╪═════════╪══════════╪══════╪═════════════════════════════════╣
║   │ Average │  0.716   │ 0.50 │                                 ║
╚═══╧═════════╧══════════╧══════╧═════════════════════════════════╝
```
___

Border styles is a `Character` array of length 29 and you can configure your own 
styles by passing in different array. To see which element in `Character` array 
corresponds to which element in the table:
```java
Character[] borderStyles = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123".chars().mapToObj(c -> (char)c).toArray(Character[]::new);
```
Prints
```
ABBBCBBBBBBBBBCBBBBBBBBBBCBBBBBBCBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBD
E   F  Name   F Diameter F Mass F Atmosphere                      G
HIIIJIIIIIIIIIJIIIIIIIIIIJIIIIIIJIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIK
L 1 M Mercury M  0.382   M 0.06 M             minimal             N
OPPPQPPPPPPPPPQPPPPPPPPPPQPPPPPPQPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPR
L 2 M   Venus M  0.949   M 0.82 M    Carbon dioxide, Nitrogen     N
OPPPQPPPPPPPPPQPPPPPPPPPPQPPPPPPQPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPR
L 3 M   Earth M  1.000   M 1.00 M     Nitrogen, Oxygen, Argon     N
OPPPQPPPPPPPPPQPPPPPPPPPPQPPPPPPQPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPR
L 4 M    Mars M  0.532   M 0.11 M Carbon dioxide, Nitrogen, Argon N
STTTUTTTTTTTTTUTTTTTTTTTTUTTTTTTUTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTV
W   X Average X  0.716   X 0.50 X                                 Y
Z111211111111121111111111211111121111111111111111111111111111111113
```
