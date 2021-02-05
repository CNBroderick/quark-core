package org.bklab.quark.util.object;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@SuppressWarnings("unchecked")
public class DeepCopy<T> {

    private final T origin;

    public DeepCopy(T origin) {
        this.origin = origin;
    }

    public T viaStream() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(origin);
            out.flush();
            out.close();
            return (T) new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray())).readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private T viaGson() {
        return new Gson().fromJson(new Gson().toJson(origin), (Class<T>) origin.getClass());
    }

}
