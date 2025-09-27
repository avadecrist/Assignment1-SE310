package com.se310.ledger;

import java.security.MessageDigest;

/**
 * Default SHA-256 hashing strategy used for Merkle tree nodes.
 */
public class SHA256HashStrategy implements HashStrategy {

    @Override
    public String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(input.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder(2 * digest.length);
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            //failsafe
            return "";
        }
    }
}
