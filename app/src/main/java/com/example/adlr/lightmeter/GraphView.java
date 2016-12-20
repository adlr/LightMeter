package com.example.adlr.lightmeter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by adlr on 12/19/16.
 */

public class GraphView extends View {
    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawOval(0, 0, getWidth(), getHeight(), new Paint());
    }
}
