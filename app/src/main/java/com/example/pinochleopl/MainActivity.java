package com.example.pinochleopl;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textview.MaterialTextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

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

    private Bitmap[] id_to_bitmap;
    private HashMap<Integer, Integer> view_id_to_hand_index;
    private ArrayList<Integer> view_ids_selected;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        image_button_stuff();
        initialize_members();
        load_card_bitmaps();

        this.model.start_new_round(0);
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void redraw_cards() {
        clear_all_cards();
        this.view_ids_selected = new ArrayList<Integer>();
        this.view_id_to_hand_index = new HashMap<Integer, Integer>();

        if(this.model.is_user_input_state_meld()){
            hide_throw_button();
            show_meld_button();
        } else {
            hide_meld_button();
            show_throw_button();
        }

        if(this.model.getModelState() == ModelState.COMPUTER_THREW_CARD) {
            this.show_computer_threw_card_screen(this.model.getLatest_message());
        } else if (this.model.getModelState() == ModelState.COMPUTER_PLAYED_MELD) {
            this.show_computer_threw_meld_screen(this.model.getLatest_message());
        }

        PlayerCardData[] playerCardData = this.model.get_players_card_data();

        this.redraw_hand_cards(Constants.HUMAN, playerCardData[Constants.HUMAN]);
        this.redraw_hand_cards(Constants.COMPUTER, playerCardData[Constants.COMPUTER]);

        this.redraw_meld_cards(Constants.HUMAN, playerCardData[Constants.HUMAN]);
        this.redraw_meld_cards(Constants.COMPUTER, playerCardData[Constants.COMPUTER]);

        this.redraw_capture_cards(Constants.HUMAN, playerCardData[Constants.HUMAN]);
        this.redraw_capture_cards(Constants.COMPUTER, playerCardData[Constants.COMPUTER]);

        this.redraw_stock_cards(this.model.get_stock_data());

        this.draw_trump_card();
    }

    private void show_computer_threw_card_screen(String message) {
        findViewById(R.id.overlay).setVisibility(View.VISIBLE);
        findViewById(R.id.frame_computer_move_message).setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.text_computer_move)).setText(message);
    }

    private void show_computer_threw_meld_screen(String message) {
        findViewById(R.id.overlay).setVisibility(View.VISIBLE);
        findViewById(R.id.frame_computer_meld_message).setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.text_computer_meld)).setText(message);
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
            this.stock_cards_layout.addView(frameLayout);
            left_margin += dx;
        }
    }

    private void draw_trump_card(){
        if(this.model.isTrump_card_be_shown()) {
            ((ImageButton)(findViewById(R.id.trump_card_display).findViewById(R.id.card_image))).setImageBitmap(
                    id_to_bitmap[this.model.getTrump_card()]
            );
        } else {
            ((ImageButton)(findViewById(R.id.trump_card_display).findViewById(R.id.card_image))).setBackgroundResource(0);;
        }
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
    public void listener_play_card(View v) {
        if(this.view_ids_selected.isEmpty()) {
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

        if(this.model.handler_play_meld(indexes) == -1){
            ((MaterialTextView)findViewById(R.id.text_help)).setText("Invalid Meld");
        } else {
            ((MaterialTextView)findViewById(R.id.text_help)).setText("");
        }
        this.redraw_cards();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void listener_btn_skip_meld(View v) {
        ((MaterialTextView)findViewById(R.id.text_help)).setText("");
        this.model.go_to_next_turn();
        this.redraw_cards();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void listener_btn_ask_help(View v) {
        ((MaterialTextView)findViewById(R.id.text_help)).setText(
                this.model.handler_ask_help()
        );
    }
}