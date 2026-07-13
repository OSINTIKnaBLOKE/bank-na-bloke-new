package TerminalBankVZ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class BankNaBloke {
    private static final String NAME_COMPANY = "NA BLOKE";
    private final static ArrayList<Integer> history = new ArrayList<>();
    private static double MY_BALANCE = 1000.0;

    static void main() {
        Scanner scanner = new Scanner(System.in);
        HashMap<String, Integer> userBank = new HashMap<>();
        userBank.put("Алиса", 500);
        userBank.put("Иван", 500);
        userBank.put("Кирилл", 500);
        userBank.put("София", 500);

        boolean isActive = true;
        System.out.println("Добро пожаловать в банк " + NAME_COMPANY);
        String menu = """
                1. Пополнить баланс
                2. Снять наличные
                3. Показать историю операций
                4. Показать баланс
                5. Показать пользователей
                6. Показать баланс пользователя
                7. Заблокировать пользователя
                8. Перевести деньги другому пользователю
                9. Выход
                """;

        while (isActive) {
            System.out.println(" ");
            System.out.println("=== " + NAME_COMPANY + " ===");
            System.out.println(menu);
            System.out.print("Выберите действие (введите цифру действия): ");
            int inputMenu = scanner.nextInt();
            scanner.nextLine();

            switch (inputMenu) {
                // 1. Пополнить баланс (себе)
                case 1:
                    depositAmount(scanner);
                    break;
                // 2. Снять наличные
                case 2:
                    anAmountMoney(scanner);
                    break;
                // 3. Показать историю операций
                case 3:
                    forOperation();
                    break;
                // 4. Показать баланс
                case 4:
                    System.out.println(getMyBalance());
                    break;
                // 5. Показать пользователей
                case 5:
                    userBankName(userBank);
                    break;
                // 6. Показать баланс пользователей
                case 6:
                    userBankBalance(userBank);
                    break;
                // 7. Заблокировать пользователя
                case 7:
                    System.out.println(banUserMode(scanner, userBank));
                    break;
                // 8. Перевести деньги другому пользователю
                case 8:
                    System.out.println(transactionMeAndUser(userBank, scanner));
                    break;
                // 9. Выход
                case 9:
                    System.err.println("Выход из системы...");
                    isActive = false;
                    break;
                default:
                    System.err.println("Неизвестная операция " + TransactionStatusVZ.STATUS_ERROR);
            }
        }

    }

    private static TransactionStatusVZ transactionMeAndUser(HashMap<String, Integer> userBank, Scanner scanner) {
        userBankName(userBank);
        System.out.print("Кому перевести деньги? : ");
        String userNameTransaction = scanner.nextLine();
        if (userBank.containsKey(userNameTransaction)) {
            System.out.print("Сколько перевести? : ");
            int transaction = scanner.nextInt();
            scanner.nextLine();
            if (transaction <= MY_BALANCE) {
                MY_BALANCE -= transaction;
                history.add(-transaction);
                int currentRecipientBalance = userBank.get(userNameTransaction);
                userBank.put(userNameTransaction, currentRecipientBalance + transaction);
            } else {
                return TransactionStatusVZ.INSUFFICIENT_FIND;
            }
        }
        return TransactionStatusVZ.STATUS_USER_ABSENCE;
    }

    // 3. Показать историю операций
    private static void forOperation() {
        System.out.println("Список всех операций");
        for (Integer integer : history) {
            System.out.println(integer);
        }
    }

    // 2. снять наличные
    private static void anAmountMoney(Scanner scanner) {
        System.out.println("Сколько снять?: ");
        int anAmountMoney = scanner.nextInt();
        scanner.nextLine();
        anAmountTransaction(anAmountMoney);
    }

    // 6. Показать баланс пользователей
    private static void userBankBalance(HashMap<String, Integer> userBank) {
        for (var ent : userBank.entrySet()) {
            System.out.println(ent.getKey() + "-> " + ent.getValue());
        }
    }

    // 5. показать пользователей
    private static void userBankName(HashMap<String, Integer> userBank) {
        for (var ent : userBank.entrySet()) {
            System.out.println("-> " + ent.getKey());
        }
    }

    // 1. Пополнить баланс
    private static void depositAmount(Scanner scanner) {
        System.out.print("Пополнить баланс на ");
        int depositAmount = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Деньги усмешно зачислены: " + depStatusVZ(depositAmount));
    }

    // Проверкка на снятие
    // доп ко 2
    private static TransactionStatusVZ anAmountTransaction(int anAmountMoney) {
        if (anAmountMoney > MY_BALANCE) {
            return TransactionStatusVZ.INSUFFICIENT_FIND;
        }
        if (anAmountMoney >= 50_000) {
            return TransactionStatusVZ.LIMIT_EXCEEDED;
        }
        MY_BALANCE -= anAmountMoney;
        history.add(-anAmountMoney);
        return TransactionStatusVZ.STATUS_SUCCESSFULLY;
    }

    // доп к 1
    private static TransactionStatusVZ depStatusVZ(int depAmount) {
        if (depAmount < 0) {
            return TransactionStatusVZ.INVALID_AMOUNT;
        }
        if (depAmount >= 50_000) {
            return TransactionStatusVZ.INVALID_AMOUNT;
        }
        MY_BALANCE += depAmount;
        history.add(depAmount);
        return TransactionStatusVZ.STATUS_SUCCESSFULLY;
    }

    // 7. Заблокировать пользователя
    private static TransactionStatusVZ banUserMode(Scanner scanner, HashMap<String, Integer> userBank) {
        System.out.print("Заблокировать пользователя: ");
        String bannedUserBank = scanner.nextLine();
        // Если пользователь найдем убераем его из списка
        // Иначе просто отсутствие пользователя
        if (userBank.containsKey(bannedUserBank)) {
            userBank.remove(bannedUserBank);
            return TransactionStatusVZ.STATUS_SUCCESSFULLY;
        }
        return TransactionStatusVZ.STATUS_USER_ABSENCE;
    }

    public static double getMyBalance() {
        return MY_BALANCE;
    }
}