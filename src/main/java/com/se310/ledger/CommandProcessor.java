package com.se310.ledger;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * CommandProcessor class implementation designed to process individual Blockchain commands
 *
 * @author  Sergey L. Sundukovskiy
 * @version 1.0
 */
public class CommandProcessor {
    // Make ledger an instance field so CommandProcessor instances don't share one state.
    //DIP & SRP - allow multiple processors/ledgers and easier testing.
    private Ledger ledger = null;


    public void processCommand(String command) throws CommandProcessorException {

        // Tokenize the incoming command using a helper to separate concerns (SRP).
        List<String> tokens = tokenize(command);

    switch (tokens.get(0)) {
            case "create-ledger" -> {
                if(tokens.size() != 6)
                    throw new CommandProcessorException("create-ledger", "Missing Arguments");

                System.out.println("Creating Ledger: " + tokens.get(1) + " " + tokens.get(3) + " " + tokens.get(5));
                ledger = Ledger.getInstance(tokens.get(1), tokens.get(3), tokens.get(5));
            }
            case "create-account" -> {
                if(tokens.size() != 2)
                    throw new CommandProcessorException("create-account", "Missing Arguments");

                System.out.println("Creating Account: " + tokens.get(1));
                try {
                    ledger.createAccount(tokens.get(1));
                } catch (LedgerException e) {
                    System.out.println("Failed due to: " + e.getReason());
                }
            }
            case "get-account-balance" -> {
                if(tokens.size() != 2)
                    throw new CommandProcessorException("create-account", "Missing Arguments");

                System.out.println("Getting Balance for: " + tokens.get(1));
                try {
                    System.out.println("Account Balance for: " + tokens.get(1) + " is "
                            + ledger.getAccountBalance(tokens.get(1)));

                } catch (LedgerException e) {
                    System.out.println("Failed due to: " + e.getReason());
                }
            }
            case "get-account-balances" -> {
                System.out.println("Getting All Balances");

                Map<String,Integer> map = ledger.getAccountBalances();

                if(map == null){
                    System.out.println("No Account Has Been Committed");
                    break;
                }

                Set<String> keys = new HashSet<>(map.keySet());

                for (String key : keys) {
                    System.out.println("Account Balance for: " + key + " is " + map.get(key));
                }
            }
            case "process-transaction" -> {

                if(tokens.size() != 12)
                    throw new CommandProcessorException("process-transaction", "Missing Arguments");

                System.out.println("Processing Transaction: " + tokens.get(1) + " "
                        + tokens.get(3) + " " + tokens.get(5) + " " + tokens.get(7) + " "
                        + tokens.get(9) + " " + tokens.get(11) + " ");

                Block block = ledger.getUncommittedBlock();

                Account payer = block.getAccount (tokens.get(9));
                Account receiver = block.getAccount(tokens.get(11));

                if(payer == null || receiver == null){
                    throw new CommandProcessorException("process-transaction", "Account Does Not Exist") ;
                }

        // Construct Transaction with addresses instead of Account objects to keep Transaction immutable
        Transaction tempTransaction = new Transaction(tokens.get(1), Integer.parseInt(tokens.get(3)),
            Integer.parseInt(tokens.get(5)), tokens.get(7), payer.getAddress(), receiver.getAddress());
                try {
                    ledger.processTransaction(tempTransaction);
                } catch (LedgerException e) {
                    System.out.println("Failed due to: " + e.getReason());
                }
            }
            case "get-block" -> {

                if(tokens.size() != 2)
                    throw new CommandProcessorException("get-block", "Missing Arguments");

                System.out.println("Get Block: " + tokens.get(1));
                Block block = null;
                try {
                    block = ledger.getBlock(Integer.parseInt(tokens.get(1)));
                } catch (LedgerException e) {
                    System.out.println("Failed due to: " + e.getReason());
                    break;
                }

                System.out.println("Block Number: " + block.getBlockNumber() + " "
                        + "Hash: " + block.getHash() + " " + "Previous Hash: " + block.getPreviousHash()
                );

                for(Transaction transaction: block.getTransactionList()){
                    System.out.println(transaction.toString());
                }

            }
            case "get-transaction" -> {

                if(tokens.size() != 2)
                    throw new CommandProcessorException("get-transaction", "Missing Arguments");

                System.out.println("Get Transaction: " + tokens.get(1));
                Transaction transaction = ledger.getTransaction(tokens.get(1));

                System.out.println("Transaction ID: " + transaction.getTransactionId() + " "
                        + "Amount: " + transaction.getAmount() + " " + "Fee: "
                        + transaction.getFee() + " " + "Note: " + transaction.getNote() + " " + "Payer: "
                        + transaction.getPayerAddress() + " " + "Receiver: "
                        + transaction.getReceiverAddress()
                );
            }
            case "validate" -> {
                System.out.print("Validate: ");
                try {
                    ledger.validate();
                    System.out.println("Valid");
                } catch (LedgerException e) {
                    System.out.println("Failed due to: " + e.getReason());
                }

            }
            default ->  {
                throw new CommandProcessorException(tokens.get(0), "Invalid Command");

            }


        }

    }

    /**
     * Tokenize the raw command string into a List of tokens.
     *parsing concerns are separated from command execution (SRP).
     */
    private List<String> tokenize(String command) {
        List<String> tokens = new ArrayList<>();
        // Split the line into tokens between spaces and quotes
        Matcher matcher = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(command);
        while (matcher.find())
            tokens.add(matcher.group(1).replace("\"", ""));
        return tokens;
    }

    /**
     * Process File from the command line
     */
    public void processCommandFile(String fileName){

        // Removed unused tokens variable; delegate to processCommand
        AtomicInteger atomicInteger = new AtomicInteger(0);

        //Process all lines in the file
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream
                    .forEach(line -> {
                        try {
                            atomicInteger.getAndIncrement();
                            if(!line.trim().startsWith("#") && !line.trim().isEmpty()) {
                                processCommand(line);
                            }
                        } catch (CommandProcessorException e) {
                            int currentLine = atomicInteger.get();
                            System.out.println("Failed due to: " + e.getMessage() + " for Command: " + e.getCommand()
                                    + " On Line Number: " + currentLine);
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
