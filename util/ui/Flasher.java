package com.tann.dice.util.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.util.Draw;

public class Flasher extends Group {
   public Flasher(Actor a, Color col) {
      this(a, col, 0.8F);
   }

   public Flasher(Actor a, Color col, float duration) {
      this(a, col, duration, Interpolation.linear);
   }

   public Flasher(Actor a, Color col, float duration, Interpolation interpolation) {
      this((int)a.getWidth(), (int)a.getHeight(), col, duration, interpolation);
   }

   public Flasher(int width, int height, Color col, float duration, Interpolation interpolation) {
      this.setTransform(false);
      this.setTouchable(Touchable.disabled);
      this.setSize(width, height);
      this.setColor(col);
      this.addAction(Actions.fadeOut(duration, interpolation));
      this.addAction(Actions.after(Actions.removeActor()));
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(this.getColor());
      Draw.fillActor(batch, this);
      super.draw(batch, parentAlpha);
   }
}
