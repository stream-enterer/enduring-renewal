package com.tann.dice.gameplay.trigger.personal;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.event.entState.PanelHighlightEvent;
import com.tann.dice.gameplay.fightLog.event.entState.TextEvent;
import com.tann.dice.gameplay.fightLog.event.snapshot.SoundSnapshotEvent;
import com.tann.dice.gameplay.trigger.personal.hp.MaxHP;

public class Plague extends Personal {
   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      return "plague";
   }

   @Override
   public String describeForSelfBuff() {
      return "All heroes get -1 empty hp at the end of each turn (minimum 1)";
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp;
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total + 3.0F;
   }

   @Override
   public void endOfTurn(EntState self) {
      self.addEvent(PanelHighlightEvent.plague);
      self.getSnapshot().addEvent(SoundSnapshotEvent.plague);

      for (EntState es : self.getSnapshot().getStates(true, false)) {
         es.hit(new EffBill().buff(new Buff(new MaxHP(-1))).bEff(), self.getEnt());
         es.addEvent(TextEvent.PLAGUE);
         es.addEvent(PanelHighlightEvent.plague);
      }

      super.endOfTurn(self);
   }
}
