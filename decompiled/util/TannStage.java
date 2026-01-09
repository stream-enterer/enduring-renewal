package com.tann.dice.util;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array.ArrayIterator;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.List;

public class TannStage extends Stage {
   public TannStage(Viewport viewport) {
      super(viewport);
   }

   public static int maxActorWidth(List<Actor> actors) {
      int max = 0;

      for (Actor actor : actors) {
         max = (int)Math.max((float)max, actor.getWidth());
      }

      return max;
   }

   public static void alphaSetRecursive(Group g, float alpha) {
      Tann.setAlpha(g, alpha);
      ArrayIterator var2 = g.getChildren().iterator();

      while (var2.hasNext()) {
         Actor a = (Actor)var2.next();
         if (a instanceof Group) {
            alphaSetRecursive((Group)a, alpha);
         } else {
            Tann.setAlpha(a, alpha);
         }
      }
   }

   public boolean touchDown(int screenX, int screenY, int pointer, int button) {
      return super.touchDown(screenX, screenY, pointer, button);
   }

   public boolean touchUp(int screenX, int screenY, int pointer, int button) {
      return super.touchUp(screenX, screenY, pointer, button);
   }

   public void calculateScissors(Rectangle localRect, Rectangle scissorRect) {
      super.calculateScissors(localRect, scissorRect);
      int scaleFactor = com.tann.dice.Main.scale;
      scissorRect.set(scissorRect.x / scaleFactor, scissorRect.y / scaleFactor, scissorRect.width / scaleFactor, scissorRect.height / scaleFactor);
   }
}
