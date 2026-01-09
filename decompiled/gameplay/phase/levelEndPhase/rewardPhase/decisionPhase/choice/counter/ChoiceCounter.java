package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.counter;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.ui.TextWriter;

public class ChoiceCounter extends Group {
   final int target;
   int current;

   public ChoiceCounter(int target) {
      this.target = target;
      this.setSize(OptionLib.ROMAN_MODE.c() ? Math.abs(target * 2) + 55 : 35.0F, 12.0F);
      this.setCurrent(0);
      this.setTransform(false);
   }

   public void setCurrent(int current) {
      this.current = current;
      this.layout();
   }

   private void layout() {
      this.clearChildren();
      TextWriter slash = new TextWriter("[grey]/");
      int yLoc = (int)(this.getHeight() / 2.0F - slash.getHeight() / 2.0F);
      this.addActor(slash);
      slash.setPosition((int)(this.getWidth() / 2.0F - slash.getWidth() / 2.0F), yLoc);
      TextWriter curr = this.getFromVal(this.current);
      this.addActor(curr);
      curr.setPosition((int)(slash.getX() / 2.0F - curr.getWidth() / 2.0F), yLoc);
      TextWriter targ = this.getFromVal(this.target);
      this.addActor(targ);
      float av = this.getWidth() - slash.getX() - slash.getWidth();
      float start = slash.getX() + slash.getWidth();
      targ.setPosition((int)(start + av / 2.0F - targ.getWidth() / 2.0F), yLoc);
   }

   private TextWriter getFromVal(int val) {
      return new TextWriter(Words.getTierString(val, true));
   }

   public void draw(Batch batch, float parentAlpha) {
      Draw.fillActor(batch, this, Colours.dark, this.target > 0 ? Colours.green : Colours.purple, 1);
      super.draw(batch, parentAlpha);
   }
}
