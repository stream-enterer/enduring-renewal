package com.tann.dice.screens.dungeon.panels.book.page.cogPage.menuPanel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.mode.creative.pastey.PasteMode;
import com.tann.dice.gameplay.save.settings.option.Option;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.gameplay.trigger.global.linked.DipPanel;
import com.tann.dice.screens.Screen;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.DungeonUtils;
import com.tann.dice.screens.dungeon.panels.book.Book;
import com.tann.dice.screens.dungeon.panels.book.page.stuffPage.StuffPage;
import com.tann.dice.screens.titleScreen.TitleScreen;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.statics.sound.music.JukeboxUtils;
import com.tann.dice.util.BasicKeyCatch;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannStage;
import com.tann.dice.util.bsRandom.Supplier;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.online.BugReport;
import com.tann.dice.util.ui.choice.ChoiceDialog;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.List;

public abstract class EscMenuUtils {
   public static Actor makeScale(int cWidth) {
      Pixl visual = new Pixl();
      Actor displaySettings = com.tann.dice.Main.self().control.makeDisplaySettings();
      visual.actor(displaySettings).row();
      return visual.pix();
   }

   public static Actor makeSound(int cWidth) {
      Pixl sound = new Pixl(2, 0);

      for (Option bopt : OptionUtils.EscBopType.Top.getOptions()) {
         sound.actor(bopt.makeCogActor()).row();
      }

      sound.actor(JukeboxUtils.makeCurrentlyPlaying()).row().actor(JukeboxUtils.makeEntryButton()).actor(JukeboxUtils.makeSongControls());
      Actor a = sound.pix();
      return DipPanel.makeTopPanelGroup(new ImageActor(Images.esc_sound), a, JukeboxUtils.SOUND_COL);
   }

   public static Group makeButtons(int contentWidth) {
      List<Actor> miscButtons = new ArrayList<>();
      List<Actor> navButtons = new ArrayList<>();
      boolean inDungeon = com.tann.dice.Main.getCurrentScreen() instanceof DungeonScreen;
      if (inDungeon) {
         miscButtons.add(PasteMode.makeEscButton());
      }

      miscButtons.add(makeReportButton());
      if (!OptionLib.REMOVE_SAVE_BUTT.c() && inDungeon) {
         miscButtons.add(makeSaveButton());
      }

      String lap = com.tann.dice.Main.getSettings().getLastAlmanacPage();
      if (lap != null) {
         String tag = lap;
         String[] split = lap.split("-");
         if (split.length >= 2) {
            tag = split[1];
         }

         if (!tag.contains("Options") && !tag.contains("Basics") && !tag.contains("Credits")) {
            if (tag.equals("unlock")) {
               tag = com.tann.dice.Main.t("[green]Unlock");
            }

            navButtons.add(makeBookButton("[notranslate]<- " + com.tann.dice.Main.t(tag), lap));
         }
      }

      navButtons.add(makeBookButton("Options", "stuff-options"));
      navButtons.add(makeBookButton("[green]Help", "help-basics"));
      navButtons.add(makeBookButton(StuffPage.StuffSection.Credits.getColourTaggedString(), "stuff-credits"));
      DungeonScreen ds = DungeonScreen.getCurrentScreenIfDungeon(false);
      if (ds != null && ds.canFleePhase()) {
         miscButtons.add(makeFlee());
      }

      if (Gdx.app.getType() != ApplicationType.iOS || ds != null) {
         miscButtons.add(makeQuit());
      }

      int gap = 3;
      Pixl p = new Pixl(gap);
      p.actor(almanacButtonGroup(navButtons, gap, contentWidth)).row().listActor(miscButtons, gap, contentWidth);
      return p.pix();
   }

   private static Actor almanacButtonGroup(List<Actor> actors, int gap, int contentWidth) {
      Actor almanac = new ImageActor(Images.almanac);
      almanac.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            Book.openBook("ledger-hero");
            return true;
         }
      });
      return DipPanel.makeTopPanelGroup(almanac, new Pixl(3).listActor(actors, gap, contentWidth).pix(), Book.BOOK_COL);
   }

   private static Actor makeBookButton(String options, final String path) {
      StandardButton sb = new StandardButton(options);
      sb.setRunnable(new Runnable() {
         @Override
         public void run() {
            Book.openBook(path);
         }
      });
      return sb;
   }

   private static Actor makeFlee() {
      StandardButton flee = new StandardButton("[grey]Flee");
      flee.setRunnable(new Runnable() {
         @Override
         public void run() {
            final DungeonScreen ds = DungeonScreen.getCurrentScreenIfDungeon(false);
            if (ds == null) {
               Sounds.playSound(Sounds.error);
            } else {
               String text = com.tann.dice.Main.t("Flee?\n[red](counts as a loss)").replaceAll("\n", "[n]");
               ChoiceDialog choiceDialog = new ChoiceDialog("[notranslateall]" + text, ChoiceDialog.ChoiceNames.RedYes, new Runnable() {
                  @Override
                  public void run() {
                     ds.manualFlee();
                  }
               }, null);
               Sounds.playSound(Sounds.pip);
               com.tann.dice.Main.getCurrentScreen().push(choiceDialog, 0.8F);
               Tann.center(choiceDialog);
            }
         }
      });
      return flee;
   }

   private static StandardButton makeSaveButton() {
      String text = "Save";
      if (com.tann.dice.Main.self().translator.shouldTranslate()) {
         text = com.tann.dice.Main.t("Save (% game %)");
      }

      StandardButton save = new StandardButton("[notranslate]" + text);
      save.setRunnable(new Runnable() {
         @Override
         public void run() {
            Sounds.playSound(Sounds.pip);
            Pixl p = new Pixl(3, 3).border(Colours.red);
            p.text("[text]Game saves automatically after each action, this button does nothing!", 110);
            p.row();
            p.actor(OptionLib.REMOVE_SAVE_BUTT.makeCogActor());
            Actor a = p.pix();
            com.tann.dice.Main.getCurrentScreen().push(a, true, true, true, 0.8F);
            Tann.center(a);
         }
      });
      return save;
   }

   private static StandardButton makeReportButton() {
      return BugReport.makeBugReportButton(
         "[green]Welcome to the bug report section!",
         "[text]Automated bug report includes your highscore-name, game version, recent logs and the current game state.",
         new Supplier<String>() {
            public String supply() {
               String reportString = com.tann.dice.Main.getCurrentScreen().getReportString();
               if (reportString == null) {
                  reportString = "was null";
               }

               return reportString;
            }
         },
         true
      );
   }

   private static StandardButton makeQuit() {
      boolean exit = com.tann.dice.Main.getCurrentScreen() instanceof TitleScreen;
      String buttString = exit ? "[b][red]Exit" : "[b][orange]Quit";
      StandardButton butt = new StandardButton(buttString);
      butt.setRunnable(new Runnable() {
         @Override
         public void run() {
            Screen s = com.tann.dice.Main.getCurrentScreen();
            if (s instanceof DungeonScreen) {
               Sounds.playSound(Sounds.confirm);
               DungeonScreen.get().getDungeonContext().getContextConfig().quitAction();
            } else if (s instanceof TitleScreen) {
               Actor a = new ChoiceDialog("Close Slice & Dice?", ChoiceDialog.ChoiceNames.YesCancel, new Runnable() {
                  @Override
                  public void run() {
                     if (com.tann.dice.Main.self().control.allowQuit()) {
                        Gdx.app.exit();
                     }
                  }
               }, new Runnable() {
                  @Override
                  public void run() {
                     com.tann.dice.Main.getCurrentScreen().popSingleMedium();
                  }
               });
               com.tann.dice.Main.getCurrentScreen().pushAndCenter(a);
            } else {
               com.tann.dice.Main.self().setScreen(new TitleScreen());
            }
         }
      });
      return butt;
   }

   private static StandardButton makeRestore() {
      String buttString = "Restore Purchases";
      StandardButton butt = new StandardButton(buttString);
      butt.setRunnable(new Runnable() {
         @Override
         public void run() {
            com.tann.dice.Main.self().control.checkPurchase();
         }
      });
      return butt;
   }

   public static boolean refreshIfOnTop() {
      Screen s = com.tann.dice.Main.getCurrentScreen();
      Actor a = s.getTopPushedActor();
      if (a == null) {
         return false;
      } else if (!(a instanceof Group)) {
         return false;
      } else {
         Group g = (Group)a;
         if (Tann.findByClass(g, DungeonUtils.CogTag.class) == null) {
            return false;
         } else {
            s.popSingleMedium();
            DungeonUtils.showCogMenu();
            return true;
         }
      }
   }

   public static Actor makeFullEscMenu() {
      int cWidth = (int)(com.tann.dice.Main.width * 0.9F);
      List<Actor> actors = new ArrayList<>();
      actors.add(makeScale(cWidth));
      actors.add(makeSound(cWidth));
      int bWidth = cWidth;
      if (com.tann.dice.Main.isPortrait()) {
         bWidth = (int)(TannStage.maxActorWidth(actors) * 1.2F);
      }

      Group a = new Pixl(4, 5).border(Colours.grey).listActor(actors, 4, cWidth).row().actor(makeButtons(bWidth)).pix();
      a.setTransform(false);
      a.addActor(new DungeonUtils.CogTag());
      Group var4 = new BasicKeyCatch(a);
      var4.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            return true;
         }
      });
      return var4;
   }
}
