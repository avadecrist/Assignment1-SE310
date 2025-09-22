package com.se310.ledger;

import java.util.*;
import static java.util.Map.*;

/**
 * Ledger Class representing simple implementation of Blockchain
 *
 * @author  Sergey L. Sundukovskiy
 * @version 1.0
 */
public class Ledger {
    private String name;
    private String description;
    private String seed;
    // Make blockMap and uncommittedBlock instance fields to avoid shared mutable static state.
    // Principle: Single Responsibility & Dependency Inversion — keep Ledger instance-scoped so multiple ledgers
    // (for tests or DI) can exist and so state isn't shared globally.
    private NavigableMap<Integer, Block> blockMap;
    private Block uncommittedBlock;

    // Keep a single Ledger singleton entry point but internal state is instance-based.
    private static Ledger ledger;

    // Delegate responsibilities to collaborators to follow Single Responsibility and Dependency Inversion:
    // - TransactionProcessor handles transaction application logic and block committing.
    // - LedgerValidator handles validation rules for the ledger.
    private final TransactionProcessor transactionProcessor;
    private final LedgerValidator ledgerValidator;

    /**
     * Create singleton of the Ledger
     * @param name
     * @param description
     * @param seed
     * @return
     */
    public static synchronized Ledger getInstance(String name, String description, String seed) {
        if (ledger == null) {
            ledger = new Ledger(name, description, seed);
        }
        return ledger;
    }

    /**
     * Private Ledger Constructor
     * @param name
     * @param description
     * @param seed
     */
    private Ledger(String name, String description, String seed) {
        this.name = name;
        this.description = description;
        this.seed = seed;
        // Initialize instance-scoped state previously held in static initializer
        this.blockMap = new TreeMap<>();
        this.uncommittedBlock = new Block(1, "");
        // Create master account in uncommitted block
        this.uncommittedBlock.addAccount("master", new Account("master", Integer.MAX_VALUE));

        // Create collaborators, injecting this Ledger so they operate on instance state.
        // Principle: Dependency Inversion — high level Ledger delegates to lower-level processors via abstractions.
        this.transactionProcessor = new TransactionProcessor(this);
        this.ledgerValidator = new LedgerValidator(this);
    }

    /**
     * Getter method for the name of the Ledger
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Setter Method for the name of the Ledger
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter Method for Ledger description
     * @return String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter Method for Description
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter Method for the seed
     * @return String
     */
    // Removed public getSeed to reduce public API surface. A package-private accessor is provided
    // for collaborators (TransactionProcessor) that need read access.

    /**
     * Setter Method for the seed
     * @param seed
     */
    public void setSeed(String seed) {
        this.seed = seed;
    }

    /**
     * Method for creating accounts in the blockchain
     * @param address
     * @return Account representing account in the Blockchain
     */
    public Account createAccount(String address) throws LedgerException {

        // Validate account does not already exist in the current uncommitted block
        // Principle: SRP — Ledger is responsible for account lifecycle operations; keep check here concise.
        if (uncommittedBlock.getAccount(address) != null) {
            throw new LedgerException("Create Account", "Account Already Exists");
        }

        Account account = new Account(address, 0);
        uncommittedBlock.addAccount(address, account);
        return account;
    }

    /**
     * Method implementing core functionality of the Blockchain by handling given transaction
     * @param transaction
     * @return String representing transaction id
     * @throws LedgerException
     */
    public synchronized String processTransaction(Transaction transaction) throws LedgerException {

        // Delegate transaction processing to a dedicated processor to follow SRP.
        // TransactionProcessor handles applying balances, committing blocks, and creating merkle roots.
        // Principle: SRP (separate processing from Ledger container) and Dependency Inversion (Ledger delegates to a collaborator).
        return this.transactionProcessor.processTransaction(transaction);
    }

    /**
     * Get Account balance by address
     * @param address
     * @return Integer representing balance of the Account
     * @throws LedgerException
     */
    public Integer getAccountBalance(String address) throws LedgerException {

        // Operate on instance-scoped blockMap
        if (blockMap.isEmpty()) {
            throw new LedgerException("Get Account Balance", "Account Is Not Committed to a Block");
        }

        Block block = blockMap.lastEntry().getValue();
        Account account = block.getAccount(address);

        if (account == null)
            throw new LedgerException("Get Account Balance", "Account Does Not Exist");
        else
            return account.getBalance();
    }

    /**
     * Get all Account balances that are part of the Blockchain
     * @return Map representing Accounts and balances
     */
    public Map<String,Integer> getAccountBalances(){

        if (blockMap.isEmpty())
            return null;

        Block committedBlock = blockMap.lastEntry().getValue();
        Map<String, Account> accountMap = committedBlock.getAccountBalanceMap();

        Map<String, Integer> balances = new HashMap<>();
        List<Account> accountList = new ArrayList<>(accountMap.values());

        for (Account account : accountList) {
            balances.put(account.getAddress(), account.getBalance());
        }

        return balances;
    }

    /**
     * Get Block by id
     * @param blockNumber
     * @return Block or Null
     */
    public Block getBlock (Integer blockNumber) throws LedgerException {
        // Instance-scoped access
        Block block = blockMap.get(blockNumber);
        if(block == null){
            throw new LedgerException("Get Block", "Block Does Not Exist");
        }
        return block;
    }

    /**
     * Get Transaction by id
     * @param transactionId
     * @return Transaction or Null
     */
    public Transaction getTransaction (String transactionId){

        // Search committed blocks
        for (Map.Entry<Integer, Block> mapElement : blockMap.entrySet()) {

            // Finding specific transactions in the committed blocks
            Block tempBlock = mapElement.getValue();
            for (Transaction transaction : tempBlock.getTransactionList()) {
                if (transaction.getTransactionId().equals(transactionId)) {
                    return transaction;
                }
            }
        }
        // Finding specific transactions in the uncommitted block
        for (Transaction transaction : uncommittedBlock.getTransactionList()) {
            if (transaction.getTransactionId().equals(transactionId)) {
                return transaction;
            }
        }
        return null;
    }

    /**
     * Get number of Blocks in the Blockchain
     * @return int representing number of blocks committed to Blockchain
     */
    public int getNumberOfBlocks(){
        return blockMap.size();
    }

    /**
     * Method for validating Blockchain.
     * Check each block for Hash consistency
     * Check each block for Transaction count
     * Check account balances against the total
     */
    public void validate() throws LedgerException {
    // Delegate validation to LedgerValidator to keep Ledger focused on state management.
    // Principle: SRP (separate validation logic) and OCP (Validator can be extended with new rules without modifying Ledger).
    this.ledgerValidator.validate();

    }

    /**
     * Helper method for CommandProcessor
     * @return current block we are working with
     */
    public Block getUncommittedBlock(){
        return uncommittedBlock;
    }

    // Package-private accessor for collaborators (TransactionProcessor) to update uncommitted block.
    // Kept non-public to avoid widening API surface.
    void setUncommittedBlock(Block block) {
        this.uncommittedBlock = block;
    }

    // Package-private accessor for TransactionProcessor to commit into blockMap.
    java.util.NavigableMap<Integer, Block> getBlockMap() {
        return this.blockMap;
    }

    // Package-private accessor for TransactionProcessor to read the ledger seed.
    String getSeed() {
        return this.seed;
    }

    /**
     * Helper method allowing reset the state of the Ledger
     */
    public synchronized void reset(){
        // Reset instance-scoped state; keep same collaborators (they reference this ledger instance)
        this.blockMap = new TreeMap<>();
        this.uncommittedBlock = new Block(1, "");
        this.uncommittedBlock.addAccount("master", new Account("master", Integer.MAX_VALUE));
    }
}
