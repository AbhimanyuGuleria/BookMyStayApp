import java.util.HashMap;
import java.util.Map;

/**
 * UseCase3InventorySetup - Introduces centralized inventory management using
 * a HashMap to replace scattered availability variables from Use Case 2.
 *
 * @author BookMyStay Team
 * @version 3.1
 */
public class UseCase3InventorySetup {

    /**
     * RoomInventory - Centralized manager for room availability.
     * Encapsulates all inventory operations and provides a single source of truth.
     */
    static class RoomInventory {
        private HashMap<String, Integer> inventory;

        /**
         * Constructs RoomInventory and initializes default room counts.
         */
        public RoomInventory() {
            inventory = new HashMap<>();
            inventory.put("Single Room", 5);
            inventory.put("Double Room", 3);
            inventory.put("Suite Room",  2);
        }

        /**
         * Returns the number of available rooms for a given room type.
         *
         * @param roomType the room type to query
         * @return number of available rooms, or 0 if type not found
         */
        public int getAvailability(String roomType) {
            return inventory.getOrDefault(roomType, 0);
        }

        /**
         * Updates the availability of a room type by a given delta.
         * Positive delta increases availability; negative decreases it.
         *
         * @param roomType the room type to update
         * @param delta    the change in availability count
         */
        public void updateAvailability(String roomType, int delta) {
            int current = inventory.getOrDefault(roomType, 0);
            inventory.put(roomType, current + delta);
        }

        /**
         * Displays the full inventory state to the console.
         */
        public void displayInventory() {
            System.out.println("  Current Room Inventory:");
            for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                System.out.printf("  %-15s : %d available%n", entry.getKey(), entry.getValue());
            }
        }

        /**
         * Returns the full inventory map (read-only usage intended).
         *
         * @return the inventory HashMap
         */
        public HashMap<String, Integer> getInventory() {
            return inventory;
        }
    }

    /**
     * Main method - demonstrates centralized inventory initialization and management.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Book My Stay App - Inventory Setup");
        System.out.println("   Hotel Booking System v3.1");
        System.out.println("========================================");

        RoomInventory inventory = new RoomInventory();

        System.out.println("Initial Inventory:");
        inventory.displayInventory();

        System.out.println("----------------------------------------");
        System.out.println("Simulating a booking: removing 1 Double Room...");
        inventory.updateAvailability("Double Room", -1);

        System.out.println("Updated Inventory:");
        inventory.displayInventory();

        System.out.println("----------------------------------------");
        System.out.println("Availability check for 'Suite Room': "
                + inventory.getAvailability("Suite Room") + " room(s) available.");

        System.out.println("========================================");
        System.out.println("Inventory setup complete.");
    }
}
