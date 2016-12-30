package com.example.adlr.lightmeter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by adlr on 12/19/16.
 */

public class GraphView extends View {
    private static final String TAG = "GraphView";
    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private float[] mVals;
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float xmin = 0.0f;
        float xmax = 2399.0f;
        float ymin = 0f;
        float ymax = 2.0f;

        long startTime = System.nanoTime();
        float[] dummyData = new float[(int)xmax + 1];
        for (int i = 0; i < dummyData.length; i++) {
            dummyData[i] = Math.abs((float)Math.sin(4.0f * ((float)Math.PI) * (float)i / (xmax + 1))) * 1.3f;
        }


        Paint pt = new Paint(Paint.ANTI_ALIAS_FLAG);
        pt.setColor(Color.BLACK);
        pt.setStrokeWidth(3f);
        pt.setStyle(Paint.Style.STROKE);

        Matrix trans = new Matrix();
        trans.setScale(getWidth() / xmax, -getHeight() / ymax);
        trans.postTranslate(0.0f, getHeight());

        Path path = new Path();
        float useVals[] = mVals;
        if (useVals == null)
            useVals = dummyData;
        path.moveTo(0.0f, useVals[0]);
        for (int i = 1; i < useVals.length; i++) {
            path.lineTo((float)i, useVals[i]);
        }
        path.transform(trans);
        canvas.drawPath(path, pt);
        Log.v(TAG, "Draw Time: " + (System.nanoTime() - startTime) + "ns");

        //canvas.drawLine(0, 0, getWidth(), getHeight(), new Paint());

        //canvas.drawOval(0, 0, getWidth(), getHeight(), new Paint());
    }

    public void setVals(float[] vals) {
        mVals = vals;
        invalidate();
    }
}
