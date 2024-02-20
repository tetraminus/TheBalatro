package code.vfx;

import code.cards.PokerCard;
import code.hands.AbstractHand;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ScoreHandEffect extends AbstractGameEffect {

    private ArrayList<PokerCard> hand;
    private ArrayList<PokerCard> ScoringCards;

    float handDuration;
    private Consumer<Float> callback;
    public ScoreHandEffect(ArrayList<PokerCard> hand, AbstractHand handType, Consumer<Float> callback) {
        this.duration = 2f + hand.size() * 0.2f;
        this.handDuration = hand.size() * 0.2f;
        this.callback = callback;
        this.hand = hand;
        this.ScoringCards = handType.getInHand(hand);
        // layout hand
        ArrangeHand(hand);

    }

    private static void ArrangeHand(ArrayList<PokerCard> cards) {
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).target_x = (Settings.WIDTH / 2f) - (((cards.size() - 1) * 200f) / 2f) + (i * 200f);
            cards.get(i).target_y = Settings.HEIGHT / 2f - 200f;
            cards.get(i).targetAngle = 0f;
        }
    }

    private ArrayList<PokerCard> movedCards = new ArrayList<>();
    boolean removed = false;
    public void update() {
        for (PokerCard c : hand) {
            c.update();
        }
        //move non scoring cards down
        if (!removed && this.duration < (2f + hand.size() * 0.2f) - 0.2f) {
            for (PokerCard c : hand) {
                if (!ScoringCards.contains(c) && !movedCards.contains(c)) {
                    c.target_y -= 2000f;
                    movedCards.add(c);
                }

            }
            ArrangeHand(ScoringCards);
            removed = true;
        }
        

        // move each scoring card up in order with a slight delay
        for (int i = 0; i < ScoringCards.size(); i++) {
            if (this.duration - 1 < handDuration - (i * 0.2f) && !movedCards.contains(ScoringCards.get(i))) {
                ScoringCards.get(i).target_y += 200f;
                movedCards.add(ScoringCards.get(i));
            }
        }

        this.duration -= com.badlogic.gdx.Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F) {
            this.isDone = true;
            this.callback.accept(0f);
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        for (PokerCard c : hand) {
            c.render(spriteBatch);
        }
    }

    @Override
    public void dispose() {

    }

}
