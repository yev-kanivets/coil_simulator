package com.android.coil.view;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.android.coil.logic.Shaker;
import com.android.coil.logic.SoundMaker;
import com.android.coil.logic.Winder;
import com.android.coil.model.Skin;

public class CoilView extends View {
    private static final String TAG = "CoilView";

    private static final float METERS_PER_SCREEN = 5;
    private static final float BORDER_WIDTH = 10;
    private static final float COIL_WIDTH_HEIGHT_FACTOR = 0.5f;
    private static final float BASE_LINE_WIDTH = 30;
    private static final float METERS_PER_LEVEL = METERS_PER_SCREEN * 2;
    private static final float LEVEL_INCREASE = BASE_LINE_WIDTH / 10;

    public static final Skin DEFAULT_SKIN = new Skin(Color.BLACK, Color.GRAY, Color.RED);

    private Paint faceBorderPaint;
    private Paint faceBaseLinePaint;
    private Paint faceThreadPaint;

    private Paint sideBorderPaint;
    private Paint sideBaseLinePaint;
    private Paint sideThreadPaint;

    private Handler handler;
    private Winder winder;
    private SoundMaker soundMaker;

    @Nullable
    private OnCoilScrollListener coilScrollListener;

    private ViewMode mode;
    private float scrollFactor;
    private float curLineLength;

    // Settings
    private boolean playSound;
    private Skin skin;

    public CoilView(Context context) {
        super(context);
        init(context);
    }

    public CoilView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CoilView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CoilView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void setCoilScrollListener(@Nullable OnCoilScrollListener coilScrollListener) {
        this.coilScrollListener = coilScrollListener;
        // Trigger change to update UI
        coilScrolled(0);
    }

    public void setPlaySound(boolean playSound) {
        this.playSound = playSound;
    }

    public void setSkin(Skin skin) {
        if (skin == null) {
            skin = DEFAULT_SKIN;
        }
        this.skin = skin;

        faceBorderPaint.setColor(skin.getBorderColor());
        faceBaseLinePaint.setColor(skin.getBaseLineColor());
        faceThreadPaint.setColor(skin.getThreadColor());

        sideBorderPaint.setColor(skin.getBorderColor());
        sideBaseLinePaint.setColor(skin.getBaseLineColor());
        sideThreadPaint.setColor(skin.getThreadColor());
    }

    public void setMode(@NonNull ViewMode mode) {
        this.mode = mode;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        scrollFactor = METERS_PER_SCREEN / getHeight();
        if (ViewMode.Face == mode) {
            drawInFaceMode(canvas);
        } else {
            drawInSideMode(canvas);
        }
    }

    private void init(@NonNull Context context) {
        handler = new Handler();
        curLineLength = 0;
        mode = ViewMode.Side;

        skin = DEFAULT_SKIN;
        faceBorderPaint = new Paint();
        faceBorderPaint.setColor(skin.getBorderColor());
        faceBaseLinePaint = new Paint();
        faceBaseLinePaint.setColor(skin.getBaseLineColor());
        faceThreadPaint = new Paint();
        faceThreadPaint.setColor(skin.getThreadColor());

        sideBorderPaint = new Paint();
        sideBorderPaint.setColor(skin.getBorderColor());
        sideBorderPaint.setStrokeWidth(BORDER_WIDTH);
        sideBorderPaint.setStyle(Paint.Style.STROKE);
        sideBorderPaint.setMaskFilter(new BlurMaskFilter(0.5f, BlurMaskFilter.Blur.NORMAL));
        sideBaseLinePaint = new Paint();
        sideBaseLinePaint.setColor(skin.getBaseLineColor());
        sideBaseLinePaint.setMaskFilter(new BlurMaskFilter(0.5f, BlurMaskFilter.Blur.NORMAL));
        sideThreadPaint = new Paint();
        sideThreadPaint.setColor(skin.getThreadColor());
        sideThreadPaint.setMaskFilter(new BlurMaskFilter(0.5f, BlurMaskFilter.Blur.NORMAL));

        this.setOnTouchListener(touchListener);

        // Return here to make preview visible in Android Studio UI editor
        if (isInEditMode()) return;

        winder = new Winder();
        winder.setWindListener(new Winder.OnWindListener() {
            @Override
            public void onWind(float meters) {
                coilScrolled(-(meters / scrollFactor));
            }
        });

        Shaker shaker = new Shaker(context);
        shaker.setShakeListener(new Shaker.OnShakeListener() {
            @Override
            public void onShake(float speed) {
                winder.startWinding(speed);
            }
        });

        soundMaker = new SoundMaker(context);
    }

    private void coilScrolled(double scrollSize) {
        curLineLength += scrollSize * scrollFactor;
        if (curLineLength < 0) {
            curLineLength = 0;
            winder.stopWinding();
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (playSound && curLineLength != 0) soundMaker.playTick();
                invalidate();
                if (coilScrollListener != null) {
                    coilScrollListener.onCoilScroll(curLineLength);
                }
            }
        });
    }

    private void drawInFaceMode(Canvas canvas) {
        float centerY = getHeight() / 2;
        float coilWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        float coilHeight = coilWidth * COIL_WIDTH_HEIGHT_FACTOR;
        float startY = (getHeight() - coilHeight) / 2;

        // Draw borders
        canvas.drawRect(getPaddingLeft(), startY, getPaddingLeft() + BORDER_WIDTH,
                startY + coilHeight, faceBorderPaint);
        canvas.drawRect(getPaddingLeft() + coilWidth - BORDER_WIDTH, startY,
                getPaddingLeft() + coilWidth, startY + coilHeight, faceBorderPaint);

        // Draw base line
        canvas.drawRect(getPaddingLeft() + BORDER_WIDTH, centerY - BASE_LINE_WIDTH / 2,
                getPaddingLeft() + coilWidth - BORDER_WIDTH, centerY + BASE_LINE_WIDTH / 2,
                faceBaseLinePaint);

        // Draw a thread
        int level = (int) (curLineLength / METERS_PER_LEVEL);
        float rem = curLineLength % METERS_PER_LEVEL;
        if (level != 0) {
            canvas.drawRect(getPaddingLeft() + BORDER_WIDTH,
                    centerY - BASE_LINE_WIDTH / 2 - level * LEVEL_INCREASE,
                    getPaddingLeft() + coilWidth - BORDER_WIDTH,
                    centerY + BASE_LINE_WIDTH / 2 + level * LEVEL_INCREASE,
                    faceThreadPaint);
        }
        float levelFactor = rem / METERS_PER_LEVEL;
        canvas.drawRect(getPaddingLeft() + BORDER_WIDTH,
                centerY - BASE_LINE_WIDTH / 2 - (level + 1) * LEVEL_INCREASE,
                getPaddingLeft() + BORDER_WIDTH + (coilWidth - BORDER_WIDTH) * levelFactor,
                centerY + BASE_LINE_WIDTH / 2 + (level + 1) * LEVEL_INCREASE,
                faceThreadPaint);
    }

    private void drawInSideMode(Canvas canvas) {
        float coilWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        float coilHeight = coilWidth * COIL_WIDTH_HEIGHT_FACTOR;
        float coilDiameter = Math.min(getHeight() - getPaddingTop() - getPaddingBottom(),
                getWidth() - getPaddingLeft() - getPaddingRight());
        float factor = coilDiameter / coilHeight;

        // Draw border
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, coilDiameter / 2, sideBorderPaint);

        // Draw thread
        float level = curLineLength / METERS_PER_LEVEL;
        if (level != 0) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2,
                    (BASE_LINE_WIDTH / 2 + level * LEVEL_INCREASE) * factor, sideThreadPaint);
        }

        // Draw base line
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, BASE_LINE_WIDTH * factor / 2, sideBaseLinePaint);

        // Draw horizontal and vertical lines
        float rem = curLineLength % METERS_PER_LEVEL;
        float angle = 360 * (rem / METERS_PER_LEVEL);

        canvas.save();
        canvas.rotate(angle, getWidth() / 2, getHeight() / 2);
        canvas.drawLine(getPaddingLeft() + BORDER_WIDTH / 2, getHeight() / 2,
                getPaddingLeft() + BORDER_WIDTH / 2 + coilDiameter, getHeight() / 2, sideBorderPaint);
        canvas.drawLine(getWidth() / 2, getHeight() / 2 - coilDiameter / 2 - BORDER_WIDTH / 2,
                getWidth() / 2, getHeight() / 2 + coilDiameter / 2 - BORDER_WIDTH / 2, sideBorderPaint);
        canvas.restore();
    }

    private OnTouchListener touchListener = new OnTouchListener() {
        private float oldX = -1;
        private float oldY = -1;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            winder.stopWinding();

            if (oldX != -1 && oldY != -1) {
                float deltaX = motionEvent.getY() - oldY;
                coilScrolled(deltaX);
            }

            oldX = motionEvent.getX();
            oldY = motionEvent.getY();

            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                oldX = -1;
                oldY = -1;
            }

            return true;
        }
    };

    public enum ViewMode {
        Face, Side
    }

    public interface OnCoilScrollListener {
        void onCoilScroll(double lineLength);
    }
}
