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
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.List;

public class CopyInvItems extends Personal {
   @Override
   public String describeForSelfBuff() {
      return "Copy all unequipped items";
   }

   @Override
   public List<Personal> getLinkedPersonals(Snapshot snapshot, EntState entState) {
      List<Personal> linkedTriggers = new ArrayList<>();
      List<Item> eq = snapshot.getFightLog().getContext().getParty().getItems(false);

      for (int i = 0; i < eq.size(); i++) {
         Item e = eq.get(i);
         linkedTriggers.addAll(e.getPersonals());
      }

      for (int i = linkedTriggers.size() - 1; i >= 0; i--) {
         Personal p = linkedTriggers.get(i);
         if (p instanceof CopyInvItems) {
            linkedTriggers.remove(i);
         }
      }

      return linkedTriggers;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      TextWriter tw = new TextWriter("[grey]Inv");
      int extraBorder = 4;
      Group g = new Group() {
         public void draw(Batch batch, float parentAlpha) {
            batch.setColor(Colours.z_white);
            Images.mirrorPatch.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
            super.draw(batch, parentAlpha);
         }
      };
      g.setSize(tw.getWidth() + extraBorder * 2, tw.getHeight() + extraBorder * 2);
      g.addActor(tw);
      Tann.center(tw);
      g.setTransform(false);
      return g;
   }

   @Override
   public float getPriority() {
      return -10.0F;
   }
}
