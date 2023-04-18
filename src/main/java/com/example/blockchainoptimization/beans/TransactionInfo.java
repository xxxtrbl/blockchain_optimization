package com.example.blockchainoptimization.beans;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * Transaction details.
 *
 * @author xiyuanwang
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = -4109147656476546391L;
    private String data;
    private Long timestamp;
    private byte[] signature;
    private String hash;

    public TransactionInfo(String data, byte[] signature){
        this.data = data;
        this.timestamp = new Date().getTime();
        this.signature = signature;
        this.hash = this.calculateHash();
    }

    private String calculateHash(){
        return DigestUtil.sha256Hex(data + Long.toString(timestamp));
    }
}
