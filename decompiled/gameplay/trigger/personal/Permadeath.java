package com.tann.dice.gameplay.trigger.personal;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;

public class Permadeath extends Personal {
   @Override
   public String describeForSelfBuff() {
      return "Death is permanent";
   }

   @Override
   public void endOfLevel(EntState entState, Snapshot snapshot) {
      Ent e = entState.getEnt();
      if (e instanceof Hero && entState.getDeathsForStats() > 0) {
         DungeonContext dc = snapshot.getFightLog().getContext();
         dc.getParty().kill((Hero)e, dc);
      }
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public boolean stopResurrect() {
      return true;
   }

   @Override
   public String getImageName() {
      return "permadeath";
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return new ImageActor(Images.eq_skullWhite, Colours.pink);
   }

   @Override
   public boolean singular() {
      return true;
   }

   @Override
   public boolean allTurnsOnly() {
      return true;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.death(player);
   }
}
