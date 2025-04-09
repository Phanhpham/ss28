package business.model;

import java.time.LocalDate;

public class FundsTransfer {
    private int senderId;
    private int receiverId;
    private double amount;
    private LocalDate date;
    private boolean success;

    public FundsTransfer(int senderId, int receiverId, double amount, LocalDate date, boolean success) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.date = date;
        this.success = success;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "FundsTransfer{" +
                "senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", amount=" + amount +
                ", date=" + date +
                ", success=" + success +
                '}';
    }
}