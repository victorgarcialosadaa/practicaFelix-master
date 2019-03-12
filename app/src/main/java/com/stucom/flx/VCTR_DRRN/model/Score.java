package com.stucom.flx.VCTR_DRRN.model;


public class Score {

    private Integer level;
    private Integer score;
    private String playedAt;

    public Score() {
    }

    public Integer getLevel() { return level; }
    public Integer getScore() { return score; }
    public String getPlayedAt() { return playedAt; }

    public void setLevel(Integer level) { this.level= level; }
    public void setScore(Integer score) { this.score = score; }
    public void setPlayedAt(String playedAt) { this.playedAt = playedAt; }
}

