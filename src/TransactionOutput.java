import java.security.PrivateKey;
import java.security.PublicKey;

public class TransactionOutput {


    public String id;
    public PublicKey reciepent;
    public float value;
    public String parentTransactionId;

    //Constructor
    public TransactionOutput(PublicKey reciepent,float value, String parentTransactionId){
        this.reciepent = reciepent;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(reciepent) + Float.toString(value)+parentTransactionId);
    }


    //Check if coin belongs to you
    public boolean isMine(PublicKey publicKey){
            return (publicKey == reciepent);
    }
    
}
