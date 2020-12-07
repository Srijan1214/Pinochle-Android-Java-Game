package com.example.pinochleopl;

import android.gesture.GestureUtils;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;

public class Player {

    protected ArrayList<Integer> hand_card_pile;
    protected ArrayList<ArrayList<ArrayList<Integer>>> hand_meld_involvement_list;
    protected ArrayList<ArrayList<ArrayList<Integer>>> current_meld_cards;
    private ArrayList<Integer> capture_pile;

    private boolean[][] which_card_used_for_meld;


    private int trump_card;

    public Player() {
        this.hand_card_pile = new ArrayList<Integer>();
        this.hand_meld_involvement_list = new ArrayList<ArrayList<ArrayList<Integer>>>();
        this.current_meld_cards = new ArrayList<ArrayList<ArrayList<Integer>>>();
        while (this.current_meld_cards.size() < 12) this.current_meld_cards.add(new ArrayList<>());
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
        String message = "";
        for (int i = 0; i < this.hand_card_pile.size(); i++) {
            int card_id = this.hand_card_pile.get(i);
            if (this.hand_meld_involvement_list.get(i).size() == 0) {
                message += Card.get_string_from_id(card_id);
                if (i != this.hand_card_pile.size() - 1) {
                    message += " ";
                }
            }
        }
        return message;
    }

    public String get_capture_pile_string() {
        String message = "";
        for (int i = 0; i < this.capture_pile.size(); i++) {
            int card_id = this.capture_pile.get(i);
            message += Card.get_string_from_id(card_id);
            if (i != this.capture_pile.size() - 1) {
                message += " ";
            }
        }
        return message;
    }

    public String get_meld_string() {
        String message = "";

        for (int meld_num_12 = 0; meld_num_12 < this.current_meld_cards.size(); meld_num_12++) {
            for (ArrayList<Integer> meld_card_indexes : this.current_meld_cards.get(meld_num_12)) {
                for (int hand_index : meld_card_indexes) {
                    int card_id = this.hand_card_pile.get(hand_index);
                    message += Card.get_string_from_id(card_id);
                    if (this.hand_meld_involvement_list.get(hand_index).size() > 1) {
                        message += "*";
                    }
                    if (meld_num_12 == this.current_meld_cards.size()) {
                        continue;
                    }
                    if (!(hand_index == meld_card_indexes.get(meld_card_indexes.size() - 1))) {
                        message += " ";
                    }
                }
                message += ", ";
            }
        }
        if (message.length() > 0 && message.substring(message.length() - 1, message.length()).equals(" ")) {
            message = message.substring(0, message.length() - 1);
        }
        if (message.length() > 0 && message.substring(message.length() - 1, message.length()).equals(",")) {
            message = message.substring(0, message.length() - 1);
        }
        return message;
    }

    public void load_members_from_serialization_string(ArrayList<String> serialization_str_list, boolean[] cards_that_have_been_used) {
        int start = serialization_str_list.get(0).indexOf(":") + 1;
        int end = serialization_str_list.get(0).length();
        String hand_cards_string = serialization_str_list.get(0).substring(start, end).trim();


        start = serialization_str_list.get(1).indexOf(":") + 1;
        end = serialization_str_list.get(1).length();
        String capture_cards_string = serialization_str_list.get(1).substring(start, end).trim();

        start = serialization_str_list.get(2).indexOf(":") + 1;
        end = serialization_str_list.get(2).length();
        String meld_cards_string = serialization_str_list.get(2).substring(start, end).trim();

        this.hand_card_pile.clear();
        this.hand_meld_involvement_list.clear();

        this.load_hand_cards_from_string(hand_cards_string, cards_that_have_been_used);
        this.load_capture_cards_from_string(capture_cards_string, cards_that_have_been_used);
        this.load_meld_cards_from_string(meld_cards_string, cards_that_have_been_used);
    }

    public void load_capture_cards_from_string(String capture_string, boolean[] cards_that_have_been_used) {
        if (capture_string.isEmpty()) {
            return;
        }
        String[] cards = capture_string.split(" ");
        for (String card_str : cards) {
            int id = Card.get_id_from_string(card_str);
            if (cards_that_have_been_used[id]) {
                id += 1;
            }
            cards_that_have_been_used[id] = true;
            this.capture_pile.add(id);
        }
    }

    public void load_hand_cards_from_string(String hand_string, boolean[] cards_that_have_been_used) {
        if (hand_string.isEmpty()) {
            return;
        }
        String[] cards = hand_string.split(" ");
        for (String card_str : cards) {
            int id = Card.get_id_from_string(card_str);
            if (cards_that_have_been_used[id]) {
                id += 1;
            }
            cards_that_have_been_used[id] = true;
            this.hand_card_pile.add(id);
            this.hand_meld_involvement_list.add(new ArrayList<>());
        }
    }

    public void load_meld_cards_from_string(String meld_string, boolean[] cards_that_have_been_used) {
        if (meld_string.isEmpty()) {
            return;
        }
        int[][] logic_vector = this.get_meld_logic_vector();
        for (int i = 0; i < logic_vector[2 + Card.get_suit_from_id(this.trump_card)].length; i++) {
            logic_vector[2 + Card.get_suit_from_id(this.trump_card)][i] = -1;
        }
        String[] temp_meld_card_strings = meld_string.split(", ");
        String[][] meld_card_strings = new String[temp_meld_card_strings.length][];
        ArrayList<Integer> meld_num_12_vec = new ArrayList<>();

        for (int i = 0; i < meld_card_strings.length; i++) {
            meld_card_strings[i] = temp_meld_card_strings[i].trim().split(" ");
            ArrayList<Integer> temp = new ArrayList<>();
            for (String ele : meld_card_strings[i]) {
                temp.add(Card.get_id_from_string(ele));
            }
            meld_num_12_vec.add(this.get_meld_type_12_from_cards(temp));
        }

        class LambdaFunctions {
            public int get_id_for_duplicate_cards(int id) {
                if (id % 2 == 0) {
                    return id + 1;
                } else {
                    return id - 1;
                }
            }
        }
        LambdaFunctions lambdaFunctions = new LambdaFunctions();

        int[] id_to_change_cur_card_to = new int[48];
        Arrays.fill(id_to_change_cur_card_to, -1);

        ArrayList<ArrayList<Integer>> meld_card_ids = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < meld_card_strings.length; i++) {
            meld_card_ids.add(new ArrayList<Integer>());
            for (int j = 0; j < meld_card_strings[i].length; j++) {
                meld_card_ids.get(i).add(Card.get_id_from_string(
                        meld_card_strings[i][j]
                ));
            }
        }

        for (int i = 0; i < meld_card_ids.size(); i++) {
            int cur_meld_12 = meld_num_12_vec.get(i);
            for (int j = 0; j < meld_card_ids.get(i).size(); j++) {
                int id = meld_card_ids.get(i).get(j);
                if (id_to_change_cur_card_to[id] != -1) {
                    id = id_to_change_cur_card_to[id];
                }
                if (id_to_change_cur_card_to[id] != -1) {
                    id = id_to_change_cur_card_to[id];
                }
                if (logic_vector[cur_meld_12][id] != 0) {
                    id = id_to_change_cur_card_to[id];
                }
                meld_card_ids.get(i).set(j, id);
                if (meld_card_strings[i][j].length() == 2) {
                    id = lambdaFunctions.get_id_for_duplicate_cards(id);
                }
                id_to_change_cur_card_to[Card.get_id_from_string(meld_card_strings[i][j])] = id;
            }
        }

        for (int i = 0; i < meld_card_ids.size(); i++) {
            for (int j = 0; j < meld_card_ids.get(i).size(); j++) {
                if (cards_that_have_been_used[meld_card_ids.get(i).get(j)]) {
                    meld_card_ids.get(i).set(j,
                            lambdaFunctions.get_id_for_duplicate_cards(
                                    meld_card_ids.get(i).get(j)));
                }
            }
        }
        for (int i = 0; i < meld_card_ids.size(); i++) {
            for (int j = 0; j < meld_card_ids.get(i).size(); j++) {
                cards_that_have_been_used[meld_card_ids.get(i).get(j)] = true;
            }
        }

        for (int i = 0; i < meld_card_ids.size(); i++) {
            int cur_meld_12 = meld_num_12_vec.get(i);
            for (int id : meld_card_ids.get(i)) {
                this.which_card_used_for_meld[this.to_9(cur_meld_12)][id] = true;
            }
        }

        int[] id_to_index = new int[48];
        Arrays.fill(id_to_index, -1);

        for (int i = 0; i < meld_card_ids.size(); i++) {
            for (int id : meld_card_ids.get(i)) {
                if (id_to_index[id] == -1) {
                    int index = this.hand_card_pile.size();
                    this.hand_card_pile.add(id);
                    this.hand_meld_involvement_list.add(new ArrayList<>());
                    id_to_index[id] = index;
                }
            }
        }


        for (int i = 0; i < meld_card_ids.size(); i++) {
            int meld_num_12 = meld_num_12_vec.get(i);
            ArrayList<Integer> index_vector = new ArrayList<>();
            for (int id : meld_card_ids.get(i)) {
                index_vector.add(id_to_index[id]);
            }
            this.current_meld_cards.get(meld_num_12).add(index_vector);
            for (int j = 0; j < index_vector.size(); j++) {
                int position_in_melds = this.current_meld_cards.get(meld_num_12).size() - 1;
                ArrayList<Integer> temp = new ArrayList<>();
                temp.add(meld_num_12);
                temp.add(position_in_melds);
                temp.add(j);
                this.hand_meld_involvement_list.get(index_vector.get(j)).add(
                        temp
                );
            }
        }
    }

    public void remove_card_from_pile(int card_index) {
        for (ArrayList<Integer> removal_index_to_meld_pointer_triplet : this.hand_meld_involvement_list.get(card_index)) {
            ArrayList<Integer> cards_to_remove_from_meld =
                    this.current_meld_cards.get(removal_index_to_meld_pointer_triplet.get(0)).get(removal_index_to_meld_pointer_triplet.get(1));
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
            int back_index = this.current_meld_cards.get(removal_index_to_meld_pointer_triplet.get(0)).size() - 1;
            this.current_meld_cards.get(removal_index_to_meld_pointer_triplet.get(0)).set(
                    removal_index_to_meld_pointer_triplet.get(1),
                    this.current_meld_cards.get(
                            removal_index_to_meld_pointer_triplet.get(0)).get(back_index)
            );
            int temp_index = this.current_meld_cards.get(removal_index_to_meld_pointer_triplet.get(0)).size() - 1;
            this.current_meld_cards.get(removal_index_to_meld_pointer_triplet.get(0)).remove(temp_index);
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

    protected Pair<Integer, Integer> find_index_meld_pair_of_card_to_throw() {
        int[][] meld_logic_vector = this.get_meld_logic_vector();
        this.add_to_meld_logic_vector(meld_logic_vector, this.hand_card_pile);
        this.update_logic_vector_with_history(meld_logic_vector);

        Pair<Integer, Integer> ret_pair = this.get_best_meldCardIndexPair_from_Logic(meld_logic_vector);

        if (ret_pair.second.intValue() == Melds.INVALID) {
            return new Pair<Integer, Integer>(-1, Melds.INVALID);
        }
        return ret_pair;
    }

    protected Pair<ArrayList<Integer>, Integer> get_card_ids_and_meld_number_12_best_meld() {
        int[][] meld_logic_vector = this.get_meld_logic_vector();

        this.add_to_meld_logic_vector(meld_logic_vector, this.hand_card_pile);

        this.update_logic_vector_with_history(meld_logic_vector);

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
            for (int j = 0; j < Constants.TOTAL_NO_OF_CARDS; j += Constants.NO_OF_SAME_CARDS) {
                if (lambda_function.are_next_n_elements_0(meld_logic_vector[i], j, Constants.NO_OF_SAME_CARDS)) {
                    break;
                }
                if (j == (Constants.TOTAL_NO_OF_CARDS - Constants.NO_OF_SAME_CARDS)) {
                    int cur_meld_score = Melds.get_meld_score(this.to_9(i));
                    if (cur_meld_score > max_meld_score) {
                        max_meld_score = cur_meld_score;
                        meld_number_played = i;
                    }
                }
            }
        }
        if (meld_number_played == Melds.INVALID) {
            return (new Pair<ArrayList<Integer>, Integer>(new ArrayList<Integer>(), Melds.INVALID));
        } else {
            ArrayList<Integer> ret_list = new ArrayList<>();
            int j = 0;
            while (j < Constants.TOTAL_NO_OF_CARDS) {
                int ele = meld_logic_vector[meld_number_played][j];
                if (ele > 0) {
                    ret_list.add(j);
                    while ((j % Constants.NO_OF_SAME_CARDS) != ((-1 % Constants.NO_OF_SAME_CARDS) + Constants.NO_OF_SAME_CARDS)) {
                        j += 1;
                    }
                }
                j += 1;
            }
            return (new Pair<ArrayList<Integer>, Integer>(ret_list, meld_number_played));
        }
    }

    protected boolean is_meld_allowed_by_history(ArrayList<Integer> card_ids, int meld_9) {
        for (int card_id : card_ids) {
            if (this.which_card_used_for_meld[meld_9][card_id] == true) {
                return false;
            }
        }
        return true;
    }

    protected void update_meld_history(ArrayList<Integer> card_ids, int meld_number_9) {
        for (int card_id : card_ids) {
            this.which_card_used_for_meld[meld_number_9][card_id] = true;
            if (meld_number_9 == Melds.FLUSH) {
                this.which_card_used_for_meld[Melds.ROYAL_MARRIAGE][card_id] = true;
            }
        }
    }

    protected int find_index_of_smallest_card_greater_than(int card_id) {
        // First find the smallest card the results in the worse meld and greater than chase card
        int[][] meld_logic_vector = this.get_meld_logic_vector();
        this.add_to_meld_logic_vector(meld_logic_vector, this.hand_card_pile);
        this.update_logic_vector_with_history(meld_logic_vector);

        ArrayList<int[]> data = new ArrayList<>();
        for (int i = 0; i < this.hand_card_pile.size(); i++) {
            int cur_card_id = this.hand_card_pile.get(i);
            int cur_card_weight = this.get_card_weight(cur_card_id);
            if (Card.is_first_card_greater_than_lead(cur_card_id, card_id, this.trump_card)) {
                int meld_9 = this.get_best_meld_card_if_thrown(meld_logic_vector, cur_card_id);
                if (meld_9 != Melds.INVALID) {
                    data.add(new int[]{meld_9, cur_card_weight, i});
                }
            }
        }
        Collections.sort(data, new Comparator<int[]>() {
            @Override
            public int compare(final int[] entry1, final int[] entry2) {
                final int ele1 = entry1[0];
                final int ele2 = entry2[0];
                if (ele2 == ele1) {
                    return entry1[1] - entry2[1];
                }
                return ele1 - ele2;
            }
        });
        if (!data.isEmpty()) {
            return data.get(0)[2];
        }


        // If no meld is possible then just find smallest gard greater than lead
        int min_card_index = -1;
        int min_greatest_card_weight = 9999999;
        for (int i = 0; i < this.hand_card_pile.size(); i++) {
            int cur_card_id = this.hand_card_pile.get(i);
            int cur_card_weight = this.get_card_weight(cur_card_id);
            if (Card.is_first_card_greater_than_lead(cur_card_id, card_id, this.trump_card)) {
                if (cur_card_weight < min_greatest_card_weight) {
                    min_card_index = i;
                    min_greatest_card_weight = cur_card_weight;
                }
            }
        }
        return min_card_index;
    }

    protected int find_index_of_smallest_card() {
        int min_card_weight = 999999;
        int min_card_index = -1;

        for (int i = 0; i < this.hand_card_pile.size(); i++) {
            int cur_card_id = this.hand_card_pile.get(i);
            int cur_card_weight = this.get_card_weight(cur_card_id);
            if (cur_card_weight < min_card_weight) {
                min_card_index = i;
                min_card_weight = cur_card_weight;
            }
        }

        return min_card_index;
    }

    protected int find_index_of_greatest_card() {
        int max_card_weight = -11111;
        int max_card_index = -1;

        for (int i = 0; i < this.hand_card_pile.size(); i++) {
            int cur_card_id = this.hand_card_pile.get(i);
            int cur_card_weight = this.get_card_weight(cur_card_id);
            if (cur_card_weight > max_card_weight) {
                max_card_index = i;
                max_card_weight = cur_card_weight;
            }
        }

        return max_card_index;
    }

    protected int to_9(int meld_12) {
        if (meld_12 < 2) {
            return meld_12;
        } else if (meld_12 >= 2 && meld_12 <= 5) {
            return 2;
        } else {
            return (meld_12 - 3);
        }
    }

    protected int search_card_in_pile(int card_id) {
        return this.hand_card_pile.indexOf(card_id);
    }

    private int get_card_weight(int card_id) {
        final int FACE_WEIGHT = Card.get_face_weight_from_id(card_id);
        final int TRUMP_SUIT = Card.get_suit_from_id(this.trump_card);

        if (Card.get_suit_from_id(card_id) == TRUMP_SUIT) {
            return FACE_WEIGHT + 100;
        }
        return FACE_WEIGHT;
    }

    private int[][] get_meld_logic_vector() {
        final int TRUMP_SUIT = Card.get_suit_from_id(this.trump_card);
        Integer[] FLUSH_REQUIREMENTS = {
                Card.get_id_from_face_and_suit(Card.TEN_FACE, TRUMP_SUIT),
                Card.get_id_from_face_and_suit(Card.JACK_FACE, TRUMP_SUIT),
                Card.get_id_from_face_and_suit(Card.QUEEN_FACE, TRUMP_SUIT),
                Card.get_id_from_face_and_suit(Card.KING_FACE, TRUMP_SUIT),
                Card.get_id_from_face_and_suit(Card.ACE_FACE, TRUMP_SUIT),
        };
        Integer[] ROYAL_MARRIAGE_REQUIREMENTS = {
                Card.get_id_from_face_and_suit(Card.QUEEN_FACE, TRUMP_SUIT),
                Card.get_id_from_face_and_suit(Card.KING_FACE, TRUMP_SUIT),
        };
        Integer[] SPADES_MARRIAGE_REQUIREMENTS = {
                Card.get_id_from_face_and_suit(Card.QUEEN_FACE, Card.SPADES_SUIT),
                Card.get_id_from_face_and_suit(Card.KING_FACE, Card.SPADES_SUIT),
        };
        Integer[] CLUBS_MARRIAGE_REQUIREMENTS = {
                Card.get_id_from_face_and_suit(Card.QUEEN_FACE, Card.CLUBS_SUIT),
                Card.get_id_from_face_and_suit(Card.KING_FACE, Card.CLUBS_SUIT),
        };
        Integer[] HEARTS_MARRIAGE_REQUIREMENTS = {
                Card.get_id_from_face_and_suit(Card.QUEEN_FACE, Card.HEARTS_SUIT),
                Card.get_id_from_face_and_suit(Card.KING_FACE, Card.HEARTS_SUIT),
        };
        Integer[] DIAMONDS_MARRIAGE_REQUIREMENTS = {
                Card.get_id_from_face_and_suit(Card.QUEEN_FACE, Card.DIAMONDS_SUIT),
                Card.get_id_from_face_and_suit(Card.KING_FACE, Card.DIAMONDS_SUIT),
        };
        Integer[] DIX_REQUIREMENTS = {Card.get_id_from_face_and_suit(Card.NINE_FACE, TRUMP_SUIT)};
        Integer[] FOUR_ACES_REQUIREMENTS = {
                Card.get_id_from_face_and_suit(Card.ACE_FACE, Card.SPADES_SUIT),
                Card.get_id_from_face_and_suit(Card.ACE_FACE, Card.CLUBS_SUIT),
                Card.get_id_from_face_and_suit(Card.ACE_FACE, Card.HEARTS_SUIT),
                Card.get_id_from_face_and_suit(Card.ACE_FACE, Card.DIAMONDS_SUIT),
        };
        Integer[] FOUR_KINGS_REQUIREMENTS = {
                Card.get_id_from_face_and_suit(Card.KING_FACE, Card.SPADES_SUIT),
                Card.get_id_from_face_and_suit(Card.KING_FACE, Card.CLUBS_SUIT),
                Card.get_id_from_face_and_suit(Card.KING_FACE, Card.HEARTS_SUIT),
                Card.get_id_from_face_and_suit(Card.KING_FACE, Card.DIAMONDS_SUIT),
        };
        Integer[] FOUR_QUEENS_REQUIREMENTS = {
                Card.get_id_from_face_and_suit(Card.QUEEN_FACE, Card.SPADES_SUIT),
                Card.get_id_from_face_and_suit(Card.QUEEN_FACE, Card.CLUBS_SUIT),
                Card.get_id_from_face_and_suit(Card.QUEEN_FACE, Card.HEARTS_SUIT),
                Card.get_id_from_face_and_suit(Card.QUEEN_FACE, Card.DIAMONDS_SUIT),
        };
        Integer[] FOUR_JACKS_REQUIREMENTS = {
                Card.get_id_from_face_and_suit(Card.JACK_FACE, Card.SPADES_SUIT),
                Card.get_id_from_face_and_suit(Card.JACK_FACE, Card.CLUBS_SUIT),
                Card.get_id_from_face_and_suit(Card.JACK_FACE, Card.HEARTS_SUIT),
                Card.get_id_from_face_and_suit(Card.JACK_FACE, Card.DIAMONDS_SUIT),
        };
        Integer[] PINOCHLE_REQUIREMENTS = {
                Card.get_id_from_face_and_suit(Card.QUEEN_FACE, Card.SPADES_SUIT),
                Card.get_id_from_face_and_suit(Card.JACK_FACE, Card.DIAMONDS_SUIT),
        };
        Integer[][] ALL_REQUIREMENTS = {FLUSH_REQUIREMENTS, ROYAL_MARRIAGE_REQUIREMENTS,
                SPADES_MARRIAGE_REQUIREMENTS,
                CLUBS_MARRIAGE_REQUIREMENTS,
                HEARTS_MARRIAGE_REQUIREMENTS,
                DIAMONDS_MARRIAGE_REQUIREMENTS, DIX_REQUIREMENTS,
                FOUR_ACES_REQUIREMENTS, FOUR_JACKS_REQUIREMENTS,
                FOUR_KINGS_REQUIREMENTS, FOUR_QUEENS_REQUIREMENTS,
                PINOCHLE_REQUIREMENTS};

        int[][] meld_logic_vector = new int[12][Constants.TOTAL_NO_OF_CARDS];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < Constants.TOTAL_NO_OF_CARDS; j++) {
                meld_logic_vector[i][j] = -1;
            }
        }
        for (int i = 0; i < ALL_REQUIREMENTS.length; i++) {
            if (i == (2 + TRUMP_SUIT)) {
                int[] temp_arr = new int[Constants.TOTAL_NO_OF_CARDS];
                Arrays.fill(temp_arr, 0);
                meld_logic_vector[i] = temp_arr;
                continue;
            }
            for (int j : ALL_REQUIREMENTS[i]) {
                for (int player_addition = 0; player_addition < Constants.NO_OF_SAME_CARDS; player_addition++) {
                    meld_logic_vector[i][j + player_addition] = 0;
                }
            }
        }

        return meld_logic_vector;
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

    private int get_best_meld_card_if_thrown(int[][] meld_logic_vector, int card_id) {
        int[][] temp_logic_vector = new int[meld_logic_vector.length][];
        for (int i = 0; i < meld_logic_vector.length; i++) {
            for (int j = 0; j < meld_logic_vector[i].length; j++) {
                temp_logic_vector[i] = meld_logic_vector[i].clone();
            }
        }

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
            for (int j = 0; j < Constants.TOTAL_NO_OF_CARDS; j += Constants.NO_OF_SAME_CARDS) {
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
                    if (cur_card_weight > best_card_weight) {
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
}
