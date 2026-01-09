package com.tann.dice.test;

import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.test.util.Test;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.test.util.TestUtils;

public class TestCleanse {
   @Test
   public static void testPoisonCombine() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.rollHit(f, h, h, ESB.shield.val(100));
      int numBuffs = TestUtils.getState(f, h, FightLog.Temporality.Future).getActivePersonals().size();
      TestUtils.rollHit(f, m, h, ESB.dmgPoison.val(1));
      TestRunner.assertEquals("Should be full hp", h.getHeroType().hp, TestUtils.getState(f, h).getHp());
      TestRunner.assertEquals("Should be 1 incoming poison", 1, TestUtils.getState(f, h, FightLog.Temporality.Future).getPoisonDamageTaken());
      TestRunner.assertEquals("Should be 1 extra trigger", numBuffs + 1, TestUtils.getState(f, h, FightLog.Temporality.Future).getActivePersonals().size());
      TestUtils.rollHit(f, m, h, ESB.dmgPoison.val(1));
      TestRunner.assertEquals("Should be 2 incoming poison", 2, TestUtils.getState(f, h, FightLog.Temporality.Future).getPoisonDamageTaken());
      TestRunner.assertEquals(
         "Should still only be 1 extra trigger", numBuffs + 1, TestUtils.getState(f, h, FightLog.Temporality.Future).getActivePersonals().size()
      );
      TestUtils.nextTurn(f);
      TestRunner.assertEquals("should have taken 4 poison damage", h.getHeroType().hp - 4, TestUtils.getState(f, h, FightLog.Temporality.Future).getHp());
   }

   @Test
   public static void testPoisonCleanse() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.rollHit(f, h, h, ESB.shield.val(100));
      TestUtils.rollHit(f, m, h, ESB.dmgPoison.val(1));
      TestRunner.assertEquals("Should be 1 incoming poison", 1, TestUtils.getState(f, h, FightLog.Temporality.Future).getPoisonDamageTaken());
      TestUtils.rollHit(f, h, h, ESB.shieldCleanse.val(1));
      TestRunner.assertEquals("Should be 0 incoming poison", 0, TestUtils.getState(f, h, FightLog.Temporality.Future).getPoisonDamageTaken());

      for (int i = 0; i < 10; i++) {
         TestUtils.rollHit(f, h, h, ESB.shield.val(100));
         TestUtils.undo(f);
         TestUtils.undo(f);
      }

      TestRunner.assertEquals("Should be 0 incoming poison", 0, TestUtils.getState(f, h, FightLog.Temporality.Future).getPoisonDamageTaken());
      TestUtils.nextTurn(f);
      TestRunner.assertEquals("should have taken 0 damage", h.getHeroType().hp, TestUtils.getState(f, h, FightLog.Temporality.Future).getHp());
   }

   @Test
   public static void testPoisonCleanseWithRecalculate() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.rollHit(f, h, h, ESB.shield.val(100));
      TestUtils.rollHit(f, m, h, ESB.dmgPoison.val(1), true);
      TestRunner.assertEquals("Should be 1 incoming poison", 1, TestUtils.getState(f, h, FightLog.Temporality.Future).getPoisonDamageTaken());
      TestUtils.rollHit(f, h, h, ESB.shieldCleanse.val(1));
      TestRunner.assertEquals("Should be 0 incoming poison", 0, TestUtils.getState(f, h, FightLog.Temporality.Future).getPoisonDamageTaken());
      f.recalculateToFuture();
      TestRunner.assertEquals("Should be 0 incoming poison", 0, TestUtils.getState(f, h, FightLog.Temporality.Future).getPoisonDamageTaken());
      TestUtils.nextTurn(f);
      TestRunner.assertEquals("should have taken 0 damage", h.getHeroType().hp, TestUtils.getState(f, h, FightLog.Temporality.Future).getHp());
   }

   @Test
   public static void testPoisonPartialCleanse() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.rollHit(f, h, h, ESB.shield.val(100));
      TestUtils.rollHit(f, m, h, ESB.dmgPoison.val(2), true);
      TestRunner.assertEquals("Should be 2 incoming poison", 2, TestUtils.getState(f, h, FightLog.Temporality.Future).getPoisonDamageTaken());
      TestUtils.rollHit(f, h, h, ESB.shieldCleanse.val(1));
      TestRunner.assertEquals("Should be 1 incoming poison", 1, TestUtils.getState(f, h, FightLog.Temporality.Future).getPoisonDamageTaken());

      for (int i = 0; i < 10; i++) {
         f.recalculateToFuture();
         TestUtils.rollHit(f, h, h, ESB.shield.val(100));
         TestUtils.undo(f);
         TestUtils.undo(f);
      }

      TestRunner.assertEquals("Should be 1 incoming poison", 1, TestUtils.getState(f, h, FightLog.Temporality.Future).getPoisonDamageTaken());
      TestUtils.nextTurn(f);
      TestRunner.assertEquals("should have taken 2 damage", h.getHeroType().hp - 2, TestUtils.getState(f, h, FightLog.Temporality.Future).getHp());
   }

   @Test
   public static void testMultiPoisonPartialCleanse() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.rollHit(f, h, h, ESB.shield.val(100));
      TestUtils.rollHit(f, m, h, ESB.dmgPoison.val(2), true);
      TestUtils.rollHit(f, m, h, ESB.dmgPoison.val(2), true);
      TestRunner.assertEquals("Should be 4 incoming poison", 4, TestUtils.getState(f, h, FightLog.Temporality.Future).getPoisonDamageTaken());
      TestUtils.rollHit(f, h, h, ESB.shieldCleanse.val(1));
      TestRunner.assertEquals("Should be 3 incoming poison", 3, TestUtils.getState(f, h, FightLog.Temporality.Future).getPoisonDamageTaken());

      for (int i = 0; i < 10; i++) {
         f.recalculateToFuture();
         TestUtils.rollHit(f, h, h, ESB.shield.val(100));
         TestUtils.undo(f);
         TestUtils.undo(f);
      }

      TestRunner.assertEquals("Should be 3 incoming poison", 3, TestUtils.getState(f, h, FightLog.Temporality.Future).getPoisonDamageTaken());
      TestUtils.nextTurn(f);
      TestRunner.assertEquals("should have taken 6 damage", h.getHeroType().hp - 6, TestUtils.getState(f, h, FightLog.Temporality.Future).getHp());
   }

   @Test
   public static void testMultiPoisonPartialCleanseNextTurn() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.rollHit(f, h, h, ESB.shield.val(100));
      TestUtils.rollHit(f, m, h, ESB.dmgPoison.val(1), true);
      TestRunner.assertEquals("Should be 1 incoming poison", 1, TestUtils.getState(f, h, FightLog.Temporality.Future).getPoisonDamageTaken());
      TestUtils.nextTurn(f);
      TestRunner.assertEquals("Should be 1 incoming poison", 1, TestUtils.getState(f, h, FightLog.Temporality.Future).getPoisonDamageTaken());
      TestRunner.assertEquals("Should be -2 hp total", -2, TestUtils.getState(f, h, FightLog.Temporality.Future).getHp() - h.entType.hp);
      TestUtils.rollHit(f, h, h, ESB.shieldCleanse.val(1));
      TestRunner.assertEquals("Should be 0 incoming poison", 0, TestUtils.getState(f, h, FightLog.Temporality.Future).getPoisonDamageTaken());
      TestUtils.undo(f);
      TestUtils.undo(f);
      TestRunner.assertEquals("Should be 1 incoming poison after undoing", 1, TestUtils.getState(f, h, FightLog.Temporality.Future).getPoisonDamageTaken());
   }
}
