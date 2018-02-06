package com.ns.empaque.wmpempaque.qrScanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.ns.empaque.wmpempaque.zxing.BarcodeScannerView;
import com.ns.empaque.wmpempaque.zxing.IViewFinder;
import com.ns.empaque.wmpempaque.zxing.ViewFinderView;
import com.ns.empaque.wmpempaque.zxing.ZXingScannerView;
import com.ns.empaque.wmpempaque.zxing.integration.android.Result;

import java.util.List;

public class ScanQR extends ActionBarActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    float mDist=0;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    Camera mCamera = BarcodeScannerView.mCamera;
                    Camera.Parameters params = mCamera.getParameters();
                    int action = event.getAction();

                    if (event.getPointerCount() > 1) {
                        // handle multi-touch events
                        if (action == MotionEvent.ACTION_POINTER_DOWN) {
                            mDist = getFingerSpacing(event);
                        } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                            mCamera.cancelAutoFocus();
                            handleZoom(event, params, mCamera);
                        }
                    } else {
                        // handle single touch events
                        if (action == MotionEvent.ACTION_UP) {
                            handleFocus(event, params, mCamera);
                        }
                    }
                }catch(Exception e){
                    Log.e("Error",e.getMessage()+"");
                }
            }
        });

        // Get the pointer ID

        return true;
    }

    private void handleZoom(MotionEvent event, Camera.Parameters params, Camera mCamera) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            //zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            //zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
        mCamera.setParameters(params);

    }

    public void handleFocus(MotionEvent event, Camera.Parameters params, Camera mCamera) {
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        // Get the pointer's current position
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    // currently set to auto-focus on single touch
                }
            });
        }
    }

    /** Determine the space between the first two fingers */
    private float getFingerSpacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
       // Toast.makeText(this, "Contents = " + rawResult.getText() +
        //        ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();
       // mScannerView.startCamera();
        Bundle conData = new Bundle();
        conData.putString("SCAN_RESULT",  rawResult.getText());
        conData.putString("SCAN_RESULT_FORMAT", rawResult.getBarcodeFormat().toString());
        Intent intent = new Intent();
        intent.putExtras(conData);
        setResult(RESULT_OK, intent);

        try {
            Vibrator vibs = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibs.hasVibrator())
                vibs.vibrate(200);
            else
                Log.e("vibrador", "Not Found");

            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
        }catch(Exception e){

        }
        finish();
    }

    private static class CustomViewFinderView extends ViewFinderView {
        public static final String TRADE_MARK_TEXT = "Favor de escanear el código QR";
        public static final int TRADE_MARK_TEXT_SIZE_SP = 15;
        public final Paint PAINT = new Paint();

        public CustomViewFinderView(Context context) {
            super(context);
            init();
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            PAINT.setColor(Color.WHITE);
            PAINT.setAntiAlias(true);
            float textPixelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    TRADE_MARK_TEXT_SIZE_SP, getResources().getDisplayMetrics());
            PAINT.setTextSize(textPixelSize);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            drawTradeMark(canvas);
        }

        private void drawTradeMark(Canvas canvas) {
            Rect framingRect = getFramingRect();
            float tradeMarkTop;
            float tradeMarkLeft;
            if (framingRect != null) {
                tradeMarkTop = framingRect.bottom + PAINT.getTextSize() + 10;
                tradeMarkLeft = framingRect.left;
            } else {
                tradeMarkTop = 10;
                tradeMarkLeft = canvas.getHeight() - PAINT.getTextSize() - 10;
            }
            canvas.drawText(TRADE_MARK_TEXT, tradeMarkLeft, tradeMarkTop, PAINT);
        }
    }
}
