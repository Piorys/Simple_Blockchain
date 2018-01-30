import java.security.*;
import java.util.ArrayList;


public class Transaction {

    public String transactionId;
    public PublicKey sender;
    public PublicKey reciepent;
    public float value;
    public byte[] signature;

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence = 0;

    //Constructor
    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs){
        this.sender = from;
        this.reciepent = to;
        this.value = value;
        this.inputs = inputs;
    }

    //Calculate the transaction hash

    public String calculateHash(){
        sequence++;
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(reciepent) +
                        Float.toString(value) + sequence
        );

    }

    //Signs all the data
    public byte[] generateSignature(PrivateKey privateKey){
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepent) + Float.toString(value);
        return signature = StringUtil.applyEDCASig(privateKey,data);

    }

    //Verifies the data
    public boolean verifySignature(){
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepent) + Float.toString(value);
        return StringUtil.verifyECDSASig(sender,data,signature);
    }

}
