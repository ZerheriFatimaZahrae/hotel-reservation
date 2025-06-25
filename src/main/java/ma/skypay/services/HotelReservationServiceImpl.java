package ma.skypay.services;

import ma.skypay.enums.RoomType;
import ma.skypay.models.Booking;
import ma.skypay.models.Room;
import ma.skypay.models.User;

import java.util.ArrayList;
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

    }

    @Override
    public void printAll() {

    }

    @Override
    public void printAllUsers() {

    }

    @Override
    public int getTotalRoomsCount() {
        return 0;
    }

    @Override
    public int getTotalUsersCount() {
        return 0;
    }

    @Override
    public int getTotalBookingsCount() {
        return 0;
    }

    // Helper methods
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
}
