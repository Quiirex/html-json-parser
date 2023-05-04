package com.company;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.util.Objects;

public class Main {

    /**
     * Builds html file from json data
     */
    public static void convertJsonToHtml (JsonObject jsonObject) throws IOException {
        jsonObject.keySet().forEach(key ->
        {
            JsonElement value = jsonObject.get(key);
            String tagName = value.toString().replaceAll("\"", "");
            String htmlTag = "";

            if (Objects.equals(tagName, "html")) {
                htmlTag += HtmlTagWorker.createHtmlTag(jsonObject);
            }
            try {
                writeToHtmlFile(htmlTag);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Parses the given JSON string to a JsonObject
     */
    private static JsonObject parseJsonString() throws FileNotFoundException {
        Gson gson = new Gson();
        return gson.fromJson(new FileReader("src/com/company/helloWorld.json"), JsonObject.class);
    }

    /**
     * Writes to html file
     */
    static void writeToHtmlFile (String toAdd) throws IOException {
        FileWriter fw = new FileWriter("output.html", true);
        fw.write(toAdd);
        fw.write("\n");
        fw.close();
    }

    public static void main (String[] args) throws IOException {
        File myObj = new File("output.html");
        myObj.delete(); //remove file is exists
        if (myObj.createNewFile()) {
            var parsed = parseJsonString();
            convertJsonToHtml(parsed);
        }
        //če pride do sem, dela :)
    }
}
