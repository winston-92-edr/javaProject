package com.mynet.shared.network.HttpServer;

import com.google.gson.Gson;
import com.mynet.gameserver.GameController;
import com.mynet.gameserver.okey.GameSide;
import com.mynet.gameserver.okey.Table;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.launchers.GameServerLauncher;
import com.mynet.shared.model.RoomCountModel;
import com.mynet.tableservice.service.ServiceTableModel;
import org.eclipse.jetty.http.HttpStatus;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class GameRoomCountsHandler extends HttpServlet {

    private Gson gson;

    public GameRoomCountsHandler() {
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpStatus.OK_200);

        Map<Integer, Integer> roomCounts = GameController.getInstance().getRoomCounts();
        RoomCountModel roomCountModel = new RoomCountModel(roomCounts);
        resp.getWriter().println(gson.toJson(roomCountModel));
    }
}
