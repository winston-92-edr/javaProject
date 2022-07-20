package com.mynet.tableservice.processors;

import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.gameserver.model.AvailableTableModel;
import com.mynet.gameserver.model.TableFilterModel;
import com.mynet.gameserver.room.Room;
import com.mynet.shared.builders.ErrorResponseBuilder;
import com.mynet.shared.enums.PlayerSide;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.request.EnterTableRequest;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.response.ErrorResponse;
import com.mynet.tableservice.service.ServiceProxyDataWrapper;
import com.mynet.tableservice.service.ServiceTableModel;
import com.mynet.tableservice.service.ServiceUser;
import com.mynet.tableservice.service.TableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnterTableProcessor implements MessageProcessor {
    private Logger logger = LoggerFactory.getLogger(EnterTableProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            TableService service = TableService.getInstance();
            ServiceProxyDataWrapper wrapper = NetworkMessage.getGson().fromJson(message.getData(), ServiceProxyDataWrapper.class);
            int proxyId = wrapper.getProxyId();

            EnterTableRequest request = NetworkMessage.getGson().fromJson(wrapper.getData(), EnterTableRequest.class);
            String friendId = request.getFriendId();
            Integer tableId = request.getTableId();

            if(request.getSide() == null){
                request.setSide(PlayerSide.QUICK_PLAY);
            }

            String userId = message.getId();
            int userTableId = CacheController.getInstance().getTableId(userId);

            if (!request.getSide().equals(PlayerSide.JOIN_AUDIENCE)) {
                if (userTableId != -1 && tableId != userTableId) {
                    NetworkMessage errorMessage = new NetworkMessage(GameCommands.ERROR);
                    errorMessage.setDataAsJSON(new ErrorResponse(ErrorCode.ALREADY_HAVE_TABLE));
                    service.sendNetworkMessage(userId, wrapper.getProxyId(), errorMessage);
                    return;
                }
            } else {
                if (userTableId != -1) {
                    NetworkMessage errorMessage = new NetworkMessage(GameCommands.ERROR);
                    errorMessage.setDataAsJSON(new ErrorResponse(ErrorCode.ALREADY_HAVE_TABLE));
                    service.sendNetworkMessage(userId, wrapper.getProxyId(), errorMessage);
                    return;
                }
            }

            //Go to friend and quickplay check
            if (!friendId.isEmpty()) {
                tableId = CacheController.getInstance().getTableId(friendId);
                request.setTableId(tableId);
            } else if (tableId == -1) {

                TableFilterModel filter = request.getFilter();

                if(filter == null){
                    filter = new TableFilterModel();
                    request.setFilter(filter);
                }

                if(filter.getRoomId() != -1){
                    ServiceUser user = service.getUser(userId);
                    Room room = service.getRoom(filter.getRoomId());

                    int moneyLimit = room.getMinBet();

                    boolean isLowBet = service.isLowBet(user.getUser().getMoney(), room.getBet());

                    if (user.getUser().getMoney() < moneyLimit) {

                        ErrorResponse error = new ErrorResponseBuilder().setCode(ErrorCode.NOT_ENOUGH_MONEY_QUICK_PLAY).createErrorResponse();
                        NetworkMessage errorMessage = new NetworkMessage(GameCommands.ERROR);
                        errorMessage.setDataAsJSON(error);
                        service.sendNetworkMessage(userId, proxyId, errorMessage);

                        return;

                    } else if(isLowBet) {

                        ErrorResponse error = new ErrorResponseBuilder().setCode(ErrorCode.LOW_BET_QUICK_PLAY).createErrorResponse();
                        NetworkMessage errorMessage = new NetworkMessage(GameCommands.ERROR);
                        errorMessage.setDataAsJSON(error);
                        service.sendNetworkMessage(userId, proxyId, errorMessage);

                        return;

                    }
                }

                AvailableTableModel table = service.getQuickTable(userId, filter);

                if (table == null) {
                    NetworkMessage response = new NetworkMessage(GameCommands.PROXY_ENTER_TABLE);
                    response.setDataAsJSON(request);
                    service.sendNetworkMessage(userId, proxyId, response);

                    return;
                }

                tableId = table.getTableId();
                request.setTableId(tableId);

            }

            if(tableId != -1) {

                ServiceTableModel table = service.getTable(tableId);
                if(table != null) {
                    if (table.getGameServerId() == -1) {
                        NetworkMessage response = new NetworkMessage(GameCommands.ERROR);
                        response.setDataAsJSON(new ErrorResponse(ErrorCode.TABLE_NULL));

                        service.sendNetworkMessage(userId, proxyId, response);
                        return;
                    }

                    request.setGameNodeId(table.getGameServerId());
                    request.setRoomId(table.getRoomId());

                    NetworkMessage response = new NetworkMessage(GameCommands.PROXY_ENTER_TABLE);
                    response.setDataAsJSON(request);

                    service.sendNetworkMessage(userId, proxyId, response);
                }else{
                    NetworkMessage response = new NetworkMessage(GameCommands.ERROR);
                    response.setDataAsJSON(new ErrorResponse(ErrorCode.TABLE_NULL));

                    service.sendNetworkMessage(userId, proxyId, response);

                }
            }else{
                NetworkMessage response = new NetworkMessage(GameCommands.ERROR);
                response.setDataAsJSON(new ErrorResponse(ErrorCode.TABLE_NULL));

                service.sendNetworkMessage(userId, proxyId, response);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
