package com.example.adlr.lightmeter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    TeensyConnection teensyConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        teensyConnection = new TeensyConnection(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!teensyConnection.connected()) {
                    teensyConnection.connect();
                } else {
                    final Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            float[] vals = teensyConnection.readMany();
                            if (vals == null)
                                return;
                            GraphView graphView = (GraphView) findViewById(R.id.view);
                            graphView.setVals(vals);
                            //handler.postDelayed(this, 500);
                        }
                    };
                    handler.postDelayed(runnable, 500);
                }
//                Snackbar.make(view, "Replaced with my own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.bottom_text);
        tv.setText(stringFromJNI());
//        RelativeLayout rl = (RelativeLayout)findViewById(R.id.content_main);
//        rl.requestLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
