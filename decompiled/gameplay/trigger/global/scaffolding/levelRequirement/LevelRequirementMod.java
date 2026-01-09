package com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.lang.Words;

public class LevelRequirementMod extends LevelRequirement {
   final int mod;
   final int rem;

   public LevelRequirementMod(int mod, int rem) {
      this.mod = mod;
      this.rem = rem;
   }

   @Override
   public boolean validFor(DungeonContext dungeonContext) {
      if (dungeonContext.isFirstLevel()) {
         return false;
      } else {
         int level = dungeonContext.getCurrentLevelNumber();
         return level % this.mod == this.rem;
      }
   }

   @Override
   public String describe() {
      return "every " + Words.ordinal(this.mod) + " fight";
   }

   @Override
   public Actor makePanelActor() {
      return new Pixl().image(Images.fightIcon).gap(0).text("[text]/" + this.mod).pix();
   }
}
