package com.tann.dice.gameplay.trigger.personal.hp;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.modifier.modBal.ModTierUtils;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;

public class MaxHP extends Personal {
   public final int maxHpModifier;

   public MaxHP(int maxHpModifier) {
      this.maxHpModifier = Math.min(999, maxHpModifier);
   }

   @Override
   public int getBonusMaxHp(int maxHp, EntState state) {
      return this.maxHpModifier;
   }

   @Override
   public String describeForSelfBuff() {
      return Tann.delta(this.maxHpModifier) + " hp";
   }

   @Override
   protected boolean removeGainsFromGiveText() {
      return false;
   }

   @Override
   public String describeForGiveBuff(Eff source) {
      String result = this.maxHpModifier >= 0 ? "+" : "";
      return result + this.maxHpModifier + " hp";
   }

   @Override
   public String[] getSound() {
      return Sounds.heals;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Pixl p = new Pixl(0);
      int num = Math.abs(this.maxHpModifier);

      for (int i = 0; i < num; i++) {
         p.image(Images.hp, Colours.red);
         if (i != num - 1) {
            if (i % 5 == 4) {
               p.row(1);
            } else {
               p.gap(1);
            }
         }
      }

      Pixl start = new Pixl();
      if (this.maxHpModifier > 0) {
         start.image(Images.plusBig, Colours.text);
      } else {
         start.image(Images.minus, Colours.purple);
      }

      start.gap(2).actor(p.pix(8));
      Actor a = start.pix();
      if (OptionLib.MOD_CALC.c()) {
         float estVal = ModTierUtils.extraMonsterHP(ModTierUtils.calcBonusMonsterHpFlat(this.maxHpModifier));
         a = new Pixl(2).actor(a).row().text(Tann.floatFormat(estVal)).pix();
      }

      return a;
   }

   @Override
   public float getEffectTier(int pips, int tier) {
      return pips * 0.9F;
   }

   @Override
   public float getPriority() {
      return this.buff == null ? -10.0F : super.getPriority();
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.hpFor(player);
   }

   @Override
   public Personal genMult(int mult) {
      return new MaxHP(this.maxHpModifier * mult);
   }

   @Override
   public boolean allTurnsOnly() {
      return true;
   }

   @Override
   public boolean isMultiplable() {
      return true;
   }

   @Override
   public String hyphenTag() {
      return "" + Math.abs(this.maxHpModifier);
   }
}
