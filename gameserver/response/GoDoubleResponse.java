package com.mynet.gameserver.response;

public class GoDoubleResponse {
    private final int side;
    private final String id;

    public GoDoubleResponse(Builder builder) {
        this.side = builder.side;
        this.id = builder.userId;
    }

    public static class Builder{
        private int side;
        private String userId;

        public Builder setSide(int side){
            this.side = side;
            return this;
        }

        public Builder setUserId(String userId){
            this.userId = userId;
            return this;
        }

        public GoDoubleResponse build(){ return new GoDoubleResponse(this);}
    }

}
