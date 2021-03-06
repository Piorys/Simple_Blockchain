import java.util.ArrayList;
import java.util.Date;


public class Block {

    public String hash;
    public String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>(); //our data will be a simple message.
    private long timeStamp;
    private int nonce;

    public Block(String previousHash){

        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();

    }

    public String calculateHash(){
        String calculatedHash = StringUtil.applySha256(
                previousHash +
                Long.toString(timeStamp) +
                Integer.toString(nonce) +
                merkleRoot

        );
        return calculatedHash;
    }

    public void mineBlock(int difficulty){
        String target = new String(new char[difficulty]).replace('\0','0');
        while(!hash.substring(0, difficulty).equals(target)){
            nonce ++;
            hash = calculateHash();
        }
        System.out.println("Block mined!!! : "+ hash );

    }

    // Add transaction to this block

    public boolean addTransaction(Transaction transaction){
        //process the transaction and check if valid, unless is genesis block, then ignore
        if(transaction == null) return false;
        if((previousHash != "0")){
            if((transaction.processTransaction() != true)){
                System.out.println("Transaction failed to process, discarted");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction succesfully added to block");
        return true;
    }

}
