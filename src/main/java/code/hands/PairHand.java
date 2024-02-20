package code.hands;

import code.cards.PokerCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;

import static code.TheBalatroMod.makeID;

public class PairHand extends AbstractHand {

    public static final String ID = "PairHand";

    public PairHand() {
        super(ID);
    }
    public int Priority() {
        return 5;
    }
    public boolean detect(ArrayList<PokerCard> hand) {
        for (int i = 0; i < hand.size(); i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                if (hand.get(i).getRank() == hand.get(j).getRank()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ArrayList<PokerCard> getInHand(ArrayList<PokerCard> hand) {
        ArrayList<PokerCard> pairCards = new ArrayList<>();
        for (int i = 0; i < hand.size(); i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                if (hand.get(i).getRank() == hand.get(j).getRank()) {
                    pairCards.add(hand.get(i));
                    pairCards.add(hand.get(j));
                }
            }
        }
        return pairCards;
    }

    public int chips() {
        return 10;
    }
    public int mult() {
        return 1;
    }

}
