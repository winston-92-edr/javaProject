package com.mynet.shared.network.HttpServer;

import com.google.gson.Gson;
import com.mynet.gameserver.GameController;
import com.mynet.gameserver.okey.GameSide;
import com.mynet.gameserver.okey.Table;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.launchers.GameServerLauncher;
import com.mynet.tableservice.service.ServiceTableModel;
import org.eclipse.jetty.http.HttpStatus;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class GameTableListHandler extends HttpServlet {

    private Gson gson;

    public GameTableListHandler() {
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpStatus.OK_200);
        String strRoomId = req.getParameter("roomId");
        Enumeration<Table> tables =  GameController.getInstance().getRoom(Integer.parseInt(strRoomId)).getTables();
        List<ServiceTableModel> serviceTables = new ArrayList<>();

        while(tables.hasMoreElements()){
            Table table = tables.nextElement();

            ServiceTableModel model = new ServiceTableModel();
            model.setTableId(table.getTableId());
            model.setRoomId(table.getRoomId());
            model.setGameServerId(GameServerLauncher.currentNode.getId());
            model.setBet(table.getBet());
            model.setMinBet(table.getMinBet());
            model.setPotValue(table.getPotValue());
            model.setVip(table.getIsVip());
            model.setSideCount(table.getSideCount());
            model.setDealerSide(table.getDealerSide());
            model.setPaired(table.getIsPartner());
            model.setTableType(table.getTableType());

            for (int side = 0; side < table.getSideCount(); side++) {
                GameSide gameSide = table.getGameSide(side);
                if(gameSide == null){
                    model.setSide(side, null);
                    continue;
                }

                GameUser user = gameSide.getUser();
                if(user == null){
                    model.setSide(side, null);
                }else{
                    long userId = Long.parseLong(user.getfuid());
                    if(userId < 1000){
                        model.setSide(side, true);
                    }else{
                        model.setSide(side, user.getBasicUser());
                    }
                }
            }

            serviceTables.add(model);
        }
        resp.getWriter().println(gson.toJson(serviceTables));
    }
}
