package com.tann.dice.screens.dungeon.panels.combatEffects;

import com.tann.dice.gameplay.fightLog.FightLog;

public class ParallelCombatEffectController extends CombatEffectController {
   CombatEffectActor[] combatEffectActors;
   final FightLog fightLog;

   public ParallelCombatEffectController(CombatEffectActor[] combatEffectActors, FightLog fightLog) {
      if (combatEffectActors.length == 0) {
         throw new RuntimeException("hmm..");
      } else {
         this.combatEffectActors = combatEffectActors;
         this.fightLog = fightLog;
      }
   }

   @Override
   protected void start() {
      for (CombatEffectActor cea : this.combatEffectActors) {
         cea.start(this.fightLog);
      }
   }

   @Override
   protected float getExtraDuration() {
      return this.combatEffectActors[0].getExtraDurationInternal();
   }

   @Override
   protected float getImpactDuration() {
      return this.combatEffectActors[0].getImpactDuration();
   }
}
