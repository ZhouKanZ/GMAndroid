package com.jms.cleanse;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UItestActivity extends AppCompatActivity {

    @BindView(R.id.btn_toast)
    Button btnToast;
    Dialog dialog;
    View dialogView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uitest);
        ButterKnife.bind(this);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_point);
        Button btn = dialog.findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UItestActivity.this,"nmh",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @OnClick(R.id.btn_toast)
    public void onViewClicked() {
        dialog.show();
    }
}
