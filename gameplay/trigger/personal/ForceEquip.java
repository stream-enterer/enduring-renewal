package com.tann.dice.gameplay.trigger.personal;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.statics.Images;
import com.tann.dice.util.ImageActor;

public class ForceEquip extends Personal {
   @Override
   public boolean forceEquip() {
      return true;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return new ImageActor(Images.forceEquip);
   }

   @Override
   public String describeForSelfBuff() {
      return "[purple]Must be equipped[cu]";
   }

   @Override
   public boolean skipMultiplable() {
      return true;
   }
}
