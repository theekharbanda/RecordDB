package com.recorddb.server;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Services {

    private static Map<Integer,Map<String,Object>> DataBase = new ConcurrentHashMap<>();

    public static Map<Integer, Map<String,Object>> getDataBase() {
        return DataBase;
    }


    //Write a data of specific key to the file
//    public static void writeToFile(String key) throws IOException {
//        for (Map.Entry<String, UserContent> entry : DataBase.entrySet()) {
//            FileOutputStream fs = new FileOutputStream(Constants.PathToDB + entry.getKey()+".bin",true);
//            ObjectOutputStream writer = new ObjectOutputStream(fs);
//            if(entry.getKey().equals(key)){
//                writer.writeObject(entry.getKey() + "=" + entry.getValue());
//            }
//            writer.flush();
//            fs.close();
//            writer.close();
//        }
//
//    }

    //Writes all the data in DATABASE
    public static void writeToFile() throws IOException {
        for (Map.Entry<Integer, Map<String, Object>> entry : DataBase.entrySet()) {
            FileOutputStream fs = new FileOutputStream(Constants.PathToDB + entry.getKey()+".bin",true);
            ObjectOutputStream writer = new ObjectOutputStream(fs);
            writer.writeObject(entry.getKey() + "=" + entry.getValue());
            writer.flush();
            fs.close();
            writer.close();
        }
    }

    //Clears memory
    public static void clearMemory(){
        DataBase.clear();
    }



//    DELETES key in DATABASE
    public static synchronized AtomicInteger deleteKey(Map<String, Object> criteria){
        // Use lambda expression to remove entries that match the criteria
        AtomicInteger count= new AtomicInteger();
        DataBase.entrySet().removeIf(entry -> {
            Map<String, Object> entryData = entry.getValue();

            // Check if the entry matches the delete criteria
            for (Map.Entry<String, Object> criterion : criteria.entrySet()) {
                if (!entryData.containsKey(criterion.getKey()) || !entryData.get(criterion.getKey()).equals(criterion.getValue())) {
                    return false; // If any criterion does not match, return false
                }
            }
            count.getAndIncrement();
            return true; // If all criteria match, return true (to remove the entry)
        });
        return count;
    }

    public static synchronized void addOneKey(Integer key,Map<String,Object> entryData ) throws IOException {
        DataBase.put(key,entryData);
        writeToFile();
    }

    public static synchronized List<String> addManyKeys(Map<Integer,Map<String,Object>> entryData) throws IOException {
        List<String> successMessages = new ArrayList<>();

        for (Map.Entry<Integer, Map<String, Object>> entry : entryData.entrySet()) {
            Integer id = entry.getKey();
            Map<String, Object> data = entry.getValue();

            if (DataBase.containsKey(id)) {
                successMessages.add("ID_CONFLICT");
            } else {
                DataBase.put(id, data);
                successMessages.add("SUCCESS");
            }
        }
        writeToFile();
        return successMessages;

    }

    public static synchronized String findKeys( Map<String, Object> criteria ){
        List<Integer> matchingIds = new ArrayList<>();
        DataBase.entrySet().stream()
                .filter(entry -> {
                    Map<String, Object> entryData = entry.getValue();
                    // Check if the entry matches all the criteria
                    return criteria.entrySet().stream().allMatch(criterion ->
                            entryData.containsKey(criterion.getKey()) &&
                                    entryData.get(criterion.getKey()).equals(criterion.getValue())
                    );
                })
                .forEach(entry -> matchingIds.add(entry.getKey())); // Collect matching IDs
        Collections.sort(matchingIds);
        if(matchingIds.isEmpty()) {
            return "NO_RECORD_AVAILABLE";
        }
        return matchingIds.stream()
                .map(String::valueOf)  // Convert each Integer to String
                .collect(Collectors.joining(","));
    }
}

