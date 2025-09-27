package com.se310.ledger;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * MerkleTree Implementation
 *
 * @author  Nikhil GOYAL
 * @code https://github.com/goyalnikhil02/MerkleTree/blob/master/src/com/example/MerkleTrees.java
 */
public class MerkleTrees {

    // Immutable list of leaves (hex-strings representing transactions or node hashes)
    private final List<String> leaves;

    // Computed merkle root (hex string)
    private final String root;

    // Hashing strategy (injected for testability and algorithm swap)
    private final HashStrategy hashStrategy;

    /**
     * Construct and compute the Merkle root using the default SHA-256 strategy.
     * @param txList list of transaction hex strings or raw strings
     */
    public MerkleTrees(List<String> txList) {
        this(txList, new SHA256HashStrategy());
    }

    /**
     * Construct and compute the Merkle root with an injected hash strategy.
     */
    public MerkleTrees(List<String> txList, HashStrategy hashStrategy) {
        this.leaves = new ArrayList<>(txList == null ? List.of() : txList);
        this.hashStrategy = hashStrategy == null ? new SHA256HashStrategy() : hashStrategy;
        this.root = computeRoot(new ArrayList<>(this.leaves));
    }

    private String computeRoot(List<String> tempTxList) {
        if (tempTxList.isEmpty()) return "";

        List<String> newTxList = getNewTxList(tempTxList);
        while (newTxList.size() > 1) {
            newTxList = getNewTxList(newTxList);
        }

        return newTxList.get(0);
    }

    private List<String> getNewTxList(List<String> tempTxList) {
        List<String> newTxList = new ArrayList<>();
        int index = 0;
        while (index < tempTxList.size()) {
            String left = tempTxList.get(index);
            index++;

            String right = "";
            if (index != tempTxList.size()) {
                right = tempTxList.get(index);
            }

            String sha2HexValue = hashStrategy.hash(left + right);
            newTxList.add(sha2HexValue);
            index++;
        }

        return newTxList;
    }

    /**
     * Return hex string
     *
     * @param str
     * @return
     */
    public String getSHA2HexValue(String str) {
        byte[] cipher_byte;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(str.getBytes());
            cipher_byte = md.digest();
            StringBuilder sb = new StringBuilder(2 * cipher_byte.length);
            for (byte b : cipher_byte) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Get Root
     *
     * @return
     */
    public String getRoot() {
        return this.root;
    }

}
