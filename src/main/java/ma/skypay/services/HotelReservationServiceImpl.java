package ma.skypay.services;

import ma.skypay.enums.BookingStatus;
import ma.skypay.enums.RoomType;
import ma.skypay.exceptions.BookingException;
import ma.skypay.models.Booking;
import ma.skypay.models.Room;
import ma.skypay.models.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implémentation concrète du service de réservation d'hôtel
 *
 * Cette classe gère la logique métier complète du système de réservation,
 * incluant la gestion des chambres, utilisateurs et réservations
 */
public class HotelReservationServiceImpl implements HotelReservationService {
    private static final Logger logger = Logger.getLogger(HotelReservationServiceImpl.class.getName());
    private final ArrayList<Room> rooms;
    private final ArrayList<User> users;
    private final ArrayList<Booking> bookings;

    public HotelReservationServiceImpl() {
        this.rooms = new ArrayList<>();
        this.users = new ArrayList<>();
        this.bookings = new ArrayList<>();

        logger.info("HotelReservationService initialized successfully");
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void setRoom(int roomNumber, RoomType roomType, int roomPricePerNight) {
        try {
            Optional<Room> existingRoom = findRoomByNumber(roomNumber);

            if (existingRoom.isPresent()) {
                // Update existing room without affecting previous bookings
                updateExistingRoom(existingRoom.get(), roomType, roomPricePerNight);
                logger.info(String.format("Room %d updated successfully", roomNumber));
            } else {
                createNewRoom(roomNumber, roomType, roomPricePerNight);
                logger.info(String.format("Room %d created successfully", roomNumber));
            }

        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Invalid parameters for setRoom", e);
            System.err.println("Error setting room: " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error in setRoom", e);
            System.err.println("Unexpected error setting room: " + e.getMessage());
        }

    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void setUser(int userId, int balance) {
        try {
            Optional<User> existingUser = findUserById(userId);

            if (existingUser.isPresent()) {
                updateExistingUser(existingUser.get(), balance);
                logger.info(String.format("User %d updated successfully", userId));
            } else {
                createNewUser(userId, balance);
                logger.info(String.format("User %d created successfully", userId));
            }

        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Invalid parameters for setUser", e);
            System.err.println("Error setting user: " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error in setUser", e);
            System.err.println("Unexpected error setting user: " + e.getMessage());
        }

    }

    @Override
    public void bookRoom(int userId, int roomNumber, Date checkIn, Date checkOut) {

        try {
            validateBookRoomParameters(userId, roomNumber, checkIn, checkOut);

            LocalDate checkInDate = convertToLocalDate(checkIn);
            LocalDate checkOutDate = convertToLocalDate(checkOut);

            validateBookingDates(checkInDate, checkOutDate);

            User user = findUserById(userId)
                    .orElseThrow(() -> new BookingException("User not found: " + userId));

            Room room = findRoomByNumber(roomNumber)
                    .orElseThrow(() -> new BookingException("Room not found: " + roomNumber));

            validateRoomAvailability(roomNumber, checkInDate, checkOutDate);

            processBooking(user, room, checkInDate, checkOutDate);

            logger.info(String.format("Booking successful: User %d, Room %d", userId, roomNumber));

        } catch (BookingException e) {
            logger.log(Level.WARNING, "Booking failed", e);
            System.err.println("Booking failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Invalid booking parameters", e);
            System.err.println("Invalid booking parameters: " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during booking", e);
            System.err.println("Unexpected error during booking: " + e.getMessage());
        }
    }

    @Override
    public void printAll() {
        System.out.println("\n=== ALL ROOMS AND BOOKINGS ===");

        printAllRooms();
        printAllBookings();

        System.out.println("================================\n");

    }

    @Override
    public void printAllUsers() {
        System.out.println("\n=== ALL USERS ===");

        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            users.stream()
                    .sorted(Comparator.comparing(User::getCreatedAt).reversed())
                    .forEach(System.out::println);
        }

        System.out.println("==================\n");

    }

    @Override
    public int getTotalRoomsCount() {
        return rooms.size();
    }

    @Override
    public int getTotalUsersCount() {
        return users.size();
    }

    @Override
    public int getTotalBookingsCount() {
        return bookings.size();
    }






    // ===============================
    // MÉTHODES PRIVÉES - BUSINESS LOGIC
    // ===============================

    private void updateExistingRoom(Room room, RoomType roomType, int roomPricePerNight) {
        room.setRoomType(roomType);
        room.setPricePerNight(roomPricePerNight);
        System.out.printf("Room %d updated: type=%s, price=%d/night%n",
                room.getRoomNumber(), roomType.getDisplayName(), roomPricePerNight);
    }

    private void createNewRoom(int roomNumber, RoomType roomType, int roomPricePerNight) {
        Room newRoom = new Room(roomNumber, roomType, roomPricePerNight);
        rooms.add(newRoom);
        System.out.printf("Room %d created: type=%s, price=%d/night%n",
                roomNumber, roomType.getDisplayName(), roomPricePerNight);
    }

    private void updateExistingUser(User user, int balance) {
        user.setBalance(balance);
        System.out.printf("User %d balance updated to %d%n", user.getUserId(), balance);
    }

    private void createNewUser(int userId, int balance) {
        User newUser = new User(userId, balance);
        users.add(newUser);
        System.out.printf("User %d created with balance %d%n", userId, balance);
    }


    private void processBooking(User user, Room room, LocalDate checkInDate, LocalDate checkOutDate)
            throws BookingException {
        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        int totalCost = (int) (nights * room.getPricePerNight());

        if (!user.canAfford(totalCost)) {
            throw new BookingException(
                    String.format("Insufficient balance. Required: %d, Available: %d",
                            totalCost, user.getBalance()));
        }

        // Créer la réservation et déduire le solde
        Booking booking = new Booking(user, room, checkInDate, checkOutDate);
        user.deductBalance(totalCost);
        bookings.add(booking);

        System.out.printf("Booking successful: User %d booked Room %d for %d nights. " +
                        "Total cost: %d. Remaining balance: %d%n",
                user.getUserId(), room.getRoomNumber(), nights, totalCost, user.getBalance());
    }


    // ===============================
    // MÉTHODES PRIVÉES - VALIDATION
    // ===============================

    private void validateBookRoomParameters(int userId, int roomNumber, Date checkIn, Date checkOut) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        if (roomNumber <= 0) {
            throw new IllegalArgumentException("Room number must be positive");
        }
        if (checkIn == null) {
            throw new IllegalArgumentException("Check-in date cannot be null");
        }
        if (checkOut == null) {
            throw new IllegalArgumentException("Check-out date cannot be null");
        }
    }

    private void validateBookingDates(LocalDate checkIn, LocalDate checkOut) throws BookingException {
        if (!checkOut.isAfter(checkIn)) {
            throw new BookingException("Check-out date must be after check-in date");
        }
    }

    private void validateRoomAvailability(int roomNumber, LocalDate checkIn, LocalDate checkOut)
            throws BookingException {
        boolean hasConflict = bookings.stream()
                .filter(booking -> booking.getRoomNumber() == roomNumber)
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
                .anyMatch(booking -> booking.hasDateConflict(checkIn, checkOut));

        if (hasConflict) {
            throw new BookingException(
                    String.format("Room %d is not available for the specified period", roomNumber));
        }
    }


    // ===============================
    // MÉTHODES PRIVÉES - AFFICHAGE
    // ===============================
    private void printAllRooms() {
        System.out.println("\n--- ROOMS (Latest to Oldest) ---");
        if (rooms.isEmpty()) {
            System.out.println("No rooms found.");
        } else {
            rooms.stream()
                    .sorted(Comparator.comparing(Room::getCreatedAt).reversed())
                    .forEach(System.out::println);
        }
    }

    private void printAllBookings() {
        System.out.println("\n--- BOOKINGS (Latest to Oldest) ---");
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
        } else {
            bookings.stream()
                    .sorted(Comparator.comparing(Booking::getCreatedAt).reversed())
                    .forEach(System.out::println);
        }
    }
    // ===============================
    // MÉTHODES PRIVÉES - UTILITIES
    // ===============================

    private Optional<Room> findRoomByNumber(int roomNumber) {
        return rooms.stream()
                .filter(room -> room.getRoomNumber() == roomNumber)
                .findFirst();
    }

    private Optional<User> findUserById(int userId) {
        return users.stream()
                .filter(user -> user.getUserId() == userId)
                .findFirst();
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }



}
