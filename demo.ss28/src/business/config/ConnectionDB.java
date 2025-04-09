package business.config;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    private static final String URL = "jdbc:mysql://localhost:3306/Banking_DB";
    private static final String USER_NAME = "root";
    private static final String PASSWORD = "new_password";

    public static Connection openConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối CSDL do: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Có lỗi không xác định khi kết nối CSDL: " + e.getMessage());
        }
        return conn;
    }
    public static void closeConnection(Connection conn, CallableStatement callSt){
        if (conn!=null){
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (callSt!=null){
            try {
                callSt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}