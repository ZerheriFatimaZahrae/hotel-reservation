package zerheri.fatimazahrae.services;
/**
 * Factory pour créer des instances du service de réservation
  * Cette factory permet de centraliser la création des services
 * et facilite les tests unitaires avec des mocks
 */
public class HotelReservationServiceFactory {
    private static HotelReservationService instance;

    /**
     * Crée une nouvelle instance du service
     *
     * @return Nouvelle instance de HotelReservationService
     */
    public static HotelReservationService createService() {
        return new HotelReservationServiceImpl();
    }

    /**
     * Retourne une instance singleton du service (pour les tests)
     *
     * @return Instance singleton de HotelReservationService
     */
    public static HotelReservationService getInstance() {
        if (instance == null) {
            instance = new HotelReservationServiceImpl();
        }
        return instance;
    }

    /**
     * Permet d'injecter une instance personnalisée (pour les tests)
     *
     * @param service Instance personnalisée du service
     */
    public static void setInstance(HotelReservationService service) {
        instance = service;
    }

    /**
     * Remet à zéro l'instance singleton
     */
    public static void resetInstance() {
        instance = null;
    }
}
