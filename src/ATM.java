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

    public ATM() {
        in = new Scanner(System.in);
        try {
          this.bank = new Bank();
        } catch (IOException e) {
          System.out.println(e);
        }
    }

    public boolean checkStringInput(String input){
      if (input.isEmpty()){
        return true;
      } else if (input.equals("-1") || input.equals("+")){
        return false;
      }

      for (int i=0; i<input.length(); i++){
        if (!Character.isDigit(input.charAt(i))){
          return true;
        }
      }

      return false;
    }

    public void startup() {
        System.out.println("Welcome to the AIT ATM!");

        while (true) {
            //initializes variables
            int pin = 100000;
            String accountNo = "";

            System.out.print("\n");
            while (checkStringInput(accountNo)){
              System.out.print("Account No.: ");
              accountNo = in.nextLine().strip();
            }


            if (!accountNo.equals("+")){
                while (pin != -1 && (pin>9999 || pin<1000)){
                    System.out.print("PIN        : ");
                    try {
                      pin = in.nextInt();
                    } catch (InputMismatchException e) {
                      in.nextLine();
                    }
                }
            }

            if (accountNo.equals("+")){
                System.out.print("\n");

                //Initializes variables
                String firstName;
                String lastName;
                int newPin = 10000000;

                //Regulates variable's format
                do {
                    System.out.print("First name: ");
                    firstName = in.nextLine().strip();
                } while (firstName.length()>20 || firstName.length()<1 || firstName == null);

                do {
                    System.out.print("Last name: ");
                    lastName = in.nextLine().strip();
                } while (lastName.length()>20 || lastName.length()<1 || lastName == null);

                while (1000 > newPin || newPin > 9999) {
                    System.out.print("PIN: ");
                    try {
                      newPin = in.nextInt();
                    } catch (InputMismatchException e) {
                      System.out.println("\nPlease enter a four digit number.");
                      in.nextLine();
                    }
                }

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
          in.nextLine();
        }
      }
      return input;
    }

    public void showBalance() {
        System.out.println("\nCurrent balance: " + activeAccount.getBalance());
        getSelectionWithRedirects();
    }

    public void deposit() {
        System.out.print("\n");
        double amount = -1;
        while (amount<0){
          System.out.print("Enter amount: ");
          try {
            amount = in.nextDouble();
          } catch (Exception e){
            System.out.println("\nInvalid input. Try again.");
            in.nextLine();
          }
        }

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
        getSelectionWithRedirects();
    }

    public void withdraw() {
        System.out.print("\n");
        double amount = -1;
        while (amount<0){
          System.out.print("Enter amount: ");
          try {
            amount = in.nextDouble();
          } catch (Exception e){
            System.out.println("\nInvalid input. Try again.");
            in.nextLine();
          }
        }

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
        getSelectionWithRedirects();
    }

    public void transfer(){
        long accountToTransfer = 0;
        System.out.print("\n");
        while (accountToTransfer<100000001 || accountToTransfer > 999999999){
          System.out.print("Enter account: ");
          try {
  				  accountToTransfer = in.nextLong();
    			} catch (Exception e){
    				System.out.println("\nInvalid input. Try again.");
            in.nextLine();
    			}
        }

        BankAccount transferAccount = bank.getAccount(accountToTransfer);

        if (transferAccount == null){
          System.out.println("\nTransfer rejected. Destination account not found.");
        } else if (transferAccount == activeAccount){
          System.out.println("\nTransfer rejected. Destination account matches origin.");
        } else {

            System.out.print("Enter amount: ");
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
                bank.update(transferAccount);
                bank.save();
            } else if (depositStatus == ATM.OVERLOAD) {
              System.out.println("\nTransfer rejected. Amount would cause destination balance to exceed $999,999,999,999.99.");
            }
        }
        getSelectionWithRedirects();
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
