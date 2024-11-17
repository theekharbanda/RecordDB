package com.recorddb.server.comands;

import com.recorddb.server.Services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static com.recorddb.server.comands.InsertMany.joinMultiWordValues;

public class Find implements Callable<String> {
    private String input;

    public Find(String input){
        this.input = input;
    }

    @Override
    public String call() throws Exception {
        String criteriaString = input.substring(input.indexOf("{") + 1, input.lastIndexOf("}")).trim();

        // Split by commas to get key-value pairs
        String[] criteriaPairs = criteriaString.split(",\\s*");
        Map<String, Object> criteria = new HashMap<>();

        // Store criteria in a map for comparison
        for (String pair : criteriaPairs) {
            String[] keyValue = pair.split(":\\s*");
            String key = keyValue[0].trim();
            String value = joinMultiWordValues(keyValue[1].trim().split(" "));
            criteria.put(key,value);
        }
        return Services.findKeys(criteria);
    }
}
