package com.tann.dice.gameplay.trigger.global.linked.perN;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.linked.GlobalLinked;
import com.tann.dice.statics.Images;
import com.tann.dice.util.ImageActor;

public class PerDefeatedBossGlobal extends PerNGlobal {
   public PerDefeatedBossGlobal(Global linked) {
      super(linked);
   }

   @Override
   protected String getAddendum() {
      return "[n][grey](there are 5 bosses total)[cu]";
   }

   @Override
   protected String describeN() {
      return "defeated boss";
   }

   @Override
   protected int getAmt(int level, DungeonContext context, int turn) {
      return (context.getCurrentMod20LevelNumber() - 1) / 4;
   }

   @Override
   protected Actor getPerActor() {
      return new ImageActor(Images.bossSkull);
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return this.linked.getCollisionBits() | Collision.SPECIFIC_LEVEL;
   }

   @Override
   public GlobalLinked splice(Global newCenter) {
      return new PerDefeatedBossGlobal(newCenter);
   }
}
