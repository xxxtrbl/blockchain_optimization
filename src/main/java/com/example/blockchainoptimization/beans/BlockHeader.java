package com.example.blockchainoptimization.beans;

import com.example.blockchainoptimization.beans.MerkleTree;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The HEADER of a BLOCK.
 *
 * @author xiyuanwang
 */
@Data
public class BlockHeader implements Serializable {
    @Serial
    private static final long serialVersionUID = -8690770290997015751L;
    private String hashPreviousBlock;
    private String hashMerkleRoot;
    private long timeStamp;
    /**
     * Set of transactions saved in this block according to the order.
     */
    private List<String> hashTransactionList = new ArrayList<>();

    public BlockHeader(String hashPreviousBlock, List<TransactionInfo> transactionInfoList){
        this.hashPreviousBlock = hashPreviousBlock;
        this.timeStamp = new Date().getTime();

        for(TransactionInfo info : transactionInfoList){
            this.hashTransactionList.add(info.getHash());
        }
    }
}
