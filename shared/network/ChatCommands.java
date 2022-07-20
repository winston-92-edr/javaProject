package com.mynet.shared.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class ChatCommands
{
	public static final int ONE_TO_ONE = 1;
	public static final int TABLE = 2;
	public static final int ROOM = 3;

	private static final Logger logger = LoggerFactory.getLogger(ChatCommands.class);

	public static String GetCommandName(int cmd)
	{
		try
		{
			Class<?> c = Class.forName("mynet.game.ChatCommands");
			Field[] fields = c.getDeclaredFields();
			for (int i = 0; i < fields.length; i++)
			{
				Field f = fields[i];
				if (f.getInt(null) == cmd)
					return f.getName();
			}
		}
		catch (Exception ex)
		{
			logger.error(ex.getMessage(), ex);
		}
		return "Unknown chat command: " + cmd;
	}
}
