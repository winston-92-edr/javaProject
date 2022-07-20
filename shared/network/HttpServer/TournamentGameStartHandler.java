package com.mynet.shared.network.HttpServer;

import com.google.gson.Gson;
import com.mynet.gameserver.GameController;
import com.mynet.gameserver.enums.TableType;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.model.BasicUserModel;
import com.mynet.shared.types.GamePlayStatusType;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.CannotProceedException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TournamentGameStartHandler extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger(TournamentGameStartHandler.class);

    private Gson gson;

    public TournamentGameStartHandler() {
        this.gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            String responseBody = MonitorUtility.getResponseBody(req);
            JSONObject request = new JSONObject(responseBody);
            Integer tableId = (Integer) request.get("tableId");
            Integer sideCount = (Integer) request.get("sideCount");
            JSONArray jsonArray = (JSONArray) request.get("users");

            if(tableId == null || sideCount == null || jsonArray == null){
                logger.error("Less parameters attempt create table | tableId:" + tableId + " sideCount:" + sideCount + " users:" + jsonArray);
                return;
            }

            BasicUserModel[] users = new BasicUserModel[sideCount];
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);

                BasicUserModel user = new BasicUserModel();
                user.setTournamentId(obj.getInt("tournamentId"));
                user.setId(obj.getString("id"));

                users[i] = user;

            }

            GameController gameController = GameController.getInstance();

            GamePlayStatusType statusType = gameController.createTable(users[0].getId(), tableId, GameController.TOURNAMENT_ROOM_ID, 0, sideCount, TableType.PRIVATE, true);
            boolean success = true;

            if(statusType.equals(GamePlayStatusType.VALID)){
                for (BasicUserModel user: users) {
                    GameUser gameUser = gameController.getUser(user.getId());
                    try {
                        if(gameUser == null){
                            gameUser = gameController.createUser(user.getId());
                        }

                        gameUser.setTournamentId(user.getTournamentId());
                    }catch (CannotProceedException e){
                        success = false;
                        break;
                    }
                }

                if(success){
                    resp.setStatus(HttpStatus.OK_200);
                    resp.getWriter().println("created!");
                }else{
                    resp.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500);
                }

            }else {
                resp.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }
}

