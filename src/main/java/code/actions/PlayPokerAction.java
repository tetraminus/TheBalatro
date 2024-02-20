package code.actions;

import code.PokerDeck;
import code.TheBalatro;
import com.megacrit.cardcrawl.actions.AbstractGameAction;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class PlayPokerAction extends AbstractGameAction {
    private Consumer<Float> callback;

    private String text;
    private ArrayList<AbstractCard> hand;
    private ArrayList<AbstractCard> tempHand;

    public PlayPokerAction(int amount, Consumer<Float> callback) {
        ((TheBalatro) AbstractDungeon.player).PokerDeck.StartPokerHand();
        this.tempHand = new ArrayList<>();// 20
        this.amount = 5;// 42
        this.duration = this.startDuration = Settings.ACTION_DUR_XFAST;// 43
        this.text = "bingus";// 44
        this.callback = callback;// 48
        this.hand = player.hand.group;// 49
    }// 50


    public void update() {
        if (this.duration == this.startDuration) {// 70
            if (this.callback == null) {// 72
                this.isDone = true;// 73
            } else {
                if (this.hand.isEmpty()) {// 79
                    this.isDone = true;
                    ((TheBalatro) AbstractDungeon.player).PokerDeck.EndPokerHand();
                } else if (this.hand.size() <= this.amount) {// 84
                    addToBot(new ScoreHandAction(PokerDeck.convertToPokerCards(this.hand), PokerDeck.getHand(this.hand) , this.callback));// 85
                    this.hand.clear();//
                    this.isDone = true;
                    ((TheBalatro) AbstractDungeon.player).PokerDeck.EndPokerHand();
                } else {
                    PokerDeck.pokerCardSelectScreen.open(this.text, this.amount, true, true);// 93
                    this.tickDuration();// 94
                }
            }
        } else if (!PokerDeck.pokerCardSelectScreen.wereCardsRetrieved) {// 98
            hand.clear();
            this.hand.addAll(PokerDeck.pokerCardSelectScreen.selectedCards.group);// 100
            PokerDeck.pokerCardSelectScreen.wereCardsRetrieved = true;// 101
            PokerDeck.pokerCardSelectScreen.selectedCards.group.clear();// 102
            addToBot(new ScoreHandAction(PokerDeck.convertToPokerCards(this.hand), PokerDeck.getHand(this.hand),  this.callback));
            this.isDone = true;
            ((TheBalatro) AbstractDungeon.player).PokerDeck.EndPokerHand();
        } else {
            this.tickDuration();// 106
        }
    }// 74 81 90 95 104 107


}
