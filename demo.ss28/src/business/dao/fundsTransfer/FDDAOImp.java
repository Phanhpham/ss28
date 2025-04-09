package business.dao.fundsTransfer;

import business.config.ConnectionDB;
import business.model.FundsTransfer;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FDDAOImp implements FDDAO {

    @Override
    public List<FundsTransfer> getAllTransfers() {
        List<FundsTransfer> list = new ArrayList<>();
        Connection conn = null;
        CallableStatement callSt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionDB.openConnection();
            callSt = conn.prepareCall("{call get_all_funds_transfer()}");
            rs = callSt.executeQuery();
            while (rs.next()) {
                FundsTransfer ft = new FundsTransfer(
                        rs.getInt("acc_sender_id"),
                        rs.getInt("acc_reciver_id"),
                        rs.getDouble("fd_amount"),
                        rs.getDate("fd_created").toLocalDate(),
                        rs.getBoolean("fd_status")
                );
                list.add(ft);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách giao dịch: " + e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            ConnectionDB.closeConnection(conn, callSt);
        }
        return list;
    }

    @Override
    public double getTotalAmountTransferred(LocalDate startDate, LocalDate endDate) {
        double total = 0.0;
        Connection conn = null;
        CallableStatement callSt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionDB.openConnection();
            callSt = conn.prepareCall("{call total_transfer_amount_by_date_range(?, ?)}");
            callSt.setDate(1, Date.valueOf(startDate));
            callSt.setDate(2, Date.valueOf(endDate));
            rs = callSt.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total_transferred");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thống kê tổng tiền chuyển: " + e.getMessage());
        } finally {
            ConnectionDB.closeConnection(conn, callSt);
        }
        return total;
    }

    @Override
    public double getTotalReceivedByAccount(int accountId, String accountName) {
        double total = 0;
        Connection conn = null;
        CallableStatement callSt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionDB.openConnection();
            callSt = conn.prepareCall("{call total_received_by_account(?)}");
            callSt.setInt(1, accountId);
            rs = callSt.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total_received");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thống kê số tiền nhận: " + e.getMessage());
        } finally {
            ConnectionDB.closeConnection(conn, callSt);
        }
        return total;
    }



    @Override
    public long countSuccessfulTransfersBetweenDates(LocalDate startDate, LocalDate endDate) {
        long count = 0;
        Connection conn = null;
        CallableStatement callSt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionDB.openConnection();
            callSt = conn.prepareCall("{call successful_transaction_count_by_date_range(?, ?)}");
            callSt.setDate(1, Date.valueOf(startDate));
            callSt.setDate(2, Date.valueOf(endDate));
            rs = callSt.executeQuery();
            if (rs.next()) {
                count = rs.getLong("successful_transactions");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thống kê giao dịch thành công: " + e.getMessage());
        } finally {
            ConnectionDB.closeConnection(conn, callSt);
        }
        return count;
    }

}