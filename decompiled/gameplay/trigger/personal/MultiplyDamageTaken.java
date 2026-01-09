package com.tann.dice.gameplay.trigger.personal;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.util.lang.Words;

public class MultiplyDamageTaken extends Personal {
   private final int multiple;

   public MultiplyDamageTaken(int multiple) {
      this.multiple = multiple;
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      return "multiply";
   }

   @Override
   public String describeForSelfBuff() {
      return Words.capitaliseFirst(Words.multiple(this.multiple)) + " all damage taken";
   }

   @Override
   public Integer alterTakenDamage(int damage, Eff eff, Snapshot snapshot, EntState self, Targetable targetable) {
      return damage * this.multiple;
   }

   @Override
   public float getPriority() {
      return 5.0F;
   }
}
