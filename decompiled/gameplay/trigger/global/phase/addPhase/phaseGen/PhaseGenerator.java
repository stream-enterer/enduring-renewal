package com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.challenge.ChallengePhase;
import java.util.List;
import java.util.Random;

public abstract class PhaseGenerator implements PhaseProducer {
   protected abstract List<Phase> generate(DungeonContext var1);

   @Override
   public List<Phase> get(DungeonContext dc) {
      return this.generate(dc);
   }

   public String describe() {
      return this.getClass().getSimpleName().toLowerCase().replaceAll("phasegenerator", "") + " phase";
   }

   public static PhaseGenerator rpg(Random r) {
      return indexed(r.nextInt(11));
   }

   public static PhaseGenerator indexed(int index) {
      switch (index) {
         case 0:
            return new PhaseGeneratorLevelup();
         case 1:
            return new PhaseGeneratorStandardLoot();
         case 2:
         case 3:
            return new PhaseGeneratorReroll();
         case 4:
            return new PhaseGeneratorTweakPick();
         case 5:
            return new PhaseGeneratorPositionSwap();
         case 6:
            return new PhaseGeneratorChallenge(ChallengePhase.ChallengeDifficulty.Standard);
         case 7:
            return new PhaseGeneratorChallenge(ChallengePhase.ChallengeDifficulty.Easy);
         case 8:
            return new PhaseGeneratorPositionSwap();
         case 9:
            return new PhaseGeneratorTrade();
         default:
            return new PhaseGeneratorStandardLoot();
      }
   }

   public long getCollisionBits(Boolean player) {
      return 0L;
   }

   public Actor makePanel() {
      return null;
   }

   public String hyphenTag() {
      return null;
   }
}
