package com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Tann;

public class NotLevelRequirement extends LevelRequirement {
   final LevelRequirement levelRequirement;

   public NotLevelRequirement(LevelRequirement levelRequirement) {
      this.levelRequirement = levelRequirement;
   }

   @Override
   public boolean validFor(DungeonContext dungeonContext) {
      return !this.levelRequirement.validFor(dungeonContext);
   }

   @Override
   public String describe() {
      return "non-" + this.levelRequirement.describe();
   }

   @Override
   public Actor makePanelActor() {
      return Tann.combineActors(this.levelRequirement.makePanelActor(), new ImageActor(Images.ui_cross, Colours.red));
   }
}
