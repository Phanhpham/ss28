import java.sql.*;
import java.time.LocalDate;

public class B5 {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/shop";
        String user = "root";
        String password = "12345678";

        Connection conn = null;

        try {
            conn = DriverManager.getConnection(url, user, password);
            conn.setAutoCommit(false);

            // Tạo đơn hàng mới
            String insertOrder = "INSERT INTO Orders (order_id, customer_name, order_date) VALUES (?, ?, ?)";
            PreparedStatement orderStmt = conn.prepareStatement(insertOrder);
            int orderId = 1001;
            orderStmt.setInt(1, orderId);
            orderStmt.setString(2, "Nguyen Van A");
            orderStmt.setDate(3, Date.valueOf(LocalDate.now()));
            orderStmt.executeUpdate();

            // Thêm chi tiết đơn hàng
            String insertDetail = "INSERT INTO OrderDetails (detail_id, order_id, product_name, quantity) VALUES (?, ?, ?, ?)";
            PreparedStatement detailStmt = conn.prepareStatement(insertDetail);

            // Chi tiết 1 - Hợp lệ
            detailStmt.setInt(1, 2001);
            detailStmt.setInt(2, orderId);
            detailStmt.setString(3, "Sản phẩm A");
            detailStmt.setInt(4, 2);
            detailStmt.executeUpdate();

            // Chi tiết 2 - Lỗi: số lượng âm
            detailStmt.setInt(1, 2002);
            detailStmt.setInt(2, orderId);
            detailStmt.setString(3, "Sản phẩm B");
            detailStmt.setInt(4, -3);
            if (detailStmt.getParameterMetaData().getParameterCount() > 0 && -3 < 0) {
                throw new SQLException("Số lượng không được âm!");
            }
            detailStmt.executeUpdate();

            conn.commit();
            System.out.println("Đơn hàng và chi tiết đã được thêm thành công!");

        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println("Giao dịch bị hủy (rollback) do lỗi.");
                }
            } catch (SQLException ex) {
                System.out.println("Lỗi rollback: " + ex.getMessage());
            }
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
