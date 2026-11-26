# Online Shop

A Java 17 console shop for the Software Design Principles course at Amirkabir University of Technology. Guests browse the catalog, customers place orders and leave reviews after purchase, and admins manage the catalog and stock. The app starts with demo products and keeps shop data in memory for the duration of the run.

## Team

| Member | Phase 2 work |
| --- | --- |
| محمدمتین توکلی | SOLID refactor, class and object diagrams, payment sequence diagram |
| رادین احمدزاده | Activity, component and deployment diagrams, final design PDF |

## Run

Use JDK 17 or newer and Maven. In IntelliJ IDEA, import the Maven project and run `org.example.Main`. From a terminal:

```sh
mvn compile
java -cp target/classes org.example.Main
```

The demo admin logs in with `admin@shop.com` and `admin123`. Create a customer from the main menu to try checkout. Add a shipping address under **My Account**, add an item to **My Cart**, and open **Orders and Payment**. The payment menu lets you choose a success, a failure, or a wrong amount. A failed or rejected attempt leaves the order pending so you can try again.

## Source layout

- `org.example.model` holds entities, value objects, and domain rules.
- `org.example.service` handles shop use cases. `ShopServices.inMemory()` wires the services to `DataStore` and the simulated payment gateway.
- `org.example.cli` handles the terminal menus, input and output.
- `org.example.Main` seeds the demo data and starts the CLI.

## Design

- [Diagram sources](docs/uml/)
- [System overview and SOLID notes](docs/design-notes.md)

Render the PlantUML diagram sources as SVG from the repository root with a local PlantUML JAR:

```sh
java -Djava.awt.headless=true -jar /path/to/plantuml.jar -charset UTF-8 -tsvg -o /tmp/online-shop-uml docs/uml/class-*.puml docs/uml/object-*.puml docs/uml/sequence-*.puml
```
