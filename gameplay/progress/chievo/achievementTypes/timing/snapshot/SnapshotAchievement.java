package com.tann.dice.gameplay.progress.chievo.achievementTypes.timing.snapshot;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.command.AbilityCommand;
import com.tann.dice.gameplay.fightLog.command.Command;
import com.tann.dice.gameplay.fightLog.command.TargetableCommand;
import com.tann.dice.gameplay.progress.StatSnapshot;
import com.tann.dice.gameplay.progress.chievo.Achievement;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.util.lang.Words;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SnapshotAchievement extends Achievement {
   public SnapshotAchievement(String name, String description, Unlockable... unlockables) {
      super(name, description, unlockables);
   }

   public abstract boolean snapshotCheck(StatSnapshot var1);

   public static List<Achievement> make() {
      List<Achievement> all = new ArrayList<>();
      all.addAll(SnapshotKeywordAchievement.makeAll());
      all.addAll(SnapshotLoseHero.makeAll());
      all.addAll(MultiReviveAchievement.makeAll());
      all.addAll(
         Arrays.asList(
            (new SnapshotAchievement("Saviour", "Save 3 heroes with a single action", ItemLib.byName("Shimmering Halo")) {
                  @Override
                  public boolean snapshotCheck(StatSnapshot ss) {
                     return !ss.afterCommand.isVictory()
                        && ss.origin instanceof TargetableCommand
                        && ((TargetableCommand)ss.origin).targetable.getDerivedEffects().isFriendly()
                        && ss.previousFuture.getEntities(true, true).size() - ss.future.getEntities(true, true).size() >= 3;
                  }
               })
               .diff(7.0F),
            (new SnapshotAchievement("Overgrowth", "Activate growth on a single side 4 times", ItemLib.byName("Smelly Manure")) {
               @Override
               public boolean snapshotCheck(StatSnapshot ss) {
                  for (EntState es : ss.afterCommand.getAliveHeroStates()) {
                     EntSide currentSide = es.getEnt().getDie().getCurrentSide();
                     if (currentSide != null) {
                        EntSideState state = es.getSideState(currentSide);
                        if (state.getBonusFromGrowth() >= 4) {
                           return true;
                        }
                     }
                  }

                  return false;
               }
            }).diff(7.0F),
            (new SnapshotAchievement("Reaper", "Kill 5 monsters with a single action", ItemLib.byName("Demonic Deal")) {
               @Override
               public boolean snapshotCheck(StatSnapshot ss) {
                  return ss.future.getEntities(false, true).size() - ss.previousFuture.getEntities(false, true).size() >= 5;
               }
            }).diff(7.0F),
            (new SnapshotAchievement("Ironclad", "Shield a single hero for 10", ItemLib.byName("Quicksilver")) {
               @Override
               public boolean snapshotCheck(StatSnapshot ss) {
                  for (EntState es : ss.afterCommand.getAliveHeroStates()) {
                     if (es.getShields() >= 10) {
                        return true;
                     }
                  }

                  return false;
               }
            }).diff(7.0F),
            (new SnapshotAchievement("Wizard", "Use 6 abilities in a single turn", ItemLib.byName("Deadly Bolt")) {
               @Override
               public boolean snapshotCheck(StatSnapshot ss) {
                  int total = 0;

                  for (Command c : ss.pastCommands) {
                     if (c instanceof AbilityCommand) {
                        total++;
                     }
                  }

                  return total >= 6;
               }
            }).diff(7.0F),
            (new SnapshotAchievement("NOT FAIR!", "Stun " + Words.entName(true, false, false) + " with 20+ max hp", ItemLib.byName("Wand of Stun")) {
               @Override
               public boolean snapshotCheck(StatSnapshot ss) {
                  for (EntState m : ss.afterCommand.getAliveMonsterStates()) {
                     if (m.isStunned() && m.getMaxHp() >= 20) {
                        return true;
                     }
                  }

                  return false;
               }
            }).diff(7.0F),
            (new SnapshotAchievement("Mandrake", "Have 10+ poison on " + Words.entName(true, false, false), ItemLib.byName("Horned Viper")) {
               @Override
               public boolean snapshotCheck(StatSnapshot ss) {
                  for (EntState m : ss.afterCommand.getAliveMonsterStates()) {
                     if (m.getBasePoisonPerTurn() >= 10) {
                        return true;
                     }
                  }

                  return false;
               }
            }).diff(7.0F),
            (new SnapshotAchievement("Green Blood", "Have 5+ poison on an ally", ItemLib.byName("Wine")) {
               @Override
               public boolean snapshotCheck(StatSnapshot ss) {
                  for (EntState m : ss.afterCommand.getAliveHeroStates()) {
                     if (m.getBasePoisonPerTurn() >= 5) {
                        return true;
                     }
                  }

                  return false;
               }
            }).diff(7.0F),
            (new SnapshotAchievement("Splattered", "Overkill an enemy by 10 or more", ItemLib.byName("Obsidian Edge")) {
               @Override
               public boolean snapshotCheck(StatSnapshot ss) {
                  for (EntState m : ss.afterCommand.getStates(false, true)) {
                     if (m.getHp() <= -10) {
                        EntState b4 = ss.beforeCommand.getState(m.getEnt());
                        if (b4 != null && !b4.isDead()) {
                           return true;
                        }
                     }
                  }

                  return false;
               }
            }).diff(7.0F),
            (new SnapshotAchievement("Manastorm", "Accumulate 10 mana", ItemLib.byName("mana jelly")) {
               @Override
               public boolean snapshotCheck(StatSnapshot ss) {
                  return ss.afterCommand.getTotalMana() >= 10;
               }
            }).diff(8.0F),
            (new SnapshotAchievement("Manastorm+", "Accumulate 100 mana", ItemLib.byName("lich finger")) {
               @Override
               public boolean snapshotCheck(StatSnapshot ss) {
                  return ss.afterCommand.getTotalMana() >= 100;
               }
            }).diff(16.0F),
            new SnapshotAchievement("Challenge Accepted", "Defeat a dragon before fight 20") {
               @Override
               public boolean snapshotCheck(StatSnapshot ss) {
                  if (ss.context.getCurrentLevelNumber() >= 20) {
                     return false;
                  } else {
                     for (EntState m : ss.afterCommand.getStates(false, true)) {
                        if (m.getEnt().getEntType() == MonsterTypeLib.byName("dragon")) {
                           return true;
                        }
                     }

                     return false;
                  }
               }
            },
            new SnapshotAchievement("Max mana", "Reach 999 mana") {
               @Override
               public boolean snapshotCheck(StatSnapshot ss) {
                  return ss.afterCommand.getTotalMana() == 999;
               }
            },
            new SnapshotAchievement("Max rerolls", "Reach 999 rerolls") {
               @Override
               public boolean snapshotCheck(StatSnapshot ss) {
                  return ss.afterCommand.getRolls() == 999;
               }
            },
            new SnapshotAchievement("Max shields", "Reach 999 shields") {
               @Override
               public boolean snapshotCheck(StatSnapshot ss) {
                  List<EntState> states = ss.afterCommand.getStates(true, false);

                  for (int i = 0; i < states.size(); i++) {
                     if (states.get(i).getShields() == 999) {
                        return true;
                     }
                  }

                  return false;
               }
            }
         )
      );
      return all;
   }
}
