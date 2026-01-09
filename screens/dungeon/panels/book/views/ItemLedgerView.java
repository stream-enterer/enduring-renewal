package com.tann.dice.screens.dungeon.panels.book.views;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ConcisePanel;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ItemPanel;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;

public class ItemLedgerView extends Group {
   private final Item item;

   public ItemLedgerView(Item i, ItemLedgerView.EquipSeenState ess) {
      this.setTransform(false);
      this.item = i;
      Actor actor;
      switch (ess) {
         case Seen:
            actor = i.makeImageActor();
            break;
         case Missing: {
            Group g = Tann.makeGroup(i.makeImageActor());
            HeroLedgerView.addUnencountered(g);
            actor = g;
            break;
         }
         case Locked:
         default: {
            Group g = Tann.makeGroup(14, 14);
            ImageActor ia = new ImageActor(Images.padlock);
            g.addActor(ia);
            Tann.center(ia);
            actor = g;
         }
      }

      int gap = 1;
      this.setSize(actor.getWidth() + gap * 2, actor.getHeight() + gap * 2);
      this.addActor(actor);
      Tann.center(actor);
      this.setTransform(false);
   }

   public void draw(Batch batch, float parentAlpha) {
      Draw.fillActor(batch, this, Colours.dark, Colours.grey, 1);
      super.draw(batch, parentAlpha);
   }

   public ItemLedgerView addBasicListener() {
      this.addListener(new TannListener() {
         @Override
         public boolean info(int button, float x, float y) {
            Sounds.playSound(Sounds.pip);
            ConcisePanel cp = new ItemPanel(ItemLedgerView.this.item, true);
            com.tann.dice.Main.getCurrentScreen().push(cp, 0.5F);
            Tann.center(cp);
            return true;
         }
      });
      return this;
   }

   public static enum EquipSeenState {
      Seen,
      Missing,
      Locked;
   }
}
