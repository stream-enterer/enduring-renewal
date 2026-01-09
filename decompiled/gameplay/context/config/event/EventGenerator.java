package com.tann.dice.gameplay.context.config.event;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGenerator;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirement;

public class EventGenerator {
   public final float chance;
   public final float eventStrength;
   public final LevelRequirement lr;
   public final PhaseGenerator pg;

   public EventGenerator(float chance, float eventStrength, LevelRequirement lr, PhaseGenerator pg) {
      this.chance = chance;
      this.eventStrength = eventStrength;
      this.lr = lr;
      this.pg = pg;
   }

   public float getStrength(DungeonContext dc) {
      return this.eventStrength * this.lr.getEventStrengthMultiplier(dc);
   }

   @Override
   public String toString() {
      return "ch:" + this.chance + ", str:" + this.eventStrength + ", " + this.lr.getClass().getSimpleName() + ", " + this.pg.getClass().getSimpleName();
   }
}
