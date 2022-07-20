package com.mynet.shared.node;

import com.google.gson.Gson;
import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.observer.Observer;
import com.mynet.proxyserver.user.UserModel;
import com.mynet.shared.db.generated.enums.NodesType;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.network.ServerToServerMessageProcessor;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.response.*;
import com.mynet.shared.types.ServerType;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.utils.Utils;
import com.mynet.socialserver.request.AddUserSocialRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NodeController implements ServerToServerMessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(NodeController.class);
    private ConcurrentHashMap<Integer, Node> nodes;
    private AtomicLong lastGenericUpdateTime = new AtomicLong(-1);
    private AtomicLong lastTournamentUpdateTime = new AtomicLong(-1);
    private AtomicInteger socialIndex = new AtomicInteger(0);
    private Node availableGenericGameNode;
    private Node availableTournamentGameNode;
    List<Observer> observerList = new ArrayList<>();
    private NodeData currentNode;
    private Gson gson;

    public NodeController(NodeData currentNode) {
        this.nodes = new ConcurrentHashMap<>();
        this.currentNode = currentNode;
        this.gson = new Gson();
        startReconnectTask();
    }

    private void startReconnectTask() {
        Timer timer = new Timer(true);
        TimerTask task = new ReconnectTask();
        timer.scheduleAtFixedRate(task, 15000, 1000);
    }

    public NodeData getCurrentNode() {
        return currentNode;
    }

    public void addNode(Node node) {
        node.connect();
        this.nodes.put(node.getId(), node);
        logger.warn(String.format("NODE ADDED>> id: %d, type: %s, host: %s, port: %d", node.getId(), node.getType(), node.getHost(), node.getPort()));
    }

    public void removeNode(Node node) {
        node.disconnect();
        this.nodes.remove(node.getId());

        logger.warn(String.format("NODE REMOVED>> id: %d, type: %s, host: %s, port: %d", node.getId(), node.getType(), node.getHost(), node.getPort()));
    }

    public Node getNode(int id) {
        return this.nodes.get(id);
    }

    public Collection<Node> getNodes() {
        return this.nodes.values();
    }

    public Node getAvailableGameNode(ServerType serverType) {

        if(serverType.equals(ServerType.GENERIC)) {
            if (availableGenericGameNode == null || getLastGenericUpdateTime() + 60000 < System.currentTimeMillis()) {

                long totalUserCount = 0;
                ArrayList<Node> gameNodes = new ArrayList<>();

                for (Node n : nodes.values()) {
                    if (n.getType().equals(NodesType.game.name()) && n.getServerType().equals(ServerType.GENERIC)) {
                        int nodeId = n.getId();
                        long userCount = CacheController.getInstance().getGameNodeUserCount(nodeId);
                        totalUserCount += userCount;
                        gameNodes.add(n);
                    }
                }

                if (gameNodes.size() == 0) availableGenericGameNode = null;
                else if (gameNodes.size() == 1) availableGenericGameNode = gameNodes.get(0);
                else {
                    gameNodes.sort(Comparator.comparing(Node::getId));
                    int nodeIndex = (int) (totalUserCount) % gameNodes.size();
                    availableGenericGameNode = gameNodes.get(nodeIndex);
                }

                refreshLastGenericUpdateTime();
            }

            return availableGenericGameNode;
        }else{
            if (availableTournamentGameNode == null || getLastTournamentUpdateTime() + 60000 < System.currentTimeMillis()) {

                long totalUserCount = 0;
                ArrayList<Node> gameNodes = new ArrayList<>();

                for (Node n : nodes.values()) {
                    if (n.getType().equals(NodesType.game.name()) && n.getServerType().equals(ServerType.TOURNAMENT)) {
                        int nodeId = n.getId();
                        long userCount = CacheController.getInstance().getGameNodeUserCount(nodeId);
                        totalUserCount += userCount;
                        gameNodes.add(n);
                    }
                }

                if (gameNodes.size() == 0) availableTournamentGameNode = null;
                else if (gameNodes.size() == 1) availableTournamentGameNode = gameNodes.get(0);
                else {
                    gameNodes.sort(Comparator.comparing(Node::getId));
                    int nodeIndex = (int) (totalUserCount) % gameNodes.size();
                    availableTournamentGameNode = gameNodes.get(nodeIndex);
                }

                refreshLastTournamentUpdateTime();
            }

            return availableTournamentGameNode;
        }
    }

    public boolean isGameNodeInThisGroup(int gameNodeId) {
        boolean isGameNodeInThisGroup = false;

        for (Node n : nodes.values()) {
            if (n.getType().equals(NodesType.game.name()) && n.getId() == gameNodeId) {
                isGameNodeInThisGroup = true;
                break;
            }
        }

        return isGameNodeInThisGroup;
    }

    public long getLastGenericUpdateTime() {
        return lastGenericUpdateTime.get();
    }

    public void refreshLastGenericUpdateTime() {
        this.lastGenericUpdateTime.set(System.currentTimeMillis());
    }

    public long getLastTournamentUpdateTime() { return lastTournamentUpdateTime.get(); }

    public void refreshLastTournamentUpdateTime() {
        this.lastTournamentUpdateTime.set(System.currentTimeMillis());
    }

    public void addUser(ProxyUser user, NetworkMessage request) {
        Node node = user.getGameNode();
        if (node != null) {
            node.sendRequest(request, user);
        }
    }



    public void reloadNodeMappingProxy() {
        ArrayList<NodeData> gameNodes = CacheController.getInstance().getGameNodes(currentNode.getGroupId());

        addToNodeList(gameNodes);

        ArrayList<NodeData> socialNodes = CacheController.getInstance().getSocialNodes(currentNode.getGroupId());

        addToNodeList(socialNodes);

        NodeData matchNodeData = CacheController.getInstance().getMatchNode(currentNode.getGroupId());

        if (matchNodeData != null) {
            Node matchNode = new Node(matchNodeData.getId(), matchNodeData.getHost(), matchNodeData.getPort(), UserController.getInstance(), matchNodeData.getType(), currentNode, this, matchNodeData.getGroupId(), matchNodeData.getServerType());
            addNode(matchNode);
        }

        NodeData questNodeData = CacheController.getInstance().getQuestNode(currentNode.getGroupId());

        if (questNodeData != null) {
            Node questNode = new Node(questNodeData.getId(), questNodeData.getHost(), questNodeData.getPort(), UserController.getInstance(), questNodeData.getType(), currentNode, this, questNodeData.getGroupId(), questNodeData.getServerType());
            addNode(questNode);
        }

        NodeData bonusNodeData = CacheController.getInstance().getBonusNode(currentNode.getGroupId());

        if (bonusNodeData != null) {
            Node bonusNode = new Node(bonusNodeData.getId(), bonusNodeData.getHost(), bonusNodeData.getPort(), UserController.getInstance(), bonusNodeData.getType(), currentNode, this, bonusNodeData.getGroupId(), bonusNodeData.getServerType());
            addNode(bonusNode);
        }

        NodeData chatNodeData = CacheController.getInstance().getChatNode(currentNode.getGroupId());

        if (chatNodeData != null) {
            Node chatNode = new Node(chatNodeData.getId(), chatNodeData.getHost(), chatNodeData.getPort(), UserController.getInstance(), chatNodeData.getType(), currentNode, this, chatNodeData.getGroupId(), chatNodeData.getServerType());
            addNode(chatNode);
        }

        reloadNodeMapping();
    }

    private void addToNodeList(ArrayList<NodeData> nodeList){
        for (NodeData nodeData : nodeList) {
            Node n = nodes.get(nodeData.getId());
            if (n == null) {
                Node node = new Node(nodeData.getId(), nodeData.getHost(), nodeData.getPort(), UserController.getInstance(), nodeData.getType(), currentNode, this, nodeData.getGroupId(), nodeData.getServerType());
                addNode(node);
            }
        }
    }

    public void reloadNodeMapping() {

        NodeData tableNodeData = CacheController.getInstance().getTableNode(currentNode.getGroupId());

        if (tableNodeData != null) {
            Node tableNode = new Node(tableNodeData.getId(), tableNodeData.getHost(), tableNodeData.getPort(), UserController.getInstance(), tableNodeData.getType(), currentNode, this, tableNodeData.getGroupId(), tableNodeData.getServerType());
            addNode(tableNode);
        }
    }

    @Override
    public void processMessage(NetworkMessage message) throws InvalidServerMessage {

    }

    public Node getSocialNode(ServerType serverType) {

        List<Node> socialNodes = this.nodes.values().stream().filter(node -> node.getType().equals(NodesType.social.name()) && node.getServerType().equals(serverType)).collect(Collectors.toList());

        if(socialNodes.size() == 0) {
            return null;
        }

        if(serverType.equals(ServerType.GENERIC)) {
            return socialNodes.get(0);
        }

        int index = socialIndex.getAndIncrement();

        if(index >= socialNodes.size()) {
            index = 0;
            socialIndex.set(index);
        }

        return socialNodes.get(index);
    }

    public void removeUserFromNodes(ProxyUser user) {
        for (Node n : nodes.values()) {
            n.removeUser(user);
        }
    }

    public Node getTableNode() {
       return getNode(NodesType.table);
    }

    public Node getNode(NodesType type){
        Optional<Node> founded = nodes.values().stream().filter(node -> node.getType().equals(type.name())).findFirst();
        return founded.orElse(null);
    }

    public void addNode(NodeData nodeData) {
        Node node = new Node(nodeData.getId(), nodeData.getHost(), nodeData.getPort(), UserController.getInstance(), nodeData.getType(), currentNode, this, nodeData.getGroupId(), nodeData.getServerType());
        addNode(node);
    }

    public void removeNode(int nodeId) {
        Node node = nodes.get(nodeId);
        if (node != null) {
            removeNode(node);
        }
    }

    public void addRedisEventListeners() {

        CacheController cache = CacheController.getInstance();
        UserController userController = UserController.getInstance();

        cache.listenBanUpdateEvents((charSequence, userId) -> {
            try {
                ProxyUser user = userController.getUser((String) userId);

                if(user != null) {
                    UserModel model = DBController.getInstance().getUser(user.getId());
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                    Date banDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(model.banDate);

                    NetworkMessage message = new NetworkMessage(GameCommands.ERROR);
                    message.setDataAsJSON(new ErrorResponse(dateFormat.format(banDate) + " tarihine kadar girişiniz engellenmiştir.", ErrorCode.BANNED));
                    user.send(message);

                    Utils.setTimeout(()-> userController.removeUser((String) userId), 8000);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });

        cache.listenVipUpdateEvents((charSequence, id) -> {
            try {
                String userId = (String) id;
                ProxyUser user = userController.getUser(userId);
                if (user == null) return;

                int status = DBController.getInstance().getUserVipStatus(userId);
                if (status == -1) return;

                user.setVip(status);

                user.updateVip(true);
                NetworkMessage message = new NetworkMessage(GameCommands.VIP_STATUS_UPDATED);
                message.setDataAsJSON(new UpdateVipStatusResponse(status == 1));
                user.send(message);

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
        cache.listenTicketUpdateEvents((charSequence, id) -> {
            try {
                String userId = (String) id;
                ProxyUser user = userController.getUser(userId);
                if (user == null) return;

                int tickets = DBController.getInstance().getUserTickets(userId);
                user.updateTicket(tickets);

                UpdateTicketResponse dataResponse = new UpdateTicketResponse(user.getTickets(), UpdateTicketResponse.UpdateTicketReason.PURCHASE);

                NetworkMessage message = new NetworkMessage(GameCommands.USER_TICKET_UPDATED);
                message.setDataAsJSON(dataResponse);
                user.send(message);

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
        cache.listenMoneyUpdateEvents((charSequence, id) -> {
            try {
                String userId = (String) id;
                ProxyUser user = userController.getUser(userId);
                if (user == null) return;

                long money = DBController.getInstance().getUserMoney(userId);
                user.updateMoney(money);

                NetworkMessage message = new NetworkMessage(GameCommands.USER_RECEIVED_MONEY);
                message.setDataAsJSON(new UserReceivedMoneyResponse(money));
                user.send(message);

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });

        cache.listenGameNodeAddEvent(new NodeAddEventListener(this));
        cache.listenTableNodeAddEvent(new NodeAddEventListener(this));
        cache.listenSocialNodeAddEvent(new NodeAddEventListener(this));
        cache.listenMatchNodeAddEvent(new NodeAddEventListener(this));
        cache.listenQuestNodeAddEvent(new NodeAddEventListener(this));
        cache.listenBonusNodeAddEvent(new NodeAddEventListener(this));
        cache.listenChatNodeAddEvent(new NodeAddEventListener(this));

        cache.listenTableNodeDeleteEvent(new NodeDeleteEventListener(this));
        cache.listenSocialNodeDeleteEvent(new NodeDeleteEventListener(this));
        cache.listenMatchNodeDeleteEvent(new NodeDeleteEventListener(this));
        cache.listenGameNodeDeleteEvent(new NodeDeleteEventListener(this));
        cache.listenQuestNodeDeleteEvent(new NodeDeleteEventListener(this));
        cache.listenBonusNodeDeleteEvent(new NodeDeleteEventListener(this));
        cache.listenChatNodeDeleteEvent(new NodeDeleteEventListener(this));

        cache.listenProxyRemoveUser((channel, msg) -> {
            UserController controller = UserController.getInstance();

            if (getCurrentNode().getId() == msg.getProxyId()) return;

            ProxyUser user = controller.getUser(msg.getId());
            controller.removeUserFromNode(user);
        });

        cache.listenOldServerRemoveUser((channel, msg) -> {
            String[] removedUserModel = msg.split(";");
            String userId = removedUserModel[0];
            UserController.getInstance().removeUser(userId);
        });

    }


    public void setUserNodes(ProxyUser user) {

        Node socialNode = getSocialNode(user.getServerType());
        AddUserSocialRequest request = new AddUserSocialRequest(user.getFriendsCount());
        if (socialNode != null) {
            socialNode.addUser(user, NetworkMessage.getGson().toJson(request));
            user.setSocialNode(socialNode);
        }

        boolean isTournament = user.getServerType().equals(ServerType.TOURNAMENT);

        Node matchNode = getNode(NodesType.match);
        if (matchNode != null) {
            if (isTournament) {
                matchNode.addUser(user);
            } else {
                matchNode.removeUser(user);
            }
        }

        Node chatNode = getNode(NodesType.chat);
        if (chatNode != null) {
            if (!isTournament) {
                chatNode.addUser(user);
            } else {
                chatNode.removeUser(user);
            }
        }

        Node availableGameNode = getAvailableGameNode(user.getServerType());
        if (availableGameNode != null && user.getGameNode() == null) {
            availableGameNode.addUser(user);
            user.setGameNode(availableGameNode);
            logger.info("GAME NODE ID PORT:" + availableGameNode.getPort());
        }

        Node tableNode = getTableNode();
        if (tableNode != null && isTournament) {
            tableNode.removeUser(user);
        }

        Node questNode = getNode(NodesType.quest);
        if (questNode != null) {
            questNode.addUser(user);
        }

        Node bonusNode = getNode(NodesType.bonus);
        if (bonusNode != null) {
            logger.info("USER ADDED TO BONUS NODE: " + user.getId());
            bonusNode.addUser(user);
        }else{
            logger.info("BONUS NODE is NULL FOR: " + user.getId());
        }

    }

    public void notifyDisconnect(ProxyUser user){
        Node socialNode = UserController.getInstance().getNodeController().getSocialNode(user.getServerType());
        socialNode.sendRequest(new NetworkMessage(GameCommands.SOCKET_DISCONNECT), user);
    }

    class ReconnectTask extends TimerTask {

        @Override
        public void run() {
            nodes.forEach((id, node) -> {
                if (!node.isConnected()) {
                    System.out.println("Node " + id + " not connected! Trying to reconnect");
                    node.reconnect();
                }
            });
        }
    }
}
