package com.tann.dice.screens.dungeon.panels.combatEffects;

import com.tann.dice.gameplay.fightLog.FightLog;

public class BlankEffectActor extends CombatEffectActor {
   static final float preWait = 0.2F;
   static final float postWait = 0.3F;

   @Override
   protected void start(FightLog fightLog) {
   }

   @Override
   protected float getImpactDurationInternal() {
      return 0.2F;
   }

   @Override
   protected float getExtraDurationInternal() {
      return 0.5F;
   }
}
