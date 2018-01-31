import com.google.gson.GsonBuilder;

import java.security.Security;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

import com.google.gson.GsonBuilder;

public class Main {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
    public static int difficulty = 5; //Difficulty setting for mining purposes
    public static float minimumTransaction = 0.1f;
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction genesisTransaction;

    public static void main(String[] args) {

        //Setup Bouncey as a security provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        //Create new Wallets
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();

        //Create genesis transaction,which sends 100 coins to walletA

        genesisTransaction = new Transaction(coinbase.publicKey,walletA.publicKey, 100f,null);
        genesisTransaction.generateSignature(coinbase.privateKey);
        genesisTransaction.transactionId = "0";
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepent, genesisTransaction.value,genesisTransaction.transactionId));
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        System.out.println("Creating and mining Genesis Block...");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        //Testing
        Block block1 = new Block(genesis.hash);
        System.out.println("\nWalletA's ballance is : " + walletA.getBalance() );
        System.out.println("\nWalletA is attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey,40f));
        addBlock(block1);

        Block block2 = new Block(block1.hash);
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        addBlock(block2);

        Block block3 = new Block(block2.hash);
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds(walletA.publicKey,20));
        System.out.println("\nWalletA balance is : " + walletA.getBalance());
        System.out.println("WalletB balance is: " + walletB.getBalance());

        isChainValid();








//        System.out.println("Private and Public Keys:");
//        System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
//        System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
//        //Create test transaction from wallet a to wallet b
//        Transaction transaction = new Transaction(walletA.publicKey,walletB.publicKey,5,null);
//        transaction.signature = transaction.generateSignature(walletA.privateKey);
//        //Verify that signature works and verify it from public key
//        System.out.println("Is signature verified? : ");
//        System.out.println(transaction.verifySignature());


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
        HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>();
        tempUTXOs.put(genesisTransaction.outputs.get(0).id,genesisTransaction.outputs.get(0));


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

            TransactionOutput tempOutput;
            for(int t=0; t<currentBlock.transactions.size();t++){
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if(!currentTransaction.verifySignature()){
                    System.out.println("# Signature on transaction(" + t + ") is invalid");
                    return false;
                }

                if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()){
                    System.out.println("#Inputs are note equal on transaction("+ t+")");
                    return false;
                }

                for(TransactionInput input : currentTransaction.inputs){
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if(tempOutput == null){
                        System.out.println("#Referenced input on Transaction("+t+") is missing!");
                        return false;
                    }
                    if(input.UTXO.value != tempOutput.value){
                        System.out.println("#Referenced input on Transaction("+t+") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for(TransactionOutput output : currentTransaction.outputs){
                    tempUTXOs.put(output.id, output);
                }

                if(currentTransaction.outputs.get(0).reciepent != currentTransaction.reciepent){
                    System.out.println("#Transaction("+t+") output reciepent is not who it should be!");
                    return false;
                }

            }
        }
        System.out.println("Blockchain is valid");
        return true;
    }

    public static void addBlock(Block newBlock){
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }
}
