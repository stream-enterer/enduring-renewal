package com.tann.dice.gameplay.trigger.personal;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.util.ui.HpGrid;

public class SetStartingHp extends Personal {
   final int val;

   public SetStartingHp(int val) {
      this.val = val;
   }

   @Override
   public String describeForSelfBuff() {
      return "Set starting hp to " + this.val;
   }

   @Override
   public boolean showInEntPanelInternal() {
      return false;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return HpGrid.make(this.val, 10);
   }

   @Override
   public int affectStartingHp(int hp) {
      return this.val;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.hpFor(player);
   }

   @Override
   public float getPriority() {
      return -10.0F;
   }

   @Override
   public boolean allTurnsOnly() {
      return true;
   }
}
