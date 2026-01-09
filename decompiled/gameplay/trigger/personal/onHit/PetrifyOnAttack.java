package com.tann.dice.gameplay.trigger.personal.onHit;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.targetable.DieTargetable;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.merge.PetrifySide;
import com.tann.dice.util.TannLog;

public class PetrifyOnAttack extends OnHit {
   @Override
   public String getImageName() {
      return "petrify";
   }

   @Override
   protected void onHit(EntState source, EntState self, Snapshot snapshot, int damage, int damageTakenThisTurn, Eff sourceEff, Targetable targetable) {
      if (targetable instanceof DieTargetable) {
         DieTargetable dt = (DieTargetable)targetable;
         if (dt.getSideIndex() == -1) {
            TannLog.log("Attacked by -1 side index DieTargetable", TannLog.Severity.error);
         } else {
            Personal t = new PetrifySide(dt.getSideIndex());
            Buff b = new Buff(t);
            source.addBuff(b);
         }
      }
   }

   @Override
   protected String describeExtra() {
      return "turn the attacking side to stone this fight";
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total + 1.0F;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp * 1.3F;
   }
}
