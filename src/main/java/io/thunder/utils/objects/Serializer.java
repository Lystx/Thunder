package io.thunder.utils.objects;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import io.thunder.Thunder;
import lombok.AllArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@AllArgsConstructor
public class Serializer<T> {

    /**
     * The object
     */
    private final T object;

    /**
     * Sets object to null
     */
    public Serializer() {
        this(null);
    }

    /**
     * Serializes an Object into a String
     *
     * @return object converted to String
     */
    public String serialize() {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(this.object);
            so.flush();
            return Base64.encode(bo.toByteArray());
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
    public T deserialize(String s) {
        try {
            byte[] b = Base64.decode(s.getBytes());
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);
            return (T) si.readObject();
        } catch (Exception e) {
            Thunder.ERROR_HANDLER.onError(e);
        }
        return null;
    }
}
