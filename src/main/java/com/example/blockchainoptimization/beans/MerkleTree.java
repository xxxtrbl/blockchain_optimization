package com.example.blockchainoptimization.beans;

import cn.hutool.crypto.digest.DigestUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Merkle Tree
 *
 * @author xiyuanwang
 */
public class MerkleTree {
    /**
     * Calculate the hash of the tree root.
     * @param hashList : the transaction hash list of a block
     * @return the HASH of the tree root.
     */
    public static String getTreeRootHash(List<String> hashList){
            if(hashList==null || hashList.size()==0){
                return null;
            }

            while (hashList.size() != 1){
                hashList = getMerkleNodeList(hashList);
            }

            return hashList.get(0);
    }

    /**
     * Build nodes of a higher level in Merkle tree.
     * @param contentList: nodes hash of the current level.
     * @return: nodes of higher level Merkle tree.
     */
    private static List<String> getMerkleNodeList(List<String> contentList){
        List<String> merkleNodeList = new ArrayList<String>();

        if (contentList == null || contentList.size()==0){
            return merkleNodeList;
        }

        int index = 0, length = contentList.size();
        while (index < length){
            String left = contentList.get(index++);
            String right = "";
            if (index < length){
                right = contentList.get(index++);
            }

            String sha2HexValue = DigestUtil.sha256Hex(left+right);
            merkleNodeList.add(sha2HexValue);
        }

        return merkleNodeList;
    }
}
