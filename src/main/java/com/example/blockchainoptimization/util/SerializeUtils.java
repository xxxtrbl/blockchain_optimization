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
        kryoUtils.setRegistrationRequired(false);
        Input input = new Input(bytes);
        kryoUtils.setReferences(false);
        Object obj = kryoUtils.readClassAndObject(input);
        input.close();
        return obj;
    }

    public static byte[] serialize(Object object){
        kryoUtils.setRegistrationRequired(false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos,10000000);
        kryoUtils.setReferences(false);
        kryoUtils.writeClassAndObject(output,object);
        byte[] bytes = output.toBytes();
        output.close();
        return bytes;
    }
}
