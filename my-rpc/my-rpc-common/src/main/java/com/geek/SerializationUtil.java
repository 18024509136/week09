package com.geek;

import io.protostuff.Schema;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 序列化工具类（基于 Protostuff 实现）
 */
public class SerializationUtil {

    private static final Map<Class<?>, Schema<?>> SCHEMA_MAP = new ConcurrentHashMap<>();

    private static final Class<SerializeDeserializeWrapperObj> SERIALIZE_DESERIALIZE_WRAPPER_OBJ_CLASS =
            SerializeDeserializeWrapperObj.class;

    private static final Schema<SerializeDeserializeWrapperObj> WRAPPER_SCHEMA =
            RuntimeSchema.createFrom(SERIALIZE_DESERIALIZE_WRAPPER_OBJ_CLASS);

    private static final Set<Class<?>> WRAPPER_CLASS_SET = new HashSet<>();

    static {
        WRAPPER_CLASS_SET.add(List.class);
        WRAPPER_CLASS_SET.add(ArrayList.class);
        WRAPPER_CLASS_SET.add(Set.class);
        WRAPPER_CLASS_SET.add(Map.class);
        WRAPPER_CLASS_SET.add(HashMap.class);
        WRAPPER_CLASS_SET.add(Date.class);

    }

    public static <T> byte[] serializer(T o) {

        if (WRAPPER_CLASS_SET.contains(o.getClass())) {
            return ProtostuffIOUtil.toByteArray(SerializeDeserializeWrapperObj.builder(o), WRAPPER_SCHEMA, LinkedBuffer.allocate(1024));
        } else {
            return ProtostuffIOUtil.toByteArray(o, getSchema(o.getClass()), LinkedBuffer.allocate(1024));
        }
    }


    public static <T> Schema getSchema(Class<T> clazz) {
        if (SCHEMA_MAP.containsKey(clazz)) {
            return SCHEMA_MAP.get(clazz);
        } else {
            Schema<T> schema = RuntimeSchema.createFrom(clazz);
            SCHEMA_MAP.put(clazz, schema);
            return schema;
        }
    }

    public static <T> T deserializer(byte[] bytes, Class<T> clazz) throws IllegalAccessException, InstantiationException {


        if (WRAPPER_CLASS_SET.contains(clazz)) {
            SerializeDeserializeWrapperObj<T> obj = new SerializeDeserializeWrapperObj<>();
            ProtostuffIOUtil.mergeFrom(bytes, obj, WRAPPER_SCHEMA);
            return obj.getData();
        } else {
            Schema<T> schema = getSchema(clazz);
            T obj = clazz.newInstance();
            ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
            return obj;
        }
    }


    private static class SerializeDeserializeWrapperObj<T> {

        private T data;

        public static <T> SerializeDeserializeWrapperObj<T> builder(T data) {
            SerializeDeserializeWrapperObj<T> wrapper = new SerializeDeserializeWrapperObj<>();
            wrapper.setData(data);
            return wrapper;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }
}
