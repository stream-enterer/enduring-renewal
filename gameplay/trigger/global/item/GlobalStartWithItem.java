package com.tann.dice.gameplay.trigger.global.item;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.levelEndPhase.LevelEndPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoicePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.reveal.RandomRevealPhase;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.ui.choice.ChoiceDialog;
import java.util.Arrays;
import java.util.List;

public class GlobalStartWithItem extends Global {
   public static long SWI_COLL = 0L;
   final Item[] items;
   final boolean blessing;

   public GlobalStartWithItem(Item... items) {
      this.items = items;
      if (items.length == 0) {
         throw new RuntimeException("invalid trigger");
      } else {
         this.blessing = items[0].getTier() >= 0;
      }
   }

   public static String nameFor(String name) {
      return "i." + name;
   }

   @Override
   public String describeForSelfBuff() {
      String colTag = this.blessing ? "[green]" : "[purple]";
      return colTag + "(permanent " + Words.plural("item", this.items.length) + ")[cu]";
   }

   @Override
   public void onPick(DungeonContext context) {
      context.getParty().addItems(Arrays.asList(this.items));
      onPickItem();
   }

   public static void onPickItem() {
      addLevelEndIfNotAlready();
   }

   public static void addLevelEndIfNotAlready() {
      PhaseManager pm = PhaseManager.get();
      boolean hasAlready = pm.has(LevelEndPhase.class);
      if (!hasAlready) {
         pm.pushPhaseAfter(new LevelEndPhase(true), ChoicePhase.class, RandomRevealPhase.class);
      }
   }

   @Override
   public List<Item> getStartingItems(DungeonContext dc) {
      return Arrays.asList(this.items);
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      boolean[] var2 = Tann.BOTH_R;
      int var3 = var2.length;

      for (int var4 = 0; var4 < var3; var4++) {
         Boolean b = var2[var4];
         Pixl p = new Pixl();

         for (int i = 0; i < this.items.length; i++) {
            if (i > 0) {
               p.gap(2);
            }

            Item e = this.items[i];
            if (big) {
               p.actor(e.makeChoosableActor(b, 0), (int)(com.tann.dice.Main.width * 0.8F));
            } else {
               p.actor(Tann.combineActors(e.makeImageActor(), new ImageActor(Images.itemBorder, Colours.grey)));
            }
         }

         Group g = p.pix();
         if (!ChoiceDialog.tooBig(g, 0.8F)) {
            return g;
         }
      }

      return (Actor)(big ? this.makePanelActor(false) : new Pixl().text("broekn").pix());
   }

   @Override
   public long getCollisionBits(Boolean player) {
      long bit = SWI_COLL;

      for (Item e : this.items) {
         bit |= e.getCollisionBits();
      }

      return bit;
   }

   @Override
   public boolean skipTest() {
      return true;
   }

   @Override
   public boolean isOnPick() {
      return true;
   }
}
