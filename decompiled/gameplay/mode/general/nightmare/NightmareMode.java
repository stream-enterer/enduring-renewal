package com.tann.dice.gameplay.mode.general.nightmare;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.gameplay.save.RunHistory;
import com.tann.dice.gameplay.save.SaveState;
import com.tann.dice.screens.titleScreen.GameStart;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.ScrollPane;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NightmareMode extends Mode {
   public static int EXTRA_LEVELS = 10;

   public NightmareMode() {
      super("Nightmare");
   }

   @Override
   public Color getColour() {
      return Colours.blue;
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{
         "Use a [orange]victorious party[cu] from a previous run",
         "Take on fights [yellow]21-" + (20 + EXTRA_LEVELS) + "[cu]",
         "[purple]No restarts,[cu] [red]very unfair[cu]",
         "Monsters are lazy scale-ups"
      };
   }

   @Override
   public Actor makeStartGameCard(List<ContextConfig> all) {
      Pixl p = new Pixl();
      StandardButton sb = new StandardButton("Choose Party");
      sb.setRunnable(new Runnable() {
         @Override
         public void run() {
            NightmareMode.this.showSelectionDialog();
         }
      });
      p.actor(sb);
      Actor loadButton = SaveState.getLoadButton(this.getConfigs().get(0).getGeneralSaveKey());
      if (loadButton != null) {
         p.row(5).actor(loadButton);
      }

      return p.pix();
   }

   private Actor makeSelectionDialog() {
      int WIDTH = (int)Math.min(160.0F, com.tann.dice.Main.width * 0.8F);
      Pixl p = new Pixl(3, 3).border(Colours.orange);
      List<RunHistory> eligibles = this.getEligibleRunHistories();
      List<String> modes = new ArrayList<>();

      for (Mode m : getEligibleModes()) {
         modes.add("[notranslate]" + com.tann.dice.Main.t(m.getTextButtonName()));
      }

      if (eligibles.isEmpty()) {
         String text = com.tann.dice.Main.t(
               "[purple]No usable runs found.\n[text]To be usable, it must be:\n-[green]victorious[cu]\n-from an [yellow]eligible mode[cu]\n-from within the [grey]past week[cu]\n-not already used in "
                  + TextWriter.getTag(this.getColour())
                  + "nightmare[cu]"
            )
            .replaceAll("\n", "[n]");
         p.text("[notranslateall]" + text, WIDTH).row();
      }

      if (!eligibles.isEmpty()) {
         for (final RunHistory rh : eligibles) {
            Actor a = rh.makeActor();
            p.actor(a).row();
            a.addListener(new TannListener() {
               @Override
               public boolean action(int button, int pointer, float x, float y) {
                  NightmareMode.this.runHistoryClicked(rh);
                  return true;
               }
            });
         }
      }

      p.row(7).text("[notranslate][text]" + com.tann.dice.Main.t("Eligible modes:") + " " + Tann.commaList(modes), WIDTH);
      Actor contained = p.pix(8);
      ScrollPane sp = Tann.makeScrollpane(contained);
      sp.setSize(contained.getWidth() + 6.0F, Math.min(com.tann.dice.Main.height * 0.7F, contained.getHeight()));
      return sp;
   }

   private void runHistoryClicked(RunHistory rh) {
      GameStart.start(new NightmareConfig(rh).makeContext());
      rh.markNightmare();
      com.tann.dice.Main.self().masterStats.getRunHistoryStore().saveEdit();
   }

   private List<RunHistory> getEligibleRunHistories() {
      int sufficient = 10;
      List<RunHistory> result = new ArrayList<>();
      List<String> eligibleModeNames = new ArrayList<>();

      for (Mode m : getEligibleModes()) {
         eligibleModeNames.add(m.getSaveKey());
      }

      List<RunHistory> runHistoryList = com.tann.dice.Main.self().masterStats.getRunHistoryStore().getRunHistoryList();

      for (int i = runHistoryList.size() - 1; i >= 0; i--) {
         RunHistory runHistory = runHistoryList.get(i);
         if (runHistory.isInDateForNightmare()
            && !runHistory.nightmareConsumed()
            && runHistory.isVictory()
            && eligibleModeNames.contains(runHistory.getModeName())) {
            result.add(runHistory);
            if (result.size() >= 10) {
               break;
            }
         }
      }

      return result;
   }

   private void showSelectionDialog() {
      Actor a = this.makeSelectionDialog();
      com.tann.dice.Main.getCurrentScreen().push(a, 0.8F);
      Tann.center(a);
   }

   public static List<Mode> getEligibleModes() {
      return Arrays.asList(Mode.CLASSIC, Mode.CHOOSE_PARTY, Mode.SHORTCUT, Mode.LOOT, Mode.RAID, Mode.GENERATE_HEROES, Mode.ALTERNATE_HEROES, Mode.DREAM);
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return Arrays.asList(new NightmareConfig());
   }

   @Override
   public String getSaveKey() {
      return "asc";
   }

   @Override
   public boolean skipShowBoss() {
      return true;
   }

   @Override
   public FolderType getFolderType() {
      return FolderType.cool;
   }
}
