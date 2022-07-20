package com.mynet.shared.node;

import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeAddEventListener implements MessageListener<NodeData> {
    private static Logger logger = LoggerFactory.getLogger(NodeAddEventListener.class);
    private NodeController nodeController;

    public NodeAddEventListener(NodeController nodeController) {
        this.nodeController = nodeController;
    }

    @Override
    public void onMessage(CharSequence charSequence, NodeData nodeData) {
        try {
            if(nodeData.getGroupId().equals(nodeController.getCurrentNode().getGroupId())) {
                nodeController.addNode(nodeData);
                logger.warn(String.format("NODE ADD EVENT>> id: %d, type: %s, host: %s, port: %d", nodeData.getId(), nodeData.getType(), nodeData.getHost(), nodeData.getPort()));
            }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
    }
}
