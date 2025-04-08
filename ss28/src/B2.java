import java.sql.*;

public class B2 {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/test";
        String user = "root";
        String password = "12345678";

        Connection conn = null;

        try {
            // Kết nối database
            conn = DriverManager.getConnection(url, user, password);
            System.out.println(" Đã kết nối database!");

            // Tắt auto-commit
            conn.setAutoCommit(false);
            System.out.println("Auto-commit sau khi tắt: " + conn.getAutoCommit());

            // Câu lệnh INSERT đầu tiên (hợp lệ)
            String insert1 = "INSERT INTO users(id, name, email) VALUES(1, 'Alice', 'alice@example.com')";
            PreparedStatement stmt1 = conn.prepareStatement(insert1);
            stmt1.executeUpdate();
            System.out.println(" Câu lệnh 1 thành công");

            // Câu lệnh INSERT thứ hai (gây lỗi, trùng ID)
            String insert2 = "INSERT INTO users(id, name, email) VALUES(1, 'Bob', 'bob@example.com')";
            PreparedStatement stmt2 = conn.prepareStatement(insert2);
            stmt2.executeUpdate();
            System.out.println(" Câu lệnh 2 thành công");

            // Nếu không lỗi, commit luôn
            conn.commit();
            System.out.println(" Commit thành công!");

        } catch (SQLException e) {
            System.out.println(" Gặp lỗi: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println("↩ Rollback thành công!");
                }
            } catch (SQLException ex) {
                System.out.println(" Rollback lỗi: " + ex.getMessage());
            }
        }

        // Kiểm tra dữ liệu trong bảng
        try {
            Statement checkStmt = conn.createStatement();
            ResultSet rs = checkStmt.executeQuery("SELECT * FROM users");

            System.out.println("\n📋 Danh sách người dùng hiện tại:");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " - " + rs.getString("name") + " - " + rs.getString("email"));
            }

        } catch (SQLException e) {
            System.out.println(" Không thể truy vấn bảng: " + e.getMessage());
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

