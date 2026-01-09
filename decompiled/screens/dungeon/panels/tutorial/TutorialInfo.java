package com.tann.dice.screens.dungeon.panels.tutorial;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.HpGrid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TutorialInfo extends TutorialItem {
   private final String text;
   private final Actor actor;

   public TutorialInfo(String text) {
      this(0, text);
   }

   public TutorialInfo(int priority, String text) {
      this(priority, text, null);
   }

   public TutorialInfo(int priority, String text, Actor actor) {
      super(priority);
      this.text = text;
      this.actor = actor;
   }

   @Override
   public void onSlideAway() {
      this.markCompleted();
   }

   @Override
   public void markCompleted() {
      this.complete = true;
   }

   @Override
   protected Actor getActor() {
      return this.actor;
   }

   public String toString() {
      return "Info: " + this.text;
   }

   @Override
   protected String getDisplayText() {
      return this.text == null ? null : "[notranslate][white][info][cu] " + com.tann.dice.Main.t(this.text);
   }

   protected static Collection<? extends TutorialInfo> makeRollingPhase() {
      List<TutorialInfo> result = new ArrayList<>();
      return result;
   }

   protected static Collection<? extends TutorialInfo> makeTargetingPhase() {
      return Arrays.asList(
         new TutorialInfo(2, null, HpGrid.makeTutorial(85, 2)),
         new TutorialInfo(3, "Monsters show their intentions and you get to respond. Incoming damage is shown in [yellow]yellow [hp]"),
         new TutorialInfo(5, "If there is a skull on the [text]End turn[cu] button, a hero is about to die") {
            @Override
            public boolean isValid(FightLog fightLog) {
               for (EntState es : fightLog.getSnapshot(FightLog.Temporality.Present).getStates(true, false)) {
                  if (fightLog.getSnapshot(FightLog.Temporality.Future).getState(es.getEnt()).isDead()) {
                     return true;
                  }
               }

               return false;
            }
         },
         new TutorialInfo(6, "Tutorial does not alter gameplay"),
         new TutorialInfo(15, "Enemies target randomly, but prefer targets who are not already dying") {
            @Override
            public boolean isValid(FightLog fightLog) {
               return fightLog.getContext().getCurrentLevelNumber() > 10;
            }
         }
      );
   }

   public static Collection<? extends TutorialItem> makeLevelEndPhase() {
      return Arrays.asList(new TutorialInfo("Defeated heroes return with half hp") {
         @Override
         public boolean isValid(FightLog fightLog) {
            for (Ent de : fightLog.getActiveEntities(true)) {
               if (((Hero)de).isDiedLastRound()) {
                  return true;
               }
            }

            return false;
         }
      }, new TutorialInfo("Heroes fully-heal between fights") {
         @Override
         public boolean isValid(FightLog fightLog) {
            for (Ent de : fightLog.getActiveEntities(true)) {
               if (((Hero)de).isDiedLastRound()) {
                  return false;
               }
            }

            return true;
         }
      }, new TutorialInfo("[grey]t0 items [text]are mostly-useless") {
         @Override
         public boolean isValid(FightLog fightLog) {
            List<Item> items = fightLog.getContext().getParty().getItems(null);

            for (int i = 0; i < items.size(); i++) {
               if (items.get(i).getTier() == 0) {
                  return true;
               }
            }

            return false;
         }
      }, new TutorialInfo("[grey]t3 items [text]are about 3x as good as [grey]t1 items") {
         @Override
         public boolean isValid(FightLog fightLog) {
            List<Item> items = fightLog.getContext().getParty().getItems(null);

            for (int i = 0; i < items.size(); i++) {
               if (items.get(i).getTier() == 3) {
                  return true;
               }
            }

            return false;
         }
      }, new TutorialInfo("There is an option to bypass achievements and unlock everything") {
         @Override
         public boolean isValid(FightLog fightLog) {
            return fightLog.getContext().getCurrentLevelNumber() > 10 && Tann.chance(0.1F);
         }
      });
   }
}
