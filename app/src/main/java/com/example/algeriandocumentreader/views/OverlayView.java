package com.example.algeriandocumentreader.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class OverlayView extends View {

    private Paint paint;
    private Paint textPaint;
    private Rect rect;
    private String text;  // Store the text to display

    // Correct constructor with only Context and AttributeSet
    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();  // Initialize the paint object
    }

    // Initialization of the Paint objects
    private void init() {
        // Paint for the rectangle
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);

        // Paint for the text
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(48);
        textPaint.setStyle(Paint.Style.FILL);
    }

    // Method to set the rectangle and text
    public void setRect(Rect rect, String text) {
        this.rect = rect;
        this.text = text;  // Store the text to be displayed
        invalidate();  // Redraw the view when rect or text is set
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the rectangle if it's set
        if (rect != null) {
            // Draw the rectangle
            canvas.drawRect(rect, paint);

            // Draw the text above the rectangle
            if (text != null && !text.isEmpty()) {
                // Adjust the Y position to ensure the text is above the rectangle
                float textYPosition = rect.top - 10;
                if (textYPosition < 0) {
                    textYPosition = rect.top + 50;  // If too high, place below the rect
                }
                canvas.drawText(text, rect.left, textYPosition, textPaint);
            }
        }
    }

}
