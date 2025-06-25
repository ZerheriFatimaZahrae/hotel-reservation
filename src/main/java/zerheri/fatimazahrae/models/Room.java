package zerheri.fatimazahrae.models;

import zerheri.fatimazahrae.enums.RoomType;

import java.time.LocalDateTime;

public class Room {
    private final int roomNumber;
    private RoomType roomType;
    private int pricePerNight;
    private final LocalDateTime createdAt;

    public Room(int roomNumber, RoomType roomType, int pricePerNight) {
        if (roomNumber <= 0) {
            throw new IllegalArgumentException("Room number must be positive");
        }
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }
        if (pricePerNight <= 0) {
            throw new IllegalArgumentException("Price per night must be positive");
        }

        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.createdAt = LocalDateTime.now();
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public int getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(int pricePerNight) {
        if (pricePerNight <= 0) {
            throw new IllegalArgumentException("Price per night must be positive");
        }
        this.pricePerNight = pricePerNight;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return String.format("Room{number=%d, type=%s, price=%d/night, createdAt=%s}",
                roomNumber, roomType.getDisplayName(), pricePerNight, createdAt);
    }
}
