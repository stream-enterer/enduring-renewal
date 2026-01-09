package com.tann.dice.gameplay.trigger.personal.item.copyItem;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.List;

public class CopyAlliedItems extends Personal {
   public final int minTier;
   public final int maxTier;

   public CopyAlliedItems(int tier) {
      this(tier, tier);
   }

   public CopyAlliedItems(int minTier, int maxTier) {
      this.minTier = minTier;
      this.maxTier = maxTier;
   }

   @Override
   public String describeForSelfBuff() {
      String tierString = this.minTier + "-" + this.maxTier;
      if (this.minTier == this.maxTier) {
         tierString = this.maxTier + "";
      }

      return "Gain the effects of all tier " + tierString + " items on other heroes";
   }

   @Override
   public List<Personal> getLinkedPersonals(Snapshot snapshot, EntState entState) {
      List<Personal> linkedTriggers = new ArrayList<>();

      for (EntState es : snapshot.getStates(true, null)) {
         if (es.getEnt() != entState.getEnt()) {
            List<Item> eq = es.getEnt().getItems();

            for (int eqi = 0; eqi < eq.size(); eqi++) {
               Item e = eq.get(eqi);
               if (e.getTier() >= this.minTier && e.getTier() <= this.maxTier) {
                  List<Personal> personals = e.getPersonals();

                  for (int i = 0; i < personals.size(); i++) {
                     Personal p = personals.get(i);
                     if (!(p instanceof CopyAlliedItems)) {
                        linkedTriggers.add(p);
                     }
                  }
               }
            }
         }
      }

      return linkedTriggers;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      String tierString = "";
      if (this.minTier == this.maxTier) {
         tierString = Words.getTierString(this.minTier);
      } else {
         tierString = Words.getTierString(this.minTier) + "-" + Words.getTierString(this.maxTier);
      }

      TextWriter tw = new TextWriter("[dark]" + tierString);
      int extraBorder = 4;
      Group g = new Group() {
         public void draw(Batch batch, float parentAlpha) {
            batch.setColor(Colours.z_white);
            Images.mirrorPatch.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
            super.draw(batch, parentAlpha);
         }
      };
      g.setTransform(false);
      g.setSize(tw.getWidth() + extraBorder * 2, tw.getHeight() + extraBorder * 2);
      g.addActor(tw);
      Tann.center(tw);
      return g;
   }

   @Override
   public float getPriority() {
      return -10.0F;
   }
}
