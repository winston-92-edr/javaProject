package com.mynet.shared.model;

public class NewUserStepDetails {
    private long award;
    private long step;
    private boolean status;
    private boolean isVisible;
    private int awardLevel;

    public long getAward() {
        return award;
    }

    public void setAward(long award) {
        this.award = award;
    }

    public long getStep() {
        return step;
    }

    public void setStep(long step) {
        this.step = step;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isStatus() {
        return status;
    }

    public int getAwardLevel() {
        return awardLevel;
    }

    public void setAwardLevel(int awardLevel) {
        this.awardLevel = awardLevel;
    }
}
