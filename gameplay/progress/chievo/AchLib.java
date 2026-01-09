package com.tann.dice.gameplay.progress.chievo;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.progress.chievo.achievementTypes.debug.PlaceholderAchievement;
import com.tann.dice.gameplay.progress.chievo.achievementTypes.statAchievement.StatAchievement;
import com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.afterRoll.AfterRollAchievement;
import com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.equip.EquipAchievement;
import com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.fightEnd.FightEndAchievement;
import com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.runEnd.RunEndAchievement;
import com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.runEnd.RunEndStatAchievement;
import com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.snapshot.SnapshotAchievement;
import com.tann.dice.gameplay.progress.chievo.achievementTypes.weird.MetaAchievement;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.screens.dungeon.panels.book.page.ledgerPage.AchievementIconView;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AchLib {
   private static List<Achievement> all;
   private static List<Achievement> challenges;
   private static List<Achievement> secrets;
   private static List<Unlockable> achievementUnlockables;

   public static void init() {
      challenges = makeAllAchievements();
      secrets = new ArrayList<>();

      for (int i = challenges.size() - 1; i >= 0; i--) {
         Achievement a = challenges.get(i);
         if (a.getUnlockables().length == 0) {
            secrets.add(challenges.remove(i));
         }
      }

      all = new ArrayList<>();
      all.addAll(challenges);
      all.addAll(secrets);
      achievementUnlockables = new ArrayList<>();

      for (Achievement a : all) {
         achievementUnlockables.addAll(Arrays.asList(a.getUnlockables()));
      }
   }

   public static List<Achievement> getAll() {
      return all;
   }

   public static List<Achievement> getChallenges() {
      return challenges;
   }

   public static List<Achievement> getSecrets() {
      return secrets;
   }

   public static Achievement getAchievementFromUnlock(Unlockable unlockable) {
      for (Achievement a : com.tann.dice.Main.unlockManager().getAllAchievements()) {
         for (Unlockable u : a.getUnlockables()) {
            if (u == unlockable) {
               return a;
            }
         }
      }

      return null;
   }

   private static List<Achievement> makeAllAchievements() {
      List<Achievement> tmp = new ArrayList<>();
      tmp.addAll(StatAchievement.make());
      tmp.addAll(RunEndAchievement.make());
      tmp.addAll(RunEndStatAchievement.make());
      tmp.addAll(PlaceholderAchievement.make());
      tmp.addAll(EquipAchievement.makeAll());
      tmp.addAll(FightEndAchievement.make());
      tmp.addAll(SnapshotAchievement.make());
      tmp.addAll(AfterRollAchievement.make());
      tmp.addAll(MetaAchievement.make(tmp));
      return tmp;
   }

   public static void showUnlockFor(Unlockable unlockable) {
      Sounds.playSound(Sounds.pip);
      Achievement achievement = getAchievementFromUnlock(unlockable);
      if (achievement == null && unlockable instanceof HeroType) {
         showUnlockFor(((HeroType)unlockable).heroCol);
      } else {
         if (achievement != null) {
            Actor a = AchievementIconView.makeAchievementDetail(achievement);
            Sounds.playSound(Sounds.pip);
            com.tann.dice.Main.getCurrentScreen().push(a, true, true, true, 0.7F);
            Tann.center(a);
         }
      }
   }

   public static boolean anyAchieved(List<Achievement> achievements) {
      for (Achievement a : achievements) {
         if (a.isAchieved()) {
            return true;
         }
      }

      return false;
   }

   public static boolean hasAchievement(Unlockable u) {
      return achievementUnlockables.contains(u);
   }
}
