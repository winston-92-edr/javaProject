package com.mynet.shared.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class MessageProcessController {
    private static final Logger logger = LoggerFactory.getLogger(MessageProcessController.class);

    private static MessageProcessController INSTANCE;
    private final ConcurrentHashMap<GameCommands, MessageProcessor> processors;



    private MessageProcessController(){
        processors = new ConcurrentHashMap<>();
    }

    public static MessageProcessController getInstance(){
        return INSTANCE;
    }

    public static void init(){
       if(INSTANCE == null){
           INSTANCE = new MessageProcessController();
       }
    }

    public void registerCommand(GameCommands cmd, MessageProcessor processor){
        processors.put(cmd, processor);
    }

    public void processMessage(NetworkMessage message) throws InvalidServerMessage {
        MessageProcessor p = processors.get(message.getCmd());
        if(p == null){
            logger.info("Can't find message processor for " + message.getCmd());
        }else{
            p.process(message);
        }
    }
}
