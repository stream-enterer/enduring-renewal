package com.tann.dice.gameplay.trigger.personal.hp;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.modifier.modBal.ModTierUtils;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.HpGrid;

public class StartDamagedPer extends Personal {
   final int amt;
   final int per;

   public StartDamagedPer(int per) {
      this(1, per);
   }

   public StartDamagedPer(int amt, int per) {
      this.per = per;
      this.amt = amt;
   }

   public int getAmt(int maxHp) {
      return this.amt * (maxHp / this.per);
   }

   @Override
   public int bonusEmptyMaxHp(Integer maxHp, int empties) {
      return this.getAmt(maxHp - empties);
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Actor a = HpGrid.make(this.per - this.amt, this.per);
      if (OptionLib.MOD_CALC.c()) {
         float estVal = ModTierUtils.startDamaged(ModTierUtils.getDamagedRatio(this.amt, this.per)) * 5.0F;
         a = new Pixl(2).actor(a).row().text(Tann.floatFormat(estVal)).pix();
      }

      return a;
   }

   @Override
   public String describeForSelfBuff() {
      return this.amt + " of every " + this.per + " hp starts empty";
   }

   @Override
   public float getPriority() {
      return -9.0F;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.hpFor(player);
   }

   @Override
   public boolean allTurnsOnly() {
      return true;
   }

   @Override
   public String hyphenTag() {
      return this.amt + "/" + this.per;
   }
}
