package com.tann.dice.gameplay.progress;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.screens.titleScreen.TitleScreen;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.choice.ChoiceDialog;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.Arrays;

public class MasterStatsUtils {
   public static Actor makeResetAchievementsButton() {
      StandardButton cheat = new StandardButton("[red]Reset Achievements");
      cheat.addListener(
         new TannListener() {
            @Override
            public boolean action(int button, int pointer, float x, float y) {
               Sounds.playSound(Sounds.pip);
               ChoiceDialog choiceDialog = new ChoiceDialog(
                  null,
                  Arrays.asList(new TextWriter("[red]Reset all achievements?[n][nh][red]This is permanent", 120)),
                  ChoiceDialog.ChoiceNames.RedYes,
                  new Runnable() {
                     @Override
                     public void run() {
                        com.tann.dice.Main.getCurrentScreen().popSingleMedium();
                        com.tann.dice.Main.unlockManager().resetAchievements();
                        com.tann.dice.Main.self().setScreen(new TitleScreen());
                        com.tann.dice.Main.getCurrentScreen().showDialog("[red]Achievements reset");
                     }
                  },
                  new Runnable() {
                     @Override
                     public void run() {
                        com.tann.dice.Main.getCurrentScreen().popSingleMedium();
                     }
                  }
               );
               com.tann.dice.Main.getCurrentScreen().push(choiceDialog, 0.8F);
               Tann.center(choiceDialog);
               return true;
            }
         }
      );
      return cheat;
   }

   public static StandardButton makeCopyAchievementsButton() {
      StandardButton copy = new StandardButton("[text]Copy achievements");
      copy.setRunnable(
         new Runnable() {
            @Override
            public void run() {
               Sounds.playSound(Sounds.pip);
               String text = com.tann.dice.Main.t(
                  "[light]Copy achievements to clipboard?\n[text]It's intended for transferring your progress between devices but you can do what you want :)"
               );
               text = "[notranslateall]" + text.replaceAll("\n", "[n][nh]");
               ChoiceDialog choiceDialog = new ChoiceDialog(
                  null, Arrays.asList(new TextWriter(text, 120)), ChoiceDialog.ChoiceNames.YesCancel, new Runnable() {
                     @Override
                     public void run() {
                        com.tann.dice.Main.getCurrentScreen().popSingleMedium();
                        com.tann.dice.Main.unlockManager().saveAchievementsToClipboard();
                     }
                  }, new Runnable() {
                     @Override
                     public void run() {
                        com.tann.dice.Main.getCurrentScreen().popSingleMedium();
                     }
                  }
               );
               com.tann.dice.Main.getCurrentScreen().push(choiceDialog, 0.8F);
               Tann.center(choiceDialog);
            }
         }
      );
      return copy;
   }

   public static StandardButton makeLoadProgressButton() {
      StandardButton load = new StandardButton("[text]Load achievements");
      load.setRunnable(
         new Runnable() {
            @Override
            public void run() {
               Sounds.playSound(Sounds.pip);
               String text = com.tann.dice.Main.t(
                  "[light]Load achievements from clipboard?\n[text]This will merge the achievements from your clipboard with your current ones"
               );
               text = "[notranslateall]" + text.replaceAll("\n", "[n][nh]");
               ChoiceDialog choiceDialog = new ChoiceDialog(
                  null, Arrays.asList(new TextWriter(text, 120)), ChoiceDialog.ChoiceNames.YesCancel, new Runnable() {
                     @Override
                     public void run() {
                        com.tann.dice.Main.getCurrentScreen().popSingleMedium();
                        com.tann.dice.Main.self().masterStats.loadAchievementFromClipboard();
                     }
                  }, new Runnable() {
                     @Override
                     public void run() {
                        com.tann.dice.Main.getCurrentScreen().popSingleMedium();
                     }
                  }
               );
               com.tann.dice.Main.getCurrentScreen().push(choiceDialog, 0.8F);
               Tann.center(choiceDialog);
            }
         }
      );
      return load;
   }
}
