package com.example.blockchainoptimization.util;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayOutputStream;

/**
 * The utility class for serialization.
 * (The key and value of RocksDB can only be stored in byte[] forms.)
 *
 * @author xiyuanwang
 */
public class SerializeUtils {
//    private static KryoUtils kryoUtils = new KryoUtils();
//
//    public static Object deserialize(byte[] bytes){
//        if (bytes == null){
//            return null;
//        }
//
//        Input input = new Input(bytes);
//        kryoUtils.setRegistrationRequired(false);
//        kryoUtils.setReferences(false);
//        Object obj = kryoUtils.readClassAndObject(input);
//        input.close();
//        return obj;
//    }
//
//    public static byte[] serialize(Object object){
//        kryoUtils.setRegistrationRequired(false);
//        kryoUtils.setReferences(false);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        Output output = new Output(baos,100000);
//        kryoUtils.writeClassAndObject(output,object);
//        byte[] bytes = output.toBytes();
//        output.close();
//        return bytes;
//    }
private static KryoUtils kryoUtils = new KryoUtils();
    /**
     * 反序列化
     * @param bytes 对象对应的字节数组
     */
    public static Object deserialize(byte[] bytes){
        Input input = new Input(bytes);
        kryoUtils.setReferences(true);
        Object object = kryoUtils.readClassAndObject(input);
        input.close();
        return object;
    }
    /**
     * 序列化
     * @param object 序列化对象
     */
    public static byte[] serialize(Object object){
        Output output = new Output(4096,-1);
        kryoUtils.setReferences(true);
        kryoUtils.writeClassAndObject(output, object);
        byte[] bytes = output.toBytes();
        output.close();
        return bytes;
    }
}
