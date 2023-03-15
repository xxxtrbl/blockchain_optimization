package com.example.blockchainoptimization.beans;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * The BODY of a BLOCK, mainly contains the transaction content.
 *
 * @author xiyuanwang
 */
@Data
public class BlockBody implements Serializable {
    @Serial
    private static final long serialVersionUID = -6863435702084925881L;
    private List<TransactionInfo> transactionInfoList;

    public BlockBody(List<TransactionInfo> transactionInfoList) {
        this.transactionInfoList = transactionInfoList;
    }
}