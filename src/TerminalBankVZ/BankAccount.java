package TerminalBankVZ;

public class BankAccount {
    private String accountOwner;
    private double accountBalance;

    BankAccount(String accountOwner, double accountBalance) {
        this.accountOwner = accountOwner;
        this.accountBalance = accountBalance;
    }

    public double getBalance() {
        return accountBalance;
    }

    public String getAccountOwner() {
        return accountOwner;
    }

    public void addMoney(double amount){
        this.accountBalance += amount;
    }

    public void subtractMoney(double amount){
        this.accountBalance -= amount;
    }
}

