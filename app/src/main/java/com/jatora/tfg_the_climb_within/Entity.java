package com.jatora.tfg_the_climb_within;

public abstract class Entity {
    protected String name;
    protected int maxhp;
    protected int hp;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getMaxhp() {
        return maxhp;
    }

    public void setMaxhp(int maxhp) {
        this.maxhp = maxhp;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int newHP) {
        this.hp = newHP;
        if (this.hp > this.maxhp) {
            this.hp = this.maxhp;
        } else if (this.hp < 0) {
            this.hp = 0;
        }
    }
}
