package com.se310.ledger;

/**
 * Strategy interface for hashing strings.
 * Allows swapping hashing implementations (DIP) for testability or algorithm upgrades.
 */
public interface HashStrategy {
    /**
     * Compute a hex-encoded hash for the given input string.
     * @param input text to hash
     * @return lowercase hex encoding of the digest
     */
    String hash(String input);
}
