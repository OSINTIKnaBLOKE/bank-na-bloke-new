package TerminalBankVZ;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Scanner;

public class BankNaBloke {
    private static final String NAME_COMPANY = "NA BLOKE";
    private static final HashMap<Double, LocalDateTime> HISTORY = new HashMap<>();
    private static final HashMap<BankAccount, LocalDateTime> HISTORY_USER = new HashMap<>();
    private static double MY_BALANCE = 1000.0;

    static void main() {
        Scanner scanner = new Scanner(System.in);
        HashMap<String, BankAccount> userBank = new HashMap<>();
        loadDatabase(userBank);
        BankAccount userAlice = new BankAccount("Алиса", 1000);
        BankAccount userVanya = new BankAccount("Ваня", 150);
        BankAccount userSofia = new BankAccount("София", 17000);

        userBank.put("Алиса", userAlice);
        userBank.put("Иван", userVanya);
        userBank.put("София", userSofia);

        boolean isActive = true;
        System.out.println("Добро пожаловать в банк " + NAME_COMPANY);
        // TODO регистрацию потом
        register(scanner, userBank);

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
                10. Перевод между пользователями
                """;

        while (isActive) {
            // FIXME: Формат времини
            LocalDateTime time = LocalDateTime.now();
            System.out.println(" ");
            System.out.println("=== " + NAME_COMPANY + " ===");
            System.out.println(menu);
            System.out.print("Выберите действие (введите цифру действия): ");
            int inputMenu = scanner.nextInt();
            scanner.nextLine();


            // TODO список пользователей сделать
            // TODO это чтоб не забыть

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
                    System.out.println(transactionMeAndUser(userBank, scanner, time));
                    break;
                // 9. Выход
                case 9:
                    System.err.println("Выход из системы...");
                    saveDatebase(userBank);
                    isActive = false;
                    break;
                case 10:
                    userBankName(userBank);
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
                default:
                    System.err.println("Неизвестная операция " + TransactionStatusVZ.STATUS_ERROR);
            }
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

        BankAccount newUserBank = new BankAccount(accOwner, 0);
        userBank.put(accOwner, newUserBank);
        System.out.println("Пользователь " + accOwner + " Успешно зарегистрирован!");
        System.out.println(TransactionStatusVZ.STATUS_SUCCESSFULLY);
    }

    private static TransactionStatusVZ transactionMeAndUser(HashMap<String, BankAccount> userBank,
                                                            Scanner scanner, LocalDateTime localDateTime) {
        userBankName(userBank);
        System.out.print("Кому перевести деньги? : ");
        String userNameTransaction = scanner.nextLine();

        if (userBank.containsKey(userNameTransaction)) {
            System.out.print("Сколько перевести? : ");
            double transaction = scanner.nextDouble();
            scanner.nextLine();

            if (transaction <= MY_BALANCE) {
                MY_BALANCE -= transaction;
                HISTORY.put(-transaction, localDateTime);

                BankAccount currentRecipientBalance = userBank.get(userNameTransaction);
                double gBalance = currentRecipientBalance.getBalance() + transaction;

                userBank.put(userNameTransaction, currentRecipientBalance);
            } else {
                return TransactionStatusVZ.INSUFFICIENT_FIND;
            }
        }
        return TransactionStatusVZ.STATUS_USER_ABSENCE;
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

    // 5. показать пользователей
    private static void userBankName(HashMap<String, BankAccount> userBank) {
        for (var ent : userBank.entrySet()) {
            System.out.println("-> " + ent.getKey());
        }
    }

    // 1. Пополнить баланс
    private static void depositAmount(Scanner scanner, LocalDateTime localDateTime) {
        System.out.print("Пополнить баланс на ");
        double depositAmount = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Деньги усмешно зачислены: " + depStatusVZ(depositAmount, localDateTime));
    }

    // Проверкка на снятие
    // доп ко 2
    private static TransactionStatusVZ anAmountTransaction(double anAmountMoney, LocalDateTime localDateTime) {
        if (anAmountMoney > MY_BALANCE) {
            return TransactionStatusVZ.INSUFFICIENT_FIND;
        }
        if (anAmountMoney >= 50_000) {
            return TransactionStatusVZ.LIMIT_EXCEEDED;
        }
        MY_BALANCE -= anAmountMoney;
        HISTORY.put(-anAmountMoney, localDateTime);
        return TransactionStatusVZ.STATUS_SUCCESSFULLY;
    }

    // доп к 1
    private static TransactionStatusVZ depStatusVZ(double depAmount, LocalDateTime localDateTime) {
        if (depAmount < 0) {
            return TransactionStatusVZ.INVALID_AMOUNT;
        }
        if (depAmount >= 50_000) {
            return TransactionStatusVZ.INVALID_AMOUNT;
        }
        MY_BALANCE += depAmount;
        HISTORY.put(depAmount, localDateTime);
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

    private static void saveDatebase(HashMap<String, BankAccount> userBank){
        Path path = Path.of("bankDatabase.txt");
        StringBuilder sb = new StringBuilder();

        for (BankAccount account : userBank.values()){
            sb.append(account.getAccountOwner())
                    .append(";")
                    .append(account.getBalance())
                    .append("\n");
        }

        try {
            Files.writeString(path, sb.toString());
            System.out.println("База данных успешно сохранена в файл!");
        } catch (IOException e){
            System.err.println("Ошибка при сохранении базы: " + e.getMessage());
        }
    }

    private static void loadDatabase(HashMap<String, BankAccount> userBank){
        Path path = Path.of("bankDatabase.txt");

        if(!Files.exists(path)){
            System.err.println("Файл баззы данных не найден \n");
            System.err.println("Был создан новый файл");
            return;
        }

        try{
            java.util.List<String> list = Files.readAllLines(path);

            for(String line : list){
                // пропускаем пустые строки
                if(line.trim().isEmpty()){
                    continue;
                }

                String[] data = line.split(";");
                String name = data[0];
                double balance = Double.parseDouble(data[1]);

                BankAccount account = new BankAccount(name, balance);
                userBank.put(name, account);
            }

            System.out.println("База данных успешно обновлена ");
            System.out.println("Пользователей в системе " + userBank.size());
        } catch (IOException e){
            System.err.println("Ошибка при загрузке файла " + e.getMessage());
        }
    }
}