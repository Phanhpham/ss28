package business.dao.fundsTransfer;

import business.model.FundsTransfer;

import java.time.LocalDate;
import java.util.List;

public interface FDDAO {
    List<FundsTransfer> getAllTransfers();

    double getTotalAmountTransferred(LocalDate startDate, LocalDate endDate);

    double getTotalReceivedByAccount(int accountId, String accountName);

    long countSuccessfulTransfersBetweenDates(LocalDate startDate, LocalDate endDate);
}