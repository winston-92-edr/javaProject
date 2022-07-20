package com.mynet.socialserver.processors;

import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.user.ProxyUser;
import com.mynet.socialserver.SocialController;
import com.mynet.socialserver.request.UserTournamentProfileRequest;
import com.mynet.socialserver.model.UserTournamentStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;

public class TournamentProfileDetailProcessor implements MessageProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TournamentProfileDetailProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            SocialController controller = SocialController.getInstance();

            ProxyUser user = controller.getUser(message.getId());

            if (user != null) {

                NetworkMessage response = new NetworkMessage(GameCommands.GET_TOURNAMENT_PROFILE_DETAILS);

                UserTournamentProfileRequest request = NetworkMessage.CreateMessage(message.getData(), UserTournamentProfileRequest.class);

                UserTournamentStats userTournamentStats = CacheController.getInstance().getUserTournamentStats(request.getFuid());

                if (userTournamentStats != null) {

                    if (userTournamentStats.total_games > 0) userTournamentStats.game_winning_percent = userTournamentStats.won_games * 100 / userTournamentStats.total_games;

                    response.setDataAsJSON(userTournamentStats);
                    response.setSuccess(true);
                    controller.getNodeToProxy().addServerMessage(response, user);
                } else {

                    ArrayList<UserTournamentStats> dbStats = DBController.getInstance().getTournamentProfile(request.getFuid());

                    long now = new Date().getTime();
                    userTournamentStats = UserTournamentStats.create(0, 0, 0, 0, 1, 0, request.getFuid(), now, now);

                    if (dbStats != null) {
                        for (UserTournamentStats stat : dbStats) {

                            /*fuid, tournament_id, max_level, total_tournament, won_tournament,total_game,won_game*/
                            userTournamentStats.total_tournament = userTournamentStats.total_tournament + stat.total_tournament;
                            userTournamentStats.won_tournament = userTournamentStats.won_tournament + stat.won_tournament;
                            userTournamentStats.total_games = userTournamentStats.total_games + stat.total_games;
                            userTournamentStats.won_games = userTournamentStats.won_games + stat.won_games;

                            if (stat.max_level > userTournamentStats.max_level) userTournamentStats.max_level = stat.max_level;

                        }

                        if (userTournamentStats.total_games > 0)
                            userTournamentStats.game_winning_percent = userTournamentStats.won_games * 100 / userTournamentStats.total_games;

                        CacheController.getInstance().updateUserTournamentStats(request.getFuid(), userTournamentStats);
                        response.setDataAsJSON(userTournamentStats);
                        response.setSuccess(true);
                        controller.getNodeToProxy().addServerMessage(response, user);

                    } else {
                        userTournamentStats.game_winning_percent = 0;
                        response.setDataAsJSON(userTournamentStats);
                        response.setSuccess(true);
                        controller.getNodeToProxy().addServerMessage(response, user);
                    }
                }

            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
