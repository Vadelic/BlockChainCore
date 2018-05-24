import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.GsonBuilder;

public class Chain {
    private ArrayList<Block> blockChain = new ArrayList<Block>();
    private int difficulty;
    public static HashMap<String, TransactionOut> UTXOs = new HashMap<>(); //list of all unspent transactions.
    public static float minimumTransaction = 0.1f;
    public static Transaction genesisTransaction;

    public Chain(Block genesisBlock, int difficulty) {
        this.difficulty = difficulty;

        genesisBlock.mineBlock(difficulty);
        blockChain.add(genesisBlock);
    }

    public Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        HashMap<String, TransactionOut> tempUTXOs = new HashMap<>(); //a temporary working list of unspent transactions at a given block state.
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        for (int i = 1; i < blockChain.size(); i++) {
            currentBlock = blockChain.get(i);
            previousBlock = blockChain.get(i - 1);

            //compare registered hash and calculated hash:
            if (!currentBlock.isValidBlock()) {
                System.out.println("Current Hashes not equal" + currentBlock);
                return false;
            }
            //compare previous hash and registered previous hash
            if (!currentBlock.isValidPreviousBlock(previousBlock)) {
                System.out.println("Previous Hashes not equal previous:" + previousBlock + "\ncurrent:" + currentBlock);
                return false;
            }

            if (!currentBlock.isValidDifficulty(difficulty)) {
                System.out.println("This block hasn't been mined");
                return false;
            }

            //loop thru blockchains transactions:
            TransactionOut tempOutput;
            for (int t = 0; t < currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if (!currentTransaction.verifySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for (TransactionIn input : currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if (tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if (input.UTXO.value != tempOutput.value) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for (TransactionOut output : currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if (currentTransaction.outputs.get(0).recipient != currentTransaction.recipient) {
                    System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
                    return false;
                }
                if (currentTransaction.outputs.get(1).recipient != currentTransaction.sender) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }
            }
        }
        return true;
    }

    public void addBlock(Block block) {
        System.out.println("Try to mine..");
        block.mineBlock(difficulty);
        blockChain.add(block);

    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}