package com.example.blockchainoptimization.beans;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BlockInfo {
    long timestamp;
    int index;
    String hash;
}
