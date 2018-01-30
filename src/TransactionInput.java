public class TransactionInput{
    public String transactionOutputId; //Reference to TransactionOutputs
    public TransactionOutput UTXO; //Contains the Unspent transaction output


    public TransactionInput(String transactionOutputId){
        this.transactionOutputId = transactionOutputId;
    }
}
