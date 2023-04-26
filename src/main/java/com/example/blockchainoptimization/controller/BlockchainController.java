package com.example.blockchainoptimization.controller;

import cn.hutool.core.date.StopWatch;
import com.example.blockchainoptimization.beans.*;
import com.example.blockchainoptimization.service.impl.BlockchainService;
import com.example.blockchainoptimization.service.impl.StatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin
@Slf4j
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

    @GetMapping("/findSlowly")
    public ResponseEntity<Object> findTransactionByHash(@RequestParam("hash") String hash){
        try{
            StopWatch stopWatch = new StopWatch();

            stopWatch.start();
            TransactionInfo outcome = blockchainService.findTransactionSlowly(hash);
            stopWatch.stop();

            log.info("***************** Runtime of findTransactionSlowly() "+ stopWatch.getTotalTimeMillis()+
                    " *****************");

            if(outcome==null){
                return new ResponseEntity<>(outcome, HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(outcome, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/findFast")
    public ResponseEntity<Object> findTransactionByHashWithCache(@RequestParam("hash") String hash){
        try{
            StopWatch stopWatch = new StopWatch();

            stopWatch.start();
            TransactionInfo outcome = blockchainService.findTransactionFast(hash);
            stopWatch.stop();

            log.info("***************** Runtime of findTransactionFast() "+ stopWatch.getTotalTimeMillis()+
                    " *****************");
            if(outcome==null){
                return new ResponseEntity<>(outcome, HttpStatus.NO_CONTENT);
            }

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
            outcome = outcome.subList(0,5);
            return new ResponseEntity<>(outcome, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/latestBlocks")
    public ResponseEntity<Object> getLatestBlocks(){
        try{
            List<BlockInfo> blocks = statisticService.getLatestBlocks();
            Collections.reverse(blocks);
            return new ResponseEntity<>(blocks, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/statistic")
    public ResponseEntity<Object> getStatisticData(){
        try{
            Statistics outcome = statisticService.getStatistics();
            return new ResponseEntity<>(outcome, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/weeklyData")
    public ResponseEntity<Object> getWeeklyData(){
        try{
            List<WeeklyData> outcome = statisticService.getWeeklyTransactions();
            return new ResponseEntity<>(outcome, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
