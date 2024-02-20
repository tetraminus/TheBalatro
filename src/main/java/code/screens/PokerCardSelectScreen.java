package code.screens;

import code.PokerDeck;
import code.TheBalatro;
import code.hands.AbstractHand;
import code.ui.CardSelectDiscardButton;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;
import com.megacrit.cardcrawl.ui.buttons.CardSelectConfirmButton;

import com.megacrit.cardcrawl.ui.buttons.PeekButton;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;
import java.util.Iterator;

import static code.TheBalatroMod.makeID;
import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

public class PokerCardSelectScreen {
    public static final float MIN_HOVER_DIST = 64.0F * Settings.scale;
    public static final float HOVER_CARD_Y_POSITION = 210.0F * Settings.scale;
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("PokerCardSelectScreen"));
    public static final String[] TEXT = uiStrings.TEXT;
    private static final int ARROW_W = 64;
    public int numCardsToSelect;
    public CardGroup selectedCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
    public AbstractCard hoveredCard;
    public AbstractCard upgradePreviewCard = null;
    public String selectionReason;
    public boolean wereCardsRetrieved = false;
    public boolean canPickZero = false;
    public boolean upTo = false;
    public CardSelectConfirmButton button = new CardSelectConfirmButton();
    public CardSelectDiscardButton discardButton = new CardSelectDiscardButton();
    public int numSelected = 0;
    private String message = "";
    private PeekButton peekButton = new PeekButton();
    private boolean anyNumber = false;
    private boolean forTransform = false;
    private boolean forUpgrade;
    private boolean waitThenClose = false;
    private float waitToCloseTimer = 0.0F;
    private CardGroup hand;
    private float arrowScale1 = 0.75F, arrowScale2 = 0.75F, arrowScale3 = 0.75F, arrowTimer = 0.0F;
    public boolean isOpen = false;

    public int CurrentChips = 0;
    public int CurrentMult = 0;

    public void update() {
        updateControllerInput();
        this.peekButton.update();

        if (!PeekButton.isPeeking) {
            updateHand();
            updateSelectedCards();
            if (this.waitThenClose) {
                this.waitToCloseTimer -= Gdx.graphics.getDeltaTime();
                if (this.waitToCloseTimer < 0.0F) {
                    this.waitThenClose = false;
                    AbstractDungeon.closeCurrentScreen();
                    isOpen = false;
                    if (this.forTransform && this.selectedCards.size() == 1) {
                        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
                            AbstractDungeon.srcTransformCard(this.selectedCards.getBottomCard());
                        } else {
                            AbstractDungeon.transformCard(this.selectedCards.getBottomCard());
                        }
                        this.selectedCards.group.clear();
                    }
                }
            }
            if (Settings.FAST_HAND_CONF && this.numCardsToSelect == 1 && this.selectedCards.size() == 1 && !this.canPickZero &&
                    !this.waitThenClose) {
                InputHelper.justClickedLeft = false;
                this.waitToCloseTimer = 0.25F;
                this.waitThenClose = true;
                return;
            }
            this.button.update();
            if (this.button.hb.clicked || CInputActionSet.proceed.isJustPressed() || InputActionSet.confirm
                    .isJustPressed()) {
                CInputActionSet.proceed.unpress();
                this.button.hb.clicked = false;
                if (this.canPickZero && this.selectedCards.size() == 0) {
                    InputHelper.justClickedLeft = false;
                    AbstractDungeon.closeCurrentScreen();
                    isOpen = false;
                    return;
                }
                if (this.anyNumber || this.upTo) {
                    InputHelper.justClickedLeft = false;
                    AbstractDungeon.closeCurrentScreen();
                    isOpen = false;
                    return;
                }
                if (this.selectedCards.size() == this.numCardsToSelect) {
                    InputHelper.justClickedLeft = false;
                    AbstractDungeon.closeCurrentScreen();
                    isOpen = false;
                    if (this.forTransform && this.selectedCards.size() == 1) {
                        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
                            AbstractDungeon.srcTransformCard(this.selectedCards.getBottomCard());
                        } else {
                            AbstractDungeon.transformCard(this.selectedCards.getBottomCard());
                        }
                        this.selectedCards.group.clear();
                    }
                    return;
                }
            }
            discardButton.update();
            if (this.discardButton.hb.clicked) {
                this.discardButton.hb.clicked = false;
                int prevSelected = this.selectedCards.size();
                this.selectedCards.clear();

                this.numSelected = 0;
                this.discardButton.hide();
                if (player.chosenClass == TheBalatro.Enums.THE_BALATRO) {
                    for (int i = 0; i < prevSelected; i++) {
                        this.hand.addToTop(((TheBalatro) player).PokerDeck.drawCard());
                    }
                    this.hand.refreshHandLayout();

                }
            }
        }
    }

    private void updateControllerInput() {
        if (!Settings.isControllerMode)
            return;
        boolean inHand = true;
        boolean anyHovered = false;
        int index = 0;
        int y = 0;
        for (AbstractCard c : this.selectedCards.group) {
            if (c.hb.hovered) {
                anyHovered = true;
                inHand = false;
                break;
            }
            index++;
        }
        if (inHand) {
            index = 0;
            for (AbstractCard c : this.hand.group) {
                if (c == this.hoveredCard) {
                    anyHovered = true;
                    break;
                }
                index++;
            }
        }
        if (!anyHovered) {
            if (!this.hand.group.isEmpty()) {
                setHoveredCard(this.hand.group.get(0));
                Gdx.input.setCursorPosition((int) this.hoveredCard.hb.cX, Settings.HEIGHT - (int) this.hoveredCard.hb.cY);
            } else if (!this.selectedCards.isEmpty()) {
                Gdx.input.setCursorPosition(
                        (int) ((AbstractCard) this.selectedCards.group.get(0)).hb.cX, Settings.HEIGHT -
                                (int) ((AbstractCard) this.selectedCards.group.get(0)).hb.cY);
            }
        } else if (!inHand) {
            if ((CInputActionSet.down.isJustPressed() || CInputActionSet.altDown.isJustPressed()) &&
                    !this.hand.group.isEmpty()) {
                index = 0;
                if (((AbstractCard) this.hand.group.get(index)).hb.cY < 0.0F) {
                    y = 1;
                } else {
                    y = (int) ((AbstractCard) this.hand.group.get(index)).hb.cY;
                }
                Gdx.input.setCursorPosition((int) ((AbstractCard) this.hand.group.get(index)).hb.cX, Settings.HEIGHT - y);
                setHoveredCard(this.hand.group.get(index));
            } else if (CInputActionSet.right.isJustPressed() || CInputActionSet.altRight.isJustPressed()) {
                index++;
                if (index > this.selectedCards.size() - 1)
                    index = 0;
                Gdx.input.setCursorPosition(
                        (int) ((AbstractCard) this.selectedCards.group.get(index)).hb.cX, Settings.HEIGHT -
                                (int) ((AbstractCard) this.selectedCards.group.get(index)).hb.cY);
                unhoverCard(this.hoveredCard);
            } else if (CInputActionSet.left.isJustPressed() || CInputActionSet.altLeft.isJustPressed()) {
                index--;
                if (index < 0)
                    index = this.selectedCards.size() - 1;
                Gdx.input.setCursorPosition(
                        (int) ((AbstractCard) this.selectedCards.group.get(index)).hb.cX, Settings.HEIGHT -
                                (int) ((AbstractCard) this.selectedCards.group.get(index)).hb.cY);
                unhoverCard(this.hoveredCard);
            } else if (CInputActionSet.select.isJustPressed()) {
                CInputActionSet.select.unpress();
                if (((AbstractCard) this.selectedCards.group.get(index)).hb.hovered) {
                    AbstractCard tmp = this.selectedCards.group.get(index);
                    player.hand.addToTop(tmp);
                    this.selectedCards.group.remove(tmp);
                    refreshSelectedCards();
                    updateMessage();
                    this.hand.refreshHandLayout();
                }
            }
        } else if ((CInputActionSet.up.isJustPressed() || CInputActionSet.altUp.isJustPressed()) &&
                !this.selectedCards.isEmpty()) {
            index = 0;
            if (((AbstractCard) this.selectedCards.group.get(index)).hb.cY < 0.0F) {
                y = 1;
            } else {
                y = (int) ((AbstractCard) this.selectedCards.group.get(index)).hb.cY;
            }
            unhoverCard(this.hoveredCard);
            Gdx.input.setCursorPosition((int) ((AbstractCard) this.selectedCards.group.get(index)).hb.cX, Settings.HEIGHT - y);
        } else if (this.hand.size() > 1 && (CInputActionSet.left.isJustPressed() || CInputActionSet.altLeft
                .isJustPressed())) {
            index--;
            if (index < 0)
                index = this.hand.size() - 1;
            unhoverCard(this.hoveredCard);
            setHoveredCard(this.hand.group.get(index));
            Gdx.input.setCursorPosition((int) this.hoveredCard.hb.cX, Settings.HEIGHT - (int) this.hoveredCard.hb.cY);
        } else if (this.hand.size() > 1 && (CInputActionSet.right.isJustPressed() || CInputActionSet.altRight
                .isJustPressed())) {
            index++;
            if (index > this.hand.size() - 1)
                index = 0;
            unhoverCard(this.hoveredCard);
            setHoveredCard(this.hand.group.get(index));
            Gdx.input.setCursorPosition((int) this.hoveredCard.hb.cX, Settings.HEIGHT - (int) this.hoveredCard.hb.cY);
        } else if (this.hand.size() == 1 && this.hoveredCard == null) {
            setHoveredCard(this.hand.group.get(index));
        }
    }

    private void unhoverCard(AbstractCard card) {
        if (card == null)
            return;
        card.targetDrawScale = 0.7F;
        card.hoverTimer = 0.25F;
        card.unhover();
        card = null;
        this.hand.refreshHandLayout();
    }

    private void setHoveredCard(AbstractCard card) {
        this.hoveredCard = card;
        this.hoveredCard.current_y = HOVER_CARD_Y_POSITION;
        this.hoveredCard.target_y = HOVER_CARD_Y_POSITION;
        this.hoveredCard.drawScale = 1.0F;
        this.hoveredCard.targetDrawScale = 1.0F;
        this.hoveredCard.setAngle(0.0F, true);
        this.hand.hoverCardPush(this.hoveredCard);
    }

    private void updateHand() {
        if (!Settings.isControllerMode) {
            hoverCheck();
            unhoverCheck();
        }
        startDraggingCardCheck();
        hotkeyCheck();
    }

    private void refreshSelectedCards() {
        for (AbstractCard c : this.selectedCards.group)
            c.target_y = Settings.HEIGHT / 2.0F + 160.0F * Settings.scale;
        switch (this.selectedCards.size()) {
            case 1:
                if (this.forUpgrade) {
                    ((AbstractCard) this.selectedCards.group.get(0)).target_x = Settings.WIDTH * 0.37F;
                    break;
                }
                ((AbstractCard) this.selectedCards.group.get(0)).target_x = Settings.WIDTH / 2.0F;
                break;
            case 2:
                ((AbstractCard) this.selectedCards.group.get(0)).target_x = Settings.WIDTH / 2.0F - 120.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(1)).target_x = Settings.WIDTH / 2.0F + 120.0F * Settings.scale;
                break;
            case 3:
                ((AbstractCard) this.selectedCards.group.get(0)).target_x = Settings.WIDTH / 2.0F - 240.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(1)).target_x = Settings.WIDTH / 2.0F;
                ((AbstractCard) this.selectedCards.group.get(2)).target_x = Settings.WIDTH / 2.0F + 240.0F * Settings.scale;
                break;
            case 4:
                ((AbstractCard) this.selectedCards.group.get(0)).target_x = Settings.WIDTH / 2.0F - 360.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(1)).target_x = Settings.WIDTH / 2.0F - 120.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(2)).target_x = Settings.WIDTH / 2.0F + 120.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(3)).target_x = Settings.WIDTH / 2.0F + 360.0F * Settings.scale;
                break;
            case 5:
                ((AbstractCard) this.selectedCards.group.get(0)).target_x = Settings.WIDTH / 2.0F - 360.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(1)).target_x = Settings.WIDTH / 2.0F - 180.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(2)).target_x = Settings.WIDTH / 2.0F;
                ((AbstractCard) this.selectedCards.group.get(3)).target_x = Settings.WIDTH / 2.0F + 180.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(4)).target_x = Settings.WIDTH / 2.0F + 360.0F * Settings.scale;
                break;
            case 6:
                ((AbstractCard) this.selectedCards.group.get(0)).target_x = Settings.WIDTH / 2.0F - 450.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(1)).target_x = Settings.WIDTH / 2.0F - 270.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(2)).target_x = Settings.WIDTH / 2.0F - 90.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(3)).target_x = Settings.WIDTH / 2.0F + 90.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(4)).target_x = Settings.WIDTH / 2.0F + 270.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(5)).target_x = Settings.WIDTH / 2.0F + 450.0F * Settings.scale;
                break;
            case 7:
                ((AbstractCard) this.selectedCards.group.get(0)).target_x = Settings.WIDTH / 2.0F - 540.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(1)).target_x = Settings.WIDTH / 2.0F - 360.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(2)).target_x = Settings.WIDTH / 2.0F - 180.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(3)).target_x = Settings.WIDTH / 2.0F;
                ((AbstractCard) this.selectedCards.group.get(4)).target_x = Settings.WIDTH / 2.0F + 180.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(5)).target_x = Settings.WIDTH / 2.0F + 360.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(6)).target_x = Settings.WIDTH / 2.0F + 540.0F * Settings.scale;
                break;
            case 8:
                ((AbstractCard) this.selectedCards.group.get(0)).target_x = Settings.WIDTH / 2.0F - 630.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(1)).target_x = Settings.WIDTH / 2.0F - 450.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(2)).target_x = Settings.WIDTH / 2.0F - 270.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(3)).target_x = Settings.WIDTH / 2.0F - 90.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(4)).target_x = Settings.WIDTH / 2.0F + 90.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(5)).target_x = Settings.WIDTH / 2.0F + 270.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(6)).target_x = Settings.WIDTH / 2.0F + 450.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(7)).target_x = Settings.WIDTH / 2.0F + 630.0F * Settings.scale;
                break;
            case 9:
                ((AbstractCard) this.selectedCards.group.get(0)).target_x = Settings.WIDTH / 2.0F - 720.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(1)).target_x = Settings.WIDTH / 2.0F - 540.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(2)).target_x = Settings.WIDTH / 2.0F - 360.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(3)).target_x = Settings.WIDTH / 2.0F - 180.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(4)).target_x = Settings.WIDTH / 2.0F;
                ((AbstractCard) this.selectedCards.group.get(5)).target_x = Settings.WIDTH / 2.0F + 180.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(6)).target_x = Settings.WIDTH / 2.0F + 360.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(7)).target_x = Settings.WIDTH / 2.0F + 540.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(8)).target_x = Settings.WIDTH / 2.0F + 720.0F * Settings.scale;
                break;
            case 10:
                ((AbstractCard) this.selectedCards.group.get(0)).target_x = Settings.WIDTH / 2.0F - 810.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(1)).target_x = Settings.WIDTH / 2.0F - 630.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(2)).target_x = Settings.WIDTH / 2.0F - 450.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(3)).target_x = Settings.WIDTH / 2.0F - 270.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(4)).target_x = Settings.WIDTH / 2.0F - 90.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(5)).target_x = Settings.WIDTH / 2.0F + 90.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(6)).target_x = Settings.WIDTH / 2.0F + 270.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(7)).target_x = Settings.WIDTH / 2.0F + 450.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(8)).target_x = Settings.WIDTH / 2.0F + 630.0F * Settings.scale;
                ((AbstractCard) this.selectedCards.group.get(9)).target_x = Settings.WIDTH / 2.0F + 810.0F * Settings.scale;
                break;
        }
        if (this.upTo) {
            this.button.enable();
            this.discardButton.enable();
        } else if (this.selectedCards.size() == this.numCardsToSelect) {
            this.button.enable();
            this.discardButton.enable();
        } else if (this.selectedCards.size() > 1 && this.anyNumber && !this.canPickZero) {
            this.button.enable();
            this.discardButton.enable();
        } else if (this.selectedCards.size() != this.numCardsToSelect && !this.anyNumber) {
            this.button.disable();
            this.discardButton.disable();
        } else if (this.anyNumber && this.canPickZero) {
            this.button.enable();
            this.discardButton.enable();
        }

        AbstractHand handtype = PokerDeck.getHand(this.selectedCards.group);

        if (handtype != null) {
            PokerDeck.convertToPokerCards(this.selectedCards.group).forEach(AbstractCard::stopGlowing);
            handtype.getInHand(PokerDeck.convertToPokerCards(this.selectedCards.group)).forEach(card -> {
                card.glowColor = Color.RED;
                card.beginGlowing();
            });

        } else {
            PokerDeck.convertToPokerCards(this.selectedCards.group).forEach(AbstractCard::stopGlowing);
        }
    }

    private void hoverCheck() {
        if (this.hoveredCard == null) {
            this.hoveredCard = this.hand.getHoveredCard();
            if (this.hoveredCard != null) {
                this.hoveredCard.current_y = HOVER_CARD_Y_POSITION;
                this.hoveredCard.target_y = HOVER_CARD_Y_POSITION;
                this.hoveredCard.drawScale = 1.0F;
                this.hoveredCard.targetDrawScale = 1.0F;
                this.hoveredCard.setAngle(0.0F, true);
                this.hand.hoverCardPush(this.hoveredCard);
            }
        }
    }

    private void unhoverCheck() {
        if (this.hoveredCard != null && !this.hoveredCard.isHoveredInHand(1.0F)) {
            this.hoveredCard.targetDrawScale = 0.7F;
            this.hoveredCard.hoverTimer = 0.25F;
            this.hoveredCard.unhover();
            this.hoveredCard = null;
            this.hand.refreshHandLayout();
        }
    }

    private void startDraggingCardCheck() {
        if (this.hoveredCard != null && (InputHelper.justClickedLeft || CInputActionSet.select.isJustPressed())) {
            CInputActionSet.select.unpress();
            if (!Settings.FAST_HAND_CONF || this.numCardsToSelect != 1 || this.selectedCards.size() != 1)
                selectHoveredCard();
        }
    }

    private void selectHoveredCard() {
        if (this.numCardsToSelect > this.selectedCards.group.size()) {
            InputHelper.justClickedLeft = false;
            CardCrawlGame.sound.play("CARD_OBTAIN");
            this.hand.removeCard(this.hoveredCard);
            this.hand.refreshHandLayout();
            this.hoveredCard.setAngle(0.0F, false);
            this.selectedCards.addToTop(this.hoveredCard);
            refreshSelectedCards();
            this.hoveredCard = null;
            updateMessage();
        } else if (this.numCardsToSelect == 1 && this.selectedCards.group.size() == 1) {
            InputHelper.justClickedLeft = false;
            CardCrawlGame.sound.play("CARD_OBTAIN");
            this.hand.removeCard(this.hoveredCard);
            this.hoveredCard.setAngle(0.0F, false);
            this.selectedCards.addToBottom(this.hoveredCard);
            refreshSelectedCards();
            this.hoveredCard = null;
            player.hand.addToTop(this.selectedCards.getTopCard());
            this.selectedCards.removeTopCard();
            refreshSelectedCards();
            this.hand.refreshHandLayout();
            if (this.forUpgrade && this.selectedCards.size() == 1) {
                this.upgradePreviewCard = ((AbstractCard) this.selectedCards.group.get(0)).makeStatEquivalentCopy();
                this.upgradePreviewCard.upgrade();
                this.upgradePreviewCard.displayUpgrades();
                this.upgradePreviewCard.drawScale = 0.75F;
            }
        }
        InputHelper.moveCursorToNeutralPosition();
    }

    private void hotkeyCheck() {
        AbstractCard hotkeyCard = InputHelper.getCardSelectedByHotkey(this.hand);
        if (hotkeyCard != null) {
            this.hoveredCard = hotkeyCard;
            this.hoveredCard.setAngle(0.0F, false);
            selectHoveredCard();
        }
    }

    private void updateSelectedCards() {
        this.selectedCards.update();
        for (Iterator<AbstractCard> i = this.selectedCards.group.iterator(); i.hasNext(); ) {
            AbstractCard e = i.next();
            e.current_x = MathHelper.cardLerpSnap(e.current_x, e.target_x);
            e.current_y = MathHelper.cardLerpSnap(e.current_y, e.target_y);
            e.hb.update();
            if (this.selectedCards.group.size() >= 5) {
                e.targetDrawScale = 0.5F;
                if (Math.abs(e.current_x - e.target_x) < MIN_HOVER_DIST && e.hb.hovered)
                    e.targetDrawScale = 0.66F;
            } else {
                e.targetDrawScale = 0.66F;
                if (this.forUpgrade)
                    e.targetDrawScale = 0.75F;
                if (Math.abs(e.current_x - e.target_x) < MIN_HOVER_DIST && e.hb.hovered)
                    if (this.forUpgrade) {
                        e.targetDrawScale = 0.85F;
                    } else {
                        e.targetDrawScale = 0.75F;
                    }
            }
            if (!this.waitThenClose && Math.abs(e.current_x - e.target_x) < MIN_HOVER_DIST && e.hb.hovered && (InputHelper.justClickedLeft || CInputActionSet.select
                    .isJustPressed())) {
                InputHelper.justClickedLeft = false;
                player.hand.addToTop(e);
                i.remove();
                refreshSelectedCards();
                updateMessage();
                if (Settings.isControllerMode)
                    this.hand.refreshHandLayout();
                break;
            }
        }
        if (this.selectedCards.isEmpty() && !this.canPickZero){
            this.button.disable();
            this.discardButton.disable();
        }

    }

    private void updateMessage() {
        AbstractHand handtype = PokerDeck.getHand(this.selectedCards.group);
        if (handtype != null) {
            CurrentChips = handtype.chips();
            CurrentMult = handtype.mult();
            this.message = TEXT[0] + CurrentChips + TEXT[1] + CurrentMult + TEXT[2] + handtype.name();
        } else {
            CurrentChips = 0;
            CurrentMult = 0;

            this.message = CurrentMult + TEXT[2] + TEXT[3];
        }

//        if (this.selectedCards.group.size() == 0) {
//            this.upgradePreviewCard = null;
//            if (this.selectedCards.group.size() == this.numCardsToSelect) {
//                if (this.numCardsToSelect == 1) {
//                    this.message = TEXT[0] + this.selectionReason;
//                } else {
//                    this.message = TEXT[1] + this.selectionReason;
//                }
//            } else if (this.numCardsToSelect != 1) {
//                if (!this.anyNumber) {
//                    this.message = TEXT[2] + this.numCardsToSelect + TEXT[3] + this.selectionReason;
//                } else {
//                    this.message = TEXT[4] + this.selectionReason;
//                }
//            } else {
//                this.message = TEXT[5] + this.selectionReason;
//            }
//        } else if (this.selectedCards.group.size() != 0) {
//            int numLeft = this.numCardsToSelect - this.selectedCards.group.size();
//            if (this.selectedCards.group.size() == this.numCardsToSelect) {
//                if (this.numCardsToSelect == 1) {
//                    this.message = TEXT[0] + this.selectionReason;
//                } else {
//                    this.message = TEXT[1] + this.selectionReason;
//                }
//                if (this.forUpgrade && this.selectedCards.size() == 1) {
//                    if (this.upgradePreviewCard == null)
//                        this.upgradePreviewCard = ((AbstractCard) this.selectedCards.group.get(0)).makeStatEquivalentCopy();
//                    this.upgradePreviewCard.upgrade();
//                    this.upgradePreviewCard.displayUpgrades();
//                    this.upgradePreviewCard.drawScale = 0.75F;
//                    this.upgradePreviewCard.targetDrawScale = 0.75F;
//                } else {
//                    this.upgradePreviewCard = null;
//                }
//            } else if (numLeft != 1) {
//                this.upgradePreviewCard = null;
//                if (!this.anyNumber) {
//                    this.message = TEXT[2] + numLeft + TEXT[3] + this.selectionReason;
//                } else {
//                    this.message = TEXT[4] + this.selectionReason;
//                }
//            } else {
//                this.upgradePreviewCard = null;
//                this.message = TEXT[5] + this.selectionReason;
//            }
//        }
    }

    public void reopen() {
        AbstractDungeon.overlayMenu.showBlackScreen(0.75F);
    }

    public void open(String msg, int amount, boolean anyNumber, boolean canPickZero, boolean forTransform, boolean forUpgrade, boolean upTo) {
        prep();
        this.numCardsToSelect = amount;
        this.canPickZero = canPickZero;
        this.anyNumber = anyNumber;
        this.selectionReason = msg;
        this.upTo = upTo;
        if (canPickZero) {
            this.button.isDisabled = true;
            this.button.enable();
        } else {
            this.button.isDisabled = false;
            this.button.disable();
        }
        this.forTransform = forTransform;
        this.forUpgrade = forUpgrade;
        if (!forUpgrade)
            this.upgradePreviewCard = null;
        this.button.hideInstantly();
        this.button.show();
        this.discardButton.hideInstantly();
        this.discardButton.show();
        this.peekButton.hideInstantly();
        this.peekButton.show();
        updateMessage();
    }

    public void open(String msg, int amount, boolean anyNumber, boolean canPickZero, boolean forTransform, boolean forUpgrade) {
        open(msg, amount, anyNumber, canPickZero, forTransform, forUpgrade, false);
    }

    public void open(String msg, int amount, boolean anyNumber, boolean canPickZero, boolean forTransform) {
        open(msg, amount, anyNumber, canPickZero, forTransform, false);
    }

    public void open(String msg, int amount, boolean anyNumber, boolean canPickZero) {
        prep();
        this.numCardsToSelect = amount;
        this.canPickZero = canPickZero;
        this.anyNumber = anyNumber;
        this.selectionReason = msg;
        if (canPickZero) {
            this.button.isDisabled = true;
            this.button.enable();
        } else {
            this.button.isDisabled = false;
            this.button.disable();
        }
        this.button.hideInstantly();
        this.button.show();
        this.discardButton.hideInstantly();
        this.discardButton.show();
        if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
            this.peekButton.hideInstantly();
            this.peekButton.show();
        }
        updateMessage();
    }

    public void open(String msg, int amount, boolean anyNumber) {
        open(msg, amount, anyNumber, false);
    }

    public void render(SpriteBatch sb) {
        player.hand.render(sb);
        AbstractDungeon.overlayMenu.energyPanel.render(sb);
        if (!PeekButton.isPeeking) {
            float msgWidth = FontHelper.getSmartWidth(FontHelper.panelNameFont, this.message, 9999.0F, 0.0F);
            FontHelper.renderSmartText(sb, FontHelper.panelNameFont, this.message, Settings.WIDTH / 2.0F - msgWidth / 2f, Settings.HEIGHT / 2.0F + 350.0F * Settings.scale, 9999.0F, 32.0F, Settings.CREAM_COLOR);
            if (!Settings.FAST_HAND_CONF || this.numCardsToSelect != 1 || this.canPickZero){
                this.button.render(sb);
                this.discardButton.render(sb);
            }
            this.selectedCards.render(sb);
            if (this.forUpgrade && this.upgradePreviewCard != null) {
                renderArrows(sb);
                this.upgradePreviewCard.current_x = Settings.WIDTH * 0.63F;
                this.upgradePreviewCard.current_y = Settings.HEIGHT / 2.0F + 160.0F * Settings.scale;
                this.upgradePreviewCard.target_x = Settings.WIDTH * 0.63F;
                this.upgradePreviewCard.target_y = Settings.HEIGHT / 2.0F + 160.0F * Settings.scale;
                this.upgradePreviewCard.displayUpgrades();
                boolean t1 = this.upgradePreviewCard.isDamageModified;
                boolean t2 = this.upgradePreviewCard.isBlockModified;
                boolean t3 = this.upgradePreviewCard.isMagicNumberModified;
                boolean t4 = this.upgradePreviewCard.isCostModified;
                this.upgradePreviewCard.applyPowers();
                if (!this.upgradePreviewCard.isDamageModified && t1)
                    this.upgradePreviewCard.isDamageModified = true;
                if (!this.upgradePreviewCard.isBlockModified && t2)
                    this.upgradePreviewCard.isBlockModified = true;
                if (!this.upgradePreviewCard.isMagicNumberModified && t3)
                    this.upgradePreviewCard.isMagicNumberModified = true;
                if (!this.upgradePreviewCard.isCostModified && t4)
                    this.upgradePreviewCard.isCostModified = true;
                this.upgradePreviewCard.render(sb);
                this.upgradePreviewCard.updateHoverLogic();
                this.upgradePreviewCard.renderCardTip(sb);
            }
        }
        this.peekButton.render(sb);
        AbstractDungeon.overlayMenu.combatDeckPanel.render(sb);
        AbstractDungeon.overlayMenu.discardPilePanel.render(sb);
        AbstractDungeon.overlayMenu.exhaustPanel.render(sb);
    }

    private void renderArrows(SpriteBatch sb) {
        float x = Settings.WIDTH / 2.0F - 96.0F * Settings.scale - 10.0F * Settings.scale;
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.UPGRADE_ARROW, x, Settings.HEIGHT / 2.0F + 120.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, this.arrowScale1 * Settings.scale, this.arrowScale1 * Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        x += 64.0F * Settings.scale;
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.UPGRADE_ARROW, x, Settings.HEIGHT / 2.0F + 120.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, this.arrowScale2 * Settings.scale, this.arrowScale2 * Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        x += 64.0F * Settings.scale;
        sb.draw(ImageMaster.UPGRADE_ARROW, x, Settings.HEIGHT / 2.0F + 120.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, this.arrowScale3 * Settings.scale, this.arrowScale3 * Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        this.arrowTimer += Gdx.graphics.getDeltaTime() * 2.0F;
        this.arrowScale1 = 0.8F + (MathUtils.cos(this.arrowTimer) + 1.0F) / 8.0F;
        this.arrowScale2 = 0.8F + (MathUtils.cos(this.arrowTimer - 0.8F) + 1.0F) / 8.0F;
        this.arrowScale3 = 0.8F + (MathUtils.cos(this.arrowTimer - 1.6F) + 1.0F) / 8.0F;
    }

    private void prep() {
        isOpen = true;
        player.resetControllerValues();
        for (AbstractCard c : player.hand.group)
            c.unhover();
        this.upTo = false;
        this.forTransform = false;
        this.forUpgrade = false;
        this.canPickZero = false;
        AbstractDungeon.topPanel.unhoverHitboxes();
        AbstractDungeon.actionManager.cleanCardQueue();
        this.hand = player.hand;
        player.releaseCard();
        (AbstractDungeon.getMonsters()).hoveredMonster = null;
        this.waitThenClose = false;
        this.waitToCloseTimer = 0.0F;
        this.selectedCards.clear();
        this.hoveredCard = null;
        this.wereCardsRetrieved = false;
        AbstractDungeon.isScreenUp = true;
        AbstractDungeon.screen = AbstractDungeon.CurrentScreen.HAND_SELECT;
        player.hand.stopGlowing();
        player.hand.refreshHandLayout();
        AbstractDungeon.overlayMenu.showBlackScreen(0.75F);
        this.numSelected = 0;
        if (Settings.isControllerMode)
            Gdx.input.setCursorPosition((int) ((AbstractCard) this.hand.group.get(0)).hb.cX, (int) ((AbstractCard) this.hand.group.get(0)).hb.cY);
    }

    @SpirePatch2(clz = AbstractDungeon.class, method = "update")
    public static class UpdatePatch {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(HandCardSelectScreen.class.getName()) && m.getMethodName().equals("update")) {
                        m.replace(
                                "{ "
                                        + "if (" + PokerDeck.class.getName() + ".isPokerScreenOpen()) {"
                                        + PokerDeck.class.getName() + ".pokerScreenUpdate();"
                                        + "} else {"
                                        + "$_ = $proceed($$);"
                                        + "}"
                                        + "}"
                        );

                    }
                }
            };

        }
    }

    @SpirePatch2(clz = AbstractDungeon.class, method = "render")
    public static class RenderPatch {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(HandCardSelectScreen.class.getName()) && m.getMethodName().equals("render")) {
                        m.replace(
                                "{ "
                                        + "if (" + PokerDeck.class.getName() + ".isPokerScreenOpen()) {"
                                        + PokerDeck.class.getName() + ".pokerScreenRender($1);"
                                        + "} else {"
                                        + "$_ = $proceed($$);"
                                        + "}"
                                        + "}"
                        );

                    }
                }
            };

        }

    }
}