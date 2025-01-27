package com.jatora.tfg_the_climb_within;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

public class HealthBarView extends View {
    private Paint paint;
    private int maxHP = 1;
    private int HP = 1;
    private int healthBarColor = Color.GREEN; // Default color
    private int backgroundBarColor = Color.GRAY; // Default color

    public HealthBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public HealthBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.HealthBarView);
            healthBarColor = typedArray.getColor(R.styleable.HealthBarView_healthBarColor, healthBarColor);
            backgroundBarColor = typedArray.getColor(R.styleable.HealthBarView_backgroundBarColor, backgroundBarColor);
            typedArray.recycle(); // Always recycle TypedArray
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // Calculate bar width based on health
        float barWidth = (getWidth() * HP) / maxHP;

        // Draw health bar
        paint.setColor(healthBarColor);
        canvas.drawRect(0, 0, barWidth, getHeight(), paint);

        // Draw background for remaining health
        paint.setColor(backgroundBarColor);
        canvas.drawRect(barWidth, 0, getWidth(), getHeight(), paint);
    }

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int newHP) {
        HP = Math.max(0, Math.min(newHP, maxHP)); // Clamp between 0 and max
        invalidate(); // Redraw the view
    }


    public void setHealthBarColor(int color) {
        healthBarColor = color;
        invalidate(); // Redraw the view
    }

    public void setBackgroundBarColor(int color) {
        backgroundBarColor = color;
        invalidate(); // Redraw the view
    }
}
