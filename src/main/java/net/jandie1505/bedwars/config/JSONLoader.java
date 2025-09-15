package net.jandie1505.bedwars.config;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class JSONLoader {

    public static @NotNull JSONObject loadJSONFromFile(@NotNull File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return new JSONObject(sb.toString());
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public static boolean saveJSONToFile(@NotNull File file, @NotNull JSONObject json, int indentFactor) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(indentFactor > 0 ? json.toString(indentFactor) : json.toString());
            writer.flush();
            writer.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
