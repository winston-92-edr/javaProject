package com.mynet.gameserver.processors;

import com.google.gson.Gson;
import com.mynet.bonusservice.model.type.BonusRuleTypes;
import com.mynet.gameserver.GameController;
import com.mynet.gameserver.builders.SendGiftResponseBuilder;
import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.gameserver.model.GiftModel;
import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.request.SendGiftRequest;
import com.mynet.gameserver.response.ReceiveGiftResponse;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.logs.BonusLog;
import com.mynet.shared.logs.RabbitMQLogController;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.response.ErrorResponse;
import com.mynet.shared.response.UserReceivedMoneyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;

public class SendGiftProcessor implements MessageProcessor {
    Logger logger = LoggerFactory.getLogger(SendGiftProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            GameController controller = GameController.getInstance();

            // bir session 'da 1000 'den fazla hediye gonderilemez
            // 3 sn icerisinde 2. bir hediye gonderilemez

            Gson gson = NetworkMessage.getGson();
            SendGiftRequest request = gson.fromJson(message.getData(), SendGiftRequest.class);

            String senderId = message.getId();
            GameUser sender = controller.getUser(senderId);
            String receiverId = request.getReceiverId();
            GameUser receiver = controller.getUser(receiverId);
            String giftId = request.getGiftId();

            if ((System.currentTimeMillis() - sender.getLastTimeGiftSent()) < 3000 || sender.getGiftCount() > 10) {
                controller.sendNetworkMessage(sender, GameCommands.ERROR, gson.toJson(new ErrorResponse(ErrorCode.GIFT_ERROR)));
                return;
            }

            sender.setGiftCount(sender.getGiftCount() + 1);
            sender.setLastTimeGiftSent(System.currentTimeMillis());

            if (receiver.getTableId() != -1 && sender.getTableId() != -1 && receiver.getTableId() == sender.getTableId()) {
                Table senderTable = controller.getTable(sender.getTableId());
                if (senderTable.getGamerSide(senderId) == -1) {
                    // oturmuyor gonderen, hediye gonderemez
                    controller.sendNetworkMessage(sender, GameCommands.ERROR, gson.toJson(new ErrorResponse(ErrorCode.GIFT_ERROR)));
                    return;
                }
            } else {
                // kosullar tutmuyor, masalari yok veya ayni masada degiller gonderemez
                controller.sendNetworkMessage(sender, GameCommands.ERROR, gson.toJson(new ErrorResponse(ErrorCode.GIFT_ERROR)));
                return;
            }


            if (sender.getUserModel().guest || receiver.getUserModel().guest) {
                //return;
            }

            GiftModel gift = controller.getGift(giftId);

            if (gift == null) {
                controller.sendNetworkMessage(sender, GameCommands.ERROR, gson.toJson(new ErrorResponse(ErrorCode.GIFT_ERROR)));
                return;
            }

            int price = 0;
            int giftType = gift.getType();

            price = gift.getPrice();

            if (price < 0) {
                controller.sendNetworkMessage(sender, GameCommands.ERROR, gson.toJson(new ErrorResponse(ErrorCode.GIFT_ERROR)));
                return;
            }

            if (price <= sender.getMoney()) {
                boolean success = sender.updateMoneyAndWriteLog("giftsend", sender.getGameId(), price, false, System.currentTimeMillis());
                if (!success) {
                    controller.sendNetworkMessage(sender, GameCommands.ERROR, gson.toJson(new ErrorResponse(ErrorCode.GIFT_ERROR)));
                    return;
                }
            } else {
                controller.sendNetworkMessage(sender, GameCommands.ERROR, gson.toJson(new ErrorResponse(ErrorCode.GIFT_ERROR)));
                return;
            }

            DBController db = DBController.getInstance();

            if (giftType == 0) {

                db.addGift(senderId, receiverId, giftId);
                receiver.getGifts().add(giftId);

            } else if (giftType == 1) {
                db.updateProfileGift(receiverId, giftId);
                receiver.getGifts().add(giftId);
            }

            receiver.setUserGift(giftId);

            db.updateGiftCounter(sender.getPlatform(), giftId);

            SendGiftResponseBuilder builder = new SendGiftResponseBuilder();
            ReceiveGiftResponse response = builder.setGiftId(giftId)
                    .setGiftType(giftType)
                    .setPermanent(gift.isPermanent())
                    .setReceiverId(receiverId)
                    .setSenderId(senderId)
                    .setSenderName(sender.getName())
                    .setSenderMoney(sender.getMoney())
                    .createSendGiftResponse();

            if (receiver.getTableId() != -1) {
                Enumeration<GameUser> tableUsers = controller.getTable(String.valueOf(receiver.getTableId())).getAllUsers();
                while (tableUsers.hasMoreElements()) {
                    GameUser user = tableUsers.nextElement();
                    controller.sendNetworkMessage(user, GameCommands.RECEIVE_GIFT, gson.toJson(response));
                }
            } else {
                controller.sendNetworkMessage(receiver, GameCommands.RECEIVE_GIFT, gson.toJson(response));
            }

            long senderMoney = sender.getMoney();

            controller.sendNetworkMessage(sender, GameCommands.USER_RECEIVED_MONEY, gson.toJson(new UserReceivedMoneyResponse(senderMoney)));

            RabbitMQLogController.getInstance().addBonusLog(new BonusLog(BonusRuleTypes.ML_AMOUNT, sender.getId(), sender.getUserModel().gamesTotal, sender.getMoney(), sender.getUserModel().tickets, sender.getUserModel().joinDate));

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
