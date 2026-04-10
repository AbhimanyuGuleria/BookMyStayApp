import java.util.ArrayList;
import java.util.List;

/**
 * UseCase8BookingHistoryReport - Introduces historical tracking of confirmed
 * bookings and reporting using an ordered List data structure.
 *
 * @author BookMyStay Team
 * @version 8.0
 */
public class UseCase8BookingHistoryReport {

    /**
     * Represents a confirmed reservation record stored in history.
     */
    static class Reservation {
        private String reservationId;
        private String guestName;
        private String roomType;
        private String assignedRoomId;
        private int nights;
        private double pricePerNight;

        /**
         * Constructs a fully confirmed Reservation record.
         */
        public Reservation(String reservationId, String guestName, String roomType,
                           String assignedRoomId, int nights, double pricePerNight) {
            this.reservationId  = reservationId;
            this.guestName      = guestName;
            this.roomType       = roomType;
            this.assignedRoomId = assignedRoomId;
            this.nights         = nights;
            this.pricePerNight  = pricePerNight;
        }

        public String getReservationId()  { return reservationId; }
        public String getGuestName()      { return guestName; }
        public String getRoomType()       { return roomType; }
        public String getAssignedRoomId() { return assignedRoomId; }
        public int getNights()            { return nights; }
        public double getPricePerNight()  { return pricePerNight; }
        public double getTotalCost()      { return nights * pricePerNight; }
    }

    /**
     * BookingHistory - Maintains an ordered list of confirmed reservations.
     * Provides an audit trail without modifying reservation records.
     */
    static class BookingHistory {
        private List<Reservation> history;

        /**
         * Initializes an empty booking history list.
         */
        public BookingHistory() {
            history = new ArrayList<>();
        }

        /**
         * Adds a confirmed reservation to the history.
         *
         * @param reservation the confirmed reservation to record
         */
        public void addBooking(Reservation reservation) {
            history.add(reservation);
            System.out.println("  Recorded: [" + reservation.getReservationId()
                    + "] " + reservation.getGuestName() + " - " + reservation.getRoomType());
        }

        /**
         * Returns the full list of recorded reservations (read-only intended).
         *
         * @return the booking history list
         */
        public List<Reservation> getHistory() {
            return history;
        }

        /**
         * Returns the total number of bookings recorded.
         *
         * @return booking count
         */
        public int getTotalBookings() {
            return history.size();
        }
    }

    /**
     * BookingReportService - Generates operational reports from booking history.
     * Does not modify stored booking records.
     */
    static class BookingReportService {
        private BookingHistory bookingHistory;

        /**
         * Constructs the report service with a reference to booking history.
         *
         * @param bookingHistory the history data source
         */
        public BookingReportService(BookingHistory bookingHistory) {
            this.bookingHistory = bookingHistory;
        }

        /**
         * Displays a full detailed booking report.
         */
        public void generateFullReport() {
            System.out.println("  ========== BOOKING HISTORY REPORT ==========");
            List<Reservation> records = bookingHistory.getHistory();

            if (records.isEmpty()) {
                System.out.println("  No bookings on record.");
                return;
            }

            double grandTotal = 0.0;
            for (int i = 0; i < records.size(); i++) {
                Reservation r = records.get(i);
                System.out.printf("  [%d] ID: %-6s | Guest: %-10s | Room: %-13s | RoomID: %-5s | Nights: %d | Total: $%.2f%n",
                        i + 1, r.getReservationId(), r.getGuestName(), r.getRoomType(),
                        r.getAssignedRoomId(), r.getNights(), r.getTotalCost());
                grandTotal += r.getTotalCost();
            }

            System.out.println("  -------------------------------------------");
            System.out.printf("  Total Bookings : %d%n", bookingHistory.getTotalBookings());
            System.out.printf("  Grand Revenue  : $%.2f%n", grandTotal);
            System.out.println("  ============================================");
        }

        /**
         * Generates a summary report showing bookings per room type.
         */
        public void generateSummaryReport() {
            System.out.println("  ========== SUMMARY BY ROOM TYPE ==========");
            java.util.HashMap<String, Integer> countMap = new java.util.HashMap<>();
            java.util.HashMap<String, Double> revenueMap = new java.util.HashMap<>();

            for (Reservation r : bookingHistory.getHistory()) {
                countMap.merge(r.getRoomType(), 1, Integer::sum);
                revenueMap.merge(r.getRoomType(), r.getTotalCost(), Double::sum);
            }

            for (String type : countMap.keySet()) {
                System.out.printf("  %-15s : %d booking(s), Revenue: $%.2f%n",
                        type, countMap.get(type), revenueMap.get(type));
            }
            System.out.println("  ==========================================");
        }
    }

    /**
     * Main method - simulates confirmed bookings and generates reports.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Book My Stay App - Booking History");
        System.out.println("   Hotel Booking System v8.0");
        System.out.println("========================================");

        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService(history);

        System.out.println("Recording confirmed bookings...");
        System.out.println("----------------------------------------");

        history.addBooking(new Reservation("R001", "Alice",   "Single Room", "S001", 2, 80.0));
        history.addBooking(new Reservation("R002", "Bob",     "Suite Room",  "U001", 3, 250.0));
        history.addBooking(new Reservation("R003", "Charlie", "Double Room", "D001", 1, 130.0));
        history.addBooking(new Reservation("R004", "Diana",   "Single Room", "S002", 4, 80.0));
        history.addBooking(new Reservation("R005", "Edward",  "Suite Room",  "U002", 2, 250.0));

        System.out.println("----------------------------------------");
        System.out.println("Generating reports...");
        System.out.println();
        reportService.generateFullReport();
        System.out.println();
        reportService.generateSummaryReport();

        System.out.println("========================================");
        System.out.println("Reporting complete. History unmodified.");
    }
}
