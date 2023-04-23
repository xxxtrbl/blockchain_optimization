package com.example.blockchainoptimization.beans;

import lombok.Data;

@Data
public class BSTNode {
    private int index;
    private long timestamp;
    private BSTNode left;
    private BSTNode right;

    public BSTNode(int index, long timestamp){
        this.index = index;
        this.timestamp = timestamp;
    }

    public BSTNode(){

    }

    public int getHeight(){
        int leftHeight = this.left==null?0:this.left.getHeight();
        int rightHeight = this.right==null?0:this.right.getHeight();

        return leftHeight>rightHeight?leftHeight+1:rightHeight+1;
    }

    private int getLeftHeight(){
        if(this.left==null)
            return 0;
        else
            return this.left.getHeight();
    }

    private int getRightHeight(){
        if(this.right==null)
            return 0;
        else
            return this.right.getHeight();
    }

    private void leftRotate(){
        BSTNode node = new BSTNode(this.index,this.timestamp);
        node.setLeft(this.left);
        node.setRight(this.right.getLeft());
        this.index = this.right.getIndex();
        this.timestamp = this.right.getTimestamp();
        this.setRight(this.right.right);
        this.setLeft(node);
    }

    private void rightRotate(){
        BSTNode node = new BSTNode(this.index,this.timestamp);
        node.setRight(this.right);
        node.setLeft(this.left.getRight());
        this.index = this.left.getIndex();
        this.timestamp = this.left.getTimestamp();
        this.setLeft(this.left.getLeft());
        this.setRight(node);
    }

    public void add(BSTNode node){
        if(node == null){
            return;
        }

        if(node.timestamp<this.timestamp){
            if(this.left == null){
                this.setLeft(node);
            } else{
                this.left.add(node);
            }
        } else{
            if(this.right == null){
                this.setRight(node);
            } else{
                this.right.add(node);
            }
        }

        // rightHeight - leftHeight > 1, left rotate
        if(this.getRightHeight()-this.getLeftHeight()>1){
            if(this.right!=null && this.right.getLeftHeight()>this.getLeftHeight()){
                this.right.rightRotate();
            }

            this.leftRotate();
        }else if(this.getLeftHeight()-this.getRightHeight()>1){
            if(this.left!=null&&this.left.getRightHeight()>this.getRightHeight()){
                this.left.leftRotate();
            }

            this.rightRotate();
        }
    }

    public BSTNode findNode(long timestamp){
        return null;
    }
}
