import java.sql.*;
import java.time.LocalDate;

public class B8 {
    static final String URL = "jdbc:mysql://localhost:3306/hotel";
    static final String USER = "root";
    static final String PASSWORD = "12345678";

    public static void main(String[] args) {
        int customerId = 1;
        int roomId = 101;

        try {
            bookRoom(customerId, roomId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void bookRoom(int customerId, int roomId) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);

            // Kiểm tra khách hàng có tồn tại không
            if (!customerExists(conn, customerId)) {
                logFailedBooking(conn, customerId, roomId, "Khách hàng không tồn tại");
                conn.commit();
                System.out.println(" Khách hàng không tồn tại.");
                return;
            }

            // Kiểm tra phòng có trống không
            if (!isRoomAvailable(conn, roomId)) {
                logFailedBooking(conn, customerId, roomId, "Phòng không khả dụng");
                conn.commit();
                System.out.println(" Phòng không khả dụng.");
                return;
            }

            // Cập nhật phòng thành không khả dụng
            PreparedStatement updateRoom = conn.prepareStatement("UPDATE rooms SET availability = FALSE WHERE room_id = ?");
            updateRoom.setInt(1, roomId);
            updateRoom.executeUpdate();

            //  Thêm bản ghi booking
            PreparedStatement insertBooking = conn.prepareStatement(
                    "INSERT INTO bookings (customer_id, room_id, booking_date, status) VALUES (?, ?, ?, ?)");
            insertBooking.setInt(1, customerId);
            insertBooking.setInt(2, roomId);
            insertBooking.setDate(3, Date.valueOf(LocalDate.now()));
            insertBooking.setString(4, "CONFIRMED");
            insertBooking.executeUpdate();

            conn.commit();
            System.out.println(" Đặt phòng thành công!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(" Lỗi giao dịch, rollback...");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static boolean isRoomAvailable(Connection conn, int roomId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT availability FROM rooms WHERE room_id = ?");
        stmt.setInt(1, roomId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getBoolean("availability");
        }
        return false;
    }

    private static boolean customerExists(Connection conn, int customerId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM customers WHERE customer_id = ?");
        stmt.setInt(1, customerId);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    private static void logFailedBooking(Connection conn, int customerId, int roomId, String reason) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO failed_bookings (customer_id, room_id, reason) VALUES (?, ?, ?)");
        stmt.setInt(1, customerId);
        stmt.setInt(2, roomId);
        stmt.setString(3, reason);
        stmt.executeUpdate();
    }
}
