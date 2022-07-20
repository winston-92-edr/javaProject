package com.mynet.shared.config;

import com.mynet.shared.model.ServerVariable;
import com.mynet.shared.resource.db.work.GetServerVariables;
import com.mynet.shared.resource.db.DatabaseWork;
import com.mynet.shared.resource.db.DatabaseWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class ServerGlobalVariables {
    private static final Logger logger = LoggerFactory.getLogger(ServerGlobalVariables.class);

    private static ServerGlobalVariables INSTANCE;

    private final ConcurrentHashMap<String, String> variables;

    public ServerGlobalVariables() {
        variables = new ConcurrentHashMap<>();
    }

    public static void init(){
        if(INSTANCE == null){
            ServerGlobalVariables variables = new ServerGlobalVariables();
            variables.startTask();
            INSTANCE = variables;
        }
    }

    public static ServerGlobalVariables getInstance(){
        return INSTANCE;
    }


    private void startTask(){
        Timer timer = new Timer(true);
        TimerTask task = new GlobalVariablesReLoaderTask();
        timer.scheduleAtFixedRate(task, 0, 60000);
    }

    class GlobalVariablesReLoaderTask extends TimerTask{
        @Override
        public void run() {
            try {
                GetServerVariables serverVariables = new GetServerVariables();
                DatabaseWorker.getInstance().addWork(new DatabaseWork(serverVariables, vars ->{
                    List<ServerVariable> list = (List<ServerVariable>) vars;

                    for (ServerVariable v:list) {
                        variables.put(v.getName().toLowerCase(), v.getValue());
                    }
                }));
            }catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        }
    }

    public String get(String name) {
        return variables.get(name.toLowerCase());
    }

    public double getDouble(String name, double def) {
        String st = null;
        st = variables.get(name.toLowerCase());

        try {
            if (st != null) {
                return Double.parseDouble(st);
            }
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            return def;
        }
        return def;
    }

    public float getFloat(String name, float def) {
        String st = null;
        st = variables.get(name.toLowerCase());

        try {
            if (st != null) {
                return Float.parseFloat(st);
            }
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            return def;
        }
        return def;
    }

    public long getLong(String name, long def) {
        String st = null;

        st = variables.get(name.toLowerCase());

        try {
            if (st != null) {
                return Long.parseLong(st);
            }
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            return def;
        }
        return def;
    }

    public String getString(String name, String def) {
        String st = null;

        st = variables.get(name.toLowerCase());

        try {
            if (st != null) {
                return st;
            }
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            return def;
        }
        return def;
    }

    public int getInt(String name, int def) {
        String st = null;

        st = variables.get(name.toLowerCase());

        try {
            if (st != null) {
                return Integer.parseInt(st);
            }
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            return def;
        }
        return def;
    }
}
