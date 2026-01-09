package com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Pixl;

public class LevelRequirementRange extends LevelRequirement {
   final int min;
   final int max;

   public LevelRequirementRange(int level) {
      this(level, level);
   }

   public LevelRequirementRange(int min, int max) {
      this.min = min;
      this.max = max;
   }

   @Override
   public boolean validFor(DungeonContext dungeonContext) {
      int level = dungeonContext.getCurrentMod20LevelNumber();
      return level >= this.min && level <= this.max;
   }

   @Override
   public Actor makePanelActor() {
      return new Pixl().image(Images.fightIcon).row().text("[text]" + this.describeRange()).pix();
   }

   @Override
   public String describe() {
      boolean same = this.min == this.max;
      String start = same ? "fight" : "fights";
      return start + " " + this.describeRange();
   }

   public String describeRange() {
      return this.min == this.max ? this.min + "" : this.min + "-" + this.max;
   }
}
