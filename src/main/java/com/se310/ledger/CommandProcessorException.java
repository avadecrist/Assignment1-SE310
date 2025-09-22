package com.se310.ledger;

/**
 * CommandProcessorException class implementation designed display errors to the user while
 * processing commands
 *
 * @author  Sergey L. Sundukovskiy
 * @version 1.0
 */
public class CommandProcessorException extends Exception {
    // serialVersionUID for Exception serialization compatibility
    private static final long serialVersionUID = 1L;

    // Immutable payload describing the failing command; kept final to follow immutability
    private final String command;

    // lineNumber is optional; -1 means not provided. final to make the exception immutable.
    private final int lineNumber;

    /**
     * Construct an exception with the command and reason (message).
     * We call super(reason) so the standard Exception message is set.
     * SOLID rationale: make the exception a simple immutable value object (Single Responsibility,
     * and safer to pass between layers).
     *
     * @param command the command text that caused the error
     * @param reason human-readable reason / message
     */
    public CommandProcessorException(String command, String reason) {
        super(reason);
        this.command = command;
        this.lineNumber = -1;
    }

    /**
     * Construct an exception with command, reason and a line number where it occurred.
     * @param command the command text that caused the error
     * @param reason human-readable reason / message
     * @param lineNumber optional 1-based line number (or -1 if unknown)
     */
    public CommandProcessorException(String command, String reason, int lineNumber) {
        super(reason);
        this.command = command;
        this.lineNumber = lineNumber;
    }

    /**
     * Construct an exception with a cause.
     * @param command the command text that caused the error
     * @param reason human-readable reason / message
     * @param cause the root cause
     */
    public CommandProcessorException(String command, String reason, Throwable cause) {
        super(reason, cause);
        this.command = command;
        this.lineNumber = -1;
    }

    /**
     * Getter for the original command text. Immutable (no setter provided).
     * @return the command text that caused the error
     */
    public String getCommand() {
        return command;
    }

    /**
     * The Exception message (reason) is available via getMessage() from Throwable.
     * We intentionally do not duplicate it here as a mutable field.
     */

    /**
     * Getter for the optional line number; -1 if not provided.
     * @return line number or -1
     */
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        return "CommandProcessorException{" +
                "command='" + command + '\'' +
                ", message='" + getMessage() + '\'' +
                ", lineNumber=" + lineNumber +
                '}';
    }
}
