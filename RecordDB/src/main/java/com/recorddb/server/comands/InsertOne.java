package com.recorddb.server.comands;

import com.recorddb.server.Services;

import java.util.*;
import java.util.concurrent.Callable;

public class InsertOne implements Callable<String> {
    private String input ;
    public InsertOne(String input){
        this.input = input;
    }

    @Override
    public String call() throws Exception {
        Map<String, Object> entryData = null;
        int id = 0;
        if (input.startsWith("INSERT_ONE")) {
            String DataString = input.substring(input.indexOf("{") + 1, input.indexOf("}")).trim();
            String[] keyValues = DataString.split(",\\s*");

            entryData = new HashMap<>();

            for (String pair : keyValues) {
                String[] keyValue = pair.split(":\\s*");
                if(keyValue.length!=2) throw new Exception("INVALID_COMMAND");

                // Clean the keys and values by removing spaces and unwanted characters
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                // Parse the _id field
                if (key.equals("_id")) {
                    id = Integer.parseInt(value);
                } else {
                    // Store the remaining key-value pairs in the map
                    entryData.put(key, value);
                }
            }
        }
        Services.addOneKey(id, entryData);
        return "SUCCESS";
    }
}
