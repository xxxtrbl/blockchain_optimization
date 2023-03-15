package com.example.blockchainoptimization.beans;

import cn.hutool.crypto.digest.DigestUtil;
import com.example.blockchainoptimization.util.EncryptionUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *  The struct of a BLOCK.
 *
 * @author xiyuanwang
 */
@Data
public class Block {
    private BlockHeader blockHeader;
    private BlockBody blockBody;
    private String blockHash;

    public Block(String previousHash, List<TransactionInfo> transactionInfoList){
        BlockHeader header = new BlockHeader(previousHash,transactionInfoList);
        BlockBody blockBody = new BlockBody(transactionInfoList);
        this.blockHash = this.generateBlockHash();
    }

    private String generateBlockHash(){
        return DigestUtil.sha256Hex(blockHeader.getHashPreviousBlock()
                +blockHeader.getTimeStamp()
                +blockHeader.getHashMerkleRoot());
    }

    public static Block createGenesisBlock() throws Exception{
        String data = "This is a genesis block.";
        byte[] signature = EncryptionUtils.applyECDSASig(KeyPairs.getKeyPair().getPrivateKey(), "This is a genesis block.");
        TransactionInfo info = new TransactionInfo(data, signature);

        List<TransactionInfo> transactionInfoList = new ArrayList<TransactionInfo>();
        transactionInfoList.add(info);

        return new Block("0",transactionInfoList);
    }
}
