package code.hands;

import code.cards.PokerCard;

import java.util.ArrayList;

public class HighCardHand extends AbstractHand {
    public static final String ID = "HighCardHand";

    public HighCardHand() {
        super(ID);
    }

    public int Priority() {
        return 99;
    }

    @Override
    public boolean detect(ArrayList<PokerCard> hand) {
        return !hand.isEmpty();
    }

    @Override
    public ArrayList<PokerCard> getInHand(ArrayList<PokerCard> hand) {
        //return highest card
        if (hand.isEmpty()) {
            return new ArrayList<>();
        }
        hand.sort((c1, c2) -> c2.getRank() - c1.getRank());
        ArrayList<PokerCard> highCard = new ArrayList<>();
        highCard.add(hand.get(0));
        return highCard;
    }


    public int chips() {
        return 5;
    }

    public int mult() {
        return 1;
    }
}
