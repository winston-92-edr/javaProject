package com.mynet.proxyserver.network.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.proxyserver.network.FlashEncryption;
import com.mynet.proxyserver.network.StringUtil;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;

public class XmlParser {
    private static final Logger logger = LoggerFactory.getLogger(XmlParser.class);

    public static String getLoginString(String message){

        if(!message.endsWith("</messages>")){
            message +=  "</messages>";
        }

        if (message.startsWith("<policy-file-request")) {
            //TODO: send policy xml
            //sendPolicyXml(chx);
            logger.warn("getLoginString REJECTED : policy-file-request");
            return null;
        }

        if (!message.contains("<enc-sck-type>42</enc-sck-type>")) {
            logger.warn("getLoginString REJECTED : message doesnt contains <enc-sck-type>42</enc-sck-type>");
            logger.info("message: " + message);
            return null;
        }

        try {
            String decryptedXmlStr;

            if (message.startsWith("<")) {
                decryptedXmlStr = message + ">";
            } else {
                decryptedXmlStr = decryptXml(message);
            }

            if(decryptedXmlStr.length() < 8){
                logger.warn("getLoginString REJECTED : decryptedXmlStr length under 8");
                return null;
            }

            XmlDocHandler xml = new XmlDocHandler(decryptedXmlStr);
            decryptedXmlStr = xml.getMessage();

            if (xml.getSendInitError() == 1) {
                logger.warn("getLoginString REJECTED : getSendInitError equals 1");
                return null;
            }

            String[] xmlTokens = StringUtil.processRawString(decryptedXmlStr, ";");

            if (xmlTokens.length < 5) {
                logger.warn("getLoginString REJECTED : xmlTokens length under 5");
                return null;
            }

            return decryptedXmlStr;

        } catch (Exception e){
            logger.error("getLoginString FAILED");
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public static NetworkMessage getNetworkMessage(String message){

        if(!message.endsWith("</messages>")){
            message +=  "</messages>";
        }

        try {

            XmlDocHandler xml = new XmlDocHandler(message);
            GameCommands command = GameCommands.forCode(xml.getType());
            if(command == null){
                logger.warn("CMD: " + xml.getType() + " is null and ignored!");
            }else{
                return new NetworkMessage(String.valueOf(xml.getMessageId()), command, xml.getMessage());
            }


        } catch (Exception e){
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    private static String decryptXml(String xmlString) {
        String decryptedXmlStr = xmlString;
        decryptedXmlStr = decryptedXmlStr.replaceAll("&lt;", "<");
        decryptedXmlStr = decryptedXmlStr.replaceAll("&gt;", ">");
        decryptedXmlStr = decryptedXmlStr.replaceAll("_%aWp%_", "&");
        decryptedXmlStr = new FlashEncryption().decrypt(decryptedXmlStr);
        return decryptedXmlStr;
    }

    public static String createXML(int MessageType, int Command, String Message, String platform) {
        return createXML(MessageType, Command, Message, platform,true);
    }

    public static String createXML(int MessageType, int Command, String Message, String platform, boolean success) {
        String xmlText = "";
        xmlText = "<messages>";
        if (MessageType == 1) {
            xmlText += "<msg-id>" + MessageType + "</msg-id>";
        } else {
            xmlText += "<enc-sck-id>" + MessageType + "</enc-sck-id>";
        }
        if (MessageType == 1) {
            xmlText += "<type>" + Command + "</type>";
        } else {
            xmlText += "<enc-sck-type>" + Command + "</enc-sck-type>";
        }
        if (MessageType == 1) {
            xmlText += "<msg-str>" + Message + "</msg-str>";
        } else {
            xmlText += "<enc-sck-str>" + Message + "</enc-sck-str>";
        }
        if (MessageType == 1) {
            xmlText += "<msg-s>" + (success ? 1 : 0) + "</msg-s>";
        } else {
            xmlText += "<enc-sck-s>" + (success ? 1 : 0) + "</enc-sck-s>";
        }

        xmlText += "</messages>";


        String msg;
        String b;
        if (!platform.startsWith("web")) {

            xmlText = xmlText.replaceAll("%", "~");
            msg = xmlText;

            b = msg;
        } else {
            xmlText = xmlText.replaceAll("~", "%");
            b = xmlText.replace("<enc-sck-str>", "<enc-sck-str><![CDATA[");
            b = b.replace("</enc-sck-str>", "]]></enc-sck-str>");
            b = b + "\u0000";
        }

        return b;
    }
}
