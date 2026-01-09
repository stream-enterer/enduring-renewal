package com.tann.dice.screens.dungeon.panels.book.page.ledgerPage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.progress.MasterStatsUtils;
import com.tann.dice.gameplay.progress.chievo.AchLib;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.screens.dungeon.panels.book.TopTab;
import com.tann.dice.screens.dungeon.panels.book.page.BookPage;
import com.tann.dice.screens.dungeon.panels.book.page.stuffPage.APIUtils;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.choice.ChoiceDialog;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LedgerPage extends BookPage {
   public static final int CHALLENGES_SHOWN = 5;

   public LedgerPage(Map<String, Stat> allMergedStats, int width, int height) {
      super("[blue]ledger", allMergedStats, width, height);
   }

   @Override
   public void onFocus(String defaultSidebar) {
      super.onFocus(defaultSidebar);
      com.tann.dice.Main.stage.setScrollFocus(this.contentPanel);
   }

   @Override
   protected List<TopTab> getAllListItems() {
      List<TopTab> items = new ArrayList<>();

      for (LedgerPage.LedgerPageType t : LedgerPage.LedgerPageType.getPagesInOrder()) {
         if (!UnUtil.isLocked(t)) {
            if (t.equals(LedgerPage.LedgerPageType.Pin) && com.tann.dice.Main.self().translator.shouldTranslate()) {
            }

            items.add(new TopTab(t, TextWriter.getTag(t.col) + t.getName(), getSideWidth()));
         }
      }

      return items;
   }

   @Override
   protected Actor getContentActorFromSidebar(Object type, int contentWidth) {
      switch ((LedgerPage.LedgerPageType)type) {
         case Hero:
            return LedgerUtils.makeHeroGroup(this.allMergedStats, contentWidth);
         case Monster:
            return LedgerUtils.makeMonsterGroup(this.allMergedStats, contentWidth);
         case Item:
            return LedgerUtils.makeItemsGroup(this.allMergedStats, contentWidth);
         case Modifier:
            return LedgerUtils.makeModifiersGroup(this.allMergedStats, contentWidth, this, false, LedgerUtils.ModGenType.Designed);
         case Keyword:
            return LedgerUtils.makeKeywordsGroup(this.allMergedStats, contentWidth);
         case Pin:
            return LedgerUtils.makePinsGroup(this.allMergedStats, contentWidth, this);
         case Unlock:
            return this.makeChievoPage(contentWidth);
         case TextMod:
            return APIUtils.makeMetaApiPage(this, contentWidth);
         default:
            throw new RuntimeException("eep-" + type);
      }
   }

   private Group makeChievoGroup(List<Achievement> all, List<Achievement> achievedInOrder, List<Achievement> show, int maxWidth, boolean secret) {
      boolean challenge = all.get(0).isChallenge();
      Pixl p = new Pixl(-1).row(0);
      int achieved = 0;

      for (Achievement a : all) {
         if (a.isAchieved()) {
            achieved++;
         }
      }

      String allString = all.size() + "";
      String partString = achieved + "";
      if (secret && achieved < all.size()) {
         allString = "?";
      }

      p.text((challenge ? "[yellow]" : "[purple]") + partString + "/" + allString);
      p.row(2);
      if (achievedInOrder.size() > 1) {
         String t = com.tann.dice.Main.t("most recent").replace(" ", "[n]");
         Actor ax = new Pixl().text("[notranslateall][green]" + t).gap(1).text("[green]->").pix();
         Group g = Tann.makeGroup(ax);
         g.setSize(ax.getWidth() + 1.0F, 18.0F);
         Tann.center(ax);
         p.actor(g);
      }

      for (int i = achievedInOrder.size() - 1; i >= 0; i--) {
         Achievement ax = achievedInOrder.get(i);
         p.actor(new AchievementIconView(ax), maxWidth);
      }

      if (show.size() > 0) {
         p.row(4);
         p.text("[text]Some incomplete " + Words.plural("achievement") + ":").row(2);

         for (Achievement ax : show) {
            p.actor(new AchievementIconView(ax), maxWidth);
         }
      }

      return p.pix();
   }

   private Group makeChievoPage(int contentWidth) {
      Pixl p = new Pixl(0);
      int CHIEVO_GAP = 10;
      int CHIEVO_WIDTH = (int)(contentWidth * 0.9F);
      int INFO_GAP = 2;
      int MINI_CHIEV_GAP = 3;
      List<Achievement> extraChallenges = com.tann.dice.Main.unlockManager().getShownChallenges();
      String cc = TextWriter.getTag(Achievement.CHALLENGE_COL);
      String sc = TextWriter.getTag(Achievement.SECRET_COL);
      p.text(cc + Words.plural(Words.capitaliseFirst("achievement")))
         .gap(2)
         .actor(
            makeInfoButton(
               cc + "?",
               cc
                  + Words.plural(Words.capitaliseFirst("achievement"))
                  + " all unlock something.[n]"
                  + 5
                  + " incomplete "
                  + "achievement"
                  + "s are shown below."
            )
         )
         .row(3);
      p.actor(
            this.makeChievoGroup(
               AchLib.getChallenges(),
               com.tann.dice.Main.self().masterStats.getUnlockManager().getCompletedAchievements(false),
               extraChallenges,
               CHIEVO_WIDTH,
               false
            )
         )
         .row(10);
      if (AchLib.anyAchieved(AchLib.getSecrets())) {
         p.text(sc + Words.plural(Words.capitaliseFirst("secret")))
            .gap(2)
            .actor(
               makeInfoButton(
                  sc + "?",
                  sc
                     + Words.plural(Words.capitaliseFirst("secret"))
                     + " are a type of achievement but they don't unlock anything and probably aren't fun to attempt!"
               )
            )
            .row(3);
         p.actor(
               this.makeChievoGroup(
                  AchLib.getSecrets(),
                  com.tann.dice.Main.self().masterStats.getUnlockManager().getCompletedAchievements(true),
                  new ArrayList<>(),
                  CHIEVO_WIDTH,
                  true
               )
            )
            .row(10);
      }

      p.actor(
         new Pixl(4)
            .listActor(
               contentWidth,
               MasterStatsUtils.makeCopyAchievementsButton(),
               MasterStatsUtils.makeLoadProgressButton(),
               makeCopyPickRates(),
               OptionLib.BYPASS_UNLOCKS.makeCogActor()
            )
      );
      return p.pix();
   }

   public static Actor makeCopyPickRates() {
      StandardButton copy = new StandardButton("[grey]Copy pick-rates");
      copy.setRunnable(
         new Runnable() {
            @Override
            public void run() {
               Sounds.playSound(Sounds.pip);
               String text = com.tann.dice.Main.t(
                  "[light]Copy pick-rates to clipboard?\n[text]This may be very large and will not be easy to understand, I don't know what this is for yet..."
               );
               text = "[notranslateall]" + text.replaceAll("\n", "[n][nh]");
               ChoiceDialog choiceDialog = new ChoiceDialog(
                  null, Arrays.asList(new TextWriter(text, 120)), ChoiceDialog.ChoiceNames.YesCancel, new Runnable() {
                     @Override
                     public void run() {
                        com.tann.dice.Main.getCurrentScreen().popSingleMedium();
                        com.tann.dice.Main.self().masterStats.savePickRatesToClipboard();
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

   private static Actor makeInfoButton(String title, final String info) {
      StandardButton q = new StandardButton(title).makeTiny();
      q.setRunnable(new Runnable() {
         @Override
         public void run() {
            Sounds.playSound(Sounds.pip);
            com.tann.dice.Main.getCurrentScreen().showDialog(info);
         }
      });
      return q;
   }

   public static enum LedgerPageType implements Unlockable {
      Pin(Colours.blue),
      Unlock(Colours.green),
      Hero(HeroType.getIdCol()),
      Monster(MonsterType.getIdCol()),
      Item(Item.getIdCol()),
      Modifier(Colours.purple),
      Keyword(Colours.pink),
      TextMod(Colours.red);

      final Color col;

      static LedgerPage.LedgerPageType[] getPagesInOrder() {
         return com.tann.dice.Main.self().translator.shouldTranslate()
            ? new LedgerPage.LedgerPageType[]{Unlock, Pin, Hero, Monster, Item, Modifier, Keyword, TextMod}
            : values();
      }

      private LedgerPageType(Color col) {
         this.col = col;
      }

      @Override
      public Actor makeUnlockActor(boolean big) {
         return new TextWriter(this.getColourTaggedString());
      }

      @Override
      public TextureRegion getAchievementIcon() {
         return null;
      }

      @Override
      public String getAchievementIconString() {
         return "[pink]G";
      }

      public String getName() {
         String name = this.name();
         if (this.name().equals("Pin") && com.tann.dice.Main.self().translator.shouldTranslate()) {
            name = "[notranslate]" + com.tann.dice.Main.t("Pin (% menu item %)");
         }

         return name;
      }

      public String getColourTaggedString() {
         return TextWriter.getTag(this.col) + this.getName() + "[cu]";
      }
   }
}
