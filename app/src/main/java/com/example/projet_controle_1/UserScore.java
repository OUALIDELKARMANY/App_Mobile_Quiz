package com.example.projet_controle_1;

public class UserScore {
    private String name;
    private int score;

    public UserScore(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}
