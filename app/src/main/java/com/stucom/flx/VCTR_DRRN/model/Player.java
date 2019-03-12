package com.stucom.flx.VCTR_DRRN.model;

import java.util.Arrays;

public class Player {
    private Integer id;
    private String name;
    private String image;
    private String from;
    private String totalScore;
    private String lastLevel;
    private String lastScore;
    private Score[] scores;

    public Player() {
    }

    public void getImageAPI(){
        if (getImage() == "" || getImage()== null){
            setImage("https://api.flx.cat/imgs/unknown.png");
        }
    }
    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getImage() { return image; }
    public String getFrom() { return from; }
    public String getTotalScore() { return totalScore; }
    public String getLastLevel() { return lastLevel; }
    public String getLastScore() { return lastScore; }
    public Score[] getScores() { return scores; }

    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setImage(String image) { this.image = image; }
    public void setFrom(String from) { this.from = from; }
    public void setTotalScore(String totalScore) { this.totalScore = totalScore;}
    public void setLastLevel(String lastLevel) { this.lastLevel = lastLevel; }
    public void setLastScore(String lastScore) { this.lastScore = lastScore; }
    public void setScores(Score[] scores) { this.scores = scores; }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", from='" + from + '\'' +
                ", totalScore='" + totalScore + '\'' +
                ", lastLevel='" + lastLevel + '\'' +
                ", lastScore='" + lastScore + '\'' +
                ", scores=" + Arrays.toString(scores) +
                '}';
    }
}
