import java.util.Scanner;
import java.io.IOException;

public class ATM {

    private Scanner in;
    private BankAccount activeAccount;
    private Bank bank;

    public static final int VIEW = 1;
    public static final int DEPOSIT = 2;
    public static final int WITHDRAW = 3;
    public static final int TRANSFER = 4;
    public static final int LOGOUT = 5;

    public static final int INVALID = 0;
    public static final int INSUFFICIENT = 1;
    public static final int SUCCESS = 2;
    public static final int OVERLOAD = 3;

    public static final int FIRST_NAME_WIDTH = 20;
    public static final int LAST_NAME_WIDTH = 30;

    int pin;
    String accountNo;

    public ATM() {
        in = new Scanner(System.in);
        try {
          this.bank = new Bank();
        } catch (IOException e) {
          System.out.println(e);
        }
    }

    public void startup() {
        System.out.println("Welcome to the AIT ATM!");

        while (true) {
            System.out.print("\n");
            System.out.print("Account No.: ");
            accountNo = in.nextLine().strip();
            // System.out.println(!accountNo.equals("+"));

            if (!accountNo.equals("+")){
                System.out.print("PIN        : ");
                pin = in.nextInt();
            }

            if (accountNo.equals("+")){
                System.out.print("\n");

                System.out.print("First name: ");
                String firstName = in.nextLine().strip();

                System.out.print("Last name: ");
                String lastName = in.nextLine().strip();

                int newPin;
                do {
                  System.out.print("PIN: ");
                  newPin = in.nextInt();
                } while (1000 > newPin || newPin > 9999);

                System.out.println(bank.createAccount(newPin, new User(firstName, lastName)));

            } else if (isValidLogin(Long.parseLong(accountNo), pin)) {
                activeAccount = bank.login(Long.parseLong(accountNo), pin);
                System.out.print(activeAccount);

                System.out.println("\nHello, again, " + activeAccount.getAccountHolder().getFirstName() + "!\n");

                boolean validLogin = true;
                while (validLogin) {
                    switch (getSelection()) {
                        case VIEW: showBalance(); break;
                        case DEPOSIT: deposit(); break;
                        case WITHDRAW: withdraw(); break;
                        case TRANSFER: transfer(); break;
                        case LOGOUT: validLogin = false; break;
                        default: System.out.println("\nInvalid selection.\n"); break;
                    }
                    in.nextLine();
                }
            } else {
                if (Long.parseLong(accountNo) == -1 && pin == -1) {
                    shutdown();
                } else {
                    System.out.println("\nInvalid account number and/or PIN.\n");
                }
            }
        }
    }

    public boolean isValidLogin(long accountNo, int pin) {
        if (bank.getAccount(accountNo)!=null){
          if (bank.login(accountNo, pin)!=null){
            return true;
          } else {
            return false;
          }
        } else {
          return false;
        }
    }

    public int getSelection() {
        System.out.println("[1] View balance");
        System.out.println("[2] Deposit money");
        System.out.println("[3] Withdraw money");
        System.out.println("[4] Transfer money");
        System.out.println("[5] Logout\n");

        return in.nextInt();
    }

    public void showBalance() {
        System.out.println("\nCurrent balance: " + activeAccount.getBalance() + "\n");
    }

    public void deposit() {
        System.out.print("\nEnter amount: ");
        double amount = in.nextDouble();

        int status = activeAccount.deposit(amount);
        if (status == ATM.INVALID) {
            System.out.println("\nDeposit rejected. Amount must be greater than $0.00.\n");
        } else if (status == ATM.OVERLOAD) {
            System.out.println("\nDeposit rejected. Amount would cause balance to exceed $999,999,999,999.99.\n");
        } else if (status == ATM.SUCCESS) {
            System.out.println("\nDeposit accepted.\n");
            System.out.println(activeAccount);
            bank.update(activeAccount);
            bank.save();
        }
    }

    public void withdraw() {
        System.out.print("\nEnter amount: ");
        double amount = in.nextDouble();

        int status = activeAccount.withdraw(amount);
        if (status == ATM.INVALID) {
            System.out.println("\nWithdrawal rejected. Amount must be greater than $0.00.\n");
        } else if (status == ATM.INSUFFICIENT) {
            System.out.println("\nWithdrawal rejected. Insufficient funds.\n");
        } else if (status == ATM.SUCCESS) {
            System.out.println("\nWithdrawal accepted.\n");
        }
    }

    public void transfer(){
      // do {
        System.out.print("\nEnter account: ");
        long accountToTransfer = in.nextLong();
      // } while ();

      System.out.print("\nEnter amount: ");
      double amount = in.nextDouble();

      int status = activeAccount.withdraw(amount);
      if (status == ATM.INVALID) {
          System.out.println("\nTransfer rejected. Amount must be greater than $0.00.\n");
      } else if (status == ATM.INSUFFICIENT) {
          System.out.println("\nTransfer rejected. Insufficient funds.\n");
      } else if (status == ATM.SUCCESS) {
          System.out.println("\nTransfer accepted.\n");
      }
    }

    public void shutdown() {
        if (in != null) {
            in.close();
        }

        System.out.println("\nGoodbye!");
        System.exit(0);
    }

    public static void main(String[] args) {
        ATM atm = new ATM();

        atm.startup();
    }
}
