package com.mynet.shared.model;

import com.mynet.matchserver.model.GameTypeInfo;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.types.GameType;
import com.mynet.shared.user.ProxyUser;
import com.mynet.socialserver.model.UserTournamentStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.response.ClientTournamentInfoResponse;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TournamentLevelController {
    private static final Logger logger = LoggerFactory.getLogger(TournamentLevelController.class);

    private static TournamentLevelController instance;
    private ConcurrentHashMap<Integer, TournamentModel> tournamentHashMap;
    private long updatedTime = 0;
    private boolean taskScheduled;
    private GameTypeInfo[] gameTypeInfoList;


    public static void init(){
        try {
            if (instance == null) {
                instance = new TournamentLevelController();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
    public static void init(GameTypeInfo[] info){
        try {
            if (instance == null) {
                instance = new TournamentLevelController(info);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
    public static TournamentLevelController getInstance() {
        return instance;
    }

    public TournamentLevelController() {
        setTournamentHashMap();
        if(!taskScheduled){
            Timer timer = new Timer(true);
            TimerTask task = new TournamentActivityTask();
            timer.scheduleAtFixedRate(task, 0, 60000);
            taskScheduled = true;
        }
    }
    public TournamentLevelController(GameTypeInfo[] info) {
        gameTypeInfoList = info;
        setTournamentHashMap();
        if(!taskScheduled){
            Timer timer = new Timer(true);
            TimerTask task = new TournamentActivityTask();
            timer.scheduleAtFixedRate(task, 0, 60000);
            taskScheduled = true;
        }
    }

    public GameTypeInfo getGameTypeInfo(int tournamentId){
        TournamentModel tournament = this.getTournament(tournamentId);
        GameType type = tournament.getSideCount() == 4 ? GameType.TOURNAMENT_CLASSIC : GameType.TOURNAMENT_DUELLO;
        for (GameTypeInfo typeInfo : gameTypeInfoList) {
            if(typeInfo.getGameType() == type){
                return typeInfo;
            }
        }
        return null;
    }

    private void setTournamentHashMap(){
        HashMap<Integer, TournamentModel> tournamentMap = new HashMap<>();
        long current = System.currentTimeMillis();

        List<TournamentModel> tournaments = DBController.getInstance().getTournaments();

        if (tournaments.size() == 0){
            logger.info("NO TOURNAMENT FOUND!");
        }else {
            List<TournamentLevel> tournamentLevels = DBController.getInstance().getTournamentLevels();
            for (TournamentModel tournament: tournaments) {
                Predicate<TournamentLevel> byTid = level -> level.getTournamentId() == tournament.getTournamentId();
                List<TournamentLevel> levels = tournamentLevels.stream().filter(byTid).collect(Collectors.toList());
                tournament.setLevels(levels);

                boolean active = tournament.getStartDate() == -1 || (tournament.getStartDate() <= current && tournament.getEndDate() >= current);
                tournament.setActive(active);
                tournamentMap.put(tournament.getTournamentId(), tournament);
            }

        }
        tournamentHashMap = new ConcurrentHashMap<>(tournamentMap);
        updatedTime = System.currentTimeMillis();
    }


    public TournamentModel getTournament(int id) {
        return tournamentHashMap.get(id);
    }


    public List<ClientTournamentInfoResponse> getTournamentForUserInit(ProxyUser user) {
        try {
            List<ClientTournamentInfoResponse> listTournament = new ArrayList<>();
            for (TournamentModel tournament : tournamentHashMap.values()) {
                ClientTournamentInfoResponse response = new ClientTournamentInfoResponse(tournament);
                UserTournamentModel userTournamentModel = user.getTournament(tournament.getTournamentId());

                response.setActive(tournament.isActive());
                response.setType(tournament.getType());

                if (tournament.isActive() && (tournament.getEndDate() != -1))
                    response.setDate(((tournament.getEndDate() - System.currentTimeMillis())/1000)+30);
                else if (!tournament.isActive() && (tournament.getStartDate() != -1))
                    response.setDate(((tournament.getStartDate() - System.currentTimeMillis())/1000)+30);
                else response.setDate(-1);


                if (userTournamentModel!=null && (!tournament.isActive() || (tournament.getStartDate() > userTournamentModel.endDate))) {
                    userTournamentModel.state = UserTournamentState.FAILED;
                    userTournamentModel.remainingTryCount = 0;
                    userTournamentModel.lostGames = tournament.getFailPoint();
                    userTournamentModel.expired = true;
                    CacheController.getInstance().updateUserTournament(user.getId(),tournament.getTournamentId(),userTournamentModel);
                }
                response.setUserLevel(userTournamentModel);
                listTournament.add(response);

                try {
                    DBController dbController = DBController.getInstance();
                    UserTournamentStats dbStat = dbController.getTournamentProfile(user.getId(), tournament.getTournamentId());

                    if (dbStat == null) {
                        dbController.setTournamentProfile(user.getId(), tournament.getTournamentId());
                        long now = new Date().getTime();
                        UserTournamentStats stats = UserTournamentStats.create(0, 0, 0, 0, 1, 0, user.getId(), now, now);
                        CacheController.getInstance().updateUserTournamentStats(user.getId(), stats);

                    }
                } catch (Exception e) {

                }

            }
            return listTournament;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    } 

    public void setUserTournamentStats(ProxyUser user) {
        try {
            for (TournamentModel tournament : tournamentHashMap.values()) {
                DBController dbController = DBController.getInstance();
                UserTournamentStats dbStat = dbController.getTournamentProfile(user.getId(), tournament.getTournamentId());

                if (dbStat == null) {
                    dbController.setTournamentProfile(user.getId(), tournament.getTournamentId());
                    long now = new Date().getTime();
                    UserTournamentStats stats = UserTournamentStats.create(0, 0, 0, 0, 1, 0, user.getId(), now, now);
                    CacheController.getInstance().updateUserTournamentStats(user.getId(), stats);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private class TournamentActivityTask extends TimerTask{

        @Override
        public void run() {
            long current = System.currentTimeMillis();
            for (TournamentModel tournament : tournamentHashMap.values()) {
                boolean active = tournament.getStartDate() == -1 || (current >= tournament.getStartDate() && tournament.getEndDate() <= current);
                tournament.setActive(active);
            }

            setTournamentHashMap();
        }
    }
}
