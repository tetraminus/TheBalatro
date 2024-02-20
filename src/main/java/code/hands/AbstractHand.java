package code.hands;

import code.cards.PokerCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;

import static code.TheBalatroMod.makeID;

public abstract class AbstractHand {

    private String id;

    public UIStrings uiStrings;

    public AbstractHand(String id) {
        this.id = makeID(id);
        uiStrings = CardCrawlGame.languagePack.getUIString(this.id);
    }


    public abstract int Priority();

    public abstract boolean detect(ArrayList<PokerCard> hand);

    public abstract ArrayList<PokerCard> getInHand(ArrayList<PokerCard> hand);

    public abstract int chips();

    public abstract int mult();
    public String name() {
        return uiStrings.TEXT[0];
    }


}
