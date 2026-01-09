package com.tann.dice.gameplay.context.config.difficultyConfig;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class DifficultyConfig extends ContextConfig {
   protected final Difficulty difficulty;

   public DifficultyConfig(Mode mode, Difficulty difficulty) {
      super(mode);
      this.difficulty = difficulty;
   }

   @Override
   public String getSpecificKey() {
      return this.getGeneralSaveKey() + "-" + this.difficulty;
   }

   @Override
   public String getSaveFileButtonName() {
      return ", " + com.tann.dice.Main.t(this.difficulty.getColourTaggedName());
   }

   @Override
   public StandardButton makeStartButton(boolean big) {
      int w = big ? 50 : 38;
      int h = big ? 20 : 16;
      if (com.tann.dice.Main.self().translator.longDifficultyNames()) {
         if (com.tann.dice.Main.isPortrait()) {
            w = (int)(w * 1.8F);
            h = (int)(h * 1.3F);
         } else {
            w = -1;
         }
      } else if (com.tann.dice.Main.isPortrait()) {
         w = (int)(w * 1.3F);
         h = (int)(h * 1.3F);
      }

      return new StandardButton(this.difficulty.getColourTaggedName(), this.difficulty.getColor(), w, h);
   }

   @Override
   public boolean isLocked() {
      return UnUtil.isLocked(this.difficulty);
   }

   @Override
   public String serialise() {
      return this.difficulty.name();
   }

   @Override
   public List<Global> getSpecificModeGlobals() {
      List<Global> result = new ArrayList<>();
      result.addAll(this.getSpecificDifficultyModeGlobals());
      return result;
   }

   @Override
   public Collection<Global> getSpecificModeAddPhases() {
      return this.difficulty.getGlobals();
   }

   protected List<Global> getSpecificDifficultyModeGlobals() {
      return new ArrayList<>();
   }

   @Override
   public Difficulty getDifficulty() {
      return this.difficulty;
   }

   @Override
   public String describeConfig() {
      return this.difficulty.getColourTaggedName();
   }

   @Override
   public String getAnticheeseKey() {
      return this.getSpecificKey();
   }
}
