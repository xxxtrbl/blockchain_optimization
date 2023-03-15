package com.example.blockchainoptimization.util;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class EncryptionUtils {
    /**
     * This function is used for sign a transaction.
     * @param privateKey buyer's private key
     * @param input information needed in encryption
     * @return byte array with signature
     */
    public static byte[] applyECDSASig(PrivateKey privateKey, String input){
        Signature dsa;
        byte[] output = new byte[0];
        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    /**
     * This function is used for verify a transaction.
     * @param publicKey
     * @param data
     * @param signature
     * @return To tell if the signature is valid.
     */
    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
