package com.flatstack.touchme.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.flatstack.touchme.App;
import com.flatstack.touchme.data.responses.ConnectionResponse;
import com.flatstack.touchme.utils.AndroidUtils;
import com.flatstack.touchme.utils.Errors;
import com.flatstack.touchme.utils.Logger;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by Ilya Eremin on 10/30/15.
 */
public class TouchMeView extends View {

    private static final int INVALIDATE_PERIOD = 16;
    private static final int NETWORK_CALL_PERIOD = 1000;

    private Paint paint;
    private Paint centerColor;
    private Paint ripplePaint;
    Handler mHandler;

    public TouchMeView(Context context) {
        super(context);
        init();
    }

    public TouchMeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(30);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);

        centerColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerColor.setStrokeWidth(1);
        centerColor.setColor(0xff3261DD);

        ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ripplePaint.setStrokeWidth(mainRippleWidth);
        ripplePaint.setColor(0xff3261DD);
        ripplePaint.setStyle(Paint.Style.STROKE);

        mHandler = new Handler();

        mHandler.postDelayed(m_Runnable, INVALIDATE_PERIOD);
        mHandler.postDelayed(retrieveCoordinates, NETWORK_CALL_PERIOD);
        mHandler.postDelayed(sendMyCoordinates, NETWORK_CALL_PERIOD);
    }

    private Runnable retrieveCoordinates = new Runnable() {
        @Override public void run() {
            App.getApi().checkConnection("79625535459")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ConnectionResponse>() {
                    @Override public void call(ConnectionResponse connection) {
                        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            someoneTouchAt(connection.connection.receiverX, connection.connection.receiverY);
                        } else {
                            someoneTouchAt(connection.connection.senderX, connection.connection.senderY);
                        }
                        mHandler.postDelayed(retrieveCoordinates, NETWORK_CALL_PERIOD);
                    }
                }, Errors.onError());
        }
    };

    private Runnable sendMyCoordinates = new Runnable() {
        @Override public void run() {
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                App.getApi().updateSenderCoordinates(2, (int) x, (int) y)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Void>() {
                        @Override public void call(Void aVoid) {
                            mHandler.postDelayed(sendMyCoordinates, NETWORK_CALL_PERIOD);
                        }
                    }, Errors.onError());
            } else {
                App.getApi().updateReceiverCoordinates(2, (int) x, (int) y)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Void>() {
                        @Override public void call(Void aVoid) {
                            mHandler.postDelayed(sendMyCoordinates, NETWORK_CALL_PERIOD);
                        }
                    }, Errors.onError());
            }
        }
    };

    private final Runnable m_Runnable = new Runnable() {
        public void run() {
            invalidate();
            mHandler.postDelayed(m_Runnable, INVALIDATE_PERIOD);
        }
    };

    boolean someonePressed;
    float   someoneX, someoneY;

    public void someoneTouchAt(int x, int y) {
        someonePressed = true;
        someoneX = x;
        someoneY = y;
    }

    public void someoneNoMorePressed() {
        someonePressed = false;
    }

    float x, y;
    int cycle;

    boolean viewPressed;

    @Override public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                viewPressed = true;
                mHandler.postDelayed(mLongPressed, 500);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mHandler.removeCallbacks(mLongPressed);
                viewPressed = false;
                break;
        }
        x = event.getX();
        y = event.getY();
        Logger.d("x:" + x + "y: " + y);
        return true;
    }

    Runnable mLongPressed = new Runnable() {
        public void run() {
            if (someonePressed && viewPressed) {
                if (Math.abs(x - someoneX) < 20 && Math.abs(y - someoneY) < 20) {
                    Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(new long[]{0, 50, 200, 50, 200, 50}, -1);
                    setBackgroundColor(0xffAECD45);
                }
            }
        }
    };

    int step            = 40;
    int MAX_RADIUS      = 100;
    int mainRippleWidth = 40;

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int minSide = canvas.getWidth() > canvas.getHeight() ? canvas.getHeight() : canvas.getWidth();


        int mainCircleRadius = minSide / 2 - AndroidUtils.dp(48);
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, mainCircleRadius, centerColor);

        for (int i = 0; i < 3; i++) {
            int maxWidgetSide = minSide / 2 - AndroidUtils.dp(16);
            int radius = (mainCircleRadius + (mainRippleWidth * i + cycle % (maxWidgetSide - mainCircleRadius))) % maxWidgetSide;
            ripplePaint.setColor(Color.argb((int) (255 * (1 - (double) radius / maxWidgetSide)), 50, 97, 221));
            canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, radius, ripplePaint);
        }

        for (int i = 0; i < MAX_RADIUS / step; i++) {
            int radius = (step * i + cycle) % MAX_RADIUS;
            paint.setColor(Color.argb((int) (255 * (1 - (double) radius / MAX_RADIUS)), 255, 0, 0));
            canvas.drawCircle(someoneX, someoneY, radius, paint);
        }

        canvas.drawPoint(x, y, paint);

        cycle++;
    }
}
