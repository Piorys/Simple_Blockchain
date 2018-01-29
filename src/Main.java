import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class Main {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static int difficulty = 1; //Difficulty setting for mining purposes

    public static void main(String[] args) {

        blockchain.add(new Block("Hi! I'm the first Block!", "0"));
        System.out.println("Trying to Mine Block 1...");
        blockchain.get(0).mineBlock(difficulty);

        blockchain.add(new Block("Hi! I'm the second Block", blockchain.get(blockchain.size() - 1).hash));
        System.out.println("Trying to mine Block 2...");
        blockchain.get(1).mineBlock(difficulty);

        blockchain.add(new Block("Hi! I'm the third Block!", blockchain.get(blockchain.size() - 1).hash));
        System.out.println("Trying to mine block 3...");
        blockchain.get(2).mineBlock(difficulty);

        System.out.println("\nBlockchain is Valid: " + isChainValid());

        String blockchainJSON = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println("The blockchain:" + blockchainJSON);



    }

    public static Boolean isChainValid(){
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0','0');

        for(int i = 0; i < blockchain.size(); i++){
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
