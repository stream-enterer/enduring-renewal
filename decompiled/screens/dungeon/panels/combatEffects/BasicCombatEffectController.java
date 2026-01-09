package com.tann.dice.screens.dungeon.panels.combatEffects;

import com.tann.dice.gameplay.fightLog.FightLog;

public class BasicCombatEffectController extends CombatEffectController {
   CombatEffectActor combatEffectActor;
   FightLog fightLog;

   public BasicCombatEffectController(CombatEffectActor combatEffectActor, FightLog fightLog) {
      this.fightLog = fightLog;
      this.combatEffectActor = combatEffectActor;
   }

   @Override
   protected void start() {
      this.combatEffectActor.start(this.fightLog);
   }

   @Override
   protected float getExtraDuration() {
      return this.combatEffectActor.getExtraDurationInternal();
   }

   @Override
   protected float getImpactDuration() {
      return this.combatEffectActor.getImpactDuration();
   }
}
