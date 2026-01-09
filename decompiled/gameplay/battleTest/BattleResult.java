package com.tann.dice.gameplay.battleTest;

public class BattleResult {
   final boolean playerVictory;
   final float damageTaken;

   public BattleResult(boolean playerVictory, float damageTaken) {
      this.playerVictory = playerVictory;
      this.damageTaken = damageTaken;
   }

   @Override
   public String toString() {
      return this.playerVictory + ":" + this.damageTaken;
   }

   public boolean isPlayerVictory() {
      return this.playerVictory;
   }

   public boolean isValidLevel() {
      float ahpd = 0.08F;
      float target = 0.45F;
      return this.playerVictory && Math.abs(target - this.damageTaken) < ahpd;
   }
}
