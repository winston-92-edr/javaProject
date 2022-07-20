package com.mynet.shared.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DateUtils {
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss.Ms";
    public static final HashMap<String,String> mn = new HashMap<String , String>() {{
        put("Jan","Oca");
        put("Feb","Sub");
        put("Mar","Mar");
        put("Apr","Nis");
        put("May","May");
        put("Jun","Haz");
        put("Jul","Tem");
        put("Aug","Agu");
        put("Sep","Eyl");
        put("Oct","Eki");
        put("Nov","Kas");
        put("Dec","Ara");
    }};

    public static String Now()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }

    public static String changeDateToTurkish(String dateStr){

        //System.out.println("dateStr: " + dateStr);
        String[] splitDate = dateStr.split(" ");
        String splitMn = splitDate[1];

        String nMn = mn.get(splitMn);
        if(nMn.equals(null)){
            return dateStr;
        }

        return splitDate[0] +" "+ nMn +" "+splitDate[2];
    }
}
