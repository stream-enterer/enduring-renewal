package com.tann.dice.screens.dungeon.panels.time;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.TextWriter;

public class SpeedrunTimer extends Group {
   private static final int gap = 1;
   final DungeonContext context;
   TextWriter tw;

   public SpeedrunTimer(DungeonContext context) {
      this.context = context;
      this.tw = new TextWriter("Time unset");
      this.addActor(this.tw);
      this.tw.setPosition(1.0F, 1.0F);
      this.tickStuff();
      this.setTouchable(Touchable.disabled);
      this.setTransform(false);
   }

   public void tickStuff() {
      this.tw.setText("[grey]" + Tann.parseSeconds(this.context.getTimeTakenSeconds()));
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
