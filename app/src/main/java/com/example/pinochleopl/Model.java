package com.example.pinochleopl;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class Model {
    private int[] round_scores;
    private int[] prev_cumulative_scores;

    private int lead_player;
    private int cur_player;

    private int trump_card;

    private Human player_1;
    private Computer player_2;
    private Player[] players;
    private Deck deck;

    private int round_number;
    private int turn_number;
    private int turn_lead_card;

    public ArrayList<Integer> getTurn_thrown_cards() {
        return (ArrayList<Integer>) turn_thrown_cards.clone();
    }

    private ArrayList<Integer> turn_thrown_cards;

    private int no_of_turns_played;

    private boolean is_user_input_meld;

    private boolean trump_card_be_shown;

    private ModelState modelState;

    private String latest_message;

    public Model() {
        this.prev_cumulative_scores = new int[]{0, 0};
//        this.decide_lead_through_coin_toss();
    }

    public void start_new_round(int round_number) {
        this.round_number = round_number;
        this.no_of_turns_played = 0;
        this.trump_card_be_shown = true;

        this.deck = new Deck();
        this.player_1 = new Human();
        this.player_2 = new Computer();
        this.players = new Player[]{player_1, player_2};

        this.deal_cards_from_deck_to_players();
        this.modelState = ModelState.HUMAN_THROWING_CARD;

        this.turn_thrown_cards = new ArrayList<Integer>();

        this.round_scores = new int[]{0, 0};

    }

    public void start_new_turn() {
        this.turn_number = 0;
        this.turn_thrown_cards = new ArrayList<Integer>();
        this.turn_lead_card = -1;
        this.is_user_input_meld = false;

        if (this.no_of_turns_played != 0) {
            this.pick_up_cards_from_deck();
        }
        this.no_of_turns_played += 1;
        this.cur_player = this.lead_player;
        this.continue_turn_loop();
    }

    public void continue_turn_loop() {
        if (this.turn_number == 2) {
            this.finish_turn_after_all_cards_are_thrown();
            return;
        }

        this.cur_player = (this.lead_player + this.turn_number) % 2;
        if (this.cur_player == Constants.COMPUTER) {
            this.play_computer_turn();
        } else {
            this.modelState = ModelState.HUMAN_THROWING_CARD;
            this.is_user_input_meld = false;
        }

    }

    private void play_computer_turn() {
        Pair<String, Integer> computer_move = this.player_2.throw_most_suitable_card(this.turn_lead_card);
        String message = computer_move.first;
        int thrown_card = computer_move.second;

        this.turn_thrown_cards.add(thrown_card);
        this.turn_lead_card = thrown_card;
        this.turn_number += 1;
        this.modelState = ModelState.COMPUTER_THREW_CARD;
        this.latest_message = message;
        return;
    }

    private void finish_turn_after_all_cards_are_thrown() {
        this.is_user_input_meld = true;
        if (this.turn_lead_card == -1 ||
                Card.is_first_card_greater_than_lead(
                        this.turn_thrown_cards.get(1), this.turn_thrown_cards.get(0), this.trump_card
                )
        ) {
            this.lead_player = this.lead_player ^ 1;
        }
        for (int thrown_card : this.turn_thrown_cards) {
            this.round_scores[this.lead_player] += Card.get_face_weight_from_id(thrown_card);
        }
        this.players[this.lead_player].add_cards_to_player_capture_pile(this.turn_thrown_cards);

        //Determine who plays meld
        if (this.lead_player == Constants.COMPUTER) {
            this.computer_play_meld();
        } else {
            this.modelState = ModelState.HUMAN_PLAYING_MELD;
            this.is_user_input_meld = true;
        }
    }

    private void computer_play_meld() {
        Pair<String, Integer> played_pair = this.player_2.play_best_meld();
        String message = played_pair.first;
        int meld_played = played_pair.second;
        if (meld_played != Melds.INVALID) {
            this.round_scores[Constants.COMPUTER] += Melds.get_meld_score(meld_played);
        }
        this.latest_message = message;
        this.modelState = ModelState.COMPUTER_PLAYED_MELD;
    }

    public void go_to_next_turn() {
        if (this.player_1.get_no_of_remaining_cards() == 0) {
            this.end_round();
        } else {
            this.start_new_turn();
        }
    }

    private void end_round() {
        this.modelState = ModelState.ROUND_ENDED;
        this.prev_cumulative_scores[Constants.HUMAN] = this.round_scores[Constants.HUMAN];
        this.prev_cumulative_scores[Constants.COMPUTER] = this.round_scores[Constants.COMPUTER];
    }

    private void pick_up_cards_from_deck() {
        if (this.deck.get_stock_size() == 1) {
            players[this.lead_player].give_card_to_player(deck.pop_card_from_deck());
            players[this.lead_player ^ 1].give_card_to_player(this.trump_card);
            this.trump_card_be_shown = false;
        } else if (this.deck.get_stock_size() != 0) {
            players[this.lead_player].give_card_to_player(deck.pop_card_from_deck());
            players[this.lead_player ^ 1].give_card_to_player(deck.pop_card_from_deck());
        }
    }

    public void decide_lead_through_coin_toss() {
        this.lead_player = new Random().nextInt(2);
    }

    private void deal_cards_from_deck_to_players() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                this.players[0].give_card_to_player(this.deck.pop_card_from_deck());
            }

            for (int j = 0; j < 4; j++) {
                this.players[1].give_card_to_player(this.deck.pop_card_from_deck());
            }
        }
        this.pick_up_trump_card();
    }

    public void handler_throw_card(int card_index) {
        int thrown_card_id = this.player_1.get_hand_card_from_index(card_index);
        this.player_1.remove_card_from_pile(card_index);
        this.turn_thrown_cards.add(thrown_card_id);
        this.turn_number += 1;
        if (this.turn_lead_card == -1 || Card.is_first_card_greater_than_lead(
                thrown_card_id, this.turn_lead_card, this.trump_card)
        ) {
            this.turn_lead_card = thrown_card_id;
        }
        this.continue_turn_loop();
    }

    public int handler_play_meld(ArrayList<Integer> hand_indexes) {
        int meld_number = this.player_1.perform_meld_if_valid(hand_indexes);

        if (meld_number != Melds.INVALID) {
            this.round_scores[Constants.HUMAN] += Melds.get_meld_score(meld_number);
            this.go_to_next_turn();
            return meld_number;
        }
        this.modelState = ModelState.PLAYED_INVALID_MELD;
        return -1;
    }

    public Pair<String, ArrayList<Integer>> handler_ask_help() {
        if (is_user_input_meld) {
            return this.player_1.get_meld_help_message();
        } else {
            return this.player_1.get_card_to_throw_help_message(this.turn_lead_card);
        }
    }

    public void save_game(String file_name, Context ctx) {
        FileOutputStream fos = null;
        try {
            fos = (ctx.openFileOutput(file_name, Context.MODE_PRIVATE));
            fos.write(get_save_string().getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String get_save_string() {
        String ret_string = "";
        ret_string += ("Round: " + this.round_number + "\n\n");

        ret_string += "Computer:\n";
        ret_string += ("Score: " + this.prev_cumulative_scores[Constants.COMPUTER]
                + " / " + this.round_scores[Constants.COMPUTER] + "\n");
        ret_string += ("   Hand: " + this.player_2.get_hand_pile_string() + "\n");
        ret_string += ("   Capture Pile: " + this.player_2.get_capture_pile_string() + "\n");
        ret_string += ("   Melds: " + this.player_2.get_meld_string() + "\n\n");

        ret_string += "Human:\n";
        ret_string += ("Score: " + this.prev_cumulative_scores[Constants.HUMAN]
                + " / " + this.round_scores[Constants.HUMAN] + "\n");
        ret_string += ("   Hand: " + this.player_1.get_hand_pile_string() + "\n");
        ret_string += ("   Capture Pile: " + this.player_1.get_capture_pile_string() + "\n");
        ret_string += ("   Melds: " + this.player_1.get_meld_string() + "\n\n");

        if (this.trump_card_be_shown) {
            ret_string += ("Trump Card: " + Card.get_string_from_id(trump_card) + "\n");
        } else {
            ret_string += ("Trump Card: " + Card.get_string_from_id(trump_card).substring(1, 2) + "\n");
        }

        ret_string += ("Stock: " + this.deck.get_stock_string() + "\n\n");

        ret_string += ("Next Player: ");
        if (this.lead_player == Constants.COMPUTER) {
            ret_string += "Computer";
        } else {
            ret_string += "Human";
        }
        return ret_string;
    }

    public void load_game_from_file(String file_name, Context ctx) {
        FileInputStream fis = null;

        try {
            fis = ctx.openFileInput(file_name);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);


            load_game_from_string(br);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void load_game_from_string(BufferedReader br) throws IOException {
        this.deck = new Deck();
        this.player_1 = new Human();
        this.player_2 = new Computer();
        this.players = new Player[]{player_1, player_2};
        this.round_scores = new int[]{0, 0};
        class LambdaFunctions {
            public int find_first_not_of(String string, char the_char, int start) {
                for (int i = start; i < string.length(); i++) {
                    if (string.charAt(i) != the_char) {
                        return i;
                    }
                }
                return -1;
            }

            public int find_last_not_of(String string, char the_char) {
                for (int i = string.length() - 1; i >= 0; i--) {
                    if (string.charAt(i) != the_char) {
                        return i;
                    }
                }
                return -1;
            }

            public Pair<Integer, Integer> get_score_pair_from_string(String line) {
                line = line.trim();
                int start = line.indexOf(':') + 1;
                start = find_first_not_of(line, ' ', start);
                int end = line.indexOf(' ', start);
                String num_str = line.substring(start, end);
                int ret_first = Integer.parseInt(num_str.trim());
                start = find_first_not_of(line, ' ', end);
                start = find_first_not_of(line, ' ', start + 1);
                num_str = line.substring(start, line.length());
                int ret_second = Integer.parseInt(num_str.trim());
                return new Pair<Integer, Integer>(ret_first, ret_second);
            }
        }
        LambdaFunctions lambdaFunctions = new LambdaFunctions();

        ArrayList<String> computer_load_lines = new ArrayList<String>();
        ArrayList<String> human_load_lines = new ArrayList<String>();
        String stock_load_string = "";

        String line;
        int line_number = 1;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line_number == 1) {
                int start = line.indexOf(':') + 1;
                String num_str = line.substring(start, line.length());
                this.round_number = Integer.parseInt(num_str.trim());
            } else if (line_number == 4) {
                Pair<Integer, Integer> temp = lambdaFunctions.get_score_pair_from_string(line);
                this.prev_cumulative_scores[Constants.COMPUTER] = temp.first;
                this.round_scores[Constants.COMPUTER] = temp.second;
            } else if (line_number == 10) {
                Pair<Integer, Integer> temp = lambdaFunctions.get_score_pair_from_string(line);
                this.prev_cumulative_scores[Constants.HUMAN] = temp.first;
                this.round_scores[Constants.HUMAN] = temp.second;
            } else if (line_number == 15) {
                int start = line.indexOf(':') + 1;
                start = lambdaFunctions.find_first_not_of(line, ' ', start);
                String card_str = line.substring(start, line.length());
                if(card_str.length() == 1){
                    card_str = "A" + card_str;
                    this.trump_card_be_shown = false;
                } else {
                    this.trump_card_be_shown = true;
                }
                this.trump_card = Card.get_id_from_string(card_str);
            } else if (line_number == 16) {
                int start = line.indexOf(':') + 1;
                start = lambdaFunctions.find_first_not_of(line, ' ', start);
                int end = lambdaFunctions.find_last_not_of(line, ' ');
                if (start != -1) {
                    stock_load_string = line.substring(start, end + 1);
                }
            } else if (line_number == 18) {
                int start = line.indexOf(':') + 1;
                start = lambdaFunctions.find_first_not_of(line, ':', start) + 1;
                int end = lambdaFunctions.find_last_not_of(line, ' ') + 1;
                String turn_str = line.substring(start, end);
                if(turn_str.equals("Human")) {
                    this.lead_player = Constants.HUMAN;
                } else {
                    this.lead_player = Constants.COMPUTER;
                }
            } else if (line_number >= 5 && line_number < 8) {
                computer_load_lines.add(line.trim());
            } else if (line_number >= 11 && line_number < 14) {
                human_load_lines.add(line.trim());
            }
            line_number++;
        }

        boolean[] cards_that_have_been_used = new boolean[Constants.TOTAL_NO_OF_CARDS];
        for (int i = 0; i < cards_that_have_been_used.length; i++) {
            cards_that_have_been_used[i] = false;
        }
        cards_that_have_been_used[this.trump_card] = true;
        for(Player player: this.players) {
            player.setTrump_card(this.trump_card);
        }
        this.player_1.load_members_from_serialization_string(human_load_lines, cards_that_have_been_used);
        this.player_2.load_members_from_serialization_string(computer_load_lines, cards_that_have_been_used);
        this.deck.load_stock_pile_from_string(stock_load_string, cards_that_have_been_used);

    }

    public PlayerCardData[] get_players_card_data() {
        return new PlayerCardData[]{player_1.get_cards_for_drawing(), player_2.get_cards_for_drawing()};
    }

    public ArrayList<Integer> get_stock_data() {
        return this.deck.get_stock_pile();
    }

    private void pick_up_trump_card() {
        this.trump_card = deck.pop_card_from_deck();
        this.player_1.setTrump_card(this.trump_card);
        this.player_2.setTrump_card(this.trump_card);
    }

    public int[] getRound_scores() {
        return round_scores;
    }

    public int[] getPrev_cumulative_scores() {
        return prev_cumulative_scores;
    }

    public boolean is_user_input_state_meld() {
        return is_user_input_meld;
    }

    public ModelState getModelState() {
        return modelState;
    }

    public String getLatest_message() {
        return latest_message;
    }

    public boolean isTrump_card_be_shown() {
        return trump_card_be_shown;
    }

    public int getRound_number() {
        return round_number;
    }

    public int getTrump_card() {
        return trump_card;
    }

    public int getLead_player() {
        return lead_player;
    }
}
