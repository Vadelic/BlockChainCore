import java.util.ArrayList;

import com.google.gson.GsonBuilder;

public class Chain {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static int difficulty = 2;

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        //loop through blockchain to check hashes:
        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);

            //compare registered hash and calculated hash:
            if (!currentBlock.isValidBlock()) {
                System.out.println("Current Hashes not equal" + currentBlock);
                return false;
            }
            //compare previous hash and registered previous hash
            if (!currentBlock.isValidPreviousBlock(previousBlock)) {
                System.out.println("Previous Hashes not equal" + previousBlock);
                return false;
            }

            if (!currentBlock.isValidDifficulty(difficulty)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
        }
        return true;
    }


    public static void main(String[] args) {
        //add our blocks to the blockchain ArrayList:
        blockchain.add(Block.getGenesisBlock());
        System.out.println("Trying to Mine block 1... ");
        blockchain.get(0).mineBlock(difficulty);

        blockchain.add(new Block("Yo im the second block", blockchain.get(blockchain.size() - 1)));
        System.out.println("Trying to Mine block 2... ");
        blockchain.get(1).mineBlock(difficulty);

        blockchain.add(new Block("Hey im the third block", blockchain.get(blockchain.size() - 1)));
        System.out.println("Trying to Mine block 3... ");
        blockchain.get(2).mineBlock(difficulty);

        if (isChainValid()) {
            String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
            System.out.println(blockchainJson);
        } else {
            System.out.println("Invalid Chain");
        }
    }

}