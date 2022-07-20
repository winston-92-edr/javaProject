package com.mynet.proxyserver.network;

import com.mynet.shared.config.ServerConfiguration;

public class FlashEncryption {
    //public String chars_str = "aZyL-Nv�zI3=CW�hQR�s2j^�BiodE/[�M7;r16*cF�K8Onu�)D]9�Jex'0w�5!q�XU(T%.PgfA &VY#kbS�?l�m_G$pt,H@4<>";
    public String chars_str = "+1CYe2x*#kZ^0.PnEc5$&M(4<>|Nt_=6wBi8u)Dr-bSGfApj]odX[vzIV9K7;W!@U 3QRJs':OaLgTFh,H%/`?lmyq";
    public int[] lookupObj;
    private String encKey = "_,9pZ2o0w /&vn7#@";

    public FlashEncryption()
    {
        int l_length = chars_str.length();
        lookupObj = new int[2048];
        for (int i=0; i<l_length; i++)
        {
            lookupObj[(int)chars_str.charAt(i)] = i;
        }
        encKey = encKey + ServerConfiguration.getXmlConfig("encryptionKey");
    }

    public String encrypt(String message)
    {
        String enc_message = "";
        int kPos = 0;
        for (int i = 0; i < message.length(); i++)
        {
            int offset = getOffset(encKey.charAt(kPos), message.charAt(i));
            enc_message = enc_message + chars_str.charAt(offset);
            kPos++;
            if (kPos >= encKey.length())
                kPos = 0;
        }

        return enc_message;
    }

    public String decrypt(String message)
    {
        StringBuilder dec_message = new StringBuilder();
        int kPos = 0;
        for (int i = 0; i < message.length(); i++)
        {
            int pos1 = lookupObj[(int) message.charAt(i)];
            int pos2 = lookupObj[(int) encKey.charAt(kPos)];
            int nPos = pos1 + pos2;
            nPos = nPos % chars_str.length();
            dec_message.append(chars_str.charAt(nPos));
            kPos++;
            if (kPos >= encKey.length())
                kPos = 0;
        }
        return dec_message.toString();
    }

    public String createAndEncrypt(int MessageType , int Command , String Msg)
    {
        String message = createXML(MessageType, Command, Msg);
        String enc_message = "";
        int kPos = 0;
        for (int i=0; i<message.length(); i++ )
        {
            int offset = getOffset(encKey.charAt(kPos), message.charAt(i));
            enc_message = enc_message + chars_str.charAt(offset);
            kPos++;
            if (kPos >=encKey.length())
                kPos = 0;
        }

        return enc_message;
    }

    public int getOffset(char start, char end)
    {
        int sNum = lookupObj[(int)start];
        int eNum = lookupObj[(int)end];

        int offset = eNum - sNum;
        if (offset < 0)
            offset = chars_str.length() + offset;
        return offset;
    }

    public String createXML(int MessageType, int Command, String Message)
    {
        String xmlText = "";
        xmlText = "<messages>";
        xmlText += "<msg-id>" + MessageType + "</msg-id>";
        xmlText += "<type>" + Command + "</type>";
        xmlText += "<msg-str>" + Message + "</msg-str>";
        xmlText += "</messages>";
        return xmlText;
    }
}
