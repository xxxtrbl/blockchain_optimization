package com.example.blockchainoptimization.util;

import com.example.blockchainoptimization.BlockchainoptimizationApplication;
import com.example.blockchainoptimization.beans.Block;
import com.example.blockchainoptimization.beans.Blockchain;
import com.example.blockchainoptimization.beans.TransactionInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BlockchainUtils {
    public static boolean isBlockchainValid() throws Exception{
        ArrayList<Block> blocks = BlockchainoptimizationApplication.blocks;

        for(int i= blocks.size()-1;i>0;i--){
            String curBlockHash = blocks.get(i).getBlockHeader().getHashPreviousBlock();
            String preBlockHash = blocks.get(i-1).getBlockHash();

            if(!curBlockHash.equals(preBlockHash)){
                return false;
            }
        }
        return true;
    }

    public static ArrayList<Block> initBlockchain() throws Exception{
        ArrayList<Block> blocks = new ArrayList<Block>();
        String lastBlockHash = RocksDBUtils.getInstance().getLastBlockHash();

        for(BlockchainIterator iterator = new BlockchainIterator(lastBlockHash); iterator.exists()||iterator.hasNext();iterator.getNextBlock()){
            Block curBlock = iterator.getCurrentBlock();
            blocks.add(curBlock);
        }

        return blocks;
    }
}
