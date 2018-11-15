package main.util;

import com.google.gson.Gson;

/**
 * Serializes and deserializes objects sent to and from the client
 *
 * Note: the types of objects that are received and sent are pretty complex, we might need to revisit this a few times
 */
public class Serializer {

    public static String serialize(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public static <T> T deserialize(String string, Class<T> classType) {
        Gson gson = new Gson();
        return gson.fromJson(string, classType);
    }

}
