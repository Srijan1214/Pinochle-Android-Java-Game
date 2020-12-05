package com.example.pinochleopl;

import java.util.ArrayList;

public class Player {

    private static final int TOTAL_NO_OF_CARDS = Deck.TOTAL_NO_OF_CARDS;
    private static final int NO_OF_SAME_CARDS = Card.NO_OF_SAME_CARDS;
    private static final int NO_OF_CARDS_PER_SUIT = TOTAL_NO_OF_CARDS / 4;

    private ArrayList<Integer> hand_card_pile;
    private ArrayList<ArrayList<ArrayList<Integer>>> hand_meld_involvement_list;
    private ArrayList<ArrayList<ArrayList<Integer>>> current_meld_cards;
    private ArrayList<Integer> capture_pile;


    private int trump_card;

    public Player() {
        this.hand_card_pile = new ArrayList<Integer>();
        this.hand_meld_involvement_list = new ArrayList<ArrayList<ArrayList<Integer>>>();
        this.current_meld_cards = new ArrayList<ArrayList<ArrayList<Integer>>>(12);
        this.capture_pile = new ArrayList<Integer>();
    }

    public void give_card_to_player(int a_card_id) {
        this.hand_card_pile.add(a_card_id);
        this.hand_meld_involvement_list.add(new ArrayList<>());
    }

    public void add_cards_to_player_capture_pile(ArrayList<Integer> a_card_list) {
        for (Integer ele : a_card_list) {
            this.capture_pile.add(ele);
        }
    }

    public void setTrump_card(int trump_card) {
        this.trump_card = trump_card;
    }

    public int get_hand_card_from_index(int index) {
        return this.hand_card_pile.get(index);
    }

    public String get_hand_pile_string() {
        return "";
    }

    public void remove_card_from_pile(int card_index) {
        for (ArrayList<Integer> removal_index_to_meld_pointer_triplet : this.hand_meld_involvement_list.get(card_index)) {
            ArrayList<Integer> cards_to_remove_from_meld =
                    this.current_meld_cards.get(removal_index_to_meld_pointer_triplet.get(0)).get(removal_index_to_meld_pointer_triplet.get(0));
            for (int index_to_remove_from_meld : cards_to_remove_from_meld) {
                for (int i = 0; i < this.hand_meld_involvement_list.get(index_to_remove_from_meld).size(); i++) {
                    ArrayList<Integer> search_triplet = this.hand_meld_involvement_list.get(index_to_remove_from_meld).get(i);
                    if (search_triplet.get(0) == removal_index_to_meld_pointer_triplet.get(0)) {
                        if (!search_triplet.equals(removal_index_to_meld_pointer_triplet)) {
                            int temp_index = this.hand_meld_involvement_list.get(index_to_remove_from_meld).size() - 1;
                            this.hand_meld_involvement_list.get(index_to_remove_from_meld).set(i,
                                    this.hand_meld_involvement_list.get(index_to_remove_from_meld).get(temp_index)
                                    );
                            this.hand_meld_involvement_list.get(index_to_remove_from_meld).remove(this.hand_meld_involvement_list.get(index_to_remove_from_meld).size() - 1);
                        }
                    }
                }
            }
        }

        if(card_index != this.hand_card_pile.size() - 1) {
            for(ArrayList<Integer> ele:this.hand_meld_involvement_list.get(this.hand_meld_involvement_list.size() - 1)) {
                this.current_meld_cards.get(ele.get(0)).get(ele.get(1)).set(ele.get(2), card_index);
            }
        }

        this.hand_card_pile.set(card_index, this.hand_card_pile.get(this.hand_card_pile.size() - 1));
        this.hand_card_pile.remove(this.hand_card_pile.size() - 1);
        this.hand_meld_involvement_list.set(card_index, this.hand_meld_involvement_list.get(this.hand_meld_involvement_list.size() - 1));
        this.hand_meld_involvement_list.remove(this.hand_meld_involvement_list.size() - 1);
    }
}
