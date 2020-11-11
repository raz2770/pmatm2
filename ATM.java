import java.util.ArrayList;
import java.util.Scanner;

public class ATM
{
    private Screen screen;
    private Keypad keypad;

    private DepositSlot depositSlot;
    private Users users;
    private boolean userAuthenticated;
    private int currentAccountNumber;

    public ATM()
    {
        screen = new Screen();
        keypad = new Keypad();

        depositSlot = new DepositSlot();
        users = new Users();
        userAuthenticated = false;
        currentAccountNumber = 0;
    }
    public static void main(String[] args)
    {
        ATM atm = new ATM();
        atm.run();
    }

    public void run()
    {
        while(true)
        {
            screen.displayMessageLine("\n👏👏 Welcome in sbi bank atm barodameo 🙏🙏🙏 !");

            while (!userAuthenticated)
            {
                authenticateUser();
            }

            performTransactions();
            screen.displayMessageLine("\nThank you");
            userAuthenticated = false;
            currentAccountNumber = 0;
        }
    }

    private void authenticateUser()
    {
        screen.displayMessage("\nPlease enter your account number: ");
        int accountNumber = keypad.getInput();
        screen.displayMessage("\nEnter your PIN: ");
        int pin = keypad.getInput();

        userAuthenticated = users.authenticateUser(accountNumber, pin);

        if(userAuthenticated)
        {
            currentAccountNumber = accountNumber;
        }
        else
        {
            screen.displayMessageLine("Invalid account number or PIN. Please try again.");
        }
    }

    private void performTransactions()
    {
        boolean userExited = false;

        while(!userExited)
        {
            int mainMenuSelection = displayMainMenu();

            switch(mainMenuSelection)
            {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    createTransaction(mainMenuSelection).transicationexecute();
                    break;
                case 6:
                    screen.displayMessageLine("\nExiting the system...");
                    userExited = true;
                    break;
                default:
                    screen.displayMessageLine("\nYou did not enter a valid selection. Try again.");
                    break;
            }
        }
    }

    private final int displayMainMenu()
    {
        screen.displayMessageLine("\nMain menu:- please choose one key for operations");
        screen.displayMessageLine("1 - View my balance");
        screen.displayMessageLine("2 - Withdraw cash");
        screen.displayMessageLine("3 - Deposit funds");
        screen.displayMessageLine("4 - change pin");
        screen.displayMessageLine("5 -  Balance transfer");


        screen.displayMessageLine("6 - Exit\n");
        screen.displayMessage("Enter a choice: ");
        return keypad.getInput(); // return user's selection
    }

    private Transaction createTransaction(int Selection)
    {
        Transaction transaction = null;

        switch(Selection)
        {
           case 1:
               transaction = new BalanceInquiry(currentAccountNumber, users, screen,keypad);
               break;
           case 2:
               transaction = new Withdrawal(currentAccountNumber, users, screen, keypad);
               break;
           case 3:
               transaction = new Deposit(currentAccountNumber, users, screen, keypad, depositSlot);
               break;
            case 4:
                transaction = new Changepin(currentAccountNumber, users, screen, keypad);
                break;
            case 5:
                transaction = new Transfer(currentAccountNumber, users, screen, keypad);


        }

        return transaction;
    }
}
 class Account
{
    private int accountNumber;
    private int pin;
    private  String ifsc;
    private  String name;
    private String num ;


    private double totalBalance;


    public Account(int accountNumber,String name, int pin,double totalBalance,String ifsc,String num)
    {
        this.accountNumber = accountNumber;
        this.name =name;
        this.pin =  pin;
        this.ifsc = ifsc;
        this.num= num;

        this.totalBalance = totalBalance;
    }


    final public boolean validatePIN(int userPIN)
    {
        return userPIN == pin;
    }


    final public int getAccountNumber()
    {
        return accountNumber;
    }
    final public String getName()
    {
        return name;
    }
    final public String getIfsc()
    {
        return ifsc;
    }
    final public String getNum()
    {
        return num;
    }




    final public double getTotalBalance()
    {
        return totalBalance;
    }


    public void credit(double amount)
    {
        totalBalance += amount;
    }


    public void debit(double amount)
    {

        totalBalance -= amount;
    }
    public void setPin(int npin)
    {
        pin = npin;
    }

}


 class BalanceInquiry extends Transaction

{
    private Keypad keypad;
    public BalanceInquiry(int userAccountNumber,Users users, Screen screen,Keypad keypad)
    {
        super(userAccountNumber, users, screen);
        this.keypad=keypad;
    }

    public void transicationexecute() {
        Users users = getusers();
        Screen screen = getScreen();
        String otp = generateOTP();


        screen.displayMessageLine("otp successfully sent to your mobile num. :-   " + users.getNum(getAccountNumber()));
        screen.displayMessageLine("your otp(just for information):-  " + otp);
        screen.displayMessageLine("please enter your otp");
        String userotp = keypad.getStr();
        if (otp.equals(userotp)) {


            double totalBalance = users.getTotalBalance(getAccountNumber());


            screen.displayMessageLine("\nBalance Information:");

            screen.displayMessage("\n - name: ");

            screen.displayMessage(users.getName(getAccountNumber()));
            screen.displayMessage("\n - ifsc: ");

            screen.displayMessage(users.getIfsc(getAccountNumber()));
            screen.displayMessage("\n - mobile num.: ");
            screen.displayMessage(users.getNum(getAccountNumber()));

            screen.displayMessage("\n - Total balance: ");
            screen.displayAmount(totalBalance);

            screen.displayMessageLine("");
        } else {
            screen.displayMessage("invalid otp");
        }
    }
}


class Deposit extends Transaction
{
    static final int CANCEL = 0;
    private Keypad keypad;
    private DepositSlot depositSlot;

    public Deposit(int userAccountNumber, Users users, Screen screen, Keypad keypad, DepositSlot depositSlot)
    {
        super(userAccountNumber, users, screen);
        this.keypad = keypad;
        this.depositSlot = depositSlot;
    }

    public void transicationexecute()
    {
        Users users = getusers();
        Screen screen = getScreen();
        String otp =generateOTP();


        screen.displayMessageLine("otp successfully sent to your mobile num. :-   "+ users.getNum(getAccountNumber()) );
        screen.displayMessageLine("your otp(just for information):-  " +otp);
        screen.displayMessageLine("please enter your otp" );
        String userotp= keypad.getStr();
        if(otp.equals(userotp)) {

            double amount = input();

            if (amount != CANCEL) {

                screen.displayAmount(amount);
                screen.displayMessageLine(" in the deposit slot.");


                boolean envelopeReceived = depositSlot.isEnvelopeReceived();

                if (envelopeReceived) {

                    users.credit(getAccountNumber(), amount);
                } else {
                    screen.displayMessageLine("\nYou did not insert an envelope, so the ATM has canceled your transaction.");
                }
            } else {
                screen.displayMessageLine("\nCanceling transaction...");
            }
        }
        else
        {
            screen.displayMessageLine("\ninvalid otp");
        }
    }

    public final double input()
    {
        Screen screen = getScreen();


        screen.displayMessage("\nPlease enter a deposit amount  (or 0 to cancel): ");
        int input = keypad.getInput();

        if(input == CANCEL)
        {
            return CANCEL;
        }
        else
        {
            return input;
        }
    }
}
 class DepositSlot
{
    final boolean isEnvelopeReceived()
    {
        return true;
    }
}




 class Keypad
{
    Scanner kb = new Scanner(System.in);


    public final int getInput()
    {
        int input;
        input = kb.nextInt();
        return input;
    }
    public final String  getStr()
    {
        String str;
        str = kb.next();
        return str;
    }

}


 class Screen
{

    public final void displayMessage(String message)
    {
        System.out.print(message);
    }


    public final void displayMessageLine(String message)
    {
        System.out.println(message);
    }


    public final void displayAmount(double amount)
    {
        System.out.print(String.format("%.2f", amount));
    }
    public final void displayIfsc(int ifsc)
    {
        System.out.print(ifsc);
    }

}


 abstract class Transaction
{
    private int accountNumber;
    private Users users;
    private Screen screen;

    public Transaction(int userAccountNumber, Users users, Screen screen)
    {
        accountNumber = userAccountNumber;
        this.users = users;
        this.screen = screen;
    }

    public int getAccountNumber()
    {
        return accountNumber;
    }
    public static String generateOTP()
    {
        int randomPin   =(int) (Math.random()*9000)+1000;
        String otp  = String.valueOf(randomPin);
        return otp;
    }

    public Users getusers()
    {
        return users;
    }

    public Screen getScreen()
    {
        return screen;
    }

    public abstract void transicationexecute();

}



 class Users
{

    private ArrayList<Account> accounts;

    public Users()
    {


        Account account1 = new Account(1111,"shubhamraj" ,1111, 10000.0,"PUNB0166","9999999999");
        Account account2 = new Account(2222, "manojkumar", 2222,20000.0,"SBI0166","9999999998");
        Account account3 = new Account(3333, "ajaykumar" ,3333, 30000.0,"CTAE0122","9999999997");
        Account account4= new Account(4444, "sanjay",4444, 40000.0,"BRKB0166","9999999996");
        Account account5= new Account(5555, "mohan",5555, 50000.0,"PUNB0166","9999999995");



        accounts = new ArrayList();
        accounts.add(account1);
        accounts.add(account2);
        accounts.add(account3);
        accounts.add(account4);
        accounts.add(account5);
    }

    public Account getAccount(int accountNumber)
    {
        for(int i = 0; i < accounts.size(); i++)
        {
            if (accounts.get(i).getAccountNumber()==accountNumber)
            {
                return accounts.get(i);
            }
        }

        return null;
    }

    public boolean authenticateUser(int userAccountNumber, int userPIN)
    {
        Account userAccount = getAccount(userAccountNumber);
        if(userAccount != null)
        {
            return userAccount.validatePIN(userPIN);
        }
        else
        {
            return false;
        }
    }



    public double getTotalBalance(int userAccountNumber)
    {
        return getAccount(userAccountNumber).getTotalBalance();
    }
    public String getIfsc(int userAccountNumber)
    {
        return getAccount(userAccountNumber).getIfsc();
    }
    public String getNum(int userAccountNumber)
    {
        return getAccount(userAccountNumber).getNum();
    }
    public String getName(int userAccountNumber)
    {
        return getAccount(userAccountNumber).getName();
    }

    public void credit(int userAccountNumber, double amount)
    {
        getAccount(userAccountNumber).credit(amount);
    }

    public void debit(int userAccountNumber, double amount)
    {
        getAccount(userAccountNumber).debit(amount);
    }
    public void setPin(int userAccountNumber,int pin)
    {
        getAccount(userAccountNumber).setPin(pin);
    }


}



 class Withdrawal extends Transaction
{
    static final int CANCEL= 2;
    private Keypad keypad;


    public Withdrawal(int userAccountNumber, Users users, Screen screen, Keypad keypad)
    {
        super(userAccountNumber, users, screen);
        this.keypad = keypad;

    }

    public void transicationexecute()
    {
        Users users = getusers();
        Screen screen = getScreen();

        String otp =generateOTP();


        screen.displayMessageLine("otp successfully sent to your mobile num. :-   "+ users.getNum(getAccountNumber()) );
        screen.displayMessageLine("your otp(just for information):-  " +otp);
        screen.displayMessageLine("please enter your otp" );
        String userotp= keypad.getStr();
        if(otp.equals(userotp)) {

            int amount = input();

            if (amount != CANCEL) {


                if (users.getTotalBalance(getAccountNumber()) > amount) {


                    users.debit(getAccountNumber(), amount);


                    screen.displayMessageLine("\nPlease take your cash from the cash dispenser.");
                } else {
                    screen.displayMessageLine("\nInsufficient cash available in the account."
                            + "\nPlease choose a smaller amount.");
                }


            } else {
                screen.displayMessageLine("\nCanceling transaction...");
            }
        }
        else
        {
            screen.displayMessageLine("\nInvalid otp");
        }

    }

    public final int input()
    {
        Screen screen = getScreen();


        int amount = 0;

        while(amount == 0)
        {

            screen.displayMessageLine("\nWithdrawal options:");
            screen.displayMessageLine("1 - for withdraw money");

            screen.displayMessageLine("2 - Cancel transaction");
            screen.displayMessage("\nChoose a withdrawal option (1-2): ");


            int input = keypad.getInput();


            switch(input)
            {
                case 1:
                    screen.displayMessageLine("please enter money for withdraw");
                    amount=keypad.getInput();
                    break;
                case 2:
                    amount = CANCEL;
                    break;
                default:
                    screen.displayMessageLine("\nInvalid selection. Try again.");
            }
        }

        return amount;
    }
}
class Changepin extends Transaction
{
    private Keypad keypad;


    public Changepin(int userAccountNumber, Users users, Screen screen, Keypad keypad)
    {
        super(userAccountNumber, users, screen);
        this.keypad = keypad;


    }
    public void transicationexecute() {
        Users users = getusers();
        Screen screen = getScreen();
        String otp = generateOTP();


        screen.displayMessageLine("otp successfully sent to your mobile num. :-   " + users.getNum(getAccountNumber()));
        screen.displayMessageLine("your otp(just for information):-  " + otp);
        screen.displayMessageLine("please enter your otp");
        String userotp = keypad.getStr();
        if (otp.equals(userotp)) {
            screen.displayMessageLine("\nenter new pin ,pin number should be in 4 digit");

            int input = keypad.getInput();
            if (input <= 9999 && input >= 1000) {
                users.setPin(getAccountNumber(), input);
                screen.displayMessageLine("\nsuccessfully pin changed");
            } else
                screen.displayMessageLine("\ninvalid input");


        } else {
            screen.displayMessageLine("\ninvalid otp");
        }
    }

}
class Transfer extends Transaction
{
    private Keypad keypad;


    public Transfer(int userAccountNumber, Users users, Screen screen, Keypad keypad)
    {
        super(userAccountNumber, users, screen);
        this.keypad = keypad;


    }
    public void transicationexecute() {
        Users users = getusers();
        Screen screen = getScreen();
        String otp =generateOTP();


        screen.displayMessageLine("otp successfully sent to your mobile num. :-   "+ users.getNum(getAccountNumber()) );
        screen.displayMessageLine("your otp(just for information):-  " +otp);
        screen.displayMessageLine("please enter your otp" );
        String userotp= keypad.getStr();
        if(otp.equals(userotp)) {
            screen.displayMessageLine("please enter account number for transfer:-");
            int anotherac = keypad.getInput();

            screen.displayMessageLine("please enter ifsc code for transfer:-");
            String ifsc = keypad.getStr();

            if ((users.getAccount(anotherac) != null) && (users.getAccount(anotherac).getIfsc().equals(ifsc))) {

                screen.displayMessageLine("please enter money for transfer:-");
                int amount = keypad.getInput();
                if (users.getTotalBalance(getAccountNumber()) > amount) {
                    users.debit(getAccountNumber(), amount);


                    screen.displayMessageLine("\nAmount successfully transferred");
                    users.credit(anotherac, amount);
                }


            } else {
                screen.displayMessageLine("invalid account number or ifsc code");

            }
        }
        else
        {
            screen.displayMessageLine("invalid otp");
        }



    }

}






