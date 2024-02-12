package it.unipi.largescale.pixelindex.dto;

import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationStatsDTO {
    private int month;
    private HashMap<String, Long> hashMap;

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public HashMap<String, Long> getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap<String, Long> hashMap) {
        this.hashMap = hashMap;
    }

    public String toString(){
        String result = "month: " + month;
        for(Map.Entry<String, Long> entry : hashMap.entrySet()){
            result += " " + (entry.getKey() + " " + entry.getValue());
        }
        result += "\n";
        return result;
    }
}
