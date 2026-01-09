package com.tann.dice.gameplay.save.settings.option;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Slider;

public class FlOption extends Option {
   public static final String SFX = "sfx";
   public static final String MUSIC = "music";
   float val = 0.1F;

   protected FlOption(String name) {
      super(name, null);
   }

   public float getVal() {
      return this.val;
   }

   public void setValue(float newVal, boolean manual) {
      this.val = newVal;
      if (manual) {
         com.tann.dice.Main.getSettings().saveOptions();
         this.manualSelectAction();
      }
   }

   @Override
   public Actor makeCogActor() {
      return this.makeCogActor(this.name);
   }

   public Actor makeCogActor(String override) {
      final Slider s = new Slider(com.tann.dice.Main.t(override), this.val, Colours.light, Colours.dark);
      s.addSlideAction(new Runnable() {
         @Override
         public void run() {
            FlOption.this.setValue(s.getValue(), true);
         }
      });
      return s;
   }

   public float getDefaultValue() {
      return 0.5F;
   }

   public boolean isOff() {
      return isOff(this.val);
   }

   public static boolean isOff(float value) {
      return value <= 0.01F;
   }

   public float getValForUse() {
      return this.isOff() ? 0.0F : this.getVal();
   }
}
