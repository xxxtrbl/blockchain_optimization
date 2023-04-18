package com.example.blockchainoptimization.beans;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class BloomFilter implements Serializable {
    @Serial
    private static final long serialVersionUID = 144723788062456762L;
    private static int SIZE;
    private static int[] SEEDS;
    private BitSet bits;
    private SimpleHash[] func;
    private MisjudgmentRate rate;
    private Double autoClearRate;
    private final AtomicInteger useCount = new AtomicInteger(0);

    public BloomFilter(int dataCount) {
        this(MisjudgmentRate.MIDDLE, dataCount,null);
    }

    public BloomFilter(MisjudgmentRate rate, int dataCount, Double autoClearRate){
        long bitSize = rate.seeds.length * dataCount;
        if (bitSize < 0 || bitSize > Integer.MAX_VALUE){
            throw new RuntimeException("Overflowed, please lower the misjudgement rate or the data count.");
        }

        this.rate = rate;
        SEEDS = rate.seeds;
        SIZE = (int) bitSize;
        func = new SimpleHash[SEEDS.length];
        for (int i=0; i<SEEDS.length;i++){
            func[i] = new SimpleHash(SIZE, SEEDS[i]);
        }
        bits = new BitSet(SIZE);
        this.autoClearRate = autoClearRate;
    }

    public void add(Object value){
        checkNeedClear();

        if(!contains(value)){
            for (SimpleHash f : func){
                bits.set(f.hash(value),true);
            }
            useCount.getAndIncrement();
        }
    }

    public boolean contains(Object value){
        boolean ret = true;
        for(SimpleHash f: func){
            ret = ret && bits.get(f.hash(value));
        }

        return ret;
    }

    private void checkNeedClear(){
        if (autoClearRate != null){
            if (getUseRate() >= autoClearRate){
                synchronized (this){
                    if(getUseRate() >= autoClearRate){
                        bits.clear();
                        useCount.set(0);
                    }
                }
            }
        }
    }

    public double getUseRate(){
        return (double) useCount.intValue() / (double) SIZE;
    }

    public static class SimpleHash implements Serializable{
        @Serial
        private static final long serialVersionUID = -7330180916770815810L;
        private int cap;
        private int seed;

        public SimpleHash(int cap, int seed){
            this.cap = cap;
            this.seed = seed;
        }

        public int hash(Object value){
            int h;
            return (value == null) ? 0 : Math.abs(seed * (cap - 1) & ((h = value.hashCode()) ^ (h >>> 16)));
        }
    }

    public enum MisjudgmentRate implements Serializable{
        VERY_SMALL(new int[] {2, 3, 5, 7}),

        SMALL(new int[] {2, 3, 5, 7, 11, 13, 17, 19}),

        MIDDLE(new int[] { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53 }),

        HIGH(new int[] { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97,
                101, 103, 107, 109, 113, 127, 131 });

        private int[] seeds;

        private MisjudgmentRate(int[] seeds){
            this.seeds = seeds;
        }

        public int[] getSeeds(){
            return seeds;
        }

        public void setSeeds(int[] seeds){
            this.seeds = seeds;
        }
    }

}
