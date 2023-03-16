package com.example.blockchainoptimization.util;

import com.example.blockchainoptimization.beans.KeyPairs;

import java.io.*;

public class KeypairUtils {
    private static final String FILE_NAME =  "keypair.txt";

    public void saveObject(KeyPairs keyPairs){
        try{
            File file = new File(FILE_NAME);
            if(!file.exists()){
                file.createNewFile();
            }

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(keyPairs);
            oos.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public KeyPairs getObject(){
        try{
            File file = new File(FILE_NAME);
            if(!file.exists()){
                file.createNewFile();
                return null;
            }

            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            KeyPairs keyPairs = (KeyPairs) ois.readObject();
            return keyPairs;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
