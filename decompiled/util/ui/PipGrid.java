package com.tann.dice.util.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Rectactor;

public class PipGrid {
   public static final int WIDTH = 2;
   public static final int HEIGHT = 1;
   public static final int GAP = 1;

   public static Actor make(int pips) {
      Pixl p = new Pixl();

      for (int i = 0; i < pips; i++) {
         p.actor(new Rectactor(2, 1, Colours.light));
         if (i < pips - 1) {
            p.row(1);
         }
      }

      return p.pix();
   }
}
