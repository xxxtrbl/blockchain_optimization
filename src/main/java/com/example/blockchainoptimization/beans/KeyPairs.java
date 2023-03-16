package com.example.blockchainoptimization.beans;

import com.example.blockchainoptimization.util.KeypairUtils;
import com.example.blockchainoptimization.util.RocksDBUtils;
import lombok.Data;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.Serial;
import java.io.Serializable;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

@Data
public class KeyPairs implements Serializable {
    @Serial
    private static final long serialVersionUID = 1747446376774496469L;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private volatile static KeyPairs keyPairs;

    public static KeyPairs getKeyPair() throws Exception{
        synchronized (KeyPairs.class){
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            KeypairUtils keypairUtils = new KeypairUtils();
            if(keypairUtils.getObject() != null){
                keyPairs = keypairUtils.getObject();
                return keyPairs;
            }else{
                keyPairs = new KeyPairs();
                keypairUtils.saveObject(keyPairs);
                return keyPairs;
            }
        }
    }

    private KeyPairs(){
        generateKeyPairs();
    }

    private void generateKeyPairs(){
        try{
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
