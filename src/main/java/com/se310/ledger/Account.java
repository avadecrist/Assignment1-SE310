package com.se310.ledger;

/**
 * Account class implementation representing account in the Blockchain
 *
 * @author  Sergey L. Sundukovskiy
 * @version 1.0
 */
public class Account {

    // Make address final to preserve account identity and prevent accidental changes (SRP)
    private final String address;
    // Keep balance as Integer internally but control mutation via methods (credit/debit)
    private Integer balance;

    /**
     * Account Constructor
     * @param address
     * @param balance
     */
    public Account(String address, Integer balance) {
        // Validate inputs: address must be present and balance non-negative
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("address must be provided");
        }
        if (balance == null || balance < 0) {
            throw new IllegalArgumentException("balance must be non-negative");
        }

        this.address = address;
        this.balance = balance;
    }

    /**
     * Getter Method for account address
     * @return
     */
    public String getAddress() {
        return address;
    }

    // Removed setAddress 

    /**
     * Getter method for account balance
     * @return
     */
    public int getBalance() {
        return balance;
    }

    // Expose controlled balance mutation methods instead of a generic setter.
    // This centralizes validation and protects invariants (e.g., no negative balances).

    /**
     * Increase the account balance by amount. Validates amount > 0.
     */
    public void credit(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("credit amount must be non-negative");
        }
        // Consider overflow check if needed
        this.balance = this.balance + amount;
    }

    /**
     * Decrease the account balance by amount. Throws LedgerException on insufficient funds.
     */
    public void debit(int amount) throws LedgerException {
        if (amount < 0) {
            throw new IllegalArgumentException("debit amount must be non-negative");
        }
        if (this.balance < amount) {
            throw new LedgerException("Account", "Insufficient Funds");
        }
        this.balance = this.balance - amount;
    }

    /**
     * Method for creating an account copy
     * @return
     */
    public Object clone() {
        // Provide a typed copy() method instead of relying on Object.clone
        return new Account(this.getAddress(), this.balance);
    }

    /**
     * Typed copy method used by Ledger when replicating accounts between blocks.
     */
    public Account copy() {
        return new Account(this.address, this.balance);
    }
}
