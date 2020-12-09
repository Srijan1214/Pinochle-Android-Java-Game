/*
 ************************************************************
 * Name: Srijan Prasad Joshi
 * Project: Pinochle Java/Android
 * Class: OPL Fall 20
 * Date: 12/8/2020
 ************************************************************
*/

package com.example.pinochleopl;

// These are the states the model is going to be in.
// These states tell the controller, what should be seen in the view
public enum ModelState {
    HUMAN_THROWING_CARD,
    COMPUTER_THREW_CARD,
    HUMAN_PLAYING_MELD,
    COMPUTER_PLAYED_MELD,
    ROUND_ENDED,
    PLAYED_INVALID_MELD,
    GAME_ENDED
}
