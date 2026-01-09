package com.tann.dice.gameplay.trigger.personal.quest;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.personal.Personal;

public class Buddies extends Personal {
   @Override
   public void damageTaken(
      EntState source, EntState self, Snapshot snapshot, int damage, int damageTakenThisTurn, Eff sourceEff, Targetable targetable, int minTriggerPipHp
   ) {
      if (damage == self.getHp()) {
         self.flee();
      }
   }

   @Override
   public String describeForSelfBuff() {
      return "I flee if I lose [purple]exactly half[cu] my hp in a single attack";
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp * 0.78F;
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total;
   }
}
