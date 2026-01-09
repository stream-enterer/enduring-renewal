package com.tann.dice.gameplay.trigger.global.roll;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.statics.Images;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.TextWriter;

public class GlobalKeepRerolls extends Global {
   @Override
   public Actor makePanelActorI(boolean big) {
      return Tann.combineActors(new ImageActor(Images.rerollBonus), new TextWriter("[blue] ="));
   }

   @Override
   public String describeForSelfBuff() {
      return "Keep unused rerolls";
   }

   @Override
   public boolean keepRerolls() {
      return true;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.REROLLS;
   }
}
