import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * UseCase6RoomAllocationService - Confirms bookings by assigning unique room IDs,
 * preventing double-booking using a Set, and maintaining inventory consistency.
 *
 * @author BookMyStay Team
 * @version 6.0
 */
public class UseCase6RoomAllocationService {

    /**
     * Represents a guest's booking request.
     */
    static class Reservation {
        private String guestName;
        private String roomType;
        private int nights;
        private String assignedRoomId;

        public Reservation(String guestName, String roomType, int nights) {
            this.guestName = guestName;
            this.roomType = roomType;
            this.nights = nights;
        }

        public String getGuestName()  { return guestName; }
        public String getRoomType()   { return roomType; }
        public int getNights()        { return nights; }
        public String getAssignedRoomId() { return assignedRoomId; }
        public void setAssignedRoomId(String id) { this.assignedRoomId = id; }

        @Override
        public String toString() {
            return "Reservation{guest='" + guestName + "', room='" + roomType
                    + "', nights=" + nights + ", roomId='" + assignedRoomId + "'}";
        }
    }

    /**
     * RoomInventory - Centralized inventory manager.
     */
    static class RoomInventory {
        private HashMap<String, Integer> inventory;

        public RoomInventory() {
            inventory = new HashMap<>();
            inventory.put("Single Room", 3);
            inventory.put("Double Room", 2);
            inventory.put("Suite Room",  1);
        }

        public int getAvailability(String roomType) {
            return inventory.getOrDefault(roomType, 0);
        }

        public void decrementAvailability(String roomType) {
            inventory.put(roomType, inventory.getOrDefault(roomType, 0) - 1);
        }

        public void displayInventory() {
            System.out.println("  Inventory State:");
            for (HashMap.Entry<String, Integer> e : inventory.entrySet()) {
                System.out.printf("    %-15s: %d%n", e.getKey(), e.getValue());
            }
        }
    }

    /**
     * RoomAllocationService - Processes queued booking requests, assigns unique
     * room IDs, and updates inventory atomically to prevent double-booking.
     */
    static class RoomAllocationService {
        private RoomInventory inventory;
        private Set<String> allocatedRoomIds;
        private HashMap<String, Set<String>> roomTypeAllocations;
        private HashMap<String, Integer> roomTypeCounters;

        /**
         * Constructs the allocation service with shared inventory.
         *
         * @param inventory the centralized room inventory
         */
        public RoomAllocationService(RoomInventory inventory) {
            this.inventory = inventory;
            this.allocatedRoomIds = new HashSet<>();
            this.roomTypeAllocations = new HashMap<>();
            this.roomTypeCounters = new HashMap<>();
        }

        /**
         * Processes a booking request: validates availability, generates a unique
         * room ID, updates inventory, and confirms the reservation.
         *
         * @param reservation the booking request to process
         * @return true if confirmed, false if unavailable
         */
        public boolean processBooking(Reservation reservation) {
            String roomType = reservation.getRoomType();

            if (inventory.getAvailability(roomType) <= 0) {
                System.out.println("  [DENIED]  No availability for: " + roomType
                        + " (Guest: " + reservation.getGuestName() + ")");
                return false;
            }

            // Generate unique room ID
            int counter = roomTypeCounters.getOrDefault(roomType, 0) + 1;
            roomTypeCounters.put(roomType, counter);
            String prefix = roomType.substring(0, 1).toUpperCase();
            String roomId = prefix + String.format("%03d", counter);

            // Ensure uniqueness via Set
            if (allocatedRoomIds.contains(roomId)) {
                System.out.println("  [ERROR]   Duplicate room ID detected: " + roomId);
                return false;
            }

            allocatedRoomIds.add(roomId);
            roomTypeAllocations.computeIfAbsent(roomType, k -> new HashSet<>()).add(roomId);

            reservation.setAssignedRoomId(roomId);
            inventory.decrementAvailability(roomType);

            System.out.println("  [CONFIRMED] " + reservation.getGuestName()
                    + " -> Room ID: " + roomId + " (" + roomType + ")");
            return true;
        }

        /**
         * Displays all allocated room IDs grouped by room type.
         */
        public void displayAllocations() {
            System.out.println("  Allocation Summary:");
            for (HashMap.Entry<String, Set<String>> entry : roomTypeAllocations.entrySet()) {
                System.out.println("    " + entry.getKey() + " -> " + entry.getValue());
            }
        }
    }

    /**
     * Main method - processes queued reservations with allocation and uniqueness enforcement.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Book My Stay App - Room Allocation");
        System.out.println("   Hotel Booking System v6.0");
        System.out.println("========================================");

        RoomInventory inventory = new RoomInventory();
        RoomAllocationService allocationService = new RoomAllocationService(inventory);

        // Build booking queue
        Queue<Reservation> queue = new LinkedList<>();
        queue.offer(new Reservation("Alice",   "Single Room", 2));
        queue.offer(new Reservation("Bob",     "Suite Room",  3));
        queue.offer(new Reservation("Charlie", "Single Room", 1));
        queue.offer(new Reservation("Diana",   "Suite Room",  2));  // should be denied
        queue.offer(new Reservation("Edward",  "Double Room", 4));

        System.out.println("Processing booking queue...");
        System.out.println("----------------------------------------");

        while (!queue.isEmpty()) {
            allocationService.processBooking(queue.poll());
        }

        System.out.println("----------------------------------------");
        inventory.displayInventory();
        System.out.println("----------------------------------------");
        allocationService.displayAllocations();
        System.out.println("========================================");
        System.out.println("Allocation complete.");
    }
}
