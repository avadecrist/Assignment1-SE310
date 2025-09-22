package com.se310.ledger;

/**
 * LedgerException class implementation designed display errors for Ledger operations
 *
 * @author  Sergey L. Sundukovskiy
 * @version 1.0
 */
public class LedgerException extends Exception {
    // Make the exception immutable: action and line/ reason are final and set at construction.
    // Calling super(message) ensures getMessage() returns the reason and avoids duplication.
    private static final long serialVersionUID = 1L;

    private final String action;

    /**
     * Construct a LedgerException with an action and a reason. Stores action and sets the
     * Throwable message to reason. This class is intentionally immutable to follow SRP and
     * make exceptions safe to pass between layers without introducing side-effects.
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

    /**
     * For backward compatibility callers that previously used getReason(), provide a
     * simple accessor that delegates to getMessage(). Prefer getMessage() in new code.
     */
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
