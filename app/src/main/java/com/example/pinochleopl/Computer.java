/*
 ************************************************************
 * Name: Srijan Prasad Joshi
 * Project: Pinochle Java/Android
 * Class: OPL Fall 20
 * Date: 12/8/2020
 ************************************************************
*/

package com.example.pinochleopl;

import android.util.Pair;

import java.util.ArrayList;

public class Computer extends Player {

    public Pair<String, Integer> play_best_meld() {
        Pair<ArrayList<Integer>, Integer> recommended_meld = this.get_card_ids_and_meld_number_12_best_meld();
        ArrayList<Integer> meld_card_ids = (ArrayList<Integer>) recommended_meld.first.clone();
        int recommended_meld_number_12 = recommended_meld.second;
        ArrayList<Integer> meld_card_indexes = (ArrayList<Integer>) meld_card_ids.clone();
        for (int i = 0; i < meld_card_indexes.size(); i++) {
            meld_card_indexes.set(i, this.search_card_in_pile(meld_card_indexes.get(i)));
        }

        String message = "";
        int recommended_meld_number_9 = this.to_9(recommended_meld_number_12);

        if (recommended_meld_number_9 != Melds.INVALID) {
            for (int card_id : meld_card_ids) {
                message += (Card.get_string_from_id(card_id) + " ");
            }
            message += ("as a " +
                    Melds.get_meld_name(recommended_meld_number_9) + " to earn " +
                    Melds.get_meld_score(recommended_meld_number_9) + " points");

            for (int i = 0; i < meld_card_indexes.size(); i++) {
                int card_index = meld_card_indexes.get(i);
                int number_of_similar_melds = this.current_meld_cards.get(recommended_meld_number_12).size();
                ArrayList<Integer> temp = new ArrayList<Integer>();
                temp.add(recommended_meld_number_12);
                temp.add(number_of_similar_melds);
                temp.add(i);
                this.hand_meld_involvement_list.get(card_index).add(temp);
            }
            this.current_meld_cards.get(recommended_meld_number_12).add(meld_card_indexes);
            this.update_meld_history(meld_card_ids, recommended_meld_number_9);
        } else {
            message = "no melds because it had none.";
        }
        return new Pair<String, Integer>(message, recommended_meld_number_9);
    }

    public Pair<String, Integer> throw_most_suitable_card(int card_id) {
        int index = -1;
        String message = "";
        if (card_id == -1) {
            Pair<Integer, Integer> recommended_card_with_best_meld = this.find_index_meld_pair_of_card_to_throw();
            int best_card_index = recommended_card_with_best_meld.first;

            if (best_card_index != -1) {
                index = best_card_index;
                int id = this.hand_card_pile.get(index);
                message += (Card.get_string_from_id(id) + " ");
                message += "because it was the card resulting \nin the highest possible meld";
            } else {
                index = this.find_index_of_greatest_card();
                int id = this.hand_card_pile.get(index);
                message += (Card.get_string_from_id(id) + " ");
                message += "which is the greatest card. \nHowever, it has no melds available.";
            }
        } else {
            index = this.find_index_of_smallest_card_greater_than(card_id);
            if (index == -1) {
                index = this.find_index_of_smallest_card();
                int id = this.hand_card_pile.get(index);
                message += (Card.get_string_from_id(id) + " ");
                message += "which is the smallest card, \nbecause it had no possibility of winning.";
            } else {
                int id = this.hand_card_pile.get(index);
                message += (Card.get_string_from_id(id) + " ");
                message += "which is the smallest card greater\n than the lead card";
            }
        }
        int ret_card_id = this.hand_card_pile.get(index);
        this.remove_card_from_pile(index);
        return new Pair<String, Integer>(message, ret_card_id);
    }
}
