package com.mynet.gameserver;

import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.room.Room;
import com.mynet.proxyserver.user.UserModel;
import com.mynet.shared.connection.ProxyToGame;
import com.mynet.shared.connection.ProxyToSocial;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.node.Node;
import com.mynet.shared.request.FriendRequestRequest;
import com.mynet.shared.request.GoToFriendRequest;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.response.FriendRequestResponse;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;
import com.mynet.socialserver.SocialController;

import javax.naming.CannotProceedException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Random;

public class Tester {
    private final int nodeId;
    private HashSet<String> sits;
    private String[] users;
    private String requester;

    public Tester(int nodeId) {
        this.nodeId = nodeId;
        this.users = new String[]{
                "100008567119380",
                "1010372655"
//                "99900000000000279",
//                "99900000000000280",
//                "100003093152535",
//                "100008843015634",
//                "10152110468073990",
//                "111013717275760",
//                "114724850126007"
        };
        this.requester = "579627811";
        this.sits = new HashSet<>();
    }

//    public void sendSitRequest(){
//        UserController userController = UserController.getInstance();
//
//        for (String userId : users) {
//          try {
//              NetworkMessage message = new NetworkMessage(GameCommands.SIT_TABLE_2);
//              UserModel userModel = DBController.getInstance().getUser(userId);
//              ProxyUser user = new ProxyUser(userModel);
//              Node gameNode = userController.getAvailableGameNode();
//              gameNode.addUser(user);
//              userController.addUser(user);
//
//              user.setGameNode(gameNode);
//              user.setGameId(gameNode.getId());
//
//              user.setProxyId(nodeId);
//              message.setData(getAvailableSit());
//              userController.processRequest(user, message);
//          }catch (CannotProceedException e){
//
//          }
//        }
//    }

    private String getAvailableSit(){
        Random random = new Random();
        int side;
        String sitData;

        int size = sits.size();

        do {
            side = random.nextInt(4);
            sitData = side + ";" + "2207";
            sits.add(sitData);
        }while (size == sits.size());

        return sitData;
    }

    public String getRequester() {
        return requester;
    }

    public void sendGift(){
        NetworkMessage message = new NetworkMessage(GameCommands.SEND_GIFT);
        message.setId(requester);
        message.setData("100008567119380;g116");

        try {
            //pg.processMessage(message);
        }catch (Exception e) {

        }
    }

    public void sendFriendRequest(ProxyToSocial ps){ ;
        NetworkMessage message = new NetworkMessage(GameCommands.FRIEND_REQUEST);
        message.setId(requester);
        message.setDataAsJSON(new FriendRequestRequest("100008567119380"));

        try {
            ps.processMessage(message);
        }catch (Exception e) {

        }
    }

    public void sendGoToFriendRequest(ProxyToSocial ps){
        try {
            UserController userController = UserController.getInstance();

            UserModel userModel = DBController.getInstance().getUser("100008567119380");
            ProxyUser user = new ProxyUser(userModel);
            SocialController.getInstance().addUser(user);

            UserModel userModel1 = DBController.getInstance().getUser(requester);
            ProxyUser user1 = new ProxyUser(userModel1);
            SocialController.getInstance().addUser(user1);

            NetworkMessage message = new NetworkMessage(GameCommands.GO_TO_FRIEND);
            message.setData("100008567119380");
            message.setId(requester);
            ps.processMessage(message);
        }catch (Exception e){

        }


    }
}
