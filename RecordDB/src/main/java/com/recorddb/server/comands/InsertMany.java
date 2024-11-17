package com.recorddb.server.comands;

import com.recorddb.server.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class InsertMany implements Callable<String> {
    private String input;

    public InsertMany(String input) {
        this.input = input;
    }

    // Helper function to join multi-word values
    public static String joinMultiWordValues(String[] words) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            sb.append(words[i]);
            if (i != words.length - 1) sb.append(" ");
        }
        return sb.toString();
    }

    @Override
    public String call() throws Exception {
        Map<Integer,Map<String,Object>> entryData = new HashMap<>();
        if (input.startsWith("INSERT_MANY")) {
            String dataString = input.substring(input.indexOf("[") + 1, input.lastIndexOf("]")).trim();

            // Split by closing curly braces and opening curly braces to separate objects
            String[] objects = dataString.split("},\\s*\\{");

            for (String object : objects) {
                // Clean up the braces
                object = object.replace("{", "").replace("}", "").trim();

                // Split the string by commas to extract key-value pairs
                String[] keyValuePairs = object.split(",\\s*");

                // Create a new list for each entry's data
                Map<String,Object> Data = new HashMap<>();

                int id = 0;

                // Iterate over the key-value pairs
                for (String pair : keyValuePairs) {
                    String[] keyValue = pair.split(":\\s*");

                    String key = keyValue[0].trim();
                    String value = joinMultiWordValues(keyValue[1].trim().split(" "));

                    // Parse the _id field and use it as the key in the outer map
                    if (key.equals("_id")) {
                        id = Integer.parseInt(value);
                    } else {
                        // Add the value to the entryData list
                        Data.put(key,value);
                    }
                }
                entryData.put(id,Data);
            }
        }
        List<String> result = Services.addManyKeys(entryData);
        return String.join(",", result);

    }
}