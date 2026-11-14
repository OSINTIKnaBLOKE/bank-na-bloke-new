package TerminalBankVZ;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class BankAccount {
    private String accountOwner;
    private double accountBalance;
    private LocalDate subscriptionEndData;

    BankAccount(String accountOwner,
                double accountBalance,
                LocalDate subscriptionEndData)
    {
        this.accountOwner = accountOwner;
        this.accountBalance = accountBalance;
        this.subscriptionEndData = subscriptionEndData;
    }

    public double getBalance() {
        return accountBalance;
    }

    public String getAccountOwner() {
        return accountOwner;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public void addMoney(double amount){
        this.accountBalance += amount;
    }

    public void subtractMoney(double amount){
        this.accountBalance -= amount;
    }

    // покупка подписки
    public void buyPremium(Scanner scanner, BankAccount userBank){

        System.out.println("Подписка на 1 месяц: 1000.00");
        System.out.println("Подписка на 3 месяца: 3000.00");
        System.out.println("Подписка на 6 месяцев: 6000.00");
        System.out.println("Подписка на 12 месяцев: 10000.00 (Скидка 16%)");

        System.out.println("Введите сумму для подклюсения подписки: ");


        int buy = scanner.nextInt();
        int status = statusBalance(buy);

        if(userBank.getBalance() >= buy){
            this.subscriptionEndData = LocalDate.now().plusMonths(status);
            double r = userBank.getBalance() - buy;
            userBank.setAccountBalance(r);
        }
    }

    public boolean isPremiumActive(){
        if(subscriptionEndData == null){
            return false;
        }

        return LocalDate.now().isBefore(subscriptionEndData) || LocalDate.now().isEqual(subscriptionEndData);
    }

    private int statusBalance(int x){
        if(x == 1000){
            System.out.println(TransactionStatusVZ.STATUS_SUCCESSFULLY);
            return 1;
        }
        if(x == 3000){
            System.out.println(TransactionStatusVZ.STATUS_SUCCESSFULLY);
            return 3;
        }
        if(x == 6000){
            System.out.println(TransactionStatusVZ.STATUS_SUCCESSFULLY);
            return 6;
        }
        if(x == 10000){
            System.out.println(TransactionStatusVZ.STATUS_SUCCESSFULLY);
            return 12;
        }
        System.out.println(TransactionStatusVZ.STATUS_ERROR);
        return 0;
    }

    public LocalDate getSubscriptionEndData() {
        return subscriptionEndData;
    }

    public void setSubscriptionEndData(LocalDate subscriptionEndData) {
        this.subscriptionEndData = subscriptionEndData;
    }


}

