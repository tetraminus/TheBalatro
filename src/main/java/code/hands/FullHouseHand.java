package code.hands;

import code.cards.PokerCard;

import java.util.ArrayList;
import java.util.Comparator;

public class FullHouseHand extends AbstractHand{
    public static final String ID = "FullHouseHand";

    public FullHouseHand() {
        super(ID);
    }

    public int Priority() {
        return 2;
    }

    public boolean detect(ArrayList<PokerCard> hand) {
        if (hand.size() < 5) {
            return false;
        }
        hand.sort(Comparator.comparingInt(PokerCard::getRank));
        if (hand.get(0).getRank() == hand.get(1).getRank() && hand.get(1).getRank() == hand.get(2).getRank() && hand.get(3).getRank() == hand.get(4).getRank()) {
            return true;
        }
        return hand.get(0).getRank() == hand.get(1).getRank() && hand.get(2).getRank() == hand.get(3).getRank() && hand.get(3).getRank() == hand.get(4).getRank();
    }

    public ArrayList<PokerCard> getInHand(ArrayList<PokerCard> hand) {
        ArrayList<PokerCard> fullHouseCards = new ArrayList<>();
        if (hand.size() < 5) {
            return fullHouseCards;
        }
        hand.sort(Comparator.comparingInt(PokerCard::getRank));
        if (hand.get(0).getRank() == hand.get(1).getRank() && hand.get(1).getRank() == hand.get(2).getRank() && hand.get(3).getRank() == hand.get(4).getRank()) {
            fullHouseCards.add(hand.get(0));
            fullHouseCards.add(hand.get(1));
            fullHouseCards.add(hand.get(2));
            fullHouseCards.add(hand.get(3));
            fullHouseCards.add(hand.get(4));
            return fullHouseCards;
        }
        if (hand.get(0).getRank() == hand.get(1).getRank() && hand.get(2).getRank() == hand.get(3).getRank() && hand.get(3).getRank() == hand.get(4).getRank()) {
            fullHouseCards.add(hand.get(0));
            fullHouseCards.add(hand.get(1));
            fullHouseCards.add(hand.get(2));
            fullHouseCards.add(hand.get(3));
            fullHouseCards.add(hand.get(4));
            return fullHouseCards;
        }
        return fullHouseCards;
    }

    public int chips() {
        return 40;
    }
    public int mult() {
        return 4;
    }

}
