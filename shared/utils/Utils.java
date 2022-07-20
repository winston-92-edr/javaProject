package com.mynet.shared.utils;

import com.mynet.matchserver.model.GameTypeInfo;
import com.mynet.shared.config.ServerConfiguration;
import com.mynet.shared.types.GameType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.StringTokenizer;

public class Utils {
    public static String dateConversion(long timestamp) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }

    public static GameTypeInfo[] getGameTypes(){

        String strings = ServerConfiguration.get("game_types");
        if(strings == null) return null;

        StringTokenizer st = new StringTokenizer(strings, "|");
        GameTypeInfo[] gameTypeInfo = new GameTypeInfo[st.countTokens()];
        int index = 0;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            StringTokenizer stGame = new StringTokenizer(token, ".");
            GameType gameType = GameType.forCode(Integer.parseInt(stGame.nextToken()));
            int playerCount = Integer.parseInt(stGame.nextToken());
            gameTypeInfo[index] = new GameTypeInfo(gameType, playerCount);
            index++;
        }


        return gameTypeInfo;
    }

    public static long getMidnight() {
        Date midnight = new Date();
        midnight.setHours(24);
        midnight.setMinutes(0);
        midnight.setSeconds(0);

        return midnight.getTime();
    }

    public static String getCurrentDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        return dtf.format(now);
    }

    public static void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();
    }

    public static boolean isOldVersion(String currentVersion, String userVersion){
        if(currentVersion == null || userVersion == null  || currentVersion.isEmpty() || userVersion.isEmpty()) return false;

        int comparisonResult = 0;

        String[] version1Splits = currentVersion.split("\\.");
        String[] version2Splits = userVersion.split("\\.");
        int maxLengthOfVersionSplits = Math.max(version1Splits.length, version2Splits.length);

        for (int i = 0; i < maxLengthOfVersionSplits; i++){
            Integer v1 = i < version1Splits.length ? Integer.parseInt(version1Splits[i]) : 0;
            Integer v2 = i < version2Splits.length ? Integer.parseInt(version2Splits[i]) : 0;
            int compare = v1.compareTo(v2);
            if (compare != 0) {
                comparisonResult = compare;
                break;
            }
        }
        return comparisonResult > 0;
     }

    public static String getName(String id, String name) {
        String firstName = "";

        if (id.startsWith("99900") && name.equals("Misafir")) {
            String guestId = id.substring(id.length() - 7);
            firstName = "M-" + guestId;
        }else{
            firstName = name;
        }
        return firstName;
    }

    public static boolean checkSameIp(){
        return ServerConfiguration.getInt("ignoreSameIP", 0) == 0;
    }
}
