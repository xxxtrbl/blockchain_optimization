package com.example.blockchainoptimization.service;

import com.example.blockchainoptimization.beans.*;

import java.util.List;

public interface IStatisticService {
    List<TransactionInfo> getLatestTransactions() throws Exception;

    List<BlockInfo> getLatestBlocks() throws Exception;

    long getDailyTransactionCount() throws Exception;

    long getTotalTransactionCount() throws Exception;

    double getRatioOfTransactionToBlock() throws Exception;

    Statistics getStatistics() throws Exception;

    List<WeeklyData> getWeeklyTransactions() throws Exception;
}
