package com.mynet.shared.network.HttpServer;
import com.mynet.chatserver.ChatController;
import com.mynet.shared.utils.ProfanityFilter;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class BadWordHandler extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String responseBody = MonitorUtility.getResponseBody(req);
            JSONObject request = new JSONObject(responseBody);
            String badWord = (String) request.get("badWord");

            byte[] bytes = ("," + badWord).getBytes();

            if (badWord != null) {
                Files.write(Paths.get("swear.txt"), bytes, StandardOpenOption.APPEND);
            }

            ChatController.getInstance().getProfanityFilter().loadConfigs();

            resp.setStatus(HttpStatus.OK_200);
            resp.getWriter().println("created!");

        } catch (Exception e) {
        }
    }

}
