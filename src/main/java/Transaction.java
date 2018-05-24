import org.apache.commons.codec.digest.DigestUtils;

import java.security.*;
import java.util.ArrayList;
import java.util.Base64;

public class Transaction {

    public String transactionId; // this is also the hash of the transaction.
    private static int sequence = 0; // a rough count of how many transactions have been generated.

    public PublicKey sender; // senders address/public key.
    public PublicKey recipient; // Recipients address/public key.
    public float value;
    public byte[] signature; // this is to prevent anybody else from spending funds in our wallet.

    public ArrayList<TransactionIn> inputs;
    public ArrayList<TransactionOut> outputs = new ArrayList<>();


    // Constructor:
    public Transaction(PublicKey from, PrivateKey privateKey, PublicKey to, float value, ArrayList<TransactionIn> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
        generateSignature(privateKey);
    }

    public static void buildGenesisTx(Transaction transaction) {
        transaction.transactionId = "0";
        TransactionOut output = new TransactionOut(transaction.recipient, transaction.value, transaction.transactionId);
        transaction.outputs.add(output); //manually add the Transactions Output
        Chain.UTXOs.put(transaction.outputs.get(0).id, transaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.
        System.out.println("Creating and Mining Genesis block... ");
    }

    // This Calculates the transaction hash (which will be used as its Id)
    private String calculateHash() {
        return DigestUtils.sha256Hex(getSignatureData() + ++sequence);
    }

    private String getSignatureData() {
        String sender64 = Base64.getEncoder().encodeToString(sender.getEncoded());
        String recipient64 = Base64.getEncoder().encodeToString(recipient.getEncoded());
        return sender64 + recipient64 + Float.toString(value);
    }

    //Signs all the data we dont wish to be tampered with.
    private void generateSignature(PrivateKey privateKey) {
        signature = SignatureUtil.applyECDSASig(privateKey, getSignatureData());
    }

    //Verifies the data we signed hasnt been tampered with
    public boolean verifySignature() {
        return SignatureUtil.verifyECDSASig(sender, getSignatureData(), signature);
    }


    public boolean processTransaction() {

        if (!verifySignature()) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        //gather transaction inputs (Make sure they are unspent):
        for (TransactionIn input : inputs) {
            input.UTXO = Chain.UTXOs.get(input.transactionOutputId);
        }

        //check if transaction is valid:
        if (getInputsValue() < Chain.minimumTransaction) {
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        //generate transaction outputs:
        float leftOver = getInputsValue() - value; //get value of inputs then the left over change:
        transactionId = calculateHash();
        outputs.add(new TransactionOut(this.recipient, value, transactionId)); //send value to recipient
        outputs.add(new TransactionOut(this.sender, leftOver, transactionId)); //send the left over 'change' back to sender

        //add outputs to Unspent list
        for (TransactionOut output : outputs) {
            Chain.UTXOs.put(output.id, output);
        }

        //remove transaction inputs from UTXO lists as spent:
        for (TransactionIn input : inputs) {
            if (input.UTXO == null) continue; //if Transaction can't be found skip it
            Chain.UTXOs.remove(input.UTXO.id);
        }

        return true;
    }

    //returns sum of inputs(UTXOs) values
    public float getInputsValue() {
        float total = 0;
        for (TransactionIn input : inputs) {
            if (input.UTXO == null) continue; //if Transaction can't be found skip it
            total += input.UTXO.value;
        }
        return total;
    }

    //returns sum of outputs:
    public float getOutputsValue() {
        float total = 0;
        for (TransactionOut output : outputs) {
            total += output.value;
        }
        return total;
    }
}