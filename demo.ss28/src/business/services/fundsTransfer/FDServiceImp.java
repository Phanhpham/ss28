package business.services.fundsTransfer;

import business.dao.fundsTransfer.FDDAO;
import business.dao.fundsTransfer.FDDAOImp;
import business.model.FundsTransfer;

import java.time.LocalDate;
import java.util.List;

public class FDServiceImp implements FDService{
    private FDDAO dao;

    public FDServiceImp() {
        this.dao = new FDDAOImp();
    }

    @Override
    public List<FundsTransfer> getAllTransfers() {
        return dao.getAllTransfers();
    }

    @Override
    public double getTotalAmountTransferred(LocalDate start, LocalDate end) {
        return dao.getTotalAmountTransferred(start, end);
    }

    @Override
    public double getTotalReceivedByAccount(int accountId, String accountName) {
        return  dao.getTotalReceivedByAccount(accountId, accountName);
    }

    @Override
    public long countSuccessfulTransfers(LocalDate start, LocalDate end) {
        return dao.countSuccessfulTransfersBetweenDates(start, end);
    }
}