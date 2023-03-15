package com.example.blockchainoptimization.controller;

import com.example.blockchainoptimization.beans.Transaction;
import com.example.blockchainoptimization.beans.TransactionInfo;
import com.example.blockchainoptimization.service.impl.BlockchainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class BlockchainController {
    @Autowired
    private BlockchainService blockchainService;

    @PostMapping("/add")
    public ResponseEntity<Object> addTransaction(@RequestBody Transaction transaction){
        try{
            blockchainService.addTransaction(transaction);
        }catch (Exception e){

        }
        return null;
    }

    @GetMapping("/find")
    public ResponseEntity<Object> findTransactionByHash(@RequestParam("hash") String hash){
        try{
            blockchainService.findTransactionSlowly(hash);
        }catch (Exception e){

        }
        return null;
    }
}
