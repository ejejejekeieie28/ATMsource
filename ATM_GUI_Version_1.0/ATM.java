// ATM.java
// Represents an automated teller machine

public class ATM                                                         
{
   private boolean userAuthenticated; // whether user is authenticated
   private int currentAccountNumber; // current user's account number
   private Screen screen; // ATM's screen
   private Keypad keypad; // ATM's keypad
   private CashDispenser cashDispenser; // ATM's cash dispenser

   private BankDatabase bankDatabase; // account information database

   // constants corresponding to main menu options
   private static final int BALANCE_INQUIRY = 1;
   private static final int WITHDRAWAL = 2;
   private static final int TRANSFER = 3;                                       
   private static final int EXIT = 4;                                           

   // no-argument ATM constructor initializes instance variables
   public ATM() 
   {
      userAuthenticated = false; // user is not authenticated to start
      currentAccountNumber = 0; // no current account number to start
      keypad = new Keypad(); // create keypad 
      screen = new Screen(keypad.getPanel()); // create screen
      cashDispenser = new CashDispenser(); // create cash dispenser
      bankDatabase = new BankDatabase(); // create acct info database
   } // end no-argument ATM constructor

   // start ATM 
   public void run()
   {
      // welcome and authenticate user; perform transactions
      while ( true )
      {
         // loop while user is not yet authenticated
         while ( !userAuthenticated ) 
         {
            screen.displayMessageLine( "Welcome!" );       
            authenticateUser(); // authenticate user
         } // end while
         
         performTransactions(); // user is now authenticated 
         userAuthenticated = false; // reset before next ATM session
         currentAccountNumber = 0; // reset before next ATM session 
      } // end while   
   } // end method run

   // attempts to authenticate user against database
   private void authenticateUser() 
   {
      screen.displayMessageLine( "Please insert your card" );
      screen.displayMessageLine("[Enter your account ID]");
      keypad.idHandle();
      int accountNumber = keypad.getInput();
      screen.displayMessageLine("Please enter your password");
      keypad.passwordHandle();
      int pin = keypad.getPassword();

      
      // set userAuthenticated to boolean value returned by database
      userAuthenticated = 
         bankDatabase.authenticateUser( accountNumber, pin );
      
      // check whether authentication succeeded
      if ( userAuthenticated )
      {
         currentAccountNumber = accountNumber; // save user's account #
      } // end if
      else {
         screen.displayErroeMessage("Invalid account number or PIN. Please try again." );
         keypad.passed = true;
      }
   } // end method authenticateUser

   // display the main menu and perform transactions
   private void performTransactions()                                           
   {
      // local variable to store transaction currently being processed
      Transaction currentTransaction = null;
      
      boolean userExited = false; // user has not chosen to exit

      // loop while user has not chosen option to exit system
      while ( !userExited )
      {     
         // show main menu and get user selection
         int mainMenuSelection = displayMainMenu();

         // decide how to proceed based on user's menu selection
         switch ( mainMenuSelection )
         {
            // user chose to perform one of three transaction types
            case BALANCE_INQUIRY: 
            case WITHDRAWAL:
            case TRANSFER:                                                      

               // initialize as new object of chosen type
               currentTransaction = 
                  createTransaction( mainMenuSelection );
               keypad.setDisable();
               keypad.clear();
               screen.displayReset();
               currentTransaction.execute(); // execute transaction
               screen.displayMessageLine("\n=======================================");
               screen.displayMessageLine("Click OK to exit");
               keypad.waiting();
               keypad.setEnable(0);
               break; 
            case EXIT: // user chose to terminate session
               screen.displayMessageLine( "Exiting the system..." );
               screen.displayMessageLine("\n=======================================");
               screen.displayMessageLine("Click OK to exit");
               keypad.waiting();
               keypad.clear();
               keypad.setEnable(0);
               screen.displayReset();
               userExited = true; // this ATM session should end
               break;
            default: // user did not enter an integer from 1-4
               screen.displayErroeMessage( 
                  "You did not enter a valid selection. Try again." );
               break;
         } // end switch
      } // end while
   } // end method performTransactions
   
   // display the main menu and return an input selection
   private int displayMainMenu()                                                
   {
      keypad.clear();
      keypad.setEnable(0);
      screen.displayReset();
      screen.displayMessageLine( "Main menu:" );
      screen.displayMessageLine( "1 - View my balance" );
      screen.displayMessageLine( "2 - Withdraw cash" );
      screen.displayMessageLine( "3 - Transfer funds" );                        
      screen.displayMessageLine( "4 - Exit" );                                
      screen.displayMessageLine( "Enter a choice: " );            
      keypad.waiting();
      return keypad.getInput(); // return user's selection
   } // end method displayMainMenu
         
   // return object of specified Transaction subclass
   private Transaction createTransaction( int type )
   {
      Transaction temp = null; // temporary Transaction variable
      
      // determine which type of Transaction to create     
      switch ( type )
      {
         case BALANCE_INQUIRY: // create new BalanceInquiry transaction
            temp = new BalanceInquiry( 
               currentAccountNumber, screen, bankDatabase );

            break;
         case WITHDRAWAL: // create new Withdrawal transaction
            temp = new Withdrawal( currentAccountNumber, screen, 
               bankDatabase, keypad, cashDispenser );

            break; 

           
         case TRANSFER: // create new Transfer transaction                      
            temp = new Transfer( currentAccountNumber, screen, 
               bankDatabase, keypad );
              
            break;
      } // end switch

      return temp; // return the newly created object
   } // end method createTransaction

} // end class ATM



/**************************************************************************
 * (C) Copyright 1992-2007 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/
