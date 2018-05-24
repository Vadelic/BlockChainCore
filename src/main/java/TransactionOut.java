import org.apache.commons.codec.digest.DigestUtils;

import java.security.PublicKey;
import java.util.Base64;

/**
 * Created by 14675742 on 23.05.2018.
 */
public class TransactionOut {
    public String id;
    public String parentTransactionId; //the id of the transaction this output was created in
    public PublicKey recipient; //also known as the new owner of these coins.
    public float value; //the amount of coins they own

    //Constructor
    public TransactionOut(PublicKey recipient, float value, String parentTransactionId) {
        this.id = DigestUtils.sha256Hex(Base64.getEncoder().encodeToString(recipient.getEncoded()) + Float.toString(value) + parentTransactionId);
        this.parentTransactionId = parentTransactionId;
        this.recipient = recipient;
        this.value = value;
    }

    //Check if coin belongs to you
    public boolean isMine(PublicKey publicKey) {
        return (publicKey.equals(recipient));
    }
}
