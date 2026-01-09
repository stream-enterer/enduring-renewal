package com.tann.dice.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.tann.dice.statics.sound.Sounds;

public class InputBlocker extends Actor {
   public static final float DARK = 0.7F;
   Runnable r;
   private float alpha = 0.0F;
   boolean blockerListen;
   private boolean medium;

   public void setAction(Runnable r) {
      this.r = r;
   }

   public InputBlocker() {
      this.setAction(new Runnable() {
         @Override
         public void run() {
            if (InputBlocker.this.blockerListen) {
               com.tann.dice.Main.getCurrentScreen().forcePop();
            }
         }
      });
      this.setSize(com.tann.dice.Main.width, com.tann.dice.Main.height);
      this.addListener(new InputListener() {
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (InputBlocker.this.r != null) {
               InputBlocker.this.r.run();
            }

            com.tann.dice.Main.getCurrentScreen().popAllLight();
            Sounds.playSound(Sounds.pop);
            event.handle();
            event.stop();
            return true;
         }
      });
   }

   public void setAlpha(float alpha) {
      this.alpha = alpha;
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(Colours.withAlpha(Colours.z_black, this.alpha));
      Draw.fillActor(batch, this);
   }

   public void setActiveClicker(boolean blockerListen) {
      this.blockerListen = blockerListen;
   }

   public void setMedium(boolean medium) {
      this.medium = medium;
   }

   public boolean isMedium() {
      return this.medium;
   }
}
