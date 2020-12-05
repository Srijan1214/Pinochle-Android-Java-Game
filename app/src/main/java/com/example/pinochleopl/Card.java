package com.example.pinochleopl;

public class Card {
    private final static int[] FACE_WEIGHTS = {0, 10, 2, 3, 4, 11};
    private final static char[] FACE_CHAR_ARRAY = {'9', 'X', 'J', 'Q', 'K', 'A'};
    private final static char[] SUIT_CHAR_ARRAY = {'S', 'C', 'H', 'D'};

    public final static int NINE_FACE = 0;
    public final static int TEN_FACE = 1;
    public final static int JACK_FACE = 2;
    public final static int QUEEN_FACE = 3;
    public final static int KING_FACE = 4;
    public final static int ACE_FACE = 5;

    public final static int SPADES_SUIT = 0;
    public final static int CLUBS_SUIT = 1;
    public final static int HEARTS_SUIT = 2;
    public final static int DIAMONDS_SUIT = 3;

    public static int get_face_from_id(int id) {
        return (int) ((id % Constants.NO_OF_CARDS_PER_SUIT) / Constants.NO_OF_SAME_CARDS);
    }

    public static int get_suit_from_id(int id) {
        return (int) (id / Constants.NO_OF_CARDS_PER_SUIT);
    }

    public static int get_id_from_face_and_suit(int face, int suit) {
        return (int) (suit * Constants.NO_OF_CARDS_PER_SUIT + face * Constants.NO_OF_SAME_CARDS);
    }

    public static int get_face_weight_from_id(int id) {
        return FACE_WEIGHTS[get_face_from_id(id)];
    }

    public static String get_string_from_id(int id) {
        StringBuilder sb = new StringBuilder();
        sb.append(FACE_CHAR_ARRAY[get_face_from_id(id)]);
        sb.append(SUIT_CHAR_ARRAY[get_suit_from_id(id)]);
        return (sb.toString());
    }

    public static int get_id_from_string(String card_string) {
        int face_val, suit_val;
        switch (card_string.charAt(0)) {
            case '9':
                face_val = 0;
                break;
            case 'X':
                face_val = 1;
                break;
            case 'J':
                face_val = 2;
                break;
            case 'Q':
                face_val = 3;
                break;
            case 'K':
                face_val = 4;
                break;
            case 'A':
                face_val = 5;
                break;
            default:
                face_val = -1;
        }
        switch (card_string.charAt(1)) {
            case 'S':
                suit_val = 0;
                break;
            case 'C':
                suit_val = 1;
                break;
            case 'H':
                suit_val = 2;
                break;
            case 'D':
                suit_val = 3;
                break;
            default:
                suit_val = -1;
        }

        return suit_val * Constants.NO_OF_CARDS_PER_SUIT + face_val * Constants.NO_OF_SAME_CARDS;
    }

    public static boolean is_first_card_greater_than_lead(int card_first, int card_lead, int trump_card) {
        int TRUMP_SUIT = get_suit_from_id(trump_card);
        if (get_suit_from_id(card_first) == TRUMP_SUIT && get_suit_from_id(card_lead) != TRUMP_SUIT) {
            return true;
        }
        if (get_suit_from_id(card_lead) == TRUMP_SUIT && get_suit_from_id(card_first) != TRUMP_SUIT) {
            return false;
        }
        if (get_suit_from_id(card_first) != get_suit_from_id(card_lead)) {
            return false;
        }
        if (get_face_weight_from_id(card_first) > get_face_weight_from_id(card_lead)) {
            return true;
        }
        return false;
    }
}
