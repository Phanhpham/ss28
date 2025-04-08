import java.sql.*;

public class B9 {
    static final String URL = "jdbc:mysql://localhost:3306/auction_system";
    static final String USER = "root";
    static final String PASSWORD = "12345678";

    public static void main(String[] args) {
        int userId = 1;
        int auctionId = 1;
        double bidAmount = 1500.00;

        try {
            placeBid(userId, auctionId, bidAmount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void placeBid(int userId, int auctionId, double bidAmount) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            // Kiểm tra số dư người dùng
            PreparedStatement balanceStmt = conn.prepareStatement("SELECT balance FROM users WHERE user_id = ?");
            balanceStmt.setInt(1, userId);
            ResultSet balanceRs = balanceStmt.executeQuery();

            if (!balanceRs.next()) {
                logFailedBid(conn, auctionId, userId, bidAmount, "Người dùng không tồn tại");
                conn.commit();
                System.out.println(" Người dùng không tồn tại.");
                return;
            }

            double balance = balanceRs.getDouble("balance");
            if (balance < bidAmount) {
                logFailedBid(conn, auctionId, userId, bidAmount, "Không đủ số dư");
                conn.commit();
                System.out.println("Không đủ số dư.");
                return;
            }

            // Kiểm tra đấu giá có tồn tại không và lấy highest_bid
            PreparedStatement auctionStmt = conn.prepareStatement("SELECT highest_bid, status FROM auctions WHERE auction_id = ?");
            auctionStmt.setInt(1, auctionId);
            ResultSet auctionRs = auctionStmt.executeQuery();

            if (!auctionRs.next()) {
                logFailedBid(conn, auctionId, userId, bidAmount, "Phiên đấu giá không tồn tại");
                conn.commit();
                System.out.println(" Phiên đấu giá không tồn tại.");
                return;
            }

            double highestBid = auctionRs.getDouble("highest_bid");
            String status = auctionRs.getString("status");

            if (!"OPEN".equalsIgnoreCase(status)) {
                logFailedBid(conn, auctionId, userId, bidAmount, "Phiên đấu giá đã đóng");
                conn.commit();
                System.out.println(" Phiên đấu giá đã kết thúc.");
                return;
            }

            if (bidAmount <= highestBid) {
                logFailedBid(conn, auctionId, userId, bidAmount, "Giá đặt thấp hơn giá cao nhất hiện tại");
                conn.commit();
                System.out.println(" Giá đặt không hợp lệ.");
                return;
            }

            // Cập nhật highest_bid
            PreparedStatement updateAuction = conn.prepareStatement("UPDATE auctions SET highest_bid = ? WHERE auction_id = ?");
            updateAuction.setDouble(1, bidAmount);
            updateAuction.setInt(2, auctionId);
            updateAuction.executeUpdate();

            // 4. Thêm vào bảng bids
            PreparedStatement insertBid = conn.prepareStatement("INSERT INTO bids (auction_id, user_id, bid_amount) VALUES (?, ?, ?)");
            insertBid.setInt(1, auctionId);
            insertBid.setInt(2, userId);
            insertBid.setDouble(3, bidAmount);
            insertBid.executeUpdate();

            conn.commit();
            System.out.println("Đặt giá thành công!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(" Giao dịch thất bại, rollback...");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void logFailedBid(Connection conn, int auctionId, int userId, double bidAmount, String reason) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO failed_bids (auction_id, user_id, bid_amount, reason) VALUES (?, ?, ?, ?)");
        stmt.setInt(1, auctionId);
        stmt.setInt(2, userId);
        stmt.setDouble(3, bidAmount);
        stmt.setString(4, reason);
        stmt.executeUpdate();
    }
}
