package com.example.blockchainoptimization.beans;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * Transaction details.
 *
 * @author xiyuanwang
 */
@Data
public class TransactionInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = -4109147656476546391L;
    private String data;
    private Long timeStamp;
    private byte[] signature;
    private String hash;

    public TransactionInfo(String data, byte[] signature){
        this.data = data;
        this.timeStamp = new Date().getTime();
        this.signature = signature;
        this.hash = this.calculateHash();
    }

    private String calculateHash(){
        return DigestUtil.sha256Hex(data + Long.toString(timeStamp));
    }
}
