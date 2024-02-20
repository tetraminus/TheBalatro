package code.cards;

import basemod.ReflectionHacks;
import code.TheBalatroMod;
import code.util.TexLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static code.TheBalatroMod.makeID;

public class PokerCard extends AbstractEasyCard {
    public final static String ID = makeID("PokerCard");
    // intellij stuff SKILL, NONE, SPECIAL, , , , , ,

    private int Rank;
    private Suits Suit;

    private static final Texture cardBg = TexLoader.getTexture(TheBalatroMod.makeCardPath("Enhancers2.png"));
    private static final TextureAtlas.AtlasRegion cardBgAtlas = new TextureAtlas.AtlasRegion(cardBg, 0, 0, cardBg.getWidth(), cardBg.getHeight());

    public PokerCard(int rank, Suits suit) {
        super(ID, -2, CardType.ATTACK, CardRarity.SPECIAL, CardTarget.ENEMY);
        Rank = rank;
        Suit = suit;

    }
    public PokerCard() {
        this(1, Suits.HEARTS);
        Randomize();
    }

    private void Randomize() {
        if (AbstractDungeon.cardRandomRng == null) {
            return;
        }
        Rank = AbstractDungeon.cardRandomRng.random(1, 13);
        Suit = Suits.values()[AbstractDungeon.cardRandomRng.random(0, 3)];
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
    }

//render hacks
    @Override
    public Texture getCardBg() {
        return cardBg;
    }

    @Override
    public TextureAtlas.AtlasRegion getCardBgAtlas() {
        return cardBgAtlas;
    }

    @SpireOverride
    public void renderCardBg(SpriteBatch sb, float x, float y) {
        //renderHelper(sb, this.renderColor, ImageMaster.CARD_SKILL_BG_BLACK, x, y);
        Color renderColor = ReflectionHacks.getPrivate(this, AbstractCard.class, "renderColor");
        ReflectionHacks.privateMethod(AbstractCard.class, "renderHelper", SpriteBatch.class, Color.class, TextureAtlas.AtlasRegion.class, float.class, float.class, float.class).invoke(this, sb, renderColor, cardBgAtlas, x, y, 2.2f);

    }

    @SpireOverride
    public void renderPortrait(SpriteBatch sb) {
        float drawX = this.current_x ;
        float drawY = this.current_y ;
        Color renderColor = ReflectionHacks.getPrivate(this, AbstractCard.class, "renderColor");
        ReflectionHacks.privateMethod(AbstractCard.class, "renderHelper", SpriteBatch.class, Color.class, TextureAtlas.AtlasRegion.class, float.class, float.class, float.class).invoke(this, sb, renderColor, getForeground(), drawX, drawY, 2.2f);

    }

    @SpireOverride
    public void renderType(SpriteBatch sb) {
    }

    @SpireOverride
    public void renderDescription(SpriteBatch sb) {
    }

    @Override
    public void renderTitle(SpriteBatch sb) {
    }

    @SpireOverride
    public void renderEnergy(SpriteBatch sb) {
    }

    @SpireOverride
    public void renderBannerImage(SpriteBatch sb, float drawX, float drawY) {
    }

    private TextureAtlas.AtlasRegion getForeground() {
        int idx = 0;
        idx += Rank;
        idx += 13 * Suit.ordinal();

        return TexLoader.getTextureAsAtlasRegion(TheBalatroMod.makeCardPath("8BitDeck" + idx + ".png"));
    }

    @SpireOverride
    public void renderPortraitFrame(SpriteBatch sb, float x, float y) {

    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {

    }

    public int getRank() {
        return Rank;
    }

    public Suits getSuit() {
        return Suit;
    }

    public void setRank(int rank) {
        Rank = rank;
    }

    public void setSuit(Suits suit) {
        Suit = suit;
    }

    public boolean isfaceCard() {
        return Rank > 10;
    }


    @Override
    public AbstractCard makeStatEquivalentCopy() {
        return new PokerCard(Rank, Suit);
    }

    public static enum Suits {
        HEARTS,
        DIAMONDS,
        CLUBS,
        SPADES
    }

    public static class PokerCardSaveDummy {

        public PokerCardSaveDummy(AbstractCard c) {
            if (c instanceof PokerCard) {
                Rank = ((PokerCard) c).Rank;
                Suit = ((PokerCard) c).Suit;
            }
        }

        public PokerCard GetCard() {
            return new PokerCard(Rank, Suit);
        }

        public int Rank;
        public Suits Suit;
    }
}