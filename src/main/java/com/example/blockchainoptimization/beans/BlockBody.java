package com.example.blockchainoptimization.beans;

import lombok.Data;

import java.util.List;

/**
 * The BODY of a BLOCK, mainly contains the transaction content.
 *
 * @author xiyuanwang
 */
@Data
public class BlockBody {
    private List<TransactionInfo> transactionInfoList;

    public BlockBody(List<TransactionInfo> transactionInfoList) {
        this.transactionInfoList = transactionInfoList;
    }
}