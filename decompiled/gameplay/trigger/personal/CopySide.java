package com.tann.dice.gameplay.trigger.personal;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;
import com.tann.dice.screens.dungeon.panels.Explanel.affectSides.SwapSideView;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.BorderImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CopySide extends Personal {
   final SpecificSidesType a;
   final SpecificSidesType b;

   public CopySide(SpecificSidesType a, SpecificSidesType b) {
      this.a = a;
      this.b = b;
      if (a.sideIndices.length != 1) {
         throw new RuntimeException("Invalid swap sides trigger: " + a + "/" + b);
      }
   }

   @Override
   public String describeForSelfBuff() {
      return "Copy " + this.a.description + " onto " + this.b.description;
   }

   @Override
   public void affectSide(EntSideState sideState, EntState owner, int triggerIndex) {
      int bIndex = this.b.validIndex(sideState, owner);
      if (bIndex != -1) {
         EntSide newSide = owner.getEnt().getSides()[this.a.sideIndices[0]];
         ReplaceWith.replaceSide(sideState, new EntSideState(owner, newSide, triggerIndex));
      }
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Actor special = makeCombined(this.a, this.b, findArrow(this.a, this.b), Colours.light, Colours.blue);
      return (Actor)(special != null
         ? special
         : new Pixl(3).actor(new SwapSideView(this.a)).image(Images.arrowRight, Colours.light).actor(new SwapSideView(this.b)).pix());
   }

   public static Actor makeCombined(SpecificSidesType a, SpecificSidesType b, TextureRegion tr, Color light, Color blue) {
      SpecificSidesType simple = getSimple(a, b);
      if (simple != null && tr != null) {
         Group g = Tann.makeGroup(new ImageActor(simple.templateImage, Colours.text));
         Actor aa = new BorderImage(tr, light, blue);
         g.addActor(aa);
         Tann.center(aa);
         if (simple == SpecificSidesType.LeftTwo) {
            aa.setX(aa.getX() - 2.0F);
         }

         return g;
      } else {
         return null;
      }
   }

   private static TextureRegion findArrow(SpecificSidesType a, SpecificSidesType b) {
      if (a.sideIndices[0] == 2) {
         return Images.arrowRight;
      } else {
         return b.sideIndices[0] == 2 ? Images.arrowLeft : null;
      }
   }

   private static SpecificSidesType getSimple(SpecificSidesType a, SpecificSidesType b) {
      List<Integer> total = new ArrayList<>();

      for (SpecificSidesType specificSidesType : Arrays.asList(a, b)) {
         for (int sideIndex : specificSidesType.sideIndices) {
            total.add(sideIndex);
         }
      }

      int ots = total.size();
      Tann.uniquify(total);
      if (total.size() != ots) {
         return null;
      } else {
         int size = a.sideIndices.length + b.sideIndices.length;

         for (SpecificSidesType value : SpecificSidesType.values()) {
            if (value.sideIndices.length == size) {
               List<Integer> cpy = new ArrayList<>(total);

               for (int sideIndex : value.sideIndices) {
                  cpy.remove(Integer.valueOf(sideIndex));
               }

               if (cpy.isEmpty()) {
                  return value;
               }
            }
         }

         return null;
      }
   }

   @Override
   public float getPriority() {
      return -10.0F;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return this.a.getCollisionBits(player) | this.b.getCollisionBits(player);
   }
}
