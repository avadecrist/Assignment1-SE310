package com.se310.ledger;

/**
 * LedgerException class implementation designed display errors for Ledger operations
 *
 * @author  Sergey L. Sundukovskiy
 * @version 1.0
 */
public class LedgerException extends Exception {
    private static final long serialVersionUID = 1L;

    private final String action;

    /**
     * Construct a LedgerException with an action and a reason.
     * Immutability: action and reason are final and set at construction.
     *
     * @param action short action identifier (e.g., "create-account")
     * @param reason human-readable reason
     */
    public LedgerException(String action, String reason) {
        super(reason);
        this.action = action;
    }

    /**
     * Construct a LedgerException with a cause.
     */
    public LedgerException(String action, String reason, Throwable cause) {
        super(reason, cause);
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    //getReason for backward compatibility
    public String getReason() {
        return getMessage();
    }

    @Override
    public String toString() {
        return "LedgerException{" +
                "action='" + action + '\'' +
                ", reason='" + getMessage() + '\'' +
                '}';
    }
}
