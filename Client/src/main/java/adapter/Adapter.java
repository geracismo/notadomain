package adapter;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.hildan.fxgson.FxGson;

import java.io.FileReader;

public class Adapter {
    public static String getJson(Object o){
        Gson gson = FxGson.coreBuilder().setPrettyPrinting().create();
        return gson.toJson(o);
    }

    public static <T> T getObject(String jsonString, Class<T> clazz){
        Gson gson = FxGson.coreBuilder().create();
        return gson.fromJson(jsonString, clazz);
    }
    public static <T> T getObjectFromFile(FileReader reader, Class<T> clazz){
        Gson gson = FxGson.coreBuilder().create();
        return gson.fromJson(new JsonReader(reader), clazz);
    }
}
