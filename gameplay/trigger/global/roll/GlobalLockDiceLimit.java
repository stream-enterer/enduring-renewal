package com.tann.dice.gameplay.trigger.global.roll;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.die.EntDie;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Pixl;
import java.util.List;

public class GlobalLockDiceLimit extends Global {
   final int maxKeep;

   public GlobalLockDiceLimit(int maxKeep) {
      this.maxKeep = maxKeep;
   }

   @Override
   public String describeForSelfBuff() {
      return this.maxKeep == 0 ? "You can't lock dice [grey](can still 'done rolling' though)" : "Cannot lock more than " + this.maxKeep + " dice at a time";
   }

   @Override
   public String getRollError(List<Ent> entitiesToRoll, int size) {
      int kept = size - entitiesToRoll.size();
      return kept > this.maxKeep ? "You can only lock a maximum of " + this.maxKeep + " dice" : super.getRollError(entitiesToRoll, size);
   }

   @Override
   public boolean allowToggleLock(boolean currentlyLocked, List<EntState> states) {
      if (currentlyLocked) {
         return true;
      } else {
         int numLocked = 0;

         for (EntState es : states) {
            EntDie ed = es.getEnt().getDie();
            if (ed.getState().isLockedOrLocking()) {
               numLocked++;
            }
         }

         return numLocked < this.maxKeep;
      }
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return new Pixl(0).image(Images.diceLock).row(2).text("[red]" + this.maxKeep + "/5").pix();
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.REROLLS | Collision.NUM_HEROES;
   }

   @Override
   public String hyphenTag() {
      return this.maxKeep + "";
   }
}
