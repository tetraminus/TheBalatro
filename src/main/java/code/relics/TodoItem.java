package code.relics;

import code.TheBalatro;

import static code.TheBalatroMod.makeID;

public class TodoItem extends AbstractEasyRelic {
    public static final String ID = makeID("TodoItem");

    public TodoItem() {
        super(ID, RelicTier.STARTER, LandingSound.FLAT, TheBalatro.Enums.BALATRO_COLOR);
    }
}
