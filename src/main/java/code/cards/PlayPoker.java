package code.cards;

import code.TheBalatroMod;
import code.actions.PlayPokerAction;
import code.cards.AbstractEasyCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static code.TheBalatroMod.makeID;

import static code.util.Wiz.*;

public class PlayPoker extends AbstractEasyCard {
    public final static String ID = makeID("PlayPoker");
    // intellij stuff skill, none, basic, , , , , , 

    public PlayPoker() {
        super(ID, 0, CardType.SKILL, CardRarity.BASIC, CardTarget.NONE);
    }

    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new PlayPokerAction(8, (score) -> {
            TheBalatroMod.logger.info("Poker hand score: " + score);
            atb(new DamageAllEnemiesAction(p, DamageInfo.createDamageMatrix(score.intValue(), true), DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        }));

    }


}