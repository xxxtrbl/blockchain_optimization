package com.example.blockchainoptimization.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * The utility class for serialization.
 * (The key and value of RocksDB can only be stored in byte[] forms.)
 *
 * @author xiyuanwang
 */
public class SerializeUtils {
    public static Object deserialize(byte[] bytes){
        Input input = new Input(bytes);
        Object obj = new Kryo().readClassAndObject(input);
        input.close();
        return obj;
    }

    public static byte[] serialize(Object object){
        Output output = new Output(4096,-1);
        new Kryo().writeClassAndObject(output,object);
        byte[] bytes = output.toBytes();
        output.close();
        return bytes;
    }
}
