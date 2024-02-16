package it.unipi.largescale.pixelindex.dto;

import java.util.HashMap;

public class RegistrationStatsDTO {
    private int month;
    private HashMap<String, Long> hashMap;

    public void setMonth(int month) {
        this.month = month;
    }

    public void setHashMap(HashMap<String, Long> hashMap) {
        this.hashMap = hashMap;
    }

    public String toString(){
        Long lessThan18 = 0L; Long moreThan18 = 0L;
        Long moreThan30 = 0L; Long moreThan50 = 0L;
        int maxWidthAge1 = 12, maxWidthAge2 = 12, maxWidthAge3 = 12, maxWidthAge4 = 12;
        int maxWidthMonth = Math.max(6, 12);
        if(hashMap.get("< 18 y.o.") != null) {
            maxWidthAge1 = Math.max(12, hashMap.get("< 18 y.o.").toString().length());
            lessThan18 = hashMap.get("< 18 y.o.");
        }
        if(hashMap.get("18-30 y.o.") != null){
            maxWidthAge2 = Math.max(12, hashMap.get("18-30 y.o.").toString().length());
            moreThan18 = hashMap.get("18-30 y.o.");
        }
        if(hashMap.get("30-50 y.o.") != null){
            maxWidthAge3 = Math.max(12, hashMap.get("30-50 y.o.").toString().length());
            moreThan30 = hashMap.get("30-50 y.o.");
        }
        if(hashMap.get("50+ y.o") != null){
            maxWidthAge4 = Math.max(12, hashMap.get("50+ y.o").toString().length());
            moreThan50 = hashMap.get("50+ y.o");
        }

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
                month, lessThan18, moreThan18, moreThan30, moreThan50);

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
