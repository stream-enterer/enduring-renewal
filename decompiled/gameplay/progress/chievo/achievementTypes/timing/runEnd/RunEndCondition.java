package com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.runEnd;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.DifficultyConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;

public class RunEndCondition {
   final Mode mode;
   final Difficulty difficulty;
   final boolean victory;

   public RunEndCondition(Mode mode) {
      this(mode, null, true);
   }

   public RunEndCondition(Mode mode, Difficulty difficulty) {
      this(mode, difficulty, true);
   }

   public RunEndCondition(Mode mode, Difficulty difficulty, boolean victory) {
      this.mode = mode;
      this.difficulty = difficulty;
      this.victory = victory;
   }

   public boolean isValid(boolean victory, ContextConfig contextConfig) {
      if (this.victory != victory) {
         return false;
      } else if (this.mode != null && this.mode != contextConfig.mode) {
         return false;
      } else if (this.difficulty != null) {
         if (!(contextConfig instanceof DifficultyConfig)) {
            return false;
         } else {
            DifficultyConfig dc = (DifficultyConfig)contextConfig;
            return Difficulty.equalOrHarderThan(dc.getDifficulty(), this.difficulty);
         }
      } else {
         return true;
      }
   }

   public String describeShort() {
      String s = "";
      if (this.mode != null) {
         s = s + this.mode.getTextButtonName() + " ";
      }

      if (this.difficulty != null) {
         s = s + this.difficulty.getColourTaggedName() + " ";
      }

      return s + (this.victory ? "victory" : "defeat");
   }

   public String describe() {
      return this.describe(null);
   }

   public String describe(String extra) {
      String s = "";
      s = s + (this.victory ? "Complete " : "Get defeated on ");
      if (this.mode == null) {
         s = s + "any mode ";
      } else {
         s = s + this.mode.getTextButtonName() + " ";
      }

      if (this.difficulty != null) {
         s = s + "on " + Difficulty.getOrHarderString(this.difficulty) + " ";
      }

      if (extra != null) {
         s = s + extra + " ";
      }

      return s.trim();
   }

   public boolean isCompletable() {
      return (this.mode == null || !UnUtil.isLocked(this.mode)) && (this.difficulty == null || !UnUtil.isLocked(this.difficulty));
   }
}
