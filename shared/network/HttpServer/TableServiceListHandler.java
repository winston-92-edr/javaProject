package com.mynet.shared.network.HttpServer;

import com.google.gson.Gson;
import com.mynet.gameserver.GameController;
import com.mynet.gameserver.okey.Table;
import com.mynet.shared.node.NodeData;
import com.mynet.tableservice.service.ServiceTableModel;
import com.mynet.tableservice.service.TableService;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TableServiceListHandler extends HttpServlet {

    private Gson gson;

    public TableServiceListHandler() {
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpStatus.OK_200);
        String strRoomId = req.getParameter("roomId");
        ArrayList<ServiceTableModel> tables = TableService.getInstance().getTables(Integer.parseInt(strRoomId));
        resp.getWriter().println(gson.toJson(tables));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpStatus.OK_200);
        List<NodeData> gameNodes = TableService.getInstance().getGameNodes();
        resp.getWriter().println(gson.toJson(gameNodes));
    }
}
