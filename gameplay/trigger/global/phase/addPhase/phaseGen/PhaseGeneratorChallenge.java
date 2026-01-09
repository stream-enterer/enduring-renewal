package com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.challenge.ChallengePhase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhaseGeneratorChallenge extends PhaseGenerator {
   final ChallengePhase.ChallengeDifficulty challengeDifficulty;

   public PhaseGeneratorChallenge(ChallengePhase.ChallengeDifficulty challengeDifficulty) {
      this.challengeDifficulty = challengeDifficulty;
   }

   @Override
   public String describe() {
      return this.challengeDifficulty + " challenge";
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return 0L;
   }

   @Override
   public List<Phase> generate(DungeonContext dc) {
      ChallengePhase challengePhase = ChallengePhase.generate(this.challengeDifficulty, dc.getCurrentMod20LevelNumber(), dc);
      return (List<Phase>)(challengePhase != null ? Arrays.asList(challengePhase) : new ArrayList<>());
   }
}
