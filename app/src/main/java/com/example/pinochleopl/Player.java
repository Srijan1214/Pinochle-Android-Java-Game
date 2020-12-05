package com.example.pinochleopl;

import java.util.ArrayList;
import java.util.Collections;

public class Player {

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

    public PlayerCardData get_cards_for_drawing() {
        PlayerCardData playerCardData = new PlayerCardData();
        playerCardData.hand_card_pile = this.hand_card_pile;
        playerCardData.hand_meld_involvement_list = this.hand_meld_involvement_list;
        playerCardData.current_meld_cards = this.current_meld_cards;
        playerCardData.capture_pile = this.capture_pile;
        return playerCardData;
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

    public String get_capture_pile_string() {
        return "";
    }

    public String get_meld_string() {
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

        if (card_index != this.hand_card_pile.size() - 1) {
            for (ArrayList<Integer> ele : this.hand_meld_involvement_list.get(this.hand_meld_involvement_list.size() - 1)) {
                this.current_meld_cards.get(ele.get(0)).get(ele.get(1)).set(ele.get(2), card_index);
            }
        }

        this.hand_card_pile.set(card_index, this.hand_card_pile.get(this.hand_card_pile.size() - 1));
        this.hand_card_pile.remove(this.hand_card_pile.size() - 1);
        this.hand_meld_involvement_list.set(card_index, this.hand_meld_involvement_list.get(this.hand_meld_involvement_list.size() - 1));
        this.hand_meld_involvement_list.remove(this.hand_meld_involvement_list.size() - 1);
    }

    public int get_meld_type_12_from_cards(ArrayList<Integer> card_ids) {
        Collections.sort(card_ids);
        final int TRUMP_SUIT = Card.get_suit_from_id(this.trump_card);

        //flush
        if (card_ids.size() == 5) {
            int TEN_FACE = Card.get_face_from_id(card_ids.get(0));
            int TEN_SUIT = Card.get_suit_from_id(card_ids.get(0));

            int JACK_FACE = Card.get_face_from_id(card_ids.get(1));
            int JACK_SUIT = Card.get_suit_from_id(card_ids.get(1));

            int QUEEN_FACE = Card.get_face_from_id(card_ids.get(2));
            int QUEEN_SUIT = Card.get_suit_from_id(card_ids.get(2));

            int KING_FACE = Card.get_face_from_id(card_ids.get(3));
            int KING_SUIT = Card.get_suit_from_id(card_ids.get(3));

            int ACE_FACE = Card.get_face_from_id(card_ids.get(4));
            int ACE_SUIT = Card.get_suit_from_id(card_ids.get(4));

            if ((TEN_SUIT == TRUMP_SUIT && JACK_SUIT == TRUMP_SUIT && QUEEN_SUIT == TRUMP_SUIT &&
                    KING_SUIT == TRUMP_SUIT && ACE_SUIT == TRUMP_SUIT) &&
                    (TEN_FACE == Card.TEN_FACE && JACK_FACE == Card.JACK_FACE &&
                            QUEEN_FACE == Card.QUEEN_FACE && KING_FACE == Card.KING_FACE &&
                            ACE_FACE == Card.ACE_FACE)) {
                return Melds.FLUSH;
            }
        } else if (card_ids.size() == 2) {
            int FIRST_FACE = Card.get_face_from_id(card_ids.get(0));
            int FIRST_SUIT = Card.get_suit_from_id(card_ids.get(0));

            int SECOND_FACE = Card.get_face_from_id(card_ids.get(1));
            int SECOND_SUIT = Card.get_suit_from_id(card_ids.get(1));

            if (FIRST_FACE == Card.QUEEN_FACE && SECOND_FACE == Card.KING_FACE) {
                if (SECOND_SUIT == TRUMP_SUIT && FIRST_SUIT == TRUMP_SUIT) {
                    return Melds.ROYAL_MARRIAGE;
                } else if (FIRST_SUIT == SECOND_SUIT) {
                    return Melds.ROYAL_MARRIAGE + 1 + FIRST_SUIT;
                }
            }
            if (FIRST_FACE == Card.QUEEN_FACE &&
                    SECOND_FACE == Card.JACK_FACE &&
                    FIRST_SUIT == Card.SPADES_SUIT &&
                    SECOND_SUIT == Card.DIAMONDS_SUIT) {
                return Melds.PINOCHLE + 3;
            }
        } else if (card_ids.size() == 1) {
            int FIRST_FACE = Card.get_face_from_id(card_ids.get(0));
            int FIRST_SUIT = Card.get_suit_from_id(card_ids.get(0));

            if (FIRST_FACE == Card.NINE_FACE && FIRST_SUIT == TRUMP_SUIT) {
                return Melds.DIX + 3;
            }
        } else if (card_ids.size() == 4) {
            int FIRST_FACE = Card.get_face_from_id(card_ids.get(0));
            int FIRST_SUIT = Card.get_suit_from_id(card_ids.get(0));

            int SECOND_FACE = Card.get_face_from_id(card_ids.get(1));
            int SECOND_SUIT = Card.get_suit_from_id(card_ids.get(1));

            int THIRD_FACE = Card.get_face_from_id(card_ids.get(2));
            int THIRD_SUIT = Card.get_suit_from_id(card_ids.get(2));

            int FOURTH_FACE = Card.get_face_from_id(card_ids.get(3));
            int FOURTH_SUIT = Card.get_suit_from_id(card_ids.get(3));

            if (FIRST_SUIT == Card.SPADES_SUIT &&
                    SECOND_SUIT == Card.CLUBS_SUIT &&
                    THIRD_SUIT == Card.HEARTS_SUIT &&
                    FOURTH_SUIT == Card.DIAMONDS_SUIT) {
                if (FIRST_FACE == Card.ACE_FACE &&
                        SECOND_FACE == Card.ACE_FACE &&
                        THIRD_FACE == Card.ACE_FACE &&
                        FOURTH_FACE == Card.ACE_FACE) {
                    return Melds.FOUR_ACES;
                }
                if (FIRST_FACE == Card.KING_FACE &&
                        SECOND_FACE == Card.KING_FACE &&
                        THIRD_FACE == Card.KING_FACE &&
                        FOURTH_FACE == Card.KING_FACE) {
                    return Melds.FOUR_KINGS;
                }
                if (FIRST_FACE == Card.QUEEN_FACE &&
                        SECOND_FACE == Card.QUEEN_FACE &&
                        THIRD_FACE == Card.QUEEN_FACE &&
                        FOURTH_FACE == Card.QUEEN_FACE) {
                    return Melds.FOUR_QUEENS;
                }
                if (FIRST_FACE == Card.JACK_FACE &&
                        SECOND_FACE == Card.JACK_FACE &&
                        THIRD_FACE == Card.JACK_FACE &&
                        FOURTH_FACE == Card.JACK_FACE) {
                    return Melds.FOUR_JACKS;
                }
            }
        }
        return Melds.INVALID;
    }
}
