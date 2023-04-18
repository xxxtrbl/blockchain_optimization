package com.example.blockchainoptimization.controller;

import com.example.blockchainoptimization.beans.Block;
import com.example.blockchainoptimization.beans.Transaction;
import com.example.blockchainoptimization.beans.TransactionInfo;
import com.example.blockchainoptimization.service.impl.BlockchainService;
import com.example.blockchainoptimization.service.impl.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin
public class BlockchainController {
    @Autowired
    private BlockchainService blockchainService;
    @Autowired
    private StatisticService statisticService;

    @PostMapping("/add")
    public ResponseEntity<Object> addTransaction(@RequestBody Transaction transaction){
        try{
            String hash = blockchainService.addTransaction(transaction);
            return new ResponseEntity<>(hash, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/find")
    public ResponseEntity<Object> findTransactionByHash(@RequestParam("hash") String hash){
        try{
            TransactionInfo outcome = blockchainService.findTransactionSlowly(hash);
            return new ResponseEntity<>(outcome, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/latestTransactions")
    public ResponseEntity<Object> getLatestTransactions(){
        try{
            List<TransactionInfo> outcome = statisticService.getLatestTransactions();
            Collections.reverse(outcome);
            return new ResponseEntity<>(outcome, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/latestBlocks")
    public ResponseEntity<Object> getLatestBlocks(){
        try{
            List<Block> blocks = statisticService.getLatestBlocks();
            Collections.reverse(blocks);
            return new ResponseEntity<>(blocks, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/dailyTransactionCount")
    public ResponseEntity<Object> getDailyTransactionCount(){

        return null;
    }

    @GetMapping("/totalTransactionCount")
    public ResponseEntity<Object> getTotalTransactionCount(){

        return null;
    }
}
