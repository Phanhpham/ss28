package business.services.account;

import business.dao.account.AccountDAO;
import business.dao.account.AccountDAOImp;
import business.model.Account;

import java.util.List;

public class AccountServiceImp implements AccountService{
    private final AccountDAO accountDao;

    public AccountServiceImp() {
        accountDao = new AccountDAOImp();
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountDao.getAllAccounts();
    }

    @Override
    public boolean createAccount(int id, String name, double balance, String status) {
        return accountDao.createAccount(id, name, balance, status);
    }

    @Override
    public Account getAccountByIdAndName(int id, String name) {
        return accountDao.getAccountByIdAndName(id, name);
    }

    @Override
    public boolean updateAccount(int id, String newName, String newStatus) {
        return accountDao.updateAccount(id, newName, newStatus);
    }

    @Override
    public boolean deleteAccount(int id) {
        return accountDao.deleteAccount(id);
    }

    @Override
    public int fundsTransfer(int accSenderId, String accSenderName, int accReceiverId, String accReceiverName, double amount) {
        return accountDao.fundsTransfer(accSenderId, accSenderName, accReceiverId, accReceiverName, amount);
    }
}