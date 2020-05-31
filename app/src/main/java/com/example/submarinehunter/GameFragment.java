package com.example.submarinehunter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment{

    int numberHorizontalPixels;
    int numberVerticalPixels;
    float blockSize;
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
    //    int shots;
    ArrayList<int[]> shots;
    int distanceFromSub;
    float density;
    boolean boomed;

    ImageView gameView;
    Bitmap blankBitmap;
    Bitmap seabedBitmap;
    Canvas canvas;
    Paint paint;

    private Game game;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GameFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GameFragment newInstance(String param1, String param2) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        float density = getResources().getDisplayMetrics().density;


//        bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        View view = inflater.inflate(R.layout.fragment_game, container, false);
        FrameLayout host = view.findViewById(R.id.game_host);
        game = new Game(getContext(), container.getWidth(), container.getHeight(), density);
        game.setZOrderOnTop(true);
        game.getHolder().setFormat(PixelFormat.TRANSPARENT);
        host.addView(game);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        game.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        game.resume();
    }
}
