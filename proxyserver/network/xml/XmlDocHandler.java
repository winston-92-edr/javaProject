package com.mynet.proxyserver.network.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class XmlDocHandler {
    private static final Logger logger = LoggerFactory.getLogger(XmlDocHandler.class);

    private Document doc;
    private int message_id = -1;
    private int type = -1;
    private String msg = "";
    private int sendInitError;


    public XmlDocHandler(String xmlString) {
        sendInitError = 0;
        //xmlString += "df#^sd";

        if (xmlString.substring(xmlString.length() - 8).equals("messages")) {
            xmlString = xmlString + ">";
        }

        if (xmlString.contains("</messages>")) {
            if (xmlString.length() - xmlString.indexOf("</messages>") > 11) {
                xmlString = xmlString.substring(0, xmlString.indexOf("</messages>") + 11);
            }
        }

        if (xmlString.startsWith("<messages><t>42")) {
            return;
        }

        if (xmlString.startsWith("<policy-file-request")) {
            return;
        }

        xmlString.replaceAll("~", "%");
        String XML = xmlString;

        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            try {
                doc = docBuilder.parse(new InputSource(new StringReader(xmlString)));
            } catch (SAXParseException saxParseException) {
                try {
                    if ((xmlString.contains("<type>42</type>")) || (xmlString.contains("<type>67</type>"))) {
                        return;
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }

            try {
                if(doc != null){
                    doc.getDocumentElement().normalize();
                }else{
                    logger.error("doc is nul xmlString: " + xmlString);
                }
            } catch (Exception e) {
                logger.error("xmlString: " + xmlString);
                logger.error(e.getMessage(), e);
            }

            NodeList messageTag = doc.getElementsByTagName("messages");
            Node messageNode = messageTag.item(0);
            Element messageElement = (Element) messageNode;
            NodeList mes_id;
            int isChat = xmlString.contains("msg-id") ? 1 : 0;
            if (isChat == 1) {
                mes_id = messageElement.getElementsByTagName("msg-id");
            } else {
                mes_id = messageElement.getElementsByTagName("enc-sck-id");
            }
            Element mes_id_element = (Element) mes_id.item(0);
            NodeList textMIList = mes_id_element.getChildNodes();
            setMessageId(((Node) textMIList.item(0)).getNodeValue().trim());

            NodeList type_nl;
            if (isChat == 1) {
                type_nl = messageElement.getElementsByTagName("type");
            } else {
                type_nl = messageElement.getElementsByTagName("enc-sck-type");
            }
            Element type_nl_element = (Element) type_nl.item(0);
            NodeList typeNList = type_nl_element.getChildNodes();
            setType(((Node) typeNList.item(0)).getNodeValue().trim());

            NodeList msg_str_nl;
            if (isChat == 1) {
                msg_str_nl = messageElement.getElementsByTagName("msg-str");
            } else {
                msg_str_nl = messageElement.getElementsByTagName("enc-sck-str");
            }
            Element msg_str_nl_element = (Element) msg_str_nl.item(0);
            NodeList typeMSGList = msg_str_nl_element.getChildNodes();

            try {
                if ((Node) typeMSGList.item(0) != null) {
                    setMessage(((Node) typeMSGList.item(0)).getNodeValue().trim());
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            if (!(message_id == 2 && type == 42)) {
                NodeList socketid_nl;
                socketid_nl = messageElement.getElementsByTagName("s");
                Element socketid_nl_element = (Element) socketid_nl.item(0);
                if (socketid_nl_element != null) {
                    NodeList socketidNList = socketid_nl_element.getChildNodes();
                    // setSocketidStr(((Node)socketidNList.item(0)).getNodeValue().trim());
                    setSocketidStr(((Node) socketidNList.item(0)).getNodeValue());
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

    }

    private void setMessageId(String id) {
        try {
            message_id = Integer.parseInt(id);
        } catch (Exception e) {
            message_id = -1;
        }
    }

    public int getMessageId() {
        return message_id;
    }

    private void setType(String type) {
        try {
            this.type = Integer.parseInt(type);
        } catch (Exception e) {
            this.type = -1;
        }
    }

    public int getType() {
        return type;
    }

    private void setMessage(String msg) {
        this.msg = msg;
    }

    public String getMessage() {
        return msg;
    }

    private void setSocketidStr(String msg) {
    }
    public int getSendInitError() {
        return sendInitError;
    }
}
