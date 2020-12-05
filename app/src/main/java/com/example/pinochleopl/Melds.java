package com.example.pinochleopl;

public class Melds {
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

    public static int get_meld_score(int meld_9) {
        return meld_scores[meld_9];
    }

    public static String get_meld_name(int meld_9) {
        return  meld_names[meld_9];
    }
}
