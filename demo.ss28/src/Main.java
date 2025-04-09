import presentation.AccountUI;
import presentation.FundsTransferUI;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        displayMenu(sc);
    }
    public static void displayMenu(Scanner sc) {
        do {
            System.out.println("****************MENNU******************");
            System.out.println("1. Account Service");
            System.out.println("2. Funds Transfer");
            System.out.println("3. Exit");
            System.out.println("Lựa chọn của bạn: ");
            int choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1:
                    AccountUI.displayAccountMenu(sc);
                    break;
                case 2:
                    FundsTransferUI.displayFundsTransferMenu(sc);
                    break;
                case 3:
                    System.exit(0);
                default:
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng chọn lại: ");
            }
        }while (true);
    }
}
