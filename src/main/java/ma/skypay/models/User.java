package ma.skypay.models;

import java.time.LocalDateTime;

public class User {
    private final int userId;
    private int balance;
    private final LocalDateTime createdAt;

    public User(int userId, int balance) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }

        this.userId = userId;
        this.balance = balance;
        this.createdAt = LocalDateTime.now();
    }

    public int getUserId() {
        return userId;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.balance = balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean canAfford(int amount) {
        return balance >= amount;
    }

    public void deductBalance(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (!canAfford(amount)) {
            throw new IllegalStateException("Insufficient balance");
        }
        this.balance -= amount;
    }

    @Override
    public String toString() {
        return String.format("User{id=%d, balance=%d, createdAt=%s}",
                userId, balance, createdAt);
    }
}
