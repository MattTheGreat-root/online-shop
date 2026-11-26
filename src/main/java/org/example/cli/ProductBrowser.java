package org.example.cli;

import org.example.model.Product;
import org.example.service.ProductSearchCriteria;
import org.example.service.ShopServices;
import org.example.service.SortOption;

import java.util.List;

public class ProductBrowser {

    private final ConsoleIO io;
    private final ConsolePrinter printer;
    private final ShopServices services;

    public ProductBrowser(ConsoleIO io, ConsolePrinter printer, ShopServices services) {
        this.io = io;
        this.printer = printer;
        this.services = services;
    }

    public void showMenu() {
        new Menu(io, "Products")
                .option("Show all products", this::showAll)
                .option("Search, filter and sort", this::search)
                .option("Browse categories", this::browseCategories)
                .option("Product details and reviews", this::showDetails)
                .run();
    }

    private void showAll() {
        printer.products(services.products().listActiveProducts());
    }

    private void search() {
        ProductSearchCriteria criteria = new ProductSearchCriteria()
                .withQuery(io.readLine("Search text (empty = all): "))
                .withCategory(io.readLine("Category id (empty = any): "))
                .withPriceRange(io.readOptionalLong("Min price (empty = none): "),
                        io.readOptionalLong("Max price (empty = none): "))
                .withBrand(io.readLine("Brand (empty = any): "))
                .withMinRating(readOptionalRating())
                .onlyInStock(io.confirm("Only products in stock?"))
                .sortBy(readSortOption());
        printer.products(services.products().search(criteria));
    }

    private Integer readOptionalRating() {
        Long rating = io.readOptionalLong("Minimum rating 1-5 (empty = any): ");
        return rating == null ? null : rating.intValue();
    }

    private SortOption readSortOption() {
        SortOption[] options = SortOption.values();
        for (int i = 0; i < options.length; i++) {
            io.println((i + 1) + ". " + options[i]);
        }
        return options[io.readInt("Sort by: ", 1, options.length) - 1];
    }

    private void browseCategories() {
        printer.categories(services.categories().listActiveCategories());
        String categoryId = io.readLine("Category id: ");
        List<Product> products = services.categories().listActiveProductsOf(categoryId);
        if (products.isEmpty()) {
            io.println("This category has no product yet.");
        } else {
            printer.products(products);
        }
    }

    private void showDetails() {
        String productId = io.readLine("Product id: ");
        Product product = services.products().getActiveProduct(productId);
        printer.productDetails(product, services.reviews().averageRating(productId),
                services.reviews().listReviews(productId));
    }
}
