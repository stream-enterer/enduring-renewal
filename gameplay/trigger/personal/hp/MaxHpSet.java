package com.tann.dice.gameplay.trigger.personal.hp;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.ui.HpGrid;

public class MaxHpSet extends Personal {
   final int hpSet;

   public MaxHpSet(int hpSet) {
      this.hpSet = hpSet;
   }

   @Override
   public int getBonusMaxHp(int maxHp, EntState state) {
      return this.hpSet - maxHp;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return new Pixl().text("[grey]=").gap(2).actor(HpGrid.make(this.hpSet, this.hpSet)).pix();
   }

   @Override
   public String describeForSelfBuff() {
      return "Set hp to " + this.hpSet;
   }

   @Override
   public float getPriority() {
      return -10.0F;
   }

   @Override
   public boolean allTurnsOnly() {
      return true;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.hpFor(player);
   }
}
