package com.tann.dice.screens.generalPanels;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.entPanel.ItemHeroPanel;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ItemPanel;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.AlternativePop;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.PostPop;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.Glowverlay;
import com.tann.dice.util.ui.ScrollPane;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.resolver.ItemResolver;
import java.util.ArrayList;
import java.util.List;

public class InventoryPanel extends Group implements PostPop, AlternativePop {
   public static final int down = 3;
   private static final int PANEL_SIZE = 14;
   private static final int gap = 1;
   private static InventoryPanel self;
   private Actor invTitle;
   Actor zoom;

   public static InventoryPanel get() {
      if (self == null) {
         self = new InventoryPanel();
      }

      return self;
   }

   public static int getAcross() {
      return PartyManagementPanel.isTinyPanels() ? 4 : 6;
   }

   public static void resetSingleton() {
      self = null;
   }

   private InventoryPanel() {
      this.setSize(14 * getAcross() + 1 * (getAcross() + 1), 46.0F);
      this.reset();
      this.setTransform(false);
   }

   public void reset() {
      if (DungeonScreen.get() != null) {
         this.clearChildren();
         List<Item> items = DungeonScreen.get().getDungeonContext().getParty().getItems(false);
         List<Glowverlay> glows = new ArrayList<>();

         for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            ItemHeroPanel ep = new ItemHeroPanel(item, null);
            this.addActor(ep);
            ep.setPosition(i % getAcross() * 15 + 1, i / getAcross() * 15 + 1);
            if (item.isForceEquip()) {
               Glowverlay glowverlay = new Glowverlay(Colours.red);
               ep.addActor(glowverlay);
               glows.add(glowverlay);
            } else if (item.isNew()) {
               Glowverlay glowverlay = new Glowverlay();
               ep.addActor(glowverlay);
               glows.add(glowverlay);
            }
         }

         com.tann.dice.Main.getCurrentScreen().setGlowverlays(glows);
         TextWriter tw = new TextWriter("[grey]Items", 999, Colours.purple, 3);
         this.addActor(tw);
         tw.setPosition((int)(this.getWidth() / 2.0F - tw.getWidth() * 2.0F / 3.0F), (int)(this.getHeight() - 1.0F));
         this.invTitle = tw;
         Party p = DungeonScreen.get().partyManagementPanel.fightLog.getContext().getParty();
         if (!p.hasAnyItems()) {
            TextWriter twx = new TextWriter("[purple]no items :(");
            twx.setPosition((int)(this.getWidth() / 2.0F - twx.getWidth() / 2.0F), (int)(this.getHeight() / 2.0F - twx.getHeight() / 2.0F));
            this.addActor(twx);
         } else if (!com.tann.dice.Main.getSettings().isHasEquipped()) {
            TextWriter twx = new TextWriter("[yellow]drag to equip", (int)(this.getWidth() - 20.0F));
            twx.setTouchable(Touchable.disabled);
            twx.toBack();
            twx.setPosition((int)(this.getWidth() / 2.0F - twx.getWidth() / 2.0F), (int)(this.getHeight() / 2.0F - twx.getHeight() / 2.0F));
            this.addActor(twx);
         }

         this.setTouchable(Touchable.childrenOnly);
         this.getInvTitle().addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
               if (DungeonScreen.isWish()) {
                  (new ItemResolver() {
                     public void resolve(Item item) {
                        Sounds.playSound(Sounds.magic);
                        DungeonScreen ds = DungeonScreen.get();
                        if (ds != null) {
                           ds.getDungeonContext().getParty().addItem(item);
                        }
                     }
                  }).activate(Mode.WISH.wishFor("item", Colours.grey));
               }

               return true;
            }
         });
         if (this.zoom != null) {
            this.addActor(this.zoom);
            this.setZoomPos();
         }
      }
   }

   private void setZoomPos() {
      this.zoom.toBack();
      Actor a = get().getInvTitle();
      this.zoom.setX(a.getX(16) - 1.0F);
      this.zoom.setY(a.getY());
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(Colours.dark);
      Draw.fillActor(batch, this);
      batch.setColor(Colours.purple);
      PartyManagementPanel pmp = DungeonScreen.get().partyManagementPanel;
      if (pmp.isDragging()) {
         batch.setColor(Colours.light);
      }

      Draw.drawRectangle(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1);
      super.draw(batch, parentAlpha);
   }

   @Override
   public void postPop() {
      Sounds.playSound(Sounds.pop);
   }

   @Override
   public boolean alternativePop() {
      Tann.slideAway(this, Tann.TannPosition.Top, true);
      return true;
   }

   public Actor getInvTitle() {
      return this.invTitle;
   }

   public void addZoom(final Party prty) {
      this.zoom = new Pixl(0, 1).border(Colours.purple).actor(new ImageActor(Images.zoom2, Colours.grey)).pix();
      this.addActor(this.zoom);
      this.setZoomPos();
      this.zoom.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            Sounds.playSound(Sounds.pip);
            Pixl p = new Pixl(3, 3).border(Colours.grey);
            int WIDTH = (int)(com.tann.dice.Main.width * 0.7F);
            int HEIGHT = (int)(com.tann.dice.Main.height * 0.7F);
            boolean hasBoth = prty.getItems(true).size() > 0 && prty.getItems(false).size() > 0;

            for (boolean eqpt : Tann.BOTH) {
               List<Item> items = prty.getItems(eqpt);
               if (!items.isEmpty()) {
                  if (hasBoth) {
                     p.text("[text]" + (eqpt ? "equipped" : "inventory")).row(3);
                  }

                  for (Item item : items) {
                     p.actor(new ItemPanel(item, false), WIDTH);
                  }

                  if (!eqpt) {
                     p.row(10);
                  }
               }
            }

            Actor a = p.pix();
            ScrollPane scrollPane = Tann.makeScrollpane(a);
            Tann.selfPop(scrollPane);
            scrollPane.setWidth(Math.max(a.getWidth() + 6.0F, scrollPane.getWidth()));
            scrollPane.setHeight(Math.min(a.getHeight(), (float)HEIGHT));
            com.tann.dice.Main.getCurrentScreen().push(scrollPane, true, true, true, 0.8F);
            Tann.center(scrollPane);
            return true;
         }
      });
   }
}
