import java.sql.*;

public class B6 {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/company";
        String user = "root";
        String password = "12345678";

        Connection conn = null;

        try {
            conn = DriverManager.getConnection(url, user, password);
            conn.setAutoCommit(false);

            //  Thêm phòng ban mới
            String insertDeptSQL = "INSERT INTO departments (id, name) VALUES (?, ?)";
            PreparedStatement deptStmt = conn.prepareStatement(insertDeptSQL);
            int deptId = 10;
            deptStmt.setInt(1, deptId);
            deptStmt.setString(2, "Phòng Kỹ Thuật");
            deptStmt.executeUpdate();

            // Thêm nhiều nhân viên thuộc phòng ban đó
            String insertEmpSQL = "INSERT INTO employees (id, name, department_id) VALUES (?, ?, ?)";
            PreparedStatement empStmt = conn.prepareStatement(insertEmpSQL);

            // Nhân viên 1
            empStmt.setInt(1, 101);
            empStmt.setString(2, "Nguyen Van A");
            empStmt.setInt(3, deptId);
            empStmt.executeUpdate();

            // Nhân viên 2
            empStmt.setInt(1, 102);
            empStmt.setString(2, "Tran Thi B");
            empStmt.setInt(3, deptId);
            empStmt.executeUpdate();

            // Nếu mọi thứ hợp lệ thì Commit
            conn.commit();
            System.out.println(" Đã thêm phòng ban và nhân viên thành công!");

        } catch (SQLException e) {
            System.out.println(" Lỗi: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println(" Giao dịch đã bị rollback do lỗi.");
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
