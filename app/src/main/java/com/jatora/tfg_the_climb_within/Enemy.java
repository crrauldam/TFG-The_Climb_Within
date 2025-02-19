package com.jatora.tfg_the_climb_within;

import android.util.Log;

import androidx.annotation.NonNull;

public class Enemy extends Entity implements Cloneable {
    private int id;
//    private String name;
    private int atk;
//    private int hp;
//    private int maxhp;
    private String img;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    /**
     * Returns a deep copy of this object (modifications to the copy will not affect the original).
     */
    @NonNull
    @Override
    public Enemy clone() {
        final String TAG = "Enemy-clone";

        try {
            return (Enemy) super.clone();
        } catch (CloneNotSupportedException e) {
            Log.e(TAG, "Error cloning enemy: "+this.id);
            Log.e(TAG, "ERROR: "+e.getMessage());
            throw new RuntimeException(e);
        }

    }
}
