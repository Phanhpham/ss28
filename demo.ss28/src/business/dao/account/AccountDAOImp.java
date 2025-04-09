package business.dao.account;

import business.config.ConnectionDB;
import business.model.Account;
import business.model.AccountStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAOImp implements AccountDAO{
    public List<Account> getAllAccounts() {
        Connection conn = null;
        CallableStatement callSt = null;
        ResultSet rs = null;
        List<Account> accounts = new ArrayList<>();
        try {
            // 1. Khởi tạo đối tượng Connection
            conn = ConnectionDB.openConnection();
            // 2. Khởi tạo đối tượng CallableStatement
            callSt = conn.prepareCall("{call get_all_accounts()}");
            // 3. Thực hiện gọi procedure
            rs = callSt.executeQuery();
            // 4. Xử lý dữ liệu nhận được
            while (rs.next()) {
                String statusStr = rs.getString("acc_status");
                AccountStatus status = null;
                if (statusStr != null) {
                    try {
                        status = AccountStatus.valueOf(statusStr.toUpperCase()); // hoặc giữ nguyên tùy enum
                    } catch (IllegalArgumentException e) {
                        System.out.println("Trạng thái không hợp lệ: " + statusStr);
                    }
                }
                Account account = new Account(rs.getInt("acc_id"), rs.getString("acc_name"),
                        rs.getDouble("acc_balance"), status);
                accounts.add(account);
            }
            return accounts;
        } catch (SQLException e) {
            System.err.println("Có lỗi xảy ra khi lấy danh sách tài khoản: " + e.getMessage());
            return accounts; // Trả về danh sách rỗng nếu có lỗi
        } finally {
            // 5. Đóng kết nối
            ConnectionDB.closeConnection(conn, callSt);
        }
    }

    @Override
    public Account getAccountByIdAndName(int id, String name) {
        Connection conn = null;
        CallableStatement callSt = null;
        ResultSet rs = null;
        try {
            // 1. Khởi tạo đối tượng Connection
            conn = ConnectionDB.openConnection();
            // 2. Khởi tạo đối tượng CallableStatement
            callSt = conn.prepareCall("{call get_account_by_id_and_name(?, ?)}");
            // 3. Set giá trị cho các tham số vào
            callSt.setInt(1, id);
            callSt.setString(2, name);
            // 4. Thực hiện gọi procedure
            rs = callSt.executeQuery();
            // 5. Xử lý dữ liệu nhận được
            if (rs.next()) {
                AccountStatus status = AccountStatus.valueOf(rs.getString("acc_status").toUpperCase());
                Account account = new Account(rs.getInt("acc_id"), rs.getString("acc_name"), rs.getDouble("acc_balance"), status);
                return account;
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Có lỗi xảy ra khi lấy tài khoản: " + e.getMessage());
            return null;
        } finally {
            // 6. Đóng kết nối
            ConnectionDB.closeConnection(conn, callSt);
        }
    }

    @Override
    public boolean createAccount(int id, String name, double balance, String status) {
        Connection conn = null;
        CallableStatement callSt = null;
        try {
            conn = ConnectionDB.openConnection();
            conn.setAutoCommit(false);

            callSt = conn.prepareCall("{call create_account(?, ?, ?, ?)}");
            callSt.setInt(1, id);
            callSt.setString(2, name);
            callSt.setDouble(3, balance);
            callSt.setString(4, status);

            int rowsAffected = callSt.executeUpdate();
            conn.commit();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Có lỗi khi tạo tài khoản, rollback lại dữ liệu: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            ConnectionDB.closeConnection(conn, callSt);
        }
    }

    @Override
    public boolean updateAccount(int id, String newName, String newStatus) {
        Connection conn = null;
        CallableStatement callSt = null;
        try {
            conn = ConnectionDB.openConnection();
            conn.setAutoCommit(false);

            callSt = conn.prepareCall("{call update_account(?, ?, ?)}");
            callSt.setInt(1, id);
            callSt.setString(2, newName);
            callSt.setString(3, newStatus);

            int rowsAffected = callSt.executeUpdate();
            conn.commit();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật tài khoản, rollback: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            ConnectionDB.closeConnection(conn, callSt);
        }
    }

    @Override
    public boolean deleteAccount(int id) {
        Connection conn = null;
        CallableStatement callSt = null;
        try {
            conn = ConnectionDB.openConnection();
            conn.setAutoCommit(false);

            callSt = conn.prepareCall("{call delete_account(?)}");
            callSt.setInt(1, id);

            int rowsAffected = callSt.executeUpdate();
            conn.commit();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa tài khoản, rollback: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            ConnectionDB.closeConnection(conn, callSt);
        }
    }


    @Override
    public int fundsTransfer(int accSenderId, String accSenderName, int accReceiverId, String accReceiverName, double amount) {
        Connection conn = null;
        CallableStatement callSt = null;
        try {
            conn = ConnectionDB.openConnection();
            conn.setAutoCommit(false);

            System.out.println("Gọi procedure chuyển khoản: " + accSenderId + " -> " + accReceiverId + ", amount: " + amount);
            callSt = conn.prepareCall("{call funds_transfer_amount(?,?,?,?,?,?)}");
            callSt.setInt(1, accSenderId);
            callSt.setString(2, accSenderName);
            callSt.setInt(3, accReceiverId);
            callSt.setString(4, accReceiverName);
            callSt.setDouble(5, amount);
            callSt.registerOutParameter(6, Types.INTEGER);

            callSt.execute();
            int result = callSt.getInt(6);
            conn.commit();
            return result;
        } catch (SQLException e) {
            System.err.println("Có lỗi xảy ra trong quá trình chuyển khoản, dữ liệu đã được rollback");
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("Lỗi không xác định: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConnectionDB.closeConnection(conn, callSt);
        }
        return 0;
    }

}