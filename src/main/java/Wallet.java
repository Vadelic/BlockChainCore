import exception.WalletException;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;

public class Wallet {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    public HashMap<String, TransactionOut> UTXOs = new HashMap<>(); //only UTXOs owned by this wallet.

    public Wallet() {
        generateKeyPair();
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    private void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random);   //256 bytes provides an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();
            // Set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Transaction createTransaction(Wallet destination, float value) throws WalletException {
        if (getBalance() < value) {
            throw new WalletException("Not Enough funds to send");
        } else {
            ArrayList<TransactionIn> inputs = new ArrayList<>();
            float total = 0;
            for (TransactionOut output : UTXOs.values()) {
                total += output.value;
                inputs.add(new TransactionIn(output.id));
                if (total > value) break;
            }


            Transaction transaction = new Transaction(getPublicKey(), privateKey, destination.getPublicKey(), value, null);
            for (TransactionIn input : inputs) {
                UTXOs.remove(input.transactionOutputId);
            }
            return transaction;
        }
    }


    public float getBalance() {
        return (float) Chain.UTXOs.values().stream().filter(s -> s.isMine(publicKey)).mapToDouble(s -> s.value).sum();
    }


}