package com.example.pinochleopl;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    LayoutInflater layoutInflater;
    FrameLayout frameLayout;
    FrameLayout frameLayout2;

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

    Bitmap[] id_to_bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        image_button_stuff();
        initialize_members();
        load_card_bitmaps();
        this.model.deal_cards_from_deck_to_players();
        redraw_cards();
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

        this.CARD_HEIGHT = (int) (60 * this.getResources().getDisplayMetrics().density);
        this.CARD_WIDTH = (int) ((691 * this.CARD_HEIGHT) / 1056);
        this.layoutInflater = getLayoutInflater();
        this.id_to_bitmap = new Bitmap[48];
    }

    private int getResId(String resName, Class<?> c) {
        try {
            Field field = c.getField(resName);
            return field.getInt(null);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private Bitmap get_resized_bitmap(Bitmap bitmap, int width, int height) {
        Matrix matrix = new Matrix();

        RectF src = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF dst = new RectF(0, 0, width, height);

        matrix.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void load_card_bitmaps() {
        class ImageName {
            public String get_image_name_from_id(int id) {
                int face = Card.get_face_from_id(id);
                int suit = Card.get_suit_from_id(id);
                String face_string;
                String suit_string;
                switch (face) {
                    case Card.NINE_FACE:
                        face_string = "nine";
                        break;
                    case Card.TEN_FACE:
                        face_string = "ten";
                        break;
                    case Card.JACK_FACE:
                        face_string = "jack";
                        break;
                    case Card.QUEEN_FACE:
                        face_string = "queen";
                        break;
                    case Card.KING_FACE:
                        face_string = "king";
                        break;
                    case Card.ACE_FACE:
                        face_string = "ace";
                        break;
                    default:
                        face_string = "";
                }

                switch (suit) {
                    case Card.SPADES_SUIT:
                        suit_string = "_s";
                        break;
                    case Card.CLUBS_SUIT:
                        suit_string = "_c";
                        break;
                    case Card.DIAMONDS_SUIT:
                        suit_string = "_d";
                        break;
                    case Card.HEARTS_SUIT:
                        suit_string = "_h";
                        break;
                    default:
                        suit_string = "";
                }

                return face_string + suit_string;
            }
        }

        ImageName imageName = new ImageName();

        for (int i = 0; i < id_to_bitmap.length; i += 2) {
            id_to_bitmap[i] =
                    get_resized_bitmap(
                            BitmapFactory.decodeResource(
                                    getResources(),
                                    getResId(
                                            imageName.get_image_name_from_id(i),
                                            R.drawable.class)),
                            CARD_WIDTH,
                            CARD_HEIGHT);
            id_to_bitmap[i + 1] = id_to_bitmap[i];
        }
    }

    private void redraw_cards() {
        PlayerCardData[] playerCardData = this.model.get_players_card_data();

        this.redraw_hand_cards(Constants.HUMAN, playerCardData[Constants.HUMAN]);
        this.redraw_hand_cards(Constants.COMPUTER, playerCardData[Constants.COMPUTER]);

        this.redraw_meld_cards(Constants.HUMAN, playerCardData[Constants.HUMAN]);
        this.redraw_meld_cards(Constants.COMPUTER, playerCardData[Constants.COMPUTER]);

        this.redraw_capture_cards(Constants.HUMAN, playerCardData[Constants.HUMAN]);
        this.redraw_capture_cards(Constants.COMPUTER, playerCardData[Constants.COMPUTER]);

        this.redraw_stock_cards(this.model.get_stock_data());
    }

    private void redraw_hand_cards(int player_num, PlayerCardData playerCardData) {
        int dx = (int) (20 * this.getResources().getDisplayMetrics().density);
        int left_margin = 0;

        for (int i = 0; i < playerCardData.hand_card_pile.size(); i++) {
            if (playerCardData.hand_meld_involvement_list.get(i).size() != 0) {
                continue;
            }
            int card_id = playerCardData.hand_card_pile.get(i);
            frameLayout = (FrameLayout) layoutInflater.inflate(R.layout.card_with_border, null);
            ((ImageButton) frameLayout.findViewById(R.id.card_image)).setImageBitmap(id_to_bitmap[card_id]);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(CARD_WIDTH, CARD_HEIGHT);
            layoutParams.setMargins(left_margin, 0, 0, 0);
            frameLayout.setLayoutParams(layoutParams);
            if (player_num == Constants.HUMAN) {
                this.human_hand_cards_layout.addView(frameLayout);
            } else {
                this.computer_hand_cards_layout.addView(frameLayout);
            }
            left_margin += dx;
        }
    }

    private void redraw_meld_cards(int player_num, PlayerCardData playerCardData) {
        int dx = (int) (20 * this.getResources().getDisplayMetrics().density);
        int left_margin = 0;

        for (int meld_num_12 = 0; meld_num_12 < playerCardData.current_meld_cards.size(); meld_num_12++) {
            for (ArrayList<Integer> meld_card_indexes : playerCardData.current_meld_cards.get(meld_num_12)) {
                for (int hand_index : meld_card_indexes) {
                    int card_id = playerCardData.hand_card_pile.get(hand_index);
                    frameLayout = (FrameLayout) layoutInflater.inflate(R.layout.card_with_border, null);
                    ((ImageButton) frameLayout.findViewById(R.id.card_image)).setImageBitmap(id_to_bitmap[card_id]);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(CARD_WIDTH, CARD_HEIGHT);
                    layoutParams.setMargins(left_margin, 0, 0, 0);
                    frameLayout.setLayoutParams(layoutParams);
                    if (player_num == Constants.HUMAN) {
                        this.human_meld_cards_layout.addView(frameLayout);
                    } else {
                        this.computer_meld_cards_layout.addView(frameLayout);
                    }
                    left_margin += this.CARD_WIDTH;
                }
                left_margin += dx;
            }
        }
    }

    private void redraw_capture_cards(int player_num, PlayerCardData playerCardData) {
        int dx = (int) (20 * this.getResources().getDisplayMetrics().density);
        int left_margin = 0;

        for (int i = 0; i < playerCardData.capture_pile.size(); i++) {
            int card_id = playerCardData.hand_card_pile.get(i);
            frameLayout = (FrameLayout) layoutInflater.inflate(R.layout.card_with_border, null);
            ((ImageButton) frameLayout.findViewById(R.id.card_image)).setImageBitmap(id_to_bitmap[card_id]);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(CARD_WIDTH, CARD_HEIGHT);
            layoutParams.setMargins(left_margin, 0, 0, 0);
            frameLayout.setLayoutParams(layoutParams);
            if (player_num == Constants.HUMAN) {
                this.human_capture_cards_layout.addView(frameLayout);
            } else {
                this.computer_capture_cards_layout.addView(frameLayout);
            }
            left_margin += dx;
        }
    }

    private void redraw_stock_cards(ArrayList<Integer> stock_card_ids) {
        int dx = (int) (20 * this.getResources().getDisplayMetrics().density);
        int left_margin = 0;

        for (int i = 0; i < stock_card_ids.size(); i++) {
            int card_id = stock_card_ids.get(i);
            frameLayout = (FrameLayout) layoutInflater.inflate(R.layout.card_with_border, null);
            ((ImageButton) frameLayout.findViewById(R.id.card_image)).setImageBitmap(id_to_bitmap[card_id]);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(CARD_WIDTH, CARD_HEIGHT);
            layoutParams.setMargins(left_margin, 0, 0, 0);
            frameLayout.setLayoutParams(layoutParams);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.bringToFront();
                    clear_all_cards();
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    long start = System.currentTimeMillis();
                                    redraw_cards();
                                    long finish = System.currentTimeMillis();
                                    long timeElapsed = finish - start;
                                    System.out.println(timeElapsed);
                                }
                            },
                            300);
                    System.out.println("Removed Cadrdsda!!!!");
                }
            };
            frameLayout.setOnClickListener(onClickListener);
            this.stock_cards_layout.addView(frameLayout);
            left_margin += dx;
        }
    }

    private void clear_all_cards() {
        this.stock_cards_layout.removeAllViews();
        this.human_hand_cards_layout.removeAllViews();
        this.human_meld_cards_layout.removeAllViews();
        this.human_capture_cards_layout.removeAllViews();
        this.computer_hand_cards_layout.removeAllViews();
        this.computer_meld_cards_layout.removeAllViews();
        this.computer_capture_cards_layout.removeAllViews();
    }

    private void image_button_stuff() {
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.stock_cards);
        RelativeLayout hand_cards = (RelativeLayout) findViewById(R.id.computer_cards);

        int card_height = (int) (60 * this.getResources().getDisplayMetrics().density);
        int card_width = (int) ((691 * card_height) / 1056);
        layoutInflater = getLayoutInflater();
        frameLayout = (FrameLayout) layoutInflater.inflate(R.layout.card_with_border, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(card_width, card_height);
        layoutParams.setMargins(10, 0, 0, 0);
        frameLayout.setLayoutParams(layoutParams);
        rl.addView(frameLayout);

        frameLayout2 = (FrameLayout) layoutInflater.inflate(R.layout.card_with_border, null);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(card_width, card_height);
        layoutParams2.setMargins(100, 0, 0, 0);
        frameLayout2.setLayoutParams(layoutParams2);
        rl.addView(frameLayout2);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.findViewById(R.id.black_highlight).setVisibility(View.VISIBLE);
                try {
                    hand_cards.removeView(v);
                    rl.addView(v);
                } catch (Exception e) {
                }
                v.bringToFront();
                System.out.println("Clicked!!!");
                System.out.println(v.getId());
            }
        };
        frameLayout.setOnClickListener(onClickListener);
        frameLayout2.setOnClickListener(onClickListener);
    }
}