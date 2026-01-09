package com.tann.dice.screens.dungeon.panels.tutorial;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.gameplay.PlayerRollingPhase;
import com.tann.dice.gameplay.phase.gameplay.TargetingPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.LevelEndPhase;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.Flasher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TutorialItem extends Group {
   protected boolean complete;
   final int priority;

   public TutorialItem() {
      this(0);
   }

   public TutorialItem(int priority) {
      this.priority = priority;
      this.setTransform(false);
   }

   void layout() {
      this.clearChildren();
      Pixl p = new Pixl(1).forceWidth(83);
      String text = this.getDisplayText();
      if (text != null) {
         p.text(this.getDisplayText());
      }

      Actor a = this.getActor();
      if (a != null) {
         p.actor(a);
      }

      Tann.become(this, p.pix(8));
   }

   protected Actor getActor() {
      return null;
   }

   protected abstract String getDisplayText();

   public void draw(Batch batch, float parentAlpha) {
      super.draw(batch, parentAlpha);
   }

   public void markCompleted() {
      this.complete = true;
      this.layout();
      this.addActor(new Flasher(this, Colours.light));
   }

   public void newSnapshot(Snapshot snapshot) {
   }

   public void onRoll(List<Ent> heroes) {
   }

   public void newStatsSnapshot(StatSnapshot ss) {
   }

   public boolean isComplete() {
      return this.complete;
   }

   public boolean isValid(FightLog fightLog) {
      return true;
   }

   public void onLock(List<Ent> heroes) {
   }

   public void onAction(TutorialManager.TutorialAction type, Object arg) {
   }

   public static Map<Class<? extends Phase>, List<TutorialItem>> makeAll() {
      Comparator<TutorialItem> comp = new Comparator<TutorialItem>() {
         public int compare(TutorialItem o1, TutorialItem o2) {
            return o1.getPriority() - o2.getPriority();
         }
      };
      Map<Class<? extends Phase>, List<TutorialItem>> tutorialMap = new HashMap<>();
      List<TutorialItem> roll = new ArrayList<>();
      roll.addAll(TutorialInfo.makeRollingPhase());
      roll.addAll(TutorialQuest.makeRollingPhase());
      Collections.sort(roll, comp);
      tutorialMap.put(PlayerRollingPhase.class, roll);
      roll = new ArrayList<>();
      roll.addAll(TutorialInfo.makeTargetingPhase());
      roll.addAll(TutorialQuest.makeTargetingPhase());
      Collections.sort(roll, comp);
      tutorialMap.put(TargetingPhase.class, roll);
      roll = new ArrayList<>();
      roll.addAll(TutorialInfo.makeLevelEndPhase());
      roll.addAll(TutorialQuest.makeLevelEndPhase());
      roll.addAll(com.tann.dice.Main.self().control.makeTutorialLevelEnd());
      Collections.sort(roll, comp);
      tutorialMap.put(LevelEndPhase.class, roll);
      return tutorialMap;
   }

   private int getPriority() {
      return this.priority;
   }

   public boolean isComplex() {
      this.layout();
      return this.getHeight() > 17.0F;
   }

   public void onSlideAway() {
   }

   public String getSortText() {
      if (this.getDisplayText() != null) {
         return this.getDisplayText();
      } else if (this.getActor().getName() == null) {
         throw new RuntimeException("bad tutorial");
      } else {
         return this.getActor().getName();
      }
   }

   public void loadIn() {
   }
}
