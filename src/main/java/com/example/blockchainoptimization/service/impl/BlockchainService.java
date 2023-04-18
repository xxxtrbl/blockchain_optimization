package com.example.blockchainoptimization.service.impl;

import com.example.blockchainoptimization.BlockchainoptimizationApplication;
import com.example.blockchainoptimization.beans.*;
import com.example.blockchainoptimization.service.IBlockchainService;
import com.example.blockchainoptimization.util.*;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class BlockchainService implements IBlockchainService {
    @Getter
    private List<TransactionInfo> transactionInfoList = new ArrayList<>();
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public String addTransaction(Transaction transaction) throws Exception {

        String data = new GsonBuilder().setPrettyPrinting().create().toJson(transaction);
        byte[] signature = EncryptionUtils.applyECDSASig(KeyPairs.getKeyPair().getPrivateKey(), data);
        TransactionInfo transactionInfo = new TransactionInfo(data,signature);
        transactionInfoList.add(transactionInfo);
        log.info("Successfully added a transaction!");

        // [CACHE] Add into cache, which will be deleted after the block is added.
        redisUtils.hset("BLOCK",transactionInfo.getHash(), transactionInfo);
        // [STATISTICS] Add transaction into set to record its timestamp.
        long timestamp = new Date().getTime();
        redisUtils.zAdd("TRANSACTION",transactionInfo.getHash(),(double)timestamp);

        // Add block.
        if (transactionInfoList.size() == 5){
            List<TransactionInfo> transactionInfos = new ArrayList<>(transactionInfoList);
            addBlock(transactionInfos);

            // [INDEX] Add into indexTree.
            int size = BlockchainoptimizationApplication.blocks.size();
            BSTNode newNode = new BSTNode(size-1,BlockchainoptimizationApplication.blocks.get(size-1).getBlockHeader().getTimeStamp());
            BlockchainoptimizationApplication.indexTree.add(newNode);

            // Delete useless data.
            redisUtils.del("BLOCK");
            transactionInfoList.clear();

            log.info("Successfully added into a block!");
        }

        return transactionInfo.getHash();
    }

    private void addBlock(List<TransactionInfo> transactionInfoList) throws Exception {
        if (BlockchainUtils.isBlockchainValid()){
            ArrayList<Block> blocks = BlockchainoptimizationApplication.blocks;
            int count = blocks.size();
            if (count < 1){
                return;
            }

            String previousBlockHash = blocks.get(blocks.size()-1).getBlockHash();
            Block newBlock = new Block(previousBlockHash,transactionInfoList);
            Blockchain.addNewBlock(newBlock);
            BlockchainoptimizationApplication.blocks.add(newBlock);
            log.info("Successfully added a new BLOCK！");
        }else{
            log.error("Blockchain is not valid, please check the integrity of the blockchain.");
        }
    }

    /**
     * Query version without optimization.
     * @param hashTransaction
     * @return
     * @throws Exception
     */
    @Override
    public TransactionInfo findTransactionSlowly(String hashTransaction) throws Exception {
        TransactionInfo outcome = new TransactionInfo();

        for (int i = BlockchainoptimizationApplication.blocks.size()-1;i>=0;i--){
            Block currentBlock = BlockchainoptimizationApplication.blocks.get(i);
            List<TransactionInfo> infoList = currentBlock.getBlockBody().getTransactionInfoList();
            for(TransactionInfo info : infoList){
                if(info.getHash() == hashTransaction){
                    outcome = info;
                    break;
                }
            }
        }

        return outcome;
    }

    /**
     * Query version with optimization like cache and merkle tree.
     * @param hashTransaction
     * @return
     * @throws Exception
     */
    @Override
    public TransactionInfo findTransactionFast(String hashTransaction) throws Exception {
        // Find in cache firstly.
        Object object =  redisUtils.hget("BLOCK",hashTransaction);
        TransactionInfo outcome = (TransactionInfo) object;

        // Check out the timestamp, block index and find the target transaction.
        if(outcome == null){
            Long timestamp = Double.doubleToLongBits(redisUtils.zGet("TRANSACTION",hashTransaction));
            BSTNode node = BlockchainoptimizationApplication.indexTree.findNode(timestamp);

            int index = node.getIndex();
            Block block = BlockchainoptimizationApplication.blocks.get(index);
            outcome = block.getMerkleTree().findTransaction(hashTransaction);
        }

        return outcome;
    }
}
