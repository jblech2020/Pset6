import java.text.NumberFormat;

public class BankAccount {

    private static long prevAccountNo = 100000000L;

    private int pin;
    private long accountNo;
    private double balance;
    private User accountHolder;

    public BankAccount(int pin, long accountNo, User accountHolder) {
        this.pin = pin;
        this.accountNo = ++BankAccount.prevAccountNo;
        // System.out.println("Account Number: " + this.accountNo);
        this.balance = 0.0;
        this.accountHolder = accountHolder;
    }

    public BankAccount(int pin, long accountNo, double balance, User accountHolder) {
      this.pin = pin;
      this.accountNo = ++BankAccount.prevAccountNo;
      // System.out.println("Account Number: " + this.accountNo);
      this.balance = balance;
      this.accountHolder = accountHolder;
    }

    public int getPin() {
        return pin;
    }

    public long getAccountNo() {
        return accountNo;
    }

    public String getBalance() {
        NumberFormat currency = NumberFormat.getCurrencyInstance();

        return currency.format(balance);
    }

    public User getAccountHolder() {
        return accountHolder;
    }

    public int deposit(double amount) {
        if (amount <= 0) {
            return ATM.INVALID;
        } else if (amount > 999999999999.99) {
            return ATM.OVERLOAD;
        } else {
            balance = balance + amount;
        }

        return ATM.SUCCESS;
    }

    public int withdraw(double amount) {
        if (amount <= 0) {
            return ATM.INVALID;
        } else if (amount > balance) {
            return ATM.INSUFFICIENT;
        } else {
            balance = balance - amount;
        }

        return ATM.SUCCESS;
    }
}
