package com.tann.dice.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class RainbowAction extends TemporalAction {
   Color[] colours;

   public RainbowAction(float duration, Color... colours) {
      this.setDuration(duration);
      this.colours = colours;
   }

   protected void update(float percent) {
      this.actor.setColor(Colours.shiftedTowards(this.colours, percent));
   }
}
