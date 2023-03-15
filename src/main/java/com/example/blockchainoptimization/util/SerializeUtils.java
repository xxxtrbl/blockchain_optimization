package com.example.blockchainoptimization.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * The utility class for serialization.
 * (The key and value of RocksDB can only be stored in byte[] forms.)
 *
 * @author xiyuanwang
 */
public class SerializeUtils {
    private static KryoUtils kryoUtils = new KryoUtils();

    public static Object deserialize(byte[] bytes){
        Input input = new Input(bytes);
        kryoUtils.setRegistrationRequired(false);
        kryoUtils.setReferences(true);
        Object obj = kryoUtils.readClassAndObject(input);
        input.close();
        return obj;
    }

    public static byte[] serialize(Object object){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Output output = new Output(bos);
        kryoUtils.setRegistrationRequired(false);
        kryoUtils.setReferences(true);
        kryoUtils.writeClassAndObject(output,object);
        byte[] bytes = output.toBytes();
        output.close();
        return bytes;
    }
}
