package com.example.blockchainoptimization.beans;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class EasyNode implements Serializable {
    @Serial
    private static final long serialVersionUID = 8010082335103586895L;
    private long timestamp;
    private int index;

    public EasyNode(){

    }
}
