package com.example.pinochleopl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Deck {
    private static final int TOTAL_NO_OF_CARDS = 48;
    private ArrayList<Integer> m_stock;

    public Deck() {
        m_stock = new ArrayList<Integer>();
        for (int i = 0; i < TOTAL_NO_OF_CARDS; i++) {
            m_stock.add(i);
        }
        Collections.shuffle(m_stock, new Random(5));
    }

    public int pop_card_from_deck() {
        return m_stock.remove(m_stock.size() - 1).intValue();
    }

    public int get_stock_size() {
        return m_stock.size();
    }

    public ArrayList<Integer> get_stock_pile(){
        return (ArrayList<Integer>) m_stock.clone();
    }

}
