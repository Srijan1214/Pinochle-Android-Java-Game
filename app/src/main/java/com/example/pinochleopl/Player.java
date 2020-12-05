package com.example.pinochleopl;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;

public class Player {

    private ArrayList<Integer> hand_card_pile;
    private ArrayList<ArrayList<ArrayList<Integer>>> hand_meld_involvement_list;
    private ArrayList<ArrayList<ArrayList<Integer>>> current_meld_cards;
    private ArrayList<Integer> capture_pile;

    private boolean[][] which_card_used_for_meld;


    private int trump_card;

    public Player() {
        this.hand_card_pile = new ArrayList<Integer>();
        this.hand_meld_involvement_list = new ArrayList<ArrayList<ArrayList<Integer>>>();
        this.current_meld_cards = new ArrayList<ArrayList<ArrayList<Integer>>>(12);
        this.capture_pile = new ArrayList<Integer>();

        which_card_used_for_meld = new boolean[9][Constants.TOTAL_NO_OF_CARDS];
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

    public int get_no_of_remaining_cards() {
        return this.hand_card_pile.size();
    }

    public ArrayList<Integer> pop_all_cards_from_capture_pile() {
        ArrayList<Integer> ret_list = (ArrayList<Integer>) this.capture_pile.clone();
        this.capture_pile.clear();
        return ret_list;
    }

    public void reset_meld_history() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < Constants.TOTAL_NO_OF_CARDS; j++) {
                this.which_card_used_for_meld[i][j] = false;
            }
        }
    }

    public ArrayList<Integer> get_ids_from_indexes(ArrayList<Integer> card_indexes) {
        ArrayList<Integer> ret_list = (ArrayList<Integer>) card_indexes.clone();
        for (int i = 0; i < ret_list.size(); i++) {
            ret_list.set(i, this.hand_card_pile.get(ret_list.get(i)));
        }
        return ret_list;
    }

    private void play_meld(ArrayList<Integer> card_indexes, int meld_no_12) {
        for (int i = 0; i < card_indexes.size(); i++) {
            int card_index = card_indexes.get(i);
            int number_of_similar_melds = this.current_meld_cards.get(meld_no_12).size();
            this.hand_meld_involvement_list.get(card_index).add(
                    new ArrayList<Integer>(
                            Arrays.asList(new Integer[]{meld_no_12, number_of_similar_melds, i})));
            ArrayList<Integer> card_ids = (ArrayList<Integer>) card_indexes.clone();
            for (int j = 0; j < card_ids.size(); j++) {
                card_ids.set(i, this.hand_card_pile.get(card_ids.get(i)));
            }
            this.update_meld_history(card_ids, to_9(meld_no_12));
        }
    }

    private void update_meld_history(ArrayList<Integer> card_ids, int meld_number_9) {
        for (int card_id : card_ids) {
            this.which_card_used_for_meld[meld_number_9][card_id] = true;
            if (meld_number_9 == Melds.FLUSH) {
                this.which_card_used_for_meld[Melds.ROYAL_MARRIAGE][card_id] = true;
            }
        }
    }

    private Pair<Integer, Integer> find_index_meld_pair_of_card_to_throw() {
        int[][] meld_logic_vector = new int[12][Constants.TOTAL_NO_OF_CARDS];

        this.add_to_meld_logic_vector(meld_logic_vector, this.hand_card_pile);

        this.update_logic_vector_with_history(meld_logic_vector);

        Pair<Integer, Integer> ret_pair = this.get_best_meldCardIndexPair_from_Logic(meld_logic_vector);

        if (ret_pair.second.intValue() == Melds.INVALID) {
            return new Pair<Integer, Integer>(-1, Melds.INVALID);
        }
        return ret_pair;
    }

    private void add_to_meld_logic_vector(int[][] meld_logic_vector, ArrayList<Integer> card_pile) {
        for (int i = 0; i < 12; i++) {
            for (int card_id : card_pile) {
                if (this.which_card_used_for_meld[to_9(i)][card_id] == false) {
                    if (meld_logic_vector[i][card_id] >= 0) {
                        meld_logic_vector[i][card_id] += 1;
                    }
                }
            }
        }
    }

    private Pair<Integer, Integer> get_best_meldCardIndexPair_from_Logic(int[][] meld_logic_vector) {
        int max_meld_score = -1111111;
        int best_meld_number = Melds.INVALID;
        int best_meld_index = -1;
        int best_card_weight = -1;

        for (int index = 0; index < this.hand_card_pile.size(); index++) {
            int card_id = this.hand_card_pile.get(index);
            int meld_number_played = this.get_best_meld_card_if_thrown(meld_logic_vector, card_id);

            if (meld_number_played != -1) {
                int cur_score = Melds.get_meld_score(meld_number_played);
                if (cur_score > max_meld_score) {
                    max_meld_score = cur_score;
                    best_meld_number = meld_number_played;
                    best_meld_index = index;
                    best_card_weight = this.get_card_weight(card_id);
                } else if (cur_score == max_meld_score) {
                    int cur_card_weight = this.get_card_weight(card_id);
                    if(cur_card_weight > best_card_weight) {
                        max_meld_score = cur_score;
                        best_meld_number = meld_number_played;
                        best_meld_index = index;
                        best_card_weight = cur_card_weight;
                    }
                }
            }
        }
        return new Pair<Integer, Integer>(best_meld_index, best_meld_number);
    }

    private int get_card_weight(int card_id) {
        final int FACE_WEIGHT = Card.get_face_weight_from_id(card_id);
        final int TRUMP_SUIT = Card.get_suit_from_id(this.trump_card);

        if (Card.get_suit_from_id(card_id) == TRUMP_SUIT) {
            return FACE_WEIGHT + 100;
        }
        return FACE_WEIGHT;
    }

    private int get_best_meld_card_if_thrown(int[][] meld_logic_vector, int card_id) {
        int[][] temp_logic_vector = new int[12][Constants.TOTAL_NO_OF_CARDS];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < Constants.TOTAL_NO_OF_CARDS; j++) {
                if (this.which_card_used_for_meld[this.to_9(i)][card_id] == false) {
                    if (temp_logic_vector[i][card_id] > 0) {
                        temp_logic_vector[i][card_id] -= 1;
                    } else {
                        temp_logic_vector[i][card_id] = 0;
                    }
                }
            }
        }

        class Lambda_Function {
            public boolean are_next_n_elements_0(int[] the_list, int start, int n) {
                for (int i = 0; i < n; i++) {
                    if (the_list[start + i] != 0) {
                        return false;
                    }
                }
                return true;
            }
        }
        Lambda_Function lambda_function = new Lambda_Function();

        int max_meld_score = -111111;
        int meld_number_played = Melds.INVALID;
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < Constants.TOTAL_NO_OF_CARDS; j++) {
                if (lambda_function.are_next_n_elements_0(
                        temp_logic_vector[i], j, Constants.NO_OF_SAME_CARDS)) {
                    break;
                }
                if (j == (Constants.TOTAL_NO_OF_CARDS - Constants.NO_OF_SAME_CARDS)) {
                    int cur_meld_score = Melds.get_meld_score(this.to_9(i));
                    if (cur_meld_score > max_meld_score) {
                        max_meld_score = cur_meld_score;
                        meld_number_played = this.to_9(i);
                    }
                }
            }
        }

        return meld_number_played;
    }

    private void update_logic_vector_with_history(int[][] meld_logic_vector) {
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < Constants.TOTAL_NO_OF_CARDS; j++) {
                if (this.which_card_used_for_meld[to_9(i)][j] == true) {
                    if (meld_logic_vector[i][j] >= 0) {
                        meld_logic_vector[i][j] = 0;
                    }
                }
            }
        }
    }

    private int to_9(int meld_12) {
        if (meld_12 < 2) {
            return meld_12;
        } else if (meld_12 >= 2 && meld_12 <= 5) {
            return 2;
        } else {
            return (meld_12 - 3);
        }
    }
}
