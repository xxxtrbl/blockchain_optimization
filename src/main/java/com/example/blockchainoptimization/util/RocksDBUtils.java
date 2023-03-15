package com.example.blockchainoptimization.util;

import com.example.blockchainoptimization.beans.Block;
import com.example.blockchainoptimization.beans.KeyPairs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.util.HashMap;
import java.util.Map;

/**
 * Tools for RocksDB.
 *
 * @author xiyuanwang
 */
public class RocksDBUtils {
    private static final String DB_FILE = "blockchain.db";
    private static final String BLOCKS_BUCKET_PREFIX = "blocks_";
    private static final String LAST_BLOCK_KEY = "l";
    private static final String KEYPAIR_PREFIX = "keypair_";
    private volatile static RocksDBUtils instance;
    @Getter
    private RocksDB rocksDB;
    private Gson gson;

    public static RocksDBUtils getInstance(){
        if(instance==null){
            synchronized (RocksDBUtils.class){
                if(instance == null){
                    instance = new RocksDBUtils();
                }
            }
        }
        return instance;
    }

    private RocksDBUtils(){
        gson = new GsonBuilder().create();
        openDB();
        initRocksDB();
    }

    private Map<String, byte[]> blocksBucket;

    private void openDB(){
        try{
            rocksDB = RocksDB.open(DB_FILE);
        }catch (RocksDBException e){
            throw new RuntimeException("Fail to open db!", e);
        }
    }

    private void initRocksDB(){
        try {
            byte[] blockBucketKey = SerializeUtils.serialize(BLOCKS_BUCKET_PREFIX);
            byte[] blockBucketBytes = rocksDB.get(blockBucketKey);
            if(blockBucketBytes != null){
                blocksBucket = (Map) SerializeUtils.deserialize(blockBucketBytes);
            } else {
                blocksBucket = new HashMap<>();
                rocksDB.put(blockBucketKey, SerializeUtils.serialize(blocksBucket));
            }
        } catch (RocksDBException e) {
            throw new RuntimeException("Fail to init block bucket!", e);
        }
    }

    public void putLastBlockHash(String tipBlockHash) throws Exception{
        try{
            blocksBucket.put(LAST_BLOCK_KEY,SerializeUtils.serialize(tipBlockHash));
            rocksDB.put(SerializeUtils.serialize(BLOCKS_BUCKET_PREFIX),SerializeUtils.serialize(blocksBucket));
        }catch (RocksDBException e){
            throw new RuntimeException("Fail to put last block hash!", e);
        }
    }

    public String getLastBlockHash() throws Exception{
        byte[] lastBlockHashBytes = blocksBucket.get(LAST_BLOCK_KEY);
        if (lastBlockHashBytes != null) {
            return (String) SerializeUtils.deserialize(lastBlockHashBytes);
        }
        return "";
    }

    public void putBlock(Block block) throws Exception{
        try{
            String jsonObject = gson.toJson(block);
            blocksBucket.put(block.getBlockHash(),SerializeUtils.serialize(jsonObject));
            rocksDB.put(SerializeUtils.serialize(BLOCKS_BUCKET_PREFIX),SerializeUtils.serialize(blocksBucket));
        }catch (RocksDBException e){
            throw new RuntimeException("Fail to put block!", e);
        }
    }

    public Block getBlock(String blockHash) throws Exception{
        byte[] temp = blocksBucket.get(blockHash);
        String jsonObject = (String) SerializeUtils.deserialize(temp);
        Block block = gson.fromJson(jsonObject,Block.class);
        return block;
    }

    public void putKeyPair(KeyPairs keyPairs) throws Exception{
        byte[] key = SerializeUtils.serialize(KEYPAIR_PREFIX);
        rocksDB.put(key,SerializeUtils.serialize(keyPairs));
    }

    public KeyPairs getKeyPair() throws Exception{
        byte[] key = SerializeUtils.serialize(KEYPAIR_PREFIX);
        return  (KeyPairs) SerializeUtils.deserialize(key);
    }

    public void closeRocksDB(){
        try {
            rocksDB.close();
        }catch (Exception e){
            throw new RuntimeException("Fail to close the RocksDB.");
        }
    }

}
