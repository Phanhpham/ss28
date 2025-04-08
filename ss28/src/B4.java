import java.sql.*;

public class B4 {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/test";
        String user = "root";
        String password = "12345678";

        Connection conn = null;

        try {
            conn = DriverManager.getConnection(url, user, password);
            conn.setAutoCommit(false);

            int fromAccountId = 1;
            int toAccountId = 2;
            double amount = 200.0;

            // Kiểm tra số dư người gửi
            String checkSQL = "SELECT balance FROM bank_accounts WHERE account_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
            checkStmt.setInt(1, fromAccountId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Tài khoản gửi không tồn tại");
            }

            double currentBalance = rs.getDouble("balance");
            if (currentBalance < amount) {
                throw new SQLException("Không đủ tiền để chuyển khoản");
            }

            // Trừ tiền người gửi
            String deductSQL = "UPDATE bank_accounts SET balance = balance - ? WHERE account_id = ?";
            PreparedStatement deductStmt = conn.prepareStatement(deductSQL);
            deductStmt.setDouble(1, amount);
            deductStmt.setInt(2, fromAccountId);
            deductStmt.executeUpdate();

            // Cộng tiền người nhận
            String addSQL = "UPDATE bank_accounts SET balance = balance + ? WHERE account_id = ?";
            PreparedStatement addStmt = conn.prepareStatement(addSQL);
            addStmt.setDouble(1, amount);
            addStmt.setInt(2, toAccountId);
            int rowsAffected = addStmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Tài khoản nhận không tồn tại");
            }

            conn.commit();
            System.out.println("Chuyển khoản thành công!");

        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println("Đã rollback thay đổi");
                }
            } catch (SQLException ex) {
                System.out.println("Lỗi khi rollback: " + ex.getMessage());
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
