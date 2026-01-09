package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.misc;

import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.ui.choice.ChoiceDialog;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.List;

public class PositionSwapPhase extends Phase {
   int swapA;
   int swapB;
   ChoiceDialog cd;

   public PositionSwapPhase(int numHeroes) {
      if (numHeroes >= 2) {
         this.swapA = (int)(Math.random() * numHeroes);
         this.swapB = (this.swapA + 1 + (int)(Math.random() * (numHeroes - 2))) % numHeroes;
         if (this.swapA == this.swapB) {
            TannLog.log("Position swap phase creation error with " + numHeroes + " heroes");
         }
      }
   }

   public PositionSwapPhase(int a, int b) {
      this.swapA = a;
      this.swapB = b;
   }

   public PositionSwapPhase(String s) {
      if (s.length() == 2) {
         try {
            this.swapA = Integer.parseInt(s.substring(0, 1));
            this.swapB = Integer.parseInt(s.substring(1, 2));
         } catch (NumberFormatException var3) {
            TannLog.log(var3.getMessage(), TannLog.Severity.error);
         }
      }
   }

   @Override
   public String serialise() {
      return "8" + this.swapA + "" + this.swapB;
   }

   @Override
   public void activate() {
      Sounds.playSound(Sounds.pip);
      List<Hero> heroList = this.getFightLog().getSnapshot(FightLog.Temporality.Present).getAliveHeroEntities();
      if (this.swapA >= heroList.size() || this.swapB >= heroList.size() || this.swapA == this.swapB) {
         this.stop();
      }

      Hero a = heroList.get(this.swapA);
      Hero b = heroList.get(this.swapB);
      this.cd = new ChoiceDialog(
         "Swap " + a.getName(true) + " with " + b.getName(true) + "?[n][n][purple](no side-effects)", ChoiceDialog.ChoiceNames.YesNo, new Runnable() {
            @Override
            public void run() {
               Sounds.playSound(Sounds.magic);
               DungeonScreen ds = DungeonScreen.get();
               DungeonContext dc = ds.getDungeonContext();
               dc.getParty().swapHeroes(dc, PositionSwapPhase.this.swapA, PositionSwapPhase.this.swapB);
               PositionSwapPhase.this.getFightLog().resetDueToFiddling();
               PositionSwapPhase.this.stop();
            }
         }, new Runnable() {
            @Override
            public void run() {
               PositionSwapPhase.this.stop();
            }
         }
      );
      DungeonScreen.get().addActor(this.cd);
      Tann.center(this.cd);
   }

   private void stop() {
      if (this.cd != null) {
         this.cd.remove();
      }

      PhaseManager.get().popPhase(PositionSwapPhase.class);
      DungeonScreen.get().save();
   }

   @Override
   public boolean showCornerInventory() {
      return true;
   }

   @Override
   public void deactivate() {
      if (this.cd != null) {
         this.cd.remove();
      }
   }

   @Override
   public StandardButton getLevelEndButtonInternal() {
      return new StandardButton(Images.phaseSwapIcon, Colours.pink, 53, 20);
   }

   @Override
   public boolean canSave() {
      return true;
   }
}
