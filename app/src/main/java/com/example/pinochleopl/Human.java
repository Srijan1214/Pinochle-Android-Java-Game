package com.example.pinochleopl;

import android.util.Pair;

import java.util.ArrayList;

public class Human extends Player {
    public String get_meld_help_message() {
        Pair<ArrayList<Integer>, Integer> recommended_meld = this.get_card_ids_and_meld_number_12_best_meld();
        ArrayList<Integer> meld_card_ids = (ArrayList<Integer>) recommended_meld.first.clone();
        int recommended_meld_number_12 = recommended_meld.second;
        int recommended_meld_number_9 = this.to_9(recommended_meld_number_12);
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
            return message;
        } else {
            return " You have no melds.";
        }
    }

    public String get_card_to_throw_help_message(int lead_card) {
        int index = -1;
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
                message += "which is the smallest card, \\nbecause you cannot win.";
            } else {
                int id = this.hand_card_pile.get(index);
                message += (Card.get_string_from_id(id) + " ");
                message += "which is the smallest card greater\\n than the lead card";
            }
        }
        return message;
    }

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
