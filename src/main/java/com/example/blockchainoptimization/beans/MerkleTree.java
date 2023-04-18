package com.example.blockchainoptimization.beans;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Merkle Tree
 *
 * @author xiyuanwang
 */
@Data
public class MerkleTree implements Serializable {
    @Serial
    private static final long serialVersionUID = 2855342716633101459L;
    private Node root;
    private List<TransactionInfo> transactions;
    public MerkleTree(List<TransactionInfo> transactionInfoArrayList){
        this.transactions = transactionInfoArrayList;
        constructTree();
    }

    private void constructTree(){
        if(transactions == null || transactions.size()==0){
            throw new RuntimeException("Nothing to construct a merkle tree.");
        }

        ArrayList<Node> parents = bottomLevel();
        while(parents.size() > 1){
            parents = internalLevel(parents);
        }

        if(parents.size()>0)
            root = parents.get(0);
    }

    private ArrayList<Node> bottomLevel(){
        ArrayList<Node> parents = new ArrayList<>();
        int size = transactions.size();

        for(int i=0;i<size - 1;i+=2){
            Node leaf1 = new Node(transactions.get(i).getHash());
            leaf1.isLeafNode = 1;
            leaf1.transaction = transactions.get(i);

            Node leaf2 = new Node(transactions.get(i+1).getHash());
            leaf2.isLeafNode = 1;
            leaf2.transaction = transactions.get(i+1);

            Node parent = constructParentNode(leaf1, leaf2);
            parents.add(parent);
        }

        if(size%2!=0){
            Node lastParent = new Node(transactions.get(size-1).getHash());
            lastParent.isLeafNode = 1;
            parents.add(lastParent);
        }

        return parents;
    }

    private ArrayList<Node> internalLevel(ArrayList<Node> children){
        ArrayList<Node> parents = new ArrayList<>();

        for(int i=0;i<children.size() - 1;i += 2){
            Node child1 = children.get(i);
            Node child2 = children.get(i+1);

            Node parent = constructParentNode(child1, child2);
            parents.add(parent);
        }

        if(children.size() % 2 !=0 ){
            Node child = children.get(children.size() - 1);
            Node parent = constructParentNode(child,null);
            parents.add(parent);
        }

        return parents;
    }

    private Node constructParentNode(Node leftChild, Node rightChild){
        Node parent = new Node(null);
        if(rightChild == null){
            parent.hash = leftChild.hash;
        }else {
            parent.hash = DigestUtil.sha256Hex(leftChild.hash+rightChild.hash);
        }

        parent.left = leftChild;
        parent.right = rightChild;
        parent.isLeafNode = 0;

        ArrayList<String> filters = new ArrayList<>();
        findFilter(parent, filters);
        parent.addFilter(filters);
        return  parent;
    }

    private void findFilter(Node node, ArrayList<String> filters){
        if (node.getIsLeafNode() == 0){
            if(node.getRight() != null){
                findFilter(node.left, filters);
                findFilter(node.right,filters);
            }else {
                findFilter(node.left, filters);
            }
        }else {
            filters.add(node.getHash());
            return;
        }
    }

    public TransactionInfo findTransaction(String hash){
        return null;
    }

    @Data
    public class Node implements Serializable{
        @Serial
        private static final long serialVersionUID = -2651004793798559901L;
        private String hash;
        private Node left;
        private Node right;
        private TransactionInfo transaction;
        private int isLeafNode;
        private BloomFilter bloomFilter;

        public Node(String hash){
            this.hash = hash;
        }

        private void addFilter(ArrayList<String> transactionHashes){
            bloomFilter = new BloomFilter(1000);
            for(String transactionHash : transactionHashes){
                bloomFilter.add(transactionHash);
            }
        }

        private void addFilter(String transactionHash){
            bloomFilter = new BloomFilter(1000);
            bloomFilter.add(transactionHash);
        }
    }

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
