package com.example.blockchainoptimization;

import com.example.blockchainoptimization.beans.BSTNode;
import com.example.blockchainoptimization.beans.EasyNode;
import com.example.blockchainoptimization.util.RedisUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Component
@Slf4j
public class OperationsBeforeStart {
    @Autowired
    private RedisUtils redisUtils;

    @PostConstruct
    private void operate(){
        // Get indices and generate index tree.
        generateIndexTree();
    }

    private void generateIndexTree(){
        List<EasyNode> nodes = (ArrayList<EasyNode>) redisUtils.get("NEWINDEX");
        if(nodes.size()<=0){
            return;
        }

        EasyNode top = nodes.get(0);
        BSTNode root = new BSTNode(top.getIndex(), top.getTimestamp());
        Queue<BSTNode> nodeQueue = new LinkedList<>();
        nodeQueue.add(root);
        BSTNode curRoot = root;

        int i = 0;
        int size = nodes.size();
        while(nodeQueue.size()>0){
            curRoot = nodeQueue.poll();

            if(2*i+1<size){
                EasyNode left = nodes.get(2*i+1);
                BSTNode curLeft = new BSTNode(left.getIndex(), left.getTimestamp());
                if(curLeft.getIndex()!=-1){
                    curRoot.setLeft(curLeft);
                    nodeQueue.add(curLeft);
                }
            }

            if(2*(i+1)<size){
                EasyNode right = nodes.get(2*(i+1));
                BSTNode curRight = new BSTNode(right.getIndex(), right.getTimestamp());
                if(curRight.getIndex()!=-1){
                    curRoot.setRight(curRight);
                    nodeQueue.add(curRight);
                }
            }

            i++;
        }

        BlockchainoptimizationApplication.indexTree = root;
    }
}
