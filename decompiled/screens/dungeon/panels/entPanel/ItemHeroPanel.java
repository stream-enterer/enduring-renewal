package com.tann.dice.screens.dungeon.panels.entPanel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.ExplanelReposition;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ItemPanel;
import com.tann.dice.screens.generalPanels.InventoryPanel;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannFont;
import com.tann.dice.util.listener.TannListener;

public class ItemHeroPanel extends Group {
   public final Item item;
   public final Hero hero;
   final int slotIndex;

   public ItemHeroPanel(Item item, Hero hero) {
      this(item, hero, -1);
   }

   public ItemHeroPanel(final Item item, Hero hero, int slot) {
      this.hero = hero;
      this.item = item;
      this.slotIndex = slot;
      this.setTransform(false);
      int size = Images.itemBorder.getRegionHeight();
      this.setSize(size, size);
      this.addListener(
         new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
               DungeonScreen ds = DungeonScreen.get();
               return ds != null && item != null && ds.partyManagementPanel.hasParent() && pointer == 0
                  ? ds.partyManagementPanel.dragPanel(ItemHeroPanel.this)
                  : false;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
               DungeonScreen ds = DungeonScreen.get();
               if (ds == null || item == null || !ds.partyManagementPanel.hasParent()) {
                  super.touchUp(event, x, y, pointer, button);
               } else if (pointer == 0) {
                  ds.partyManagementPanel.releasePanel();
               }
            }
         }
      );
      this.addListener(new TannListener() {
         @Override
         public boolean info(int button, float x, float y) {
            DungeonScreen ds = DungeonScreen.get();
            if (ds != null && ds.partyManagementPanel.hasParent()) {
               return false;
            } else if (item != null && !ItemHeroPanel.this.isLookingToEquip() && !ItemHeroPanel.this.isDragging()) {
               Actor a = com.tann.dice.Main.getCurrentScreen().getTopPushedActor();
               if (a instanceof ItemPanel) {
                  com.tann.dice.Main.getCurrentScreen().popSingleLight();
                  ItemPanel old = (ItemPanel)a;
                  if (old.getItem() == item) {
                     return true;
                  }
               }

               Actor exp = new ItemPanel(item, true);
               a = com.tann.dice.Main.getCurrentScreen().getTopPushedActor();
               com.tann.dice.Main.getCurrentScreen().push(exp, false, true, true, 0.0F);
               Tann.center(exp);
               Sounds.playSound(Sounds.pip);
               if (!(a instanceof ExplanelReposition)) {
                  Actor var8 = com.tann.dice.Main.getCurrentScreen();
               }

               return true;
            } else {
               return false;
            }
         }
      });
      if (item != null) {
         Actor a = item.makeImageActor();
         this.addActor(a);
         Tann.center(a);
      }
   }

   public void draw(Batch batch, float parentAlpha) {
      DungeonScreen ds = DungeonScreen.get();
      if (this.item != null || ds == null || ds.partyManagementPanel.isDragging()) {
         boolean lookingToEquip = this.isLookingToEquip();
         boolean draggingMyItem = this.isDragging();
         boolean drawBorder = lookingToEquip;
         if (this.getParent() != null && this.getParent() instanceof InventoryPanel) {
            drawBorder = true;
         }

         if (draggingMyItem || this.hasAbilityItem()) {
            drawBorder = false;
         }

         boolean canEquipMaybe = true;
         if (ds != null) {
            Item i = ds.partyManagementPanel.getDraggingItem();
            canEquipMaybe = i == null || i.canEquip(this.hero);
         }

         if (drawBorder && (canEquipMaybe || !lookingToEquip)) {
            batch.setColor(lookingToEquip ? Colours.light : Colours.grey);
            Draw.drawScaled(batch, Images.itemBorder, (float)((int)this.getX()), (float)((int)this.getY()), 1.0F, 1.0F);
         }

         boolean drawNumber = this.item == null && (drawBorder || draggingMyItem) && this.slotIndex != -1;
         if (drawNumber) {
            if (!canEquipMaybe) {
               batch.setColor(Colours.red);
               TannFont.font.drawString(batch, "X", (int)(this.getX() + this.getWidth() / 2.0F), (int)(this.getY() + this.getHeight() / 2.0F), 1);
            } else {
               batch.setColor(Colours.withAlpha(Colours.text, 0.7F));
               TannFont.font
                  .drawString(batch, "" + (this.slotIndex + 1), (int)(this.getX() + this.getWidth() / 2.0F), (int)(this.getY() + this.getHeight() / 2.0F), 1);
            }
         }

         super.draw(batch, parentAlpha);
      }
   }

   private boolean hasAbilityItem() {
      return this.item != null && this.item.getAbility() != null;
   }

   private boolean isLookingToEquip() {
      DungeonScreen ds = DungeonScreen.get();
      return ds == null ? false : ds.partyManagementPanel.isDragging() && this.item == null;
   }

   private boolean isDragging() {
      DungeonScreen ds = DungeonScreen.get();
      return ds == null ? false : ds.partyManagementPanel.getDraggingItem() == this.item;
   }
}
