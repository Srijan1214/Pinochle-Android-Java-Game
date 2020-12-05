package com.example.pinochleopl;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    LayoutInflater layoutInflater;
    FrameLayout frameLayout;
    FrameLayout frameLayout2;
    boolean bordered = false;

    Deck deck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image_button_stuff();
        deck = new Deck();
    }

    private void image_button_stuff() {
//        highlight = (ImageButton) findViewById(R.id.highlight);
//        frameLayout = (FrameLayout) findViewById(R.id.card_layout);
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.stock_cards);
        RelativeLayout hand_cards = (RelativeLayout)findViewById(R.id.computer_cards);

        int card_height = (int)  (60 * this.getResources().getDisplayMetrics().density);
        int card_width = (int)  ((691 * card_height) / 1056);
        layoutInflater = getLayoutInflater();
        frameLayout = (FrameLayout) layoutInflater.inflate(R.layout.card_with_border, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(card_width,card_height);
        layoutParams.setMargins(10, 0,0,0);
        frameLayout.setLayoutParams(layoutParams);
        rl.addView(frameLayout);

        frameLayout2 = (FrameLayout) layoutInflater.inflate(R.layout.card_with_border, null);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(card_width,card_height);
        layoutParams2.setMargins(100, 0,0,0);
        frameLayout2.setLayoutParams(layoutParams2);
        rl.addView(frameLayout2);

        View.OnClickListener onClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(bordered) {
                    v.findViewById(R.id.black_highlight).setVisibility(View.INVISIBLE);
                    try {
                        rl.removeView(v);
                        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(card_width,card_height);
                        layoutParams2.setMargins(2000, 0,0,0);
                        v.setLayoutParams(layoutParams2);
                        hand_cards.addView(v);
                    } catch (Exception e) {}
                } else {
                    v.findViewById(R.id.black_highlight).setVisibility(View.VISIBLE);
                    try {
                        hand_cards.removeView(v);
                        rl.addView(v);
                    } catch (Exception e) {}
                }
                bordered = !bordered;
                v.bringToFront();
                System.out.println("Clicked!!!");
                System.out.println(v.getId());
            }
        };
        frameLayout.setOnClickListener(onClickListener);
        frameLayout2.setOnClickListener(onClickListener);
    }
}