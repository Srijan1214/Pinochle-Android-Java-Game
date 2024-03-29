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

public class Human extends Player {
    /* *********************************************************************
    Function Name: get_meld_help_message 
    Purpose: To get the help message and the recommended meld card indexes to meld.
    Parameters:
    			None
    Return Value: A pair object of String, and ArrayList<Integer>. The first is the help message and 
					the second is the list of card indexes recommended to play for meld.
    Local Variables:
				None
    Assistance Received: none
    ********************************************************************* */
    public Pair<String, ArrayList<Integer>> get_meld_help_message() {
        Pair<ArrayList<Integer>, Integer> recommended_meld = this.get_card_ids_and_meld_number_12_best_meld();
        ArrayList<Integer> meld_card_ids = (ArrayList<Integer>) recommended_meld.first.clone();
        int recommended_meld_number_12 = recommended_meld.second;
        int recommended_meld_number_9 = this.to_9(recommended_meld_number_12);
        ArrayList<Integer> meld_card_indexes = (ArrayList<Integer>) meld_card_ids.clone();
        for (int i = 0; i < meld_card_indexes.size(); i++) {
            meld_card_indexes.set(i, this.search_card_in_pile(meld_card_indexes.get(i)));
        }
        String message = "";
        if (recommended_meld_number_9 != Melds.INVALID) {
            message = "Recommend you present ";
            for (int id : meld_card_ids) {
                message += (Card.get_string_from_id(id) + " ");
            }
            message += (
                    "as a "
                            + Melds.get_meld_name(recommended_meld_number_9)
                            + "to earn" + Melds.get_meld_score(recommended_meld_number_9) + " points");
            return new Pair<>(message, meld_card_indexes);
        } else {
            return new Pair<>(" You have no melds.", meld_card_indexes);
        }
    }

    /* *********************************************************************
    Function Name: get_card_to_throw_help_message 
    Purpose: To get the help message and the recommended meld card index to throw.
    Parameters:
    			lead_card, an integer, that tells the card if of the lead card. It is -1 if we are the lead
				player.
    Return Value: A pair object of String, and ArrayList<Integer>. The first is the help message and 
					the second is a list of size 1 that tells the recommended card index to throw..
    Local Variables:
				None
    Assistance Received: none
    ********************************************************************* */
    public Pair<String, ArrayList<Integer>> get_card_to_throw_help_message(int lead_card) {
        int index;
        String message = "Recommend you play ";
        if (lead_card == -1) {
            Pair<Integer, Integer> recommended_card_with_best_meld = this.find_index_meld_pair_of_card_to_throw();
            int best_card_index = recommended_card_with_best_meld.first;

            if (best_card_index != -1) {
                index = best_card_index;
                int id = this.hand_card_pile.get(index);
                message += (Card.get_string_from_id(id) + " ");
                message += "because it is the card resulting \nin the highest possible meld";
            } else {
                index = this.find_index_of_greatest_card();
                int id = this.hand_card_pile.get(index);
                message += (Card.get_string_from_id(id) + " ");
                message += "which is the greatest card. \nHowever, it has no melds available.";
            }
        } else {
            index = this.find_index_of_smallest_card_greater_than(lead_card);
            if (index == -1) {
                index = this.find_index_of_smallest_card();
                int id = this.hand_card_pile.get(index);
                message += (Card.get_string_from_id(id) + " ");
                message += "which is the smallest card, \nbecause you cannot win.";
            } else {
                int id = this.hand_card_pile.get(index);
                message += (Card.get_string_from_id(id) + " ");
                message += "which is the smallest card greater\n than the lead card";
            }
        }
        ArrayList<Integer> ret_list = new ArrayList<Integer>();
        ret_list.add(index);
        return new Pair<String, ArrayList<Integer>>(message, ret_list);
    }

    /* *********************************************************************
    Function Name: perform_meld_if_valid
    Purpose: To perform the meld if the card indexes arguments allows for a meld.
    Parameters:
    			card_indexes, an ArrayList of integers, that tell the indexes of the cards the user
				wants to use for a meld
    Return Value: A pair object of String, and ArrayList<Integer>. The first is the help message and 
					the second is a list of size 1 that tells the recommended card index to throw..
    Local Variables:
				None
    Assistance Received: none
    ********************************************************************* */
    public int perform_meld_if_valid(ArrayList<Integer> card_indexes) {
        ArrayList<Integer> meld_cards = this.get_ids_from_indexes(card_indexes);

        int meld_number_12 = this.get_meld_type_12_from_cards(meld_cards);

        if (meld_number_12 != Melds.INVALID && this.is_meld_allowed_by_history(meld_cards, this.to_9(meld_number_12))) {
            for (int i = 0; i < card_indexes.size(); i++) {
                int card_index = card_indexes.get(i);
                int number_of_similar_melds = this.current_meld_cards.get(meld_number_12).size();
                ArrayList<Integer> temp = new ArrayList<Integer>();
                temp.add(meld_number_12);
                temp.add(number_of_similar_melds);
                temp.add(i);
                this.hand_meld_involvement_list.get(card_index).add(temp);
            }
            this.current_meld_cards.get(meld_number_12).add(card_indexes);
            this.update_meld_history(meld_cards, this.to_9(meld_number_12));
            return this.to_9(meld_number_12);
        } else {
            return Melds.INVALID;
        }
    }
}
