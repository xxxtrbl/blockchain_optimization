package com.example.blockchainoptimization.service.impl;

import com.example.blockchainoptimization.BlockchainoptimizationApplication;
import com.example.blockchainoptimization.beans.Block;
import com.example.blockchainoptimization.beans.TransactionInfo;
import com.example.blockchainoptimization.service.IStatisticService;
import com.example.blockchainoptimization.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticService implements IStatisticService {
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public List<TransactionInfo> getLatestTransactions() throws Exception {
        int size = BlockchainoptimizationApplication.blocks.size();
        Block latestBlock = BlockchainoptimizationApplication.blocks.get(size-1);

        return latestBlock.getBlockBody().getTransactionInfoList();
    }

    @Override
    public List<Block> getLatestBlocks() throws Exception{
        return BlockchainoptimizationApplication.blocks;
    }

    @Override
    public long getDailyTransactionCount() throws Exception {
        long todayStartTimestamp = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        long todayEndTimestamp = LocalDate.now().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toEpochSecond();

        return getTransactionCountFrom(todayStartTimestamp,todayEndTimestamp);
    }

    @Override
    public long getTotalTransactionCount() throws Exception {
        return redisUtils.zTotal("transaction");
    }

    @Override
    public List<Long> getWeeklyTransactionsCount() throws Exception {
        List<Long> weeklyTransactionCount = new ArrayList<>();

        for (int i = 7; i > 0; i--) {
            LocalDate currentDate = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = currentDate.atStartOfDay();
            long startTimestamp = startOfDay.toEpochSecond(ZoneOffset.UTC);

            LocalDateTime endOfDay = currentDate.plusDays(1).atStartOfDay().minusSeconds(1);
            long endTimestamp = endOfDay.toEpochSecond(ZoneOffset.UTC);

            long count = getTransactionCountFrom(startTimestamp, endTimestamp);
            weeklyTransactionCount.add(count);
        }

        return weeklyTransactionCount;
    }

    private Long getTransactionCountFrom(long start, long end){
        return redisUtils.zRangeCount("transaction", start, end);
    }
}
