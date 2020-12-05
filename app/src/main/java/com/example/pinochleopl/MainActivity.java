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

    RelativeLayout stock_cards_layout;
    RelativeLayout human_hand_cards_layout;
    RelativeLayout human_meld_cards_layout;
    RelativeLayout human_capture_cards_layout;
    RelativeLayout computer_hand_cards_layout;
    RelativeLayout computer_meld_cards_layout;
    RelativeLayout computer_capture_cards_layout;

    Model model;

    int CARD_HEIGHT;
    int CARD_WIDTH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        image_button_stuff();
        initialize_members();
        add_hand_card_to_view(0, 12);
    }

    private void initialize_members() {
        this.stock_cards_layout = (RelativeLayout) findViewById(R.id.stock_cards);
        this.human_hand_cards_layout = (RelativeLayout) findViewById(R.id.human_hand);
        this.human_meld_cards_layout = (RelativeLayout) findViewById(R.id.human_meld);
        this.human_capture_cards_layout = (RelativeLayout) findViewById(R.id.human_capture);
        this.computer_hand_cards_layout = (RelativeLayout) findViewById(R.id.computer_hand);
        this.computer_meld_cards_layout = (RelativeLayout) findViewById(R.id.computer_meld);
        this.computer_capture_cards_layout = (RelativeLayout) findViewById(R.id.computer_capture);

        this.model = new Model();

        this.CARD_HEIGHT = (int)  (60 * this.getResources().getDisplayMetrics().density);
        this.CARD_WIDTH = (int)  ((691 * this.CARD_HEIGHT) / 1056);
        this.layoutInflater = getLayoutInflater();
    }

    private void add_hand_card_to_view(int player_num,int card_id) {
        frameLayout = (FrameLayout) layoutInflater.inflate(R.layout.card_with_border, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(CARD_WIDTH,CARD_HEIGHT);
        layoutParams.setMargins(10, 0,0,0);
        frameLayout.setLayoutParams(layoutParams);
        if(player_num == 0) {
            this.human_hand_cards_layout.addView(frameLayout);
        } else {
            this.computer_hand_cards_layout.addView(frameLayout);
        }
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