import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {

    public PrivateKey privateKey;
    public PublicKey publicKey;
    public HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>(); //only UTXOs owned by this wallet.

    public Wallet(){
        generateKeyPair();
    }

    public void generateKeyPair(){
        try{

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec,random); // 256bytes provide an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();
            //set up public variables
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    // Returns Balance and stores the UTXO's owned by this wallet in this.utxo

    public float getBalance(){
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item: Main.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)){
                UTXOs.put(UTXO.id,UTXO);
                total += UTXO.value;
            }
        }
        return total;
    }

    //Generates and returns a new transacton from this wallet.
    public Transaction sendFunds(PublicKey _reciepent,float value){

        if(getBalance() < value){ //gather balance and check funds
            System.out.println("#Not enough funds to send transaction. Transaction denied");
            return  null;
        }

        //Create array of inputs

        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

        float total =0;

        for(Map.Entry<String,TransactionOutput> item: UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if(total>value) break;
        }

        Transaction newTransaction = new Transaction(publicKey,_reciepent,value,inputs);
        newTransaction.generateSignature(privateKey);

        for(TransactionInput input:inputs){
            UTXOs.remove(input.transactionOutputId);
        }

        return newTransaction;
    }

}
