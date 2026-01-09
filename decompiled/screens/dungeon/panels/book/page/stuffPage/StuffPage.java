package com.tann.dice.screens.dungeon.panels.book.page.stuffPage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.leaderboard.Leaderboard;
import com.tann.dice.gameplay.leaderboard.LeaderboardBlob;
import com.tann.dice.gameplay.leaderboard.LeaderboardDisplay;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.progress.RunHistoryStore;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.save.RunHistory;
import com.tann.dice.screens.dungeon.panels.book.TopTab;
import com.tann.dice.screens.dungeon.panels.book.page.BookPage;
import com.tann.dice.screens.dungeon.panels.book.page.cogPage.menuPanel.CreditsPanel;
import com.tann.dice.screens.dungeon.panels.book.page.cogPage.menuPanel.OptionsMenu;
import com.tann.dice.screens.dungeon.panels.book.page.ledgerPage.PatchBlob;
import com.tann.dice.screens.graph.GraphUpdate;
import com.tann.dice.screens.graph.GraphUtils;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.statics.sound.music.JukeboxUtils;
import com.tann.dice.util.Colours;
import com.tann.dice.util.DebugUtilsUseful;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.VersionUtils;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.choice.ChoiceDialog;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StuffPage extends BookPage {
   private static final String MODAL_NAME = "leaderboard_modal";
   static final int CONTENT_GAP = 4;

   public StuffPage(Map<String, Stat> allMergedStats, int width, int height) {
      super("[grey]stuff", allMergedStats, width, height);
   }

   @Override
   public Color getColour() {
      return Colours.grey;
   }

   @Override
   protected List<TopTab> getAllListItems() {
      List<TopTab> result = new ArrayList<>();

      for (StuffPage.StuffSection sp : StuffPage.StuffSection.values()) {
         if (!UnUtil.isLocked(sp)) {
            result.add(new TopTab(sp, sp.getColourTaggedString(), getSideWidth()));
         }
      }

      return result;
   }

   @Override
   protected Actor getContentActorFromSidebar(Object type, int contentWidth) {
      switch ((StuffPage.StuffSection)type) {
         case Options:
            return new OptionsMenu(contentWidth);
         case Credits:
            return CreditsPanel.make(contentWidth);
         case Patch:
            return makePatchGroup(contentWidth);
         case Graph:
            Actor a = GraphUtils.makeWidth(contentWidth, new GraphUpdate() {
               @Override
               public void newGraph(Actor newGraphs) {
                  StuffPage.this.showThing(newGraphs, true);
               }
            });
            a.setName("bot");
            return a;
         case Jukebox:
            return JukeboxUtils.makeJukebox(contentWidth);
         case Online:
            return this.makeLeaderboard(null, contentWidth);
         case Numbers:
            return this.makeNumbersPage(contentWidth);
         default:
            return new TextWriter("//TODO");
      }
   }

   private Actor makeGalleryPage(int type, final int contentWidth) {
      Pixl p = new Pixl();

      for (int i = 0; i < 3; i++) {
         String typeName = "??";
         switch (i) {
            case 0:
               typeName = "2d";
               break;
            case 1:
               typeName = "3d";
               break;
            case 2:
               typeName = "2d-big";
         }

         StandardButton sb = new StandardButton(typeName).makeTiny();
         final int finalI = i;
         sb.setRunnable(new Runnable() {
            @Override
            public void run() {
               StuffPage.this.showThing(StuffPage.this.makeGalleryPage(finalI, contentWidth));
            }
         });
         p.actor(sb);
         if (i < 2) {
            p.gap(2);
         }
      }

      p.row(3).text("[text]Good art by a3um, bad art by tann");
      p.row(3).actor(DebugUtilsUseful.showImages(type, contentWidth));
      return p.pix();
   }

   public static Group makePatchGroup(int contentWidth) {
      int MAX_WIDTH = contentWidth - 4;
      Pixl p = new Pixl(4).forceWidth(MAX_WIDTH);
      p.text("Current version: [yellow]" + VersionUtils.versionName).row();

      for (PatchBlob pb : PatchBlob.getRawData()) {
         p.actor(pb.makeActor(MAX_WIDTH)).row();
      }

      return p.pix();
   }

   private Group makeNumbersPage(int contentWidth) {
      Pixl p = new Pixl(10, 0);
      List<Actor> sections = new ArrayList<>();

      for (int side = 0; side < 2; side++) {
         Pixl left = new Pixl(1);
         Pixl right = new Pixl(1);
         List<Stat> stats = new ArrayList<>();

         for (Stat s : this.allMergedStats.values()) {
            if (s.showInAlmanac(side)) {
               stats.add(s);
            }
         }

         Collections.sort(stats, new Comparator<Stat>() {
            public int compare(Stat o1, Stat o2) {
               return o1.getOrder() - o2.getOrder();
            }
         });

         for (Stat sx : stats) {
            left.text(sx.getNameForDisplay()).row();
            right.text(sx.getValueForDisplay()).row();
         }

         Group l = left.pix(8);
         Group r = right.pix(8);
         Actor a = new Pixl(3).actor(l).gap(10).actor(r).pix();
         sections.add(a);
      }

      p.listActor(contentWidth, sections);
      StandardButton reset = new StandardButton("[red]Reset Stats");
      reset.setRunnable(
         new Runnable() {
            @Override
            public void run() {
               Sounds.playSound(Sounds.pip);
               String text = com.tann.dice.Main.t(
                  "[red]Reset stats?\nThis is permanent\n\n[purple]Includes\n-current saves\n-run history\n-pick rates\n-probably more"
               );
               text = text.replaceFirst("\n\n", "[n][nh]");
               text = text.replaceAll("\n", "[n]");
               ChoiceDialog second = new ChoiceDialog("[notranslateall]" + text, ChoiceDialog.ChoiceNames.RedYes, new Runnable() {
                  @Override
                  public void run() {
                     com.tann.dice.Main.self().masterStats.resetAllStatsButNotAchievements();
                     ContextConfig.CLEAR_ALL_SAVES();
                     com.tann.dice.Main.self().setScreen(com.tann.dice.Main.getCurrentScreen().copy());
                     String text = com.tann.dice.Main.t("[green]All stats reset permanently\n[purple]I hope you are happy").replaceAll("\n", "[n]");
                     com.tann.dice.Main.getCurrentScreen().showDialog("[notranslateall]" + text, Colours.red);
                  }
               }, new Runnable() {
                  @Override
                  public void run() {
                     com.tann.dice.Main.getCurrentScreen().pop(ChoiceDialog.class);
                  }
               });
               com.tann.dice.Main.getCurrentScreen().push(second, 0.7F);
               Tann.center(second);
            }
         }
      );
      p.row(5).actor(reset);
      return p.pix();
   }

   private Group makeLeaderboard(final Leaderboard selected, final int contentWidth) {
      Pixl p = new Pixl(4).forceWidth(contentWidth);
      p.text("[blue]Online Leaderboards").row();
      final Map<String, List<Leaderboard>> leaderboardMap = new HashMap<>();
      List<String> keys = new ArrayList<>();
      List<Leaderboard> acp = new ArrayList<>(LeaderboardBlob.all);
      acp.addAll(LeaderboardBlob.getOld());

      for (Leaderboard l : acp) {
         if (!UnUtil.isLocked(l) && !l.isUnavailable()) {
            String name = l.getSuperName();
            if (leaderboardMap.get(name) == null) {
               leaderboardMap.put(name, new ArrayList<>());
               keys.add(name);
            }

            leaderboardMap.get(name).add(l);
         }
      }

      for (final String s : keys) {
         StandardButton sb = new StandardButton(s);
         p.actor(sb, contentWidth - 10);
         sb.addListener(new TannListener() {
            @Override
            public boolean action(int button, int pointer, float x, float y) {
               Color c = Colours.blue;
               int gap = 4;
               Pixl modal = new Pixl(gap, gap + 1).border(c);
               List<Leaderboard> lbs = leaderboardMap.get(s);
               List<Actor> lButts = new ArrayList<>();

               for (final Leaderboard lx : lbs) {
                  StandardButton tb = lx.makeTextButton();
                  lButts.add(tb);
                  tb.setRunnable(new Runnable() {
                     @Override
                     public void run() {
                        StuffPage.this.showLeaderboard(l, contentWidth);
                     }
                  });
                  if (lx == selected) {
                     tb.setBorder(Colours.light);
                  }
               }

               modal.actor(Tann.layoutMinArea(lButts, gap));
               Actor a = Tann.makeScrollpaneIfNecessary(modal.pix());
               a.setName("leaderboard_modal");
               com.tann.dice.Main.getCurrentScreen().pushAndCenter(a, 0.8F);
               return true;
            }
         });
      }

      p.row();
      if (selected != null) {
         selected.clearCache();
         if (selected.canSubmitBetterScore()) {
            p.actor(selected.getSubmitHighscoreButton()).row();
         }

         LeaderboardDisplay ld = new LeaderboardDisplay(selected);
         p.actor(ld);
      }

      return p.pix();
   }

   private void showLeaderboard(Leaderboard l, int contentWidth) {
      try {
         com.tann.dice.Main.getCurrentScreen().pop("leaderboard_modal");
         Sounds.playSound(Sounds.pipSmall);
         this.showThing(this.makeLeaderboard(l, contentWidth));
      } catch (Exception var4) {
         TannLog.error(var4, "leaderboard ui");
         var4.printStackTrace();
         com.tann.dice.Main.getCurrentScreen().showDialog(var4.getClass().getSimpleName());
      }
   }

   private Group makeRunHistory(Mode selected) {
      Pixl mainPixl = new Pixl(4);
      int modesAdded = 0;
      RunHistoryStore rhs = com.tann.dice.Main.self().masterStats.getRunHistoryStore();

      for (final Mode mode : Mode.getPlayableModes()) {
         if (!UnUtil.isLocked(mode) && rhs.getRuns(mode).size() != 0) {
            StandardButton tb = new StandardButton(mode.getTextButtonName());
            tb.addListener(new TannListener() {
               @Override
               public boolean action(int button, int pointer, float x, float y) {
                  Sounds.playSound(Sounds.pip);
                  StuffPage.this.showThing(StuffPage.this.makeRunHistory(mode));
                  return true;
               }
            });
            mainPixl.actor(tb, (int)(this.getWidth() * 0.9F));
            modesAdded++;
         }
      }

      if (modesAdded == 0) {
         mainPixl.text("[purple]no runs logged :(");
      }

      if (selected != null) {
         mainPixl.row();
         List<RunHistory> runs = rhs.getRuns(selected);
         if (runs.size() == 0) {
            mainPixl.row(7).text("Hmm, bugged?");
         } else {
            List<RunHistory> data = rhs.getRuns(selected);
            mainPixl.actor(RunHistory.makeGroup(data));
         }
      }

      return mainPixl.pix();
   }

   @Override
   public void onFocus(String defaultSidebar) {
      super.onFocus(defaultSidebar);
      com.tann.dice.Main.stage.setScrollFocus(this.contentPanel);
   }

   public static enum StuffSection implements Unlockable {
      Options(Colours.text),
      Credits(CreditsPanel.CREDITS_COL),
      Jukebox(Colours.orange),
      Online(Colours.blue),
      Graph(Colours.red),
      Patch(Colours.purple),
      Numbers(Colours.grey);

      final Color col;

      private StuffSection(Color col) {
         this.col = col;
      }

      private StuffSection() {
         this(Colours.grey);
      }

      public String getColourTaggedString() {
         return TextWriter.getTag(this.col) + this.name();
      }

      @Override
      public Actor makeUnlockActor(boolean big) {
         return UnUtil.makeDefaultUnlock(this.getColourTaggedString(), big);
      }

      @Override
      public TextureRegion getAchievementIcon() {
         return null;
      }

      @Override
      public String getAchievementIconString() {
         return "j";
      }
   }
}
