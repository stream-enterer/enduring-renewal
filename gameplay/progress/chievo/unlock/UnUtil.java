package com.tann.dice.gameplay.progress.chievo.unlock;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.leaderboard.Leaderboard;
import com.tann.dice.gameplay.leaderboard.StreakLeaderboard;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderMode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.progress.chievo.AchLib;
import com.tann.dice.gameplay.save.settings.option.BOption;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.Screen;
import com.tann.dice.screens.dungeon.panels.book.page.ledgerPage.LedgerPage;
import com.tann.dice.screens.titleScreen.TitleScreen;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.List;

public class UnUtil {
   public static boolean isLocked(Unlockable u) {
      if (com.tann.dice.Main.demo && u == Mode.CLASSIC) {
         return true;
      } else if (u instanceof HeroType && isLocked(((HeroType)u).heroCol)) {
         return true;
      } else if (u instanceof FolderMode && ((FolderMode)u).isLocked()) {
         return true;
      } else {
         if (u instanceof StreakLeaderboard) {
            StreakLeaderboard sl = (StreakLeaderboard)u;
            Difficulty d = sl.difficulty;
            if (d != null && isLocked(d)) {
               return true;
            }
         }

         return com.tann.dice.Main.unlockManager().isLocked(u);
      }
   }

   public static boolean wasLocked(Unlockable u) {
      return AchLib.hasAchievement(u) && !com.tann.dice.Main.unlockManager().isLocked(u);
   }

   public static String nameFor(Unlockable u) {
      return nameFor((Class<? extends Unlockable>)u.getClass());
   }

   public static String nameFor(Class<? extends Unlockable> aClass) {
      if (aClass.isAnonymousClass()) {
         aClass = (Class<? extends Unlockable>)aClass.getSuperclass();
      }

      if (Mode.class.isAssignableFrom(aClass)) {
         return "mode";
      } else if (Leaderboard.class.isAssignableFrom(aClass)) {
         return "leaderboard";
      } else if (aClass == HeroType.class) {
         return "hero";
      } else if (aClass == LedgerPage.LedgerPageType.class) {
         return "ledger page";
      } else if (aClass == MonsterType.class) {
         return "monster";
      } else {
         return aClass == BOption.class ? "option" : aClass.getSimpleName().toLowerCase();
      }
   }

   public static List<Unlockable> makeAll(Boolean everLocked) {
      List<Unlockable> result = new ArrayList<>();
      result.addAll(ItemLib.getMasterCopy());
      result.addAll(ModifierLib.getAll());
      result.addAll(EntTypeUtils.getAll());
      result.addAll(Mode.getAllModes());
      result.addAll(OptionUtils.getAll());
      if (everLocked != null) {
         for (int i = result.size() - 1; i >= 0; i--) {
            if (AchLib.hasAchievement(result.get(i)) != everLocked) {
               result.remove(i);
            }
         }
      }

      return result;
   }

   public static Actor makeDefaultUnlock(String colourTaggedString, boolean big) {
      return new Pixl(2).text(colourTaggedString).pix();
   }

   public static void colUnlocked(TitleScreen screen, List<HeroCol> unl) {
      if (!unl.isEmpty()) {
         Pixl p = new Pixl(-1);

         for (HeroCol h : unl) {
            com.tann.dice.Main.getSettings().notifyUnlock(h.colName);
            p.actor(new TextWriter("New hero color unlocked: " + h.colourTaggedName(false), 999, h.col, 4));
            p.row(2);
         }

         Group g = p.pix();
         screen.push(g, 0.8F);
         Tann.center(g);
      }
   }

   private static void modeUnlocked(Screen screen, List<Mode> modes) {
      Pixl p = new Pixl(-1);
      boolean anyUnlocked = false;
      if (modes.size() > 4) {
         for (Mode m : modes) {
            com.tann.dice.Main.getSettings().notifyModeUnlock(m);
         }

         anyUnlocked = true;
         p.actor(new TextWriter("[b][blue]" + modes.size() + " modes unlocked!", 999, Colours.grey, 3));
      } else {
         for (Mode m : modes) {
            if (!m.isDebug()) {
               com.tann.dice.Main.getSettings().notifyModeUnlock(m);
               if (m.displayPopup()) {
                  FolderType ft = m.getFolderType();
                  String modeType = ft != null && m != Mode.CURSE ? ft + " " : "";
                  p.actor(new TextWriter("New " + modeType + "mode unlocked: " + m.getTextButtonName(), 999, m.getColour(), 4));
                  p.row(2);
                  anyUnlocked = true;
               }
            }
         }
      }

      if (anyUnlocked) {
         Group g = p.pix();
         screen.push(g, 0.8F);
         Tann.center(g);
      }
   }

   public static void checkModeUnlocks(Screen screen) {
      if (!com.tann.dice.Main.demo) {
         List<Mode> unlockedModes = new ArrayList<>();

         for (Mode m : Mode.getPlayableModes()) {
            if (!m.skipUnlockNotify() && !isLocked(m) && !com.tann.dice.Main.getSettings().isModeUnlockNotified(m)) {
               unlockedModes.add(m);
            }
         }

         modeUnlocked(screen, unlockedModes);
      }
   }

   public static List<Mode> getUnlockNotified() {
      List<Mode> result = new ArrayList<>();

      for (Mode m : Mode.getPlayableModes()) {
         if (com.tann.dice.Main.getSettings().isModeUnlockNotified(m)) {
            result.add(m);
         }
      }

      return result;
   }
}
