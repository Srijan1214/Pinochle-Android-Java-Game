package com.example.pinochleopl;

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
    private int no_of_turns_played;


    private boolean should_trump_card_be_shown;
    public Model() {
        this.prev_cumulative_scores = new int[]{0, 0};
        this.deck = new Deck();
        this.decide_lead_through_coin_toss();
    }

    public void start_new_round(int round_number) {
        this.round_number = round_number;
        this.no_of_turns_played = 0;
        this.should_trump_card_be_shown = true;

        this.player_1 = new Human();
        this.player_2 = new Computer();
        this.players = new Player[]{player_1, player_2};

        this.round_scores = new int[]{0, 0};

        this.decide_lead_through_coin_toss();
        start_new_turn();
    }

    private void start_new_turn() {
        this.turn_number = 0;
        this.turn_lead_card = -1;

        if(this.no_of_turns_played != 0) {
            this.pick_up_cards_from_deck();
        }
        this.no_of_turns_played += 1;
        this.cur_player = this.lead_player;
        this.continue_turn_loop();
    }

    private void continue_turn_loop() {
        if(this.turn_number == 2) {
            this.finish_turn_after_all_cards_are_thrown();
            return;
        }
        
        if(this.cur_player == Constants.COMPUTER) {
            this.play_computer_turn();
        }
        
    }

    private void play_computer_turn() {
    }

    private void finish_turn_after_all_cards_are_thrown() {
    }

    private void pick_up_cards_from_deck() {
        if(this.deck.get_stock_size() == 1) {
            players[this.lead_player].give_card_to_player(deck.pop_card_from_deck());
            players[this.lead_player ^ 1].give_card_to_player(this.trump_card);
        }else if (this.deck.get_stock_size() != 0) {
            players[this.lead_player].give_card_to_player(deck.pop_card_from_deck());
            players[this.lead_player ^ 1].give_card_to_player(deck.pop_card_from_deck());
        }
    }

    private void decide_lead_through_coin_toss() {
        this.lead_player = Constants.HUMAN;
    }

    public void deal_cards_from_deck_to_players() {
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

    public PlayerCardData[] get_players_card_data() {
        return new PlayerCardData[]{player_1.get_cards_for_drawing(), player_2.get_cards_for_drawing()};
    }

    public ArrayList<Integer> get_stock_data() {
        return this.deck.get_stock_pile();
    }

    private void pick_up_trump_card() {
        this.trump_card = deck.pop_card_from_deck();
    }


}
