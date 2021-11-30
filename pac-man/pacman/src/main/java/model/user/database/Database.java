package model.user.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

public class Database {

    private final Userbase userbase;
    private final Gson gson;
    {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        gson = builder.create();
    }

    public Database() {
        Userbase userbase;
        try {
            Type USERBASE_TYPE = new TypeToken<Userbase>() {}.getType();
            JsonReader reader = new JsonReader(new FileReader("userbase.json"));

            if (!reader.hasNext())
                userbase = new Userbase();
            else
                userbase = gson.fromJson(reader, USERBASE_TYPE);
        } catch (NullPointerException|IOException exception) {
            userbase = new Userbase();
        }
        this.userbase = userbase;
    }

    public Userbase getUserbase() {
        return userbase;
    }

    public void saveData() throws IOException {
        FileWriter writer = new FileWriter("userbase.json");
        writer.write(gson.toJson(userbase));
        writer.close();
    }
}
