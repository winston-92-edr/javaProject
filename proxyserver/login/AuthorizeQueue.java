package com.mynet.proxyserver.login;

import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.proxyserver.model.RemovedUserModel;
import com.mynet.proxyserver.user.UserModel;
import com.mynet.shared.launchers.ProxyServerLauncher;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.response.ErrorResponse;
import com.mynet.shared.types.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.node.Node;
import com.mynet.shared.node.NodeController;
import com.mynet.shared.types.ConnectionCloseType;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class AuthorizeQueue extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizeQueue.class);

    private final NodeController nodeController;
    private BlockingQueue<AuthorizeTask> queue;
    private static final int QUEUE_SIZE = 5000;
    private volatile boolean isRunning = false;

    public AuthorizeQueue(NodeController nodeController) {
        this.nodeController = nodeController;
        queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        isRunning = true;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                AuthorizeTask task = queue.take();
                processTask(task);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

    }

    private void sendLoginError(AuthorizeTask task) {
        logger.info("login error");
        NetworkMessage message = new NetworkMessage(GameCommands.ERROR);
        message.setDataAsJSON(new ErrorResponse(ErrorCode.INVALID_LOGIN));
        task.getChannel().writeAndFlush(message, task.getChannel().voidPromise());
    }

    private void processTask(AuthorizeTask task) {

        try {
            UserModel user = task.call();
            if (user != null) {
                if (user.banned) {
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                    Date banDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(user.banDate);

                    NetworkMessage message = new NetworkMessage(GameCommands.ERROR);
                    message.setDataAsJSON(new ErrorResponse(dateFormat.format(banDate) + " tarihine kadar girişiniz engellenmiştir.", ErrorCode.INVALID_LOGIN));
                    task.getChannel().writeAndFlush(message, task.getChannel().voidPromise());

                } else {
                    loginUser(task, user, task.getRequest().getAppVersion(), task.getRequest().getServerType());
                }
            } else {
                sendLoginError(task);

            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            sendLoginError(task);
        }
    }

    public void add(AuthorizeTask task) {
        boolean success = queue.offer(task);
        if (!success) {
            task.getChannel().close();
        }
    }

    private void loginUser(AuthorizeTask task, UserModel userModel, String applicationVersion, ServerType serverType) {
        UserController userController = UserController.getInstance();

        CacheController cacheController = CacheController.getInstance();
        UserModel model = cacheController.getUserGameModel(userModel.fuid);

        boolean reconnect = model != null && model.tableID != -1;

        ProxyUser oldUser = userController.getUser(userModel.fuid);

        logger.info("HERE COMES A NEW CHALLENGER!!! >> " + userModel.fuid);
        if(serverType == null){
            serverType = ServerType.GENERIC;
        }

        if (oldUser != null) {
            handleOldLogin(task, userModel, applicationVersion, userController, oldUser, reconnect, serverType);
        } else {
            handleNewLogin(task, userModel, applicationVersion, userController, reconnect, serverType);
        }

    }

    private void handleOldLogin(AuthorizeTask task, UserModel userModel, String applicationVersion, UserController userController, ProxyUser oldUser, boolean reconnect, ServerType serverType) {
        logger.info("Old player founded :  " + oldUser.getId());
        oldUser.setUserModel(userModel);
        oldUser.setProxyId(ProxyServerLauncher.currentNode.getId());
        userController.closeConnection(oldUser, ConnectionCloseType.ANOTHER_CLIENT);
        if (oldUser.getChannel() != null) {
            logger.info("Old player: " + oldUser.getId() + " closing old channel...");
            //TODO: send information message
            oldUser.getChannel().close();
        }
        oldUser.setChannel(task.getChannel());
        oldUser.setLastConnectionLostTime(0);
        task.getHandler().setUser(oldUser);
        oldUser.setUserLoginTime(System.currentTimeMillis());
        applicationVersion = applicationVersion != null ? applicationVersion : "";
        oldUser.setApplicationVersion(applicationVersion);
        oldUser.setPlatform(task.getPlatform());

        // remove player from old handler..
        if (oldUser.getUserConnectionHandler() != null) {
            oldUser.getUserConnectionHandler().setUser(null);
        }
        // set player's new handler
        oldUser.setUserConnectionHandler(task.getHandler());

        if(task.getRequest().getFriendsCount() != 0){
            oldUser.setFriendsCount(task.getRequest().getFriendsCount());
        }else{
            oldUser.setFriendsCount(0);
        }

        if(reconnect) {
            Node oldGameNode = oldUser.getGameNode();
            oldUser.setServerType(oldGameNode.getServerType());
        }else{
            oldUser.setGameNode(null);
            oldUser.setServerType(serverType);
        }

        nodeController.setUserNodes(oldUser);

        userController.sendAuthorize(oldUser, reconnect);

        if (reconnect) {
            // send request to node
            NetworkMessage sendUserState = new NetworkMessage(GameCommands.SEND_USER_STATE);
            sendUserState.setData(nodeController.getCurrentNode().getId() + "");
            oldUser.getGameNode().sendRequest(sendUserState, oldUser);
        }
    }

    private void handleNewLogin(AuthorizeTask task, UserModel userModel, String applicationVersion, UserController userController, boolean reconnect, ServerType serverType) {
        ProxyUser user = createNewUser(task, userModel, applicationVersion, userController);

        Node gameNode = nodeController.getNode(userModel.gameID);

        if(task.getRequest().getFriendsCount() != 0){
            user.setFriendsCount(task.getRequest().getFriendsCount());
        }else{
            user.setFriendsCount(0);
        }

        NetworkMessage sendUserState = null;

        if (reconnect) {
            //Set user's game node to old game node
            user.setGameId(userModel.gameID);
            user.setGameNode(gameNode);

            user.setServerType(gameNode.getServerType());

            sendUserState = new NetworkMessage(GameCommands.SEND_USER_STATE);
            sendUserState.setData(nodeController.getCurrentNode().getId() + "");
        }else {
            user.setServerType(serverType);
        }

        nodeController.setUserNodes(user);

        userController.sendAuthorize(user, reconnect);

        if(sendUserState != null){
            user.getGameNode().sendRequest(sendUserState, user);
        }

        RemovedUserModel removedUser = new RemovedUserModel(user.getId(), nodeController.getCurrentNode().getId());
        CacheController.getInstance().publishProxyRemoveUser(removedUser);
        CacheController.getInstance().publishRemoveUser(user.getId() + ";" + nodeController.getCurrentNode().getId());
    }

    private ProxyUser createNewUser(AuthorizeTask task, UserModel userModel, String applicationVersion, UserController userController) {
        ProxyUser user = new ProxyUser(userModel);
        logger.info("NEW player :  " + user.getId());

        int currentNodeId = ProxyServerLauncher.currentNode.getId();

        user.setChannel(task.getChannel());
        user.setLastConnectionLostTime(0);
        task.getHandler().setUser(user);
        user.setUserConnectionHandler(task.getHandler());
        user.setProxyId(currentNodeId);
        user.setPlatform(task.getPlatform());

        applicationVersion = applicationVersion != null ? applicationVersion : "";
        user.setApplicationVersion(applicationVersion);
        String analyticsDeviceId = task.getRequest().getAnalyticsDevId() != null ? task.getRequest().getAnalyticsDevId() : "";
        user.setAnalyticsDeviceId(analyticsDeviceId);
        String analyticsSessionId = task.getRequest().getAnalyticsSessionId() != null ? task.getRequest().getAnalyticsSessionId() : "";
        user.setAnalyticsSessionId(analyticsSessionId);

        userController.addUser(user);

        return user;
    }
}
