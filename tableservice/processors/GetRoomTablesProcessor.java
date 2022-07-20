package com.mynet.tableservice.processors;

import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.gameserver.model.TableInfoModel;
import com.mynet.shared.GetRoomTablesRequest;
import com.mynet.shared.builders.ErrorResponseBuilder;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.response.ErrorResponse;
import com.mynet.tableservice.model.NotEnoughMoneyErrorModel;
import com.mynet.tableservice.service.TableService;

import java.util.ArrayList;

public class GetRoomTablesProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            TableService service = TableService.getInstance();

            GetRoomTablesRequest request = NetworkMessage.CreateMessage(message.getData(), GetRoomTablesRequest.class);
            ArrayList<TableInfoModel> tables = service.getRoomTables(request.getRoomId());

            ErrorResponse error = new ErrorResponseBuilder().setCode(ErrorCode.NOT_ENOUGH_MONEY_ROOM).setData(NetworkMessage.getGson().toJson(new NotEnoughMoneyErrorModel(tables))).createErrorResponse();

            NetworkMessage response = new NetworkMessage(GameCommands.ERROR);
            response.setDataAsJSON(error);

            service.sendNetworkMessage(request.getUserId(), request.getProxyId(),response);
        }catch (Exception e){

        }
    }
}
