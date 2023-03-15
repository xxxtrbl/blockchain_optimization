package com.example.blockchainoptimization.beans;

import lombok.Data;

@Data
public class Transaction {
    private String sender;
    private String receiver;
    private int value;
}
