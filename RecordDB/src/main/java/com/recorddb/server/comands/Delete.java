package com.recorddb.server.comands;

import com.recorddb.server.Services;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import static com.recorddb.server.comands.InsertMany.joinMultiWordValues;

public class Delete implements Callable<String> {
    private String input;

    public  Delete(String input){
        this.input = input;
    }
    @Override
    public String call() throws Exception {
        Map<String, Object> criteria = null;
        if (input.startsWith("DELETE")) {
            String criteriaString = input.substring(input.indexOf("{") + 1, input.lastIndexOf("}")).trim();
            // Split by commas to get key-value pairs
            String[] criteriaPairs = criteriaString.split(",\\s*");
            criteria = new HashMap<>();

            // Store criteria in a map for comparison
            for (String pair : criteriaPairs) {
                String[] keyValue = pair.split(":\\s*");
                String key = keyValue[0].trim();
                String value = joinMultiWordValues(keyValue[1].trim().split(" "));
                criteria.put(key, value);
            }
        }
        AtomicInteger count = Services.deleteKey(criteria);
        return "DELETED "+ count +" File(s)";
    }
}
