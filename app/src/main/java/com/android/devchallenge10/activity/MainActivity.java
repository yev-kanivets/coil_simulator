package com.android.devchallenge10.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.android.coil.model.Skin;
import com.android.devchallenge10.R;
import com.android.coil.view.CoilView;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_APPLY_SKIN = 955;

    private Skin skin = CoilView.DEFAULT_SKIN;

    private TextView tvCoilLineLength;
    private CoilView coilView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCoilLineLength = (TextView) findViewById(R.id.coil_line_length);
        coilView = (CoilView) findViewById(R.id.coilView);
        CheckBox cbPlaySound = (CheckBox) findViewById(R.id.cb_play_sound);
        Button btnApplySkin = (Button) findViewById(R.id.btn_apply_skin);
        Switch swSideViewMode = (Switch) findViewById(R.id.sw_side_view_mode);

        coilView.setSkin(skin);

        coilView.setCoilScrollListener(new CoilView.OnCoilScrollListener() {
            @Override
            public void onCoilScroll(double lineLength) {
                tvCoilLineLength.setText(getString(R.string.meter_counter, lineLength));
            }
        });

        cbPlaySound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                coilView.setPlaySound(b);
            }
        });

        btnApplySkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SkinActivity.class);
                intent.putExtra(SkinActivity.KEY_SKIN, skin);
                startActivityForResult(intent, REQUEST_APPLY_SKIN);
            }
        });

        swSideViewMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    coilView.setMode(CoilView.ViewMode.Face);
                } else {
                    coilView.setMode(CoilView.ViewMode.Side);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_APPLY_SKIN:
                    skin = data.getParcelableExtra(SkinActivity.KEY_SKIN);
                    coilView.setSkin(skin);
                    break;

                default:
                    break;
            }
        }
    }
}
