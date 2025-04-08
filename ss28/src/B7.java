import java.sql.*;

public class B7 {
    static final String URL = "jdbc:mysql://localhost:3306/isolation_test";
    static final String USER = "root";
    static final String PASSWORD = "12345678";

    public static void main(String[] args) throws Exception {
        // Chạy thử với từng Isolation Level
        testIsolationLevel(Connection.TRANSACTION_READ_UNCOMMITTED, "READ_UNCOMMITTED");
        testIsolationLevel(Connection.TRANSACTION_READ_COMMITTED, "READ_COMMITTED");
        testIsolationLevel(Connection.TRANSACTION_REPEATABLE_READ, "REPEATABLE_READ");
        testIsolationLevel(Connection.TRANSACTION_SERIALIZABLE, "SERIALIZABLE");
    }

    public static void testIsolationLevel(int level, String levelName) throws Exception {
        System.out.println("========== Isolation Level: " + levelName + " ==========");

        try (
                Connection conn1 = DriverManager.getConnection(URL, USER, PASSWORD);
                Connection conn2 = DriverManager.getConnection(URL, USER, PASSWORD)
        ) {
            // Set isolation level for conn1
            conn1.setAutoCommit(false);
            conn1.setTransactionIsolation(level);

            // Truncate bảng và chuẩn bị dữ liệu
            try (Statement clearStmt = conn1.createStatement()) {
                clearStmt.execute("DELETE FROM orders");
                conn1.commit();
            }

            // === Connection 2: Thực hiện INSERT nhưng chưa commit ===
            conn2.setAutoCommit(false);
            PreparedStatement insertStmt = conn2.prepareStatement(
                    "INSERT INTO orders (order_id, customer_name, status) VALUES (?, ?, ?)"
            );
            insertStmt.setInt(1, 1);
            insertStmt.setString(2, "Nguyen Van A");
            insertStmt.setString(3, "Pending");
            insertStmt.executeUpdate();
            System.out.println(">>> Connection 2 đã thực hiện INSERT nhưng chưa commit.");

            // === Connection 1: Thực hiện SELECT ===
            PreparedStatement selectStmt = conn1.prepareStatement("SELECT * FROM orders");
            ResultSet rs = selectStmt.executeQuery();

            System.out.println(">>> Connection 1 SELECT kết quả:");
            boolean hasRow = false;
            while (rs.next()) {
                hasRow = true;
                System.out.printf("  - order_id: %d, name: %s, status: %s\n",
                        rs.getInt("order_id"), rs.getString("customer_name"), rs.getString("status"));
            }

            if (!hasRow) {
                System.out.println("  => Không thấy dữ liệu nào.");
            }

            // Kết thúc: rollback
            conn2.rollback();
            conn1.commit();

            System.out.println(">>> Đã rollback dữ liệu thử nghiệm.\n");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
