package com.tann.dice.util.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.save.settings.option.Option;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.choice.ChoiceDialog;
import java.util.Arrays;

public class Checkbox extends Actor {
   boolean on;
   public static final int HORI_SIZE = 12;
   private Runnable toggleRunnable;
   private static final int MAX_CHECKBOX_TEXT_LABEL_WIDTH = 93;

   private static int getSize() {
      return 12;
   }

   public Checkbox(boolean initial) {
      this(initial, getSize());
   }

   public Checkbox(boolean initial, int size) {
      this.on = initial;
      this.setSize(size, size);
   }

   private void toggle() {
      this.on = !this.on;
      if (this.toggleRunnable != null) {
         this.toggleRunnable.run();
      }

      Flasher f = new Flasher(this, this.on ? Colours.green : Colours.grey, 0.25F);
      f.setPosition(this.getX(), this.getY());
      this.getParent().addActor(f);
   }

   public void addToggleRunnable(Runnable runnable) {
      this.toggleRunnable = runnable;
   }

   public void draw(Batch batch, float parentAlpha) {
      Draw.fillActor(batch, this, Colours.dark, Colours.purple, 1);
      if (this.on) {
         batch.setColor(Colours.z_white);
         Draw.drawCentered(
            batch, Images.checkbox_tick_alone, (float)((int)(this.getX() + this.getWidth() / 2.0F)), (float)((int)(this.getY() + this.getHeight() / 2.0F))
         );
      }

      super.draw(batch, parentAlpha);
   }

   public boolean isOn() {
      return this.on;
   }

   public void force(boolean newValue) {
      this.on = newValue;
   }

   public Actor makeLabelledCheckbox(final String name, final String extraText, final String warnString) {
      Pixl p = new Pixl();
      p.actor(this).gap(this instanceof RadioCheckbox ? 1 : 3).text("[text]" + name, 93);
      Actor a = p.pix();
      a.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            if (!Checkbox.this.warn(name, warnString)) {
               Checkbox.this.toggle();
            }

            return true;
         }

         @Override
         public boolean info(int button, float x, float y) {
            Option.showExtraInfo(name, extraText);
            return true;
         }
      });
      return a;
   }

   public Actor makeLabelledCheckbox(String name, String extraText) {
      return this.makeLabelledCheckbox(name, extraText, null);
   }

   public void addDefaultToggleListener() {
      this.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            Checkbox.this.toggle();
            Sounds.playSound(Sounds.pipSmall);
            return true;
         }
      });
   }

   private boolean warn(String checkboxName, String warn) {
      if (warn == null) {
         return false;
      } else {
         String s = "Are you sure you want to " + (!this.on ? "enable" : "disable") + " [light]" + checkboxName + "[cu]? " + warn;
         Actor a = new Pixl().text(s, (int)(com.tann.dice.Main.width * 0.7F)).pix();
         ChoiceDialog cd = new ChoiceDialog(null, Arrays.asList(a), ChoiceDialog.ChoiceNames.YesCancel, new Runnable() {
            @Override
            public void run() {
               Checkbox.this.toggle();
               com.tann.dice.Main.getCurrentScreen().pop(ChoiceDialog.class);
            }
         }, new Runnable() {
            @Override
            public void run() {
               Sounds.playSound(Sounds.pop);
               com.tann.dice.Main.getCurrentScreen().pop(ChoiceDialog.class);
            }
         });
         Sounds.playSound(Sounds.pip);
         com.tann.dice.Main.getCurrentScreen().push(cd, true, true, false, 0.9F);
         Tann.center(cd);
         return true;
      }
   }
}
