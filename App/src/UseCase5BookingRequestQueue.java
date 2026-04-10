import java.util.LinkedList;
import java.util.Queue;

/**
 * UseCase5BookingRequestQueue - Demonstrates fair booking request handling
 * using a Queue (FIFO) to preserve arrival order under peak demand.
 *
 * @author BookMyStay Team
 * @version 5.0
 */
public class UseCase5BookingRequestQueue {

    /**
     * Represents a guest's booking request/reservation intent.
     */
    static class Reservation {
        private String guestName;
        private String roomType;
        private int nights;

        /**
         * Constructs a Reservation with guest details.
         *
         * @param guestName the name of the guest
         * @param roomType  the type of room requested
         * @param nights    number of nights for the stay
         */
        public Reservation(String guestName, String roomType, int nights) {
            this.guestName = guestName;
            this.roomType = roomType;
            this.nights = nights;
        }

        public String getGuestName() { return guestName; }
        public String getRoomType()  { return roomType; }
        public int getNights()       { return nights; }

        @Override
        public String toString() {
            return "Reservation{guest='" + guestName + "', roomType='" + roomType
                    + "', nights=" + nights + "}";
        }
    }

    /**
     * BookingRequestQueue - Manages incoming booking requests in FIFO order.
     * Requests are stored but not processed (no inventory changes occur here).
     */
    static class BookingRequestQueue {
        private Queue<Reservation> requestQueue;

        /**
         * Initializes an empty booking request queue.
         */
        public BookingRequestQueue() {
            requestQueue = new LinkedList<>();
        }

        /**
         * Adds a booking request to the end of the queue.
         *
         * @param reservation the reservation request to enqueue
         */
        public void addRequest(Reservation reservation) {
            requestQueue.offer(reservation);
            System.out.println("  Queued: " + reservation);
        }

        /**
         * Returns the current size of the queue.
         *
         * @return number of pending requests
         */
        public int size() {
            return requestQueue.size();
        }

        /**
         * Peeks at the front of the queue without removing it.
         *
         * @return the next request to be processed
         */
        public Reservation peek() {
            return requestQueue.peek();
        }

        /**
         * Removes and returns the next request from the queue (FIFO).
         *
         * @return the next Reservation in order
         */
        public Reservation poll() {
            return requestQueue.poll();
        }

        /**
         * Displays all queued requests in order without modifying the queue.
         */
        public void displayQueue() {
            if (requestQueue.isEmpty()) {
                System.out.println("  Queue is empty.");
                return;
            }
            System.out.println("  Pending Booking Requests (in arrival order):");
            int position = 1;
            for (Reservation r : requestQueue) {
                System.out.println("  [" + position++ + "] " + r);
            }
        }
    }

    /**
     * Main method - simulates multiple guests submitting booking requests.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Book My Stay App - Booking Queue");
        System.out.println("   Hotel Booking System v5.0");
        System.out.println("========================================");

        BookingRequestQueue queue = new BookingRequestQueue();

        System.out.println("Guests submitting booking requests...");
        System.out.println("----------------------------------------");
        queue.addRequest(new Reservation("Alice",   "Single Room", 2));
        queue.addRequest(new Reservation("Bob",     "Suite Room",  3));
        queue.addRequest(new Reservation("Charlie", "Double Room", 1));
        queue.addRequest(new Reservation("Diana",   "Single Room", 4));
        queue.addRequest(new Reservation("Edward",  "Suite Room",  2));

        System.out.println("----------------------------------------");
        System.out.println("Total requests in queue: " + queue.size());
        System.out.println();
        queue.displayQueue();

        System.out.println("----------------------------------------");
        System.out.println("Next to be processed: " + queue.peek());
        System.out.println("========================================");
        System.out.println("Queue ready for processing. No inventory changes made.");
    }
}
