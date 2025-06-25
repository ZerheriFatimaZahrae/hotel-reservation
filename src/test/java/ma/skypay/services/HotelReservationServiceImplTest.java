package ma.skypay.services;

import ma.skypay.enums.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaires pour HotelReservationServiceImpl
 * Utilise le pattern Given-When-Then pour une meilleure lisibilité
 */
@DisplayName("Hotel Reservation Service Tests")
class HotelReservationServiceImplTest {

    private HotelReservationService hotelService;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        // Given: Une nouvelle instance du service pour chaque test
        hotelService = new HotelReservationServiceImpl();

        // Configuration pour capturer les sorties console
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Nested
    @DisplayName("Room Management Tests")
    class RoomManagementTests {

        @Test
        @DisplayName("Should create new room when room number doesn't exist")
        void shouldCreateNewRoom() {
            // Given
            var roomNumber = 101;
            var roomType = RoomType.STANDARD;
            var pricePerNight = 1000;

            // When
            hotelService.setRoom(roomNumber, roomType, pricePerNight);

            // Then
            assertThat(hotelService.getTotalRoomsCount()).isEqualTo(1);
            var output = outputStream.toString();
            assertThat(output).contains("Room 101 created: type=standard, price=1000/night");
        }

        @Test
        @DisplayName("Should update existing room when room number already exists")
        void shouldUpdateExistingRoom() {
            // Given: Une chambre existante
            var roomNumber = 102;
            hotelService.setRoom(roomNumber, RoomType.STANDARD, 1000);
            outputStream.reset(); // Reset pour ne capturer que la mise à jour

            // When: Mise à jour de la chambre
            hotelService.setRoom(roomNumber, RoomType.MASTER_SUITE, 5000);

            // Then
            assertThat(hotelService.getTotalRoomsCount()).isEqualTo(1);
            var output = outputStream.toString();
            assertThat(output).contains("Room 102 updated: type=suite, price=5000/night");
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -100})
        @DisplayName("Should handle invalid room numbers gracefully")
        void shouldHandleInvalidRoomNumbers(int invalidRoomNumber) {
            // Given: Un numéro de chambre invalide
            var roomType = RoomType.STANDARD;
            var pricePerNight = 1000;

            // When & Then: Aucune exception ne devrait être lancée
            assertDoesNotThrow(() -> hotelService.setRoom(invalidRoomNumber, roomType, pricePerNight));

            // Vérifier qu'aucune chambre n'a été créée avec un numéro invalide
            assertThat(hotelService.getTotalRoomsCount()).isZero();
        }

        @Test
        @DisplayName("Should handle null room type gracefully")
        void shouldHandleNullRoomType() {
            // Given
            var roomNumber = 103;
            RoomType nullRoomType = null;
            var pricePerNight = 1000;

            // When & Then
            assertDoesNotThrow(() -> hotelService.setRoom(roomNumber, nullRoomType, pricePerNight));

            // Vérifier qu'aucune chambre n'a été créée avec un type null
            assertThat(hotelService.getTotalRoomsCount()).isZero();
        }
    }

    @Nested
    @DisplayName("User Management Tests")
    class UserManagementTests {

        @Test
        @DisplayName("Should create new user when user ID doesn't exist")
        void shouldCreateNewUser() {
            // Given
            var userId = 1;
            var balance = 5000;

            // When
            hotelService.setUser(userId, balance);

            // Then
            assertThat(hotelService.getTotalUsersCount()).isEqualTo(1);
            var output = outputStream.toString();
            assertThat(output).contains("User 1 created with balance 5000");
        }

        @Test
        @DisplayName("Should update existing user when user ID already exists")
        void shouldUpdateExistingUser() {
            // Given: Un utilisateur existant
            var userId = 2;
            hotelService.setUser(userId, 3000);
            outputStream.reset();

            // When: Mise à jour du solde
            hotelService.setUser(userId, 8000);

            // Then
            assertThat(hotelService.getTotalUsersCount()).isEqualTo(1);
            var output = outputStream.toString();
            assertThat(output).contains("User 2 balance updated to 8000");
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -500})
        @DisplayName("Should handle invalid user IDs gracefully")
        void shouldHandleInvalidUserIds(int invalidUserId) {
            // Given
            var balance = 1000;

            // When & Then
            assertDoesNotThrow(() -> hotelService.setUser(invalidUserId, balance));

            // Vérifier qu'aucun utilisateur n'a été créé avec un ID invalide
            assertThat(hotelService.getTotalUsersCount()).isZero();
        }

        @Test
        @DisplayName("Should handle negative balance")
        void shouldHandleNegativeBalance() {
            // Given
            var userId = 3;
            var negativeBalance = -1000;

            // When & Then: Le service devrait accepter les soldes négatifs
            assertDoesNotThrow(() -> hotelService.setUser(userId, negativeBalance));
            assertThat(hotelService.getTotalUsersCount()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Booking Tests")
    class BookingTests {

        @BeforeEach
        void setUpBookingTest() {
            // Given: Configuration de base pour les tests de réservation
            hotelService.setRoom(101, RoomType.STANDARD, 1000);
            hotelService.setRoom(102, RoomType.JUNIOR_SUITE, 2000);
            hotelService.setUser(1, 10000);
            hotelService.setUser(2, 3000);
            outputStream.reset(); // Reset pour ne capturer que les réservations
        }

        @Test
        @DisplayName("Should successfully book available room with sufficient balance")
        void shouldSuccessfullyBookRoom() {
            // Given
            var userId = 1;
            var roomNumber = 101;
            var checkInDate = createDate(2026, 6, 15);
            var checkOutDate = createDate(2026, 6, 18); // 3 nuits

            // When
            hotelService.bookRoom(userId, roomNumber, checkInDate, checkOutDate);

            // Then
            assertThat(hotelService.getTotalBookingsCount()).isEqualTo(1);
            var output = outputStream.toString();
            assertThat(output)
                    .contains("Booking successful")
                    .contains("User 1 booked Room 101 for 3 nights")
                    .contains("Total cost: 3000")
                    .contains("Remaining balance: 7000");
        }

        @Test
        @DisplayName("Should fail booking when user has insufficient balance")
        void shouldFailBookingWithInsufficientBalance() {
            // Given: Utilisateur avec solde insuffisant
            var userId = 2; // Solde: 3000
            var roomNumber = 102; // Prix: 2000/nuit
            var checkInDate = createDate(2026, 7, 1);
            var checkOutDate = createDate(2026, 7, 3); // 2 nuits = 4000 total

            // When
            hotelService.bookRoom(userId, roomNumber, checkInDate, checkOutDate);

            // Then: Aucune réservation ne devrait être créée
            assertThat(hotelService.getTotalBookingsCount()).isZero();
        }

        @Test
        @DisplayName("Should fail booking when user doesn't exist")
        void shouldFailBookingWithNonExistentUser() {
            // Given
            var nonExistentUserId = 999;
            var roomNumber = 101;
            var checkInDate = createDate(2026, 8, 1);
            var checkOutDate = createDate(2026, 8, 2);

            // When
            hotelService.bookRoom(nonExistentUserId, roomNumber, checkInDate, checkOutDate);

            // Then: Aucune réservation ne devrait être créée
            assertThat(hotelService.getTotalBookingsCount()).isZero();
        }

        @Test
        @DisplayName("Should fail booking when room doesn't exist")
        void shouldFailBookingWithNonExistentRoom() {
            // Given
            var userId = 1;
            var nonExistentRoomNumber = 999;
            var checkInDate = createDate(2026, 8, 1);
            var checkOutDate = createDate(2026, 8, 2);

            // When
            hotelService.bookRoom(userId, nonExistentRoomNumber, checkInDate, checkOutDate);

            // Then: Aucune réservation ne devrait être créée
            assertThat(hotelService.getTotalBookingsCount()).isZero();
        }

        @Test
        @DisplayName("Should fail booking when check-out date is before check-in date")
        void shouldFailBookingWithInvalidDates() {
            // Given
            var userId = 1;
            var roomNumber = 101;
            var checkInDate = createDate(2026, 8, 5);
            var checkOutDate = createDate(2026, 8, 2); // Date antérieure

            // When
            hotelService.bookRoom(userId, roomNumber, checkInDate, checkOutDate);

            // Then: Aucune réservation ne devrait être créée
            assertThat(hotelService.getTotalBookingsCount()).isZero();
        }

        @Test
        @DisplayName("Should fail booking when room is already booked for overlapping dates")
        void shouldFailBookingWithRoomConflict() {
            // Given: Première réservation réussie
            var userId1 = 1;
            var roomNumber = 101;
            var firstCheckIn = createDate(2026, 9, 1);
            var firstCheckOut = createDate(2026, 9, 5);
            hotelService.bookRoom(userId1, roomNumber, firstCheckIn, firstCheckOut);
            outputStream.reset();

            // When: Tentative de réservation avec conflit de dates
            var userId2 = 2;
            var conflictCheckIn = createDate(2026, 9, 3); // Chevauchement
            var conflictCheckOut = createDate(2026, 9, 7);
            hotelService.bookRoom(userId2, roomNumber, conflictCheckIn, conflictCheckOut);

            // Then: Seule la première réservation devrait exister
            assertThat(hotelService.getTotalBookingsCount()).isEqualTo(1);
        }

        @ParameterizedTest
        @MethodSource("provideInvalidBookingParameters")
        @DisplayName("Should handle invalid booking parameters gracefully")
        void shouldHandleInvalidBookingParameters(int userId, int roomNumber, Date checkIn, Date checkOut, String expectedError) {
            // When
            hotelService.bookRoom(userId, roomNumber, checkIn, checkOut);

            // Then: Aucune réservation ne devrait être créée
            assertThat(hotelService.getTotalBookingsCount()).isZero();
        }

        static Stream<Arguments> provideInvalidBookingParameters() {
            var validDate = createDate(2026, 10, 1);
            return Stream.of(
                    Arguments.of(0, 101, validDate, validDate, "User ID must be positive"),
                    Arguments.of(-1, 101, validDate, validDate, "User ID must be positive"),
                    Arguments.of(1, 0, validDate, validDate, "Room number must be positive"),
                    Arguments.of(1, -1, validDate, validDate, "Room number must be positive"),
                    Arguments.of(1, 101, null, validDate, "Check-in date cannot be null"),
                    Arguments.of(1, 101, validDate, null, "Check-out date cannot be null")
            );
        }
    }

    @Nested
    @DisplayName("Display Methods Tests")
    class DisplayMethodsTests {

        @Test
        @DisplayName("Should display message when no data exists")
        void shouldDisplayEmptyStateMessages() {
            // When
            hotelService.printAll();
            hotelService.printAllUsers();

            // Then
            var output = outputStream.toString();
            assertThat(output)
                    .contains("No rooms found.")
                    .contains("No bookings found.")
                    .contains("No users found.");
        }

        @Test
        @DisplayName("Should display all entities when data exists")
        void shouldDisplayAllEntitiesWhenDataExists() {
            // Given: Créer des données de test
            hotelService.setRoom(101, RoomType.STANDARD, 1000);
            hotelService.setUser(1, 5000);
            var checkIn = createDate(2026, 11, 1);
            var checkOut = createDate(2026, 11, 3);
            hotelService.bookRoom(1, 101, checkIn, checkOut);
            outputStream.reset();

            // When
            hotelService.printAll();
            hotelService.printAllUsers();

            // Then
            var output = outputStream.toString();
            assertThat(output)
                    .contains("ALL ROOMS AND BOOKINGS")
                    .contains("ALL USERS")
                    .contains("ROOMS (Latest to Oldest)")
                    .contains("BOOKINGS (Latest to Oldest)");
        }
    }

    @Nested
    @DisplayName("Count Methods Tests")
    class CountMethodsTests {

        @Test
        @DisplayName("Should return correct counts for empty state")
        void shouldReturnZeroCountsForEmptyState() {
            // When & Then
            assertThat(hotelService.getTotalRoomsCount()).isZero();
            assertThat(hotelService.getTotalUsersCount()).isZero();
            assertThat(hotelService.getTotalBookingsCount()).isZero();
        }

        @Test
        @DisplayName("Should return correct counts after adding entities")
        void shouldReturnCorrectCountsAfterAddingEntities() {
            // Given
            hotelService.setRoom(101, RoomType.STANDARD, 1000);
            hotelService.setRoom(102, RoomType.JUNIOR_SUITE, 2000);
            hotelService.setUser(1, 5000);
            hotelService.setUser(2, 3000);
            hotelService.setUser(3, 8000);

            var checkIn = createDate(2026, 12, 1);
            var checkOut = createDate(2026, 12, 3);
            hotelService.bookRoom(1, 101, checkIn, checkOut);

            // When & Then
            assertThat(hotelService.getTotalRoomsCount()).isEqualTo(2);
            assertThat(hotelService.getTotalUsersCount()).isEqualTo(3);
            assertThat(hotelService.getTotalBookingsCount()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should handle complete booking workflow successfully")
        void shouldHandleCompleteBookingWorkflow() {
            // Given: Scénario complet similaire au Main
            hotelService.setRoom(1, RoomType.STANDARD, 1000);
            hotelService.setRoom(2, RoomType.JUNIOR_SUITE, 2000);
            hotelService.setRoom(3, RoomType.MASTER_SUITE, 3000);

            hotelService.setUser(1, 5000);
            hotelService.setUser(2, 10000);

            var checkIn1 = createDate(2026, 6, 30);
            var checkOut1 = createDate(2026, 7, 7); // 7 nuits
            var checkIn2 = createDate(2026, 7, 7);
            var checkOut2 = createDate(2026, 7, 8); // 1 nuit

            // When: Réservations multiples
            hotelService.bookRoom(1, 2, checkIn1, checkOut1); // 7 * 2000 = 14000 > 5000 (échec)
            hotelService.bookRoom(1, 1, checkIn2, checkOut2); // 1 * 1000 = 1000 < 5000 (succès)
            hotelService.bookRoom(2, 3, checkIn2, checkOut2); // 1 * 3000 = 3000 < 10000 (succès)

            // Then
            assertThat(hotelService.getTotalRoomsCount()).isEqualTo(3);
            assertThat(hotelService.getTotalUsersCount()).isEqualTo(2);
            assertThat(hotelService.getTotalBookingsCount()).isEqualTo(2); // 2 réservations réussies
        }
    }

    // ===============================
    // MÉTHODES UTILITAIRES
    // ===============================

    private static Date createDate(int year, int month, int day) {
        return Date.from(LocalDate.of(year, month, day)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
    }

    private String getErrorOutput() {
        // Pour les tests, on va intercepter les erreurs via System.err
        // mais d'abord essayons de capturer ce qui est dans outputStream
        return outputStream.toString();
    }

    // Nettoyage après chaque test
    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (Exception e) {
                // Ignorer les erreurs de fermeture
            }
        }
    }
}