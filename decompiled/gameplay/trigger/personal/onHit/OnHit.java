package com.tann.dice.gameplay.trigger.personal.onHit;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.DieTargetable;
import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.personal.Personal;

public abstract class OnHit extends Personal {
   @Override
   public final String describeForSelfBuff() {
      return "[notranslate]" + this.ONHIT() + ": " + com.tann.dice.Main.t(this.describeExtra());
   }

   public String ONHIT() {
      return com.tann.dice.Main.t("[orange]On-hit[cu]");
   }

   @Override
   public final boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public final void damageTaken(
      EntState source, EntState self, Snapshot snapshot, int damage, int damageTakenThisTurn, Eff sourceEff, Targetable targetable, int minTriggerPipHp
   ) {
      if (targetable instanceof DieTargetable || targetable instanceof SimpleTargetable) {
         if (sourceEff == null || !sourceEff.hasKeyword(Keyword.ranged)) {
            if (source != null) {
               this.onHit(source, self, snapshot, damage, damageTakenThisTurn, sourceEff, targetable);
            }
         }
      }
   }

   protected abstract void onHit(EntState var1, EntState var2, Snapshot var3, int var4, int var5, Eff var6, Targetable var7);

   protected abstract String describeExtra();
}
