package com.mynet.questservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;
import java.util.Hashtable;

public class QuestSettings
{
    private static final Logger logger = LoggerFactory.getLogger(QuestSettings.class);
    private static Hashtable<Object, Object> hValues = new Hashtable<Object, Object>();

    public static void setValue(Object key, Object value)
    {
        boolean result = hValues.containsKey(key);
        if (result)
        {
            hValues.remove(key);
            hValues.put(key, value);
        }
        else
        {
            hValues.put(key, value);
        }
    }

    public static Object getValue(String key)
    {
        return hValues.get(key);
    }

    public static Enumeration<Object> getEnum()
    {
        return hValues.elements();
    }

    public static void remove(String key)
    {
        hValues.remove(key);
    }

    public static int getInt(String name, int def) {
        try {
            if (hValues.containsKey(name)) {
                return Integer.parseInt((String)(hValues.get(name)));
            }

        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
        }
        return def;
    }

    public static float getFloat(String name, float def) {
        try {
            if (hValues.containsKey(name)) {
                return  Float.parseFloat((String)(hValues.get(name)));
            }
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
        }
        return def;
    }

    public static boolean getBoolean(String name, boolean def) {
        try {
            if (hValues.containsKey(name)) {
                return  Boolean.parseBoolean(hValues.get(name).toString());
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return def;
    }

    public static String getString(String name, String def) {
        try {
            if (hValues.containsKey(name)) {
                return  (String)(hValues.get(name));
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return def;
    }

    public static long getLong(String name, long def){
        try {
            if (hValues.containsKey(name)) {
                return Long.parseLong((String)(hValues.get(name)));
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return def;
    }
}

