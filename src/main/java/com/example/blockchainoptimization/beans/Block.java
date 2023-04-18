package com.example.blockchainoptimization.beans;

import cn.hutool.crypto.digest.DigestUtil;
import com.example.blockchainoptimization.util.EncryptionUtils;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *  The struct of a BLOCK.
 *
 * @author xiyuanwang
 */
@Data
public class Block implements Serializable{
    @Serial
    private static final long serialVersionUID = -6435421364657052620L;
    private BlockHeader blockHeader;
    private BlockBody blockBody;
    private String blockHash;
    private MerkleTree merkleTree;

    public Block(String previousHash, List<TransactionInfo> transactionInfoList){
        this.blockHeader = new BlockHeader(previousHash,transactionInfoList);
        this.blockBody = new BlockBody(transactionInfoList);
        this.blockHash = this.generateBlockHash();
        this.merkleTree = new MerkleTree(transactionInfoList);

        if(merkleTree.getRoot()!=null)
            blockHeader.setHashMerkleRoot(merkleTree.getRoot().getHash());
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

        return new Block(String.valueOf(0),transactionInfoList);
    }
}
