package com.jatora.tfg_the_climb_within;

public class Player {
    private String name;
    private int maxhp;
    private int hp;
    private int unlocked_cards;
    private int tower_coins;
    private EmotionCoins emotionCoins;
    private int deck;
    private Stats stats;

    static class EmotionCoins {
        private int anger;
        private int disgust;
        private int fear;
        private int happiness;
        private int sadness;
        private int surprise;

        public int getAnger() {
            return anger;
        }

        public void setAnger(int anger) {
            this.anger = anger;
        }

        public int getDisgust() {
            return disgust;
        }

        public void setDisgust(int disgust) {
            this.disgust = disgust;
        }

        public int getFear() {
            return fear;
        }

        public void setFear(int fear) {
            this.fear = fear;
        }

        public int getHappiness() {
            return happiness;
        }

        public void setHappiness(int happiness) {
            this.happiness = happiness;
        }

        public int getSadness() {
            return sadness;
        }

        public void setSadness(int sadness) {
            this.sadness = sadness;
        }

        public int getSurprise() {
            return surprise;
        }

        public void setSurprise(int surprise) {
            this.surprise = surprise;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getUnlocked_cards() {
        return unlocked_cards;
    }

    public void setUnlocked_cards(int unlocked_cards) {
        this.unlocked_cards = unlocked_cards;
    }

    public int getTower_coins() {
        return tower_coins;
    }

    public void setTower_coins(int tower_coins) {
        this.tower_coins = tower_coins;
    }

    public EmotionCoins getEmotionCoins() {
        return emotionCoins;
    }

    public void setEmotionCoins(EmotionCoins emotionCoins) {
        this.emotionCoins = emotionCoins;
    }

    public int getDeck() {
        return deck;
    }

    public void setDeck(int deck) {
        this.deck = deck;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }
}
