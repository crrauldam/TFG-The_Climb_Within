package com.jatora.tfg_the_climb_within;

import com.google.gson.annotations.SerializedName;

public class Tower {
    private int id;
    private String name;
    private String img;
//    @SerializedName("enemies")
    private Enemies enemies;
    private Bosses bosses;
    private double multiplier;
    private String dialogues;

    static class Enemies {
        private String[] floor1;
        private String[] floor2;
        private String[] floor3;

        public String[] getFloor1() {
            return floor1;
        }

        public String[] getFloor2() {
            return floor2;
        }

        public String[] getFloor3() {
            return floor3;
        }
    }

    static class Bosses {
        private String floor1;
        private String floor2;
        private String floor3;

        public String getFloor1() {
            return floor1;
        }

        public String getFloor2() {
            return floor2;
        }

        public String getFloor3() {
            return floor3;
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public Enemies getEnemies() {
        return enemies;
    }

    public Bosses getBosses() {
        return bosses;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public String getDialogues() {
        return dialogues;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setEnemies(Enemies enemies) {
        this.enemies = enemies;
    }

    public void setBosses(Bosses bosses) {
        this.bosses = bosses;
    }
}
