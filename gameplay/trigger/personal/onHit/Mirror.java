package com.tann.dice.gameplay.trigger.personal.onHit;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.entState.PanelHighlightEvent;

public class Mirror extends OnHit {
   @Override
   public String getImageName() {
      return "mirror";
   }

   @Override
   protected void onHit(EntState source, EntState self, Snapshot snapshot, int damage, int damageTakenThisTurn, Eff sourceEff, Targetable targetable) {
      if (damage > 0) {
         source.addEvent(PanelHighlightEvent.painMirror);
         self.addEvent(PanelHighlightEvent.painMirror);
         source.hit(new EffBill().damage(damage).bEff(), self.getEnt());
      }
   }

   @Override
   protected String describeExtra() {
      return "attacker takes equal damage to me";
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total + 3.0F;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp * 1.13F;
   }
}
