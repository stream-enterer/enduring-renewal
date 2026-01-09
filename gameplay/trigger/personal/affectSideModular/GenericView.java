package com.tann.dice.gameplay.trigger.personal.affectSideModular;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.test.util.TestUtils;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannFont;
import java.util.ArrayList;
import java.util.List;

public abstract class GenericView extends Group {
   List<EffectDraw> draws = new ArrayList<>();
   private String err = null;

   public GenericView() {
      this.setTransform(false);
   }

   public void addDraw(EffectDraw a) {
      this.draws.add(a);
   }

   public abstract Vector2[] getSidePositions();

   public void draw(Batch batch, float parentAlpha) {
      if (this.err != null) {
         batch.setColor(Colours.purple);
         TannFont.font.drawString(batch, this.err, (int)this.getX(), (int)this.getY());
      } else {
         super.draw(batch, parentAlpha);
         Vector2[] positions = this.getSidePositions();

         try {
            for (int i = 0; i < positions.length; i++) {
               Vector2 v = positions[i];

               for (EffectDraw a : this.draws) {
                  int x = (int)(this.getX() + v.x);
                  int y = (int)(this.getY() + v.y);
                  a.draw(batch, x, y, i);
               }
            }
         } catch (Exception var10) {
            var10.printStackTrace();
            if (TestUtils.shouldCrash()) {
               throw var10;
            }

            this.err = Tann.makeEllipses(var10.getClass().getSimpleName(), 15);
         }
      }
   }
}
