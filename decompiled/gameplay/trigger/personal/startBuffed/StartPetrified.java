package com.tann.dice.gameplay.trigger.personal.startBuffed;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.modifier.ModifierUtils;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;

public class StartPetrified extends StartBuffed {
   final int petrifyAmount;

   public StartPetrified(int petrifyAmount) {
      this.petrifyAmount = petrifyAmount;
   }

   @Override
   public String describeForSelfBuff() {
      return "Start petrified for " + this.petrifyAmount + ModifierUtils.afterItems();
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      SpecificSidesType sst;
      switch (this.petrifyAmount) {
         case 1:
            sst = SpecificSidesType.Top;
            break;
         case 6:
            sst = SpecificSidesType.All;
            break;
         default:
            return null;
      }

      return new AffectSides(sst, new ReplaceWith(ESB.blankPetrified)).makePanelActor(big);
   }

   @Override
   public void startOfCombat(Snapshot snapshot, EntState entState) {
      entState.petrify(this.petrifyAmount);
   }

   @Override
   public long getCollisionBits(Boolean player) {
      long result = Collision.DEBUFF;
      switch (this.petrifyAmount) {
         case 1:
            result |= SpecificSidesType.Top.getCollisionBits(player);
            break;
         case 6:
            result |= Collision.allSides(player);
      }

      return result;
   }

   @Override
   public boolean isMultiplable() {
      return this.petrifyAmount == 1;
   }

   @Override
   public String hyphenTag() {
      return this.petrifyAmount + "";
   }
}
