package code.actions;

import code.cards.PokerCard;
import code.hands.AbstractHand;
import code.vfx.ScoreHandEffect;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.function.Consumer;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

public class ScoreHandAction extends AbstractGameAction {

    private ArrayList<PokerCard> hand;
    private Consumer<Float> callback;
    private ScoreHandEffect effect;

    public ScoreHandAction(ArrayList<PokerCard> hand, AbstractHand handType, Consumer<Float> callback) {
        this.actionType = ActionType.SPECIAL;
        this.amount = 0;
        this.duration = 2f + hand.size() * 0.5f;
        this.callback = callback;
        this.hand = hand;
        if(hand.isEmpty()){
            this.isDone = true;
            return;
        }
        effect = new ScoreHandEffect(hand, handType, callback);
        AbstractDungeon.effectsQueue.add(effect);
    }

    public void update() {

        if (effect.isDone) {
            this.isDone = true;
        }
    }

}
