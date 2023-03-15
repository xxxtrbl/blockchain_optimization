package com.example.blockchainoptimization.beans;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class Transaction implements Serializable {
    @Serial
    private static final long serialVersionUID = -7730182443793172318L;
    private String sender;
    private String receiver;
    private int value;

    public Transaction(String sender, String receiver, int value) {
        this.sender = sender;
        this.receiver = receiver;
        this.value = value;
    }
}
