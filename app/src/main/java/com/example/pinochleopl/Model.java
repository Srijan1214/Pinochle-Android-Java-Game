package com.example.pinochleopl;

public class Model {
    private int[] round_scores;
    private int[] prev_cumulative_scores;

    private int cur_lead_player;
    private int trump_card;

    Human player_1;
    Computer player_2;
    Player[] players;
    Deck deck;

    public Model() {
        this.player_1 = new Human();
        this.player_2 = new Computer();
        this.players = new Player[]{player_1, player_2};

        this.round_scores = new int[]{0, 0};
        this.prev_cumulative_scores = new int[]{0, 0};
        this.deck = new Deck();
        this.decide_lead_through_coin_toss();
    }

    private void decide_lead_through_coin_toss() {
        this.cur_lead_player = 0;
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

    private void pick_up_trump_card() {
        this.trump_card = deck.pop_card_from_deck();
    }


}
