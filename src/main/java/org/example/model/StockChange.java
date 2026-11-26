package org.example.model;

import java.time.LocalDateTime;

public record StockChange(String id, String productId, String adminId,
                          int oldStock, int newStock, LocalDateTime changedAt) {
}
