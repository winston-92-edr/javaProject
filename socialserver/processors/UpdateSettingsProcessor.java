package com.mynet.socialserver.processors;

import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.types.SettingsTypes;
import com.mynet.shared.user.ProxyUser;
import com.mynet.socialserver.SocialController;
import com.mynet.socialserver.request.UpdateSettingsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateSettingsProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(UpdateSettingsProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            ProxyUser user = SocialController.getInstance().getUser(message.getId());

            if (user != null) {

                if ((System.currentTimeMillis() - user.getLastUserOptionUpdate()) < 5000) {
                    return;
                } else {
                    user.setLastUserOptionUpdate(0);
                }

                user.setLastUserOptionUpdate(System.currentTimeMillis());

                System.out.println("UPDATE SETTINGS MESSAGE:" + message.getData());

                UpdateSettingsRequest request = NetworkMessage.CreateMessage(message.getData(), UpdateSettingsRequest.class);
                SettingsTypes type = request.getSettingType();

                boolean status = request.isStatus();
                int value = status ? 0 : 1;

                if (type.equals(SettingsTypes.PRIVATE_CHAT)) {
                    DBController.getInstance().updateUserSettings(message.getId(), type, value);
                    user.getUserModel().privateChat = status;
                }else if(type.equals(SettingsTypes.GO_PROFILE)) {
                    DBController.getInstance().updateUserSettings(message.getId(), type, value);
                    user.getUserModel().goProfile = status;
                }

            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
