package com.example.pinochleopl;

import android.util.Pair;

import java.util.ArrayList;

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
    private ArrayList<Integer> turn_thrown_cards;

    private int no_of_turns_played;

    private boolean is_user_input_meld;

    private boolean trump_card_be_shown;

    private ModelState modelState;

    private String latest_message;

    public Model() {
        this.prev_cumulative_scores = new int[]{0, 0};
        this.decide_lead_through_coin_toss();
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

        this.round_scores = new int[]{0, 0};

        this.decide_lead_through_coin_toss();
        start_new_turn();
    }

    private void start_new_turn() {
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
        if(this.lead_player == Constants.COMPUTER) {
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
        if(meld_played != Melds.INVALID) {
            this.round_scores[Constants.COMPUTER] += Melds.get_meld_score(meld_played);
        }
        this.latest_message = message;
        this.modelState = ModelState.COMPUTER_PLAYED_MELD;
    }

    public void go_to_next_turn() {
        if(this.player_1.get_no_of_remaining_cards() == 0) {
            this.end_round();
        } else {
            this.start_new_turn();
        }
    }

    private void end_round(){

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

    private void decide_lead_through_coin_toss() {
        this.lead_player = Constants.HUMAN;
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

        if(meld_number != Melds.INVALID) {
            this.round_scores[Constants.HUMAN] += Melds.get_meld_score(meld_number);
            this.go_to_next_turn();
            return meld_number;
        }
        return -1;
    }

    public String handler_ask_help() {
        String help_msg = "";
        if(is_user_input_meld) {
            help_msg = this.player_1.get_meld_help_message();
        } else {
            help_msg = this.player_1.get_card_to_throw_help_message(this.turn_lead_card);
        }
        return help_msg;
    }

    public PlayerCardData[] get_players_card_data() {
        return new PlayerCardData[]{player_1.get_cards_for_drawing(), player_2.get_cards_for_drawing()};
    }

    public ArrayList<Integer> get_stock_data() {
        return this.deck.get_stock_pile();
    }

    private void pick_up_trump_card() {
        this.trump_card = deck.pop_card_from_deck();
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

    public int getTrump_card() {
        return trump_card;
    }
}
