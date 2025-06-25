package ma.skypay;

import ma.skypay.enums.RoomType;
import ma.skypay.services.HotelReservationService;
import ma.skypay.services.HotelReservationServiceFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        // Utilisation de l'interface avec factory pattern
        HotelReservationService hotelService = HotelReservationServiceFactory.createService();
        try {
            System.out.println("=== HOTEL RESERVATION SYSTEM TEST ===\n");

            // Create 3 rooms
            System.out.println("Creating rooms...");
            hotelService.setRoom(1, RoomType.STANDARD, 1000);
            hotelService.setRoom(2, RoomType.JUNIOR_SUITE, 2000);
            hotelService.setRoom(3, RoomType.MASTER_SUITE, 3000);

            // Create 2 users
            System.out.println("\nCreating users...");
            hotelService.setUser(1, 5000);
            hotelService.setUser(2, 10000);

            // Create dates for testing
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date checkIn1 = sdf.parse("30/06/2026");
            Date checkOut1 = sdf.parse("07/07/2026");
            Date checkIn2 = sdf.parse("07/07/2026");
            Date checkOut2 = sdf.parse("30/06/2026"); // Invalid: before check-in
            Date checkIn3 = sdf.parse("07/07/2026");
            Date checkOut3 = sdf.parse("08/07/2026");
            Date checkIn4 = sdf.parse("07/07/2026");
            Date checkOut4 = sdf.parse("09/07/2026");

            // Test bookings
            System.out.println("\n=== BOOKING TESTS ===");

            System.out.println("\n1. User 1 tries booking Room 2 from 30/06/2026 to 07/07/2026 (7 nights):");
            hotelService.bookRoom(1, 2, checkIn1, checkOut1);

            System.out.println("\n2. User 1 tries booking Room 2 from 07/07/2026 to 30/06/2026 (invalid dates):");
            hotelService.bookRoom(1, 2, checkIn2, checkOut2);

            System.out.println("\n3. User 1 tries booking Room 1 from 07/07/2026 to 08/07/2026 (1 night):");
            hotelService.bookRoom(1, 1, checkIn3, checkOut3);

            System.out.println("\n4. User 2 tries booking Room 1 from 07/07/2026 to 09/07/2026 (2 nights):");
            hotelService.bookRoom(2, 1, checkIn4, checkOut4);

            System.out.println("\n5. User 2 tries booking Room 3 from 07/07/2026 to 08/07/2026 (1 night):");
            hotelService.bookRoom(2, 3, checkIn3, checkOut3);

            System.out.println("\n6. Updating Room 1 to suite type with price 10000:");
            hotelService.setRoom(1, RoomType.MASTER_SUITE, 10000);

            // Print final results
            System.out.println("\n=== FINAL RESULTS ===");
            hotelService.printAll();
            hotelService.printAllUsers();

        } catch (Exception e) {
            System.err.println("Error in main: " + e.getMessage());
            e.printStackTrace();
        }

    }
}