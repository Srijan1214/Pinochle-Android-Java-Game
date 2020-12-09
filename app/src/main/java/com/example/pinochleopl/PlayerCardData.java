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

// An instance of this class is like a mini Player object, by we only get the necessary card information
public class PlayerCardData {
    public ArrayList<Integer> hand_card_pile;
    public ArrayList<ArrayList<ArrayList<Integer>>> hand_meld_involvement_list;
    public ArrayList<ArrayList<ArrayList<Integer>>> current_meld_cards;
    public ArrayList<Integer> capture_pile;
}
