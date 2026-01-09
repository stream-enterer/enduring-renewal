package com.tann.dice.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.EntSidesLib;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobHuge;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobSmall;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.cursed.BlursedConfig;
import com.tann.dice.gameplay.context.config.cursed.CurseConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.ClassicConfig;
import com.tann.dice.gameplay.context.config.misc.DebugConfig;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.effect.eff.VisualEffectType;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.TargetingRestriction;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.effect.targetable.ability.AbilityUtils;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellBill;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellLib;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.command.AbilityCommand;
import com.tann.dice.gameplay.mode.cursey.BlyptraMode;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.ModifierPickContext;
import com.tann.dice.gameplay.modifier.ModifierPickUtils;
import com.tann.dice.gameplay.modifier.ModifierType;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoicePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.LevelupHeroChoosable;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorDifficulty;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorLevelup;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorModifierPickAdvanced;
import com.tann.dice.gameplay.trigger.personal.IncomingEffBonus;
import com.tann.dice.gameplay.trigger.personal.OnRescue;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.gameplay.trigger.personal.eff.StartOfCombat;
import com.tann.dice.gameplay.trigger.personal.hp.MaxHP;
import com.tann.dice.gameplay.trigger.personal.immunity.HealImmunity;
import com.tann.dice.platform.control.desktop.DesktopControl;
import com.tann.dice.screens.dungeon.TargetingManager;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.test.util.Skip;
import com.tann.dice.test.util.Slow;
import com.tann.dice.test.util.Test;
import com.tann.dice.test.util.TestPlat;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.test.util.TestUtils;
import com.tann.dice.util.DebugUtilsUseful;
import com.tann.dice.util.Tann;
import com.tann.dice.util.tp.TP;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestBugRepro {
   @Test
   public static void afterCastBalanceCursedBolt() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      h.addItem(ItemLib.byName("cursed bolt"));
      TestUtils.hit(f, null, new EffBill().mana(5).bEff(), false);
      f.addCommand(new AbilityCommand(new SpellBill().title("d").cost(1).eff(new EffBill().damage(1).group().friendly()), null), false);
      TestRunner.assertEquals("hero should have taken 3 damage", h.entType.hp - 3, TestUtils.getState(f, h, FightLog.Temporality.Present).getHp());
      f.addCommand(new AbilityCommand(new SpellBill().title("e").cost(1).eff(new EffBill().heal(10).group().friendly()), null), false);
      TestRunner.assertEquals("hero should be on full hp because i am kind", h.entType.hp, TestUtils.getState(f, h, FightLog.Temporality.Present).getHp());
   }

   @Test
   public static void resurrectWand() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Fighter"), HeroTypeUtils.byName("Thief")}, new MonsterType[]{MonsterTypeLib.byName("testGoblin")}
      );
      Hero h = TestUtils.heroes.get(0);
      TestUtils.rollHit(f, h, null, ESB.wandMana.val(3), false);
      TestRunner.assertEquals("should have 3 mana", 3, f.getSnapshot(FightLog.Temporality.Present).getTotalMana());
      TestRunner.assertTrue("die side shouldn't be -1", h.getDie().getSideIndex() != -1);
      TestRunner.assertEquals(
         "side should be replaced with cross",
         EffType.Blank,
         TestUtils.getState(f, h, FightLog.Temporality.Present).getSideState(h.getDie().getCurrentSide()).getCalculatedEffect().getType()
      );
      TestUtils.hit(f, h, new EffBill().kill().bEff(), false);
      TestRunner.assertTrue("hero should be dead", TestUtils.getState(f, h, FightLog.Temporality.Present).isDead());
      TestUtils.hit(f, null, new EffBill().resurrect(1).friendly().bEff(), false);
      TestRunner.assertTrue("hero should be alive", !TestUtils.getState(f, h, FightLog.Temporality.Present).isDead());
      TestRunner.assertEquals(
         "side should be back from cross-hood",
         EffType.Damage,
         TestUtils.getState(f, h, FightLog.Temporality.Present).getSideState(h.getDie().getCurrentSide()).getCalculatedEffect().getType()
      );
   }

   @Test
   @Skip
   public static void doorBug() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Defender")},
         new MonsterType[]{
            MonsterTypeLib.byName("testGoblin"),
            MonsterTypeLib.byName("testGoblin"),
            MonsterTypeLib.byName("testGoblin"),
            MonsterTypeLib.byName("testGoblin"),
            MonsterTypeLib.byName("testGoblin")
         }
      );
      Hero h = TestUtils.heroes.get(0);
      EntSide es = h.getSides()[2];
      TestRunner.assertEquals("should be shield side", EffType.Shield, es.getBaseEffect().getType());
      int val = es.getBaseEffect().getValue();
      EntState state = TestUtils.getState(f, h, FightLog.Temporality.Present);
      TestRunner.assertEquals("should be buffed by 2", val + 2, state.getSideState(es).getCalculatedEffect().getValue());
      TestUtils.hit(f, TestUtils.monsters.get(0), new EffBill().kill().bEff(), false);
      state = TestUtils.getState(f, h, FightLog.Temporality.Present);
      TestRunner.assertEquals("should still be buffed by 2", val + 2, state.getSideState(es).getCalculatedEffect().getValue());
      TestUtils.hit(f, TestUtils.monsters.get(1), new EffBill().kill().bEff(), false);
      state = TestUtils.getState(f, h, FightLog.Temporality.Present);
      TestRunner.assertEquals("should not be buffed by 2", val, state.getSideState(es).getCalculatedEffect().getValue());
   }

   @Test
   @Slow
   public static void testModifiersInFights() {
      List<Modifier> toTest = new ArrayList<>();
      toTest.addAll(ModifierLib.getAll());
      SpriteBatch batch = new SpriteBatch();
      batch.begin();
      int MONSTER_TESTS = 3;

      for (Modifier m : toTest) {
         if (!m.skipTest()) {
            testSingleModifier(m, 3, batch);
         }
      }

      batch.end();
      batch.dispose();
   }

   @Test
   @Slow
   public static void testDoomPP() {
      SpriteBatch batch = new SpriteBatch();
      batch.begin();
      testSingleModifier(ModifierLib.byName("Doom++"), 10, batch);
      batch.end();
      batch.dispose();
   }

   public static void testSingleModifier(Modifier m, int numMonsterTests, Batch batch) {
      try {
         List<MonsterType> masterCopy = MonsterTypeLib.getMasterCopy();

         for (int testIndex = 0; testIndex < numMonsterTests; testIndex++) {
            MonsterType[] ms = new MonsterType[]{
               Tann.random(masterCopy), Tann.random(masterCopy), Tann.random(masterCopy), Tann.random(masterCopy), Tann.random(masterCopy)
            };
            FightLog f = TestUtils.setupFight(
               new HeroType[]{HeroTypeUtils.byName("veteran"), HeroTypeUtils.byName("Fighter"), HeroTypeUtils.byName("statue")}, ms, new Modifier[]{m}
            );
            String prefix = "Modifier: " + m.toString() + ", monster: " + TestUtils.monsters + " - ";
            TestRunner.assertEquals(prefix + "Should be 3 heroes in the fight", 3, f.getSnapshot(FightLog.Temporality.Present).getStates(true, false).size());
            TestRunner.assertTrue(
               prefix + "Should be 2-10 enemies in the fight",
               f.getSnapshot(FightLog.Temporality.Present).getStates(false, false).size() <= 10
                  && f.getSnapshot(FightLog.Temporality.Present).getStates(false, false).size() >= 2
            );
            TestUtils.hit(f, TestUtils.monsters.get(0), new EffBill().damage(1).bEff(), false);
            List<Ent> enemies = f.getSnapshot(FightLog.Temporality.Present).getEntities(false, false);
            Hero h1 = TestUtils.heroes.get(0);
            Hero h2 = TestUtils.heroes.get(1);

            for (int hitIndex = 0; hitIndex < 3 && enemies.size() > 2; hitIndex++) {
               Ent monstt = enemies.get(hitIndex);
               if (!f.get(h1, FightLog.Temporality.Present).isDead()
                  && !f.get(h2, FightLog.Temporality.Present).isDead()
                  && !f.getState(FightLog.Temporality.Present, monstt).isDead()) {
                  TestUtils.rollHit(f, hitIndex % 2 == 0 ? h1 : h2, enemies.get(hitIndex), ESB.dmg.val(1));
               }
            }

            TestRunner.assertTrue("Should not be defeat " + m, !f.getSnapshot(FightLog.Temporality.Present).isLoss());

            for (Ent de : f.getSnapshot(FightLog.Temporality.Present).getEntities(null, null)) {
               for (FightLog.Temporality tp : new FightLog.Temporality[]{
                  FightLog.Temporality.Visual, FightLog.Temporality.Present, FightLog.Temporality.Future
               }) {
                  de.setState(tp, f.getState(tp, de));
               }

               EntPanelCombat ep = new EntPanelCombat(de);
               ep.layout();
               ep.draw(batch, 1.0F);
            }
         }
      } catch (Exception var17) {
         Tann.assertTrue("Test single modifier crashed, " + m.getName(), false);
         var17.printStackTrace();
      }
   }

   @Test
   public static void healingImmunityPlusDrain() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      TestUtils.hit(f, h, new EffBill().damage(2).bEff(), false);
      TestRunner.assertEquals("hero should be damaged for 2", h.getHeroType().hp - 2, f.getState(FightLog.Temporality.Present, h).getHp());
      TestUtils.hit(f, h, new EffBill().buff(new Buff(new HealImmunity())).bEff(), false);
      TestUtils.hit(f, h, new EffBill().heal(3).bEff(), false);
      TestRunner.assertEquals("hero should be damaged for 2", h.getHeroType().hp - 2, f.getState(FightLog.Temporality.Present, h).getHp());
      TestUtils.hit(f, h, TestUtils.monsters.get(0), ESB.dmgSelfHeal.val(1), false);
      TestRunner.assertEquals("hero should be damaged for 2", h.getHeroType().hp - 2, f.getState(FightLog.Temporality.Present, h).getHp());
   }

   @Test
   public static void rescueSingleUse() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      TestUtils.addTrigger(f, h, new AffectSides(new AddKeyword(Keyword.rescue, Keyword.singleUse)));
      TestUtils.hit(f, h, new EffBill().damage(h.getHeroType().hp).bEff(), true);
      TestRunner.assertTrue("Player should be alive in the present", !f.getState(FightLog.Temporality.Present, h).isDead());
      TestRunner.assertTrue("Player should be dead in the future", f.getState(FightLog.Temporality.Future, h).isDead());
      TestUtils.rollHit(f, h, h, ESB.shield.val(1), false);
      TestRunner.assertTrue("Player should be alive in the future", !f.getState(FightLog.Temporality.Future, h).isDead());
      TestRunner.assertEquals(
         "Side should be replaced with nothing", EffType.Blank, f.getState(FightLog.Temporality.Present, h).getSideState(0).getCalculatedEffect().getType()
      );
      TestRunner.assertTrue("Side should not be used due to rescuing self", !f.getState(FightLog.Temporality.Present, h).isUsed());
   }

   @Test
   public static void basiliskSingleUse() {
      FightLog f = TestUtils.setupFight(new HeroType[]{HeroTypeUtils.byName("Fighter")}, new MonsterType[]{MonsterTypeLib.byName("basilisk")});
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.addTrigger(f, h, new AffectSides(new AddKeyword(Keyword.singleUse)));
      TestUtils.rollHit(f, h, m, ESB.dmgSelfShield.val(1), false);
      TestRunner.assertTrue("Side should be petrified", f.getState(FightLog.Temporality.Present, h).getSideState(0).describe().contains("petrified"));
      TestUtils.hit(f, h, new EffBill().heal(1).keywords(Keyword.cleanse).bEff(), false);
      TestRunner.assertEquals(
         "Side should be blank", EffType.Blank, f.getState(FightLog.Temporality.Present, h).getSideState(0).getCalculatedEffect().getType()
      );
   }

   @Test
   public static void basiliskRampage() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Fighter")}, new MonsterType[]{MonsterTypeLib.byName("basilisk"), MonsterTypeLib.byName("dragon")}
      );
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.rollHit(f, h, m, ESB.dmgRampage.val(100), false);
      TestRunner.assertTrue("Side should be petrified", f.getState(FightLog.Temporality.Present, h).getSideState(0).describe().contains("petrified"));
      TestRunner.assertTrue("Side should not be used due to killing basilisk", !f.getState(FightLog.Temporality.Present, h).isUsed());
   }

   @Test
   public static void regenBonusHealing() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      TestUtils.hit(f, h, new EffBill().damage(3).bEff(), false);
      TestRunner.assertEquals("Hero should be damage for 3", h.getHeroType().hp - 3, f.getState(FightLog.Temporality.Present, h).getHp());
      TestUtils.hit(f, h, new EffBill().heal(1).keywords(Keyword.regen).bEff(), false);
      TestRunner.assertEquals("Hero should be damage for 2", h.getHeroType().hp - 2, f.getState(FightLog.Temporality.Present, h).getHp());
      TestRunner.assertEquals("Hero should be damage for 1 in the future", h.getHeroType().hp - 1, f.getState(FightLog.Temporality.Future, h).getHp());
      TestUtils.addTrigger(f, h, new IncomingEffBonus(1, EffType.Heal));
      TestRunner.assertEquals("Hero should be damage for 0 in the future", h.getHeroType().hp, f.getState(FightLog.Temporality.Future, h).getHp());
      TestUtils.hit(f, h, new EffBill().damage(1).keywords(Keyword.poison).bEff(), false);
      TestRunner.assertEquals("Hero should be damage for 3", h.getHeroType().hp - 3, f.getState(FightLog.Temporality.Present, h).getHp());
      TestRunner.assertEquals("Hero should be damage for 2 in the future", h.getHeroType().hp - 2, f.getState(FightLog.Temporality.Future, h).getHp());
   }

   @Test
   public static void ironHeartTest() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      int bonus = 100;
      TestUtils.addTrigger(f, h, new OnRescue(new EffBill().self().buff(new Buff(new MaxHP(bonus))).bEff()));
      TestUtils.hit(f, h, new EffBill().damage(h.getHeroType().hp).bEff(), true);
      TestRunner.assertTrue("Hero should be dying", f.getState(FightLog.Temporality.Future, h).isDead());
      TestRunner.assertEquals("Hero should have unchanged max hp", h.getHeroType().hp, f.getState(FightLog.Temporality.Present, h).getMaxHp());
      TestUtils.rollHit(f, h, h, ESB.shield.val(1), false);
      TestRunner.assertTrue("Hero should not be dying", !f.getState(FightLog.Temporality.Future, h).isDead());
      TestRunner.assertEquals("Extra hp should have triggered", h.getHeroType().hp + bonus, f.getState(FightLog.Temporality.Present, h).getMaxHp());
      TestRunner.assertEquals("Extra hp should affect current hp", h.getHeroType().hp + bonus, f.getState(FightLog.Temporality.Present, h).getHp());
   }

   @Test
   public static void friendlyAbilitiesTestSpells() {
      for (Spell s : SpellLib.makeAllSpellsList()) {
         Boolean friendly = null;
         Eff e = s.getBaseEffect();
         if (e.getType() == EffType.Event && !e.isFriendly()) {
            throw new RuntimeException("unfriendly event in spell: " + s.getTitle());
         }
      }
   }

   @Test
   public static void friendlyAbilitiesTestSides() {
      List<EntSide> sides = EntSidesLib.getAllSidesWithValue();
      Field[] fields = ESB.class.getDeclaredFields();

      for (EntSide es : sides) {
         Boolean friendly = null;
         Eff e = es.getBaseEffect();
         if (e.getType() == EffType.Event) {
            if (!e.isFriendly()) {
               throw new RuntimeException("unfriendly event in ability: " + es.getTexture());
            }
         } else if (friendly == null) {
            friendly = e.isFriendly();
         } else if (friendly != e.isFriendly() && e.needsTarget() && e.getTargetingType() != TargetingType.Group && e.getTargetingType() != TargetingType.Self) {
            throw new RuntimeException("partially-friendly side: " + es.getTexture());
         }
      }
   }

   @Test
   @Skip
   public static void cleaveKillTargetLich() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Healer")},
         new MonsterType[]{MonsterTypeLib.byName("testGoblin"), MonsterTypeLib.byName("lich"), MonsterTypeLib.byName("testGoblin")}
      );
      Hero h = TestUtils.heroes.get(0);
      Monster lich = TestUtils.monsters.get(1);
      TestRunner.assertEquals("lich should be lich", MonsterTypeLib.byName("lich").getName(false), lich.name);
      TestUtils.rollHit(f, h, lich, ESB.headshot.val(5), false);
      TestRunner.assertEquals("lich should not be killed", false, f.get(lich, FightLog.Temporality.Present).isDead());
      TestRunner.assertEquals("goblins should not be killed", 3, f.getSnapshot(FightLog.Temporality.Present).getAliveMonsterStates().size());
      SimpleTargetable basicHeadshot = new SimpleTargetable(
         h, new EffBill().kill().restrict(TargetingRestriction.OrLessHp).keywords(Keyword.ranged).visual(VisualEffectType.Arrow).value(5).bEff()
      );
      TestRunner.assertEquals(
         "Should be two valid targets, the goblins",
         2,
         TargetingManager.getValidTargets(f.getSnapshot(FightLog.Temporality.Present), basicHeadshot, true).size()
      );
      basicHeadshot = new SimpleTargetable(
         h,
         new EffBill().kill().restrict(TargetingRestriction.OrLessHp).keywords(Keyword.ranged, Keyword.cleave).visual(VisualEffectType.Arrow).value(5).bEff()
      );
      TestRunner.assertEquals(
         "Should be three valid targets, the goblins and the lich",
         3,
         TargetingManager.getValidTargets(f.getSnapshot(FightLog.Temporality.Present), basicHeadshot, true).size()
      );
      TestUtils.addTrigger(f, h, new AffectSides(new AddKeyword(Keyword.cleave)));
      TestUtils.rollHit(f, h, lich, ESB.headshot.val(5), false);
      TestRunner.assertEquals("lich should not be killed", false, f.get(lich, FightLog.Temporality.Present).isDead());
      TestRunner.assertEquals("goblins should be killed", 1, f.getSnapshot(FightLog.Temporality.Present).getAliveMonsterStates().size());
   }

   @Test
   @Slow
   public static void ensureNoTargetingCrash() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Healer"), HeroTypeUtils.byName("Mage"), HeroTypeUtils.byName("Defender"), HeroTypeUtils.byName("Fighter")},
         new MonsterType[]{
            MonsterTypeLib.byName("testGoblin"), MonsterTypeLib.byName("testGoblin"), MonsterTypeLib.byName("testGoblin"), MonsterTypeLib.byName("hexia")
         }
      );
      TestUtils.attack(f, TestUtils.monsters.get(0), TestUtils.heroes.get(0), 3, false);
      TestUtils.attack(f, TestUtils.monsters.get(0), TestUtils.heroes.get(1), 1, false);
      TestUtils.attack(f, TestUtils.heroes.get(0), TestUtils.monsters.get(0), 3, false);
      TestUtils.attack(f, TestUtils.heroes.get(0), TestUtils.monsters.get(1), 1, false);
      TargetingManager tm = new TargetingManager(f);
      List<Ent> allEntities = new ArrayList<>();
      allEntities.addAll(TestUtils.monsters);
      allEntities.addAll(TestUtils.heroes);

      for (EntSide es : EntSidesLib.getAllSidesWithValue()) {
         SimpleTargetable st = new SimpleTargetable(TestUtils.heroes.get(0), es.getBaseEffect());
         TargetingManager.getValidTargets(f.getSnapshot(FightLog.Temporality.Present), st, true);
         TargetingManager.getValidTargets(f.getSnapshot(FightLog.Temporality.Present), st, false);
         if (es.getBaseEffect().needsTarget()) {
            for (Ent de : allEntities) {
               TestUtils.rollHit(f, TestUtils.heroes.get(0), de, es, false);
               TestUtils.undo(f);
               TestUtils.undo(f);
            }

            for (Ent de : allEntities) {
               tm.getInvalidTargetReason(de, st, true);
            }
         } else {
            TestUtils.rollHit(f, TestUtils.heroes.get(0), null, es, false);
            TestUtils.undo(f);
            TestUtils.undo(f);
         }
      }

      for (Spell s : SpellLib.makeAllSpellsList()) {
         TargetingManager.getValidTargets(f.getSnapshot(FightLog.Temporality.Present), s, true);
         if (s.getBaseEffect().needsTarget()) {
            for (Ent de : allEntities) {
               TestUtils.hit(f, null, new EffBill().mana(999).bEff(), false);
               f.addCommand(new AbilityCommand(s, de), false);
               TestUtils.undo(f);
            }

            for (Ent de : allEntities) {
               tm.getInvalidTargetReason(de, s, true);
            }
         } else {
            f.addCommand(new AbilityCommand(s, null), false);
            TestUtils.undo(f);
         }
      }
   }

   @Test
   public static void lichShuriken() {
      Hero h = new Hero(HeroTypeUtils.byName("Healer"));
      h.addItem(new ItBill(1, "ultra-shuriken").prs(new StartOfCombat(new EffBill().damage(10).targetType(TargetingType.Top).bEff())).bItem());
      FightLog f = TestUtils.setupFight(Arrays.asList(h), Arrays.asList(new Monster(MonsterTypeLib.byName("lich"))), new Modifier[0]);
      TestRunner.assertEquals("bones should be summoned", 3, f.getSnapshot(FightLog.Temporality.Present).getStates(false, null).size());
   }

   @Test
   public static void selfKillCleave() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{
            HeroTypeUtils.byName("Mage"),
            HeroTypeUtils.byName("Healer"),
            HeroTypeUtils.byName("Defender"),
            HeroTypeUtils.byName("Fighter"),
            HeroTypeUtils.byName("Thief")
         },
         new MonsterType[]{MonsterTypeLib.byName("testGoblin")}
      );
      TestUtils.rollHit(f, TestUtils.heroes.get(0), TestUtils.heroes.get(1), ESB.shieldCleave.val(1), false);

      for (int i = 0; i < 3; i++) {
         TestRunner.assertEquals("heroes should have 1 shield", 1, f.get(TestUtils.heroes.get(i), FightLog.Temporality.Present).getShields());
      }

      TestUtils.hit(f, TestUtils.heroes.get(1), new EffBill().kill().bEff(), false);
      TestUtils.rollHit(f, TestUtils.heroes.get(0), TestUtils.heroes.get(2), ESB.shieldCleave.val(1), false);
      TestRunner.assertEquals("bottom hero should have 2 shield", 2, f.get(TestUtils.heroes.get(0), FightLog.Temporality.Present).getShields());
      TestRunner.assertEquals("next hero should be dead with 1 shield", 1, f.get(TestUtils.heroes.get(1), FightLog.Temporality.Present).getShields());
      TestRunner.assertEquals("next hero should be dead with 1 shield", true, f.get(TestUtils.heroes.get(1), FightLog.Temporality.Present).isDead());
      TestRunner.assertEquals("middle hero should have 2 shields", 2, f.get(TestUtils.heroes.get(2), FightLog.Temporality.Present).getShields());
      TestRunner.assertEquals("next hero should have 1 shields", 1, f.get(TestUtils.heroes.get(3), FightLog.Temporality.Present).getShields());
      TestRunner.assertEquals("next hero should have 0 shields", 0, f.get(TestUtils.heroes.get(4), FightLog.Temporality.Present).getShields());
   }

   @Test
   public static void testShaderCompilation() {
      String vert = Gdx.files.internal("shader/dice/vertex.glsl").readString();
      String frag = Gdx.files.internal("shader/dice/fragment.glsl").readString();
      ShaderProgram s = new ShaderProgram(vert, frag);
      TestRunner.assertTrue("shaderprogram should compile", s.isCompiled());
      String log = s.getLog();
      System.out.println(log);
   }

   @Test
   public static void buffMergeUndo() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Fighter"), HeroTypeUtils.byName("Healer")}, new MonsterType[]{MonsterTypeLib.byName("testGoblin")}
      );
      Hero a = TestUtils.heroes.get(0);
      Hero b = TestUtils.heroes.get(1);
      TestUtils.rollHit(f, b, null, ESB.mana.val(1), false);
      TestRunner.assertEquals("should have 1 mana", 1, f.getSnapshot(FightLog.Temporality.Present).getTotalMana());
      TestUtils.rollHit(f, a, b, ESB.healBoost.val(1), false);
      TestUtils.rollHit(f, a, b, ESB.healBoost.val(1), false);
      TestUtils.rollHit(f, b, null, ESB.mana.val(1), false);
      TestRunner.assertEquals("should have 4 mana", 4, f.getSnapshot(FightLog.Temporality.Present).getTotalMana());
      TestUtils.undo(f);
      TestUtils.undo(f);
      TestRunner.assertEquals("should have 1 mana", 1, f.getSnapshot(FightLog.Temporality.Present).getTotalMana());
      TestUtils.undo(f);
      TestUtils.undo(f);
      TestUtils.rollHit(f, b, null, ESB.mana.val(1), false);
      TestRunner.assertEquals("should have 3 mana", 3, f.getSnapshot(FightLog.Temporality.Present).getTotalMana());
   }

   @Test
   public static void cleanseRemoveDebuffs() {
      FightLog f = TestUtils.setupFight();
      int triggers = f.getState(FightLog.Temporality.Present, TestUtils.heroes.get(0)).getActivePersonals().size();
      TestUtils.hit(f, TestUtils.heroes.get(0), ESB.dmgPoison.val(1).getBaseEffect(), false);
      int newTriggers = f.getState(FightLog.Temporality.Present, TestUtils.heroes.get(0)).getActivePersonals().size();
      TestRunner.assertTrue("more triggers now", newTriggers > triggers);
      TestUtils.hit(f, TestUtils.heroes.get(0), EntSidesBlobSmall.curse.val(1).getBaseEffect(), false);
      int newNewTriggers = f.getState(FightLog.Temporality.Present, TestUtils.heroes.get(0)).getActivePersonals().size();
      TestRunner.assertTrue("more triggers now", newNewTriggers > newTriggers);
      TestUtils.hit(f, TestUtils.heroes.get(0), ESB.healCleanse.val(1).getBaseEffect(), false);
      int finalTriggers = f.getState(FightLog.Temporality.Present, TestUtils.heroes.get(0)).getActivePersonals().size();
      TestRunner.assertTrue("cleansed is now a trigger", finalTriggers == triggers + 1);
   }

   @Test
   public static void boostGivesTrigger() {
      FightLog f = TestUtils.setupFight();
      int triggers = f.getState(FightLog.Temporality.Present, TestUtils.heroes.get(0)).getActivePersonals().size();
      TestUtils.hit(f, TestUtils.heroes.get(0), ESB.healBoost.val(1).getBaseEffect(), false);
      int newTriggers = f.getState(FightLog.Temporality.Present, TestUtils.heroes.get(0)).getActivePersonals().size();
      TestRunner.assertTrue("more triggers now", newTriggers > triggers);
   }

   @Test
   public static void regenPersists() {
      FightLog f = TestUtils.setupFight();
      int initialTriggers = f.getState(FightLog.Temporality.Present, TestUtils.heroes.get(0)).getActivePersonals().size();
      TestRunner.assertTrue(
         "same triggers then", f.getState(FightLog.Temporality.Future, TestUtils.heroes.get(0)).getActivePersonals().size() == initialTriggers
      );
      TestUtils.hit(f, TestUtils.heroes.get(0), ESB.healRegen.val(1).getBaseEffect(), false);
      int newPresentTriggers = f.getState(FightLog.Temporality.Present, TestUtils.heroes.get(0)).getActivePersonals().size();
      TestRunner.assertTrue("more triggers now", newPresentTriggers > initialTriggers);
      int newFutureTriggers = f.getState(FightLog.Temporality.Future, TestUtils.heroes.get(0)).getActivePersonals().size();
      TestRunner.assertTrue("more triggers then", newFutureTriggers > initialTriggers);
   }

   @Test
   public static void cleanseRemovesWeaken() {
      FightLog f = TestUtils.setupFight();
      int initialTriggers = f.getState(FightLog.Temporality.Present, TestUtils.heroes.get(0)).getActivePersonals().size();
      TestUtils.hit(f, TestUtils.heroes.get(0), ESB.dmgWeaken.val(1).getBaseEffect(), false);
      int newPresentTriggers = f.getState(FightLog.Temporality.Present, TestUtils.heroes.get(0)).getActivePersonals().size();
      TestRunner.assertTrue("more triggers now", newPresentTriggers > initialTriggers);
      TestUtils.hit(f, TestUtils.heroes.get(0), ESB.healCleanse.val(1).getBaseEffect(), false);
      newPresentTriggers = f.getState(FightLog.Temporality.Present, TestUtils.heroes.get(0)).getActivePersonals().size();
      TestRunner.assertTrue("same triggers now", newPresentTriggers == initialTriggers + 1);
   }

   @Test
   public static void ogrePipsNotGoCrazy() {
      FightLog f = TestUtils.setupFight(new HeroType[]{HeroTypeUtils.byName("Healer")}, new MonsterType[]{MonsterTypeLib.byName("ogre")});
      int startingPower = f.getState(FightLog.Temporality.Present, TestUtils.monsters.get(0)).getSideState(0).getCalculatedEffect().getValue();
      TestUtils.hit(f, TestUtils.monsters.get(0), new EffBill().damage(1).bEff(), false);
      TestRunner.assertEquals(
         "should not change",
         startingPower,
         f.getState(FightLog.Temporality.Present, TestUtils.monsters.get(0)).getSideState(0).getCalculatedEffect().getValue()
      );
      TestUtils.hit(f, TestUtils.monsters.get(0), new EffBill().damage(4).bEff(), false);
      TestRunner.assertEquals(
         "should change by 1",
         startingPower + 1,
         f.getState(FightLog.Temporality.Present, TestUtils.monsters.get(0)).getSideState(0).getCalculatedEffect().getValue()
      );
      TestUtils.hit(f, TestUtils.monsters.get(0), new EffBill().damage(4).bEff(), false);
      TestRunner.assertEquals(
         "should change by 2",
         startingPower + 2,
         f.getState(FightLog.Temporality.Present, TestUtils.monsters.get(0)).getSideState(0).getCalculatedEffect().getValue()
      );
      TestUtils.undo(f);
      TestUtils.undo(f);
      TestUtils.hit(f, TestUtils.monsters.get(0), new EffBill().damage(4).bEff(), false);
      TestUtils.hit(f, TestUtils.monsters.get(0), new EffBill().damage(4).bEff(), false);
      TestUtils.undo(f);
      TestUtils.undo(f);
      TestUtils.hit(f, TestUtils.monsters.get(0), new EffBill().damage(4).bEff(), false);
      TestUtils.hit(f, TestUtils.monsters.get(0), new EffBill().damage(4).bEff(), false);
      TestRunner.assertEquals(
         "should change by 2",
         startingPower + 2,
         f.getState(FightLog.Temporality.Present, TestUtils.monsters.get(0)).getSideState(0).getCalculatedEffect().getValue()
      );
   }

   @Test
   public static void enchantedShieldUndo() {
      Hero h = new Hero(HeroTypeUtils.byName("Healer"));
      h.addItem(ItemLib.byName("Enchanted Shield"));
      FightLog f = TestUtils.setupFight(Arrays.asList(h), Arrays.asList(new Monster(MonsterTypeLib.byName("ogre"))), new Modifier[0]);
      TestRunner.assertEquals("should start at 1 shield", 1, f.getState(FightLog.Temporality.Present, h).getShields());
      TestUtils.hit(f, TestUtils.monsters.get(0), h, ESB.dmg.val(1), false);
      TestRunner.assertEquals("should be at 0 shields", 0, f.getState(FightLog.Temporality.Present, h).getShields());
      TestRunner.assertTrue("Undo should work", TestUtils.undo(f, true));
      TestRunner.assertEquals("should return to 1 shield", 1, f.getState(FightLog.Temporality.Present, h).getShields());
   }

   @Test
   public static void checkTotalPowerEquality() {
      List<HeroType> bads = new ArrayList<>();

      for (HeroType ht : new ArrayList<>(HeroTypeLib.getMasterCopy())) {
         String name = ht.getName(false);
         if (!ht.isMissingno()
            && !name.equalsIgnoreCase("roulette")
            && !name.equalsIgnoreCase("museum")
            && !name.equalsIgnoreCase("addSection(buttons")
            && ht.getTotalEffectTier() != ht.getTotalEffectTier()) {
            bads.add(ht);
         }
      }

      Tann.assertTrue("no bads: " + bads, bads.isEmpty());
   }

   @Test
   public static void vulnRevenge() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.hit(f, m, h, ESB.dmg.val(1), true);
      TestRunner.assertEquals("hero should be on full hp", h.entType.hp, TestUtils.getState(f, h).getHp());
      TestUtils.hit(f, h, m, ESB.dmgVuln.val(1), false);
      TestRunner.assertEquals("enemy should have taken 1 dmg", m.entType.hp - 1, TestUtils.getState(f, m).getHp());
      TestUtils.rollHit(f, h, h, ESB.shieldRepel.val(1), false);
      TestRunner.assertEquals("enemy should have taken 3 dmg", m.entType.hp - 3, TestUtils.getState(f, m).getHp());
   }

   @Test
   public static void equipCrash() {
      List<Item> bugged = new ArrayList<>();

      for (Item e : ItemLib.getMasterCopy()) {
         Hero h = new Hero(HeroTypeUtils.byName("Fighter"));
         h.addItem(e);

         try {
            for (EntSide es : h.getSides()) {
               h.getBlankState().getSideState(es).getCalculatedEffect().getValue();
            }
         } catch (Exception var8) {
            var8.printStackTrace();
            bugged.add(e);
         }
      }

      Tann.assertTrue(bugged.toString(), bugged.isEmpty());
   }

   @Test
   public static void basicInspire() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Fighter"), HeroTypeUtils.byName("Mage")}, new MonsterType[]{MonsterTypeLib.byName("testGoblin")}
      );
      Hero h0 = TestUtils.heroes.get(0);
      Hero h1 = TestUtils.heroes.get(1);
      TestUtils.turnInto(f, h1, ESB.dmg.val(1).withKeyword(Keyword.inspired), false);
      Tann.assertEquals("inspire sword should be unbuffed", 1, TestUtils.getState(f, h1).getSideState(0).getCalculatedEffect().getValue());
      TestUtils.rollHit(f, h0, h1, ESB.shield.val(1), false);
      Tann.assertEquals("inspire sword should be unbuffed", 1, TestUtils.getState(f, h1).getSideState(0).getCalculatedEffect().getValue());
      TestUtils.rollHit(f, h0, h1, ESB.shield.val(2), false);
      Tann.assertEquals("inspire sword should be buffed", 2, TestUtils.getState(f, h1).getSideState(0).getCalculatedEffect().getValue());
   }

   @Test
   public static void exertInspire() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Fighter"), HeroTypeUtils.byName("Mage")}, new MonsterType[]{MonsterTypeLib.byName("testGoblin")}
      );
      Hero h0 = TestUtils.heroes.get(0);
      Hero h1 = TestUtils.heroes.get(1);
      TestUtils.turnInto(f, h1, ESB.dmg.val(1).withKeyword(Keyword.inspired), false);
      Tann.assertEquals("inspire sword should be unbuffed", 1, TestUtils.getState(f, h1).getSideState(0).getCalculatedEffect().getValue());
      TestUtils.rollHit(f, h0, h1, ESB.shield.val(1), false);
      Tann.assertEquals("inspire sword should be unbuffed", 1, TestUtils.getState(f, h1).getSideState(0).getCalculatedEffect().getValue());
      TestUtils.rollHit(f, h0, h1, ESB.shield.val(2).withKeyword(Keyword.exert), false);
      Tann.assertEquals("inspire sword should be buffed", 2, TestUtils.getState(f, h1).getSideState(0).getCalculatedEffect().getValue());
   }

   @Test
   public static void enemyHeavyHealed() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Fighter"), HeroTypeUtils.byName("statue")}, new MonsterType[]{MonsterTypeLib.byName("testGoblin")}
      );
      Hero h0 = TestUtils.heroes.get(0);
      Hero statue = TestUtils.heroes.get(1);
      Monster m = TestUtils.monsters.get(0);
      int dmg = 2;
      TestUtils.rollHit(f, m, statue, ESB.dmgHeavy.val(dmg), true);
      Tann.assertEquals("Statue should be on full hp", statue.getHeroType().hp, TestUtils.getState(f, statue).getHp());
      Tann.assertEquals("Statue should have incoming damage", statue.getHeroType().hp - dmg, TestUtils.getState(f, statue, FightLog.Temporality.Future).getHp());
      int pain = 16;
      TestUtils.rollHit(f, statue, null, ESB.manaPain.val(pain), false);
      Tann.assertEquals("Statue should be on n-p hp", statue.getHeroType().hp - pain, TestUtils.getState(f, statue).getHp());
      Tann.assertEquals(
         "Statue should have incoming damage", statue.getHeroType().hp - pain - dmg, TestUtils.getState(f, statue, FightLog.Temporality.Future).getHp()
      );
   }

   @Test
   public static void enemyPainShield() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.hit(f, m, new EffBill().shield(1).bEff(), false);
      Tann.assertEquals("Monster should have 1 shield", 1, TestUtils.getState(f, m).getShields());
      TestUtils.hit(f, m, h, ESB.dmgPain.val(2), true);
      Tann.assertEquals("Hero should be undamaged in present", h.getHeroType().hp, TestUtils.getState(f, h, FightLog.Temporality.Present).getHp());
      Tann.assertEquals("Hero should be damaged for 2", h.getHeroType().hp - 2, TestUtils.getState(f, h, FightLog.Temporality.Future).getHp());
      Tann.assertEquals("Monster should be be damaged for 1", m.getEntType().hp - 1, TestUtils.getState(f, m, FightLog.Temporality.Future).getHp());
   }

   @Test
   @TestPlat(platformClass = DesktopControl.class)
   public static void androidSoundCapitalisation() {
      List<String> badPaths = new ArrayList<>();

      for (String s : Sounds.allStrings) {
         FileHandle a = Gdx.files.internal(s);
         String filename = a.name();
         String folder = a.path().substring(0, a.path().length() - filename.length() - 1);
         boolean found = false;

         for (File f : new File(folder).listFiles()) {
            if (f.getName().equals(filename)) {
               found = true;
               break;
            }

            if (f.getName().equalsIgnoreCase(filename)) {
               System.out.println("probably miscapitalisation - " + f.getName() + "  -  " + filename);
               break;
            }
         }

         if (!found) {
            badPaths.add(filename);
         }
      }

      Tann.assertTrue("no bad paths", badPaths.size() == 0);
   }

   @Test
   public static void cantripPair() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.turnInto(f, h, ESB.dmgCantrip.val(1).withKeyword(Keyword.pair));
      TestUtils.roll(f, h, m, 0, false);
      Tann.assertEquals("Monster should have taken 1 damage", m.entType.hp - 1, TestUtils.getState(f, m).getHp());
      TestUtils.roll(f, h, m, 0, false);
      Tann.assertEquals("Monster should have taken 3 damage", m.entType.hp - 3, TestUtils.getState(f, m).getHp());
   }

   @Test
   public static void boneMath() {
      MonsterType bones = MonsterTypeLib.byName("Test Bones");
      int numBones = 6;
      MonsterType[] bs = new MonsterType[numBones];
      Arrays.fill(bs, bones);
      FightLog f = TestUtils.setupFight(bs);
      Hero h = TestUtils.heroes.get(0);
      Tann.assertEquals("Should be 6 bones", numBones, f.getSnapshot(FightLog.Temporality.Present).getEntities(false, false).size());
      TestUtils.rollHit(f, h, TestUtils.monsters.get(1), ESB.dmg.val(4), false);
      Tann.assertEquals("Should be 5 bones", numBones - 1, f.getSnapshot(FightLog.Temporality.Present).getEntities(false, false).size());
      Tann.assertEquals("First bones should be damaged", 3, TestUtils.getState(f, TestUtils.monsters.get(0)).getHp());
      Tann.assertEquals("Second bones should be dead", true, TestUtils.getState(f, TestUtils.monsters.get(1)).isDead());
      Tann.assertEquals("Third bones should be damaged", 3, TestUtils.getState(f, TestUtils.monsters.get(2)).getHp());
      TestUtils.rollHit(f, h, TestUtils.monsters.get(0), ESB.dmg.val(2), false);
      TestUtils.rollHit(f, h, TestUtils.monsters.get(2), ESB.dmg.val(2), false);
      TestUtils.rollHit(f, h, TestUtils.monsters.get(3), ESB.dmg.val(2), false);
      Tann.assertEquals("Should be 5 bones", numBones - 1, f.getSnapshot(FightLog.Temporality.Present).getEntities(false, false).size());
      Tann.assertEquals("First bones should be on 1hp", 1, TestUtils.getState(f, TestUtils.monsters.get(0)).getHp());
      Tann.assertEquals("Third bones should be on 1hp", 1, TestUtils.getState(f, TestUtils.monsters.get(2)).getHp());
      Tann.assertEquals("4th bones should be on 2hp", 2, TestUtils.getState(f, TestUtils.monsters.get(3)).getHp());
      Tann.assertEquals("5th bones should be on 4hp", 4, TestUtils.getState(f, TestUtils.monsters.get(4)).getHp());
      Tann.assertEquals("6th bones should be on 4hp", 4, TestUtils.getState(f, TestUtils.monsters.get(5)).getHp());
      TestUtils.rollHit(f, h, TestUtils.monsters.get(2), ESB.dmg.val(1), false);
      Tann.assertEquals("Should be 2 bones", 2, f.getSnapshot(FightLog.Temporality.Present).getEntities(false, false).size());
      Tann.assertEquals("5th bones should be on 3hp", 3, TestUtils.getState(f, TestUtils.monsters.get(4)).getHp());
      Tann.assertEquals("6th bones should be on 4hp", 4, TestUtils.getState(f, TestUtils.monsters.get(5)).getHp());
      TestUtils.undo(f);
      TestUtils.rollHit(f, h, TestUtils.monsters.get(0), ESB.dmg.val(1), false);
      Tann.assertEquals("Should be 3 bones", 3, f.getSnapshot(FightLog.Temporality.Present).getEntities(false, false).size());
      Tann.assertEquals("4th bones should be on 1hp", 1, TestUtils.getState(f, TestUtils.monsters.get(3)).getHp());
      Tann.assertEquals("5th bones should be on 4hp", 4, TestUtils.getState(f, TestUtils.monsters.get(4)).getHp());
      Tann.assertEquals("6th bones should be on 4hp", 4, TestUtils.getState(f, TestUtils.monsters.get(5)).getHp());
   }

   @Test
   public static void hourglassReedsOrdering() {
      MonsterType testGob = MonsterTypeLib.byName("test goblin");
      int sideIndex = 2;
      Hero h = HeroTypeUtils.byName("Fighter").makeEnt();
      h.addItem(ItemLib.byName("hourglass"));
      FightLog f = TestUtils.setupFight(Arrays.asList(h), Arrays.asList(testGob.makeEnt()), new Modifier[0]);
      Tann.assertEquals("value of left side should be 3", 3, TestUtils.getState(f, h).getSideState(2).getCalculatedEffect().getValue());
      h = HeroTypeUtils.byName("Fighter").makeEnt();
      h.addItem(ItemLib.byName("two reeds"));
      f = TestUtils.setupFight(Arrays.asList(h), Arrays.asList(testGob.makeEnt()), new Modifier[0]);
      Tann.assertEquals("value of left side should be 3", 3, TestUtils.getState(f, h).getSideState(2).getCalculatedEffect().getValue());
      h = HeroTypeUtils.byName("Fighter").makeEnt();
      h.addItem(ItemLib.byName("two reeds"));
      h.addItem(ItemLib.byName("hourglass"));
      f = TestUtils.setupFight(Arrays.asList(h), Arrays.asList(testGob.makeEnt()), new Modifier[0]);
      Tann.assertEquals("value of left side should be 4", 4, TestUtils.getState(f, h).getSideState(2).getCalculatedEffect().getValue());
      h = HeroTypeUtils.byName("Fighter").makeEnt();
      h.addItem(ItemLib.byName("hourglass"));
      h.addItem(ItemLib.byName("two reeds"));
      f = TestUtils.setupFight(Arrays.asList(h), Arrays.asList(testGob.makeEnt()), new Modifier[0]);
      Tann.assertEquals("value of left side should be 3", 3, TestUtils.getState(f, h).getSideState(2).getCalculatedEffect().getValue());
   }

   @Test
   public static void thimbleSelfPainShield() {
      for (boolean b : Tann.BOTH) {
         Hero h = HeroTypeUtils.byName("Fighter").makeEnt();
         if (b) {
            h.addItem(ItemLib.byName("thimble"));
         }

         int expectedShields = b ? 5 : 0;
         FightLog f = TestUtils.setupFight(h, MonsterTypeLib.byName("testGoblin"));
         TestUtils.rollHit(f, h, h, ESB.shield.val(5).withKeyword(Keyword.pain), false);
         TestRunner.assertEquals("Should be full hp", h.entType.hp, TestUtils.getState(f, h).getHp());
         TestRunner.assertEquals("Should have " + expectedShields + " shields", expectedShields, TestUtils.getState(f, h).getShields());
      }
   }

   @Test
   public static void slateVulnerable() {
      FightLog f = TestUtils.setupFight(MonsterTypeLib.byName("slate"));
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestRunner.assertEquals("slate should be at full hp", m.entType.hp, TestUtils.getState(f, m).getHp());
      TestUtils.rollHit(f, h, m, ESB.dmgVuln.val(2));
      TestRunner.assertEquals("slate should be at n-1 hp", m.entType.hp - 1, TestUtils.getState(f, m).getHp());
      TestUtils.rollHit(f, h, m, ESB.dmg.val(2));
      TestRunner.assertEquals("slate should be at n-2 hp", m.entType.hp - 2, TestUtils.getState(f, m).getHp());
   }

   @Test
   public static void cleaveHeavy() {
      FightLog f = TestUtils.setupFight(MonsterTypeLib.byName("rat"), MonsterTypeLib.byName("troll"));
      Hero h = TestUtils.heroes.get(0);
      Monster rat = TestUtils.monsters.get(0);
      Monster troll = TestUtils.monsters.get(1);
      TestUtils.rollHit(f, h, troll, ESB.dmgCleave.val(1).withKeyword(Keyword.heavy));
      TestRunner.assertEquals("Troll should take 1dmg", troll.entType.hp - 1, TestUtils.getState(f, troll).getHp());
      TestRunner.assertEquals("Rat should take 1dmg", rat.entType.hp - 1, TestUtils.getState(f, rat).getHp());
   }

   @Test
   public static void checkIfDebugHeroesGetOffered() {
      for (int partyTier = 0; partyTier < 30; partyTier++) {
         DungeonContext dc = new DungeonContext(new DebugConfig(), Party.generate(partyTier), 1);
         List<TP<Hero, HeroType>> results = PhaseGeneratorLevelup.getLevelupOptions(dc, 2);
         TestRunner.assertEquals("should be 2 results offered", 2, results.size());

         for (TP<Hero, HeroType> entry : results) {
            TestRunner.assertEquals("Should not be debug hero offered: " + entry, false, entry.b.isMissingno());
         }
      }
   }

   @Test
   public static void cleansePetrifyOrder() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      firstNSidesPetrified(TestUtils.getState(f, h), 0);
      TestUtils.hit(f, m, h, EntSidesBlobSmall.petrify.val(1));
      firstNSidesPetrified(TestUtils.getState(f, h), 1);
      TestUtils.hit(f, m, h, EntSidesBlobSmall.petrify.val(1));
      firstNSidesPetrified(TestUtils.getState(f, h), 2);
      TestUtils.hit(f, h, h, ESB.healCleanse.val(1));
      firstNSidesPetrified(TestUtils.getState(f, h), 1);
      TestUtils.hit(f, m, h, EntSidesBlobSmall.petrify.val(2));
      firstNSidesPetrified(TestUtils.getState(f, h), 3);
   }

   private static void firstNSidesPetrified(EntState es, int num) {
      for (int i = 0; i < 6; i++) {
         boolean shouldBe = num > i;
         TestRunner.assertEquals(
            (shouldBe ? "Should be " : "Should not be ") + "petrified",
            shouldBe,
            es.getSideState(SpecificSidesType.PetrifyOrder.sideIndices[i]).getCalculatedEffect().describe().contains("petrified")
         );
      }
   }

   @Test
   public static void witherNotHittingDeadHeroes() {
      FightLog f = TestUtils.setupFight(
         Arrays.asList(HeroTypeUtils.byName("Fighter").makeEnt()),
         Arrays.asList(MonsterTypeLib.byName("testGoblin").makeEnt()),
         new Modifier[]{ModifierLib.byName("Sandstorm^1")}
      );
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      int maxHp = h.entType.hp;
      TestRunner.assertEquals("Hero should be on full hp", maxHp, TestUtils.getState(f, h, FightLog.Temporality.Present).getHp());
      TestRunner.assertEquals("Hero should be taking damage from Sandstorm", maxHp - 1, TestUtils.getState(f, h, FightLog.Temporality.Future).getHp());
      TestUtils.rollHit(f, m, h, ESB.dmg.val(maxHp));
      TestRunner.assertEquals("Hero should be dying", true, TestUtils.getState(f, h, FightLog.Temporality.Future).isDead());
      TestRunner.assertEquals("Hero should be on -1 hp", -1, TestUtils.getState(f, h, FightLog.Temporality.Future).getHp());
   }

   @Test
   public static void witherNotHittingResurrectedHeroes() {
      FightLog f = TestUtils.setupFight(
         Arrays.asList(HeroTypeUtils.byName("Fighter").makeEnt(), HeroTypeUtils.byName("Fighter").makeEnt()),
         Arrays.asList(MonsterTypeLib.byName("testGoblin").makeEnt()),
         new Modifier[]{ModifierLib.byName("Sandstorm^1")}
      );
      Hero h1 = TestUtils.heroes.get(0);
      Hero h2 = TestUtils.heroes.get(1);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.hit(f, h1, new EffBill().kill().bEff());
      TestUtils.nextTurn(f);
      TestRunner.assertEquals("h2 should have taken 2 damage at eot", h2.entType.hp - 2, TestUtils.getState(f, h2, FightLog.Temporality.Future).getHp());
      TestRunner.assertEquals("h1 should be dead", true, TestUtils.getState(f, h1).isDead());
      TestUtils.rollHit(f, h2, null, ESB.resurrect.val(1));
      TestRunner.assertEquals("h1 should be alive", false, TestUtils.getState(f, h1).isDead());
      TestRunner.assertEquals("h1 should have taken 1 damage at eot", h1.entType.hp - 1, TestUtils.getState(f, h1, FightLog.Temporality.Future).getHp());
   }

   @Test
   public static void tarantusHittingDeadHero() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Fighter"), HeroTypeUtils.byName("Thief"), HeroTypeUtils.byName("Mage")},
         new MonsterType[]{MonsterTypeLib.byName("tarantus")}
      );
      Hero h1 = TestUtils.heroes.get(0);
      Hero h2 = TestUtils.heroes.get(1);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.rollHit(f, h1, null, ESB.manaPain.val(10));
      TestRunner.assertTrue("h1 should be dead", TestUtils.getState(f, h1).isDead());
      TestRunner.assertTrue("h2 should be alive", !TestUtils.getState(f, h2).isDead());
      TestUtils.rollDamage(f, h2, m, m.entType.hp - 1, false);
      TestRunner.assertTrue("h2 should be dead", TestUtils.getState(f, h2).isDead());
   }

   @Test
   @Skip
   public static void cleaveHealHitsDeadHeroes() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Fighter"), HeroTypeUtils.byName("Thief"), HeroTypeUtils.byName("Mage")},
         new MonsterType[]{MonsterTypeLib.byName("tarantus")}
      );
      Hero h1 = TestUtils.heroes.get(0);
      Hero h2 = TestUtils.heroes.get(1);
      Hero h3 = TestUtils.heroes.get(2);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.rollHit(f, m, null, ESB.dmgAll.val(2), false);
      TestUtils.rollHit(f, m, h1, ESB.dmg.val(10), false);
      TestRunner.assertTrue("h1 should be dead", TestUtils.getState(f, h1).isDead());
      TestRunner.assertTrue("h2 should be damaged", TestUtils.getState(f, h2).isDamaged());
      TestRunner.assertTrue("h3 should be damaged", TestUtils.getState(f, h3).isDamaged());
      TestUtils.spell(f, HeroTypeUtils.byName("enchanter").traits.get(0).personal.getSpell(), null);
      TestRunner.assertTrue("h3 should be undamaged", !TestUtils.getState(f, h3).isDamaged());
      TestRunner.assertTrue("h2 should be undamaged", !TestUtils.getState(f, h2).isDamaged());
   }

   @Test
   public static void testRevengeCleanse() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Fighter"), HeroTypeUtils.byName("Thief"), HeroTypeUtils.byName("Mage")},
         new MonsterType[]{MonsterTypeLib.byName("tarantus")}
      );
      Hero h1 = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.rollHit(f, m, h1, EntSidesBlobHuge.chomp.val(1), true);
      TestUtils.rollHit(f, h1, h1, ESB.shieldRepel.val(1).withKeyword(Keyword.cleanse), false);
      TestRunner.assertTrue("m should be damaged", TestUtils.getState(f, m).isDamaged());
      TestUtils.rollHit(f, h1, m, ESB.dmgPoison.val(1), true);
      TestRunner.assertTrue("m should be poisoned", TestUtils.getState(f, m, FightLog.Temporality.Future).getPoisonDamageTaken() == 1);
   }

   @Test
   public static void chainShouldTriggerOnSelf() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Fighter"), HeroTypeUtils.byName("Thief"), HeroTypeUtils.byName("Mage")},
         new MonsterType[]{MonsterTypeLib.byName("tarantus")}
      );
      Hero h1 = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.turnInto(f, h1, ESB.dmgCleaveChain.val(1));
      TestUtils.roll(f, h1, m, 0, false);
      TestUtils.roll(f, h1, m, 0, false);
      TestRunner.assertEquals("m should have taken 3 damage", m.entType.hp - 3, TestUtils.getState(f, m).getHp());
   }

   @Test
   public static void chainShouldOnlyTriggerOnSelfWhenAppropriate() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Fighter"), HeroTypeUtils.byName("Thief"), HeroTypeUtils.byName("Mage")},
         new MonsterType[]{MonsterTypeLib.byName("tarantus")}
      );
      Hero h1 = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.rollHit(f, h1, m, ESB.dmg.val(1));
      TestUtils.rollHit(f, h1, m, ESB.dmgCleaveChain.val(1));
      TestRunner.assertEquals("m should have taken 2 damage", m.entType.hp - 2, TestUtils.getState(f, m).getHp());
   }

   @Test
   @Skip
   public static void jinxLeftPlus4() {
      FightLog f = TestUtils.setupFight(MonsterTypeLib.byName("Jinx-9"));
      Monster jinx = TestUtils.monsters.get(0);
      Hero h = TestUtils.heroes.get(0);
      TestUtils.roll(f, jinx, h, 2, true);
      TestRunner.assertEquals("Should be taking 7 damage", h.entType.hp - 8, f.getState(FightLog.Temporality.Future, h).getHp());
   }

   @Test
   public static void duplicateInsanity() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Fighter"), HeroTypeUtils.byName("Thief"), HeroTypeUtils.byName("Mage")},
         new MonsterType[]{MonsterTypeLib.byName("tarantus")}
      );
      Hero h = TestUtils.heroes.get(0);
      Hero h2 = TestUtils.heroes.get(1);
      Hero h3 = TestUtils.heroes.get(2);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.rollHit(f, h, m, ESB.dmgDuplicate.val(1).withKeyword(Keyword.bloodlust));
      TestRunner.assertEquals("Should have taken 1 damage", m.entType.hp - 1, f.getState(FightLog.Temporality.Present, m).getHp());
      TestUtils.roll(f, h2, m, 0, false);
      TestRunner.assertEquals("Should have taken 3 damage", m.entType.hp - 3, f.getState(FightLog.Temporality.Present, m).getHp());
      TestUtils.roll(f, h3, m, 0, false);
      TestRunner.assertEquals("Should have taken 5 damage", m.entType.hp - 5, f.getState(FightLog.Temporality.Present, m).getHp());
   }

   @Test
   public static void levelupViewCrash() {
      List<HeroType> fails = new ArrayList<>();
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Fighter"), HeroTypeUtils.byName("Thief"), HeroTypeUtils.byName("Mage")},
         new MonsterType[]{MonsterTypeLib.byName("tarantus")}
      );

      for (HeroType ht : HeroTypeLib.getMasterCopy()) {
         Hero h = ht.makeEnt();
         EntState es = new EntState(h, f.getSnapshot(FightLog.Temporality.Present), new ArrayList<>());

         for (int i = 0; i < 6; i++) {
            try {
               es.getSideState(i);
            } catch (Exception var8) {
               var8.printStackTrace();
               fails.add(ht);
            }
         }
      }

      Tann.assertTrue("Should be no fails: " + fails, fails.isEmpty());
   }

   @Test
   public static void permadeathResurrect() {
      List<Hero> heroes = HeroTypeUtils.getHeroes(
         new HeroType[]{HeroTypeUtils.byName("Fighter"), HeroTypeUtils.byName("Fighter"), HeroTypeUtils.byName("Fighter")}
      );
      heroes.get(0).addItem(ItemLib.byName("glass helm"));
      FightLog f = TestUtils.setupFight(heroes, Arrays.asList(MonsterTypeLib.byName("testGoblin").makeEnt()), new Modifier[0]);
      Tann.assertEquals("Should be no dead heroes", 0, f.getSnapshot(FightLog.Temporality.Present).getStates(true, true).size());
      TestUtils.hit(f, heroes.get(1), new EffBill().kill().bEff());
      Tann.assertEquals("Should be 1 dead hero", 1, f.getSnapshot(FightLog.Temporality.Present).getStates(true, true).size());
      TestUtils.hit(f, null, new EffBill().resurrect(1).bEff());
      Tann.assertEquals("Should be 0 dead hero", 0, f.getSnapshot(FightLog.Temporality.Present).getStates(true, true).size());
      TestUtils.hit(f, heroes.get(0), new EffBill().kill().bEff());
      TestUtils.hit(f, heroes.get(1), new EffBill().kill().bEff());
      Tann.assertEquals("Should be 2 dead hero", 2, f.getSnapshot(FightLog.Temporality.Present).getStates(true, true).size());
      TestUtils.hit(f, null, new EffBill().resurrect(1).bEff());
      Tann.assertEquals("Should be 1 dead hero", 1, f.getSnapshot(FightLog.Temporality.Present).getStates(true, true).size());
   }

   @Test
   public static void curseRestartKeepItems() {
      List<Hero> heroes = HeroTypeUtils.getHeroes(new HeroType[]{HeroTypeUtils.byName("ludus"), HeroTypeUtils.byName("barbarian"), HeroTypeUtils.byName("ace")});

      for (Hero h : heroes) {
         h.addItem(ItemLib.random());
      }

      FightLog f = TestUtils.setupFight(heroes, Arrays.asList(MonsterTypeLib.byName("testGoblin").makeEnt()), new Modifier[0]);
      f.getContext().getParty().addItem(ItemLib.random());
      Tann.assertEquals("party should have 4 items total", 4, f.getContext().getParty().getItems(null).size());

      for (Hero h : f.getContext().getParty().getHeroes()) {
         Tann.assertEquals("tier should be 3", 3, h.getLevel());
      }

      f.getContext().clearForLoop();
      Tann.assertEquals("party should have no items", 0, f.getContext().getParty().getItems(null).size());

      for (Hero h : f.getContext().getParty().getHeroes()) {
         Tann.assertEquals("tier should be 1", 1, h.getLevel());
      }
   }

   @Test
   public static void cruelHealShield() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.attack(f, m, h, 3);
      Tann.assertTrue("damaged for 3", TestUtils.getState(f, h).getHp() == h.entType.hp - 3);
      TestUtils.hit(f, h, h, ESB.healShield.val(1).withKeyword(Keyword.cruel));
      Tann.assertTrue("damaged for 1", TestUtils.getState(f, h).getHp() == h.entType.hp - 1);
      Tann.assertTrue("shielded for 2", TestUtils.getState(f, h).getShields() == 2);
   }

   @Test
   public static void checkModifiersCollideWithPlusVersionOfThemselves() {
      Map<String, List<Modifier>> baseCurseMap = new HashMap<>();

      for (Modifier c : ModifierLib.getAll(ModifierType.Curse)) {
         String s = c.getName().replaceAll("\\+", "");
         if (!s.contains("Caltrops") && !s.contains("Trio") && !s.contains("Add ") && !s.contains("Summon ")) {
            if (baseCurseMap.get(s) == null) {
               baseCurseMap.put(s, new ArrayList<>());
            }

            baseCurseMap.get(s).add(c);
         }
      }

      List<Modifier> bads = new ArrayList<>();

      for (List<Modifier> lst : baseCurseMap.values()) {
         for (int i = 0; i < lst.size(); i++) {
            Modifier a = lst.get(i);

            for (int j = i + 1; j < lst.size(); j++) {
               Modifier b = lst.get(j);
               if (!ChoosableUtils.collides(a, b) && !bads.contains(a)) {
                  bads.add(a);
               }
            }
         }
      }

      Tann.assertTrue("Should be no bads; " + bads, bads.isEmpty());
   }

   @Test
   public static void mortalEmptyMaxHp() {
      List<Hero> heroes = HeroTypeUtils.getHeroes(new HeroType[]{HeroTypeUtils.byName("ludus"), HeroTypeUtils.byName("ludus")});
      heroes.get(1).addItem(ItemLib.byName("scar"));
      FightLog f = TestUtils.setupFight(heroes, Arrays.asList(MonsterTypeLib.byName("testBones").makeEnt()), new Modifier[]{ModifierLib.byName("Mortal^6")});
      Tann.assertEquals("Ludus 1 should have 6hp", 6, TestUtils.getState(f, heroes.get(0)).getHp());
      Tann.assertEquals("Ludus 2 should have 6hp", 6, TestUtils.getState(f, heroes.get(1)).getHp());
   }

   @Test
   @Slow
   public static void unfairCurseSelectionDuplicates() {
      int ATTEMPTS = 300;

      for (int i = 0; i < 300; i++) {
         List<Modifier> mods = PhaseGeneratorDifficulty.getModifiersForChoiceDebug(Difficulty.Unfair);
         List<Modifier> cleared = new ArrayList<>(mods);
         Tann.clearDupes(cleared);
         Tann.assertEquals(i + "Should be no dupes " + mods, mods.size(), cleared.size());
      }
   }

   @Test
   public static void startDamagedScar() {
      List<Hero> heroes = HeroTypeUtils.getHeroes(new HeroType[]{HeroTypeUtils.byName("ludus.hp.6"), HeroTypeUtils.byName("ludus.hp.6")});
      Hero a = heroes.get(0);
      Hero b = heroes.get(1);
      b.addItem(ItemLib.byName("scar"));
      FightLog f = TestUtils.setupFight(
         heroes, Arrays.asList(MonsterTypeLib.byName("testGoblin").makeEnt()), new Modifier[]{ModifierLib.byName("Start Damaged^1/6")}
      );
      Tann.assertEquals("Hero a should have 5hp", 5, TestUtils.getState(f, a).getHp());
      Tann.assertEquals("Hero b should have 5hp", 5, TestUtils.getState(f, b).getHp());
   }

   @Test
   @Skip
   public static void bindPetrifyPoison() {
      for (boolean useBind : Tann.BOTH) {
         FightLog f = TestUtils.setupFight();
         Hero h = TestUtils.heroes.get(0);
         Monster m = TestUtils.monsters.get(0);
         if (useBind) {
            TestUtils.spell(f, AbilityUtils.spellByName("Bind"), h);
         }

         TestUtils.hit(f, m, h, ESB.dmgPoison.val(1), true);
         TestUtils.hit(f, m, h, EntSidesBlobSmall.petrify.val(1), true);
         EntState futureState = TestUtils.getState(f, h, FightLog.Temporality.Future);
         String suff = useBind ? " after bind" : "";
         Tann.assertTrue(
            "Should be petrified" + suff,
            futureState.getSideState(SpecificSidesType.PetrifyOrder.sideIndices[0]).getCalculatedEffect().getType() == EffType.Blank
         );
         Tann.assertTrue("Should be poisoned" + suff, futureState.getBasePoisonPerTurn() == 1);
      }
   }

   @Test
   @Slow
   public static void cursedModeDuplicateCurses() {
      int NUM_ATTEMPTS = 10;

      for (int attempt = 0; attempt < 10; attempt++) {
         DungeonContext dc = new CurseConfig().makeContext();

         for (int cursePick = 0; cursePick < 50; cursePick++) {
            List<Modifier> mods = ModifierPickUtils.generateModifiers(-1, 3, ModifierPickContext.Cursed, dc);
            if (Tann.anySharedItems(mods, dc.getCurrentModifiers())) {
               List<Modifier> shared = new ArrayList<>();

               for (Modifier currentModifier : dc.getCurrentModifiers()) {
                  if (mods.contains(currentModifier)) {
                     shared.add(currentModifier);
                  }
               }

               Tann.assertTrue("No shared modifiers: " + shared, false);
            }

            for (int modIndex = 0; modIndex < mods.size(); modIndex++) {
               if (modIndex == 0) {
                  mods.get(modIndex).onChoose(dc, 0);
               } else {
                  mods.get(modIndex).onReject(dc);
               }
            }

            if (cursePick % 4 == 0) {
               dc.clearForLoop();
            }
         }
      }
   }

   @Test
   @Slow
   public static void cursedModeDuplicateCursesTwo() {
      int NUM_ATTEMPTS = 1;

      for (int attempt = 0; attempt < 1; attempt++) {
         DungeonContext dc = new BlyptraMode.BlyptraConfig().makeContext();
         List<Phase> phases = new ArrayList<>();

         for (int lv = 0; lv < 400; lv++) {
            dc.nextLevel();
            phases.clear();
            dc.addPhasesFromCurrentLevel(phases);

            for (int i = 0; i < phases.size(); i++) {
               Phase p = phases.get(i);
               if (p instanceof ChoicePhase) {
                  ChoicePhase c = (ChoicePhase)p;
                  Choosable choz = Tann.random(c.getOptions());
                  choz.onChoose(dc, 0);
                  Set<String> set = new HashSet<>();

                  for (Modifier currentModifier : dc.getCurrentModifiers()) {
                     String n = currentModifier.getName();
                     if (set.contains(n) && !n.contains(".")) {
                        Tann.assertTrue("bad set of " + set.size() + " mods: " + n, false);
                     } else {
                        set.add(n);
                     }
                  }
               }
            }
         }
      }
   }

   @Test
   @Slow
   public static void checkSlowSpellsPlusCanBeOffered() {
      int NUM_ATTEMPTS = 200;
      DungeonContext dc = new CurseConfig().makeContext();
      Modifier slowSpells = ModifierLib.byName("Slow Spells^4");
      Modifier slowSpellsPlus = ModifierLib.byName("Slow Spells^3");
      slowSpells.onChoose(dc, 0);

      for (int attempt = 0; attempt < 200; attempt++) {
         List<Modifier> mods = ModifierPickUtils.generateModifiers(-1, 3, ModifierPickContext.Cursed, dc);
         Tann.assertTrue("No slow spells in offer", !mods.contains(slowSpells));
         if (mods.contains(slowSpellsPlus)) {
            return;
         }
      }

      Tann.throwEx("Never offered slow spells+ in 200 offers");
   }

   @Test
   public static void ensureDifferentCursesOffered() {
      int NUM_ATTEMPTS = 1000;
      DungeonContext dc = new CurseConfig().makeContext();

      for (int attempt = 0; attempt < 1000; attempt++) {
         List<Modifier> mods = ModifierPickUtils.generateModifiers(-1, 3, ModifierPickContext.Cursed, dc);
         Tann.clearDupes(mods);
         Tann.assertEquals("Should be 3 items in list", 3, mods.size());
      }
   }

   @Test
   public static void fumesMonsterHp() {
      FightLog f = TestUtils.loadFromString(
         "`{v:203b,d:{n:107,p:{h:[Juggler,Gladiator,Warden,Druid~Pocket Phylactery,Mage~Castor Root~Garnet]},m:[Monster HP,Fumes],l:{m:[Magrat,Gytha,Agnes],b:true}},c:[1322c,20Z4,2164,2250,4],s:44022205,p:[02;0]}`"
      );
      int poisonDamage = 0;

      for (EntState es : f.getSnapshot(FightLog.Temporality.Future).getStates(false, false)) {
         poisonDamage += es.getPoisonDamageTaken();
      }

      Tann.assertEquals("Should be 6 poison damage taken, from fumes", 6, poisonDamage);
   }

   @Test
   @Skip
   public static void cleaveKillHexia() {
      FightLog f = TestUtils.loadFromString(
         "`{v:203b,d:{n:19,p:{h:[Sharpshot~Abacus~Olympian Trident,Wanderer,Paladin,Forsaken,Chronos],e:[Bone Charm,Iron Heart,Tower Shield,Spell: Heat,Demonic Deal,Demon Claw,Ironblood Pendant,Dumbbell,Splitting Arrows]},l:{m:[Imp,Imp,Hexia,Imp]},sl:19},c:[18p4c,1424,20Z5,2142,2280,2382,4],s:520240023,p:[{ps:1998}]}`"
      );

      for (EntState es : f.getSnapshot(FightLog.Temporality.Present).getStates(false, true)) {
         Tann.assertTrue("Hexia should not be dead", es.getEnt().getEntType() != MonsterTypeLib.byName("hexia"));
      }
   }

   @Test
   @Skip
   public static void stasisCharged() {
      FightLog f = TestUtils.loadFromString(
         "`{v:207i,d:{n:1,p:{h:[Thief,Fighter,Defender,Priestess,Mage~Stasis~Charge Link]},l:{m:[Goblin,Rat,Goblin]}},c:[1661,17Z0,2030,2143,2272,4],s:03232110,p:[1996]}`"
      );
      Tann.assertEquals("Should have 2 mana", 2, f.getSnapshot(FightLog.Temporality.Present).getTotalMana());
      f = TestUtils.loadFromString(
         "`{v:207i,d:{n:1,p:{h:[Thief,Fighter,Defender,Priestess,Mage~Charge Link~Stasis]},l:{m:[Goblin,Rat,Goblin]}},c:[1661,17Z0,2030,2143,2272,4],s:03232110,p:[1996]}`"
      );
      Tann.assertEquals("Should have 2 mana", 2, f.getSnapshot(FightLog.Temporality.Present).getTotalMana());
   }

   @Test
   @Skip
   public static void riseBandit() {
      FightLog f = TestUtils.loadFromString(
         "`{v:208i,d:{p:{h:[Rogue,Scrapper,Stalwart,Enchanter,Ace],e:[Ballet Shoes,Square Wheel,Rain of Arrows,PowerStone,Ambrosia,Shimmering Halo]},m:[Rise],l:{m:[illusion,gnat,Bandit,Gnoll,Bandit,Bandit,gnat]}},c:[1a82,1981,1bZ4,308,308,1831,2085,2174,22b5,2392,2472,2590,2690,4],s:5452320021124,p:[1998]}`"
      );
      List<EntState> states = f.getSnapshot().getStates(false, false);
      int bandits = 0;

      for (EntState state : states) {
         if (state.getEnt().getEntType() == MonsterTypeLib.byName("bandit")) {
            bandits++;
         }
      }

      Tann.assertEquals("Should be 1 bandit", 1, bandits);
   }

   @Test
   @Skip
   public static void slimeBandit() {
      FightLog f = TestUtils.loadFromString(
         "`{v:208i,d:{p:{h:[Ludus,Ludus,Ludus,Ludus,Ludus],e:[Scar]},l:{m:[Bandit,Slimer,Bandit],b:true}},c:[1510,1525,1714,2074,2143,2255,4],s:4323505043,p:[11]}`"
      );
      List<EntState> states = f.getSnapshot().getStates(false, false);
      int bandits = 0;

      for (EntState state : states) {
         if (state.getEnt().getEntType() == MonsterTypeLib.byName("bandit")) {
            bandits++;
         }
      }

      Tann.assertEquals("Should be 0 bandit", 0, bandits);
   }

   @Test
   public static void poisonThisTurn() {
      FightLog f = TestUtils.loadFromString(
         "`{v:208i,d:{p:{h:[Lost,Fighter,Squire,Healer,Mage]},m:[3rd.Wolves],l:{m:[Wolf,Rat,Archer]}},c:[16Z4,17Z1,1412,301,1422,1212,2033,2134,2264,4,1202,1311,1414,15Z3,16Z2,301,2060,2131,4],s:04121432,p:[1995]}`"
      );
      Tann.assertTrue("Should be victory in future", f.getSnapshot(FightLog.Temporality.Future).isVictory());
   }

   @Test
   public static void weakenedNegative() {
      int side = 0;
      int wkAmt = 3;
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      int initialVal = TestUtils.getState(f, h).getSideState(0).getCalculatedEffect().getValue();
      Tann.assertTrue("Val should be positive", initialVal > 0);
      TestUtils.hit(f, TestUtils.heroes.get(0), new EffBill().damage(3).keywords(Keyword.weaken).bEff());
      int val = TestUtils.getState(f, h).getSideState(0).getCalculatedEffect().getValue();
      Tann.assertTrue("Val should be negative", val < 0);
      Tann.assertEquals("Val should be calculated right", initialVal - 3, val);
      TestUtils.hit(f, TestUtils.heroes.get(0), new EffBill().heal(3).keywords(Keyword.boost).bEff());
      val = TestUtils.getState(f, h).getSideState(0).getCalculatedEffect().getValue();
      Tann.assertEquals("Val should be back to normal", initialVal, val);
   }

   @Test
   public static void itemedNegative() {
      Hero h = HeroTypeLib.byName("fey").makeEnt();
      h.addItem(ItemLib.byName("Ace of Spades"));
      h.addItem(ItemLib.byName("Face of Horus"));
      FightLog f = TestUtils.setupFight(h, MonsterTypeLib.byName("testGoblin"));

      for (int i = 0; i < 6; i++) {
         int val = TestUtils.getState(f, h).getSideState(i).getCalculatedEffect().getValue();
         Tann.assertTrue("Value should be 5 or 1", val == 5 || val == 1);
      }
   }

   @Test
   @Slow
   public static void manyMonstersCrash() {
      int amt = 300;
      MonsterType[] types = new MonsterType[300];

      for (int i = 0; i < 300; i++) {
         types[i] = MonsterTypeLib.byName("archer");
      }

      FightLog f = TestUtils.setupFight(new HeroType[]{HeroTypeLib.byName("ludus")}, types);

      for (int i = 0; i < 299; i++) {
         Ent e = f.getSnapshot(FightLog.Temporality.Present).getStates(false, false).get(0).getEnt();
         TestUtils.roll(f, TestUtils.heroes.get(0), e, 0, false);
      }

      f.serialiseCommands();
   }

   @Test
   public static void testCopycatEcho() {
      FightLog f = TestUtils.loadFromString(
         "`{v:2023i,d:{n:1,p:{h:[Dabble,Brigand~Collar,Defender,Vampire~Duck,Initiate]},l:{m:[Boar,Boar]}},c:[1553,1300,2050,21Z3,4],s:0330234,p:[1993]}`"
      );
      int damageToEnemies = 0;

      for (EntState state : f.getSnapshot(FightLog.Temporality.Present).getStates(false, false)) {
         damageToEnemies += state.getDamageTakenThisTurn();
      }

      Tann.assertEquals("Boar should have taken 5 damage", 5, damageToEnemies);
   }

   @Test
   public static void testCopycatEchoRefract() {
      FightLog f = TestUtils.loadFromString(
         "`{v:21026b,d:{n:2,p:{h:[Dabble,Brigand~Collar,Defender,Vampire~Duck,Initiate~k.resonate],e:[Healing Wand]},l:{m:[Rat,Rat,Wolf,Rat]}},c:[2043,2171,2260,2361,4,1763,1870,1540,2060,2152,2252,2382,4],s:022250330,p:[1994]}`"
      );
      int heroMissingHp = 0;

      for (EntState state : f.getSnapshot(FightLog.Temporality.Present).getStates(true, false)) {
         heroMissingHp += state.getMissingHp();
      }

      Tann.assertEquals("0hp should be missing", 0, heroMissingHp);
   }

   @Test
   public static void statueBagBlank() {
      HeroType ht = HeroTypeLib.byName("statue.i.knife bag");
      Tann.assertTrue("statue:knife bag not be missingno", !ht.isMissingno());
      Tann.assertTrue("should have sides outside of fightlog", ht.makeEnt().getBlankState().getSideState(0).getCalculatedEffect().hasKeyword(Keyword.pain));
   }

   @Test
   @Slow
   public static void clayMirrorSatchel() {
      TestUtils.loadFromString(
         "`{v:2028i,d:{n:8,p:{h:[Trouble,Berserker,Monk,Fey,Glacia~emerald satchel],e:[clay]},l:{m:[Quartz,Ghost,Quartz]},sl:8},s:31133431,p:[\"2{}\",3]}`"
      );
   }

   @Test
   public static void clayMirror() {
      TestUtils.loadFromString(
         "`{v:2028i,d:{n:8,p:{h:[Trouble,Berserker,Monk,Fey,Glacia~clay~emerald mirror],e:[leather vest]},l:{m:[Quartz,Ghost,Quartz]},sl:8},s:31133431,p:[\"2{}\",3]}`"
      );
   }

   @Test
   public static void testEggSuicide() {
      FightLog f = TestUtils.loadFromString(
         "`{v:2028i,d:{p:{h:[Spellblade,Brigand,Alloy,Fey,Initiate],e:[Change of Heart]},l:{m:[Seed,Seed,Caw Egg,Seed]}},c:[1712,20Z4,21Z4,22Z0,23Z5,4],s:440503325,p:[1995]}`"
      );
      TestUtils.nextTurn(f);
      int numEggs = 0;

      for (EntState state : f.getSnapshot(FightLog.Temporality.Present).getStates(false, false)) {
         if (state.getEnt().getName(false).equalsIgnoreCase("caw egg")) {
            numEggs++;
         }
      }

      Tann.assertEquals("Should be 1 egg", 1, numEggs);
   }

   @Test
   public static void levelRangeAddMonster() {
      FightLog f = TestUtils.loadFromString(
         "`{v:2036i,d:{n:1,p:{h:[Trouble2,Brigand,Defender,Healer,Mage]},m:[1.Add.Dragon],l:{m:[Valiant,Rat]}},s:10300155,p:[\"2{}\",3]}`"
      );
      Tann.assertEquals("Should be 3 monsters", 3, f.getSnapshot(FightLog.Temporality.Present).getStates(false, false).size());
   }

   @Test
   @Skip
   public static void levelRangeAddCurse() {
      DungeonContext dc = new DungeonContext(new ClassicConfig(Difficulty.Normal), Party.generate(0), 0);
      dc.addModifier(ModifierLib.byName("1-10 curses^1"));
      List<Integer> curseLevels = new ArrayList<>();

      for (int i = 0; i < 19; i++) {
         List<Phase> phases = new ArrayList<>();
         dc.nextLevel();
         dc.addPhasesFromCurrentLevel(phases);
         boolean hasCursePick = false;

         for (Phase phase : phases) {
            if (phase instanceof ChoicePhase) {
               ChoicePhase cp = (ChoicePhase)phase;

               for (Choosable cho : cp.getOptions()) {
                  if (cho instanceof Modifier) {
                     Modifier m = (Modifier)cho;
                     if (m.getTier() < 0) {
                        hasCursePick = true;
                     }
                  }
               }
            }
         }

         if (hasCursePick) {
            curseLevels.add(i);
         }
      }

      Tann.assertEquals("Should be 10 curse levels :" + curseLevels, 10, curseLevels.size());
      Tann.assertEquals("First curse level should be 0 :" + curseLevels, 0, curseLevels.get(0));
   }

   @Test
   @Slow
   public static void blursedBlessingOfferPermanentBoon() {
      ContextConfig cc = new BlursedConfig();
      DungeonContext dc = new DungeonContext(cc, Party.generate(0), 0);
      PhaseGeneratorModifierPickAdvanced pg = BlursedConfig.firstPickPhase();
      List<Modifier> bad = new ArrayList<>();
      int tests = 100;

      for (int i = 0; i < 100; i++) {
         ChoicePhase cp = (ChoicePhase)pg.get(dc).get(0);

         for (Choosable ch : cp.getOptions()) {
            Modifier m = (Modifier)ch;
            if (ChoosableUtils.collides(m, Collision.MODIFIER)) {
               bad.add(m);
            }
         }
      }

      Tann.assertTrue("Should be no bads: " + bad, bad.isEmpty());
   }

   @Test
   @Skip
   public static void noEventsOnLevelZero() {
      for (int i = 0; i < 50; i++) {
         DungeonContext dc = DebugUtilsUseful.dummyContext();
         List<Phase> phases = new ArrayList<>();
         dc.addPhasesFromCurrentLevel(phases);
         Tann.assertTrue("Should be no phases: " + phases, phases.isEmpty());
      }
   }

   @Test
   public static void armourPlus() {
      FightLog f = TestUtils.loadFromString(
         "`{v:2094i,d:{n:1,p:{h:[Glunk,Glink,Alloy,Lost,Hoarder]},m:[Armour^1/1],l:{m:[Thorn,Arbiter]},sl:1},c:[2020,2151,4],s:0153044,p:[02;0]}`"
      );
      Tann.assertEquals("Enemy should have shield", 1, f.getSnapshot().getStates(false, false).get(0).getShields());
   }

   @Test
   @Skip
   public static void twinPick() {
      DungeonContext dc = new DungeonContext(
         new ClassicConfig(Difficulty.Normal),
         new Party(
            new ArrayList<>(
               Arrays.asList(
                  HeroTypeLib.byName("soldier").makeEnt(),
                  HeroTypeLib.byName("soldier").makeEnt(),
                  HeroTypeLib.byName("statue").makeEnt(),
                  HeroTypeLib.byName("soldier").makeEnt(),
                  HeroTypeLib.byName("soldier").makeEnt()
               )
            )
         )
      );
      Tann.assertEquals("should be 5 heroes", 5, dc.getParty().getHeroes().size());
      HeroType tt = HeroTypeLib.byName("Twin");
      HeroType t2 = HeroTypeLib.byName("Tw1n");
      new LevelupHeroChoosable(tt).onChoose(dc, 0);
      Tann.assertEquals("should be 6 heroes", 6, dc.getParty().getHeroes().size());
      Tann.assertEquals("twin should be position 3", t2, dc.getParty().getHeroes().get(3).entType);
   }

   @Test
   public static void nanSidePower() {
      List<EntSide> nans = new ArrayList<>();
      EntType et = HeroTypeLib.byName("o0.123");
      List<EntSide> sides = new ArrayList<>();

      for (HeroCol basic : HeroCol.basics()) {
         sides.addAll(HeroTypeUtils.getSidesWithColour(basic, true, false));
      }

      for (EntSide entSide : sides) {
         float f = entSide.getEffectTier(et);
         if (Float.isNaN(f)) {
            nans.add(entSide);
         }
      }

      TestRunner.assertTrue("Should be empty: " + nans, nans.isEmpty());
   }

   @Test
   public static void annulHang() {
      FightLog f = TestUtils.loadFromString(
         "`{v:28130a,d:{n:1,p:{h:[Alien,Ninja]},m:[party.Alien:Ninja,fight.Troll,x9.Extra Reroll],l:{m:[Archer,Boar,Rat]},sl:1},c:[1121,1204,2015,4],s:514,p:[1988]}`"
      );

      for (EntState state : f.getSnapshot(FightLog.Temporality.Present).getStates(null, null)) {
         state.getAllSideStates();
      }
   }

   @Test
   public static void vulnTough() {
      FightLog f = TestUtils.loadFromString(
         " `{v:21026b,d:{n:8,p:{h:[Disciple,Gardener~Pocket Phylactery,Sparky~Leather Vest~Cloak,Presence~Ballet Shoes~Seedling,Trapper~Brittle~Blessed Ring],e:[Stake,Bent Fork,Reagents,Iron Heart]},m:[Tough Hp^1,Boss Curses^1,i.Brittle,Tower^4,Skulk^3],l:{m:[Gnoll,Sarcophagus,Gnoll]},sl:1},c:[14Z2,15Z3,1702,1232,1512,331,1122,320,2043,2154,2230,4,1425,1233,11Z0,300,20Z5,4,13Z1,1710,301,20Z3,2171,2274,4],s:3314011420,p:[10]}`"
      );
      Tann.assertEquals("vuln bug", 3, f.getSnapshot(FightLog.Temporality.Present).getStates(false, false).size());
   }
}
