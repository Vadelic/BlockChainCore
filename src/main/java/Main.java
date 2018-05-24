import exception.WalletException;

import java.security.Security;

/**
 * Created by 14675742 on 23.05.2018.
 */
public class Main {
    public static void main(String[] args) throws WalletException {

        //Setup Bouncey castle as a Security Provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        //Create the new wallets
        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();

        Transaction genesisTx = walletA.createTransaction(walletB, 0);
        Transaction.buildGenesisTx(genesisTx);
        Block genesisBlock = Block.getGenesisBlock();
        genesisBlock.addTransaction(genesisTx);
        System.out.println("Creating and Mining Genesis block... ");

//        System.out.println(chain);
        System.out.println("Is signature verified");
        System.out.println(genesisTx.verifySignature());

        Chain chain = new Chain(genesisBlock, 2);
        System.out.println();

        //testing
        Block block1 = new Block(genesisBlock);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        Transaction transaction = walletA.createTransaction(walletB, 40f);
        block1.addTransaction(transaction);
        chain.addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block2 = new Block(block1);
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.createTransaction(walletB, 1000f));
        chain.addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2);
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.createTransaction( walletA, 20));
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        chain.isChainValid();

    }
}
