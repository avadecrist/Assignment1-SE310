package com.se310.ledger;

import java.util.List;
import java.util.Map;

/**
 * LedgerValidator encapsulates ledger validation rules.
 *
 * SOLID notes:
 * - Single Responsibility: validation rules are in their own class, keeping Ledger focused on state management.
 * - Open/Closed: new validation rules can be added here without modifying Ledger.
 */
public class LedgerValidator {

    private final Ledger ledger;

    public LedgerValidator(Ledger ledger) {
        this.ledger = ledger;
    }

    public void validate() throws LedgerException {

        if (ledger.getBlockMap().isEmpty()) {
            throw new LedgerException("Validate", "No Block Has Been Committed");
        }

        Block committedBlock = ledger.getBlockMap().lastEntry().getValue();
        Map<String, Account> accountMap = committedBlock.getAccountBalanceMap();
        List<Account> accountList = new java.util.ArrayList<>(accountMap.values());

        int totalBalance = 0;
        for (Account account : accountList) {
            totalBalance += account.getBalance();
        }

        int fees = 0;
        for (Integer key : ledger.getBlockMap().keySet()) {
            Block block = ledger.getBlockMap().get(key);

            // Check for Hash Consistency
            if (block.getBlockNumber() != 1)
                if (!block.getPreviousHash().equals(block.getPreviousBlock().getHash())) {
                    throw new LedgerException("Validate", "Hash Is Inconsistent: " + block.getBlockNumber());
                }

            // Check for Transaction Count
            if (block.getTransactionList().size() != 10) {
                throw new LedgerException("Validate", "Transaction Count Is Not 10 In Block: " + block.getBlockNumber());
            }

            for (Transaction transaction : block.getTransactionList()) {
                fees += transaction.getFee();
            }
        }

        int adjustedBalance = totalBalance + fees;

        // Check for account balances against the total
        if (adjustedBalance != Integer.MAX_VALUE) {
            throw new LedgerException("Validate", "Balance Does Not Add Up");
        }
    }
}
