package com.tann.dice.gameplay.trigger.personal.immunity;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.statics.Images;
import com.tann.dice.util.ImageActor;

public class HealImmunity extends Immunity {
   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      return "heartBroken";
   }

   @Override
   public String describeForSelfBuff() {
      return "Immune to healing";
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return new ImageActor(Images.eq_immuneHealing);
   }

   @Override
   public boolean immuneToHealing() {
      return true;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.HEAL;
   }
}
