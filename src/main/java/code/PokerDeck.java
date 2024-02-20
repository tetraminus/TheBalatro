package code;

import basemod.abstracts.CustomSavable;
import code.cards.PokerCard;
import code.hands.*;
import code.screens.PokerCardSelectScreen;
import code.ui.PokerDrawPileButton;
import code.util.Wiz;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.DrawPilePanel;

import java.util.ArrayList;
import java.util.Comparator;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

public class PokerDeck {


    public CardGroup PokerMasterDeck = new CardGroup(CardGroup.CardGroupType.MASTER_DECK);
    public CardGroup PokerDrawPile = new CardGroup(CardGroup.CardGroupType.DRAW_PILE);
    public CardGroup TempHand = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
    public boolean PokerActive = false;
    public static ArrayList<AbstractHand> PokerHands = new ArrayList<>();
    public static PokerCardSelectScreen pokerCardSelectScreen = new PokerCardSelectScreen();
    public static PokerDrawPileButton combatPokerPileButton = new PokerDrawPileButton();


    public static ArrayList<AbstractCard> generateDeck() {
        ArrayList<AbstractCard> deck = new ArrayList<>();
        for (int i = 1; i <= 13; i++) {
            for (PokerCard.Suits suit : PokerCard.Suits.values()) {
                deck.add(new PokerCard(i, suit));
            }

        }
        return deck;
    }

    public static Float scoreHand(ArrayList<AbstractCard> hand) {

        //cast all cards to poker cards
        ArrayList<PokerCard> pokerHand = new ArrayList<>();
        for (AbstractCard c : hand) {
            pokerHand.add((PokerCard) c);
        }

        for (AbstractHand h : PokerHands) {
            if (h.detect(pokerHand)) {
                return (float) h.chips() * h.mult();
            }

        }
        return 0f;
    }


    public static AbstractHand getHand(ArrayList<AbstractCard> hand) {

        //cast all cards to poker cards
        ArrayList<PokerCard> pokerHand = new ArrayList<>();
        for (AbstractCard c : hand) {
            pokerHand.add((PokerCard) c);
        }

        for (AbstractHand h : PokerHands) {
            if (h.detect(pokerHand)) {
                return h;
            }

        }
        return null;
    }

    public void initializeDeck() {
        PokerMasterDeck.clear();
        PokerDrawPile.clear();
        PokerMasterDeck.group.addAll(generateDeck());
    }

    public void shuffle() {
        PokerDrawPile.clear();
        PokerDrawPile.group.addAll(PokerMasterDeck.group);
        PokerDrawPile.shuffle();
    }

    public void StartPokerHand() {
        TempHand.clear();
        TempHand.group.addAll(player.hand.group);
        player.hand.clear();
        for (int i = 0; i < 8; i++) {
            draw();
        }

        PokerActive = true;
        player.hand.refreshHandLayout();
    }

    public void EndPokerHand() {
        PokerActive = false;
        player.hand.clear();
        player.hand.group.addAll(TempHand.group);
        TempHand.clear();
    }

    public void draw() {
        if (!PokerDrawPile.isEmpty()) {
            AbstractCard c = PokerDrawPile.getTopCard();
            PokerDrawPile.removeCard(c);
            player.hand.addToHand(c);
        }
    }

    public PokerCard drawCard() {
        if (!PokerDrawPile.isEmpty()) {
            AbstractCard c = PokerDrawPile.getTopCard();
            PokerDrawPile.removeCard(c);
            return (PokerCard) c;
        }
        return null;
    }

    public static boolean isPokerScreenOpen() {
        return pokerCardSelectScreen.isOpen;
    }

    public static void pokerScreenUpdate() {
        pokerCardSelectScreen.update();
    }

    public static void pokerScreenRender(SpriteBatch sb) {
        pokerCardSelectScreen.render(sb);
    }




    static {


        PokerHands.add(new FlushHand());
        PokerHands.add(new StraightHand());
        PokerHands.add(new PairHand());
        PokerHands.add(new TwoPairHand());
        PokerHands.add(new FullHouseHand());
        PokerHands.add(new HighCardHand());

        PokerHands.sort(Comparator.comparingInt(AbstractHand::Priority));
    }
    @SpirePatch(
            clz = DrawPilePanel.class,
            method = "render"
    )
    public static class RenderCollectionButton {
        public static void Postfix(DrawPilePanel __instance, SpriteBatch spriteBatch) {
            if (Wiz.isInCombat() && player.chosenClass.equals(TheBalatro.Enums.THE_BALATRO)) {
                if (combatPokerPileButton != null) {
                    combatPokerPileButton.setX(AbstractDungeon.overlayMenu.combatDeckPanel.current_x);
                    combatPokerPileButton.render(spriteBatch);
                }
            }
        }
    }

    public static ArrayList<PokerCard> convertToPokerCards(ArrayList<AbstractCard> hand) {
        ArrayList<PokerCard> pokerHand = new ArrayList<>();
        for (AbstractCard c : hand) {
            pokerHand.add((PokerCard) c);
        }
        return pokerHand;
    }

    public static class PokerSaver implements CustomSavable<ArrayList<PokerCard.PokerCardSaveDummy>>{

        @Override
        public ArrayList<PokerCard.PokerCardSaveDummy> onSave() {
            if (player instanceof TheBalatro) {
                ArrayList<PokerCard.PokerCardSaveDummy> save = new ArrayList<>();

                for (AbstractCard c : ((TheBalatro) player).PokerDeck.PokerMasterDeck.group) {
                    save.add(new PokerCard.PokerCardSaveDummy(c));
                }
                return save;
            }
            return new ArrayList<>();
        }

        @Override
        public void onLoad(ArrayList<PokerCard.PokerCardSaveDummy> pokerCardSaveDummies) {
            if (player instanceof TheBalatro && pokerCardSaveDummies != null) {
                ((TheBalatro) player).PokerDeck.PokerMasterDeck.clear();
                for (PokerCard.PokerCardSaveDummy c : pokerCardSaveDummies) {
                    ((TheBalatro) player).PokerDeck.PokerMasterDeck.addToTop(c.GetCard());
                }
            }

        }
    }

}
