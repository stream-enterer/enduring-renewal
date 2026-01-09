package com.tann.dice.gameplay.trigger.personal;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.entState.ChatStateEvent;
import com.tann.dice.screens.shaderFx.DeathType;

public class RottenTrick extends Personal {
   final int damageRequired;

   public RottenTrick(int damageRequired) {
      this.damageRequired = damageRequired;
   }

   @Override
   public void damageTaken(
      EntState source, EntState self, Snapshot snapshot, int damage, int damageTakenThisTurn, Eff sourceEff, Targetable targetable, int minTriggerPipHp
   ) {
      if (damage >= this.damageRequired && source != null) {
         self.addEvent(ChatStateEvent.Z0mbieLaugh, true);
         source.kill(DeathType.Acid);
      }
   }

   @Override
   public String describeForSelfBuff() {
      return "Attacker dies if I take " + this.damageRequired + " or more damage in a single attack";
   }

   @Override
   public String getImageName() {
      return "vile";
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp * 1.15F;
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total + 1.0F;
   }
}
