package io.thunder.utils.objects;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import io.thunder.Thunder;
import lombok.AllArgsConstructor;

import java.io.*;

@AllArgsConstructor
public class Serializer<T extends Serializable> {

    /**
     * The object
     */
    private T object;

    /**
     * Sets object to null
     */
    public Serializer() {
        this(null);
    }

    /**
     * Serializes an object
     * @param object the object
     * @return byte array
     */
    public byte[] serialize(T object) {
        this.object = object;
        return this.serialize();
    }
    /**
     * Serializes an Object into a String
     *
     * @return object converted to bytes
     */
    public byte[] serialize() {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(this.object);
            so.flush();
            return bo.toByteArray();
        } catch (Exception e) {
            Thunder.ERROR_HANDLER.onError(e);
        }
        return null;
    }

    /**
     * Deserializes an Object from a given input String
     *
     * @param s the input
     * @return object
     */
    public T deserialize(byte[] b) {
        try {
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);

            Object o = si.readObject();
            return (T) o;
        } catch (Exception e) {
            Thunder.ERROR_HANDLER.onError(e);
        }
        return null;
    }
}
