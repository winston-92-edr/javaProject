package com.mynet.shared;

import com.mynet.shared.launchers.*;
import com.mynet.shared.config.ServerConfiguration;
import com.mynet.shared.types.ServerType;

public class MainUnit {
    public static boolean DEBUG = false;

    public static void main(String[] args) {
        System.setProperty("my.log", args[1]);
        if(!ServerConfiguration.init(args[0])){
            System.out.println("configuration file problem, exiting..");
            return;
        }

        String groupId = ServerConfiguration.get("group.id");
        if(groupId == null)
        {
            System.out.println("group id not found, exiting..");
            System.exit(2);
        }

        String type = ServerConfiguration.get("server_type");
        int sgType = 0;
        try {
            sgType = ServerConfiguration.getInt("server_game_type");
        }catch (Exception ex){
            System.out.println("server_game_type environment variable NOT FOUND!");
        }
        ServerType serverType = ServerType.forCode(sgType);

        System.out.println(type + " server initializing...");

        String debugMode = System.getenv("DEBUG_MODE");
        if(debugMode != null && debugMode.equals("true")){
            DEBUG = true;
        }

        if(type == null){
            System.out.println("server_type environment variable NOT FOUND!");
        }else {

            switch (type){
                case "proxy":
                    ProxyServerLauncher.launch(type, serverType, groupId);
                    break;
                case "game":
                    GameServerLauncher.launch(type, serverType, groupId);
                    break;
                case "social":
                    SocialServerLauncher.launch(type, serverType, groupId);
                    break;
                case "match":
                    MatchServerLauncher.launch(type, serverType, groupId);
                    break;
                case "table":
                    TableServiceLauncher.launch(groupId);
                    break;
                case "bonus":
                    BonusServiceLauncher.launch(groupId);
                    break;
                case "chat":
                    ChatServerLauncher.launch(groupId);
                    break;
                case "quest":
                    QuestServiceLauncher.launch();
                    break;
                default:
                    System.out.println("Unknown server_type..");
                    break;
            }

        }
    }
}