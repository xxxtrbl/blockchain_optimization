package com.example.blockchainoptimization.service.impl;

import com.example.blockchainoptimization.BlockchainoptimizationApplication;
import com.example.blockchainoptimization.beans.*;
import com.example.blockchainoptimization.service.IBlockchainService;
import com.example.blockchainoptimization.util.BlockchainIterator;
import com.example.blockchainoptimization.util.BlockchainUtils;
import com.example.blockchainoptimization.util.EncryptionUtils;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class BlockchainService implements IBlockchainService {
    private List<TransactionInfo> transactionInfoList;

    @Override
    public void addTransaction(Transaction transaction) throws Exception {
        /**
         * Create transactionInfo and sign.
         */
        String data = new GsonBuilder().setPrettyPrinting().create().toJson(transaction);
        byte[] signature = EncryptionUtils.applyECDSASig(KeyPairs.getKeyPair().getPrivateKey(), data);
        TransactionInfo transactionInfo = new TransactionInfo(data,signature);
        transactionInfoList.add(transactionInfo);

        if (transactionInfoList.size() == 5){
            addBlock(transactionInfoList);
        }
    }

    private void addBlock(List<TransactionInfo> transactionInfoList) throws Exception {
        if (BlockchainUtils.isBlockchainValid()){
            Block newBlock = new Block(Blockchain.getInstance().getLastBlockHash(),transactionInfoList);
            Blockchain.addNewBlock(newBlock);
            BlockchainoptimizationApplication.blocks.add(newBlock);
            log.info("Successfully added a new BLOCK！");
        }else{
            log.info("Blockchain is not valid, please check the integrity of the blockchain.");
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
        TransactionInfo outcome = null;

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
     *
     * @param hashTransaction
     * @return
     * @throws Exception
     */
    @Override
    public TransactionInfo findTransactionFast(String hashTransaction) throws Exception {
        return null;
    }
}
