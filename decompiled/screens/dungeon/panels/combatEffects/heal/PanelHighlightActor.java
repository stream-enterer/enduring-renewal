package com.tann.dice.screens.dungeon.panels.combatEffects.heal;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.util.Colours;

public class PanelHighlightActor extends Actor {
   private static final NinePatch patch = new NinePatch(ImageUtils.loadExt("patch/panelBorderBorder"), 6, 6, 6, 6);
   public static float FADE_DURATION = 0.8F;

   public PanelHighlightActor(Color col, float duration, Group parent) {
      this.setColor(col);
      Color target = Colours.withAlpha(Colours.shiftedTowards(this.getColor(), Colours.z_white, 0.7F), 0.0F);
      this.addAction(Actions.sequence(Actions.color(target, duration, Interpolation.pow3In), Actions.removeActor()));
      parent.addActor(this);
      int extra = 3;
      this.setPosition(-extra, -extra);
      this.setSize(parent.getWidth() + extra * 2, parent.getHeight() + extra * 2);
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(this.getColor());
      patch.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
      super.draw(batch, parentAlpha);
   }
}
