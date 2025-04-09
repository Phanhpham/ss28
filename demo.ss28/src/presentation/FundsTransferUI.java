package presentation;

import business.model.FundsTransfer;
import business.services.fundsTransfer.FDService;
import business.services.fundsTransfer.FDServiceImp;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class FundsTransferUI {
    public static void displayFundsTransferMenu(Scanner scanner){
        FDService service = new FDServiceImp();
        boolean continueMenu = true;

        while (continueMenu) {
            System.out.println("*****************FT MENU***************");
            System.out.println("1. Lịch sử giao dịch");
            System.out.println("2. Thống kê số tiền chuyển trong khoảng từ ngày đến ngày");
            System.out.println("3. Thống kê số tiền nhận theo tài khoản");
            System.out.println("4. Thống kê số giao dịch thành công từ ngày đến ngày");
            System.out.println("5. Thoát");
            System.out.print("Lựa chọn của bạn: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    listAllTransactions(service);
                    break;
                case 2:
                    totalTransferredBetweenDates(scanner, service);
                    break;
                case 3:
                    totalReceivedByAccount(scanner, service);
                    break;
                case 4:
                    countSuccessfulTransactions(scanner, service);
                    break;
                case 5:
                    continueMenu = false;
                    System.out.println("Đã thoát khỏi menu.");
                    break;
                default:
                    System.err.println("Vui lòng chọn từ 1 đến 5.");
            }
        }
    }

    /** 1. Hiển thị toàn bộ lịch sử giao dịch */
    private static void listAllTransactions(FDService service) {
        List<FundsTransfer> list = service.getAllTransfers();
        if (list == null || list.isEmpty()) {
            System.out.println("Không có giao dịch nào.");
        } else {
            System.out.println("Danh sách giao dịch:");
            for (FundsTransfer f : list) {
                System.out.println(f);
            }
        }
    }

    /** 2. Thống kê tổng tiền chuyển trong khoảng thời gian */
    private static void totalTransferredBetweenDates(Scanner scanner, FDService service) {
        System.out.println("Nhập ngày bắt đầu (yyyy-MM-dd):");
        LocalDate startDate = LocalDate.parse(scanner.nextLine());
        System.out.println("Nhập ngày kết thúc (yyyy-MM-dd):");
        LocalDate endDate = LocalDate.parse(scanner.nextLine());
        double total = service.getTotalAmountTransferred(startDate, endDate);
        System.out.println("Tổng số tiền đã chuyển: " + total);
    }

    /** 3. Thống kê tổng tiền nhận theo tài khoản */
    private static void totalReceivedByAccount(Scanner scanner, FDService service) {
        System.out.println("Nhập ID tài khoản:");
        int accId = Integer.parseInt(scanner.nextLine());
        System.out.println("Nhập tên tài khoản:");
        String accName = scanner.nextLine();
        double total = service.getTotalReceivedByAccount(accId, accName);
        System.out.println("Tổng tiền đã nhận: " + total);
    }

    /** 4. Thống kê số giao dịch thành công trong khoảng thời gian */
    private static void countSuccessfulTransactions(Scanner scanner, FDService service) {
        System.out.println("Nhập ngày bắt đầu (yyyy-MM-dd):");
        LocalDate startDate = LocalDate.parse(scanner.nextLine());
        System.out.println("Nhập ngày kết thúc (yyyy-MM-dd):");
        LocalDate endDate = LocalDate.parse(scanner.nextLine());
        long count = service.countSuccessfulTransfers(startDate, endDate);
        System.out.println("Số giao dịch thành công: " + count);
    }
}