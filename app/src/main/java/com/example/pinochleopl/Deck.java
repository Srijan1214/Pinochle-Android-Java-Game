/*
 ************************************************************
 * Name: Srijan Prasad Joshi
 * Project: Pinochle Java/Android
 * Class: OPL Fall 20
 * Date: 12/8/2020
 ************************************************************
*/

package com.example.pinochleopl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Deck {
    private ArrayList<Integer> stock;

    /* *********************************************************************
    Function Name: Deck 
    Purpose: To construct the deck object
    Parameters:
    			None
    Return Value: None
    Local Variables:
    			None
    Assistance Received: none
    ********************************************************************* */
    public Deck() {
        this.stock = new ArrayList<Integer>();
        for (int i = 0; i < Constants.TOTAL_NO_OF_CARDS; i++) {
            this.stock.add(i);
        }
        Collections.shuffle(this.stock, new Random());
    }

    /* *********************************************************************
    Function Name: pop_card_from_deck 
    Purpose: To pop the first card in the deck and return the value of the card
    Parameters:
    			None
    Return Value: the first card in the stock
    Local Variables:
    			None
    Assistance Received: none
    ********************************************************************* */
    public int pop_card_from_deck() {
        return this.stock.remove(0).intValue();
    }

    /* *********************************************************************
    Function Name: get_stock_size 
    Purpose: To get the size of the stock pile
    Parameters:
    			None
    Return Value: the size of the stock pile
    Local Variables:
    			None
    Assistance Received: none
    ********************************************************************* */
    public int get_stock_size() {
        return this.stock.size();
    }

    /* *********************************************************************
    Function Name: load_stock_pile_from_string
    Purpose: To load the stock string stored in the serialization file into the deck.
    Parameters:
                stock_str, the string value, that stores the stock cards.
                cards_that_have_already_been_used, an array of integers, that tells us which cards id's has
					already been used by the serialization loading of players. Helpful since two of the same
					card strings cannot have the same id.  After a new card id is used, the particular index 
					for that id is set to true.
    Local Variables:
                card_s, an array of strings that separate the stock_str by a space character.
    Algorithm:
                1) Loop through all the card strings from the a_stock_string.
                2) Get the id of that card and change the id if it is already used.
                3) Create a new card with that id to play the game.
                4) Set the index represented by the id in a_cards_that_have_been_used to true.
    Assistance Received: none
    ********************************************************************* */
	public void load_stock_pile_from_string(String stock_str, boolean[] cards_that_have_already_been_used) {
        this.stock.clear();
        stock_str = stock_str.trim();
        if(stock_str.isEmpty()){
            return;
        }
        String[] cards = stock_str.split(" ");
        for(String card_str: cards) {
            int id = Card.get_id_from_string(card_str);
            if(cards_that_have_already_been_used[id]) {
                id+= 1;
            }
            cards_that_have_already_been_used[id] = true;
            this.stock.add(id);
        }
    }

    /* *********************************************************************
    Function Name: get_stock_string 
    Purpose: To get the card in the stock, in string representation separated by a space
    Parameters:
    			None
    Return Value: A string with the cards separated by spaces.
    Local Variables:
    			message, the storage for the return value
    Assistance Received: none
    ********************************************************************* */
    public String get_stock_string(){
        String message = "";
        for (int i = 0; i < this.stock.size(); i++) {
            int card_id = this.stock.get(i);
            message += Card.get_string_from_id(card_id);
            if(i != this.stock.size() - 1) {
                message+= " ";
            }
        }
        return message;
    }

    /* *********************************************************************
    Function Name: get_stock_pile 
    Purpose: To get a clone of the stock pile for rendering 
    Parameters:
    			None
    Return Value: A list of integers, representing the clone of the stock pile
    Local Variables:
    			None
    Assistance Received: none
    ********************************************************************* */
    public ArrayList<Integer> get_stock_pile(){
        return (ArrayList<Integer>) this.stock.clone();
    }

}
