package com.tann.dice.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.action.PixAction;

public class TextWisp extends Group {
   static final int gap = 2;
   TextWriter tw;

   public TextWisp(String text, int dx, int dy, float t) {
      this(text, dx, dy, t, 0.0F);
   }

   public TextWisp(String text, int dx, int dy, float t, float delay) {
      this.tw = new TextWriter(text);
      this.addActor(this.tw);
      this.tw.setPosition(2.0F, 2.0F);
      this.setColor(Colours.dark);
      this.setSize(this.tw.getWidth() + 4.0F, this.tw.getHeight() + 4.0F);
      this.addAction(
         Actions.sequence(
            Actions.delay(delay),
            Actions.parallel(PixAction.moveBy(dx, dy, t, Interpolation.linear), Actions.alpha(0.0F, t, Interpolation.pow2In)),
            Actions.removeActor()
         )
      );
      this.setTransform(false);
   }

   public void act(float delta) {
      super.act(delta);
      float alpha = this.getColor().a;
      this.tw.setAlpha(alpha);
   }

   public void draw(Batch batch, float parentAlpha) {
      Draw.fillActor(batch, this, this.getColor(), Colours.withAlpha(Colours.grey, this.getColor().a), 1);
      super.draw(batch, parentAlpha);
   }
}
