package com.example.blockchainoptimization.beans;

import com.example.blockchainoptimization.util.RocksDBUtils;
import io.micrometer.common.util.StringUtils;
import lombok.Data;

/**
 * BLOCKCHAIN, a chain of block.
 *
 * @author xiyuanwang
 */
@Data
public class Blockchain {
    private String lastBlockHash;
    private static volatile Blockchain instance;

    private Blockchain(String lastBlockHash) {
        this.lastBlockHash = lastBlockHash;
    }

    public static Blockchain getInstance() throws Exception{
        String lastBlockHash = RocksDBUtils.getInstance().getLastBlockHash();

        if(StringUtils.isBlank(lastBlockHash)){
            Block genesisBlock = Block.createGenesisBlock();
            Blockchain.addNewBlock(genesisBlock);
        }

        return new Blockchain(lastBlockHash);
    }

    public static void addNewBlock(Block newBlock) throws Exception{
        String lastBlockHash = newBlock.getBlockHash();
        RocksDBUtils.getInstance().putBlock(newBlock);
        RocksDBUtils.getInstance().putLastBlockHash(lastBlockHash);
        Blockchain.getInstance().lastBlockHash = lastBlockHash;
    }
}