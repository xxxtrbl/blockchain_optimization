package com.example.blockchainoptimization.service;

import com.example.blockchainoptimization.beans.Block;
import com.example.blockchainoptimization.beans.TransactionInfo;

import java.util.List;

public interface IStatisticService {
    List<TransactionInfo> getLatestTransactions() throws Exception;

    List<Block> getLatestBlocks() throws Exception;

    long getDailyTransactionCount() throws Exception;

    long getTotalTransactionCount() throws Exception;

    List<Long> getWeeklyTransactionsCount() throws Exception;
}
