package com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.util.Pixl;

public abstract class LevelRequirement {
   public abstract boolean validFor(DungeonContext var1);

   public String describe() {
      return "not implemented ld";
   }

   public Actor makePanelActor() {
      return new Pixl().text("[text]" + this.describe(), 30).pix();
   }

   public boolean isDescribedAt() {
      return false;
   }

   public float getEventStrengthMultiplier(DungeonContext dc) {
      throw new UnsupportedOperationException("levelreq not valid for strmul");
   }
}
