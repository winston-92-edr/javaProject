package com.mynet.proxyserver.network.xml;

import com.mynet.proxyserver.network.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.config.ServerConfiguration;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class LoginXmlUtils {
    private static final Logger logger = LoggerFactory.getLogger(LoginXmlUtils.class);

    public static String GetIsOnCanvas(String fuid, String platform) {

        try {
            String decryptedText = StringUtil.AESDecrypt(platform, ServerConfiguration.getXmlConfig("AESsecretKeyWeb"), ServerConfiguration.getXmlConfig("AESviWeb"));
            StringTokenizer stDetails = new StringTokenizer(decryptedText, "|");

            String fbUserId = stDetails.nextToken();
            String dateStr = stDetails.nextToken();
            String chkPlatform = stDetails.nextToken();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dt = formatter.parse(dateStr);

            if (!chkPlatform.equals("canvas")) {
                return "err";
            }

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, -6);
            if (fuid.equals(fbUserId) && dt.compareTo(cal.getTime()) > 0) {
                return "web";
            } else {
                return "err";
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "err";
        }
    }

    public static boolean IsValidLogin(String fuid, String secret, String platform) {

        try {
            String decryptedText = "";

            if (platform.equals("web"))
                decryptedText = StringUtil.AESDecrypt(secret, ServerConfiguration.getXmlConfig("AESsecretKeyWeb"), ServerConfiguration.getXmlConfig("AESviWeb"));
            else
                decryptedText = StringUtil.AESDecrypt(secret, ServerConfiguration.getXmlConfig("AESsecretKey"), ServerConfiguration.getXmlConfig("AESvi"));


            StringTokenizer stDetails = new StringTokenizer(decryptedText, "|");
            String fbUserId = stDetails.nextToken();
            String dateStr = stDetails.nextToken();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dt = formatter.parse(dateStr);

            boolean isOnFacebook = false;
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, -6);
            isOnFacebook = fuid.equals(fbUserId) && dt.compareTo(cal.getTime()) > 0;

            return isOnFacebook;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    private static boolean CheckUserMD5Key(String md5Key, String fuid, String sessionKey, String timeKey, String platform) throws NoSuchAlgorithmException {

        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
        digest.update((fuid + sessionKey + timeKey + platform).getBytes());
        byte[] hash = digest.digest();

        BigInteger bigInt = new BigInteger(1, hash);
        String hashtext = bigInt.toString(16);

        return hashtext.equals(md5Key) || ("0" + hashtext).equals(md5Key);

    }

}
