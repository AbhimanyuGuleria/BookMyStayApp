import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * UseCase10BookingCancellation - Enables safe cancellation of confirmed bookings
 * by reversing inventory state using a Stack for LIFO rollback tracking.
 *
 * @author BookMyStay Team
 * @version 10.0
 */
public class UseCase10BookingCancellation {

    /**
     * Represents a confirmed reservation.
     */
    static class Reservation {
        private String reservationId;
        private String guestName;
        private String roomType;
        private String assignedRoomId;
        private boolean cancelled;

        public Reservation(String reservationId, String guestName,
                           String roomType, String assignedRoomId) {
            this.reservationId  = reservationId;
            this.guestName      = guestName;
            this.roomType       = roomType;
            this.assignedRoomId = assignedRoomId;
            this.cancelled      = false;
        }

        public String getReservationId()  { return reservationId; }
        public String getGuestName()      { return guestName; }
        public String getRoomType()       { return roomType; }
        public String getAssignedRoomId() { return assignedRoomId; }
        public boolean isCancelled()      { return cancelled; }
        public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

        @Override
        public String toString() {
            return "[" + reservationId + "] " + guestName + " | "
                    + roomType + " | RoomID: " + assignedRoomId
                    + (cancelled ? " (CANCELLED)" : " (CONFIRMED)");
        }
    }

    /**
     * RoomInventory - Centralized inventory with increment support for rollback.
     */
    static class RoomInventory {
        private HashMap<String, Integer> inventory;

        public RoomInventory() {
            inventory = new HashMap<>();
            inventory.put("Single Room", 1);
            inventory.put("Double Room", 1);
            inventory.put("Suite Room",  1);
        }

        public int getAvailability(String roomType) {
            return inventory.getOrDefault(roomType, 0);
        }

        public void decrement(String roomType) {
            inventory.put(roomType, inventory.getOrDefault(roomType, 0) - 1);
        }

        public void increment(String roomType) {
            inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1);
        }

        public void displayInventory() {
            System.out.println("  Inventory:");
            for (HashMap.Entry<String, Integer> e : inventory.entrySet()) {
                System.out.printf("    %-15s: %d%n", e.getKey(), e.getValue());
            }
        }
    }

    /**
     * CancellationService - Validates and processes booking cancellations,
     * restoring inventory using a Stack-based LIFO rollback mechanism.
     */
    static class CancellationService {
        private HashMap<String, Reservation> bookingRegistry;
        private RoomInventory inventory;
        private Stack<String> rollbackStack;   // tracks released room IDs
        private List<Reservation> history;

        /**
         * Constructs the cancellation service with a shared booking registry and inventory.
         *
         * @param bookingRegistry map of reservation ID to Reservation objects
         * @param inventory       the centralized inventory
         * @param history         the booking history list to update on cancellation
         */
        public CancellationService(HashMap<String, Reservation> bookingRegistry,
                                   RoomInventory inventory, List<Reservation> history) {
            this.bookingRegistry = bookingRegistry;
            this.inventory       = inventory;
            this.history         = history;
            this.rollbackStack   = new Stack<>();
        }

        /**
         * Attempts to cancel a reservation. Validates existence and cancellation status,
         * then performs rollback in a strict, predefined order.
         *
         * @param reservationId the ID of the reservation to cancel
         */
        public void cancelBooking(String reservationId) {
            Reservation reservation = bookingRegistry.get(reservationId);

            // Validate existence
            if (reservation == null) {
                System.out.println("  [ERROR] Reservation not found: " + reservationId);
                return;
            }

            // Validate not already cancelled
            if (reservation.isCancelled()) {
                System.out.println("  [ERROR] Reservation already cancelled: " + reservationId);
                return;
            }

            // Step 1: Mark as cancelled
            reservation.setCancelled(true);

            // Step 2: Push released room ID onto rollback stack
            rollbackStack.push(reservation.getAssignedRoomId());

            // Step 3: Restore inventory immediately
            inventory.increment(reservation.getRoomType());

            System.out.println("  [CANCELLED] " + reservation);
            System.out.println("    Inventory restored for: " + reservation.getRoomType());
            System.out.println("    Rollback stack top: " + rollbackStack.peek());
        }

        /**
         * Displays the rollback stack (released room IDs in LIFO order).
         */
        public void displayRollbackStack() {
            System.out.println("  Rollback Stack (LIFO - released room IDs):");
            if (rollbackStack.isEmpty()) {
                System.out.println("    Stack is empty.");
            } else {
                // Display without consuming
                Stack<String> temp = new Stack<>();
                temp.addAll(rollbackStack);
                int pos = temp.size();
                while (!temp.isEmpty()) {
                    System.out.println("    [" + pos-- + "] " + temp.pop());
                }
            }
        }
    }

    /**
     * Main method - demonstrates booking confirmation followed by safe cancellation.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Book My Stay App - Cancellation");
        System.out.println("  Hotel Booking System v10.0");
        System.out.println("========================================");

        RoomInventory inventory = new RoomInventory();

        // Pre-populate confirmed bookings
        HashMap<String, Reservation> registry = new HashMap<>();
        registry.put("R001", new Reservation("R001", "Alice",   "Single Room", "S001"));
        registry.put("R002", new Reservation("R002", "Bob",     "Suite Room",  "U001"));
        registry.put("R003", new Reservation("R003", "Charlie", "Double Room", "D001"));

        // Simulate used inventory
        inventory.decrement("Single Room");
        inventory.decrement("Suite Room");
        inventory.decrement("Double Room");

        List<Reservation> history = new ArrayList<>(registry.values());

        CancellationService cancellationService =
                new CancellationService(registry, inventory, history);

        System.out.println("Inventory before cancellations:");
        inventory.displayInventory();
        System.out.println("----------------------------------------");

        // Process cancellations
        cancellationService.cancelBooking("R002");
        System.out.println();
        cancellationService.cancelBooking("R001");
        System.out.println();

        // Attempt duplicate cancellation
        cancellationService.cancelBooking("R001");
        System.out.println();

        // Attempt non-existent reservation
        cancellationService.cancelBooking("R999");

        System.out.println("----------------------------------------");
        System.out.println("Inventory after cancellations:");
        inventory.displayInventory();

        System.out.println("----------------------------------------");
        cancellationService.displayRollbackStack();

        System.out.println("========================================");
        System.out.println("Cancellation process complete.");
    }
}
