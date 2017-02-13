package com.android.devchallenge10.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.android.coil.model.Skin;
import com.android.devchallenge10.R;
import com.pavelsikun.vintagechroma.ChromaDialog;
import com.pavelsikun.vintagechroma.IndicatorMode;
import com.pavelsikun.vintagechroma.OnColorSelectedListener;
import com.pavelsikun.vintagechroma.colormode.ColorMode;

public class SkinActivity extends AppCompatActivity {
    public static final String KEY_SKIN = "key_skin";

    @ColorInt
    private int borderColor;
    @ColorInt
    private int baseLineColor;
    @ColorInt
    private int threadColor;

    private View viewBorderColor;
    private View viewBaseLineColor;
    private View viewThreadColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);
        initToolbar();

        viewBorderColor = findViewById(R.id.view_border_color);
        viewBaseLineColor = findViewById(R.id.view_base_line_color);
        viewThreadColor = findViewById(R.id.view_thread_color);

        final Skin skin = getIntent().getParcelableExtra(KEY_SKIN);
        if (skin == null) {
            finish();
            return;
        }

        borderColor = skin.getBorderColor();
        baseLineColor = skin.getBaseLineColor();
        threadColor = skin.getThreadColor();

        viewBorderColor.setBackgroundColor(borderColor);
        viewBaseLineColor.setBackgroundColor(baseLineColor);
        viewThreadColor.setBackgroundColor(threadColor);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(KEY_SKIN, new Skin(borderColor, baseLineColor, threadColor));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        viewBorderColor.setOnClickListener(borderViewListener);
        viewBaseLineColor.setOnClickListener(baseLineViewListener);
        viewThreadColor.setOnClickListener(threadViewListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    protected Toolbar initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        return toolbar;
    }

    private View.OnClickListener borderViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new ChromaDialog.Builder()
                    .initialColor(borderColor)
                    .colorMode(ColorMode.ARGB)
                    .indicatorMode(IndicatorMode.HEX)
                    .onColorSelected(new OnColorSelectedListener() {
                        @Override
                        public void onColorSelected(@ColorInt int color) {
                            borderColor = color;
                            viewBorderColor.setBackgroundColor(borderColor);
                        }
                    })
                    .create()
                    .show(getSupportFragmentManager(), "ChromaDialog");
        }
    };

    private View.OnClickListener baseLineViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new ChromaDialog.Builder()
                    .initialColor(baseLineColor)
                    .colorMode(ColorMode.ARGB)
                    .indicatorMode(IndicatorMode.HEX)
                    .onColorSelected(new OnColorSelectedListener() {
                        @Override
                        public void onColorSelected(@ColorInt int color) {
                            baseLineColor = color;
                            viewBaseLineColor.setBackgroundColor(baseLineColor);
                        }
                    })
                    .create()
                    .show(getSupportFragmentManager(), "ChromaDialog");
        }
    };

    private View.OnClickListener threadViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new ChromaDialog.Builder()
                    .initialColor(threadColor)
                    .colorMode(ColorMode.ARGB)
                    .indicatorMode(IndicatorMode.HEX)
                    .onColorSelected(new OnColorSelectedListener() {
                        @Override
                        public void onColorSelected(@ColorInt int color) {
                            threadColor = color;
                            viewThreadColor.setBackgroundColor(threadColor);
                        }
                    })
                    .create()
                    .show(getSupportFragmentManager(), "ChromaDialog");
        }
    };
}
