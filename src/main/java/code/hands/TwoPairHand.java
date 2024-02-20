package code.hands;

import code.cards.PokerCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;

import static code.TheBalatroMod.makeID;

public class TwoPairHand extends AbstractHand{

    public static final String ID = "TwoPairHand";

    public TwoPairHand() {
        super(ID);
    }
    @Override
    public int Priority() {
        return 4;
    }

    @Override
    public boolean detect(ArrayList<PokerCard> hand) {
        int pairCount = 0;
        ArrayList<Integer> existingRanks = new ArrayList<>();

        for (int i = 0; i < hand.size(); i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                if (hand.get(i).getRank() == hand.get(j).getRank() && !existingRanks.contains(hand.get(i).getRank() )) {
                    if (!existingRanks.contains(hand.get(i).getRank())) {
                        existingRanks.add(hand.get(i).getRank());
                    }
                    pairCount++;
                }
            }
        }
        return pairCount >= 2;
    }

    @Override
    public ArrayList<PokerCard> getInHand(ArrayList<PokerCard> hand) {
        ArrayList<PokerCard> pairCards = new ArrayList<>();
        ArrayList<Integer> existingRanks = new ArrayList<>();
        for (int i = 0; i < hand.size(); i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                if (hand.get(i).getRank() == hand.get(j).getRank() && !existingRanks.contains(hand.get(i).getRank() )) {
                    if (!existingRanks.contains(hand.get(i).getRank())) {
                        existingRanks.add(hand.get(i).getRank());
                    }
                    pairCards.add(hand.get(i));
                    pairCards.add(hand.get(j));
                }
            }
        }
        return pairCards;
    }

    @Override
    public int chips() {
        return 40;
    }

    @Override
    public int mult() {
        return 2;
    }

    @Override
    public String name() {
        return super.name();
    }
}
