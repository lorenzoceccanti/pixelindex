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

    /*
    public String toString(){
        String result = "month: " + month;
        result += " <18.y.o.: " + hashMap.get("< 18 y.o.");
        result += " 18-30 y.o.: " + hashMap.get("18-30 y.o.");
        result += " 30-50 y.o.: " + hashMap.get("30-50 y.o.");
        result += " 50+ y.o.: " + hashMap.get("50+ y.o");
        return result;
    }*/

    public String toString(){
        int maxWidthMonth = Math.max(6, 12);
        int maxWidthAge1 = Math.max(12, hashMap.get("< 18 y.o.").toString().length());
        int maxWidthAge2 = Math.max(12, hashMap.get("18-30 y.o.").toString().length());
        int maxWidthAge3 = Math.max(12, hashMap.get("30-50 y.o.").toString().length());
        int maxWidthAge4 = Math.max(12, hashMap.get("50+ y.o").toString().length());

        String header = String.format("| %-" + maxWidthMonth + "s | %-" + maxWidthAge1 + "s | %-" + maxWidthAge2 + "s | %-" + maxWidthAge3 + "s | %-" + maxWidthAge4 + "s |\n",
                "Month", "< 18 y.o.", "18-30 y.o.", "30-50 y.o.", "50+ y.o.");

        StringBuilder separator = new StringBuilder();
        separator.append("+");
        separator.append("-".repeat(maxWidthMonth + 2));
        separator.append("+");
        separator.append("-".repeat(maxWidthAge1 + 2));
        separator.append("+");
        separator.append("-".repeat(maxWidthAge2 + 2));
        separator.append("+");
        separator.append("-".repeat(maxWidthAge3 + 2));
        separator.append("+");
        separator.append("-".repeat(maxWidthAge4 + 2));
        separator.append("+\n");

        String dataRow = String.format("| %-" + maxWidthMonth + "s | %-" + maxWidthAge1 + "d | %-" + maxWidthAge2 + "d | %-" + maxWidthAge3 + "d | %-" + maxWidthAge4 + "d |\n",
                month, hashMap.get("< 18 y.o."), hashMap.get("18-30 y.o."), hashMap.get("30-50 y.o."), hashMap.get("50+ y.o"));

        StringBuilder result = new StringBuilder();
        if(month == 1)
        {
            result.append(separator);
            result.append(header);
        }
        result.append(separator);
        result.append(dataRow);
        if(month == 12){
            result.append(separator);
        }
        return result.toString();
    }
}
