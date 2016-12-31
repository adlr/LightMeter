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

import java.util.Arrays;

/**
 * Created by adlr on 12/19/16.
 */

public class GraphView extends View {
    private static final String TAG = "GraphView";
    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private float[] mVals;

    public float[] TrimValues(float[] vals) {
        // find max/min values in the first third, then find the upswing at half amplitude
        float top = 0.0f, bot = 4.0f;
        int scan_length = vals.length / 3;
        for (int i = 0; i < scan_length; i++) {
            vals[i] = Math.max(top, vals[i]);
            vals[i] = Math.min(bot, vals[i]);
        }
        float mid = (top + bot) / 2.0f;
        for (int i = 2; i < scan_length; i++) {
            if (vals[i - 1] < mid && vals[i - 2] < mid &&
                    vals[i + 1] > mid && vals[i + 2] > mid) {
                return Arrays.copyOfRange(vals, i, scan_length * 2);
            }
        }
        return Arrays.copyOfRange(vals, 0, scan_length * 2);
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float xmin = 0.0f;
        float xmax = 2399.0f;
        float ymin = 0f;
        float ymax = 2.0f;

        float useVals[] = mVals;
        if (useVals == null) {
            float[] dummyData = new float[400];
            for (int i = 0; i < dummyData.length; i++) {
                dummyData[i] = Math.abs((float)Math.sin(4.0f * ((float)Math.PI) * (float)i / dummyData.length)) * 1.3f;
            }
            useVals = dummyData;
        } else {
            useVals = TrimValues(useVals);
        }
        xmax = useVals.length - 1;

        long startTime = System.nanoTime();


        Paint pt = new Paint(Paint.ANTI_ALIAS_FLAG);
        pt.setColor(Color.BLACK);
        pt.setStrokeWidth(3f);
        pt.setStyle(Paint.Style.STROKE);

        Matrix trans = new Matrix();
        trans.setScale(getWidth() / xmax, -getHeight() / ymax);
        trans.postTranslate(0.0f, getHeight());

        Path path = new Path();
        path.moveTo(0.0f, useVals[0]);
        for (int i = 1; i < useVals.length; i++) {
            path.lineTo((float)i, useVals[i]);
        }
        path.transform(trans);
        canvas.drawPath(path, pt);
        //Log.v(TAG, "Draw Time: " + (System.nanoTime() - startTime) + "ns");

        //canvas.drawLine(0, 0, getWidth(), getHeight(), new Paint());

        //canvas.drawOval(0, 0, getWidth(), getHeight(), new Paint());
    }

    public void setVals(float[] vals) {
        mVals = vals;
        invalidate();
    }
}
