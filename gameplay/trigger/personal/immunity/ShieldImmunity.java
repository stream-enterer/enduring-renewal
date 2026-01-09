package com.tann.dice.gameplay.trigger.personal.immunity;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.statics.Images;
import com.tann.dice.util.ImageActor;

public class ShieldImmunity extends Immunity {
   @Override
   public Actor makePanelActorI(boolean big) {
      return new ImageActor(Images.eq_immuneShield);
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      return "shieldBroken";
   }

   @Override
   public String describeForSelfBuff() {
      return "Immune to shields";
   }

   @Override
   public boolean immuneToShields() {
      return true;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.SHIELD;
   }
}
