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
    private Paint paintWinText;

    private final long MS_IN_SECOND = 1000;
    private long fps;

    private int containerWidth;
    private int containerHeight;
    private float screenDensity;

    private float blockSize;
    private float blockWidth;
    private float blockHeight;
    private float fontSize;
    private int gridWidth;
    private int gridHeight;
    private int horizontalTouched;
    private int verticalTouched;
    private int subHorizontalPosition;
    private int subVerticalPosition;
    private boolean isHit = false;
//    private Animation bounceAnim;
    private ArrayList<int[]> shots;
    private int distanceFromSub;
    private boolean isBoom;

    private DB db;


    public Game(Context context) {
        super(context);
    }

    public Game(Context context, int width, int height, float destiny) {
        super(context);

        db = new DB(context);

        containerWidth = width;
        containerHeight = height;
        screenDensity = destiny;

        holder = getHolder();
        blockSize = 40 * screenDensity;
        fontSize = 20 * screenDensity;

        gridWidth = containerWidth / (int) blockSize;
        gridHeight = containerHeight / (int) blockSize;

        blockWidth = containerWidth / (float)gridWidth;
        blockHeight = containerHeight / (float)gridHeight;

        Log.d("blockWidth", containerWidth + " : " + gridWidth + " : " + blockWidth);

        paint = new Paint();
        paintWinText = new Paint();
        paintWinText.setTextSize(fontSize * 2);
        paintWinText.setColor(Color.WHITE);

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
        for (int i = 1; i < gridWidth; i++) {
            canvas.drawLine(blockWidth * i, 0, blockWidth * i, containerHeight, paint);
        }

        for (int i = 1; i < gridHeight; i++) {
            canvas.drawLine(0, blockHeight * i, containerWidth, blockHeight * i, paint);
        }

        for (int[] shot : shots) {
            float left = (shot[0] - 1) * blockWidth;
            float top = (shot[1] - 1) * blockHeight;

            paint.setColor(Color.WHITE);
            canvas.drawRect(left, top, left + blockWidth, top + blockHeight, paint);

            paint.setColor(Color.BLACK);
            paint.setTextSize(fontSize);
            paint.setFakeBoldText(true);
            String text = "" + shot[2];
            float textWidth = paint.measureText(text);
            canvas.drawText(text, left + (blockWidth - textWidth) / 2, top + blockHeight - (blockHeight - fontSize) / 2, paint);
        }
    }

    private void draw() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();

            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            if (isBoom) {
                String line1 = "You have won";
                String line2 = "Your result is " + shots.size();
                float startY = containerHeight / 2;

                float widthLine1 = paintWinText.measureText(line1);
                canvas.drawText(line1, (containerWidth - widthLine1) / 2, startY, paintWinText);

                float widthLine2 = paintWinText.measureText(line2);
                canvas.drawText(line2, (containerWidth - widthLine2) / 2, startY + paintWinText.getTextSize() * 1.5f, paintWinText );
            } else {
                drawGrid();
            }

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
            if (isBoom) {
                showResults();
            } else {
                onHit(event.getX(), event.getY());
            }
        }
        return true;
    }

    private void showResults() {
        Navigation.findNavController(this).navigate(R.id.gameToEnd);
    }

    private void onHit(float touchX, float touchY) {
//        gameView.startAnimation(bounceAnim);
        horizontalTouched = (int) (touchX / blockWidth + 1);
        verticalTouched = (int) (touchY / blockHeight + 1);

        isHit = horizontalTouched == subHorizontalPosition && verticalTouched == subVerticalPosition;


        if (isHit) {
            boom();
        } else {
            int horizontalGap = Math.abs(horizontalTouched - subHorizontalPosition);
            int verticalGap = Math.abs(verticalTouched - subVerticalPosition);

            distanceFromSub = Math.max(horizontalGap, verticalGap);
            int[] shot = new int[]{horizontalTouched, verticalTouched, distanceFromSub};
            shots.add(shot);
//            Log.d("test", "horGap: " + horizontalGap + ", verGap: " + verticalGap);
//            Log.d("test", "distanceFromSub: " + distanceFromSub);
//            Log.d("test", "touchPosition: " + horizontalTouched + ", " + verticalTouched);
//            Log.d("test", "subPosition: " + subHorizontalPosition + ", " + subVerticalPosition);
        }
    }

    private void boom() {
        isBoom = true;
        db.insert(shots.size());
    }
}
