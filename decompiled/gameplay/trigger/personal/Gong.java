package com.tann.dice.gameplay.trigger.personal;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.event.entState.TextEvent;
import com.tann.dice.gameplay.fightLog.event.snapshot.SoundSnapshotEvent;

public class Gong extends Personal {
   @Override
   public String describeForSelfBuff() {
      return "At the end of each turn, 1 damage to all heroes and 5 damage to me";
   }

   @Override
   public void endOfTurn(EntState entState) {
      Ent source = entState.getEnt();
      entState.getSnapshot().addEvent(SoundSnapshotEvent.gong);
      entState.addEvent(TextEvent.GONG);
      entState.getSnapshot().untargetedUse(new EffBill().damage(1).group().bEff(), source);
      entState.hit(new EffBill().damage(5).bEff(), source);
      super.endOfTurn(entState);
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total + 4.0F;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp * 0.62F;
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      return "bell";
   }
}
