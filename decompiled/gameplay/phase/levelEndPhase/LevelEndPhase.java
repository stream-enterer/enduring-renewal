package com.tann.dice.gameplay.phase.levelEndPhase;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.tutorial.TutorialHolder;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class LevelEndPhase extends Phase {
   public static final int MINIMAP_SLIDE_DIST = 10;
   public LevelEndPanel levelEndPanel;
   MiniMap miniMap;
   List<Phase> phases = new ArrayList<>();
   final boolean standalone;
   private String oldNameUnequipReminder;

   public LevelEndPhase() {
      this(false);
   }

   public LevelEndPhase(boolean standalone) {
      this.standalone = standalone;
   }

   public LevelEndPhase(String saved) {
      this.standalone = false;
      LevelEndData ssd = (LevelEndData)com.tann.dice.Main.getJson().fromJson(LevelEndData.class, saved);
      this.phases = ssd.makePhases();
   }

   public static void unequipHero(Party party, Hero hero, String oldHeroName) {
      boolean unequipped = party.unequip(hero);
      if (unequipped) {
         Phase p = PhaseManager.get().find(LevelEndPhase.class);
         if (p instanceof LevelEndPhase) {
            ((LevelEndPhase)p).showUnequippedReminder(oldHeroName);
         }

         if (com.tann.dice.Main.getCurrentScreen() instanceof DungeonScreen) {
            DungeonScreen.get().getDungeonContext().setCheckedItems(false);
         }
      }
   }

   private void showUnequippedReminder(String oldHeroName) {
      if (this.levelEndPanel != null) {
         this.levelEndPanel.addUnequippedReminder(oldHeroName);
      } else {
         this.oldNameUnequipReminder = oldHeroName;
      }
   }

   public void cancelUnequipReminder() {
      this.oldNameUnequipReminder = null;
   }

   @Override
   public String serialise() {
      return "2" + com.tann.dice.Main.getJson(true).toJson(new LevelEndData(this.phases));
   }

   @Override
   public void activate() {
      FightLog f = this.getFightLog();
      DungeonScreen ds = DungeonScreen.get();
      DungeonContext dc = ds.getDungeonContext();
      this.miniMap = new MiniMap(f.getContext());
      ds.slideButton(ds.rollGroup, false, false);
      ds.slideButton(ds.undoButton, false, false);
      ds.slideButton(ds.confirmButton, false, false);
      ds.slideButton(ds.doneRollingButton, false, false);
      ds.slideButton(ds.abilityHolder, false, false);
      ds.removeAllEffects();
      if (f.getContext().getContextConfig().mode.showMinimap()) {
         this.miniMap = new MiniMap(f.getContext());
         this.miniMap.setPosition((int)(com.tann.dice.Main.width / 2 - this.miniMap.getWidth() / 2.0F), 0.0F);
         DungeonScreen.get().addActor(this.miniMap);
         this.miniMap.toBack();
      }

      if (!this.fromSave && !this.standalone) {
         dc.addPhasesFromCurrentLevel(this.phases);
         dc.specialCachedAchievementCheck();
         ds.startLevel(false);
         ds.enemy.slideAway();
         ds.progressBackground();
      }

      this.levelEndPanel = new LevelEndPanel(this.phases, f, this.fromSave);
      DungeonScreen.get().addActor(this.levelEndPanel);
      this.levelEndPanel.setZIndex(this.levelEndPanel.getZIndex() - 3);
      this.slideIn();
   }

   private void slideIn() {
      this.levelEndPanel.setTouchable(Touchable.enabled);
      this.levelEndPanel.layout();
      Tann.slideIn(this.miniMap, Tann.TannPosition.Bot, 10, 0.3F);
      Tann.slideIn(this.levelEndPanel, Tann.TannPosition.Top, this.getMainPanelSlideDistance(), 0.3F);
      if (this.oldNameUnequipReminder != null) {
         this.levelEndPanel.addUnequippedReminder(this.oldNameUnequipReminder);
      }
   }

   private int getMainPanelSlideDistance() {
      return com.tann.dice.Main.isPortrait()
         ? (int)(com.tann.dice.Main.height * 0.33333334F - this.levelEndPanel.getHeight() / 2.0F)
         : 25 + com.tann.dice.Main.self().notch(0) * 2;
   }

   @Override
   public void reactivate() {
      this.slideIn();
   }

   @Override
   public void hide() {
      super.hide();
      this.levelEndPanel.setTouchable(Touchable.disabled);
      Tann.slideAway(this.levelEndPanel, Tann.TannPosition.Top, 30, false);
      Tann.slideAway(this.miniMap, Tann.TannPosition.Bot, 10, false);
   }

   @Override
   public void deactivate() {
      this.hide();
   }

   @Override
   public void positionTutorial(TutorialHolder tutorialHolder) {
      tutorialHolder.setX((int)(com.tann.dice.Main.width / 2 - tutorialHolder.getWidth() / 2.0F));
      if (com.tann.dice.Main.isPortrait()) {
         tutorialHolder.setY((int)(com.tann.dice.Main.height / 3.0F));
      } else {
         tutorialHolder.setY(
            (int)((com.tann.dice.Main.height - this.levelEndPanel.getHeight() - this.getMainPanelSlideDistance()) / 2.0F - tutorialHolder.getHeight() / 2.0F)
         );
      }

      super.positionTutorial(tutorialHolder);
   }

   @Override
   public boolean canEquip() {
      return true;
   }

   public void addPhase(Phase phase) {
      this.phases.add(phase);
   }

   @Override
   public boolean canSave() {
      return true;
   }

   public List<Phase> getNestedPhases() {
      return this.phases;
   }

   @Override
   public boolean canFlee() {
      return true;
   }

   @Override
   public boolean keyPress(int keycode) {
      switch (keycode) {
         case 37:
            this.levelEndPanel.inventoryClick();
            return true;
         case 66:
         case 160:
            this.levelEndPanel.continueClick();
            return true;
         default:
            int index = Tann.getDigit(keycode);
            if (index >= 0 && index < this.phases.size()) {
               Phase chosen = this.phases.get(index);
               this.levelEndPanel.clickPhaseStart(chosen);
               return true;
            } else {
               return super.keyPress(keycode);
            }
      }
   }
}
