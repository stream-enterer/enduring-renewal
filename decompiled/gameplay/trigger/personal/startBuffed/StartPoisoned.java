package com.tann.dice.gameplay.trigger.personal.startBuffed;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.modifier.modBal.ModTierUtils;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;

public class StartPoisoned extends StartBuffed {
   final int poisonAmount;

   public StartPoisoned(int poisonAmount) {
      this.poisonAmount = poisonAmount;
   }

   @Override
   public String describeForSelfBuff() {
      return this.poisonAmount == 1 ? "Start [green]poisoned[cu]" : "Start [green]poisoned[cu] for " + this.poisonAmount;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Pixl p = new Pixl();
      p.image(Images.plusBig, Colours.grey).gap(1);
      Pixl pips = new Pixl();

      for (int i = 0; i < this.poisonAmount; i++) {
         if (i % 5 == 0 && i != 0) {
            pips.row(1);
         }

         pips.image(Images.hp, Colours.green).gap(1);
      }

      p.actor(pips.pix(8));
      Actor a = p.pix();
      if (OptionLib.MOD_CALC.c()) {
         float estVal = ModTierUtils.startPoisoned(this.poisonAmount) * 5.0F;
         a = new Pixl(2).actor(a).row().text(Tann.floatFormat(estVal)).pix();
      }

      return a;
   }

   @Override
   public String getImageName() {
      return "startPoisoned";
   }

   @Override
   public void startOfCombat(Snapshot snapshot, EntState entState) {
      entState.poison(this.poisonAmount);
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.POISON | Collision.DEBUFF;
   }

   @Override
   public boolean isMultiplable() {
      return this.poisonAmount == 1;
   }

   @Override
   public String hyphenTag() {
      return "" + this.poisonAmount;
   }
}
