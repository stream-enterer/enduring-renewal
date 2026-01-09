package com.tann.dice.gameplay.trigger.personal.chatty;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.event.entState.StateEvent;
import com.tann.dice.gameplay.trigger.personal.Personal;

public class DieStopped extends Personal {
   final boolean lock;
   final Integer rollsLeft;
   final EffType typeRequired;
   final Boolean best;
   final StateEvent[] events;

   public DieStopped(boolean lock, Integer rollsLeft, EffType typeRequired, Boolean best, StateEvent... events) {
      this.rollsLeft = rollsLeft;
      this.typeRequired = typeRequired;
      this.best = best;
      this.events = events;
      this.lock = lock;
   }

   @Override
   public void dieLocked(EntSideState currentSide, EntState entState) {
      if (this.lock) {
         this.check(currentSide, entState);
      }
   }

   @Override
   public void dieStoppedOn(EntSideState currentSide, EntState entState) {
      if (!this.lock) {
         this.check(currentSide, entState);
      }
   }

   private void check(EntSideState currentSide, EntState entState) {
      Eff e = currentSide.getCalculatedEffect();
      if (this.typeRequired == null || e.getType() == this.typeRequired) {
         if (this.rollsLeft == null || entState.getSnapshot().getRolls() == this.rollsLeft) {
            if (this.best != null) {
               int value = e.getValue();
               int minValue = value;
               int maxValue = value;

               for (EntSideState ess : entState.getAllSideStates()) {
                  minValue = Math.min(minValue, ess.getCalculatedEffect().getValue());
                  maxValue = Math.max(maxValue, ess.getCalculatedEffect().getValue());
               }

               if (this.best && value != maxValue || !e.hasValue()) {
                  return;
               }

               if (!this.best && value == maxValue) {
                  return;
               }
            }

            for (StateEvent se : this.events) {
               if (se.chance()) {
                  se.act(entState.getEnt().getEntPanel());
               }
            }
         }
      }
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp;
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total;
   }
}
