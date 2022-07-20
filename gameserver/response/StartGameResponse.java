package com.mynet.gameserver.response;

import com.mynet.gameserver.model.TableUserShortModel;
import java.util.List;

public class StartGameResponse {
    private List<TableUserShortModel> usersMoneyAndExperience;
    private int nextTurn;
    private List<String> sideHand;
    private int remainingCardsCount;
    private String indicator;
    private long potValue;
    private int dealer;
    private int time;

    public StartGameResponse(Builder builder) {
        this.usersMoneyAndExperience = builder.usersMoneyAndExperience;
        this.nextTurn = builder.nextTurn;
        this.sideHand = builder.sideHand;
        this.remainingCardsCount = builder.remainingCardsCount;
        this.indicator = builder.indicator;
        this.potValue = builder.potValue;
        this.dealer = builder.dealer;
        this.time = builder.time;
    }

    public static class Builder {
        private List<TableUserShortModel> usersMoneyAndExperience;
        private int nextTurn;
        private List<String> sideHand;
        private int remainingCardsCount;
        private String indicator;
        private long potValue;
        private int dealer;
        private int time;

        public Builder setUsersMoneyAndExperience(List<TableUserShortModel> usersMoneyAndExperience){
            this.usersMoneyAndExperience = usersMoneyAndExperience;
            return this;
        }

        public Builder setNextTurn(int nextTurn){
            this.nextTurn = nextTurn;
            return this;
        }

        public Builder setSideHand(List<String> sideHand){
            this.sideHand = sideHand;
            return this;
        }

        public Builder setRemainingCardsCount(int reminedCardsCount){
            this.remainingCardsCount = reminedCardsCount;
            return this;
        }

        public Builder setIndicator(String indicator){
            this.indicator = indicator;
            return this;
        }

        public Builder setPotValue(long potValue){
            this.potValue = potValue;
            return this;
        }

        public Builder setDealer(int dealer) {
            this.dealer = dealer;
            return this;
        }

        public Builder setTime(int time) {
            this.time = time;
            return this;
        }

        public StartGameResponse build(){
            return new StartGameResponse(this);
        }
    }
}
