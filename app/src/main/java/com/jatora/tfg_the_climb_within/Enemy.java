package com.jatora.tfg_the_climb_within;

public class Enemy {
    private int id;
    private String name;
    private int atk;
    private int hp;
    private int maxhp;
    private String img;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAtk() {
        return atk;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxhp() {
        return maxhp;
    }

    public String getImg() {
        return img;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
