import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * UseCase11ConcurrentBookingSimulation - Demonstrates thread-safe booking under
 * concurrent access using synchronized blocks to protect shared mutable state.
 *
 * @author BookMyStay Team
 * @version 11.0
 */
public class UseCase11ConcurrentBookingSimulation {

    /**
     * Represents a guest's booking request submitted concurrently.
     */
    static class Reservation {
        private String guestName;
        private String roomType;

        public Reservation(String guestName, String roomType) {
            this.guestName = guestName;
            this.roomType  = roomType;
        }

        public String getGuestName() { return guestName; }
        public String getRoomType()  { return roomType; }
    }

    /**
     * SharedBookingQueue - A thread-safe shared queue for booking requests.
     * Synchronization ensures requests are added and consumed atomically.
     */
    static class SharedBookingQueue {
        private Queue<Reservation> queue = new LinkedList<>();

        /**
         * Thread-safely adds a reservation to the shared queue.
         *
         * @param reservation the reservation to enqueue
         */
        public synchronized void enqueue(Reservation reservation) {
            queue.offer(reservation);
        }

        /**
         * Thread-safely removes and returns the next reservation from the queue.
         *
         * @return the next Reservation, or null if queue is empty
         */
        public synchronized Reservation dequeue() {
            return queue.poll();
        }

        /**
         * Returns current queue size (for display purposes).
         *
         * @return number of pending requests
         */
        public synchronized int size() {
            return queue.size();
        }
    }

    /**
     * ThreadSafeInventory - Protects shared inventory state from race conditions
     * using synchronized methods.
     */
    static class ThreadSafeInventory {
        private HashMap<String, Integer> inventory = new HashMap<>();
        private Set<String> allocatedRoomIds       = new HashSet<>();
        private HashMap<String, Integer> counters  = new HashMap<>();

        public ThreadSafeInventory() {
            inventory.put("Single Room", 3);
            inventory.put("Double Room", 2);
            inventory.put("Suite Room",  2);
        }

        /**
         * Atomically allocates a room for the given reservation.
         * Critical section: only one thread can allocate at a time.
         *
         * @param reservation the booking request to process
         * @return the assigned room ID, or null if unavailable
         */
        public synchronized String allocate(Reservation reservation) {
            String roomType = reservation.getRoomType();

            if (inventory.getOrDefault(roomType, 0) <= 0) {
                return null;
            }

            // Generate unique room ID
            int count = counters.getOrDefault(roomType, 0) + 1;
            counters.put(roomType, count);
            String prefix = roomType.substring(0, 1).toUpperCase();
            String roomId = prefix + String.format("%03d", count);

            if (allocatedRoomIds.contains(roomId)) {
                return null; // safety guard - should not occur with counters
            }

            allocatedRoomIds.add(roomId);
            inventory.put(roomType, inventory.get(roomType) - 1);
            return roomId;
        }

        /**
         * Displays the current inventory state.
         */
        public synchronized void displayInventory() {
            System.out.println("  Final Inventory State:");
            for (HashMap.Entry<String, Integer> e : inventory.entrySet()) {
                System.out.printf("    %-15s: %d remaining%n", e.getKey(), e.getValue());
            }
            System.out.println("  Total Allocated Room IDs: " + allocatedRoomIds.size());
        }
    }

    /**
     * BookingThread - Represents a guest submitting booking requests concurrently.
     * Retrieves requests from the shared queue and allocates rooms in a critical section.
     */
    static class BookingThread extends Thread {
        private SharedBookingQueue queue;
        private ThreadSafeInventory inventory;

        public BookingThread(String name, SharedBookingQueue queue, ThreadSafeInventory inventory) {
            super(name);
            this.queue     = queue;
            this.inventory = inventory;
        }

        @Override
        public void run() {
            while (true) {
                Reservation reservation = queue.dequeue();
                if (reservation == null) break;

                String roomId = inventory.allocate(reservation);

                synchronized (System.out) {
                    if (roomId != null) {
                        System.out.printf("  [%s] CONFIRMED  %-10s -> RoomID: %s (%s)%n",
                                getName(), reservation.getGuestName(), roomId, reservation.getRoomType());
                    } else {
                        System.out.printf("  [%s] DENIED     %-10s -> No %s available%n",
                                getName(), reservation.getGuestName(), reservation.getRoomType());
                    }
                }
            }
        }
    }

    /**
     * Main method - simulates concurrent booking requests processed by multiple threads.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("========================================");
        System.out.println("  Book My Stay App - Concurrent Booking");
        System.out.println("  Hotel Booking System v11.0");
        System.out.println("========================================");

        SharedBookingQueue queue     = new SharedBookingQueue();
        ThreadSafeInventory inventory = new ThreadSafeInventory();

        // Load all booking requests into the shared queue
        String[] guests    = { "Alice", "Bob", "Charlie", "Diana", "Edward",
                "Frank", "Grace", "Henry",  "Isla",  "Jack"  };
        String[] roomTypes = { "Single Room", "Suite Room",  "Single Room", "Double Room",
                "Suite Room",  "Single Room", "Double Room", "Suite Room",
                "Single Room", "Double Room" };

        for (int i = 0; i < guests.length; i++) {
            queue.enqueue(new Reservation(guests[i], roomTypes[i]));
        }

        System.out.println("Total booking requests loaded: " + queue.size());
        System.out.println("Starting concurrent processing with 3 booking threads...");
        System.out.println("----------------------------------------");

        // Create and start multiple booking threads
        BookingThread t1 = new BookingThread("Thread-1", queue, inventory);
        BookingThread t2 = new BookingThread("Thread-2", queue, inventory);
        BookingThread t3 = new BookingThread("Thread-3", queue, inventory);

        t1.start();
        t2.start();
        t3.start();

        // Wait for all threads to finish
        t1.join();
        t2.join();
        t3.join();

        System.out.println("----------------------------------------");
        inventory.displayInventory();
        System.out.println("========================================");
        System.out.println("Concurrent simulation complete. No double bookings occurred.");
    }
}
