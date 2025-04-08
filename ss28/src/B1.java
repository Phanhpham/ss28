import java.sql.*;

public class B1 {
    public static void main(String[] args) {
        String jdbcURL = "jdbc:mysql://localhost:3306/test";
        String username = "root";
        String password = "12345678";

        Connection conn = null;

        try {
            // Kết nối đến cơ sở dữ liệu
            conn = DriverManager.getConnection(jdbcURL, username, password);
            System.out.println(" Đã kết nối cơ sở dữ liệu.");

            // Kiểm tra trạng thái auto-commit ban đầu
            System.out.println("Auto-commit ban đầu: " + conn.getAutoCommit());

            // Tắt chế độ auto-commit
            conn.setAutoCommit(false);
            System.out.println("Auto-commit sau khi tắt: " + conn.getAutoCommit());

            // Chuẩn bị câu lệnh INSERT
            String sql = "INSERT INTO users (id, name, email) VALUES (?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, 1);
            statement.setString(2, "Nguyễn Văn A");
            statement.setString(3, "nva@example.com");

            // Thực hiện INSERT
            int rowsInserted = statement.executeUpdate();
            System.out.println("Số dòng được chèn: " + rowsInserted);

            // Gọi commit để lưu thay đổi
            conn.commit();
            System.out.println(" Dữ liệu đã được commit.");

            // Kiểm tra lại dữ liệu đã thêm
            Statement checkStatement = conn.createStatement();
            ResultSet rs = checkStatement.executeQuery("SELECT * FROM users");
            System.out.println("\nDanh sách người dùng hiện có:");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " - " + rs.getString("name") + " - " + rs.getString("email"));
            }

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println(" Gặp lỗi, rollback thay đổi.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
                System.out.println(" Đã đóng kết nối.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
