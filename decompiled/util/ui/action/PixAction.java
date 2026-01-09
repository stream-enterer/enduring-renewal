package com.tann.dice.util.ui.action;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;

public class PixAction {
   public static Action moveTo(int x, int y, float dur, Interpolation terp) {
      return new PixMoveTo(x, y, dur, terp);
   }

   public static Action moveBy(int dx, int dy, float dur, Interpolation terp) {
      return new PixMoveBy(dur, terp, dx, dy);
   }
}
