import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UseCase12DataPersistenceRecovery - Introduces file-based persistence for booking
 * history and inventory state, enabling recovery across application restarts.
 *
 * @author BookMyStay Team
 * @version 12.0
 */
public class UseCase12DataPersistenceRecovery {

    /**
     * Represents a confirmed reservation record.
     * Must be Serializable to support file-based persistence.
     */
    static class Reservation implements Serializable {
        private static final long serialVersionUID = 1L;

        private String reservationId;
        private String guestName;
        private String roomType;
        private String assignedRoomId;
        private int nights;

        public Reservation(String reservationId, String guestName,
                           String roomType, String assignedRoomId, int nights) {
            this.reservationId  = reservationId;
            this.guestName      = guestName;
            this.roomType       = roomType;
            this.assignedRoomId = assignedRoomId;
            this.nights         = nights;
        }

        public String getReservationId()  { return reservationId; }
        public String getGuestName()      { return guestName; }
        public String getRoomType()       { return roomType; }
        public String getAssignedRoomId() { return assignedRoomId; }
        public int getNights()            { return nights; }

        @Override
        public String toString() {
            return "[" + reservationId + "] " + guestName + " | "
                    + roomType + " | RoomID: " + assignedRoomId
                    + " | Nights: " + nights;
        }
    }

    /**
     * PersistenceService - Handles serialization and deserialization of system state.
     * Provides graceful handling for missing or corrupted files.
     */
    static class PersistenceService {
        private static final String INVENTORY_FILE = "inventory_state.ser";
        private static final String HISTORY_FILE   = "booking_history.ser";

        /**
         * Persists the inventory snapshot to a file.
         *
         * @param inventory the current inventory state to save
         */
        public void saveInventory(HashMap<String, Integer> inventory) {
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(INVENTORY_FILE))) {
                oos.writeObject(inventory);
                System.out.println("  [SAVED] Inventory state -> " + INVENTORY_FILE);
            } catch (IOException e) {
                System.out.println("  [ERROR] Failed to save inventory: " + e.getMessage());
            }
        }

        /**
         * Loads inventory state from file.
         *
         * @return the restored inventory map, or default state if file is unavailable
         */
        @SuppressWarnings("unchecked")
        public HashMap<String, Integer> loadInventory() {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(INVENTORY_FILE))) {
                HashMap<String, Integer> inventory = (HashMap<String, Integer>) ois.readObject();
                System.out.println("  [LOADED] Inventory state <- " + INVENTORY_FILE);
                return inventory;
            } catch (FileNotFoundException e) {
                System.out.println("  [RECOVERY] No inventory file found. Using defaults.");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("  [RECOVERY] Corrupted inventory file. Using defaults.");
            }

            // Return default state as fallback
            HashMap<String, Integer> defaults = new HashMap<>();
            defaults.put("Single Room", 5);
            defaults.put("Double Room", 3);
            defaults.put("Suite Room",  2);
            return defaults;
        }

        /**
         * Persists booking history to a file.
         *
         * @param history the list of reservations to save
         */
        public void saveBookingHistory(List<Reservation> history) {
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(HISTORY_FILE))) {
                oos.writeObject(history);
                System.out.println("  [SAVED] Booking history  -> " + HISTORY_FILE);
            } catch (IOException e) {
                System.out.println("  [ERROR] Failed to save booking history: " + e.getMessage());
            }
        }

        /**
         * Loads booking history from file.
         *
         * @return the restored booking history list, or an empty list if unavailable
         */
        @SuppressWarnings("unchecked")
        public List<Reservation> loadBookingHistory() {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(HISTORY_FILE))) {
                List<Reservation> history = (List<Reservation>) ois.readObject();
                System.out.println("  [LOADED] Booking history  <- " + HISTORY_FILE);
                return history;
            } catch (FileNotFoundException e) {
                System.out.println("  [RECOVERY] No booking history file found. Starting fresh.");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("  [RECOVERY] Corrupted history file. Starting fresh.");
            }
            return new ArrayList<>();
        }
    }

    /**
     * Displays the current inventory state.
     *
     * @param inventory the inventory map to display
     */
    static void displayInventory(HashMap<String, Integer> inventory) {
        System.out.println("  Inventory State:");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.printf("    %-15s: %d%n", e.getKey(), e.getValue());
        }
    }

    /**
     * Displays all records in booking history.
     *
     * @param history the list of reservations to display
     */
    static void displayHistory(List<Reservation> history) {
        System.out.println("  Booking History (" + history.size() + " record(s)):");
        if (history.isEmpty()) {
            System.out.println("    No bookings recorded.");
        } else {
            for (Reservation r : history) {
                System.out.println("    " + r);
            }
        }
    }

    /**
     * Main method - demonstrates full persistence cycle: save, simulate restart, recover.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Book My Stay App - Data Persistence");
        System.out.println("  Hotel Booking System v12.0");
        System.out.println("========================================");

        PersistenceService persistenceService = new PersistenceService();

        // ---- PHASE 1: Build and save state ----
        System.out.println("PHASE 1: Building system state...");
        System.out.println("----------------------------------------");

        HashMap<String, Integer> inventory = new HashMap<>();
        inventory.put("Single Room", 3);
        inventory.put("Double Room", 1);
        inventory.put("Suite Room",  2);

        List<Reservation> history = new ArrayList<>();
        history.add(new Reservation("R001", "Alice",   "Single Room", "S001", 2));
        history.add(new Reservation("R002", "Bob",     "Suite Room",  "U001", 3));
        history.add(new Reservation("R003", "Charlie", "Double Room", "D001", 1));

        // Simulate bookings consuming inventory
        inventory.put("Single Room", inventory.get("Single Room") - 1);
        inventory.put("Suite Room",  inventory.get("Suite Room")  - 1);
        inventory.put("Double Room", inventory.get("Double Room") - 1);

        System.out.println("Current state before save:");
        displayInventory(inventory);
        displayHistory(history);

        System.out.println("----------------------------------------");
        System.out.println("Saving system state to disk...");
        persistenceService.saveInventory(inventory);
        persistenceService.saveBookingHistory(history);

        // ---- PHASE 2: Simulate application restart ----
        System.out.println("----------------------------------------");
        System.out.println("PHASE 2: Simulating application restart...");
        System.out.println("  (In-memory state cleared)");
        inventory = null;
        history   = null;

        // ---- PHASE 3: Recover state ----
        System.out.println("----------------------------------------");
        System.out.println("PHASE 3: Recovering state from disk...");
        HashMap<String, Integer> recoveredInventory = persistenceService.loadInventory();
        List<Reservation> recoveredHistory          = persistenceService.loadBookingHistory();

        System.out.println("----------------------------------------");
        System.out.println("Recovered state:");
        displayInventory(recoveredInventory);
        displayHistory(recoveredHistory);

        System.out.println("========================================");
        System.out.println("System resumed successfully with persisted state.");
    }
}
