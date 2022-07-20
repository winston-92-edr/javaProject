package com.mynet.chatserver;

import com.mynet.chatserver.models.ChatUser;
import com.mynet.chatserver.request.ChatEnterTableRequest;
import com.mynet.gameserver.response.TableChatResponse;
import com.mynet.proxyserver.network.StringUtil;
import com.mynet.proxyserver.user.UserModel;
import com.mynet.questservice.quests.category.QuestCategory;
import com.mynet.questservice.quests.category.QuestCategoryInfo;
import com.mynet.questservice.quests.category.SocialCategoryInfo;
import com.mynet.questservice.quests.types.SocialActionType;
import com.mynet.shared.connection.NodeToProxy;
import com.mynet.shared.logs.ChatThreadLog;
import com.mynet.shared.logs.RabbitMQLogController;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.utils.ProfanityFilter;
import com.mynet.shared.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.CannotProceedException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatController {
    Logger logger = LoggerFactory.getLogger(ChatController.class);
    private ConcurrentHashMap<String, ChatUser> users;
    private ConcurrentHashMap<Integer, Set<String>> groups;
    private NodeToProxy nodeToProxy;
    private int nodeId;
    private String groupId;
    private ProfanityFilter profanityFilter;

    private static ChatController instance;

    public static void init(int nodeId, String groupId) {
        if (instance == null) {
            instance = new ChatController(nodeId, groupId);
        }
    }

    public static ChatController getInstance() {
        return instance;
    }

    public ChatController(int nodeId, String groupId) {
        this.nodeId = nodeId;
        this.groupId = groupId;
        this.users = new ConcurrentHashMap<>();
        this.groups = new ConcurrentHashMap<>();
        this.nodeToProxy = new NodeToProxy();
        this.profanityFilter = new ProfanityFilter();

        addRedisEventListeners();
    }

    public ChatUser getUser(String id) {
        return users.get(id);
    }

    public void createUser(String id) throws CannotProceedException {

        if (hasUser(id)) {
            removeUser(id);
        }

        //UserModel userModel = DBController.getInstance().getUser(id);
        UserModel model = CacheController.getInstance().getUserGameModel(id);

        ChatUser user = new ChatUser(id);

        user.setName(model.name);
        user.setProxyId(model.proxyID);
        user.setRoomId(model.roomID);
        user.setTableId(model.tableID);
        String ip = CacheController.getInstance().getUserLastIp(id);

        //user.setMuteDate(userModel.muteDate);

        user.setIp(ip);

        addUser(user);

        if (user.getTableId() == -1) {
            removeUserGroups(id);
        } else {
            addUserGroup(user.getId(), user.getTableId(), user.getRoomId(), user.isAudience());
        }

    }

    public ProfanityFilter getProfanityFilter() {
        return profanityFilter;
    }

    public void fixUser(String userId) {
        if (!users.containsKey(userId)) {
            try {
                createUser(userId);
            } catch (CannotProceedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public boolean addUser(ChatUser user) {

        String userId = user.getId();

        if (users.containsKey(userId)) {
            return false;
        }

        users.put(userId, user);

        return true;
    }

    public void removeUser(String id) {
        ChatUser user = users.get(id);

        if (user != null) {
            removeUserGroups(id);

            users.remove(id);
        }
    }

    boolean hasUser(String id) {
        return users.containsKey(id);
    }

    public void addUserGroup(ChatUser user, ChatEnterTableRequest chatEnterTableRequest) {
        String userId = user.getId();

        removeUserGroups(userId);

        int newTableId = chatEnterTableRequest.getTableId();

        if (newTableId != -1) {
            groups.putIfAbsent(newTableId, new HashSet<>());

            Set<String> group = groups.get(newTableId);
            group.add(userId);
            user.setTableId(newTableId);
            user.setRoomId(chatEnterTableRequest.getRoomId());
            user.setAudience(chatEnterTableRequest.isAudience());
        }
    }

    public void removeUserGroups(String id) {
        ChatUser user = getUser(id);

        int cachedTableId = CacheController.getInstance().getTableId(user.getId());
        if (cachedTableId != -1) removeUserGroup(user, cachedTableId);

        if (user.getTableId() != 1) removeUserGroup(user, user.getTableId());

        user.setTableId(-1);
        user.setRoomId(-1);
        user.setAudience(false);
    }

    public void addUserGroup(String userId, int tableId, int roomId, boolean audience) {
        removeUserGroups(userId);

        ChatUser user = getUser(userId);

        if (user != null && tableId != -1) {
            groups.putIfAbsent(tableId, new HashSet<>());

            Set<String> group = groups.get(tableId);
            group.add(userId);
            user.setTableId(tableId);
            user.setRoomId(roomId);
            user.setAudience(audience);

        }
    }

    public void removeUserGroup(ChatUser user, int tableId) {
        Set<String> group = groups.get(tableId);

        if (group != null) {

            boolean success = group.remove(user.getId());

            if (group.isEmpty()) {
                groups.remove(tableId);
            }
        }
    }

    public void sendTableChatMessage(String userId, String message) {
        try {
            ChatUser user = users.get(userId);

            if (user.getMuteDate() == null) {
                String userMuteDate = DBController.getInstance().getMuteDate(userId);
                if(userMuteDate == null){
                    user.setMuteDate("");
                }else{
                    user.setMuteDate(userMuteDate);
                }
            }

            int tableId = user.getTableId();

            if(user.getMuteDate() != ""){
                Date muteDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(user.getMuteDate());
                boolean muted = System.currentTimeMillis() < muteDate.getTime();
                if (muted) {
                    ChatUser mutedUser = users.get(userId);
                    String mutedMessage = String.format("%s tarihine kadar susturma cezası aldınız!", user.getMuteDate());
                    TableChatResponse errorResponse = new TableChatResponse(userId, mutedMessage, user.getName(), mutedUser.isAudience());
                    sendNetworkMessage(mutedUser, GameCommands.TABLE_CHAT, NetworkMessage.getGson().toJson(errorResponse));
                    return;
                }
            }



            if (tableId == -1) {
                logger.info(String.format("TABLE ID is -1 WHILE TRYING SEND CHAT MESSAGE FOR USER: %s | REFRESHING...", userId));
                tableId = CacheController.getInstance().getTableId(userId);
                if (tableId == -1) {
                    logger.info(String.format("TABLE ID STILL -1 WHILE TRYING SEND CHAT MESSAGE FOR USER: %s", userId));
                    return;
                } else {
                    user.setTableId(tableId);
                    addUserGroup(user.getId(), user.getTableId(), user.getRoomId(), user.isAudience());
                }
            }

            String filterText = profanityFilter.filterText(message);

            Set<String> group = groups.get(tableId);

            if (group != null) {
                for (String id : group) {

                    ChatUser tableUser = users.get(id);

                    if (tableUser != null) {

                        boolean isAudience = user.isAudience();

                        TableChatResponse response;

                        if(tableUser.isProfanityFilter()){
                            response = new TableChatResponse(userId, filterText, user.getName(), isAudience);
                        }else {
                            response = new TableChatResponse(userId, message, user.getName(), isAudience);
                        }

                        sendNetworkMessage(tableUser, GameCommands.TABLE_CHAT, NetworkMessage.getGson().toJson(response));

                    }
                }

                String ex = "ODA: " + user.getRoomId();
                if (user.getRoomId() == 14) {
                    ex = "MLY: ";
                }

                String chatSource = ex + " - MASA: " + tableId + " ";
                String chatLine = getChatLine(userId, message, chatSource, user.getIp());
                logger.info("CHAT LOG:" + chatLine);

                RabbitMQLogController.getInstance().addChatLog(new ChatThreadLog(String.valueOf(nodeId), chatLine));

                QuestCategoryInfo questCategoryInfo = new SocialCategoryInfo(user.getId(), SocialActionType.SEND_TABLE_MESSAGE.getValue());
                RabbitMQLogController.getInstance().addUserQuestLog(QuestCategory.SOCIAL, questCategoryInfo);

            }


        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void sendNetworkMessage(ChatUser user, GameCommands command, String data) {
        NetworkMessage networkMessage = new NetworkMessage(command);
        networkMessage.setData(data);
        nodeToProxy.addServerMessage(networkMessage, user);
    }

    private String getChatLine(String fuid, String msg, String source, String ip) {
        Date date = new Date();
        SimpleDateFormat ft2 = new SimpleDateFormat("hh:mm:ss");
        String newDate2 = ft2.format(date);

        msg = StringUtil.normalTurkish(msg);

        return newDate2 + " # " + fuid + ": " + msg + " # " + source + ":" + ip + "\n";
    }

    private void addRedisEventListeners() {

        CacheController cacheController = CacheController.getInstance();

        cacheController.listenMuteUpdateEvents((charSequence, userId) -> {
            try {
                ChatUser user = getUser((String) userId);
                if (user == null) return;

                Long muted = DBController.getInstance().getUserMuted(user.getId());

                if (muted != null) {
                    user.setMuteDate(Utils.dateConversion(muted));
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });

        cacheController.listenSetChatUsers((charSequence, setChatUsersModel) -> {

            if (setChatUsersModel.getGroupId().equals(groupId)) {

                for (Object object : setChatUsersModel.getUsers()) {
                    ChatUser user = (ChatUser) object;

                    if (addUser(user)) {
                        if (user.getTableId() != -1) {
                            addUserGroup(user.getId(), user.getTableId(), user.getRoomId(), user.isAudience());
                        }
                    }
                }
            }
        });

    }

}
