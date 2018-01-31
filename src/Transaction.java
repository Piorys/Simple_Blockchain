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

    //Returns true if new transation can be created
    public boolean processTransaction(){
       if(!verifySignature()){
           System.out.println("#Transaction signature failed to verify");
           return false;

        }
        // gather transaction inputs, make sure they are unspent
        for(TransactionInput i : inputs){
           i.UTXO = Main.UTXOs.get(i.transactionOutputId);
        }

        //check if transaction is valid
        if(getInputsValue() < Main.minimumTransaction) {
            System.out.println("#Transaction inputs too small: "+ getInputsValue());
        }

        //Generate transaction outputs
        float leftOver = getInputsValue() - value;
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.reciepent,value,transactionId));
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));

        //add outputs to Unspent list
        for(TransactionOutput o : outputs){
            Main.UTXOs.put(o.id,o);
        }

        //Remove transaction inputs from UTXO lists as spent
        for(TransactionInput i :inputs){
            if(i.UTXO == null) continue;
            Main.UTXOs.remove(i.UTXO.id);
        }

    return true;
    }

    //Return sum of inputs(UTXOs) values
    public float getInputsValue(){
        float total = 0;
        for(TransactionInput i : inputs){
            if(i.UTXO==null) continue;
            total += i.UTXO.value;
        }
        return total;
    }

    //Return sum of outputs
    public float getOutputsValue(){
        float total = 0;
        for(TransactionOutput o: outputs){
            total += o.value;
        }
        return total;
    }

}
