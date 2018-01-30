import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class Wallet {

    public PrivateKey privateKey;
    public PublicKey publicKey;

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

}
