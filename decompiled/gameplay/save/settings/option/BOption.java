package com.tann.dice.gameplay.save.settings.option;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.ui.Checkbox;

public class BOption extends Option {
   boolean enabled;

   public BOption(String name) {
      this(name, null);
   }

   public BOption(String name, String extra) {
      super(name, extra);
   }

   public boolean c() {
      return this.enabled;
   }

   public void setValue(boolean value, boolean manual) {
      this.enabled = value;
      if (manual) {
         com.tann.dice.Main.getSettings().saveOptions();
         this.manualSelectAction();
      }
   }

   @Override
   public Actor makeUnlockActor(boolean big) {
      return this.makeCogActor();
   }

   @Override
   public Actor makeCogActor() {
      return this.makeComplexEscMenuActor(null);
   }

   public Actor makeComplexEscMenuActor(final Runnable extra) {
      final Checkbox cb = new Checkbox(this.enabled);
      cb.addToggleRunnable(new Runnable() {
         @Override
         public void run() {
            Sounds.playSound(cb.isOn() ? Sounds.pip : Sounds.pop);
            BOption.this.setValue(cb.isOn(), true);
            if (extra != null) {
               extra.run();
            }
         }
      });
      return cb.makeLabelledCheckbox(this.name, this.desc, this.warnString());
   }

   protected String warnString() {
      return null;
   }

   @Override
   public void reset() {
      this.enabled = false;
   }
}
