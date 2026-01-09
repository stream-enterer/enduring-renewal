package com.tann.dice.screens.dungeon.background;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.screens.Screen;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.EntContainer;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Tann;

public class Dust {
   public static boolean allowActor(Actor actorUnderMouse) {
      return actorUnderMouse instanceof Screen || actorUnderMouse instanceof EntContainer;
   }

   public static void addDust(DungeonScreen dungeonScreen) {
      int x = Gdx.input.getX() / com.tann.dice.Main.scale;
      int y = (Gdx.graphics.getHeight() - Gdx.input.getY()) / com.tann.dice.Main.scale;
      int numDust = 8;
      Color grown = Colours.shiftedTowards(Colours.grey, Colours.brown, 0.5F);

      for (int i = 0; i < numDust; i++) {
         Actor a = new Dust.CircleDraw();
         float size = Tann.random(2.0F, 4.0F);
         a.setSize(size, size);
         a.setColor(Colours.shiftedTowards(grown, Colours.random().cpy(), 0.07F));
         a.setPosition(x, y);
         float fadeDur = Tann.random(0.2F, 0.4F);
         float moveDist = Tann.random(3.0F, 7.0F);
         float angle = Tann.random((float) (Math.PI * 2));
         float dx = (float)(Math.cos(angle) * moveDist);
         float dy = (float)(Math.sin(angle) * moveDist);
         a.addAction(
            Actions.sequence(Actions.parallel(Actions.moveBy(dx, dy, fadeDur, Interpolation.pow2Out), Actions.alpha(0.0F, fadeDur)), Actions.removeActor())
         );
         dungeonScreen.addActor(a);
      }
   }

   static class CircleDraw extends Actor {
      public void draw(Batch batch, float parentAlpha) {
         batch.setColor(this.getColor());
         Draw.fillEllipse(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
         super.draw(batch, parentAlpha);
      }
   }
}
