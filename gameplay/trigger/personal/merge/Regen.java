package com.tann.dice.gameplay.trigger.personal.merge;

import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.trigger.global.linked.GlobalNumberLimit;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.statics.sound.Sounds;

public class Regen extends Merge {
   int value;
   String overrideName;

   public Regen(int value) {
      this(value, null);
   }

   public Regen(int value, String overrideName) {
      this.value = value;
      this.overrideName = overrideName;
   }

   @Override
   public Integer getRegen() {
      return this.value;
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      return "regen";
   }

   @Override
   public String describeForGiveBuff(Eff source) {
      return this.value + " regen";
   }

   @Override
   public String describeForSelfBuff() {
      return "Regenerate [red]" + this.value + "[cu] health at the end of each turn";
   }

   @Override
   public boolean canMergeInternal(Personal personal) {
      return this.overrideName == null && ((Regen)personal).overrideName == null;
   }

   @Override
   public void merge(Personal personal) {
      this.value = this.value + ((Regen)personal).value;
      this.value = GlobalNumberLimit.box(this.value);
   }

   @Override
   public String[] getSound() {
      return Sounds.regen;
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      return total;
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp + this.value * 3;
   }

   @Override
   public boolean showAsIncoming() {
      return false;
   }
}
