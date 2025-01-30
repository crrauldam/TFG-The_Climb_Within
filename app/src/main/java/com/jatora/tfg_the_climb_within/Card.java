package com.jatora.tfg_the_climb_within;

public class Card {
    private int id;
    private String name;
    private String description;
    private int effect;
    private String type;
    private String icon;
    private boolean unlockable;
    private int unlock_cost;
    private int in_game_cost;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getEffect() {
        return effect;
    }

    public String getType() {
        return type;
    }

    public String getIcon() {
        return icon;
    }

    public boolean isUnlockable() {
        return unlockable;
    }

    public int getUnlock_cost() {
        return unlock_cost;
    }

    public int getIn_game_cost() {
        return in_game_cost;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEffect(int effect) {
        this.effect = effect;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUnlockable(boolean unlockable) {
        this.unlockable = unlockable;
    }

    public void setUnlock_cost(int unlock_cost) {
        this.unlock_cost = unlock_cost;
    }

    public void setIn_game_cost(int in_game_cost) {
        this.in_game_cost = in_game_cost;
    }
}
