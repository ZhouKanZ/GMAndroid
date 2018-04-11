package com.jms.cleanse;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.jms.cleanse.entity.db.PoiPoint;
import com.jms.cleanse.widget.JMMapView;
import com.jms.cleanse.widget.mapview.TestPOI;

import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UItestActivity extends AppCompatActivity {

    List<TestPOI> testPOIS;
    @BindView(R.id.map_view)
    JMMapView mapView;
    @BindView(R.id.add_task)
    Button addTask;
    @BindView(R.id.cleanse)
    Switch cleanse;
    @BindView(R.id.btn_end)
    Button btnEnd;
    @BindView(R.id.btn_complete)
    Button btnComplete;
    @BindView(R.id.layout_test)
    RelativeLayout layoutTest;

    private boolean isCleanse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uitest);
        ButterKnife.bind(this);

        mapView = findViewById(R.id.map_view);

//        testPOIS = new ArrayList<>();
//        testPOIS.add(new TestPOI(-6.7, 0.45, false));
//        testPOIS.add(new TestPOI(-2.55, 4.4, false));
//        testPOIS.add(new TestPOI(3, 2, true));
//        testPOIS.add(new TestPOI(0.3, -2.65, false));
//
        @SuppressLint("ResourceType") InputStream is = getResources().openRawResource(R.drawable.map_test);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        mapView.setMap(bitmap);
//        mapView.setTestPOIS(testPOIS);
        mapView.setOnClickListener(new JMMapView.OnClickListener() {

            @Override
            public void onPointClick(PoiPoint poi, int pos) {

            }

            @Override
            public void onPathClick(int pos) {

            }
        });

        cleanse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //
                isCleanse = isChecked;
            }
        });

    }


    @OnClick({R.id.add_task, R.id.btn_end, R.id.btn_complete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_task:
               mapView.setTaskEditing(true);
                break;
            case R.id.btn_end:
                mapView.editComplete();
                break;
            case R.id.btn_complete:
                mapView.addPoint(isCleanse);
                break;
        }
    }
}
