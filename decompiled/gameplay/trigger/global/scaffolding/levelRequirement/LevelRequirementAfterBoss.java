package com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.statics.Images;
import com.tann.dice.util.ImageActor;

public class LevelRequirementAfterBoss extends LevelRequirement {
   @Override
   public boolean validFor(DungeonContext dungeonContext) {
      int level = dungeonContext.getCurrentLevelNumber();
      return level % 4 == 1 && dungeonContext.getCurrentMod20LevelNumber() > 1;
   }

   @Override
   public String describe() {
      return "after boss fights";
   }

   @Override
   public Actor makePanelActor() {
      return new ImageActor(Images.bossSkull);
   }
}
