package com.tann.dice.screens.pauseScreen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.save.SaveState;
import com.tann.dice.screens.Screen;
import com.tann.dice.screens.titleScreen.TitleScreen;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.TextWriter;

public class PauseScreen extends Screen {
   int taps = 0;
   Actor rem;
   int frames = 0;

   public PauseScreen() {
      Actor a = new TextWriter("[grey][b]paused[b][n]You probably shouldn't be seeing this... Tap a few times to escape.", 80);
      a.setTouchable(Touchable.disabled);
      this.addActor(a);
      Tann.center(a);
      this.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            if (PauseScreen.this.taps++ > 3) {
               com.tann.dice.Main.self().setScreen(new TitleScreen());
            }

            return true;
         }
      });
      this.rem = a;
      this.rem.setVisible(false);
   }

   @Override
   public void act(float delta) {
      this.frames++;
      this.rem.setVisible(this.frames > 60);
      if (this.frames == 10 && !this.tryLoadingAnyway()) {
         com.tann.dice.Main.self().setScreen(new TitleScreen());
      }

      super.act(delta);
   }

   private boolean tryLoadingAnyway() {
      Mode m = com.tann.dice.Main.getSettings().getLastMode();
      if (m != null && m.hasSave()) {
         String sk = m.getSaveKey();
         SaveState ss = SaveState.load(sk);
         if (ss == null) {
            return false;
         } else {
            ss.start();
            TannLog.log("loaded anyway");
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public void preDraw(Batch batch) {
      Draw.fillActor(batch, this, Colours.dark);
   }

   @Override
   public void postDraw(Batch batch) {
   }

   @Override
   public void preTick(float delta) {
   }

   @Override
   public void postTick(float delta) {
   }

   @Override
   protected void keyPress(int keycode) {
   }

   @Override
   public Screen copy() {
      return new PauseScreen();
   }
}
