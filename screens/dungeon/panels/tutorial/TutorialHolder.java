package com.tann.dice.screens.dungeon.panels.tutorial;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.effect.targetable.ability.ui.AbilityHolder;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Chrono;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.List;

public class TutorialHolder extends Group {
   public static final int WIDTH = 85;
   public static final int ITEM_GAP = 1;
   public static final int GAP_RIGHT = 2;
   public static final int GAP_TOP = 2;
   public static final int MAX_ITEMS = 2;
   List<TutorialItem> items;
   final Color bg = Colours.shiftedTowards(Colours.dark, Colours.green, 0.13F).cpy();

   public TutorialHolder() {
      this.setTransform(false);
   }

   public static int getTopGap() {
      return 2 + (com.tann.dice.Main.isPortrait() ? com.tann.dice.Main.self().notch(0) : 0);
   }

   public void setItems(List<TutorialItem> items) {
      this.items = new ArrayList<>(items);
      this.layout();
   }

   private void layout() {
      this.clearChildren();
      this.setVisible(!this.items.isEmpty());
      if (this.items.size() != 0) {
         Pixl p = new Pixl(1, 2);
         p.text("[green]Tutorial").row(3);
         Pixl itemsPix = new Pixl(1);
         itemsPix.rowedActors(this.items);
         p.actor(itemsPix.pix(8));
         Actor a = p.pix();
         Tann.become(this, a);
         this.addNavButtons();
      }
   }

   private void addNavButtons() {
      ImageActor closeButton = new ImageActor(Images.tut_close);
      this.addActor(closeButton);
      closeButton.setPosition((int)(this.getWidth() - closeButton.getWidth()), (int)(this.getHeight() - closeButton.getHeight()));
      closeButton.addListener(new TannListener() {
         @Override
         public boolean info(int button, float x, float y) {
            int maxw = 150;
            Pixl p = new Pixl(4, 4).border(Colours.grey);
            p.text("[text][green]Tutorial[cu] shows [blue]tips[cu] and suggests things you have [purple]never done[cu]", 150).row();
            StandardButton tb = new StandardButton("[red]Skip all");
            tb.setRunnable(new Runnable() {
               @Override
               public void run() {
                  com.tann.dice.Main.getSettings().skipTutorial();
                  DungeonScreen.get().getTutorialManager().reset();
                  TutorialHolder.this.slideAway();
                  com.tann.dice.Main.getCurrentScreen().popAllMedium();
                  int amtSkipped = 2;
                  AbilityHolder.showInfo("[yellow]Tutorial Skipped", Colours.yellow);
               }
            });
            p.actor(tb);
            tb = new StandardButton("Dismiss");
            tb.setRunnable(new Runnable() {
               @Override
               public void run() {
                  int amtSkipped = 0;

                  for (TutorialItem item : TutorialHolder.this.items) {
                     if (!item.isComplete()) {
                        amtSkipped++;
                        item.markCompleted();
                     }
                  }

                  TutorialHolder.this.slideAway();
                  com.tann.dice.Main.getCurrentScreen().popAllMedium();
                  amtSkipped = Math.max(1, amtSkipped);
                  AbilityHolder.showInfo(amtSkipped + " dismissed", Colours.yellow);
               }
            });
            p.actor(tb);
            Actor a = p.pix();
            com.tann.dice.Main.getCurrentScreen().push(a, 0.7F);
            Tann.center(a);
            return true;
         }
      });
   }

   public void draw(Batch batch, float parentAlpha) {
      Draw.fillActor(batch, this, this.bg, Colours.grey, 1, true);
      super.draw(batch, parentAlpha);
   }

   public boolean allComplete() {
      if (this.items == null) {
         return false;
      } else {
         for (TutorialItem ti : this.items) {
            if (!ti.isComplete()) {
               return false;
            }
         }

         return true;
      }
   }

   public void slideAway(float delay) {
      this.addAction(Actions.delay(delay, Actions.moveTo(this.getX(), com.tann.dice.Main.height, 0.3F, Chrono.i)));
      if (this.items != null) {
         for (TutorialItem ti : this.items) {
            ti.onSlideAway();
         }
      }
   }

   public void slideAway() {
      this.slideAway(0.0F);
   }
}
