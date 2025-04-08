import java.sql.*;

public class B3 {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/test";
        String user = "root";
        String password = "12345678";

        Connection conn = null;

        try {
            conn = DriverManager.getConnection(url, user, password);
            // Tắt chế độ auto-commit
            conn.setAutoCommit(false);

            int fromAccountId = 1;
            int toAccountId = 2;
            double amount = 500.0;

            // Kiểm tra số dư tài khoản A
            String checkBalanceSQL = "SELECT balance FROM accounts WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkBalanceSQL);
            checkStmt.setInt(1, fromAccountId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Tài khoản gửi không tồn tại");
            }

            double currentBalance = rs.getDouble("balance");

            if (currentBalance < amount) {
                throw new SQLException("Không đủ số dư để chuyển tiền");
            }

            // Trừ tiền từ tài khoản A
            String deductSQL = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
            PreparedStatement deductStmt = conn.prepareStatement(deductSQL);
            deductStmt.setDouble(1, amount);
            deductStmt.setInt(2, fromAccountId);
            deductStmt.executeUpdate();

            // Cộng tiền vào tài khoản B
            String addSQL = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
            PreparedStatement addStmt = conn.prepareStatement(addSQL);
            addStmt.setDouble(1, amount);
            addStmt.setInt(2, toAccountId);
            int rowsAffected = addStmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Tài khoản nhận không tồn tại");
            }

            // Nếu mọi thứ đều ổn, commit thay đổi
            conn.commit();
            System.out.println("Chuyển tiền thành công!");

        } catch (SQLException e) {
            System.out.println("Lỗi xảy ra: " + e.getMessage());
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
