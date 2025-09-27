package com.se310.ledger;

/**
 * Transaction class implementation representing operation in the Blockchain
 *
 * @author  Sergey L. Sundukovskiy
 * @version 1.0
 */
public class Transaction {

    // Make fields final to make Transaction immutable SRP
    private final String transactionId;
    private final Integer amount;
    private final Integer fee;
    private final String note;
    private final String payerAddress;
    private final String receiverAddress;

    /**
     * Constructor for Transaction
     * @param transactionId
     * @param amount
     * @param fee
     * @param note
     * @param payer
     * @param receiver
     */
    /**
     * Invariants are enforced in const
     * Comments: validate inputs here to keep Transaction instances always valid (Single Responsibility).
     */
    public Transaction(String transactionId, Integer amount, Integer fee, String note, String payerAddress, String receiverAddress) {
        // Validate transactionId (cannot be null/empty) - defensive programming
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("transactionId must be provided");
        }

        // Validate amount
        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("amount must be a non-negative Integer");
        }

        // Validate fee
        if (fee == null || fee < 0) {
            throw new IllegalArgumentException("fee must be a non-negative Integer");
        }

        // Validate note 
        if (note == null) {
            note = ""; // normalize null to empty
        }

        // Validate addresses 
        if (payerAddress == null || payerAddress.trim().isEmpty() || receiverAddress == null || receiverAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("payerAddress and receiverAddress must be provided");
        }

        this.transactionId = transactionId;
        this.amount = amount;
        this.fee = fee;
        this.note = note;
        this.payerAddress = payerAddress;
        this.receiverAddress = receiverAddress;
    }

    /**
     * Getter method for transaction id
     * @return
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Setter method for transaction id
     * @param transactionId
     */
    // Removed setter for transaction immutability

    /**
     * Getter method for transaction amount
     * @return
     */
    public Integer getAmount() {
        return amount;
    }

    /**
     * Setter method for transaction amount
     * @param amount
     */
    // Setter removed

    /**
     * Getter method for transaction fee
     * @return
     */
    public Integer getFee() {
        return fee;
    }

    /**
     * Setter method for transaction fee
     * @param fee
     */
    // Setter removed

    /**
     * Getter method for transaction note
     * @return
     */
    public String getNote() {
        return note;
    }

    /**
     * Setter method for transaction note
     * @param note
     */
    // Setter removed

    /**
     * Getter method for payer
     * @return
     */
    // Getter returns payer address; Transaction doesnt show Account objects (decoupled)
    public String getPayerAddress() {
        return payerAddress;
    }

    /**
     * Setter method for payer
     * @param payer
     */
    // Setter removed

    /**
     * Getter method for receiver
     * @return
     */
    // Getter returns receiver address; Transaction doesnt show Account objects
    public String getReceiverAddress() {
        return receiverAddress;
    }

    /**
     * Setter method for receiver
     * @param receiver
     */
    // Setter removed:

    /**
     * Method used by MerkleTrees
     * @return
     */
    @Override
    public String toString() {
    // keep the representation stable; using immutable fields ensures consistency when toString is called
    // Use stored addresses in representation; works consistently because Transaction is immutable
    return "Transaction Id: " + transactionId +
        ", Amount: " + amount +
        ", Fee: " + fee +
        ", Note: " + note +
        ", Payer: " + payerAddress +
        ", Receiver: " + receiverAddress;
    }

    /**
     * Implement equals and hashCode so Transaction can be used safely in collections and comparisons.
     * Equality is based on transactionId which is treated as unique identifier in the system.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        return transactionId.equals(that.transactionId);
    }

    @Override
    public int hashCode() {
        return transactionId.hashCode();
    }
}

