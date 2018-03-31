package com.jms.cleanse;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jms.cleanse.widget.JMMapView;
import com.jms.cleanse.widget.POIPoint;
import com.jms.cleanse.widget.mapview.TestPOI;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UItestActivity extends AppCompatActivity {

    JMMapView mapView;
    List<TestPOI> testPOIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uitest);

        mapView = findViewById(R.id.map_view);

        testPOIS = new ArrayList<>();
        testPOIS.add(new TestPOI(-6.7, 0.45, false));
        testPOIS.add(new TestPOI(-2.55, 4.4, false));
        testPOIS.add(new TestPOI(3, 2, true));
        testPOIS.add(new TestPOI(0.3, -2.65, false));

        @SuppressLint("ResourceType") InputStream is = getResources().openRawResource(R.drawable.map_test);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        mapView.setMap(bitmap);
        mapView.setTestPOIS(testPOIS);

        mapView.setOnClickListener(new JMMapView.OnClickListener() {
            @Override
            public void onPointClick(TestPOI poi, int pos) {
                Log.d("xxx", "onPointClick:  i had been clicked" );
            }

            @Override
            public void onPathClick(int pos) {

            }
        });

    }


}
