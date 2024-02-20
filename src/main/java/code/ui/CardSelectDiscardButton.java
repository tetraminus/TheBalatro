//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package code.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;

import static code.TheBalatroMod.makeID;

public class CardSelectDiscardButton {
    private static final UIStrings uiStrings;
    public static final String[] TEXT;
    private static final int W = 512;
    private static final int H = 256;
    private static final float TAKE_Y;
    private static final float SHOW_X;
    private static final float HIDE_X;
    private float current_x;
    private float target_x;
    private boolean isHidden;
    public boolean isDisabled;
    private Color textColor;
    private Color btnColor;
    private float target_a;
    private String buttonText;
    private static final float HITBOX_W;
    private static final float HITBOX_H;
    public Hitbox hb;

    public CardSelectDiscardButton() {
        this.current_x = HIDE_X;// 27
        this.target_x = this.current_x;// 28
        this.isHidden = true;// 29
        this.isDisabled = true;// 30
        this.textColor = Color.WHITE.cpy();// 32
        this.btnColor = Color.WHITE.cpy();// 33
        this.target_a = 0.0F;// 34
        this.buttonText = "NOT_SET";// 37
        this.hb = new Hitbox(0.0F, 0.0F, HITBOX_W, HITBOX_H);// 41
        this.buttonText = TEXT[0];// 44
        this.hb.move((float)Settings.WIDTH / 2.0F, TAKE_Y);// 45
    }// 46

    public void update() {
        if (!this.isHidden) {// 49
            this.hb.update();// 50
        }

        if (!this.isDisabled) {// 53
            if (this.hb.justHovered) {// 54
                CardCrawlGame.sound.play("UI_HOVER");// 55
            }

            if (this.hb.hovered && InputHelper.justClickedLeft) {// 57
                this.hb.clickStarted = true;// 58
                CardCrawlGame.sound.play("UI_CLICK_1");// 59
            }
        }

        if (this.current_x != this.target_x) {// 63
            this.current_x = MathUtils.lerp(this.current_x, this.target_x, Gdx.graphics.getDeltaTime() * 9.0F);// 64
            if (Math.abs(this.current_x - this.target_x) < Settings.UI_SNAP_THRESHOLD) {// 65
                this.current_x = this.target_x;// 66
            }
        }

        this.textColor.a = MathHelper.fadeLerpSnap(this.textColor.a, this.target_a);// 70
        this.btnColor.a = this.textColor.a;// 71
    }// 72

    public void hideInstantly() {
        this.current_x = HIDE_X;// 75
        this.target_x = HIDE_X;// 76
        this.isHidden = true;// 77
        this.target_a = 0.0F;// 78
        this.textColor.a = 0.0F;// 79
    }// 80

    public void hide() {
        if (!this.isHidden) {// 83
            this.target_a = 0.0F;// 84
            this.target_x = HIDE_X;// 85
            this.isHidden = true;// 86
        }

    }// 88

    public void show() {
        if (this.isHidden) {// 91
            this.textColor.a = 0.0F;// 92
            this.target_a = 1.0F;// 93
            this.target_x = SHOW_X;// 94
            this.isHidden = false;// 95
        }

    }// 97

    public void disable() {
        if (!this.isDisabled) {// 100
            this.hb.hovered = false;// 101
            this.isDisabled = true;// 102
            this.btnColor = Color.GRAY.cpy();// 103
            this.textColor = Color.LIGHT_GRAY.cpy();// 104
        }

    }// 106

    public void enable() {
        if (this.isDisabled) {// 109
            this.isDisabled = false;// 110
            this.btnColor = Color.WHITE.cpy();// 111
            this.textColor = Settings.CREAM_COLOR.cpy();// 112
        }

    }// 114

    public void render(SpriteBatch sb) {
        sb.setColor(this.btnColor);// 117
        this.renderButton(sb);// 119
        if (this.hb.hovered && !this.isDisabled && !this.hb.clickStarted) {// 121
            sb.setBlendFunction(770, 1);// 122
            sb.setColor(new Color(1.0F, 1.0F, 1.0F, 0.3F));// 123
            this.renderButton(sb);// 124
            sb.setBlendFunction(770, 771);// 125
        }

        if (!this.isHidden) {// 128
            FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, this.buttonText, (float)Settings.WIDTH / 2.0F, TAKE_Y, this.textColor);// 129
        }

    }// 137

    private void renderButton(SpriteBatch sb) {
        if (!this.isHidden) {// 140
            if (this.isDisabled) {// 141
                sb.draw(ImageMaster.REWARD_SCREEN_TAKE_USED_BUTTON, (float)Settings.WIDTH / 2.0F - 256.0F, TAKE_Y - 128.0F, 256.0F, 128.0F, 512.0F, 256.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 512, 256, false, false);// 142
            } else {
                if (this.hb.clickStarted) {// 160
                    sb.setColor(Color.LIGHT_GRAY);// 161
                }

                sb.draw(ImageMaster.REWARD_SCREEN_TAKE_BUTTON, (float)Settings.WIDTH / 2.0F - 256.0F, TAKE_Y - 128.0F, 256.0F, 128.0F, 512.0F, 256.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 512, 256, false, false);// 163
            }

            if (Settings.isControllerMode) {// 182
                sb.draw(CInputActionSet.proceed.getKeyImg(), this.hb.cX - 32.0F - 100.0F * Settings.scale, this.hb.cY - 32.0F + 2.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);// 183 184
            }

            this.hb.render(sb);// 202
        }

    }// 204

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(makeID("DiscardButton"));// 19
        TEXT = uiStrings.TEXT;// 20
        TAKE_Y = 400.0F * Settings.scale;// 24
        SHOW_X = (float)Settings.WIDTH - 256.0F * Settings.scale;// 25
        HIDE_X = SHOW_X + 400.0F * Settings.scale;// 26
        HITBOX_W = 260.0F * Settings.scale;// 40
        HITBOX_H = 80.0F * Settings.scale;
    }
}
