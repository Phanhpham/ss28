package business.services.fundsTransfer;

import business.model.FundsTransfer;

import java.time.LocalDate;
import java.util.List;

public interface FDService {
    List<FundsTransfer> getAllTransfers();

    double getTotalAmountTransferred(LocalDate start, LocalDate end);

    double getTotalReceivedByAccount(int accountId, String accountName);;

    long countSuccessfulTransfers(LocalDate start, LocalDate end);
}