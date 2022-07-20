package com.mynet.socialserver.processors;

import com.mynet.shared.model.NewUserStepDetails;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.user.ProxyUser;
import com.mynet.socialserver.SocialController;

public class GetNewUserStepAwardProcessor implements MessageProcessor {

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        SocialController socialController = SocialController.getInstance();
        ProxyUser user = socialController.getUser(message.getId());

        NewUserStepDetails stepDetails = user.userStepCheck();

     //   NetworkMessage response = new NetworkMessage(GameCommands.GET_NEW_USER_STEP_DETAILS);

      //  response.setDataAsJSON(stepDetails);
      //  response.setSuccess(true);

      //  socialController.getNodeToProxy().addServerMessage(response, user);
    }
}
