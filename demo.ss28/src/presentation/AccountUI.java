package presentation;

import business.model.Account;
import business.services.account.AccountService;
import business.services.account.AccountServiceImp;

import java.util.List;
import java.util.Scanner;

public class AccountUI {
    public static void displayAccountMenu(Scanner scanner) {
        AccountService accountService = new AccountServiceImp();
        boolean continueMenu = true;
        do {
            System.out.println("***************ACCOUNT MENU**************");
            System.out.println("1. Danh sách tài khoản");
            System.out.println("2. Tạo tài khoản");
            System.out.println("3. Cập nhật tài khoản"); // Tên + trạng thái
            System.out.println("4. Xóa tài khoản"); // Cập nhật trạng thái là inactive
            System.out.println("5. Chuyển khoản");
            System.out.println("6. Tra cứu số dư tài khoản");
            System.out.println("7. Thoát");
            System.out.print("Lựa chọn của bạn: ");
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    listAllAccounts(accountService);
                    break;
                case 2:
                    createAccount(scanner, accountService);
                    break;
                case 3:
                    updateAccount(scanner, accountService);
                    break;
                case 4:
                    deleteAccount(scanner, accountService);
                    break;
                case 5:
                    fundsTransfer(scanner, accountService);
                    break;
                case 6:
                    checkAccountBalance(scanner, accountService);
                    break;
                case 7:
                    continueMenu = false;
                    System.out.println("Đã thoát khỏi menu.");
                    break;
                default:
                    System.err.println("Vui lòng chọn từ 1-7");
            }
        } while (continueMenu);
    }

    private static void listAllAccounts(AccountService accountService) {
        List<Account> accounts = accountService.getAllAccounts();
        if (accounts == null || accounts.isEmpty()) {
            System.out.println("Không có tài khoản nào.");
        } else {
            System.out.println("Danh sách tài khoản:");
            System.out.println("ID\tTên\tSố dư\tTrạng thái");
            for (Account account : accounts) {
                System.out.println(account.getId() + "\t" + account.getName() + "\t" +
                        account.getBalance() + "\t" + account.getStatus());
            }
        }
    }

    /** Tạo một tài khoản mới */
    private static void createAccount(Scanner scanner, AccountService accountService) {
        System.out.println("Nhập ID tài khoản:");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.println("Nhập tên tài khoản:");
        String name = scanner.nextLine();
        System.out.println("Nhập số dư ban đầu:");
        double balance = Double.parseDouble(scanner.nextLine());
        System.out.println("Nhập trạng thái (active/inactive):");
        String status = scanner.nextLine();
        boolean success = accountService.createAccount(id, name, balance, status);
        if (success) {
            System.out.println("Tạo tài khoản thành công.");
        } else {
            System.err.println("Tạo tài khoản thất bại. Có thể ID đã tồn tại.");
        }
    }

    /** Cập nhật thông tin tài khoản */
    private static void updateAccount(Scanner scanner, AccountService accountService) {
        System.out.println("Nhập ID tài khoản cần cập nhật:");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.println("Nhập tên hiện tại của tài khoản:");
        String currentName = scanner.nextLine();
        Account account = accountService.getAccountByIdAndName(id, currentName);
        if (account == null) {
            System.err.println("Thông tin tài khoản không chính xác.");
        } else {
            System.out.println("Nhập tên mới:");
            String newName = scanner.nextLine();
            System.out.println("Nhập trạng thái mới (active/inactive):");
            String newStatus = scanner.nextLine();
            boolean success = accountService.updateAccount(id, newName, newStatus);
            if (success) {
                System.out.println("Cập nhật tài khoản thành công.");
            } else {
                System.err.println("Cập nhật tài khoản thất bại.");
            }
        }
    }

    /** Xóa tài khoản (đặt trạng thái thành inactive) */
    private static void deleteAccount(Scanner scanner, AccountService accountService) {
        System.out.println("Nhập ID tài khoản cần xóa:");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.println("Nhập tên của tài khoản:");
        String name = scanner.nextLine();
        Account account = accountService.getAccountByIdAndName(id, name);
        if (account == null) {
            System.err.println("Thông tin tài khoản không chính xác.");
        } else {
            boolean success = accountService.deleteAccount(id);
            if (success) {
                System.out.println("Xóa tài khoản thành công (đã đặt trạng thái inactive).");
            } else {
                System.err.println("Xóa tài khoản thất bại.");
            }
        }
    }

    public static void fundsTransfer(Scanner scanner, AccountService accountService) {
        System.out.println("Nhập số tài khoản người gửi:");
        int accSenderId = Integer.parseInt(scanner.nextLine());
        System.out.println("Nhập tên tài khoản người gửi:");
        String accSenderName = scanner.nextLine();
        System.out.println("Nhập số tài khoản người nhận:");
        int accReceiverId = Integer.parseInt(scanner.nextLine());
        System.out.println("Nhập tên tài khoản người nhận:");
        String accReceiverName = scanner.nextLine();
        System.out.println("Nhập số tiền chuyển:");
        double amount = Double.parseDouble(scanner.nextLine());
        int result = accountService.fundsTransfer(accSenderId, accSenderName, accReceiverId, accReceiverName, amount);
        switch (result) {
            case 1:
                System.err.println("Thông tin tài khoản người gửi không chính xác");
                break;
            case 2:
                System.err.println("Thông tin tài khoản người nhận không chính xác");
                break;
            case 3:
                System.err.println("Số dư tài khoản không đủ để chuyển khoản");
                break;
            case 4:
                System.out.println("Chuyển khoản thành công!!!");
                break;
        }
    }

    private static void checkAccountBalance(Scanner scanner, AccountService accountService) {
        System.out.println("Nhập ID tài khoản:");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.println("Nhập tên của tài khoản:");
        String name = scanner.nextLine();
        Account account = accountService.getAccountByIdAndName(id, name);
        if (account == null) {
            System.err.println("Thông tin tài khoản không chính xác.");
        } else {
            System.out.println("Số dư tài khoản: " + account.getBalance());
        }
    }
}