package com.example.submarinehunter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.Animation;

import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.Random;

public class Game extends SurfaceView implements Runnable {

    private Thread thread;
    private volatile boolean playing;
    private boolean paused = true;

    private SurfaceHolder holder;
    private Canvas canvas;
    private Paint paint;

    private final long MS_IN_SECOND = 1000;
    private long fps;

    private int containerWidth;
    private int containerHeight;
    private float screenDensity;

    float blockSize;
    float blockWidth;
    float blockHeight;
    float fontSize;
    float marginSize;
    int gridWidth;
    int gridHeight;
    int horizontalTouched;
    int verticalTouched;
    int subHorizontalPosition;
    int subVerticalPosition;
    boolean isHit = false;
    Animation bounceAnim;
    ArrayList<int[]> shots;
    int distanceFromSub;
    boolean isBoom;

    Bitmap blankBitmap;
    Bitmap seabedBitmap;

    public Game(Context context) {
        super(context);
    }

    public Game(Context context, int width, int height, float destiny) {
        super(context);

        containerWidth = width;
        containerHeight = height;
        screenDensity = destiny;

        holder = getHolder();
        blockSize = 40 * screenDensity;
        fontSize = 20 * screenDensity;

        gridWidth = (int) (containerWidth / blockSize);
        gridHeight = (int) (containerHeight / blockSize);

        blockWidth = containerWidth / gridWidth;
        blockHeight = containerHeight / gridHeight;

//        paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        shots = new ArrayList<>();
        initGame();
    }

    private void initGame() {
        Random random = new Random();
        subHorizontalPosition = random.nextInt(gridWidth) + 1;
        subVerticalPosition = random.nextInt(gridHeight) + 1;
        shots.clear();
        isBoom = false;
    }

    private void drawGrid() {
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(screenDensity);
        for (int i = 1; i <= gridWidth; i++) {
            canvas.drawLine(blockSize * i, 0, blockSize * i, containerHeight, paint);
        }

        for (int i = 1; i <= gridHeight; i++) {
            canvas.drawLine(0, blockSize * i, containerWidth, blockSize * i, paint);
        }

        for (int[] shot : shots) {
            float left = (shot[0] - 1) * blockSize;
            float top = (shot[1] - 1) * blockSize;

            paint.setColor(Color.WHITE);
            canvas.drawRect(left, top, left + blockSize, top + blockSize, paint);

            paint.setColor(Color.BLACK);
            paint.setTextSize(fontSize);
            paint.setFakeBoldText(true);
            canvas.drawText("" + shot[2], left + marginSize, top + fontSize + marginSize, paint);
        }
    }

    private void draw() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();

            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            drawGrid();

            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void run() {
        while(playing) {
            long startFrame = System.currentTimeMillis();

            draw();

            long timeFrame = System.currentTimeMillis() - startFrame;
            if (timeFrame > 0) {
                fps = MS_IN_SECOND / timeFrame;
            }
        }
    }

    public void pause() {
        playing = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }

    public void resume() {
        playing = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            onHit(event.getX(), event.getY());
        }
        return true;
    }

    private void onHit(float touchX, float touchY) {
//        gameView.startAnimation(bounceAnim);
        horizontalTouched = (int) (touchX / blockSize + 1);
        verticalTouched = (int) (touchY / blockSize + 1);

        isHit = horizontalTouched == subHorizontalPosition && verticalTouched == subVerticalPosition;


        if (isHit) {
            boom();
        } else {
            int horizontalGap = Math.abs(horizontalTouched - subHorizontalPosition);
            int verticalGap = Math.abs(verticalTouched - subVerticalPosition);

            distanceFromSub = Math.max(horizontalGap, verticalGap);
            int[] shot = new int[]{horizontalTouched, verticalTouched, distanceFromSub};
            shots.add(shot);
//            drawGrid();

            Log.d("test", "horGap: " + horizontalGap + ", verGap: " + verticalGap);
            Log.d("test", "distanceFromSub: " + distanceFromSub);
            Log.d("test", "touchPosition: " + horizontalTouched + ", " + verticalTouched);
            Log.d("test", "subPosition: " + subHorizontalPosition + ", " + subVerticalPosition);
        }
    }

    private void boom() {
        Navigation.findNavController(this).navigate(R.id.gameToEnd);
    }
}
