package com.mynet.shared.utils;

import org.hashids.Hashids;
import com.google.common.io.BaseEncoding;

public class HashidsHelper {
    public static Hashids hashids = new Hashids("SALTINTHELAKE",8,"ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");

    public static String encode (String id){
        return BaseEncoding.base64().encode(id.getBytes());

    }

    public static String decode(String id) {
        try {
            return new String(BaseEncoding.base64().decode(id));
        } catch (Exception e) {
            return null;
        }
    }

    public static String hashidsDecode(String id) {
        try {
            return hashids.decodeHex(id);
        } catch (Exception e) {
            return null;
        }
    }
}
