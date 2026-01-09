package com.tann.dice.screens.dungeon.panels.time;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.ui.TextWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Clock extends Group {
   private static final int gap = 1;
   TextWriter tw;
   SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
   Calendar calendar = Calendar.getInstance();

   public Clock() {
      this.tw = new TextWriter("Clock unset");
      this.addActor(this.tw);
      this.tw.setPosition(1.0F, 1.0F);
      this.tickStuff();
      this.setTouchable(Touchable.disabled);
      this.setTransform(false);
   }

   public void tickStuff() {
      this.calendar.setTimeInMillis(System.currentTimeMillis());
      this.tw.setText("[grey]" + this.format.format(this.calendar.getTime()));
      this.setSize(this.tw.getWidth() + 2.0F, this.tw.getHeight() + 2.0F);
   }

   public void act(float delta) {
      this.tickStuff();
      super.act(delta);
   }

   public void draw(Batch batch, float parentAlpha) {
      Draw.fillActor(batch, this, Colours.dark);
      super.draw(batch, parentAlpha);
   }
}
