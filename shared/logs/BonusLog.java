package com.mynet.shared.logs;


import com.mynet.bonusservice.model.type.BonusRuleTypes;
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

public class BonusLog extends QueueElement{
	BonusRuleTypes type;
	String id;
	int gameCount;
	long money;
	long ticket;
	long joinDate;

	public BonusLog(BonusRuleTypes type, String id, int gameCount, long money, long ticket, long joinDate) {
		this.type = type;
		this.id = id;
		this.gameCount = gameCount;
		this.money = money;
		this.ticket = ticket;
		this.joinDate = joinDate;
	}

	public BonusRuleTypes getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public int getGameCount() {
		return gameCount;
	}

	public long getMoney() {
		return money;
	}

	public long getTicket() {
		return ticket;
	}

	public long getJoinDate() {
		return joinDate;
	}
}
