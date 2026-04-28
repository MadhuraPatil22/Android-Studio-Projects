package com.example.streak;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ProgressCircleView extends View {

    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint textPaint;
    private float percentage = 0f;
    private int color = Color.BLUE;
    private String label = "";

    public ProgressCircleView(Context context) {
        this(context, null);
    }

    public ProgressCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(15f);
        backgroundPaint.setColor(Color.parseColor("#F5F5F5"));

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(15f);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(28f);
        textPaint.setFakeBoldText(true);
    }

    public void setProgress(float percentage, int color, String label) {
        this.percentage = percentage;
        this.color = color;
        this.label = label;
        progressPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();
        float centerX = width / 2;
        float centerY = height / 2 - 20; // Up slightly to make room for label
        float radius = Math.min(width, height) / 2 - 40;

        RectF oval = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        // Draw background
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint);

        // Draw progress
        canvas.drawArc(oval, -90f, (percentage * 360f) / 100f, false, progressPaint);

        // Draw percentage text
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(24f);
        canvas.drawText((int)percentage + "%", centerX, centerY + 10, textPaint);

        // Draw label text
        textPaint.setTextSize(22f);
        textPaint.setColor(Color.GRAY);
        if (label.length() > 10) {
            canvas.drawText(label.substring(0, 8) + "..", centerX, height - 10, textPaint);
        } else {
            canvas.drawText(label, centerX, height - 10, textPaint);
        }
    }
}
