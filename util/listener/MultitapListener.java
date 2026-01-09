package com.tann.dice.util.listener;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.trash.AGLCopy;
import java.util.HashMap;
import java.util.Map;

public class MultitapListener extends AGLCopy {
   static final float longPressDuration = 0.221F;
   Map<Integer, Long> touchedDown = new HashMap<>();
   int lastDownButton;
   boolean failedLastLongPress = false;
   boolean tapDownForAvoidingLongpressBug;

   public MultitapListener() {
      super(20.0F, 0.4F, 0.221F, 0.15F);
   }

   @Override
   public final void touchDown(InputEvent event, float x, float y, int pointer, int button) {
      this.tapDownForAvoidingLongpressBug = true;
      this.failedLastLongPress = false;
      this.lastDownButton = button;
      this.touchedDown.put(pointer, System.currentTimeMillis());
      super.touchDown(event, x, y, pointer, button);
   }

   @Override
   public final void touchUp(InputEvent event, float x, float y, int pointer, int button) {
      this.tapDownForAvoidingLongpressBug = false;
      long td = this.getPV(this.touchedDown, pointer);
      long dur = System.currentTimeMillis() - td;
      this.touchedDown.put(pointer, -1L);
      float durSeconds = (float)dur / 1000.0F;
      boolean overOriginal = Tann.isOver(event.getListenerActor(), x, y);
      if (button == 1) {
         if (overOriginal && this.info(button, x, y)) {
            event.cancel();
         }
      } else if (overOriginal) {
         if (!event.isCancelled()) {
            if (button == 0 && (this.failedLastLongPress || durSeconds < 0.221F || !com.tann.dice.Main.self().control.allowLongPress())) {
               boolean handled = false;
               handled |= this.action(button, pointer, x, y);
               if (!handled) {
                  handled |= this.info(button, x, y);
               }

               if (handled) {
                  event.cancel();
               }
            }

            super.touchUp(event, x, y, pointer, button);
         }
      } else {
         event.cancel();
      }
   }

   private long getPV(Map<Integer, Long> lastSuccessfulTouchedUp, int pointer) {
      Long v = lastSuccessfulTouchedUp.get(pointer);
      return v == null ? -1L : v;
   }

   @Override
   public final boolean longPress(Actor actor, float x, float y) {
      if (!com.tann.dice.Main.self().control.allowLongPress() || !this.tapDownForAvoidingLongpressBug) {
         return false;
      } else if (this.lastDownButton != 1) {
         boolean infod = this.info(42069, x, y);
         this.failedLastLongPress = !infod;
         return infod;
      } else {
         return super.longPress(actor, x, y);
      }
   }

   public boolean info(int button, float x, float y) {
      return false;
   }

   public boolean action(int button, int pointer, float x, float y) {
      return false;
   }
}
