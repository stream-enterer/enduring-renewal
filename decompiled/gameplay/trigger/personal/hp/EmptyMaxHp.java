package com.tann.dice.gameplay.trigger.personal.hp;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.global.linked.GlobalNumberLimit;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;

public class EmptyMaxHp extends Personal {
   final int bonusEmpties;
   final boolean itemLayer;

   public EmptyMaxHp(int bonusEmpties) {
      this(bonusEmpties, true);
   }

   public EmptyMaxHp(int bonusEmpties, boolean itemLayer) {
      this.bonusEmpties = GlobalNumberLimit.box(bonusEmpties);
      this.itemLayer = itemLayer;
   }

   @Override
   public String describeForSelfBuff() {
      return Tann.delta(this.bonusEmpties) + " empty hp";
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Pixl p = new Pixl(0);
      p.text(Words.plusString(true)).gap(1);
      if (this.bonusEmpties < 10) {
         for (int i = 0; i < Math.abs(this.bonusEmpties); i++) {
            p.image(Images.hp_empty, Colours.purple);
            p.gap(1);
         }
      } else {
         p.text("" + this.bonusEmpties).gap(1).image(Images.hp_empty, Colours.purple);
      }

      return p.pix();
   }

   @Override
   public int bonusEmptyMaxHp(Integer maxHp, int empties) {
      return this.bonusEmpties;
   }

   @Override
   public int getBonusMaxHp(int maxHp, EntState state) {
      return this.bonusEmpties;
   }

   @Override
   public float getPriority() {
      return this.itemLayer ? -10.0F : super.getPriority();
   }

   @Override
   public boolean isMultiplable() {
      return true;
   }

   @Override
   public Personal genMult(int mult) {
      return new EmptyMaxHp(this.bonusEmpties * mult, this.itemLayer);
   }
}
