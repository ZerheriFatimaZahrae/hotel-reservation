package zerheri.fatimazahrae.services;

import zerheri.fatimazahrae.enums.RoomType;
import zerheri.fatimazahrae.exceptions.BookingException;

import java.util.Date;

/**
 * Interface définissant les opérations principales du système de réservation d'hôtel
 *
 * Cette interface sépare le contrat de service de son implémentation,
 * permettant une meilleure testabilité et flexibilité
 */
public interface HotelReservationService {

    /**
     * Crée ou met à jour une chambre
     *
     * @param roomNumber Numéro de la chambre
     * @param roomType Type de chambre (standard, junior suite, master suite)
     * @param roomPricePerNight Prix par nuit
     * @throws IllegalArgumentException si les paramètres sont invalides
     */
    void setRoom(int roomNumber, RoomType roomType, int roomPricePerNight);

    /**
     * Crée ou met à jour un utilisateur
     *
     * @param userId Identifiant de l'utilisateur
     * @param balance Solde du compte utilisateur
     * @throws IllegalArgumentException si les paramètres sont invalides
     */
    void setUser(int userId, int balance);

    /**
     * Effectue une réservation de chambre
     *
     * @param userId Identifiant de l'utilisateur
     * @param roomNumber Numéro de la chambre à réserver
     * @param checkIn Date d'arrivée
     * @param checkOut Date de départ
     * @throws BookingException si la réservation ne peut pas être effectuée
     */
    void bookRoom(int userId, int roomNumber, Date checkIn, Date checkOut);

    /**
     * Affiche toutes les chambres et réservations
     * Ordre : du plus récent au plus ancien
     */
    void printAll();

    /**
     * Affiche tous les utilisateurs
     * Ordre : du plus récent au plus ancien
     */
    void printAllUsers();

    /**
     * Retourne le nombre total de chambres dans le système
     *
     * @return Nombre de chambres
     */
    int getTotalRoomsCount();

    /**
     * Retourne le nombre total d'utilisateurs dans le système
     *
     * @return Nombre d'utilisateurs
     */
    int getTotalUsersCount();

    /**
     * Retourne le nombre total de réservations dans le système
     *
     * @return Nombre de réservations
     */
    int getTotalBookingsCount();
}
