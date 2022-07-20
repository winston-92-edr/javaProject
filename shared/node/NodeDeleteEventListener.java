package com.mynet.shared.node;

import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeDeleteEventListener implements MessageListener<Integer> {
    private static Logger logger = LoggerFactory.getLogger(NodeDeleteEventListener.class);
    private NodeController nodeController;

    public NodeDeleteEventListener(NodeController nodeController) {
        this.nodeController = nodeController;
    }

    @Override
    public void onMessage(CharSequence charSequence, Integer nodeId) {
        logger.warn(String.format("NODE REMOVE EVENT: %d", nodeId));
        this.nodeController.removeNode(nodeId);
    }
}
