package com.mynet.shared.config;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Properties;

public class ServerConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ServerConfiguration.class);

    private static Properties properties;
    private static JSONObject jsonObject;

    public static boolean init(String filePath){
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(filePath)){
            properties.load(fis);
            properties.putAll(System.getProperties());
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            return false;
        }

        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("xmlConfig.json")){
            Object obj = jsonParser.parse(reader);
            jsonObject = (JSONObject) obj;
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            return false;
        }

        return true;
    }

    public static String getXmlConfig(String key){
        return (String) jsonObject.get(key);
    }

    public static String get(String setting, String def){
        String st = properties.getProperty(setting);
        if (st == null){
            return def;
        }else {
            return st;
        }
    }

    public static boolean has(String setting){
        return properties.getProperty(setting) != null;
    }

    public static String get(String setting) {
        return properties.getProperty(setting);
    }

    public static int getInt(String setting){
        return Integer.parseInt(properties.getProperty(setting));
    }

    public static int getInt(String setting, int def){
        try {
            return Integer.parseInt(properties.getProperty(setting));
        }catch (Exception e){
        }

        return def;
    }

    public static void set(String setting, String value) {
        properties.setProperty(setting, value);
    }
}
