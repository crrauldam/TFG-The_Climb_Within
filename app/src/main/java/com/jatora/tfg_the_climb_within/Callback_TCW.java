package com.jatora.tfg_the_climb_within;

import java.io.Serializable;

// extends another interface (Serializable) for making it able to be passed between activities
public interface Callback_TCW extends Serializable {
    public void onSuccess();
    public void onFailure(String errorMessage);
}
