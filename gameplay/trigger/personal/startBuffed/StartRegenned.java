package com.tann.dice.gameplay.trigger.personal.startBuffed;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.util.Pixl;

public class StartRegenned extends StartBuffed {
   final int value;

   public StartRegenned(int value) {
      this.value = value;
   }

   @Override
   public String describeForSelfBuff() {
      return "Start with " + this.value + " regen";
   }

   @Override
   public String getImageName() {
      return "regen";
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Pixl p = new Pixl();

      for (int i = 0; i < this.value; i++) {
         p.image(this.getImage());
         if (i < this.value - 1) {
            p.gap(2);
         }
      }

      return p.pix();
   }

   @Override
   public void startOfCombat(Snapshot snapshot, EntState entState) {
      entState.regen(this.value);
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Keyword.regen.getCollisionBits();
   }

   @Override
   public String hyphenTag() {
      return this.value + "";
   }
}
