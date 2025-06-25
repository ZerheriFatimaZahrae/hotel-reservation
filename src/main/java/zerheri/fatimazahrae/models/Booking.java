package zerheri.fatimazahrae.models;

import zerheri.fatimazahrae.enums.BookingStatus;
import zerheri.fatimazahrae.enums.RoomType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Booking {
    private static int nextBookingId = 1;

    private final int bookingId;
    private final int userId;
    private final int roomNumber;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final int totalAmount;
    private final BookingStatus status;
    private final LocalDateTime createdAt;

    // Room and User information at the time of booking
    private final RoomType roomTypeAtBooking;
    private final int roomPriceAtBooking;
    private final int userBalanceBeforeBooking;

    public Booking(User user, Room room, LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Check-in and check-out dates cannot be null");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        this.bookingId = nextBookingId++;
        this.userId = user.getUserId();
        this.roomNumber = room.getRoomNumber();
        this.checkIn = checkIn;
        this.checkOut = checkOut;

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        this.totalAmount = (int) (nights * room.getPricePerNight());

        // Store room and user information at booking time
        this.roomTypeAtBooking = room.getRoomType();
        this.roomPriceAtBooking = room.getPricePerNight();
        this.userBalanceBeforeBooking = user.getBalance();

        this.status = BookingStatus.CONFIRMED;
        this.createdAt = LocalDateTime.now();
    }

    public int getBookingId() {
        return bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public RoomType getRoomTypeAtBooking() {
        return roomTypeAtBooking;
    }

    public int getRoomPriceAtBooking() {
        return roomPriceAtBooking;
    }

    public int getUserBalanceBeforeBooking() {
        return userBalanceBeforeBooking;
    }

    public boolean hasDateConflict(LocalDate otherCheckIn, LocalDate otherCheckOut) {
        return !checkOut.isBefore(otherCheckIn) && !otherCheckOut.isBefore(checkIn);
    }

    public String toString() {
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        return String.format(
                "Booking{id=%d, userId=%d, roomNumber=%d, checkIn=%s, checkOut=%s, nights=%d, " +
                        "roomType=%s, pricePerNight=%d, totalAmount=%d, userBalanceBefore=%d, status=%s, createdAt=%s}",
                bookingId, userId, roomNumber, checkIn, checkOut, nights,
                roomTypeAtBooking.getDisplayName(), roomPriceAtBooking, totalAmount,
                userBalanceBeforeBooking, status, createdAt
        );
    }
}
