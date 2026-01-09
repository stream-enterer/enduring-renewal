package com.tann.dice.gameplay.phase.endPhase.runEnd;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.endPhase.RunEndPanel;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.List;

public class RunEndPhase extends Phase {
   RunEndPanel endPanel;
   final boolean victory;

   public RunEndPhase(boolean victory) {
      this.victory = victory;
   }

   public RunEndPhase(String data) {
      this.victory = Boolean.parseBoolean(data);
   }

   @Override
   public void activate() {
      boolean hasWon = com.tann.dice.Main.self().masterStats.hasWon();
      DungeonScreen ds = DungeonScreen.get();
      ds.slideButton(ds.rollGroup, false, false);
      ds.slideButton(ds.undoButton, false, false);
      ds.slideButton(ds.doneRollingButton, false, false);
      ds.slideButton(ds.confirmButton, false, false);
      ds.slideButton(ds.abilityHolder, false, false);
      ds.removeAllEffects();
      DungeonContext dungeonContext = this.getContext();
      boolean skipStats = dungeonContext.getContextConfig().skipStats();
      int previousFurthestReached = -1;
      if (!this.fromSave) {
         if (!skipStats) {
            previousFurthestReached = dungeonContext.getContextConfig().getFurthestReached();
         }

         if (this.victory) {
            dungeonContext.logVictory();
         } else {
            dungeonContext.logDefeat();
         }

         this.getFightLog().resetForNewFight();
         ds.save();
      } else {
         this.getFightLog().resetForNewFight();
      }

      List<Actor> leftInfo = new ArrayList<>();
      leftInfo.add(new TextWriter(dungeonContext.getContextConfig().getEndTitle()));
      if (!skipStats) {
         leftInfo.addAll(dungeonContext.getContextConfig().mode.getEndInfo(dungeonContext, previousFurthestReached, this.victory));
      }

      this.endPanel = new RunEndPanel(
         this.victory ? Images.victory : Images.defeat,
         this.victory ? Images.victoryTemplate : Images.defeatTemplate,
         this.victory ? Sounds.victory : Sounds.defeat,
         leftInfo,
         dungeonContext.getContextConfig().mode.getEndOptions(dungeonContext, this.victory),
         0.1F
      );
      DungeonScreen.get().addActor(this.endPanel);
      this.endPanel.slideIn(this.fromSave);
   }

   @Override
   public void deactivate() {
   }

   @Override
   public boolean canSave() {
      return true;
   }

   @Override
   public String serialise() {
      return "e" + this.victory;
   }
}
