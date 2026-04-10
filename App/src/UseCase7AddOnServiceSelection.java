import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UseCase7AddOnServiceSelection - Extends the booking model to support optional
 * add-on services using a Map-List composition, without modifying core booking logic.
 *
 * @author BookMyStay Team
 * @version 7.0
 */
public class UseCase7AddOnServiceSelection {

    /**
     * Represents an optional add-on service available to guests.
     */
    static class AddOnService {
        private String serviceName;
        private double cost;

        /**
         * Constructs an AddOnService with the specified name and cost.
         *
         * @param serviceName the name of the service
         * @param cost        the price of the service in USD
         */
        public AddOnService(String serviceName, double cost) {
            this.serviceName = serviceName;
            this.cost = cost;
        }

        public String getServiceName() { return serviceName; }
        public double getCost()        { return cost; }

        @Override
        public String toString() {
            return serviceName + " ($" + String.format("%.2f", cost) + ")";
        }
    }

    /**
     * AddOnServiceManager - Manages the association between reservations and
     * their selected optional services. Core booking state is not modified here.
     */
    static class AddOnServiceManager {
        private Map<String, List<AddOnService>> reservationServices;

        /**
         * Initializes the service manager with an empty mapping.
         */
        public AddOnServiceManager() {
            reservationServices = new HashMap<>();
        }

        /**
         * Attaches an add-on service to a reservation.
         *
         * @param reservationId the ID of the reservation
         * @param service       the add-on service to attach
         */
        public void addService(String reservationId, AddOnService service) {
            reservationServices
                    .computeIfAbsent(reservationId, k -> new ArrayList<>())
                    .add(service);
            System.out.println("  Added [" + service + "] to reservation: " + reservationId);
        }

        /**
         * Calculates the total cost of all add-on services for a reservation.
         *
         * @param reservationId the ID of the reservation
         * @return total add-on cost in USD
         */
        public double calculateTotalAddOnCost(String reservationId) {
            List<AddOnService> services = reservationServices.get(reservationId);
            if (services == null) return 0.0;
            double total = 0.0;
            for (AddOnService s : services) {
                total += s.getCost();
            }
            return total;
        }

        /**
         * Displays all add-on services selected for a reservation.
         *
         * @param reservationId the ID of the reservation to display
         */
        public void displayServicesForReservation(String reservationId) {
            List<AddOnService> services = reservationServices.get(reservationId);
            System.out.println("  Services for Reservation [" + reservationId + "]:");
            if (services == null || services.isEmpty()) {
                System.out.println("    No add-on services selected.");
            } else {
                for (AddOnService s : services) {
                    System.out.println("    - " + s);
                }
                System.out.printf("    Total Add-On Cost: $%.2f%n", calculateTotalAddOnCost(reservationId));
            }
        }

        /**
         * Displays a summary of all reservations and their services.
         */
        public void displayAllServices() {
            System.out.println("  Full Add-On Service Summary:");
            if (reservationServices.isEmpty()) {
                System.out.println("    No add-on services recorded.");
                return;
            }
            for (Map.Entry<String, List<AddOnService>> entry : reservationServices.entrySet()) {
                System.out.println("    Reservation: " + entry.getKey());
                for (AddOnService s : entry.getValue()) {
                    System.out.println("      - " + s);
                }
                System.out.printf("      Subtotal: $%.2f%n", calculateTotalAddOnCost(entry.getKey()));
            }
        }
    }

    /**
     * Main method - simulates guests selecting add-on services for reservations.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Book My Stay App - Add-On Services");
        System.out.println("   Hotel Booking System v7.0");
        System.out.println("========================================");

        // Define available services
        AddOnService breakfast   = new AddOnService("Breakfast",          15.00);
        AddOnService airportPick = new AddOnService("Airport Pickup",     40.00);
        AddOnService spa         = new AddOnService("Spa Session",        60.00);
        AddOnService laundry     = new AddOnService("Laundry Service",    20.00);
        AddOnService roomService = new AddOnService("Room Service",       25.00);

        AddOnServiceManager manager = new AddOnServiceManager();

        System.out.println("Attaching services to reservations...");
        System.out.println("----------------------------------------");

        // Alice's reservation: S001
        manager.addService("S001", breakfast);
        manager.addService("S001", spa);
        manager.addService("S001", airportPick);

        // Bob's reservation: B002
        manager.addService("B002", laundry);
        manager.addService("B002", roomService);

        System.out.println("----------------------------------------");
        manager.displayServicesForReservation("S001");
        System.out.println();
        manager.displayServicesForReservation("B002");

        System.out.println("----------------------------------------");
        manager.displayAllServices();
        System.out.println("========================================");
        System.out.println("Add-on selection complete. Core booking state unchanged.");
    }
}
