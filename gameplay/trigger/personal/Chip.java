package com.tann.dice.gameplay.trigger.personal;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.entState.TextEvent;
import com.tann.dice.gameplay.fightLog.event.snapshot.SoundSnapshotEvent;

public class Chip extends Personal {
   private final int amt;
   private static final int CHIP_MAX = 7;

   public Chip(int amt) {
      this.amt = amt;
   }

   @Override
   public String getImageName() {
      return this.amt <= 7 && this.amt > 0 ? "chip" + this.amt : super.getImageName();
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String describeForSelfBuff() {
      return "The first time I take exactly [pink]"
         + this.amt
         + "[cu] damage, double it to "
         + this.amt * 2
         + ", then increase [pink]"
         + this.amt
         + "[cu] to [pink]"
         + (this.amt + 1);
   }

   @Override
   public Integer alterTakenDamage(int damage, Eff eff, Snapshot snapshot, EntState self, Targetable targetable) {
      if (damage == this.amt) {
         self.ignorePersonal(this);
         self.addBuff(new Buff(new Chip(this.amt + 1)));
         self.addEvent(TextEvent.CHIP);
         self.getSnapshot().addEvent(SoundSnapshotEvent.chip);
         return damage * 2;
      } else {
         return damage;
      }
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp * 0.93F;
   }

   @Override
   public boolean showAsIncoming() {
      return false;
   }

   @Override
   public float getPriority() {
      return 50.0F;
   }
}
