package com.example.streak;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class BarChartView extends View {

    private Paint barPaint;
    private Paint textPaint;
    private Paint gridPaint;
    private List<BarData> data = new ArrayList<>();
    private float barWidth = 40f;
    private float spacing = 60f;
    private int maxVal = 10;

    public BarChartView(Context context) {
        this(context, null);
    }

    public BarChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.DKGRAY);
        textPaint.setTextSize(30f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.LTGRAY);
        gridPaint.setStrokeWidth(2f);
    }

    public void setData(List<BarData> newData) {
        this.data = newData;
        maxVal = 1;
        for (BarData d : data) {
            if (d.value > maxVal) maxVal = (int) d.value;
        }
        // Round up to nearest nice number
        maxVal = ((maxVal + 4) / 5) * 5;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data.isEmpty()) return;

        float width = getWidth();
        float height = getHeight();
        float paddingBottom = 60f;
        float paddingTop = 40f;
        float paddingLeft = 40f;
        float chartHeight = height - paddingTop - paddingBottom;
        
        spacing = (width - paddingLeft * 2) / data.size();
        barWidth = spacing * 0.6f;

        // Draw horizontal grid lines
        for (int i = 0; i <= 5; i++) {
            float y = paddingTop + chartHeight - (chartHeight * i / 5);
            canvas.drawLine(paddingLeft, y, width - paddingLeft, y, gridPaint);
        }

        for (int i = 0; i < data.size(); i++) {
            BarData d = data.get(i);
            float barHeight = (d.value / maxVal) * chartHeight;
            float left = paddingLeft + i * spacing + (spacing - barWidth) / 2;
            float top = paddingTop + chartHeight - barHeight;
            float right = left + barWidth;
            float bottom = paddingTop + chartHeight;

            RectF rect = new RectF(left, top, right, bottom);
            
            // Modern Gradient fill
            Shader shader = new LinearGradient(left, top, left, bottom,
                    Color.parseColor("#64B5F6"), Color.parseColor("#1976D2"),
                    Shader.TileMode.CLAMP);
            barPaint.setShader(shader);
            
            canvas.drawRoundRect(rect, 15f, 15f, barPaint);

            // Draw label
            canvas.drawText(d.label, left + barWidth / 2, height - 10f, textPaint);
            
            // Draw value on top of bar
            if (d.value > 0) {
                canvas.drawText(String.valueOf((int)d.value), left + barWidth / 2, top - 10f, textPaint);
            }
        }
    }

    public static class BarData {
        String label;
        float value;

        public BarData(String label, float value) {
            this.label = label;
            this.value = value;
        }
    }
}
