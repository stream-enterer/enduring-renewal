package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.challenge;

public class ChallengeData {
   ChallengeReward reward;
   ChallengeType type;

   public ChallengeData(ChallengeType type, ChallengeReward reward) {
      this.reward = reward;
      this.type = type;
   }

   public ChallengeData() {
   }

   public ChallengePhase makePhase() {
      return new ChallengePhase(this.type, this.reward);
   }
}
