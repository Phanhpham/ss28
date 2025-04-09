package business.dao.account;

import business.model.Account;

import java.util.List;

public interface AccountDAO {
    List<Account> getAllAccounts();
    boolean createAccount(int id, String name, double balance, String status);
    Account getAccountByIdAndName(int id, String name);
    boolean updateAccount(int id, String newName, String newStatus);
    boolean deleteAccount(int id);
    int fundsTransfer(int accSenderId, String accSenderName, int accReceiverId, String accReceiverName, double amount);
}