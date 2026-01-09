package com.tann.dice.gameplay.trigger.personal.linked.perN.ns;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.statics.Images;
import com.tann.dice.util.ImageActor;

public class PerNItem extends PerN {
   @Override
   public Actor makePanelActor() {
      return new ImageActor(Images.itemSmall);
   }

   @Override
   public int getAmt(Snapshot snapshot, EntState entState) {
      return entState.getEnt().getItems().size();
   }

   @Override
   public String describe() {
      return "equipped item";
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.ITEM;
   }
}
