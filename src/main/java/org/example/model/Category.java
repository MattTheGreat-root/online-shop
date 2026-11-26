package org.example.model;

public class Category {

    private final String id;
    private final String name;
    private boolean active;

    public Category(String id, String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Category name cannot be empty");
        }
        this.id = id;
        this.name = name.trim();
        this.active = true;
    }

    public void deactivate() {
        active = false;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }
}
