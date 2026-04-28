package com.example.streak;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class LineChartView extends View {

    private Paint linePaint;
    private Paint pointPaint;
    private Paint textPaint;
    private Paint gridPaint;
    private List<DataPoint> data = new ArrayList<>();
    private float maxVal = 10;

    public LineChartView(Context context) {
        this(context, null);
    }

    public LineChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.parseColor("#1976D2")); // Darker blue
        linePaint.setStrokeWidth(8f);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(Color.parseColor("#64B5F6")); // Lighter blue
        pointPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.GRAY);
        textPaint.setTextSize(24f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.LTGRAY);
        gridPaint.setStrokeWidth(2f);
    }

    public void setData(List<DataPoint> newData) {
        this.data = newData;
        maxVal = 1;
        for (DataPoint d : data) {
            if (d.value > maxVal) maxVal = d.value;
        }
        maxVal = ((float)Math.ceil(maxVal / 10.0) * 10);
        if (maxVal == 0) maxVal = 10;
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
        float paddingLeft = 60f;
        float paddingRight = 40f;
        float chartWidth = width - paddingLeft - paddingRight;
        float chartHeight = height - paddingTop - paddingBottom;
        
        float spacing = chartWidth / (data.size() > 1 ? data.size() - 1 : 1);

        // Draw grid
        for (int i = 0; i <= 4; i++) {
            float y = paddingTop + chartHeight - (chartHeight * i / 4);
            canvas.drawLine(paddingLeft, y, width - paddingRight, y, gridPaint);
            canvas.drawText(String.valueOf((int)(maxVal * i / 4)), paddingLeft - 30f, y + 10f, textPaint);
        }

        Path path = new Path();
        for (int i = 0; i < data.size(); i++) {
            float x = paddingLeft + i * spacing;
            float y = paddingTop + chartHeight - (data.get(i).value / maxVal) * chartHeight;
            
            if (i == 0) path.moveTo(x, y);
            else path.lineTo(x, y);
        }
        canvas.drawPath(path, linePaint);

        for (int i = 0; i < data.size(); i++) {
            float x = paddingLeft + i * spacing;
            float y = paddingTop + chartHeight - (data.get(i).value / maxVal) * chartHeight;
            canvas.drawCircle(x, y, 8f, pointPaint);
            canvas.drawText(data.get(i).label, x, height - 10f, textPaint);
        }
    }

    public static class DataPoint {
        String label;
        float value;
        public DataPoint(String label, float value) {
            this.label = label;
            this.value = value;
        }
    }
}
