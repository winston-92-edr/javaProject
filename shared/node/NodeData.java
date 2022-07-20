package com.mynet.shared.node;

import com.mynet.shared.types.ServerType;

import java.beans.ConstructorProperties;

public class NodeData {
    private final int id;
    private final String type;
    private final String host;
    private final int port;
    private final boolean isActive;
    private int httpPort = -1;
    private String groupId;
    private ServerType serverType;

    @ConstructorProperties({"id", "type", "host", "port", "is_active"})
    private NodeData(int id, String type, String host, int port, boolean isActive) {
        this.id = id;
        this.type = type;
        this.host = host;
        this.port = port;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    @Override
    public String toString() {
        return "NodeData{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", httpPort=" + httpPort +
                ", isActive=" + isActive +
                '}';
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        NodeData node = (NodeData) obj;
        return node.id == id;
    }

    public static class Builder{
        private int id;
        private String type;
        private String host;
        private String groupId;
        private int port;
        private boolean isActive;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setActive(boolean active) {
            isActive = active;
            return this;
        }

        public Builder setGroupId(String group_id) {
            this.groupId = group_id;
            return this;
        }

        public NodeData build(){
            return new NodeData(this.id, this.type, this.host, this.port, this.isActive);
        }
    }
}