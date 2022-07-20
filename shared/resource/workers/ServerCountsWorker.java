package com.mynet.shared.resource.workers;

import com.mynet.gameserver.GameController;
import com.mynet.shared.launchers.ProxyServerLauncher;
import com.mynet.shared.resource.db.DataSource;
import com.mynet.shared.resource.db.DatabaseWork;
import com.mynet.shared.resource.db.DatabaseWorker;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import static com.mynet.shared.db.generated.tables.Nodes.NODES;


public class ServerCountsWorker {
    private final static Logger logger = LoggerFactory.getLogger(ServerCountsWorker.class);
    private static ServerCountsWorker instance;
    private static int nodeID;
    public static void init(String serverType, int nodeId){
        if(instance == null) instance = new ServerCountsWorker(serverType);
        nodeID = nodeId;
    }

    public ServerCountsWorker(String serverType) {
        Timer timer = new Timer();
        TimerTask timerTask = new ServerCountsTask(serverType);
        timer.scheduleAtFixedRate(timerTask, 10000, 10000);
    }

    class ServerCountsTask extends TimerTask{
        private final String serverType;

        public ServerCountsTask(String serverType) {
            this.serverType = serverType;
        }

        @Override
        public void run() {
            try {
                SetServerCounts countSetter = new SetServerCounts(serverType);
                DatabaseWorker.getInstance().addWork(new DatabaseWork(countSetter));
            }catch (Exception ex){

            }
        }
    }

    class SetServerCounts implements Callable{
        private final String serverType;

        public SetServerCounts(String serverType) {
            this.serverType = serverType;
        }

        @Override
        public Object call() throws Exception {

            switch (serverType){
                case "proxy":
                    return updateCCU();
                case "game":
                    return updateGameCounts();
            }

            return false;
        }

        private boolean updateCCU(){
            long tcpCount = ProxyServerLauncher.activeTCPConnections.get();
            long webCount = ProxyServerLauncher.activeWebSocketConnections.get();
            long ccu = tcpCount + webCount;

            //logger.info(String.format("ROOM COUNTS | web: %d, mobile: %d, ccu: %d", webCount, tcpCount, ccu ));
            HikariDataSource dataSource = DataSource.getDataSource().get();
            try (DSLContext context = DSL.using(dataSource, SQLDialect.MYSQL)){
                context.update(NODES)
                        .set(NODES.USER_COUNT, (int) ccu)
                        .where(NODES.ID.eq(UInteger.valueOf(nodeID)))
                        .execute();

                return true;
            }catch (Exception e){
                logger.error(e.getMessage(), e);
            }

            return false;
        }

        private boolean updateGameCounts(){
            int count = GameController.getInstance().getPlayingTablesCount();
            int userCount = GameController.getInstance().getUsersCount();

            HikariDataSource dataSource = DataSource.getDataSource().get();
            try (DSLContext context = DSL.using(dataSource, SQLDialect.MYSQL)){
                context.update(NODES)
                        .set(NODES.GAME_COUNT, count)
                        .set(NODES.USER_COUNT, userCount)
                        .where(NODES.ID.eq(UInteger.valueOf(nodeID)))
                        .execute();

                return true;
            }catch (Exception e){
                logger.error(e.getMessage(), e);
            }

            return false;
        }
    }
}
