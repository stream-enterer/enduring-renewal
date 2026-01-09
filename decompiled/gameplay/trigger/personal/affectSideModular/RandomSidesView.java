package com.tann.dice.gameplay.trigger.personal.affectSideModular;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class RandomSidesView extends GenericView {
   Vector2[] positions;

   public RandomSidesView(int numSides) {
      this(numSides, Colours.AS_BORDER);
   }

   public RandomSidesView(int numSides, Color col) {
      List<ImageActor> sideList = new ArrayList<>();

      for (int i = 0; i < numSides; i++) {
         ImageActor ia = new ImageActor(Images.replaceAny);
         ia.setColor(col);
         sideList.add(ia);
      }

      Pixl p = new Pixl(numSides == 1 ? 0 : 2);

      for (int i = 0; i < numSides; i++) {
         p.actor(sideList.get(i));
      }

      Tann.become(this, p.pix());
      this.positions = new Vector2[numSides];

      for (int i = 0; i < numSides; i++) {
         this.positions[i] = new Vector2(sideList.get(i).getX(), sideList.get(i).getY());
      }
   }

   @Override
   public Vector2[] getSidePositions() {
      return this.positions;
   }
}
