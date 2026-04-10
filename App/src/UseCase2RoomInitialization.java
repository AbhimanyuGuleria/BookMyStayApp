/**
 * UseCase2RoomInitialization - Demonstrates object modeling through abstraction
 * and inheritance by defining room types and their static availability.
 *
 * @author BookMyStay Team
 * @version 2.1
 */
public class UseCase2RoomInitialization {

    /**
     * Abstract base class representing a generic hotel room.
     * Cannot be instantiated directly; concrete room types must extend this class.
     */
    abstract static class Room {
        private String roomType;
        private int numberOfBeds;
        private double sizeInSqFt;
        private double pricePerNight;

        /**
         * Constructs a Room with the specified attributes.
         *
         * @param roomType       the type/name of the room
         * @param numberOfBeds   number of beds in the room
         * @param sizeInSqFt     room size in square feet
         * @param pricePerNight  nightly rate in USD
         */
        public Room(String roomType, int numberOfBeds, double sizeInSqFt, double pricePerNight) {
            this.roomType = roomType;
            this.numberOfBeds = numberOfBeds;
            this.sizeInSqFt = sizeInSqFt;
            this.pricePerNight = pricePerNight;
        }

        public String getRoomType() { return roomType; }
        public int getNumberOfBeds() { return numberOfBeds; }
        public double getSizeInSqFt() { return sizeInSqFt; }
        public double getPricePerNight() { return pricePerNight; }

        /**
         * Abstract method to display room-specific amenities.
         * Each subclass must provide its own implementation.
         */
        public abstract void displayAmenities();

        /**
         * Displays common room details shared across all room types.
         */
        public void displayDetails() {
            System.out.println("  Room Type     : " + roomType);
            System.out.println("  Beds          : " + numberOfBeds);
            System.out.println("  Size          : " + sizeInSqFt + " sq ft");
            System.out.println("  Price/Night   : $" + pricePerNight);
        }
    }

    /**
     * Represents a Single Room - ideal for solo travelers.
     */
    static class SingleRoom extends Room {
        public SingleRoom() {
            super("Single Room", 1, 200.0, 80.0);
        }

        @Override
        public void displayAmenities() {
            System.out.println("  Amenities     : WiFi, TV, Mini-fridge");
        }
    }

    /**
     * Represents a Double Room - suitable for couples or small families.
     */
    static class DoubleRoom extends Room {
        public DoubleRoom() {
            super("Double Room", 2, 350.0, 130.0);
        }

        @Override
        public void displayAmenities() {
            System.out.println("  Amenities     : WiFi, TV, Mini-fridge, Bathtub");
        }
    }

    /**
     * Represents a Suite Room - premium offering with luxury features.
     */
    static class SuiteRoom extends Room {
        public SuiteRoom() {
            super("Suite Room", 3, 600.0, 250.0);
        }

        @Override
        public void displayAmenities() {
            System.out.println("  Amenities     : WiFi, Smart TV, Mini-bar, Jacuzzi, Living Area");
        }
    }

    /**
     * Main method - initializes room objects and displays availability.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Book My Stay App - Room Inventory");
        System.out.println("   Hotel Booking System v2.1");
        System.out.println("========================================");

        // Create room type objects using polymorphism
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom  = new SuiteRoom();

        // Static availability stored in individual variables
        int singleRoomAvailable = 5;
        int doubleRoomAvailable = 3;
        int suiteRoomAvailable  = 2;

        // Display details for each room type
        Room[] rooms = { singleRoom, doubleRoom, suiteRoom };
        int[] availability = { singleRoomAvailable, doubleRoomAvailable, suiteRoomAvailable };

        for (int i = 0; i < rooms.length; i++) {
            System.out.println("----------------------------------------");
            rooms[i].displayDetails();
            rooms[i].displayAmenities();
            System.out.println("  Available     : " + availability[i] + " room(s)");
        }

        System.out.println("========================================");
        System.out.println("Room initialization complete.");
    }
}
