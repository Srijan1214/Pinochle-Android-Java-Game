package com.example.pinochleopl;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textview.MaterialTextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    LayoutInflater layoutInflater;
    FrameLayout frameLayout;

    RelativeLayout stock_cards_layout;
    RelativeLayout human_hand_cards_layout;
    RelativeLayout human_meld_cards_layout;
    RelativeLayout human_capture_cards_layout;
    RelativeLayout computer_hand_cards_layout;
    RelativeLayout computer_meld_cards_layout;
    RelativeLayout computer_capture_cards_layout;

    MaterialTextView text_human_scores;
    MaterialTextView text_computer_scores;
    MaterialTextView text_round_number;

    Model model;

    int CARD_HEIGHT;
    int CARD_WIDTH;

    private Bitmap[] id_to_bitmap;
    private Bitmap empty_card;
    private HashMap<Integer, Integer> view_id_to_hand_index;
    private int[] id_to_view_id;
    private ArrayList<Integer> view_ids_selected;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        boolean should_load_from_file = intent.getBooleanExtra(StartScreenActivity.INTENT_SHOULD_LOAD_FROM_FILE, false);
        String load_file_name = intent.getStringExtra(StartScreenActivity.INTENT_FILE_NAME);

        initialize_members();
        load_card_bitmaps();

        if (should_load_from_file) {
            this.model.load_game_from_file(load_file_name, getApplicationContext());
            this.model.start_new_turn();
        } else {
            this.model.start_new_round(1);
            this.show_coin_toss_frame();
        }

        redraw_cards();
//        deleteFile("asdasdasdasdasdq2233123123123");
    }

    private void initialize_members() {
        this.stock_cards_layout = (RelativeLayout) findViewById(R.id.stock_cards);
        this.human_hand_cards_layout = (RelativeLayout) findViewById(R.id.human_hand);
        this.human_meld_cards_layout = (RelativeLayout) findViewById(R.id.human_meld);
        this.human_capture_cards_layout = (RelativeLayout) findViewById(R.id.human_capture);
        this.computer_hand_cards_layout = (RelativeLayout) findViewById(R.id.computer_hand);
        this.computer_meld_cards_layout = (RelativeLayout) findViewById(R.id.computer_meld);
        this.computer_capture_cards_layout = (RelativeLayout) findViewById(R.id.computer_capture);


        this.text_human_scores = (MaterialTextView) findViewById(R.id.score_human);
        this.text_computer_scores = (MaterialTextView) findViewById(R.id.score_computer);
        text_round_number = (MaterialTextView) findViewById(R.id.text_round_number);

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

        empty_card = BitmapFactory.decodeResource(getResources(), R.drawable.empty_card);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void redraw_cards() {
        clear_all_cards();
        this.view_ids_selected = new ArrayList<Integer>();
        this.view_id_to_hand_index = new HashMap<Integer, Integer>();
        this.id_to_view_id = new int[Constants.TOTAL_NO_OF_CARDS];

        if (this.model.is_user_input_state_meld()) {
            hide_throw_button();
            show_meld_button();
        } else {
            hide_meld_button();
            show_throw_button();
        }

        if (this.model.getModelState() == ModelState.COMPUTER_THREW_CARD) {
            this.show_computer_threw_card_screen(this.model.getLatest_message());
        } else if (this.model.getModelState() == ModelState.COMPUTER_PLAYED_MELD) {
            this.show_computer_threw_meld_screen(this.model.getLatest_message());
        } else if (this.model.getModelState() == ModelState.ROUND_ENDED) {
            this.show_round_end_screen();
        }

        PlayerCardData[] playerCardData = this.model.get_players_card_data();

        this.redraw_hand_cards(Constants.HUMAN, playerCardData[Constants.HUMAN]);
        this.redraw_hand_cards(Constants.COMPUTER, playerCardData[Constants.COMPUTER]);

        this.redraw_meld_cards(Constants.HUMAN, playerCardData[Constants.HUMAN]);
        this.redraw_meld_cards(Constants.COMPUTER, playerCardData[Constants.COMPUTER]);

        this.redraw_capture_cards(Constants.HUMAN, playerCardData[Constants.HUMAN]);
        this.redraw_capture_cards(Constants.COMPUTER, playerCardData[Constants.COMPUTER]);

        this.redraw_stock_cards(this.model.get_stock_data());

        this.redraw_scores();
        this.redraw_round_number();
        this.redraw_turn_thrown_cards();
        this.draw_trump_card();

        if (this.model.getModelState() != ModelState.PLAYED_INVALID_MELD) {
            this.clear_help_message();
        }
    }

    private void show_computer_threw_card_screen(String message) {
        findViewById(R.id.overlay).setVisibility(View.VISIBLE);
        findViewById(R.id.frame_computer_move_message).setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.text_computer_move)).setText("Computer played " + message);
    }

    private void show_computer_threw_meld_screen(String message) {
        findViewById(R.id.overlay).setVisibility(View.VISIBLE);
        findViewById(R.id.frame_computer_meld_message).setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.text_computer_meld)).setText("Computer played " + message);
    }

    private void show_round_end_screen() {
        findViewById(R.id.overlay).setVisibility(View.VISIBLE);
        findViewById(R.id.frame_round_end).setVisibility(View.VISIBLE);

        String message = "";
        int human_round_score = this.model.getRound_scores()[Constants.HUMAN];
        int computer_round_score = this.model.getRound_scores()[Constants.COMPUTER];

        message += ("Human Round Score: " + human_round_score + ", Computer Round Score: " +
                computer_round_score);
        if (human_round_score > computer_round_score) {
            message += " Human Won Round";
        } else if (computer_round_score > human_round_score) {
            message += " Computer Won Round";
        } else {
            message += " Round Draw";
        }
        ((TextView) findViewById(R.id.text_round_end_message)).setText(
                message
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void redraw_hand_cards(int player_num, PlayerCardData playerCardData) {
        int dx = (int) (30 * this.getResources().getDisplayMetrics().density);
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
                frameLayout.setOnClickListener(this.listener_card_click());
                int id = View.generateViewId();
                frameLayout.setId(id);
                this.view_id_to_hand_index.put(id, i);
                this.id_to_view_id[i] = id;
                this.human_hand_cards_layout.addView(frameLayout);
                System.out.println(human_hand_cards_layout.getId());

            } else {
                this.computer_hand_cards_layout.addView(frameLayout);
            }
            left_margin += dx;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
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
                        frameLayout.setOnClickListener(this.listener_card_click());
                        int id = View.generateViewId();
                        frameLayout.setId(id);
                        this.view_id_to_hand_index.put(id, hand_index);
                        this.id_to_view_id[hand_index] = id;
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
            int card_id = playerCardData.capture_pile.get(i);
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
            this.stock_cards_layout.addView(frameLayout);
            left_margin += dx;
        }
    }

    private void draw_trump_card() {
        if (this.model.isTrump_card_be_shown()) {
            ((ImageButton) (findViewById(R.id.trump_card_display).findViewById(R.id.card_image))).setImageBitmap(
                    id_to_bitmap[this.model.getTrump_card()]
            );
        } else {
            ((ImageButton) (findViewById(R.id.trump_card_display).findViewById(R.id.card_image))).setImageBitmap(empty_card);
        }
    }

    private void redraw_scores() {
        this.text_human_scores.setText(
                "Human Scores: " + this.model.getPrev_cumulative_scores()[0] + " / " + this.model.getRound_scores()[0]
        );

        this.text_computer_scores.setText(
                "Computer Scores: " + this.model.getPrev_cumulative_scores()[1] + " / " + this.model.getRound_scores()[1]
        );
    }

    private void redraw_round_number() {
        this.text_round_number.setText(
                "Round Number: " + this.model.getRound_number()
        );
    }

    private void redraw_turn_thrown_cards() {
        ArrayList<Integer> turn_thrown_cards = this.model.getTurn_thrown_cards();

        if (turn_thrown_cards.size() == 0) {
            ((ImageButton) findViewById(R.id.card_thrown_1).findViewById(R.id.card_image)).setImageBitmap(
                    this.empty_card
            );
            ((ImageButton) findViewById(R.id.card_thrown_2).findViewById(R.id.card_image)).setImageBitmap(
                    this.empty_card
            );
        } else if (turn_thrown_cards.size() == 1) {
            ((ImageButton) findViewById(R.id.card_thrown_1).findViewById(R.id.card_image)).setImageBitmap(
                    this.id_to_bitmap[turn_thrown_cards.get(0)]
            );
            ((ImageButton) findViewById(R.id.card_thrown_2).findViewById(R.id.card_image)).setImageBitmap(
                    this.empty_card
            );
        } else if (turn_thrown_cards.size() == 2) {
            ((ImageButton) findViewById(R.id.card_thrown_1).findViewById(R.id.card_image)).setImageBitmap(
                    this.id_to_bitmap[turn_thrown_cards.get(0)]
            );
            ((ImageButton) findViewById(R.id.card_thrown_2).findViewById(R.id.card_image)).setImageBitmap(
                    this.id_to_bitmap[turn_thrown_cards.get(1)]
            );
        }
    }

    private void clear_help_message() {
        ((MaterialTextView) findViewById(R.id.text_help)).setText("");
    }

    private View.OnClickListener listener_card_click() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!view_ids_selected.contains(v.getId())) {
                    if (!model.is_user_input_state_meld()) {
                        for (int view_id : view_ids_selected) {
                            view_ids_selected.remove(new Integer(view_id));
                            findViewById(view_id).findViewById(R.id.black_highlight).setVisibility(View.INVISIBLE);
                        }
                    }
                    view_ids_selected.add(v.getId());
                    v.findViewById(R.id.black_highlight).setVisibility(View.VISIBLE);
                    v.bringToFront();
                    System.out.println("highlight");
                } else {
                    view_ids_selected.remove(new Integer(v.getId()));
                    v.findViewById(R.id.black_highlight).setVisibility(View.INVISIBLE);
                    System.out.println("remove");
                }
            }
        };
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

    private void hide_meld_button() {
        findViewById(R.id.button_play_meld).setVisibility(View.INVISIBLE);
        findViewById(R.id.button_skip_meld).setVisibility(View.INVISIBLE);
    }

    private void show_coin_toss_frame() {
        findViewById(R.id.overlay).setVisibility(View.VISIBLE);
        findViewById(R.id.frame_coin_toss).setVisibility(View.VISIBLE);
    }

    private void show_meld_button() {
        findViewById(R.id.button_play_meld).setVisibility(View.VISIBLE);
        findViewById(R.id.button_skip_meld).setVisibility(View.VISIBLE);
    }

    private void hide_throw_button() {
        findViewById(R.id.button_play_selected_cards).setVisibility(View.INVISIBLE);
    }

    private void show_throw_button() {
        findViewById(R.id.button_play_selected_cards).setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void listener_coin_toss_input(View v) {
        findViewById(R.id.coin_toss_buttons).setVisibility(View.INVISIBLE);
        findViewById(R.id.coin_toss_result).setVisibility(View.VISIBLE);

        this.model.decide_lead_through_coin_toss();

        if (this.model.getLead_player() == Constants.COMPUTER) {
            ((TextView) findViewById(R.id.coin_toss_result_prompt)).setText("Computer Won Coin Toss");
        } else {
            ((TextView) findViewById(R.id.coin_toss_result_prompt)).setText("Human Won Coin Toss");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void listener_coin_toss_continue(View v) {
        findViewById(R.id.coin_toss_buttons).setVisibility(View.VISIBLE);
        findViewById(R.id.coin_toss_result).setVisibility(View.INVISIBLE);
        findViewById(R.id.frame_coin_toss).setVisibility(View.INVISIBLE);
        findViewById(R.id.overlay).setVisibility(View.INVISIBLE);


        this.model.start_new_turn();
        this.redraw_cards();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void listener_play_card(View v) {
        if (this.view_ids_selected.isEmpty()) {
            return;
        }
        int view_id = this.view_ids_selected.get(0);
        int card_index = this.view_id_to_hand_index.get(view_id);
        this.model.handler_throw_card(card_index);
        this.redraw_cards();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void listener_btn_computer_throw_continue(View v) {
        findViewById(R.id.overlay).setVisibility(View.INVISIBLE);
        findViewById(R.id.frame_computer_move_message).setVisibility(View.INVISIBLE);


        System.out.println("cllllllll");
        this.model.continue_turn_loop();
        this.redraw_cards();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void listener_btn_computer_meld_continue(View v) {
        findViewById(R.id.overlay).setVisibility(View.INVISIBLE);
        findViewById(R.id.frame_computer_meld_message).setVisibility(View.INVISIBLE);


        this.model.go_to_next_turn();
        this.redraw_cards();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void listener_btn_play_melds(View v) {
        ArrayList<Integer> indexes = (ArrayList<Integer>) this.view_ids_selected.clone();
        for (int i = 0; i < indexes.size(); i++) {
            indexes.set(i, this.view_id_to_hand_index.get(indexes.get(i)));
        }

        if (this.model.handler_play_meld(indexes) == -1) {
            ((MaterialTextView) findViewById(R.id.text_help)).setText("Invalid Meld");
        } else {
            ((MaterialTextView) findViewById(R.id.text_help)).setText("");
        }
        this.redraw_cards();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void listener_btn_skip_meld(View v) {
        ((MaterialTextView) findViewById(R.id.text_help)).setText("");
        this.model.go_to_next_turn();
        this.redraw_cards();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void listener_btn_ask_help(View v) {
        Pair<String, ArrayList<Integer>> help_message = this.model.handler_ask_help();

        if (!help_message.second.isEmpty()) {
            for (int card_index : help_message.second) {
                findViewById(this.id_to_view_id[card_index]).findViewById(
                        R.id.green_highlight).setVisibility(View.VISIBLE);
            }
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (int card_index : help_message.second) {
                        findViewById(id_to_view_id[card_index]).findViewById(
                                R.id.green_highlight).setVisibility(View.INVISIBLE);
                    }
                }
            }, 500);
        }
        ((MaterialTextView) findViewById(R.id.text_help)).setText(
                help_message.first
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void listener_btn_another_round(View v) {
        findViewById(R.id.overlay).setVisibility(View.INVISIBLE);
        findViewById(R.id.frame_round_end).setVisibility(View.INVISIBLE);

        this.model.start_new_round(this.model.getRound_number() + 1);
        this.redraw_cards();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void listener_btn_end_game(View v) {
        findViewById(R.id.overlay).setVisibility(View.VISIBLE);
        findViewById(R.id.frame_game_end).setVisibility(View.VISIBLE);

        int human_cumulative_score = this.model.getPrev_cumulative_scores()[Constants.HUMAN];
        int computer_cumulative_score = this.model.getPrev_cumulative_scores()[Constants.COMPUTER];

        String message = "Total Human Score: " + human_cumulative_score +
                " Total Computer Score: " + computer_cumulative_score;
        if (human_cumulative_score > computer_cumulative_score) {
            message += " Human Won";
        } else if (computer_cumulative_score > human_cumulative_score) {
            message += " Computer Won";
        } else {
            message += " Game Draw";
        }

        ((TextView) findViewById(R.id.text_game_end)).setText(message);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void listener_btn_quit_game(View v) {
        findViewById(R.id.overlay).setVisibility(View.VISIBLE);
        findViewById(R.id.frame_quit_game).setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void listener_btn_quit_game_yes(View v) {
        this.finish();
        System.exit(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void listener_btn_quit_game_no(View v) {
        findViewById(R.id.overlay).setVisibility(View.INVISIBLE);
        findViewById(R.id.frame_quit_game).setVisibility(View.INVISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void listener_btn_save_game(View v) {
        findViewById(R.id.overlay).setVisibility(View.VISIBLE);
        findViewById(R.id.frame_save_game).setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void listener_btn_save_no(View v) {
        findViewById(R.id.overlay).setVisibility(View.INVISIBLE);
        findViewById(R.id.frame_save_game).setVisibility(View.INVISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void listener_btn_save_yes(View v) {
        String message = ((TextView) findViewById(R.id.save_text_input)).getText().toString();

        this.model.save_game(message.trim(), getApplicationContext());
        this.finish();
        System.exit(0);
    }
}