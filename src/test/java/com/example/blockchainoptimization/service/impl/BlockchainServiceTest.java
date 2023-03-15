package com.example.blockchainoptimization.service.impl;

import cn.hutool.core.lang.Assert;
import com.example.blockchainoptimization.beans.Transaction;
import com.example.blockchainoptimization.beans.TransactionInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest
class BlockchainServiceTest {
    @Autowired
    private BlockchainService blockchainService;

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

        for(int i=10;i<=50;i++){
            Transaction transaction = new Transaction("tony","lily",i);
            fiveTransactions.add(transaction);
        }

        try{
            for(Transaction transaction:fiveTransactions){
                blockchainService.addTransaction(transaction);
            }
            TransactionInfo targetTransaction = blockchainService.getTransactionInfoList().get(0);
            TransactionInfo outcome = blockchainService.findTransactionSlowly(targetTransaction.getHash());
            Assert.equals(targetTransaction,outcome);
        }catch (Exception e){
            System.out.println(e);
        }
    }
}