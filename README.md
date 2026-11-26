# Online Shop (Software Design Principles, Phase 1 - Part 2)

Console online shop in Java 17 with three layers:

- `model` - entities, value objects and business rules (no dependency on other layers)
- `service` - use cases (auth, users, products, cart, orders, payment, reviews) and in-memory storage (`DataStore`)
- `cli` - terminal menus for guest, customer and admin
- `Main` - wires everything and starts the CLI

## Run

Open the folder in IntelliJ IDEA as a Maven project and run `org.example.Main`,
or from a terminal:

```
mvn compile
java -cp target/classes org.example.Main
```

Demo admin: `admin@shop.com` / `admin123`. Customers can register from the first menu.
The payment gateway is simulated; before each payment you choose success, failure or wrong amount.
