package com.tann.dice.gameplay.trigger.global.linked.perN;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.linked.GlobalLinked;
import com.tann.dice.statics.Images;
import com.tann.dice.util.ImageActor;

public class PerTurnGlobal extends PerNGlobal {
   public PerTurnGlobal(Global linked) {
      super(linked);
   }

   @Override
   protected String describeN() {
      return "turn";
   }

   @Override
   protected int getAmt(int level, DungeonContext context, int turn) {
      return turn;
   }

   @Override
   protected Actor getPerActor() {
      return new ImageActor(Images.turnIcon);
   }

   @Override
   public GlobalLinked splice(Global newCenter) {
      return new PerTurnGlobal(newCenter);
   }
}
