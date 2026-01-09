package com.tann.dice.test.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.generate.PipeHeroGenerated;
import com.tann.dice.gameplay.content.gen.pipe.item.facade.FacadeUtils;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.misc.DebugConfig;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.targetable.DieTargetable;
import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.command.AbilityCommand;
import com.tann.dice.gameplay.fightLog.command.DieCommand;
import com.tann.dice.gameplay.fightLog.command.SimpleCommand;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.save.SaveState;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.TestAffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.Explanel.Explanel;
import com.tann.dice.screens.dungeon.panels.book.page.cogPage.menuPanel.OptionsMenu;
import com.tann.dice.statics.bullet.BulletStuff;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.DebugUtilsUseful;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.saves.Prefs;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TestUtils {
   public static List<Hero> heroes;
   public static List<Monster> monsters;

   public static FightLog setupFight(HeroType[] heroTypes, MonsterType[] monsterTypes) {
      return setupFight(heroTypes, monsterTypes, new Modifier[0]);
   }

   public static FightLog setupFight(HeroType[] heroTypes, MonsterType[] monsterTypes, Modifier[] modifiers) {
      return setupFight(HeroTypeUtils.getHeroes(heroTypes), MonsterTypeLib.monsterList(monsterTypes), modifiers);
   }

   public static FightLog setupFight(Hero h, MonsterType mt) {
      return setupFight(Tann.asList(h), Tann.asList(mt.makeEnt()), new Modifier[0]);
   }

   public static FightLog setupFight(List<Hero> heroes, List<Monster> monsters, Modifier[] modifiers) {
      TestUtils.heroes = heroes;
      TestUtils.monsters = monsters;
      DungeonContext dc = new DungeonContext(new DebugConfig(), new Party(heroes), 1);

      for (Modifier m : modifiers) {
         dc.addModifier(m);
      }

      FightLog f = new FightLog(dc);
      f.setup(heroes, monsters);
      f.resetTurn(true);
      return f;
   }

   public static FightLog setupFight() {
      return setupFight(new HeroType[]{HeroTypeUtils.byName("Fighter")}, new MonsterType[]{MonsterTypeLib.byName("testGoblin")});
   }

   public static FightLog setupFight(MonsterType... monsters) {
      return setupFight(new HeroType[]{HeroTypeUtils.byName("Fighter")}, monsters);
   }

   public static void attack(FightLog fightLog, Ent source, Ent target, int damage) {
      attack(fightLog, source, target, damage, false);
   }

   public static void attack(FightLog fightLog, Ent source, Ent target, int damage, boolean future) {
      fightLog.addCommand(new SimpleCommand(target, new SimpleTargetable(source, ESB.dmg.val(damage).getBaseEffect())), future);
   }

   public static void hit(FightLog fightLog, Ent source, Ent target, EntSide entSide) {
      hit(fightLog, source, target, entSide, false);
   }

   public static void hit(FightLog fightLog, Ent source, Ent target, EntSide entSide, boolean future) {
      fightLog.addCommand(new SimpleCommand(target, new SimpleTargetable(source, entSide.getBaseEffect())), future);
   }

   public static void spell(FightLog fightLog, Spell s, Ent target) {
      fightLog.addCommand(new SimpleCommand(null, new SimpleTargetable(null, new EffBill().mana(s.getBaseCost()).bEff())), false);
      fightLog.addCommand(new AbilityCommand(s, target), false);
   }

   public static void hit(FightLog fightLog, Ent target, Personal personal, boolean future) {
      fightLog.addCommand(new SimpleCommand(target, new SimpleTargetable(null, new EffBill().buff(new Buff(personal)).bEff())), future);
   }

   public static void rollDamage(FightLog fightLog, Ent source, Ent target, int damage, boolean future) {
      rollHit(fightLog, source, target, ESB.dmg.val(damage), future);
   }

   public static void rollHit(FightLog fightLog, Ent source, Ent target, EntSide side) {
      rollHit(fightLog, source, target, side, false);
   }

   public static void rollHit(FightLog fightLog, Ent source, Ent target, EntSide side, boolean future) {
      turnInto(fightLog, source, side, future);
      fightLog.addCommand(source.getDie().getTargetable(), target, future);
   }

   public static void roll(FightLog fightLog, Ent source, Ent target, int side, boolean future) {
      fightLog.addCommand(new DieCommand(new DieTargetable(source, side), target), future);
   }

   public static void turnInto(FightLog fightLog, Ent source, EntSide side, boolean future) {
      fightLog.addCommand(
         new SimpleCommand(source, new SimpleTargetable(null, new EffBill().buff(new Buff(new TestAffectSides(new ReplaceWith(side)))).bEff())), future
      );
      source.getDie().setSide(0);
   }

   public static void turnInto(FightLog fightLog, Ent source, EntSide side) {
      turnInto(fightLog, source, side, false);
   }

   public static void hit(FightLog fightLog, Ent target, Eff eff) {
      hit(fightLog, target, eff, false);
   }

   public static void hit(FightLog fightLog, Ent target, Eff eff, boolean future) {
      fightLog.addCommand(new SimpleCommand(target, new SimpleTargetable(null, eff)), future);
   }

   public static void addTrigger(FightLog f, Ent target, Personal personal) {
      f.addCommand(new SimpleCommand(target, new SimpleTargetable(null, new EffBill().buff(new Buff(personal)).bEff())), false);
   }

   public static EntState getState(FightLog fightLog, Ent ent) {
      return getState(fightLog, ent, FightLog.Temporality.Present);
   }

   public static EntState getState(FightLog fightLog, Ent ent, FightLog.Temporality temporality) {
      return fightLog.getState(temporality, ent);
   }

   public static boolean undo(FightLog fightLog, boolean force) {
      return fightLog.undo(force);
   }

   public static boolean undo(FightLog fightLog) {
      return undo(fightLog, true);
   }

   public static int countDyingHeroes(FightLog fightLog) {
      int dead = 0;

      for (Hero h : heroes) {
         if (getState(fightLog, h, FightLog.Temporality.Future).isDead()) {
            dead++;
         }
      }

      return dead;
   }

   public static String hashDeathState(FightLog fightLog, List<? extends Ent> entities) {
      String hash = "";

      for (Ent h : entities) {
         hash = hash + (getState(fightLog, h, FightLog.Temporality.Future).isDead() ? "d" : "a");
      }

      return hash;
   }

   public static void nextTurn(FightLog f) {
      f.resetTurn(true);
   }

   public static FightLog loadFromString(String paste) {
      SaveState result = SaveState.loadPasteModeString(paste, true);
      DungeonScreen ds = result.makeDungeonScreen();
      DungeonScreen.clearStaticReference();
      FightLog f = ds.getFightLog();
      if (f.isFailed()) {
         throw new RuntimeException("Failed to load test save from " + paste);
      } else {
         return f;
      }
   }

   public static boolean shouldCrash() {
      return TestRunner.isTesting();
   }

   public static Actor fpsPanel() {
      Pixl p = new Pixl(1, 3).border(Colours.pink);
      int w = (int)(com.tann.dice.Main.width * 0.5F);

      for (StandardButton miniPanelAction : miniPanelActions()) {
         p.actor(miniPanelAction, w);
      }

      return p.pix();
   }

   private static String exportName(String tag) {
      return new SimpleDateFormat("MM-dd hhmm").format(new Date()) + "-" + "3.2.13" + "-" + tag + "-" + Tann.randomString(5) + ".txt";
   }

   private static FileHandle fh(String filename) {
      return Gdx.files.absolute("C:\\code\\games\\Dicegeon\\randombits\\exports\\" + filename);
   }

   private static List<StandardButton> miniPanelActions() {
      List<StandardButton> b = new ArrayList<>(
         Arrays.asList(
            new StandardButton("[notranslate]langaci")
               .setRunnable(
                  new Runnable() {
                     @Override
                     public void run() {
                        Pixl p = new Pixl(2);
                        List<String> ignored = Arrays.asList(
                           "togtime",
                           "togtarg",
                           "togfri",
                           "togvis",
                           "togres",
                           "togresn",
                           "togress",
                           "togresa",
                           "togresx",
                           "togreso",
                           "togeft",
                           "togpip",
                           "togkey",
                           "togunt",
                           "togorf",
                           "cleardesc",
                           "clearicon",
                           "rgreen",
                           "Idol of Chrzktx",
                           "Idol of Pythagoras",
                           "Idol of Aiiu",
                           "False Idol"
                        );

                        for (Item item : ItemLib.getMasterCopy()) {
                           if (!ignored.contains(item.getName())) {
                              p.actor(item.makeChoosableActor(true, 0), com.tann.dice.Main.width - 50);
                           }
                        }

                        com.tann.dice.Main.getCurrentScreen().pushAndCenter(Tann.makeScrollpaneIfNecessary(p.pix()));
                        Sounds.playSound(Tann.pick(Sounds.arrowFly, Sounds.deathAlien, Sounds.bats));
                     }
                  }
               ),
            new StandardButton("[notranslate]langam").setRunnable(new Runnable() {
               @Override
               public void run() {
                  Pixl p = new Pixl(2);

                  for (Modifier modifier : ModifierLib.getAll()) {
                     p.actor(modifier.makeChoosableActor(true, 0), com.tann.dice.Main.width);
                  }

                  com.tann.dice.Main.getCurrentScreen().pushAndCenter(Tann.makeScrollpaneIfNecessary(p.pix()));
                  Sounds.playSound(Tann.pick(Sounds.arrowFly, Sounds.deathAlien, Sounds.bats));
               }
            }),
            new StandardButton("[notranslate]langachero").setRunnable(new Runnable() {
               @Override
               public void run() {
                  Pixl p = new Pixl(2);

                  for (EntType entType : HeroTypeLib.getMasterCopy()) {
                     p.actor(entType.makeUnlockActor(true), com.tann.dice.Main.width);

                     for (Trait trait : entType.traits) {
                        p.actor(new Explanel(trait, entType.makeEnt(), com.tann.dice.Main.width * 0.4F), com.tann.dice.Main.width);
                     }
                  }

                  com.tann.dice.Main.getCurrentScreen().pushAndCenter(Tann.makeScrollpaneIfNecessary(p.pix()));
                  Sounds.playSound(Tann.pick(Sounds.arrowFly, Sounds.deathAlien, Sounds.bats));
               }
            }),
            new StandardButton("[notranslate]langacmonster").setRunnable(new Runnable() {
               @Override
               public void run() {
                  Pixl p = new Pixl(2);

                  for (EntType entType : MonsterTypeLib.getAllValidMonsters()) {
                     p.actor(entType.makeUnlockActor(true), com.tann.dice.Main.width);
                  }

                  com.tann.dice.Main.getCurrentScreen().pushAndCenter(Tann.makeScrollpaneIfNecessary(p.pix()));
                  Sounds.playSound(Tann.pick(Sounds.arrowFly, Sounds.deathAlien, Sounds.bats));
               }
            }),
            new StandardButton("[notranslate]langaceff").setRunnable(new Runnable() {
               @Override
               public void run() {
                  Pixl p = new Pixl(2);

                  for (EntType entType : EntTypeUtils.getAll()) {
                     for (EntSide side : entType.sides) {
                        p.actor(new Explanel(side, null), com.tann.dice.Main.width);
                     }
                  }

                  com.tann.dice.Main.getCurrentScreen().pushAndCenter(Tann.makeScrollpaneIfNecessary(p.pix()));
                  Sounds.playSound(Tann.pick(Sounds.arrowFly, Sounds.deathAlien, Sounds.bats));
               }
            }),
            new StandardButton("[notranslate]langacal").setRunnable(new Runnable() {
               @Override
               public void run() {
                  Pixl p = new Pixl(2);

                  for (EntType entType : EntTypeUtils.getAll()) {
                     p.actor(entType.makeUnlockActor(true), com.tann.dice.Main.width);
                  }

                  com.tann.dice.Main.getCurrentScreen().pushAndCenter(Tann.makeScrollpaneIfNecessary(p.pix()));
                  Sounds.playSound(Tann.pick(Sounds.arrowFly, Sounds.deathAlien, Sounds.bats));
               }
            }),
            new StandardButton("[notranslate]chiev").setRunnable(new Runnable() {
               @Override
               public void run() {
                  com.tann.dice.Main.self().masterStats.getUnlockManager().achieveRandom();
                  Sounds.playSound(Sounds.slime);
               }
            }),
            new StandardButton("[notranslate]exportAllStrings").setRunnable(new Runnable() {
               @Override
               public void run() {
                  String filename = TestUtils.exportName("strings");
                  FileHandle fh = TestUtils.fh(filename);
                  fh.writeString(DebugUtilsUseful.getAllStrings(), true);
                  com.tann.dice.Main.getCurrentScreen().showDialog("exported files.local/" + filename);
               }
            }),
            new StandardButton("[notranslate]balstring").setRunnable(new Runnable() {
               @Override
               public void run() {
                  String filename = TestUtils.exportName("bal");
                  FileHandle fh = TestUtils.fh(filename);
                  fh.writeString(DebugUtilsUseful.getBalString(), true);
                  com.tann.dice.Main.getCurrentScreen().showDialog("exported files.local/" + filename);
               }
            }),
            new StandardButton("[notranslate]shd rld").setRunnable(new Runnable() {
               @Override
               public void run() {
                  BulletStuff.initShader();
               }
            }),
            new StandardButton("[notranslate]perf").setRunnable(new Runnable() {
               @Override
               public void run() {
                  TestUtils.runPerfTest();
               }
            }),
            new StandardButton("[notranslate]metaf").setRunnable(new Runnable() {
               @Override
               public void run() {
                  Sounds.playSound(Sounds.stealth);
                  System.out.println(FacadeUtils.metacodeFolders());
               }
            }),
            new StandardButton("[notranslate]op").setRunnable(new Runnable() {
               @Override
               public void run() {
                  Actor a = OptionsMenu.boxType(OptionUtils.EscBopType.Debug);
                  a = Tann.makeScrollpaneIfNecessary(a);
                  com.tann.dice.Main.getCurrentScreen().pushAndCenter(a);
               }
            })
         )
      );

      for (final TestRunner.TestType value : TestRunner.TestType.values()) {
         StandardButton sb = new StandardButton("[notranslate]" + value + "").setRunnable(new Runnable() {
            @Override
            public void run() {
               TestRunner.runTests(value);
            }
         });
         b.add(sb);
      }

      return b;
   }

   private static void debugSetSav(String key, String text) {
      String existing = Prefs.getString(key, "");
      Prefs.setString(key, text);
      com.tann.dice.Main.getCurrentScreen()
         .pushAndCenter(
            new Pixl(3, 4)
               .border(Colours.pink)
               .text("save " + key + " edited successfully")
               .row()
               .text("[grey]from " + existing.length() + " to " + text.length())
               .pix()
         );
   }

   private static void balData(List<String> data) {
      Map<String, Integer> map = new HashMap<>();

      for (String s : data) {
         if (map.get(s) == null) {
            map.put(s, 0);
         }

         map.put(s, map.get(s) + 1);
      }

      List<Entry<String, Integer>> entries = new ArrayList<>(map.entrySet());
      Collections.sort(entries, new Comparator<Entry<String, Integer>>() {
         public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
            return o2.getValue() - o1.getValue();
         }
      });
      System.out.println(entries);
   }

   private static void runPerfTest() {
      long t = System.currentTimeMillis();
      System.out.println("Testing perf");
      int toGenerate = 1000;

      for (int i = 0; i < 1000; i++) {
         PipeHeroGenerated.generate(HeroCol.randomUnlockedBasic(), Tann.randomInt(10), Tann.randomInt(99999));
      }

      long tt = System.currentTimeMillis() - t;
      String complex = Tann.floatFormat((float)tt / 1000.0F);
      System.out.println("Took: " + tt + " (" + complex + ")");
   }
}
