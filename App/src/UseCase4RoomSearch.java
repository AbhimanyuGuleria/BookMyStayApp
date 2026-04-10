import java.util.HashMap;
import java.util.Map;

/**
 * UseCase4RoomSearch - Enables read-only room availability searches,
 * enforcing separation between search and booking operations.
 *
 * @author BookMyStay Team
 * @version 4.0
 */
public class UseCase4RoomSearch {

    /**
     * Represents a generic hotel room with pricing and description details.
     */
    static class Room {
        private String roomType;
        private double pricePerNight;
        private String description;

        public Room(String roomType, double pricePerNight, String description) {
            this.roomType = roomType;
            this.pricePerNight = pricePerNight;
            this.description = description;
        }

        public String getRoomType() { return roomType; }
        public double getPricePerNight() { return pricePerNight; }
        public String getDescription() { return description; }
    }

    /**
     * RoomInventory - Maintains centralized availability state.
     */
    static class RoomInventory {
        private HashMap<String, Integer> inventory;

        public RoomInventory() {
            inventory = new HashMap<>();
            inventory.put("Single Room", 5);
            inventory.put("Double Room", 0); // intentionally unavailable
            inventory.put("Suite Room",  2);
        }

        public int getAvailability(String roomType) {
            return inventory.getOrDefault(roomType, 0);
        }

        public HashMap<String, Integer> getInventory() {
            return inventory;
        }
    }

    /**
     * RoomSearchService - Provides read-only room search functionality.
     * Does not modify inventory state under any circumstances.
     */
    static class RoomSearchService {
        private RoomInventory inventory;
        private HashMap<String, Room> roomCatalog;

        /**
         * Constructs the search service with inventory and room catalog references.
         *
         * @param inventory   the centralized inventory instance
         * @param roomCatalog a map of room type to Room domain objects
         */
        public RoomSearchService(RoomInventory inventory, HashMap<String, Room> roomCatalog) {
            this.inventory = inventory;
            this.roomCatalog = roomCatalog;
        }

        /**
         * Searches and displays all room types with availability greater than zero.
         * Inventory state is not modified during this operation.
         */
        public void searchAvailableRooms() {
            System.out.println("  Available Rooms:");
            System.out.println("  ----------------------------------------");
            boolean anyAvailable = false;

            for (Map.Entry<String, Integer> entry : inventory.getInventory().entrySet()) {
                String type = entry.getKey();
                int count = entry.getValue();

                if (count > 0) {
                    anyAvailable = true;
                    Room room = roomCatalog.get(type);
                    if (room != null) {
                        System.out.printf("  %-15s | $%.2f/night | %s | %d left%n",
                                type, room.getPricePerNight(), room.getDescription(), count);
                    }
                }
            }

            if (!anyAvailable) {
                System.out.println("  No rooms are currently available.");
            }
        }
    }

    /**
     * Main method - demonstrates guest-initiated room search.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Book My Stay App - Room Search");
        System.out.println("   Hotel Booking System v4.0");
        System.out.println("========================================");

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();

        // Build room catalog (domain objects)
        HashMap<String, Room> roomCatalog = new HashMap<>();
        roomCatalog.put("Single Room", new Room("Single Room", 80.0,  "Cozy room for solo travelers"));
        roomCatalog.put("Double Room", new Room("Double Room", 130.0, "Spacious room for couples"));
        roomCatalog.put("Suite Room",  new Room("Suite Room",  250.0, "Luxury suite with full amenities"));

        // Perform read-only search
        RoomSearchService searchService = new RoomSearchService(inventory, roomCatalog);
        System.out.println("Guest initiates room search...");
        System.out.println();
        searchService.searchAvailableRooms();

        System.out.println("========================================");
        System.out.println("Search complete. Inventory unchanged.");
    }
}
