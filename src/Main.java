import com.google.gson.GsonBuilder;

import java.security.Security;
import java.util.ArrayList;
import java.util.Base64;
import com.google.gson.GsonBuilder;

public class Main {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static int difficulty = 5; //Difficulty setting for mining purposes
    public static Wallet walletA;
    public static Wallet walletB;

    public static void main(String[] args) {

        //Setup Bouncey as a security provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        //Create new Wallets
        walletA = new Wallet();
        walletB = new Wallet();
        System.out.println("Private and Public Keys:");
        System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
        System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
        //Create test transaction from wallet a to wallet b
        Transaction transaction = new Transaction(walletA.publicKey,walletB.publicKey,5,null);
        transaction.signature = transaction.generateSignature(walletA.privateKey);
        //Verify that signature works and verify it from public key
        System.out.println("Is signature verified? : ");
        System.out.println(transaction.verifySignature());








//        blockchain.add(new Block("Hi! Im the first Block!", "0"));
//        System.out.println("Trying to Mine Block 1...");
//        blockchain.get(0).mineBlock(difficulty);
//
//        blockchain.add(new Block("Hi! Im the second Block", blockchain.get(blockchain.size() - 1).hash));
//        System.out.println("Trying to mine Block 2...");
//        blockchain.get(1).mineBlock(difficulty);
//
//        blockchain.add(new Block("Hi! Im the third Block!", blockchain.get(blockchain.size() - 1).hash));
//        System.out.println("Trying to mine block 3...");
//        blockchain.get(2).mineBlock(difficulty);
//
//        System.out.println("\nBlockchain is Valid: " + isChainValid() + "\n");
//
//        String blockchainJSON = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
//        System.out.println("The blockchain:" + blockchainJSON);



    }

    public static Boolean isChainValid(){
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0','0');

        for(int i = 1; i < blockchain.size(); i++){
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            // Compare registered hash and calculatedhash
            if(!currentBlock.hash.equals(currentBlock.calculateHash())){
                System.out.println("Current hashes are not equal");
                return false;
            }
            // Compare previous hash and registered previous hash
            if(!previousBlock.hash.equals(currentBlock.previousHash)){
                System.out.println("Previous hashes not equal");
                return false;
            }
            //Check if hash has been solved
            if(!currentBlock.hash.substring(0,difficulty).equals(hashTarget)){
                System.out.println("This block hasn't been mined");
                return false;
            }
        }

        return true;
    }
}
