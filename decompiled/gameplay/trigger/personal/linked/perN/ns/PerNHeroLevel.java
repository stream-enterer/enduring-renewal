package com.tann.dice.gameplay.trigger.personal.linked.perN.ns;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;

public class PerNHeroLevel extends PerN {
   @Override
   public Actor makePanelActor() {
      return new Pixl(0, 2).border(Colours.grey).text("N").pix();
   }

   @Override
   public int getAmt(Snapshot snapshot, EntState entState) {
      EntType et = entState.getEnt().entType;
      return et instanceof HeroType ? ((HeroType)et).getTier() : 0;
   }

   @Override
   public String describe() {
      return "hero level";
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.LEVELUP_REWARD;
   }
}
