package com.tann.dice.screens.generalPanels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.ExplanelReposition;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.screens.dungeon.panels.Explanel.Explanel;
import com.tann.dice.screens.dungeon.panels.entPanel.ItemHeroPanel;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ItemPanel;
import com.tann.dice.screens.dungeon.panels.tutorial.TutorialManager;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.AlternativePop;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.KeyListen;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.PopRequirement;
import com.tann.dice.util.PostPop;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannStageUtils;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.tp.TP;
import java.util.ArrayList;
import java.util.List;

public class PartyManagementPanel extends Group implements PostPop, ExplanelReposition, AlternativePop, PopRequirement, KeyListen {
   final FightLog fightLog;
   List<EntPanelInventory> entityPanelInventories = new ArrayList<>();
   InventoryPanel invPan;
   private static boolean tinyPanels;
   ItemHeroPanel draggingPanel;

   public PartyManagementPanel(FightLog fightLog) {
      this.fightLog = fightLog;
      this.setTransform(false);
   }

   private Party getParty() {
      return this.fightLog.getContext().getParty();
   }

   public static boolean isTinyPanels() {
      return tinyPanels || OptionLib.DIE_PANEL_TINY.c();
   }

   public void refresh() {
      tinyPanels = false;
      this.getParty().refreshAllSlots();
      this.clearChildren();
      this.entityPanelInventories.clear();
      int gap = 2;
      List<Hero> heroList = new ArrayList<>(this.fightLog.getSnapshot(FightLog.Temporality.Present).getAliveHeroEntities());

      for (int i = heroList.size() - 1; i >= 0; i--) {
         Hero h = heroList.get(i);
         if (h.getBlankState().skipEquipScreen()) {
            heroList.remove(i);
         }
      }

      if (heroList.size() != 0) {
         EntPanelInventory examplePanel = heroList.get(0).getDiePanel();
         examplePanel.layout(false);
         int panelWidth = (int)examplePanel.getWidth();
         int panelHeight = (int)examplePanel.getHeight();
         int totalPanelsPlusInv = heroList.size() + 1;
         float rawAcross = (com.tann.dice.Main.width - 5) / (panelWidth * 1.05F);
         int panelsAcross = (int)Math.min(4.0F, rawAcross);
         int panelsDown = (int)Math.ceil((float)totalPanelsPlusInv / panelsAcross);
         if (panelsAcross == 1 && rawAcross > 1.4F || panelsDown * panelHeight > com.tann.dice.Main.height || OptionLib.HIDE_SPINNERS.c()) {
            tinyPanels = true;
            EntPanelInventory dp = heroList.get(0).getDiePanel();
            dp.layout(true);
            panelWidth = (int)dp.getWidth();
            float fpa = Math.max(1.0F, (com.tann.dice.Main.width - 5) / (panelWidth * 1.05F));
            panelsAcross = (int)fpa;
            if (panelsAcross == 1 && fpa > 1.7F) {
               panelsAcross = 2;
            }

            panelsDown = (int)Math.ceil((float)totalPanelsPlusInv / panelsAcross);
         }

         if (heroList.size() == 5 && panelsDown > 2 && !com.tann.dice.Main.isPortrait()) {
            panelsAcross = 3;
            panelsDown = 2;
         }

         for (int test = panelsAcross - 1; test >= 0; test--) {
            if (test * panelsDown >= totalPanelsPlusInv) {
               panelsAcross = test;
            }
         }

         this.setSize(panelWidth * panelsAcross + gap * (panelsAcross + 1), panelHeight * panelsDown + gap * (panelsDown + 1));
         InventoryPanel.resetSingleton();
         this.invPan = InventoryPanel.get();
         this.invPan.reset();

         for (int x = 0; x < panelsAcross; x++) {
            for (int y = 0; y < panelsDown; y++) {
               int xPos = x * panelWidth + gap * (x + 1);
               int yPos = (int)(this.getHeight() - (y + 1) * panelHeight) - gap * (y + 1);
               if (y == 0 && x == panelsAcross - 1) {
                  this.invPan.setPosition(xPos + 2, yPos);
               } else {
                  int heroIndex = y * panelsAcross + x;
                  if (y > 0) {
                     heroIndex--;
                  }

                  if (heroIndex < heroList.size()) {
                     Ent de = heroList.get(heroIndex);
                     EntPanelInventory dp = de.getDiePanel();
                     dp.setTraitsVisible(false);
                     dp.clearActions();
                     dp.setScale(1.0F);
                     dp.setPosition(xPos, yPos);
                     this.addActor(dp);
                     this.entityPanelInventories.add(dp);
                  }
               }
            }
         }

         this.addActor(this.invPan);
         Actor done = new Pixl(0, 1).border(Colours.grey).actor(new ImageActor(Images.ui_crossAlmanac, Colours.grey)).pix();
         this.addActor(done);
         done.setPosition((int)(this.getWidth() - done.getWidth()), (int)(this.getHeight() - done.getHeight()));
         done.addListener(new TannListener() {
            @Override
            public boolean action(int button, int pointer, float x, float yx) {
               PartyManagementPanel.this.doneAction();
               return true;
            }
         });
         done.toBack();
         Party prty = this.fightLog.getContext().getParty();
         if (prty.getItems().size() > 2) {
            this.invPan.addZoom(prty);
         }
      }
   }

   private void doneAction() {
      com.tann.dice.Main.getCurrentScreen().popAllLight();
      com.tann.dice.Main.getCurrentScreen().pop(PartyManagementPanel.class);
      Sounds.playSound(Sounds.pop);
   }

   public void draw(Batch batch, float parentAlpha) {
      Draw.fillActor(batch, this, Colours.dark, Colours.grey, 1);
      super.draw(batch, parentAlpha);
   }

   public void equip(Ent ent, Item item, int index) {
      Party p = this.fightLog.getContext().getParty();
      TP<int[], int[]> before = p.getWholePartyHash();
      if (index == 3 && ent.getNumberItemSlots() < 4) {
         index = 2;
      }

      if (index == 2 && ent.getNumberItemSlots() < 3) {
         index = 0;
      }

      index = Math.min(index, ent.getNumberItemSlots() - 1);
      com.tann.dice.Main.getSettings().setHasEquipped(true);
      if (!item.usableBy(ent)) {
         Sounds.playSound(Sounds.error);
      } else {
         Ent previousHolder = p.getEquippee(item);
         int previousSlot = -1;
         if (previousHolder != null) {
            previousSlot = previousHolder.removeItem(item);
         }

         Item replaced = ent.addItem(item, index);
         if (replaced != null) {
            if (previousHolder == ent) {
               DungeonScreen.get().getTutorialManager().onAction(TutorialManager.TutorialAction.SwapItems);
            }

            if (previousHolder != null && previousSlot != -1) {
               previousHolder.addItem(replaced, previousSlot);
            } else {
               p.addItem(replaced);
            }
         }

         Sounds.playSound(Sounds.drop);
         p.refreshAllSlots();
         this.invPan.reset();

         for (Hero h : p.getHeroes()) {
            h.updateOutOfCombat();
         }

         TP<int[], int[]> after = p.getWholePartyHash();
         sortFlashes(p, before, after);
         p.afterEquip();
      }
   }

   public static void sortFlashes(Party p, TP<int[], int[]> before, TP<int[], int[]> after) {
      for (int i = 0; i < p.getHeroes().size(); i++) {
         Hero h = p.getHeroes().get(i);
         int[] previousHashes = new int[6];
         int[] afterHashes = new int[6];
         System.arraycopy(before.b, i * 6, previousHashes, 0, 6);
         System.arraycopy(after.b, i * 6, afterHashes, 0, 6);
         EntPanelInventory dp = h.getDiePanel();
         dp.layout();
         dp.flash(before.a[i * 2], after.a[i * 2], before.a[i * 2 + 1], after.a[i * 2 + 1], previousHashes, afterHashes);
         h.getDie().clearTextureCache();
      }
   }

   @Override
   public void postPop() {
      this.releasePanel();
   }

   @Override
   public void repositionExplanel(Explanel exp) {
      int x = (int)(Gdx.input.getX() / com.tann.dice.Main.scale - exp.getMaxWidth() / 2.0F);
      int xOverflow = (int)(exp.getMaxWidth() - exp.getWidth());
      x = (int)Math.min(com.tann.dice.Main.width - exp.getWidth() - xOverflow / 2, (float)Math.max(xOverflow / 2, x));
      exp.setX(x);
      if (com.tann.dice.Main.isPortrait()) {
         exp.setY((Gdx.graphics.getHeight() - Gdx.input.getY()) / com.tann.dice.Main.scale + exp.getExtraBelowExtent() + 30);
      } else {
         exp.setY((int)(com.tann.dice.Main.height - exp.getHeight() - 2.0F));
      }
   }

   @Override
   public boolean alternativePop() {
      for (Ent de : this.getHeroes()) {
         de.getDiePanel().setTraitsVisible(true);
      }

      Tann.slideAway(this, Tann.TannPosition.Bot, 1, true);
      PhaseManager.get().getPhase().refreshPhase();
      return true;
   }

   private List<Hero> getHeroes() {
      return this.fightLog.getSnapshot(FightLog.Temporality.Present).getAliveHeroEntities();
   }

   @Override
   public boolean allowPop() {
      return true;
   }

   public void releasePanel() {
      if (this.draggingPanel != null) {
         Vector2 dragMid = Tann.getAbsoluteCoordinates(this.draggingPanel)
            .cpy()
            .add(this.draggingPanel.getWidth() / 2.0F, this.draggingPanel.getHeight() / 2.0F);
         this.draggingPanel.remove();
         if (this.draggingPanel != null) {
            boolean equipped = false;

            for (EntPanelInventory dp : this.entityPanelInventories) {
               Tann.TannPosition pos = Tann.globalOver(dp, dragMid.x, dragMid.y, 0.4F);
               Tann.TannPosition xPos = Tann.globalOverX(dp, dragMid.x, dragMid.y, 0.6F - (tinyPanels ? 0.22F : 0.0F));
               if (xPos == Tann.TannPosition.Right && Tann.globalOverX(dp, dragMid.x, dragMid.y, 0.8F - (tinyPanels ? 0.12F : 0.0F)) == Tann.TannPosition.Right
                  )
                {
                  xPos = Tann.TannPosition.Center;
               }

               int index;
               if (pos == Tann.TannPosition.Bot) {
                  index = 1;
               } else {
                  index = xPos == Tann.TannPosition.Right ? 2 : (xPos == Tann.TannPosition.Center ? 3 : 0);
               }

               if (pos != null && dp.ent.getNumberItemSlots() > 0) {
                  this.equip(dp.ent, this.draggingPanel.item, index);
                  equipped = true;
                  break;
               }
            }

            if (!equipped) {
               Party p = this.fightLog.getContext().getParty();
               TP<int[], int[]> before = p.getWholePartyHash();
               p.unequip(this.draggingPanel.item);
               p.addItem(this.draggingPanel.item, this.getInventoryPosition());
               Sounds.playSound(Sounds.drop);
               sortFlashes(p, before, p.getWholePartyHash());
            }

            this.draggingPanel = null;
            DungeonScreen.get().save();
         }
      }
   }

   public int getInventoryPosition() {
      float x = Gdx.input.getX() / com.tann.dice.Main.scale;
      float y = com.tann.dice.Main.height - Gdx.input.getY() / com.tann.dice.Main.scale;
      Vector2 pos = new Vector2(x, y);
      pos.sub(Tann.getAbsoluteCoordinates(this.invPan));
      pos.scl(1.0F / Images.itemBorder.getRegionHeight());
      return !(pos.x < 0.0F) && !(pos.x > InventoryPanel.getAcross()) && !(pos.y < 0.0F) && !(pos.y > 3.0F)
         ? (int)pos.y * InventoryPanel.getAcross() + (int)pos.x
         : -1;
   }

   public boolean isDragging() {
      return this.draggingPanel != null;
   }

   public Item getDraggingItem() {
      return this.draggingPanel == null ? null : this.draggingPanel.item;
   }

   public boolean dragPanel(ItemHeroPanel ep) {
      if (this.draggingPanel == ep) {
         return false;
      } else {
         com.tann.dice.Main.getCurrentScreen().popAllLight();
         this.draggingPanel = ep;
         Sounds.playSound(Sounds.pickup, 1.0F, Tann.random(0.8F, 1.2F));
         Party p = this.fightLog.getContext().getParty();
         p.removeItem(ep.item);
         ep.remove();
         ep.toFront();
         InventoryPanel.get().addActor(ep);
         ItemPanel exp = new ItemPanel(ep.item, true);
         TannStageUtils.removeCopyButton(exp);
         ep.addActor(exp);
         exp.setPosition((int)(ep.getWidth() / 2.0F - exp.getWidth() / 2.0F), (int)(ep.getHeight() + 5.0F));
         return true;
      }
   }

   public void act(float delta) {
      super.act(delta);
      Vector2 coord = Tann.getAbsoluteCoordinates(this.invPan);
      if (this.draggingPanel != null) {
         this.draggingPanel
            .setPosition(
               (int)(Gdx.input.getX() / com.tann.dice.Main.scale - coord.x - this.draggingPanel.getWidth() / 2.0F),
               (int)(com.tann.dice.Main.height - Gdx.input.getY() / com.tann.dice.Main.scale - coord.y - this.draggingPanel.getHeight() / 2.0F)
            );
      }
   }

   public void onShow() {
      for (Item i : this.fightLog.getContext().getParty().getItems()) {
         i.setNew(false);
      }

      if (this.getWidth() > com.tann.dice.Main.width * 1.1F || this.getHeight() > com.tann.dice.Main.height * 1.1F) {
         DungeonScreen.get().showDialog("Maybe decrease UI size in [white][cog]");
      }
   }

   @Override
   public boolean keyPress(int keycode) {
      switch (keycode) {
         case 37:
         case 66:
         case 111:
         case 160:
            this.doneAction();
         default:
            return true;
         case 46:
            this.randomiseEquipment();
            return true;
      }
   }

   private void randomiseEquipment() {
      Party p = this.fightLog.getContext().getParty();
      TP<int[], int[]> before = p.getWholePartyHash();
      this.releasePanel();
      if (p.randomiseEquipment()) {
         p.refreshAllSlots();
         this.invPan.reset();

         for (Hero h : p.getHeroes()) {
            h.updateOutOfCombat();
         }

         Sounds.playSound(Sounds.flap);
         sortFlashes(p, before, p.getWholePartyHash());
         DungeonScreen.get().save();
      }
   }
}
