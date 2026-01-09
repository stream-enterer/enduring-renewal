package com.tann.dice.gameplay.trigger.global;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.ui.TextWriter;

public class GlobalDescribeOnly extends Global {
   private final String desc;

   public GlobalDescribeOnly(String desc) {
      this.desc = desc;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      int mw = (int)(com.tann.dice.Main.width * 0.4F);
      TextWriter tw = new TextWriter(this.desc, mw);
      if (tw.getWidth() > 10.0F) {
         return tw;
      } else {
         int gapPad = 3;
         return new Pixl(gapPad, gapPad).border(Colours.grey).actor(tw).pix();
      }
   }

   @Override
   public String describeForSelfBuff() {
      return null;
   }

   @Override
   public boolean metaOnly() {
      return true;
   }
}
