import java.util.Scanner;
import java.io.IOException;
import java.util.InputMismatchException;


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

            if (!accountNo.equals("+")){
                System.out.print("PIN        : ");
                pin = in.nextInt();
            }

            if (accountNo.equals("+")){
                System.out.print("\n");

                //Initializes variables
                String firstName;
                String lastName;
                int newPin;

                //Regulates variable's format
                do {
                  System.out.print("First name: ");
                  firstName = in.nextLine().strip();
                } while (firstName.length()>20 || firstName.length()<1 || firstName == null);

                do {
                  System.out.print("Last name: ");
                  lastName = in.nextLine().strip();
                } while (lastName.length()>20 || lastName.length()<1 || lastName == null);

                do {
                  System.out.print("PIN: ");
                  newPin = in.nextInt();
                } while (1000 > newPin || newPin > 9999);

                //Creates the new account with the inputted information
                System.out.println(bank.createAccount(newPin, new User(firstName, lastName)));
                in.nextLine();

            } else if (isValidLogin(Long.parseLong(accountNo), pin)) {
                activeAccount = bank.login(Long.parseLong(accountNo), pin);

                System.out.println("\nHello, again, " + activeAccount.getAccountHolder().getFirstName() + "!");

                //Creates the selection menu and allows for user input
                getSelectionWithRedirects();

            } else {
                if (Long.parseLong(accountNo) == -1 && pin == -1) {
                    shutdown();
                } else {
                    System.out.println("\nInvalid account number and/or PIN.");
                    in.nextLine();
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

    public void getSelectionWithRedirects() {
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
            if (validLogin = false){
                in.nextLine();
            }
        }
    }

    public int getSelection() {
      int input = 0;
      while (input<1 || input>5){
        System.out.println("\n[1] View balance");
        System.out.println("[2] Deposit money");
        System.out.println("[3] Withdraw money");
        System.out.println("[4] Transfer money");
        System.out.println("[5] Logout");
        try {
          input = in.nextInt();
        } catch (InputMismatchException e){
          System.out.println("\nPlease input a number between [1] and [5].");
        }
        in.nextLine();

      }
      return input;
    }

    public void showBalance() {
        System.out.println("\nCurrent balance: " + activeAccount.getBalance());
        getSelectionWithRedirects();
    }

    public void deposit() {
        System.out.print("\nEnter amount: ");
        double amount = in.nextDouble();

        int status = activeAccount.deposit(amount);
        if (status == ATM.INVALID) {
            System.out.println("\nDeposit rejected. Amount must be greater than $0.00.");
        } else if (status == ATM.OVERLOAD) {
            System.out.println("\nDeposit rejected. Amount would cause balance to exceed $999,999,999,999.99.");
        } else if (status == ATM.SUCCESS) {
            System.out.println("\nDeposit accepted.");
            bank.update(activeAccount);
            bank.save();
        }
    }

    public void withdraw() {
        System.out.print("\nEnter amount: ");
        double amount = in.nextDouble();

        int status = activeAccount.withdraw(amount);
        if (status == ATM.INVALID) {
            System.out.println("\nWithdrawal rejected. Amount must be greater than $0.00.");
        } else if (status == ATM.INSUFFICIENT) {
            System.out.println("\nWithdrawal rejected. Insufficient funds.");
        } else if (status == ATM.SUCCESS) {
            System.out.println("\nWithdrawal accepted.");
            bank.update(activeAccount);
            bank.save();
        }
    }

    public void transfer(){
      long accountToTransfer = 0;
      while (accountToTransfer<100000001 || accountToTransfer > 999999999){
        System.out.print("\nEnter account: ");
        accountToTransfer = in.nextLong();
      }

      String transferAccount = bank.getAccount(accountToTransfer);

      if (transferAccount )

        System.out.print("\nEnter amount: ");
        double amount = in.nextDouble();

        int withdrawStatus = activeAccount.withdraw(amount);
        int depositStatus = transferAccount.deposit(amount);
        if (withdrawStatus == ATM.INVALID) {
            System.out.println("\nTransfer rejected. Amount must be greater than $0.00.");
        } else if (withdrawStatus == ATM.INSUFFICIENT) {
            System.out.println("\nTransfer rejected. Insufficient funds.");
        } else if (withdrawStatus == ATM.SUCCESS && depositStatus == ATM.SUCCESS) {
            System.out.println("\nTransfer accepted.");
            bank.update(activeAccount);
            bank.save();
        } else if (depositStatus == ATM.OVERLOAD) {
          System.out.println("\nTransfer rejected. Amount would cause destination balance to exceed $999,999,999,999.99.");
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
