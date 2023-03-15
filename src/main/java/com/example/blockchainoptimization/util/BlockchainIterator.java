package com.example.blockchainoptimization.util;

import com.example.blockchainoptimization.beans.Block;
import io.micrometer.common.util.StringUtils;
import lombok.Getter;

public class BlockchainIterator {
    @Getter
    private Block currentBlock;
    private String currentBlockHash;

    public BlockchainIterator(String currentBlockHash) throws Exception{
        this.currentBlock = RocksDBUtils.getInstance().getBlock(currentBlockHash);
        this.currentBlockHash = currentBlockHash;
    }

    public boolean hasNext() throws Exception{
        if(StringUtils.isBlank(currentBlockHash)){
            return false;
        }
        if(currentBlock==null){
            return false;
        }
        if(currentBlock.getBlockHeader().getHashPreviousBlock()=="0"){
            return false;
        }

        String hashPreviousBlock = currentBlock.getBlockHeader().getHashPreviousBlock();
        return RocksDBUtils.getInstance().getBlock(hashPreviousBlock)!=null;
    }

    public void getNextBlock() throws Exception {
        String hashPreviousBlock = currentBlock.getBlockHeader().getHashPreviousBlock();
        currentBlock = RocksDBUtils.getInstance().getBlock(hashPreviousBlock);
        if (currentBlock != null){
            this.currentBlockHash = currentBlock.getBlockHash();
        }
    }

    public Block getCurrentBlock(){
        return currentBlock;
    }
}
