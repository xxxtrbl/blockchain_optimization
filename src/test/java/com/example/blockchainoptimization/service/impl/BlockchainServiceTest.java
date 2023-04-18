package com.example.blockchainoptimization.service.impl;

import cn.hutool.core.lang.Assert;
import com.example.blockchainoptimization.BlockchainoptimizationApplication;
import com.example.blockchainoptimization.beans.*;
import com.example.blockchainoptimization.util.EncryptionUtils;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
class BlockchainServiceTest {
    @Autowired
    private BlockchainService blockchainService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void addTransaction() {
        Transaction transaction = new Transaction("Tony","Lily",30);
        try{
            blockchainService.addTransaction(transaction);
            int transactionListSize = blockchainService.getTransactionInfoList().size();
            Assert.equals(transactionListSize,1);
        }catch(Exception e){
            System.out.println(e);
        }
    }

    @Test
    void findTransactionSlowly() {
        ArrayList<Transaction> fiveTransactions = new ArrayList<>();

        for(int i=10;i<=100;i+=10){
            Transaction transaction = new Transaction("tony","lily",i);
            fiveTransactions.add(transaction);
        }

        try{
            Block mockGenesisBlock = Block.createGenesisBlock();
            BlockchainoptimizationApplication.blocks = new ArrayList<Block>();
            BlockchainoptimizationApplication.blocks.add(mockGenesisBlock);

            String oneHash = new String();
            for(Transaction transaction:fiveTransactions){
                oneHash = blockchainService.addTransaction(transaction);
            }
            blockchainService.findTransactionSlowly(oneHash);

        }catch (Exception e){
            System.out.println(e);
        }
    }

    @Test
    void findATransactionInCache(){
        try{
            Block mockGenesisBlock = Block.createGenesisBlock();
            BlockchainoptimizationApplication.blocks = new ArrayList<Block>();
            BlockchainoptimizationApplication.blocks.add(mockGenesisBlock);

            Transaction transaction = new Transaction("tony","lily",100);
            String data = new GsonBuilder().setPrettyPrinting().create().toJson(transaction);
            byte[] signature = EncryptionUtils.applyECDSASig(KeyPairs.getKeyPair().getPrivateKey(), data);
            TransactionInfo targetTransaction = new TransactionInfo(data,signature);

            String targetHash = blockchainService.addTransaction(transaction);
            TransactionInfo outcome = blockchainService.findTransactionSlowly(targetHash);

            System.out.println(outcome);
        }catch (Exception e){
            System.out.println(e);
        }
    }
}