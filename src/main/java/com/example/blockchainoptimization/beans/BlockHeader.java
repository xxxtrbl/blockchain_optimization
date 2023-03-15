package com.example.blockchainoptimization.beans;

import com.example.blockchainoptimization.beans.MerkleTree;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * The HEADER of a BLOCK.
 *
 * @author xiyuanwang
 */
@Data
public class BlockHeader {
    private String hashPreviousBlock;
    private String hashMerkleRoot;
    private long timeStamp;
    /**
     * Set of transactions saved in this block according to the order.
     */
    private List<String> hashTransactionList;

    public BlockHeader(String hashPreviousBlock, List<TransactionInfo> transactionInfoList){
        this.hashPreviousBlock = hashPreviousBlock;
        this.timeStamp = new Date().getTime();

        for(TransactionInfo info : transactionInfoList){
            this.hashTransactionList.add(info.getHash());
        }

        this.hashMerkleRoot = MerkleTree.getTreeRootHash(hashTransactionList);
    }
}
