package com.mynet.shared.logs;

public class TournamentEventLog extends QueueElement{
    public enum Type{
        JOIN,
        MATCH,
        WIN,
        LOST,
        CLAIM,
        CONTINUE,
        SIT_TABLE
    }

    private long fuid;
    private long tid;
    private Type type;
    private String platform;
    private long start_time;
    private long end_time;
    private int level;
    private long game_id;
    private boolean success;
    private int losses;
    private long amount;
    private int cost;
    private int tournament_id;
    private int player_count;
    private int rem_try_count;


    public TournamentEventLog(long fuid, Type type) {
        this.fuid = fuid;
        this.type = type;
        this.start_time = System.currentTimeMillis();
    }

    public int getRem_try_count() {
        return rem_try_count;
    }

    public void setRem_try_count(int rem_try_count) {
        this.rem_try_count = rem_try_count;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public long getFuid() {
        return fuid;
    }

    public void setFuid(long fuid) {
        this.fuid = fuid;
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }


    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getGame_id() {
        return game_id;
    }

    public void setGame_id(long game_id) {
        this.game_id = game_id;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getTournament_id() {
        return tournament_id;
    }

    public void setTournament_id(int tournament_id) {
        this.tournament_id = tournament_id;
    }

    public int getPlayer_count() {
        return player_count;
    }

    public void setPlayer_count(int player_count) {
        this.player_count = player_count;
    }
}
