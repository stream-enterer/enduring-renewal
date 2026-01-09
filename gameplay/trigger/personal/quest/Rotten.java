package com.tann.dice.gameplay.trigger.personal.quest;

import com.badlogic.gdx.math.Interpolation;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.entState.ChatStateEvent;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.screens.shaderFx.DeathType;

public class Rotten extends Personal {
   final int damageRequired;

   public Rotten(int damageRequired) {
      this.damageRequired = damageRequired;
   }

   @Override
   public void damageTaken(
      EntState source, EntState self, Snapshot snapshot, int damage, int damageTakenThisTurn, Eff sourceEff, Targetable targetable, int minTriggerPipHp
   ) {
      if (damage >= this.damageRequired) {
         if (source != null && ChatStateEvent.ZombieGross.chance()) {
            source.addEvent(ChatStateEvent.ZombieGross);
         }

         self.kill(DeathType.Acid);
      }
   }

   @Override
   public String describeForSelfBuff() {
      return "I die if I take " + this.damageRequired + " or more damage in a single attack";
   }

   @Override
   public String getImageName() {
      return "rotten";
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return Interpolation.linear.apply(this.damageRequired, hp, 0.65F);
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total;
   }
}
