package com.tann.dice.gameplay.trigger.personal.spell;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.entState.PanelHighlightEvent;
import com.tann.dice.gameplay.fightLog.event.entState.TextEvent;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.util.lang.Words;

public class OnSpendAbilityCost extends Personal {
   @Override
   public String describeForSelfBuff() {
      return "Whenever you use " + Words.aOrAn(Words.spab(false)) + ": damage the bottom hero equal to the cost";
   }

   @Override
   public String getImageName() {
      return "manaBurn";
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp;
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total + 6.0F;
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public void onSpendAbilityCost(int amtSpent, Snapshot snapshot, EntState es) {
      es.addEvent(PanelHighlightEvent.corruptMana);

      for (Eff eff : new Eff[]{
         new EffBill().event(TextEvent.Corrupted).enemy().targetType(TargetingType.Bot).bEff(),
         new EffBill().damage(amtSpent).targetType(TargetingType.Bot).bEff()
      }) {
         SimpleTargetable st = new SimpleTargetable(es.getEnt(), eff);
         snapshot.target(null, st, false);
      }
   }
}
