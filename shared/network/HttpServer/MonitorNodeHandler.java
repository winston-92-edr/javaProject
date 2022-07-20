package com.mynet.shared.network.HttpServer;

import com.google.gson.Gson;
import com.mynet.gameserver.GameController;
import com.mynet.gameserver.okey.Table;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MonitorNodeHandler extends HttpServlet {

    private Gson gson;

    public MonitorNodeHandler() {
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpStatus.OK_200);
        resp.getWriter().println("Node:" + GameController.getInstance().getNodeId());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String responseBody = MonitorUtility.getResponseBody(req);
            JSONObject request = new JSONObject(responseBody);
            Object tableId = request.get("tableId");

            Table table = GameController.getInstance().getTable(tableId.toString());
            resp.setStatus(HttpStatus.OK_200);

            if(table == null){
                resp.getWriter().println("Table NOT FOUND: " + tableId);
            }else{
                String json = gson.toJson(table.getTableCycleInfo());
                resp.getWriter().println(json);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
