package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.misc;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.RandomTieredChoosable;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.entPanel.ItemHeroPanel;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.choice.ChoiceDialog;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ItemCombinePhase extends Phase {
   ChoiceDialog cd;
   final ItemCombinePhase.CombineType combineType;

   public ItemCombinePhase(ItemCombinePhase.CombineType ct) {
      this.combineType = ct;
   }

   public ItemCombinePhase() {
      this(Tann.random(ItemCombinePhase.CombineType.values()));
   }

   public ItemCombinePhase(String serial) {
      this(ItemCombinePhase.CombineType.valueOf(serial));
   }

   @Override
   public void activate() {
      Sounds.playSound(Sounds.pip);
      FightLog f = this.getFightLog();
      List<Item> allItem = f.getContext().getParty().getItems();
      final List<Item> toulouse = this.combineType.getItemsToLose(allItem);
      Pixl itemPix = new Pixl(2);
      int sw = (int)(com.tann.dice.Main.width * 0.45F);

      for (Item e : toulouse) {
         ItemHeroPanel ep = new ItemHeroPanel(e, null);
         itemPix.actor(ep, sw);
      }

      final Choosable c = this.combineType.getReward(toulouse);
      String descript = c.describe();
      Pixl mainPix = new Pixl(3).actor(itemPix.pix()).image(Images.arrowRight, Colours.light).actor(new TextWriter("[text]" + descript, sw, Colours.grey, 4));
      Group meta = mainPix.pix();
      this.cd = new ChoiceDialog(this.combineType.description, Arrays.asList(meta), ChoiceDialog.ChoiceNames.AcceptDecline, new Runnable() {
         @Override
         public void run() {
            Sounds.playSound(Sounds.boost);
            DungeonContext dc = ItemCombinePhase.this.getFightLog().getContext();
            Party p = dc.getParty();

            for (Item e : toulouse) {
               p.discardItem(e);
            }

            c.onChoose(dc, 0);
            ItemCombinePhase.this.endPhase();
         }
      }, new Runnable() {
         @Override
         public void run() {
            Sounds.playSound(Sounds.pop);
            ItemCombinePhase.this.endPhase();
         }
      });
      DungeonScreen.get().addActor(this.cd);
      Tann.center(this.cd);
   }

   private void endPhase() {
      PhaseManager.get().popPhase(ItemCombinePhase.class);
      DungeonScreen.get().save();
   }

   @Override
   public String serialise() {
      return "7" + this.combineType;
   }

   @Override
   public void deactivate() {
      this.cd.remove();
      com.tann.dice.Main.getCurrentScreen().pop(ChoiceDialog.class);
   }

   @Override
   public Color getLevelEndColour() {
      return Colours.grey;
   }

   @Override
   public StandardButton getLevelEndButtonInternal() {
      return new StandardButton("[grey]Anvil", Colours.grey, 53, 20);
   }

   @Override
   public boolean showCornerInventory() {
      return true;
   }

   @Override
   public boolean canSave() {
      return true;
   }

   public static enum CombineType {
      ZeroToThreeToSingle("[text]Trade your tier 0-3 items?"),
      SecondHighestToTierThrees("[text]Smash this item?");

      final String description;

      private CombineType(String description) {
         this.description = description;
      }

      public List<Item> getItemsToLose(List<Item> allItem) {
         switch (this) {
            case ZeroToThreeToSingle:
               List<Item> result = new ArrayList<>();

               for (Item e : allItem) {
                  if (e.getTier() >= 0 && e.getTier() <= 3) {
                     result.add(e);
                  }
               }

               return result;
            case SecondHighestToTierThrees:
               List<Item> result = new ArrayList<>();
               Collections.sort(allItem, new Comparator<Item>() {
                  public int compare(Item o1, Item o2) {
                     return o1.getTier() - o2.getTier();
                  }
               });
               if (allItem.size() > 1) {
                  Item i = allItem.get(allItem.size() - 2);
                  if (i.getTier() > 0) {
                     result.add(i);
                  }
               } else if (allItem.size() == 1) {
                  Item i = allItem.get(0);
                  if (i.getTier() > 0) {
                     result.add(i);
                  }
               }

               return result;
            default:
               throw new RuntimeException("Unspecified for " + this);
         }
      }

      public TP<Integer, Integer> getRewardTierAmt(List<Item> toulouse) {
         int totalValue = 0;

         for (Item e : toulouse) {
            totalValue += e.getTier();
         }

         switch (this) {
            case ZeroToThreeToSingle:
               totalValue += toulouse.size();
               int quality = (int)(totalValue * 0.5F);
               return new TP<>(quality, 1);
            case SecondHighestToTierThrees:
               int numTierThrees = (totalValue + 2) / 3;
               return new TP<>(3, numTierThrees);
            default:
               throw new RuntimeException("Unspecified for " + this);
         }
      }

      public Choosable getReward(List<Item> toulouse) {
         switch (this) {
            case ZeroToThreeToSingle:
            case SecondHighestToTierThrees:
               TP<Integer, Integer> tierAmt = this.getRewardTierAmt(toulouse);
               return new RandomTieredChoosable(tierAmt.a, tierAmt.b, ChoosableType.Item);
            default:
               return ModifierLib.getMissingno();
         }
      }
   }
}
