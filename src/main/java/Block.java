import org.apache.commons.codec.digest.DigestUtils;

import java.util.Date;

public class Block implements ChainBlock {
    private static final Block GENESIS = new Block();
    private String previousHash;
    private String data;
    private long timeStamp;
    private String hash;
    private int nonce;

    public Block(String data, Block previousBlock) {
        this.previousHash = previousBlock.getHash();
        this.data = data;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    private Block() {
        this.previousHash = "0";
        this.data = "GenesisBlock";
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public static Block getGenesisBlock() {
        return GENESIS;
    }

    private String calculateHash() {
        return DigestUtils.sha256Hex(previousHash + Long.toString(timeStamp) + data + Integer.toString(nonce));
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

    public boolean isValidPreviousBlock(ChainBlock previousBlock) {
        return ((Block) previousBlock).getHash().equals(getPreviousHash());
    }

    public boolean isValidDifficulty(int difficulty) {
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        return hash.substring(0, difficulty).equals(hashTarget);
    }


    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0"
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }

    @Override
    public String toString() {
        return "Block{" +
                "previousHash='" + previousHash + '\'' +
                ", data='" + data + '\'' +
                ", hash='" + hash + '\'' +
                ", nonce=" + nonce +
                '}';
    }
}