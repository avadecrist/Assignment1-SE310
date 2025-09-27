package com.se310.ledger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TransactionProcessor encapsulates transaction processing rules.
 *
 */
public class TransactionProcessor {

    private final Ledger ledger;

    public TransactionProcessor(Ledger ledger) {
        this.ledger = ledger;
    }

    //Process transaction by applying balance changes and committing blocks when full, Returns the transaction id on success.
    public synchronized String processTransaction(Transaction transaction) throws LedgerException {

        // Validate basic invariants (kept here to encapsulate processing-specific rules)
        if (transaction.getAmount() < 0 || transaction.getAmount() > Integer.MAX_VALUE) {
            throw new LedgerException("Process Transaction", "Transaction Amount Is Out of Range");
        } else if (transaction.getFee() < 10) {
            throw new LedgerException("Process Transaction", "Transaction Fee Must Be Greater Than 10");
        } else if (transaction.getNote().length() > 1024) {
            throw new LedgerException("Process Transaction", "Note Length Must Be Less Than 1024 Chars");
        }

        if (ledger.getTransaction(transaction.getTransactionId()) != null) {
            throw new LedgerException("Process Transaction", "Transaction Id Must Be Unique");
        }

        // Resolve accounts from addresses stored in Transaction to avoid coupling Transaction to Account
        Account tempPayerAccount = ledger.getUncommittedBlock().getAccount(transaction.getPayerAddress());
        Account tempReceiverAccount = ledger.getUncommittedBlock().getAccount(transaction.getReceiverAddress());

        if (tempPayerAccount == null || tempReceiverAccount == null) {
            throw new LedgerException("Process Transaction", "Account Does Not Exist");
        }

        if (tempPayerAccount.getBalance() < (transaction.getAmount() + transaction.getFee()))
            throw new LedgerException("Process Transaction", "Payer Does Not Have Required Funds");

        // Apply balance changes using Account API (debit/credit) to centralize validation (SRP)
        try {
            tempPayerAccount.debit(transaction.getAmount() + transaction.getFee());
        } catch (LedgerException e) {
            // rethrow with the processing context
            throw new LedgerException("Process Transaction", "Payer Does Not Have Required Funds");
        }
        tempReceiverAccount.credit(transaction.getAmount());

    //add transaction to uncommitted block using Block API (encapsulation)
    ledger.getUncommittedBlock().addTransaction(transaction);

        //see if uncommitted block has reached max size and commit if needed
        if (ledger.getUncommittedBlock().getTransactionList().size() == 10) {

            List<String> tempTxList = new ArrayList<>();
            tempTxList.add(ledger.getSeed());

            //build merkle input
            for (Transaction tempTx : ledger.getUncommittedBlock().getTransactionList()) {
                tempTxList.add(tempTx.toString());
            }

            MerkleTrees merkleTrees = new MerkleTrees(tempTxList);
            // merkleTrees now computes the root at construction (DIP + SRP). Use getRoot() directly.
            ledger.getUncommittedBlock().setHash(merkleTrees.getRoot());

            // Commit the block into ledger's block map
            ledger.getBlockMap().put(ledger.getUncommittedBlock().getBlockNumber(), ledger.getUncommittedBlock());

            // Get committed block and its account snapshot
            Block committedBlock = ledger.getBlockMap().lastEntry().getValue();
            Map<String, Account> accountMap = committedBlock.getAccountBalanceMap();

            // Create next block and replicate accounts
            List<Account> accountList = new ArrayList<>(accountMap.values());
            ledger.setUncommittedBlock(new Block(ledger.getUncommittedBlock().getBlockNumber() + 1, committedBlock.getHash()));

            for (Account account : accountList) {
                Account tempAccount = (Account) account.clone();
                ledger.getUncommittedBlock().addAccount(tempAccount.getAddress(), tempAccount);
            }

            // Link to previous block
            ledger.getUncommittedBlock().setPreviousBlock(committedBlock);
        }

        return transaction.getTransactionId();
    }
}
