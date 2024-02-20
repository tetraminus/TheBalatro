package code.hands;

import code.cards.PokerCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;
import java.util.Comparator;

import static code.TheBalatroMod.logger;
import static code.TheBalatroMod.makeID;

public class StraightHand extends AbstractHand {

    public static final String ID = "StraightHand";

    public StraightHand() {
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
        for (int i = 0; i < hand.size() - 1; i++) {
            // ace wraps around, ranks are 1-13

            logger.info("Checking " + hand.get(i).getRank() + " and " + hand.get(i + 1).getRank());

            if (hand.get(i+1).getRank() == 13 && hand.get(0).getRank() == 1) {
                continue;
            }
            if ((hand.get(i).getRank() + 1 != hand.get(i + 1).getRank())) {
                return false;
            }


        }
        return true;

    }

    public ArrayList<PokerCard> getInHand(ArrayList<PokerCard> hand) {
        ArrayList<PokerCard> straightCards = new ArrayList<>();
        hand.sort(Comparator.comparingInt(PokerCard::getRank));
        for (int i = 0; i < hand.size() - 1; i++) {
            if (hand.get(i+1).getRank() == 13 && hand.get(0).getRank() == 1) {
                straightCards.add(hand.get(i+1));
                continue;
            }
            if ((hand.get(i).getRank() + 1 == hand.get(i + 1).getRank())) {
                straightCards.add(hand.get(i));
                straightCards.add(hand.get(i + 1));
            }
        }
        return straightCards;
    }
    public int chips() {
        return 40;
    }
    public int mult() {
        return 4;
    }
}
