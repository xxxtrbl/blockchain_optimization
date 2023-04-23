package com.example.blockchainoptimization;

import com.example.blockchainoptimization.beans.BSTNode;
import com.example.blockchainoptimization.beans.Block;
import com.example.blockchainoptimization.beans.Blockchain;
import com.example.blockchainoptimization.util.BlockchainUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

/**
 * @description: This project is responsible for the optimization of BLOCKCHAIN QUERY.
 * details can be found in README.md.
 * @date: 2023/2/27
 * @author xiyuanwang
 */
@SpringBootApplication
@Slf4j
public class BlockchainoptimizationApplication {
    public static ArrayList<Block> blocks = new ArrayList<>();
    public static BSTNode indexTree = new BSTNode();

    public static void main(String[] args) throws Exception{

        SpringApplication.run(BlockchainoptimizationApplication.class, args);

        // initialize the blockchain to block array
        Blockchain.getInstance();
        blocks = BlockchainUtils.initBlockchain();
        Collections.reverse(blocks);

        // Initialize the blockchain index tree if not exists.
        if(indexTree==null){
            BSTNode indexRoot = new BSTNode(0, blocks.get(0).getBlockHeader().getTimeStamp());
            for(int i=1;i< blocks.size();i++){
                BSTNode node = new BSTNode(i, blocks.get(i).getBlockHeader().getTimeStamp());
                indexRoot.add(node);
            }
            indexTree = indexRoot;
        }
    }
}


