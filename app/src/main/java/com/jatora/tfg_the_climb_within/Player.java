package com.jatora.tfg_the_climb_within;

import java.util.Arrays;

public class Player extends Entity {
//    private String name;
//    private int maxhp;
//    private int hp;
    private Integer[] unlocked_cards;
    private int tower_coins;
    private int[] unlocked_towers;
    private EmotionCoins emotion_coins;
    private Integer[] deck;
    private Stats stats;

    public Player() {
        this.unlocked_cards = new Integer[0];
        this.unlocked_towers = new int[0];
        this.deck = new Integer[0];
    }

    static class EmotionCoins {
        private int anger;
        private int disgust;
        private int fear;
        private int happiness;
        private int sadness;
        private int surprise;

        public int getCoin(String name) {
            int coins = -1;

            switch (name) {
                case "anger":
                    coins = anger;
                    break;
                case "disgust":
                    coins = disgust;
                    break;
                case "fear":
                    coins = fear;
                    break;
                case "happiness":
                    coins = happiness;
                    break;
                case "sadness":
                    coins = sadness;
                    break;
                case "surprise":
                    coins = surprise;
                    break;
            }

            return coins;
        }

        public void setCoin(String name, int newCoins) {
            switch (name) {
                case "anger":
                    anger = newCoins;
                    break;
                case "disgust":
                    disgust = newCoins;
                    break;
                case "fear":
                    fear = newCoins;
                    break;
                case "happiness":
                    happiness = newCoins;
                    break;
                case "sadness":
                    sadness = newCoins;
                    break;
                case "surprise":
                    surprise = newCoins;
                    break;
            }
        }
    }

    static class Stats {
        private int played_time;
        private int cards_played;
        private int damage_dealt;
        private int damage_received;

        public int getPlayed_time() {
            return played_time;
        }

        public void setPlayed_time(int played_time) {
            this.played_time = played_time;
        }

        public int getCards_played() {
            return cards_played;
        }

        public void setCards_played(int cards_played) {
            this.cards_played = cards_played;
        }

        public int getDamage_dealt() {
            return damage_dealt;
        }

        public void setDamage_dealt(int damage_dealt) {
            this.damage_dealt = damage_dealt;
        }

        public int getDamage_received() {
            return damage_received;
        }

        public void setDamage_received(int damage_received) {
            this.damage_received = damage_received;
        }
    }

//    public String getName() {
//        return name;
//    }

//    public void setName(String name) {
//        this.name = name;
//    }

//    public int getMaxhp() {
//        return maxhp;
//    }

//    public void setMaxhp(int maxhp) {
//        this.maxhp = maxhp;
//    }

//    public int getHp() {
//        return hp;
//    }

//    public void setHp(int hp) {
//        this.hp = hp;
//    }

    public Integer[] getUnlocked_cards() {
        return unlocked_cards;
    }

    public void setUnlocked_cards(Integer[] unlocked_cards) {
        this.unlocked_cards = unlocked_cards;
    }

    public int getTower_coins() {
        return tower_coins;
    }

    public void setTower_coins(int tower_coins) {
        this.tower_coins = tower_coins;
    }

    public int[] getUnlocked_towers() {
        return unlocked_towers;
    }

    public void setUnlocked_towers(int[] unlocked_towers) {
        this.unlocked_towers = unlocked_towers;
    }

    public EmotionCoins getEmotion_coins() {
        return emotion_coins;
    }

    public void setEmotion_coins(EmotionCoins emotion_coins) {
        this.emotion_coins = emotion_coins;
    }

    public Integer[] getDeck() {
        return deck;
    }

    public void setDeck(Integer[] deck) {
        this.deck = deck;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    @Override
    public String toString() {
        return "Player{" +
//                "name='" + name + '\'' +
//                ", maxhp=" + maxhp +
//                ", hp=" + hp +
                ", unlocked_cards=" + Arrays.toString(unlocked_cards) +
                ", tower_coins=" + tower_coins +
                ", unlocked_towers=" + Arrays.toString(unlocked_towers) +
                ", emotion_coins=" + emotion_coins +
                ", deck=" + Arrays.toString(deck) +
                ", stats=" + stats +
                '}';
    }
}