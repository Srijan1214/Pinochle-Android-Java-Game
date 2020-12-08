/*
 ************************************************************
 * Name: Srijan Prasad Joshi
 * Project: Pinochle Java/Android
 * Class: OPL Fall 20
 * Date: 12/8/2020
 ************************************************************
*/

package com.example.pinochleopl;

public class Melds {
    // The meld values 
    public static final int FLUSH = 0;
    public static final int ROYAL_MARRIAGE = 1;
    public static final int MARRIAGE = 2;
    public static final int DIX = 3;
    public static final int FOUR_ACES = 4;
    public static final int FOUR_KINGS = 5;
    public static final int FOUR_QUEENS = 6;
    public static final int FOUR_JACKS = 7;
    public static final int PINOCHLE = 8;
    public static final int INVALID = -1;

    private static final int[] meld_scores = {150, 40, 20, 10, 100, 80, 60, 40, 40};
    private static final String[] meld_names = {"Flush", "Royal Marriage", "Marriage", "Dix", "Four Aces",
            "Four Kings", "Four Queens", "Four Jacks", "Pinochle"};

    /* *********************************************************************
    Function Name: get_meld_score 
    Purpose: To calculate the average grade in a class
    Parameters:
				int meld_9, an integer, that tells the meld number in a 9-based index.
    Return Value: The meld score of the meld number. An integer value.
    Local Variables:
				None
    Assistance Received: none
    ********************************************************************* */
    public static int get_meld_score(int meld_9) {
        return meld_scores[meld_9];
    }

    /* *********************************************************************
    Function Name: get_meld_name 
    Purpose: To calculate the average grade in a class
    Parameters:
				int meld_9, an integer, that tells the meld number in a 9-based index.
    Return Value: The name of the meld number in terms of string.
    Local Variables:
    			None
    Assistance Received: none
    ********************************************************************* */
    public static String get_meld_name(int meld_9) {
        return  meld_names[meld_9];
    }
}
