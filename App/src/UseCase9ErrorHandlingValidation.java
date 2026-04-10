import java.util.HashMap;

/**
 * UseCase9ErrorHandlingValidation - Strengthens system reliability by introducing
 * custom exceptions, input validation, and fail-fast error handling.
 *
 * @author BookMyStay Team
 * @version 9.0
 */
public class UseCase9ErrorHandlingValidation {

    // ----------------------------------------------------------------
    // Custom Exceptions
    // ----------------------------------------------------------------

    /**
     * Thrown when a requested room type does not exist in the system.
     */
    static class InvalidRoomTypeException extends Exception {
        public InvalidRoomTypeException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when a booking request is made for a room that is fully booked.
     */
    static class RoomNotAvailableException extends Exception {
        public RoomNotAvailableException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when a guest name or other required input field is invalid.
     */
    static class InvalidBookingInputException extends Exception {
        public InvalidBookingInputException(String message) {
            super(message);
        }
    }

    // ----------------------------------------------------------------
    // Inventory
    // ----------------------------------------------------------------

    /**
     * RoomInventory - Centralized inventory with validated access methods.
     */
    static class RoomInventory {
        private HashMap<String, Integer> inventory;

        public RoomInventory() {
            inventory = new HashMap<>();
            inventory.put("Single Room", 2);
            inventory.put("Double Room", 1);
            inventory.put("Suite Room",  1);
        }

        /**
         * Checks if the given room type is recognized by the system.
         *
         * @param roomType the room type to validate
         * @return true if valid, false otherwise
         */
        public boolean isValidRoomType(String roomType) {
            return inventory.containsKey(roomType);
        }

        /**
         * Returns availability count for a room type.
         *
         * @param roomType the room type to query
         * @return available room count
         */
        public int getAvailability(String roomType) {
            return inventory.getOrDefault(roomType, 0);
        }

        /**
         * Decrements inventory for the given room type.
         *
         * @param roomType the room type to decrement
         */
        public void decrement(String roomType) {
            inventory.put(roomType, inventory.get(roomType) - 1);
        }

        public void displayInventory() {
            System.out.println("  Current Inventory:");
            for (HashMap.Entry<String, Integer> e : inventory.entrySet()) {
                System.out.printf("    %-15s: %d%n", e.getKey(), e.getValue());
            }
        }
    }

    // ----------------------------------------------------------------
    // Validator
    // ----------------------------------------------------------------

    /**
     * InvalidBookingValidator - Validates booking input and system state
     * before allowing any allocation to proceed.
     */
    static class InvalidBookingValidator {
        private RoomInventory inventory;

        public InvalidBookingValidator(RoomInventory inventory) {
            this.inventory = inventory;
        }

        /**
         * Validates all aspects of a booking request.
         * Throws a specific custom exception if any check fails.
         *
         * @param guestName the name of the guest
         * @param roomType  the requested room type
         * @param nights    number of nights
         * @throws InvalidBookingInputException if guest name or nights are invalid
         * @throws InvalidRoomTypeException     if the room type is unrecognized
         * @throws RoomNotAvailableException    if no rooms of this type are available
         */
        public void validate(String guestName, String roomType, int nights)
                throws InvalidBookingInputException, InvalidRoomTypeException, RoomNotAvailableException {

            // Validate guest name
            if (guestName == null || guestName.trim().isEmpty()) {
                throw new InvalidBookingInputException(
                        "Guest name cannot be null or empty.");
            }

            // Validate nights
            if (nights <= 0) {
                throw new InvalidBookingInputException(
                        "Number of nights must be greater than zero. Got: " + nights);
            }

            // Validate room type (case-sensitive)
            if (!inventory.isValidRoomType(roomType)) {
                throw new InvalidRoomTypeException(
                        "Room type '" + roomType + "' is not recognized. "
                                + "Valid types: Single Room, Double Room, Suite Room");
            }

            // Validate availability
            if (inventory.getAvailability(roomType) <= 0) {
                throw new RoomNotAvailableException(
                        "No rooms available for type: '" + roomType + "'.");
            }
        }
    }

    // ----------------------------------------------------------------
    // Booking Service
    // ----------------------------------------------------------------

    /**
     * BookingService - Processes validated booking requests safely.
     */
    static class BookingService {
        private RoomInventory inventory;
        private InvalidBookingValidator validator;

        public BookingService(RoomInventory inventory) {
            this.inventory = inventory;
            this.validator = new InvalidBookingValidator(inventory);
        }

        /**
         * Attempts to book a room after full validation.
         * Catches and reports specific exceptions without crashing.
         *
         * @param guestName the guest's name
         * @param roomType  the requested room type
         * @param nights    number of nights
         */
        public void book(String guestName, String roomType, int nights) {
            try {
                validator.validate(guestName, roomType, nights);
                inventory.decrement(roomType);
                System.out.println("  [SUCCESS] Booking confirmed for " + guestName
                        + " | " + roomType + " | " + nights + " night(s).");
            } catch (InvalidBookingInputException e) {
                System.out.println("  [INPUT ERROR]     " + e.getMessage());
            } catch (InvalidRoomTypeException e) {
                System.out.println("  [INVALID ROOM]    " + e.getMessage());
            } catch (RoomNotAvailableException e) {
                System.out.println("  [NOT AVAILABLE]   " + e.getMessage());
            }
        }
    }

    // ----------------------------------------------------------------
    // Main
    // ----------------------------------------------------------------

    /**
     * Main method - demonstrates validation and error handling across various scenarios.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Book My Stay App - Error Handling");
        System.out.println("   Hotel Booking System v9.0");
        System.out.println("========================================");

        RoomInventory inventory = new RoomInventory();
        BookingService bookingService = new BookingService(inventory);

        System.out.println("Initial Inventory:");
        inventory.displayInventory();
        System.out.println("----------------------------------------");
        System.out.println("Processing booking requests...");
        System.out.println();

        // Valid booking
        bookingService.book("Alice", "Single Room", 2);

        // Invalid room type (wrong case / non-existent)
        bookingService.book("Bob", "single room", 1);

        // Empty guest name
        bookingService.book("", "Double Room", 3);

        // Invalid nights
        bookingService.book("Charlie", "Suite Room", 0);

        // Valid booking - last suite
        bookingService.book("Diana", "Suite Room", 1);

        // Now Suite Room has 0 - next attempt should fail
        bookingService.book("Edward", "Suite Room", 2);

        // Another valid booking
        bookingService.book("Frank", "Double Room", 2);

        System.out.println("----------------------------------------");
        System.out.println("Final Inventory:");
        inventory.displayInventory();
        System.out.println("========================================");
        System.out.println("Error handling demonstration complete.");
    }
}
