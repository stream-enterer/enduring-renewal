package com.tann.dice.gameplay.fightLog.command;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.targetable.DieTargetable;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import java.util.List;

public class DieCommand extends TargetableCommand {
   public final DieTargetable dt;

   public DieCommand(DieTargetable dt, Ent target) {
      super(dt, target);
      this.dt = dt;
   }

   @Override
   public Ent getSource() {
      return this.dt.getSource();
   }

   @Override
   protected boolean shouldSkipAnimation(Snapshot beforeShot) {
      if (this.getSource() != null && !this.getSource().getEntPanel().hasParent()) {
         return true;
      } else {
         EntSideState beforeState = beforeShot.getSideState(this);
         if (beforeState == null) {
            return true;
         } else {
            Eff e = beforeState.getCalculatedEffect();
            if (e.isUnusableBecauseNerfed()) {
               return true;
            } else if (beforeShot.getState(this.dt.getSource()).skipTurn()) {
               return true;
            } else if (!e.needsTarget()) {
               return false;
            } else {
               for (EntState es : beforeShot.getActualTargets(this.target, e, this.dt.getSource())) {
                  if (es == null || !es.isDead()) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   @Override
   public String toSave(Snapshot previous) {
      List<Ent> entities = previous.getEntities(null, false);
      int sourceIndex = entities.indexOf(this.getSource());
      int targetIndex = entities.indexOf(this.target);
      if (this.target == null) {
         targetIndex = NULL_TARGET_INDEX;
      }

      int sideIndex = this.dt.getSideIndex();
      String type = this.isEnemy() ? "2" : "1";
      return type + intToChar(sourceIndex) + intToChar(targetIndex) + intToChar(sideIndex) + (this.usesDie ? "" : "c");
   }

   public boolean isEnemy() {
      Ent source = this.getSource();
      return source != null && !source.isPlayer();
   }

   public DieCommand(String saved, Snapshot snapshot) {
      this(new DieTargetable(loadSource(saved, snapshot), loadSideIndex(saved)), loadTarget(saved, snapshot));
      if (saved.length() != 4 && saved.length() != 5) {
         throw new RuntimeException("Invalid DieCommand: " + saved);
      } else {
         this.setUsesDie(saved.length() == 4);
      }
   }

   private static Ent loadSource(String saved, Snapshot snapshot) {
      return snapshot.getEntities(null, false).get(charToInt(saved.charAt(1)));
   }

   private static Ent loadTarget(String saved, Snapshot snapshot) {
      int targetIndex = charToInt(saved.charAt(2));
      if (targetIndex == NULL_TARGET_INDEX) {
         return null;
      } else {
         return targetIndex == -1 ? null : snapshot.getEntities(null, false).get(targetIndex);
      }
   }

   private static int loadSideIndex(String saved) {
      return charToInt(saved.charAt(3));
   }

   @Override
   public String toString() {
      return this.dt.getSource() + ":" + this.dt.getSideIndex();
   }
}
