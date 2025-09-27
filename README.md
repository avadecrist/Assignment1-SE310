Kalyan Fernande, Ava Decristofaro, Zachary Colby, AB Owusu Agmeyang
Chapman University
09/26
SE 300

Class by class analysis of our changes

CommandProcessorException
Removed setters, Message passed through addded constructors
serialVersionUID and clear toString()
Follows SRP, LSP, and OCP more closely

LedgerException
Removed setters- Immutability
getReason() still exists, but really just returns getMessage()- potential backwards compatibility issues
Follows SRP and OCP, overall safer

Hash Strategy/SHA256HashStrategy
Interface HashStrategy and concrete SHA256HashStrategy
MerkleTrees depends on HashStrategy, not on one hashing algorithm
DIP: high level merkletree construction doesn't depend on low level (SHA-256). 
SRP: Hashing logic is now away from the tree construction
In short, we can change our hashing alot easier now.

MerkleTrees
Now accepts a hashstrategy- default SHA256
Immutable- root 
SRP: Merkletrees instance represents a specifc instance of leaves and root
DIP & OCP: Class is closed for modification but can be extended if needed

TransactionProcessor
Takes in MerkleTrees, transactions are now immutable
SRP: Hashing occurs elsewherre
DIP: Abstractions through Hashstrategy/MerkleTrees
OCP: New hashing can be added more easily

Block:
Removed setBlockNumber to keep block identity immutable after creation.
SRP and Encapsulation in general, LSP

Transaction
Decoupled Transaction from Account
Immutability- Setter Removal
SRP/OCP/DIP

Account
Removed Setters and getters
address is final
balance only now mutable through credit and debit
behavior of function can be extended without messing directly with setBalance()
enforcement of invariants
SRP, OCP, LSP

Ledger
blockMap and uncommittedBlock are instance fields initialized in the constructor
collaborators operate on the specific Ledger instance.
SRP, DIP

CommandProcessor
Blockmap and Uncommited were changed from being declared as static and initialized in static block to instance fields created in constructor.
Each ledger object manages its state. Static initializer and global removed
SRP and DIP. safer concurrency between different ledgers running

To run mvn test -DskipTests=false
No issues we are aware of