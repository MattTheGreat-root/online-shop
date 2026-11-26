package org.example.model;

import java.time.LocalDateTime;

public class Session {

    private final String id;
    private final String userId;
    private final LocalDateTime startedAt;
    private boolean active;

    public Session(String id, String userId) {
        this.id = id;
        this.userId = userId;
        this.startedAt = LocalDateTime.now();
        this.active = true;
    }

    public void end() {
        active = false;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public boolean isActive() {
        return active;
    }
}
