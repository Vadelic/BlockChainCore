/**
 * Created by 14675742 on 23.05.2018.
 */
public class TransactionIn {
    public String transactionOutputId; //Reference to TransactionOutputs -> transactionId
    public TransactionOut UTXO = null; //Contains the Unspent transaction output

    public TransactionIn(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
