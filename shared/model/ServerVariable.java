package com.mynet.shared.model;

import java.beans.ConstructorProperties;

public class ServerVariable {
    private String name;
    private String value;

    @ConstructorProperties({"NAME","VALUE"})
    public ServerVariable(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
