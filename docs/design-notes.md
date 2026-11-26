# Design notes

The shop runs as a single Java process. The console menus collect input and call the services through `ShopServices`. Services check access and coordinate tasks such as checkout and payment. Rules that belong to an entity stay with that entity. For example, `Product` checks available stock and `Order` controls the transition from `PENDING_PAYMENT` to `PAID`.

Checkout copies product names and prices into order items, saves the order, and clears the cart. Payment checks stock again because orders do not reserve it. The service saves each gateway result that reaches it. On success, it marks the order paid and updates stock and sold counts. A failed or rejected payment leaves the order pending.

## SOLID in the Java code

| Principle | Where it shows up |
| --- | --- |
| Single responsibility | `ProductService` handles catalog updates and delegates search and sorting to `ProductSearch`. `DataStore` manages its collections, while business checks stay with the services and domain classes. |
| Open/closed | `PaymentService` already accepts a `PaymentGateway`, so another gateway can be supplied through the constructor. `ProductSearch` now gets its rating data through `ProductRatings`, so it can use another rating source without changing the filters. |
| Liskov substitution | `PaymentGateway` returns a `GatewayResult` for all three simulator outcomes, and `PaymentService` handles those outcomes through the same contract. A replacement storage adapter must also honor the observed contract, such as throwing `NotFoundException` for required lookups and preserving the iteration order used for lists. |
| Interface segregation | Each service receives a storage view for its use case, such as `ShopStorage.Carts` or `ShopStorage.Payments`. `AccessPolicy` only needs `UserLookup`. The services used to receive the full store and work with its maps directly. |
| Dependency inversion | The services now depend on `ShopStorage` interfaces. `DataStore` implements those interfaces and `ShopServices.inMemory()` selects that implementation and the simulated gateway. |

## Diagram notes

- The class diagram has separate domain and service views. The service view shows the main dependencies and selected interface inheritance.
- The object diagram starts from a fresh demo run. The first customer buys two copies of **Clean Code** (P4) at 950,000 Toman each, and the first payment succeeds. The order total is 1,900,000 Toman, the cart is empty, and product stock drops from 20 to 18.
- The sequence diagram starts when the customer menu calls `payOrder`. It shows the three gateway outcomes and the stock change that occurs on success.
