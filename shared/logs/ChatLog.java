package com.mynet.shared.logs;


import com.mynet.proxyserver.network.StringUtil;
import com.mynet.shared.config.ServerConfiguration;
import com.mynet.shared.network.GameCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatLog {

	private static final Logger logger = LoggerFactory.getLogger(ChatLog.class);

	private String fileName;

	public ChatLog(){}
	
	public synchronized void logChat(String lobbyName, String fuid, String msg, GameCommands type, String whereOrWho, String ip)
	{
		try {

			Date date = new Date();
			SimpleDateFormat ft =  new SimpleDateFormat ("yyyyMMdd");
		    String newDate = ft.format(date);
		    
		    SimpleDateFormat ft2 =  new SimpleDateFormat ("hh:mm:ss");
		    String newDate2 = ft2.format(date);

			File myDir = new File(ServerConfiguration.get("chatpath"));
		    myDir.mkdirs();
		    
		    fileName = lobbyName + "_chatTable_" + newDate +".txt";
		    
			File chatFile = new File(myDir, fileName);
			if(!chatFile.exists()) chatFile.createNewFile();
			msg = StringUtil.normalTurkish(msg);

			FileWriter fileWriter = new FileWriter(chatFile, true);
			fileWriter.write(newDate2 + " # " + fuid + ": " + msg + " # " + whereOrWho  + ":" + ip + "\n");

			fileWriter.close();
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
	}

}
