package com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.runEnd;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RunEndAchievement extends Achievement {
   final transient RunEndCondition runEndCondition;

   public RunEndAchievement(RunEndCondition runEndCondition, Unlockable... unlockables) {
      this(runEndCondition, NAME_FOR(runEndCondition), null, unlockables);
   }

   public RunEndAchievement(RunEndCondition runEndCondition, String name, String extraDescription, Unlockable... unlockables) {
      super(trName(name), DESC_FOR(runEndCondition, extraDescription), unlockables);
      this.runEndCondition = runEndCondition;
   }

   private static String trName(String name) {
      return name.equals("victory") ? "winner" : name;
   }

   private static String NAME_FOR(RunEndCondition runEndCondition) {
      return runEndCondition.describeShort();
   }

   private static String DESC_FOR(RunEndCondition runEndCondition, String extra) {
      return runEndCondition.describe(extra);
   }

   public final boolean runEndCheck(DungeonContext context, ContextConfig contextConfig, Difficulty difficulty, boolean victory) {
      return this.runEndCondition != null && !this.runEndCondition.isValid(victory, contextConfig) ? false : this.aRunEndCheck(context, contextConfig);
   }

   protected boolean aRunEndCheck(DungeonContext context, ContextConfig contextConfig) {
      return true;
   }

   public static List<Achievement> make() {
      List<Achievement> result = new ArrayList<>();
      result.addAll(ChoosePartyEnd.makeAllCPE());
      result.addAll(
         Arrays.asList(
            new RunEndAchievement(new RunEndCondition(null, null), HeroTypeUtils.getAltT1s(0)).diff(0.0F),
            new RunEndAchievement(new RunEndCondition(null, Difficulty.Hard), Difficulty.Unfair).diff(1.0F),
            new RunEndAchievement(new RunEndCondition(Mode.RAID), Mode.ROOT).diff(1.0F),
            new RunEndAchievement(new RunEndCondition(null, Difficulty.Unfair), Difficulty.Brutal).diff(8.0F),
            new RunEndAchievement(new RunEndCondition(null, Difficulty.Brutal), Difficulty.Hell).diff(30.0F),
            new RunEndAchievement(new RunEndCondition(null, Difficulty.Hell)),
            (new RunEndAchievement(new RunEndCondition(Mode.CLASSIC, Difficulty.Normal), "Speedrun", "with a time of 45m or less") {
               @Override
               protected boolean aRunEndCheck(DungeonContext context, ContextConfig contextConfig) {
                  return context.getFinalTimeSeconds() <= 2700L;
               }
            }).diff(5.0F),
            new RunEndAchievement(new RunEndCondition(Mode.CLASSIC), "All T1", "with only t1 heroes") {
               @Override
               protected boolean aRunEndCheck(DungeonContext context, ContextConfig contextConfig) {
                  for (Hero h : context.getParty().getHeroes()) {
                     if (h.getHeroType().level != 1) {
                        return false;
                     }
                  }

                  return true;
               }
            },
            (new RunEndAchievement(
                  new RunEndCondition(Mode.CLASSIC), "All T2", "with only t2 heroes", ModifierLib.getAllStartingWith("de-level").toArray(new Modifier[0])
               ) {
                  @Override
                  protected boolean aRunEndCheck(DungeonContext context, ContextConfig contextConfig) {
                     for (Hero h : context.getParty().getHeroes()) {
                        if (h.getHeroType().level != 2) {
                           return false;
                        }
                     }

                     return true;
                  }
               })
               .diff(10.0F),
            (new RunEndAchievement(new RunEndCondition(Mode.SHORTCUT, Difficulty.Normal), "Quick Shortcut", "in 15m or less", OptionLib.DISABLE_MARQUEE) {
               @Override
               protected boolean aRunEndCheck(DungeonContext context, ContextConfig contextConfig) {
                  return context.getFinalTimeSeconds() <= 900L;
               }
            }).diff(20.0F),
            new RunEndAchievement(new RunEndCondition(Mode.GENERATE_HEROES), OptionLib.GENERATED_HEROES).diff(4.0F),
            new RunEndAchievement(new RunEndCondition(Mode.GENERATE_HEROES, Difficulty.Hard), OptionLib.GENERATED_ITEMS).diff(8.0F),
            new RunEndAchievement(new RunEndCondition(Mode.GENERATE_HEROES, Difficulty.Unfair), OptionLib.GENERATED_MONSTERS).diff(12.0F),
            new RunEndAchievement(new RunEndCondition(Mode.DREAM, Difficulty.Hard), OptionLib.CRAZY_UI).diff(12.0F),
            new RunEndAchievement(new RunEndCondition(Mode.CLASSIC, Difficulty.Unfair, false), OptionLib.COMPLEX_HARD_EASY).diff(0.0F),
            new RunEndAchievement(new RunEndCondition(Mode.CLASSIC, null, true), "Alone", "With a single hero in your party") {
               @Override
               protected boolean aRunEndCheck(DungeonContext context, ContextConfig contextConfig) {
                  return context.getParty().getHeroes().size() == 1;
               }
            }
         )
      );
      return result;
   }

   @Override
   public boolean isCompletable() {
      return this.runEndCondition.isCompletable();
   }

   public static int getMaxOfOneColour(Party p) {
      List<HeroCol> cols = new ArrayList<>();

      for (Hero h : p.getHeroes()) {
         cols.add(h.getHeroCol());
      }

      return Tann.getMaxInList(cols);
   }

   @Override
   public boolean forSpecificMode(Mode mode) {
      return this.runEndCondition.mode == mode;
   }
}
