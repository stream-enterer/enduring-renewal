package com.tann.dice.gameplay.trigger.personal.merge;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.trigger.global.linked.GlobalNumberLimit;
import com.tann.dice.gameplay.trigger.personal.Cleansed;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Tann;
import com.tann.dice.util.tp.TP;

public class Poison extends Merge {
   int value;

   public Poison(int value) {
      this.value = value;
   }

   @Override
   public Integer getPoisonDamage() {
      return this.value;
   }

   @Override
   public boolean showInEntPanelInternal() {
      return false;
   }

   @Override
   public boolean showInDiePanel() {
      return true;
   }

   @Override
   public String getImageName() {
      return "poison";
   }

   @Override
   public boolean showImageInDiePanelTitle() {
      return false;
   }

   @Override
   public String describeForGiveBuff(Eff source) {
      return this.value + " poison damage";
   }

   @Override
   public String describeForSelfBuff() {
      return "[notranslate]" + com.tann.dice.Main.t(debuffString()) + " " + this.value + " ([green]" + Tann.repeat("[hp][p]", this.value) + "[cu])";
   }

   public static String debuffString() {
      return "[green]Poisoned[cu]";
   }

   @Override
   public Cleansed.CleanseType getCleanseType() {
      return Cleansed.CleanseType.Poison;
   }

   @Override
   public boolean canMergeInternal(Personal personal) {
      return true;
   }

   @Override
   public void merge(Personal personal) {
      this.value = this.value + ((Poison)personal).value;
      this.value = GlobalNumberLimit.box(this.value);
   }

   @Override
   public String[] getSound() {
      return Sounds.poison;
   }

   @Override
   public TP<Integer, Boolean> cleanseBy(int cleanseAmt) {
      int used = Math.min(cleanseAmt, this.value);
      this.value -= used;
      return new TP<>(used, this.value <= 0);
   }
}
