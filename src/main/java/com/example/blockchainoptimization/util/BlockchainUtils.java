package com.example.blockchainoptimization.util;

import com.example.blockchainoptimization.beans.Block;
import com.example.blockchainoptimization.beans.Blockchain;
import com.example.blockchainoptimization.beans.TransactionInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BlockchainUtils {
    public static boolean isBlockchainValid() throws Exception{
        return true;
    }

    public static ArrayList<Block> initBlockchain() throws Exception{
        ArrayList<Block> blocks = new ArrayList<Block>();
        String lastBlockHash = RocksDBUtils.getInstance().getLastBlockHash();
        BlockchainIterator iterator = new BlockchainIterator(lastBlockHash);

        for(;iterator.hasNext();iterator.getNextBlock()){
            Block curBlock = iterator.getCurrentBlock();
            String curBlockHash = curBlock.getBlockHeader().getHashPreviousBlock();

            Block preBlock = RocksDBUtils.getInstance().getBlock(curBlockHash);
            if(curBlockHash.equals(preBlock.getBlockHash()) || curBlockHash=="0"){
                blocks.add(curBlock);
            }
            else{
                throw new Exception("This chain is not valid, the hash not matches.");
            }
        }

        return blocks;
    }
}
