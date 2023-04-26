package com.example.blockchainoptimization;

import com.example.blockchainoptimization.beans.BSTNode;
import com.example.blockchainoptimization.beans.EasyNode;
import com.example.blockchainoptimization.beans.TransactionInfo;
import com.example.blockchainoptimization.service.impl.BlockchainService;
import com.example.blockchainoptimization.util.RedisUtils;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Component
@Slf4j
public class OperationsBeforeDestroy {
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private BlockchainService blockchainService;

    @PreDestroy
    public void operations() throws Exception{
        // Operate the left TransactionInfos.
        checkLeftTransactions();
        // Store BSTNode index in redis.
        storeWithFullBST();
    }

    private void checkLeftTransactions() throws Exception{
        List<TransactionInfo> transactionInfoList = blockchainService.transactionInfoList;
        if(transactionInfoList.size()>0){
            // Add into a new block.
            ArrayList<TransactionInfo> transactionInfos = new ArrayList<>(transactionInfoList);
            blockchainService.addBlock(transactionInfos);
            log.info("Added left transactions into a new block.");

            // Delete useless data.
            redisUtils.del("BLOCK");
        }
    }

    private void storeBSTNodeIndex() throws Exception{
        BSTNode root = BlockchainoptimizationApplication.indexTree;

        // Change tree to array.
        List<EasyNode> indexArray = new ArrayList<>();
        Queue<BSTNode> nodeQueue = new LinkedList<>();
        nodeQueue.add(root);

        while(nodeQueue.size()!=0){
            BSTNode curNode = nodeQueue.poll();
            EasyNode newNode = new EasyNode(curNode.getTimestamp(),curNode.getIndex());
            indexArray.add(newNode);

            BSTNode left = curNode.getLeft();
            BSTNode right = curNode.getRight();

            if(left!=null)
                nodeQueue.add(left);
            if(right!=null)
                nodeQueue.add(right);
        }

        redisUtils.set("NEWINDEX", indexArray);
        log.info("Added NEWINDEX.");
    }

    private void storeWithFullBST() throws Exception {
        BSTNode root = BlockchainoptimizationApplication.indexTree;

        // Change tree to array.
        List<EasyNode> indexArray = new ArrayList<>(100000);
        Queue<BSTNode> nodeQueue = new LinkedList<>();
        nodeQueue.add(root);

        int i = 0;
        int height = root.getHeight();
        int max = (int) (Math.pow(2,height-1)-2);

        while(i<=max){
            BSTNode curNode = nodeQueue.poll();
            EasyNode newNode = new EasyNode(curNode.getTimestamp(),curNode.getIndex());
            if(i==0)
                indexArray.add(i,newNode);

            BSTNode left = curNode.getLeft();
            BSTNode right = curNode.getRight();
            if(left!=null)
                nodeQueue.add(left);
            if(right!=null)
                nodeQueue.add(right);

            EasyNode e_left = left==null?new EasyNode(12434,-1):new EasyNode(left.getTimestamp(), left.getIndex());
            EasyNode e_right = right==null?new EasyNode(12343,-1):new EasyNode(right.getTimestamp(), right.getIndex());

            indexArray.add(i*2+1,e_left);
            indexArray.add(2*(i+1), e_right);

            i++;
        }

        redisUtils.set("NEWINDEX", indexArray);
        log.info("Added NEWINDEX.");
    }
}
