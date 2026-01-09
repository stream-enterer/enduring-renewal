package com.tann.dice.gameplay.leaderboard;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.mode.Mode;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardBlob {
   public static List<Leaderboard> all = new ArrayList<>();
   private static final String[] OLD = new String[]{
      "classic_hard_streak",
      "slice_and_dice3",
      "classic_unfair_streak",
      "slice_and_dice3",
      "classic_brutal_streak",
      "slice_and_dice3",
      "classic_hell_streak",
      "slice_and_dice3",
      "choose-party_brutal_streak",
      "slice_and_dice3",
      "choose-party_hard_streak",
      "slice_and_dice3",
      "choose-party_hell_streak",
      "slice_and_dice3",
      "choose-party_unfair_streak",
      "slice_and_dice3",
      "demo_brutal_streak",
      "slice_and_dice3",
      "demo_hard_streak",
      "slice_and_dice3",
      "demo_hell_streak",
      "slice_and_dice3",
      "demo_unfair_streak",
      "slice_and_dice3",
      "dream_brutal_streak",
      "slice_and_dice3",
      "dream_hard_streak",
      "slice_and_dice3",
      "dream_hell_streak",
      "slice_and_dice3",
      "dream_unfair_streak",
      "slice_and_dice3",
      "speedrun_choose-party",
      "slice_and_dice3",
      "speedrun_dream",
      "slice_and_dice3",
      "speedrun_classic",
      "slice_and_dice3",
      "speedrun_demo",
      "slice_and_dice3",
      "speedrun_loot",
      "slice_and_dice3",
      "speedrun_generate",
      "slice_and_dice3",
      "speedrun_raid",
      "slice_and_dice3",
      "speedrun_shortcut",
      "slice_and_dice3",
      "generate_brutal_streak",
      "slice_and_dice3",
      "generate_hard_streak",
      "slice_and_dice3",
      "generate_hell_streak",
      "slice_and_dice3",
      "generate_unfair_streak",
      "slice_and_dice3",
      "blursed_highest",
      "slice_and_dice3",
      "Blurtra_highest",
      "slice_and_dice3",
      "Cursed-Hyper_highest",
      "slice_and_dice3",
      "Cursed_highest",
      "slice_and_dice3",
      "Cursed-Ultra_highest",
      "slice_and_dice3",
      "loot_brutal_streak",
      "slice_and_dice3",
      "loot_hard_streak",
      "slice_and_dice3",
      "loot_hell_streak",
      "slice_and_dice3",
      "loot_unfair_streak",
      "slice_and_dice3",
      "raid_brutal_streak",
      "slice_and_dice3",
      "raid_hard_streak",
      "slice_and_dice3",
      "raid_hell_streak",
      "slice_and_dice3",
      "raid_unfair_streak",
      "slice_and_dice3",
      "shortcut_brutal_streak",
      "slice_and_dice3",
      "shortcut_hard_streak",
      "slice_and_dice3",
      "shortcut_hell_streak",
      "slice_and_dice3",
      "shortcut_unfair_streak",
      "slice_and_dice3",
      "alpha-losses",
      "slice_and_dice2",
      "slowest",
      "slice_and_dice2",
      "slowest2",
      "slice_and_dice2",
      "fastest_classic_normal",
      "slice_and_dice2",
      "blursed_highest3",
      "slice_and_dice2",
      "curse_highest",
      "slice_and_dice2",
      "curse_highest2",
      "slice_and_dice2",
      "curse_highest3",
      "slice_and_dice2",
      "curse_hyper_highest",
      "slice_and_dice2",
      "curse_hyper_highest2",
      "slice_and_dice2",
      "curse_hyper_highest3",
      "slice_and_dice2",
      "streak_classic_brutal",
      "slice_and_dice2",
      "streak_classic_hard",
      "slice_and_dice2",
      "streak_classic_hell",
      "slice_and_dice2",
      "streak_classic_unfair",
      "slice_and_dice2",
      "streak_demo_hard",
      "slice_and_dice2",
      "bones_defeated",
      "slice_and_dice2",
      "fastest_standard_normal",
      "slice_and_dice",
      "curse_highest",
      "slice_and_dice",
      "streak_standard_hard",
      "slice_and_dice",
      "streak_standard_unfair",
      "slice_and_dice",
      "bones_defeated",
      "slice_and_dice"
   };

   public static void setupLeaderboards() {
      all.clear();

      for (Mode m : new Mode[]{Mode.DEMO, Mode.CLASSIC, Mode.SHORTCUT, Mode.GENERATE_HEROES, Mode.LOOT, Mode.RAID, Mode.CHOOSE_PARTY, Mode.DREAM}) {
         for (Difficulty d : new Difficulty[]{Difficulty.Hard, Difficulty.Unfair, Difficulty.Brutal, Difficulty.Hell}) {
            all.add(new StreakLeaderboard(m, d, m.getName().toLowerCase() + "_" + d.name().toLowerCase() + "_streak"));
         }

         all.add(new SpeedrunLeaderboard(m));
      }

      for (Mode m : new Mode[]{Mode.CURSE, Mode.BLURSED, Mode.CURSE_HYPER, Mode.CURSED_ULTRA, Mode.BLURTRA, Mode.BLYPTRA}) {
         all.add(new CursedLeaderboard(m));
      }
   }

   public static Leaderboard byName(String name) {
      if (all.size() == 0) {
         throw new RuntimeException("leaderboards not setup yet");
      } else {
         for (Leaderboard l : all) {
            if (l.getName().equalsIgnoreCase(name)) {
               return l;
            }
         }

         throw new RuntimeException("Could not find leaderboard with name :" + name);
      }
   }

   public static List<Leaderboard> getOld() {
      List<Leaderboard> rs = new ArrayList<>();

      for (int i = 0; i < OLD.length; i += 2) {
         String url = OLD[i];
         String game = OLD[i + 1];
         rs.add(new OldLeaderboard(game, url));
      }

      return rs;
   }
}
