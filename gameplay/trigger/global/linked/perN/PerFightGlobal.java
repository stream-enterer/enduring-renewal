package com.tann.dice.gameplay.trigger.global.linked.perN;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.linked.GlobalLinked;
import com.tann.dice.statics.Images;
import com.tann.dice.util.ImageActor;

public class PerFightGlobal extends PerNGlobal {
   public PerFightGlobal(Global linked) {
      super(linked);
   }

   @Override
   protected String describeN() {
      return "completed fight";
   }

   @Override
   protected int getAmt(int level, DungeonContext context, int turn) {
      return context.getCurrentMod20LevelNumber();
   }

   @Override
   protected Actor getPerActor() {
      return new ImageActor(Images.fightIcon);
   }

   @Override
   public GlobalLinked splice(Global newCenter) {
      return new PerFightGlobal(newCenter);
   }
}
