package com.jatora.tfg_the_climb_within;

public class Tower {
    private int id;
    private String name;
    private String img;
//    @SerializedName("enemies")
    private Enemies enemies;
    private Bosses bosses;
    private float multiplier;
    private String dialogues;

    static class Enemies {
        private int[] floor1;
        private int[] floor2;
        private int[] floor3;

        public int[] getFloor(int searchedFloor) {
            int[] floor;

            switch (searchedFloor) {
                case 1:
                    floor = floor1;
                    break;
                case 2:
                    floor = floor2;
                    break;
                case 3:
                    floor = floor3;
                    break;
                default:
                    floor = null;
            }

            return floor;
        }
    }

    static class Bosses {
        private int floor1;
        private int floor2;
        private int floor3;

        public int getFloor(int searchedFloor) {
            int floor;

            switch (searchedFloor) {
                case 1:
                    floor = floor1;
                    break;
                case 2:
                    floor = floor2;
                    break;
                case 3:
                    floor = floor3;
                    break;
                default:
                    floor = 0;
            }

            return floor;
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

    public float getMultiplier() {
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
