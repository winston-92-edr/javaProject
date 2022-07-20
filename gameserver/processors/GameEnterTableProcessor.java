package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.gameserver.enums.TableType;
import com.mynet.gameserver.model.TableFilterModel;
import com.mynet.gameserver.okey.Table;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.enums.PlayerSide;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.request.EnterTableRequest;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.response.ErrorResponse;
import com.mynet.shared.types.GamePlayStatusType;
import com.mynet.tableservice.enums.TablePairedFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameEnterTableProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(GameEnterTableProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            GameController controller = GameController.getInstance();
            GameUser user = controller.getUser(message.getId());

            if(user == null) return;

            EnterTableRequest request = NetworkMessage.getGson().fromJson(message.getData(),EnterTableRequest.class);

            String from = request.isInvite() ? "INVITE" : request.getSide().name();

            if (request.getTableId() == -1) {
                TableFilterModel filter = request.getFilter();

                if (filter == null) {
                    filter = new TableFilterModel();
                }

                int roomId;
                if (filter.getRoomId() == -1) {
                    int[] tableAndRoomId = controller.getAvailableRoom(user);
                    roomId = tableAndRoomId[1];
                } else {
                    roomId = filter.getRoomId();
                }

                int withPartner = filter.getPaired() == TablePairedFilter.PAIRED ? 1 : 0;

                int tableId = CacheController.getInstance().incAndGetTableCounter();
                GamePlayStatusType error = controller.createTable(user.getfuid(), tableId, roomId, withPartner, 4, TableType.PUBLIC, true);
                if (error == GamePlayStatusType.VALID) {
                    request.setTableId(tableId);
                } else {
                    controller.sendNetworkMessage(user,GameCommands.ERROR, NetworkMessage.getGson().toJson(new ErrorResponse(ErrorCode.NO_AVAILABLE_TABLE)));
                    return;
                }
            }

            Table table = controller.getTable(request.getTableId());

            if(table != null) {
                int userStatus = table.isAudienceOrGamer(user.getfuid());

                if (!request.getSide().equals(PlayerSide.JOIN_AUDIENCE)) {
                    if (user.getTableId() == -1 || userStatus == 2) {
                        NetworkMessage resp = new NetworkMessage(GameCommands.SIT_TABLE_REQUEST);
                        controller.sitTableWithAction(user, resp, table.getTableId(), table.getRoomId(), request.getSide(), false, from);
                    } else {
                        controller.sendNetworkMessage(user, GameCommands.ERROR, NetworkMessage.getGson().toJson(new ErrorResponse(ErrorCode.ALREADY_HAVE_TABLE)));
                        return;
                    }
                } else {
                    if (user.getTableId() != -1) {
                        controller.sendNetworkMessage(user, GameCommands.ERROR, NetworkMessage.getGson().toJson(new ErrorResponse(ErrorCode.ALREADY_HAVE_TABLE)));
                        return;
                    }
                    controller.joinAnAudience(user, String.valueOf(request.getTableId()), from);
                }
            }else{
                controller.sendNetworkMessage(user,GameCommands.ERROR, NetworkMessage.getGson().toJson(new ErrorResponse(ErrorCode.TABLE_NULL)));
                return;
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
