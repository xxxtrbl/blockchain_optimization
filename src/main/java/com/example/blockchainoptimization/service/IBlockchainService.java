package com.example.blockchainoptimization.service;

import com.example.blockchainoptimization.beans.Block;
import com.example.blockchainoptimization.beans.Transaction;
import com.example.blockchainoptimization.beans.TransactionInfo;

import java.util.List;

public interface IBlockchainService {

    String addTransaction(Transaction transaction) throws Exception;

    TransactionInfo findTransactionSlowly(String hashTransaction) throws Exception;

    TransactionInfo findTransactionFast(String hashTransaction) throws Exception;
}
