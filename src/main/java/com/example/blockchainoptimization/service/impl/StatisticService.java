package com.example.blockchainoptimization.service.impl;

import com.example.blockchainoptimization.BlockchainoptimizationApplication;
import com.example.blockchainoptimization.beans.*;
import com.example.blockchainoptimization.service.IStatisticService;
import com.example.blockchainoptimization.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class StatisticService implements IStatisticService {
    @Autowired
    private RedisUtils redisUtils;

    private final String[] weekDays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    @Override
    public List<TransactionInfo> getLatestTransactions() throws Exception {
        List<TransactionInfo> transactionInfos = new ArrayList<>();
        List<Block> blocks = BlockchainoptimizationApplication.blocks;

        for(Block block : blocks){
            for(TransactionInfo transactionInfo:block.getBlockBody().getTransactionInfoList()){
                transactionInfos.add(transactionInfo);
            }
        }

        return transactionInfos;
    }

    @Override
    public List<BlockInfo> getLatestBlocks() throws Exception{
        List<Block> blocks = BlockchainoptimizationApplication.blocks;
        Collections.reverse(blocks);
        List<BlockInfo> outBlocks = new ArrayList<>();

        for(int i=0;i<blocks.size();i++){
            Block block = blocks.get(i);
            BlockInfo blockInfo = new BlockInfo(block.getBlockHeader().getTimeStamp(),i,block.getBlockHash());
            outBlocks.add(blockInfo);
        }

        return outBlocks;
    }

    @Override
    public long getDailyTransactionCount() throws Exception {
        long todayStartTimestamp = getStartOfDayTimestamp(0);
        long todayEndTimestamp = getStartOfDayTimestamp(-1);
        return getTransactionCountFrom(todayStartTimestamp,todayEndTimestamp);
    }

    @Override
    public long getTotalTransactionCount() throws Exception {
        return redisUtils.zTotal("TRANSACTION");
    }

    @Override
    public double getRatioOfTransactionToBlock() throws Exception {
        return this.getTotalTransactionCount()/BlockchainoptimizationApplication.blocks.size();
    }

    @Override
    public Statistics getStatistics() throws Exception {
        long total = getTotalTransactionCount();
        long daily = getDailyTransactionCount();
        double ratio = getRatioOfTransactionToBlock();

        Statistics statistics = new Statistics(total, daily, ratio);
        return statistics;
    }

    @Override
    public List<WeeklyData> getWeeklyTransactions() throws Exception {
        List<Long> weeklyTransactionCount = new ArrayList<>();
        List<WeeklyData> weeklyData = new ArrayList<>();

        for (int i=6;i>=0;i--) {
            long startTimestamp = getStartOfDayTimestamp(i);
            long endTimestamp = getStartOfDayTimestamp(i-1);
            long count = getTransactionCountFrom(startTimestamp, endTimestamp);

            int day = getDayOfWeek(i);
            WeeklyData weeklyData1 = new WeeklyData(count, weekDays[day-1]);
            weeklyTransactionCount.add(count);
            weeklyData.add(weeklyData1);
        }

        return weeklyData;
    }

    public Long getTransactionCountFrom(long start, long end){
        return redisUtils.zRangeCount("TRANSACTION", start, end);
    }

    public int getDayOfWeek(int daysFromToday) {
        LocalDate date = LocalDate.now(ZoneId.of("Asia/Shanghai")).minusDays(daysFromToday);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.getValue();
    }

    private long getStartOfDayTimestamp(int days) {
        ZoneId zone = ZoneId.of("Asia/Shanghai");

        long startTimestamp = LocalDate.now(zone)
                .minusDays(days)
                .atStartOfDay(zone)
                .toInstant()
                .toEpochMilli();

        return startTimestamp;
    }
}
