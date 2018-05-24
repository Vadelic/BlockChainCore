import com.google.gson.GsonBuilder;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.Date;

public class Block implements ValidateBlock {
    private static final Block GENESIS = new Block();
    private String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>(); //our data will be a simple message.

    private long timeStamp;
    private String hash;
    private int nonce;

    public Block(Block previousBlock) {
        this.previousHash = previousBlock.getHash();

        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    private Block() {
        this.previousHash = "0";
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public static Block getGenesisBlock() {
        return GENESIS;
    }

    private String calculateHash() {
        return DigestUtils.sha256Hex(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
    }

    private String getPreviousHash() {
        return previousHash;
    }

    private String getHash() {
        return hash;
    }

    public boolean isValidBlock() {
        return getHash().equals(calculateHash());
    }

    public boolean isValidPreviousBlock(ValidateBlock previousBlock) {
        return ((Block) previousBlock).getHash().equals(getPreviousHash());
    }

    public boolean isValidDifficulty(int difficulty) {
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        return hash.substring(0, difficulty).equals(hashTarget);
    }

    public void mineBlock(int difficulty) {
        merkleRoot = getMerkleRoot(transactions);
        String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0"
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

    private static String getMerkleRoot(ArrayList<Transaction> transactions) {
        int count = transactions.size();
        ArrayList<String> previousTreeLayer = new ArrayList<>();
        for (Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.transactionId);
        }
        ArrayList<String> treeLayer = previousTreeLayer;
        while (count > 1) {
            treeLayer = new ArrayList<>();
            for (int i = 1; i < previousTreeLayer.size(); i++) {
                treeLayer.add(DigestUtils.sha256Hex(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }
        return (treeLayer.size() == 1) ? treeLayer.get(0) : "";
    }

    public void addTransaction(Transaction transaction) {
        //process transaction and check if valid, unless block is genesis block then ignore.
        if (transaction == null)
            return;
        if (!previousHash.equals("0")) {
            if ((!transaction.processTransaction())) {
                System.out.println("Transaction failed to process. Discarded.");
                return;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
    }
}