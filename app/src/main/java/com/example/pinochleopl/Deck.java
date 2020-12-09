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

    public Deck() {
        this.stock = new ArrayList<Integer>();
        for (int i = 0; i < Constants.TOTAL_NO_OF_CARDS; i++) {
            this.stock.add(i);
        }
        Collections.shuffle(this.stock, new Random());

        for (int ele: this.stock) {
            System.out.println(Card.get_string_from_id(ele));
        }
    }

    public int pop_card_from_deck() {
        return this.stock.remove(0).intValue();
    }

    public int get_stock_size() {
        return this.stock.size();
    }

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
    public ArrayList<Integer> get_stock_pile(){
        return (ArrayList<Integer>) this.stock.clone();
    }

}
