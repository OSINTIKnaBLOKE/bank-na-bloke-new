package TerminalBankVZ;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Scanner;

public class BankNaBloke {
    private static final String NAME_COMPANY = "NA BLOKE";
    private static final HashMap<Double, String> HISTORY = new HashMap<>();
    private static final HashMap<BankAccount, LocalDateTime> HISTORY_USER = new HashMap<>();
    private static double MY_BALANCE = 1000.0;

    static void main() {
        Scanner scanner = new Scanner(System.in);
        HashMap<String, BankAccount> userBank = new HashMap<>();
        loadDatabase(userBank);

        boolean isActive = true;
        System.out.println("Добро пожаловать в банк " + NAME_COMPANY);
        // TODO регистрацию потом
        register(scanner, userBank);

        String menu = """
                1. Пополнить баланс              6. Показать баланс пользователя
                2. Снять наличные                7. Заблокировать пользователя
                3. Показать историю операций     8. Перевести деньги другому пользователю
                4. Показать баланс               9. Выход
                5. Показать пользователей        10. Перевод между пользователями          
        """;

        while (isActive) {
            // FIXME: Формат времини
            LocalDateTime time = LocalDateTime.now();
            System.out.println(" ");
            System.out.println("                               === " + NAME_COMPANY + " ===");
            System.out.println(menu);
            System.out.print("Выберите действие (введите цифру действия): ");
            int inputMenu = scanner.nextInt();
            scanner.nextLine();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm.ss");

            switch (inputMenu) {
                // 1. Пополнить баланс (себе)
                case 1:
                    depositAmount(scanner, time);
                    break;
                // 2. Снять наличные
                case 2:
                    anAmountMoney(scanner, time);
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
                    databaseUser(userBank);
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
                    System.out.println(transactionMeAndUser(userBank, scanner, time));
                    break;
                // 9. Выход
                case 9:
                    System.err.println("Выход из системы...");
                    saveDatabase(userBank, time);
                    isActive = false;
                    break;
                case 10:
                    transactionUserForUser(userBank, scanner);
                default:
                    System.err.println("Неизвестная операция " + TransactionStatusVZ.STATUS_ERROR);
            }
        }

    }

    // переделать переводы
    private static void transactionUserForUser(HashMap<String, BankAccount> userBank, Scanner scanner) {
        loadDatabase(userBank);
        System.out.println("Кто переводит?");
        String inputUser = scanner.nextLine();
        System.out.println("Кому?");
        String inputUser1 = scanner.nextLine();
        if (userBank.containsKey(inputUser)) {
            if (userBank.containsKey(inputUser1)) {
                BankAccount user = userBank.get(inputUser);
                BankAccount user1 = userBank.get(inputUser1);

                fundsTransfer(scanner, user, user1);
            } else {
                System.err.println(TransactionStatusVZ.STATUS_USER_ABSENCE);
            }
        } else {
            System.err.println(TransactionStatusVZ.STATUS_USER_ABSENCE);
        }
    }

    private static void register(Scanner scanner, HashMap<String, BankAccount> userBank) {
        System.out.println("Введите ваше имя для регистрации: ");
        String accOwner = scanner.nextLine();

        if (accOwner.trim().isEmpty()) {
            System.err.println("Ошибка! Имя не может быть пустым!");
            System.err.println(TransactionStatusVZ.STATUS_ERROR);
            return;
        }

        if (userBank.containsKey(accOwner)) {
            System.err.println("Ошибка! пользователь с таким именем уже существует!");
            System.err.println(TransactionStatusVZ.STATUS_ERROR);
            return;
        }

        BankAccount newUserBank = new BankAccount(accOwner, MY_BALANCE); // отталкиваться от этого наверное
        userBank.put(accOwner, newUserBank);
        System.out.println("Пользователь " + accOwner + " Успешно зарегистрирован!");
        System.out.println(TransactionStatusVZ.STATUS_SUCCESSFULLY);
    }

    private static TransactionStatusVZ transactionMeAndUser(HashMap<String, BankAccount> userBank,
                                                            Scanner scanner, LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        String text = localDateTime.format(formatter);
        loadDatabase(userBank);
        System.out.print("Кому перевести деньги? : ");
        String userNameTransaction = scanner.nextLine();

        if (userBank.containsKey(userNameTransaction)) {
            System.out.print("Сколько перевести? : ");
            double transaction = scanner.nextDouble();
            scanner.nextLine();

            if (transaction <= MY_BALANCE) {
                MY_BALANCE -= transaction;
                HISTORY.put(-transaction, text);

                BankAccount currentRecipientBalance = userBank.get(userNameTransaction);
                currentRecipientBalance.addMoney(transaction);

                userBank.put(userNameTransaction, currentRecipientBalance);
            } else {
                return TransactionStatusVZ.INSUFFICIENT_FIND;
            }
        }
        return TransactionStatusVZ.STATUS_USER_ABSENCE;
    }

    private static void databaseUser(HashMap<String, BankAccount> user){
        Path path = Path.of("src/TerminalBankVZ/bankDatabase.txt");

        try {
            java.util.List<String> list = Files.readAllLines(path);

            for (String line : list) {
                // пропускаем пустые строки
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(";");
                String name = data[0];
                double balance = Double.parseDouble(data[1]);
                System.out.println("Имя " + name + " Баланс " + balance);
            }

            System.out.println("Пользователей в базе " + list.size());
        } catch (Exception e) {
            System.err.println(TransactionStatusVZ.STATUS_ERROR);
        }
    }

    // 3. Показать историю операций
    private static void forOperation() {
        System.out.println("Список всех операций");
        for (var integer : HISTORY.entrySet()) {
            System.out.println(integer.getKey() + " - " + integer.getValue());
        }
    }

    // 2. снять наличные
    private static void anAmountMoney(Scanner scanner, LocalDateTime localDateTime) {
        System.out.println("Сколько снять?: ");
        double anAmountMoney = scanner.nextDouble();
        scanner.nextLine();
        anAmountTransaction(anAmountMoney, localDateTime);
    }

    // 6. Показать баланс пользователей
    private static void userBankBalance(HashMap<String, BankAccount> userBank) {
        for (var ent : userBank.entrySet()) {
            System.out.println(ent.getKey() + " " + ent.getValue().getBalance());
        }
    }

    // 1. Пополнить баланс
    private static void depositAmount(Scanner scanner, LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        String text = localDateTime.format(formatter);
        System.out.print("Пополнить баланс на ");
        double depositAmount = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Деньги усмешно зачислены: " + depStatusVZ(depositAmount, localDateTime));
    }

    // Проверкка на снятие
    // доп ко 2
    private static TransactionStatusVZ anAmountTransaction(double anAmountMoney, LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        if (anAmountMoney > MY_BALANCE) {
            return TransactionStatusVZ.INSUFFICIENT_FIND;
        }
        if (anAmountMoney >= 50_000) {
            return TransactionStatusVZ.LIMIT_EXCEEDED;
        }

        String txt = localDateTime.format(formatter);

        MY_BALANCE -= anAmountMoney;
        HISTORY.put(-anAmountMoney, txt);
        return TransactionStatusVZ.STATUS_SUCCESSFULLY;
    }

    // доп к 1
    private static TransactionStatusVZ depStatusVZ(double depAmount, LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        if (depAmount < 0) {
            return TransactionStatusVZ.INVALID_AMOUNT;
        }
        if (depAmount >= 50_000) {
            return TransactionStatusVZ.INVALID_AMOUNT;
        }
        String text = localDateTime.format(formatter);
        MY_BALANCE += depAmount;
        HISTORY.put(depAmount, text);
        return TransactionStatusVZ.STATUS_SUCCESSFULLY;
    }

    // 7. Заблокировать пользователя
    private static TransactionStatusVZ banUserMode(Scanner scanner, HashMap<String, BankAccount> userBank) {
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

    private static double getMyBalance() {
        return MY_BALANCE;
    }

    private static void fundsTransfer(Scanner scanner, BankAccount user, BankAccount user1) {
        System.out.println("Пользователь " + user.getAccountOwner() + " Переводит деньги " + user1.getAccountOwner());
        System.out.print("Сумма: ");
        double deposit = scanner.nextDouble();

        if (deposit > 0 && deposit <= 50_000) {
            if (user.getBalance() > 0) {
                user.subtractMoney(deposit);
                user1.addMoney(deposit);

                System.out.println(TransactionStatusVZ.STATUS_SUCCESSFULLY);
            } else {
                System.err.println(TransactionStatusVZ.INSUFFICIENT_FIND);
            }
        } else {
            System.out.println(TransactionStatusVZ.INVALID_AMOUNT);
        }
    }

    private static void saveDatabase(HashMap<String, BankAccount> userBank, LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        Path path = Path.of("src/TerminalBankVZ/bankDatabase.txt");
        StringBuilder sb = new StringBuilder();

        String text = localDateTime.format(formatter);

        for (BankAccount account : userBank.values()) {
            sb.append(account.getAccountOwner())
                    .append(";")
                    .append(account.getBalance())
                    .append(";")
                    .append(text)
                    .append("\n");
        }

        try {
            Files.writeString(path, sb.toString());
            System.out.println("База данных успешно сохранена в файл!");
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении базы: " + e.getMessage());
        }
    }

    private static void loadDatabase(HashMap<String, BankAccount> userBank) {
        Path path = Path.of("src/TerminalBankVZ/bankDatabase.txt");

        if (!Files.exists(path)) {
            System.err.println("Файл баззы данных не найден \n");
            System.err.println("Был создан новый файл");
            return;
        }

        try {
            java.util.List<String> list = Files.readAllLines(path);

            for (String line : list) {
                // пропускаем пустые строки
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(";");
                String name = data[0];
                double balance = Double.parseDouble(data[1]);

                userBank.put(name, new BankAccount(name, balance));
            }

        } catch (IOException e) {
            System.err.println("Ошибка при загрузке файла " + e.getMessage());
        }
    }

    //сделать подписку на больший перевод
    private static void transactionHistory(BankAccount userBank){

    }
}