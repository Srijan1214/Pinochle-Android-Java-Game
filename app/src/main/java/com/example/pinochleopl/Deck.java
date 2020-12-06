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
        Collections.shuffle(this.stock, new Random(5));

        for (int ele: this.stock) {
            System.out.println(Card.get_string_from_id(ele));
        }
    }

    public int pop_card_from_deck() {
        return this.stock.remove(this.stock.size() - 1).intValue();
    }

    public int get_stock_size() {
        return this.stock.size();
    }

    public String get_stock_string(){
        return "";
    }
    public ArrayList<Integer> get_stock_pile(){
        return (ArrayList<Integer>) this.stock.clone();
    }

}