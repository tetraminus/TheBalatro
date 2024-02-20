package code.hands;

import code.cards.PokerCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;

import static code.TheBalatroMod.makeID;

public class FlushHand extends AbstractHand{

    public static final String ID = "FlushHand";
    public FlushHand() {
        super(ID);
    }
    public int Priority() {
        return 4;
    }
    public boolean detect(ArrayList<PokerCard> hand) {
        if (hand.size() < 5) {
            return false;
        }
        return hand.stream().allMatch(card -> card.getSuit() == hand.get(0).getSuit());
    }

    public ArrayList<PokerCard> getInHand(ArrayList<PokerCard> hand) {
        ArrayList<PokerCard> flushCards = new ArrayList<>();
        if (hand.size() < 5) {
            return flushCards;
        }
        for (PokerCard c : hand) {
            if (c.getSuit() == hand.get(0).getSuit()) {
                flushCards.add(c);
            }
        }
        return flushCards;
    }

    public int chips() {
        return 20;
    }
    public int mult() {
        return 2;
    }
}
