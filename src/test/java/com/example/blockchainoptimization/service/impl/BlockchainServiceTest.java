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
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
class BlockchainServiceTest {
    @Autowired
    private BlockchainService blockchainService;
    @Autowired
    private StatisticService statisticServices;

    @Autowired
    private RedisTemplate redisTemplate;

//    @Test
//    void addTransaction() {
//        Transaction transaction = new Transaction("Tony","Lily",30);
//        try{
//            blockchainService.addTransaction(transaction);
//            int transactionListSize = blockchainService.getTransactionInfoList().size();
//            Assert.equals(transactionListSize,1);
//        }catch(Exception e){
//            System.out.println(e);
//        }
//    }

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

    @Test
    void testBloomFilter(){
        BloomFilter bloomFilter = new BloomFilter(100000);
        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add("77763f7169512717cc5b006ed1c8b8cfe9f72072dadf5929d7ae9a11e0f1a11e");
        stringArrayList.add("9936c6f2ad52b9bf1ca5feb13b9c88573ff858f1475bd2e79e6abd5a02246525");
        stringArrayList.add("551d2a50885158c01c7cc56f246bdceaab9c11618949db20a53026a0a222e6b7");
        stringArrayList.add("aa38dbdf5a466c27f63d33c9ac3e3c7464de9d95f7aad6556a1887abcf0053fd");
        stringArrayList.add("6bdd9da9050fa1d2ab4024c1025f8faf55fc0a5489c2935bb19a5b321d97f0d9");

        for(String str : stringArrayList){
            bloomFilter.add(str);
        }
        stringArrayList.add("1037ba37e007fdfc5e16f8a83a27316cf04a1a565e1bcbf1259397119666d7ff");

        for(String str:stringArrayList){
            Boolean out = bloomFilter.contains(str);
            System.out.println(out);
        }
    }

    @Test
    void testStatistics(){
        try{
            Block mockGenesisBlock = Block.createGenesisBlock();
            BlockchainoptimizationApplication.blocks = new ArrayList<Block>();
            BlockchainoptimizationApplication.blocks.add(mockGenesisBlock);

            Statistics out = statisticServices.getStatistics();
            List<WeeklyData> out2 = statisticServices.getWeeklyTransactions();
            Assert.equals(out.getDaily(),0);
            Assert.equals(out.getTotal(),5);

            System.out.println(out.getRatio());
            System.out.println(out2);
        }catch (Exception e)
        {
            System.out.println(e);
        }
    }
}