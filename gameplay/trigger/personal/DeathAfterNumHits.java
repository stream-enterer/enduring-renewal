package com.tann.dice.gameplay.trigger.personal;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.entState.ChatStateEvent;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.screens.shaderFx.DeathType;

public class DeathAfterNumHits extends Personal {
   final int numHits;

   public DeathAfterNumHits(int numHits) {
      this.numHits = numHits;
   }

   @Override
   public String describeForSelfBuff() {
      return "I die if I take damage " + this.numHits + " times in a turn";
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      return "heads5";
   }

   @Override
   public void damageTaken(
      EntState source, EntState self, Snapshot snapshot, int damage, int damageTakenThisTurn, Eff sourceEff, Targetable targetable, int minTriggerPipHp
   ) {
      if (self != null && self.getTimesDamagedThisTurn() >= this.numHits) {
         if (ChatStateEvent.HydraBehead.chance()) {
            self.addEvent(ChatStateEvent.HydraBehead);
         }

         self.kill(DeathType.Cut);
      }
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp * 0.95F;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.death(player);
   }
}
