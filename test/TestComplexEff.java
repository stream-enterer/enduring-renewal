package com.tann.dice.test;

import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobBig;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.trigger.personal.KeepShields;
import com.tann.dice.gameplay.trigger.personal.specialPips.pipLoc.PipLoc;
import com.tann.dice.gameplay.trigger.personal.specialPips.pipLoc.PipLocType;
import com.tann.dice.gameplay.trigger.personal.specialPips.resistive.StoneSpecialHp;
import com.tann.dice.test.util.Test;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.test.util.TestUtils;
import com.tann.dice.util.Tann;

public class TestComplexEff {
   @Test
   public static void pain() {
      FightLog f = TestUtils.setupFight();
      Monster monster = TestUtils.monsters.get(0);
      Hero hero = TestUtils.heroes.get(0);
      TestUtils.rollHit(f, hero, monster, ESB.dmgPain.val(1), true);
      EntState monsterState = f.getState(FightLog.Temporality.Future, monster);
      TestRunner.assertEquals("monster should be hit for 1 damage", monsterState.getMaxHp() - 1, monsterState.getHp());
      EntState heroState = f.getState(FightLog.Temporality.Future, hero);
      TestRunner.assertEquals("hero should be hit for 1 damage", heroState.getMaxHp() - 1, heroState.getHp());
   }

   @Test
   public static void doubleTaunt() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Fighter"), HeroTypeUtils.byName("Thief")}, new MonsterType[]{MonsterTypeLib.byName("testGoblin")}
      );
      int attackAmount = 3;
      int tauntAmount = 1;
      TestUtils.rollHit(f, TestUtils.monsters.get(0), TestUtils.heroes.get(0), ESB.dmg.val(attackAmount), true);
      EntState future = TestUtils.getState(f, TestUtils.heroes.get(0), FightLog.Temporality.Future);
      TestRunner.assertEquals("should be damaged for 10", future.getMaxHp() - attackAmount, future.getHp());
      TestUtils.rollHit(f, TestUtils.heroes.get(1), TestUtils.heroes.get(0), ESB.redirect.val(tauntAmount), false);
      future = TestUtils.getState(f, TestUtils.heroes.get(0), FightLog.Temporality.Future);
      TestRunner.assertEquals("should be taunted away", future.getMaxHp(), future.getHp());
      future = TestUtils.getState(f, TestUtils.heroes.get(1), FightLog.Temporality.Future);
      TestRunner.assertEquals("should be taunted towards", future.getMaxHp() - (attackAmount - tauntAmount), future.getHp());
      TestUtils.rollHit(f, TestUtils.heroes.get(1), TestUtils.heroes.get(1), ESB.redirect.val(tauntAmount), false);
      future = TestUtils.getState(f, TestUtils.heroes.get(1), FightLog.Temporality.Future);
      TestRunner.assertEquals("taunting self should reduce damage further", future.getMaxHp() - (attackAmount - tauntAmount * 2), future.getHp());
      future = TestUtils.getState(f, TestUtils.heroes.get(0), FightLog.Temporality.Future);
      TestRunner.assertEquals("should still be taunted away", future.getMaxHp(), future.getHp());
   }

   @Test
   public static void keepShield() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      TestUtils.rollHit(f, h, TestUtils.heroes.get(0), ESB.shield.val(3), false);
      TestRunner.assertEquals("should be shielded for 3", 3, TestUtils.getState(f, h, FightLog.Temporality.Present).getShields());
      TestUtils.nextTurn(f);
      TestRunner.assertEquals("shield should disappear", 0, TestUtils.getState(f, h, FightLog.Temporality.Present).getShields());
      TestUtils.rollHit(f, h, TestUtils.heroes.get(0), ESB.shieldCleanse.val(5), false);
      TestUtils.hit(f, h, new KeepShields(), false);
      TestRunner.assertEquals("should be shielded for 5", 5, TestUtils.getState(f, h, FightLog.Temporality.Present).getShields());
      TestUtils.nextTurn(f);
      TestRunner.assertEquals("shield should not disappear", 5, TestUtils.getState(f, h, FightLog.Temporality.Present).getShields());
   }

   @Test
   public static void stoneskin() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      int monsterMax = m.entType.hp;
      TestUtils.hit(f, m, new EffBill().buff(new Buff(new StoneSpecialHp(new PipLoc(PipLocType.LeftmostN, 10)))).bEff(), false);
      TestRunner.assertEquals("monster should have taken 0 damage", monsterMax - 0, TestUtils.getState(f, m, FightLog.Temporality.Present).getHp());
      TestUtils.hit(f, m, new EffBill().damage(0).bEff(), false);
      TestRunner.assertEquals("monster should have taken 0 damage", monsterMax - 0, TestUtils.getState(f, m, FightLog.Temporality.Present).getHp());
      TestUtils.attack(f, h, m, 1, false);
      TestRunner.assertEquals("monster should have taken 1 damage", monsterMax - 1, TestUtils.getState(f, m, FightLog.Temporality.Present).getHp());
      TestUtils.attack(f, h, m, 3, false);
      TestRunner.assertEquals("monster should have taken 2 damage", monsterMax - 2, TestUtils.getState(f, m, FightLog.Temporality.Present).getHp());
      TestUtils.attack(f, h, m, 2000, false);
      TestRunner.assertEquals("monster should have taken 3 damage", monsterMax - 3, TestUtils.getState(f, m, FightLog.Temporality.Present).getHp());
   }

   @Test
   public static void resurrect() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{
            HeroTypeUtils.byName("Healer"),
            HeroTypeUtils.byName("Fighter"),
            HeroTypeUtils.byName("Defender"),
            HeroTypeUtils.byName("Mage"),
            HeroTypeUtils.byName("Thief")
         },
         new MonsterType[]{
            MonsterTypeLib.byName("testGoblin"),
            MonsterTypeLib.byName("testGoblin"),
            MonsterTypeLib.byName("testGoblin"),
            MonsterTypeLib.byName("testGoblin"),
            MonsterTypeLib.byName("testGoblin"),
            MonsterTypeLib.byName("testGoblin")
         }
      );
      TestRunner.assertEquals("all heroes should be alive", 5, f.getSnapshot(FightLog.Temporality.Present).getAliveHeroStates().size());
      Monster m = TestUtils.monsters.get(0);
      TestUtils.hit(f, TestUtils.heroes.get(0), new EffBill().kill().bEff(), false);
      TestRunner.assertEquals("4 heroes should be alive", 4, f.getSnapshot(FightLog.Temporality.Present).getAliveHeroStates().size());
      TestUtils.hit(f, TestUtils.heroes.get(1), new EffBill().kill().bEff(), false);
      TestUtils.hit(f, TestUtils.heroes.get(2), new EffBill().kill().bEff(), false);
      TestRunner.assertEquals("2 heroes should be alive", 2, f.getSnapshot(FightLog.Temporality.Present).getAliveHeroStates().size());
      TestUtils.hit(f, TestUtils.heroes.get(3), null, ESB.resurrect.val(1), false);
      TestRunner.assertEquals("3 heroes should be alive", 3, f.getSnapshot(FightLog.Temporality.Present).getAliveHeroStates().size());
      TestUtils.hit(f, TestUtils.heroes.get(3), null, ESB.resurrect.val(5), false);
      TestRunner.assertEquals("all heroes should be alive", 5, f.getSnapshot(FightLog.Temporality.Present).getAliveHeroStates().size());
      TestUtils.hit(f, TestUtils.heroes.get(0), new EffBill().kill().bEff(), false);
      TestUtils.hit(f, TestUtils.heroes.get(1), new EffBill().kill().bEff(), false);
      TestUtils.hit(f, TestUtils.heroes.get(2), new EffBill().kill().bEff(), false);
      TestUtils.hit(f, TestUtils.heroes.get(3), new EffBill().kill().bEff(), false);
      TestRunner.assertEquals("1 heroes should be alive", 1, f.getSnapshot(FightLog.Temporality.Present).getAliveHeroStates().size());
      TestUtils.hit(f, TestUtils.heroes.get(3), null, ESB.resurrect.val(3), false);
      TestRunner.assertEquals("4 heroes should be alive", 4, f.getSnapshot(FightLog.Temporality.Present).getAliveHeroStates().size());
   }

   @Test
   public static void petrifyBug() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{
            HeroTypeUtils.byName("Fighter"),
            HeroTypeUtils.byName("Fighter"),
            HeroTypeUtils.byName("Defender"),
            HeroTypeUtils.byName("Mage"),
            HeroTypeUtils.byName("Thief")
         },
         new MonsterType[]{MonsterTypeLib.byName("basilisk")}
      );
      int swordSideOne = 1;
      int swordSideTwo = 2;
      Hero a = TestUtils.heroes.get(0);
      Hero b = TestUtils.heroes.get(1);

      for (int side : new int[]{swordSideOne, swordSideTwo}) {
         for (Hero h : new Hero[]{a, b}) {
            Monster basilisk = TestUtils.monsters.get(0);
            EntSideState ess = TestUtils.getState(f, h, FightLog.Temporality.Present).getSideState(side);
            TestRunner.assertTrue(side + " side should be sword", ess.getCalculatedEffect().getType() == EffType.Damage);
            TestUtils.roll(f, h, basilisk, side, false);
            ess = TestUtils.getState(f, h, FightLog.Temporality.Present).getSideState(h.getSides()[side]);
            TestRunner.assertTrue(side + " side should be petrified", ess.getCalculatedEffect().getType() == EffType.Blank);
         }
      }

      TestUtils.undo(f);
      TestUtils.undo(f);

      for (Hero h : new Hero[]{a, b}) {
         EntSideState ess = TestUtils.getState(f, h, FightLog.Temporality.Present).getSideState(swordSideOne);
         TestRunner.assertTrue(swordSideOne + " side should be petrified", ess.getCalculatedEffect().getType() == EffType.Blank);
         ess = TestUtils.getState(f, h, FightLog.Temporality.Present).getSideState(swordSideTwo);
         TestRunner.assertTrue(swordSideTwo + " side should be sword", ess.getCalculatedEffect().getType() == EffType.Damage);
      }

      TestUtils.hit(f, a, new EffBill().shield(1).bEff(), false);

      for (Hero h : new Hero[]{a, b}) {
         EntSideState ess = TestUtils.getState(f, h, FightLog.Temporality.Present).getSideState(swordSideOne);
         TestRunner.assertTrue(swordSideOne + " side should be petrified", ess.getCalculatedEffect().getType() == EffType.Blank);
         ess = TestUtils.getState(f, h, FightLog.Temporality.Present).getSideState(swordSideTwo);
         TestRunner.assertTrue(swordSideTwo + " side should be sword", ess.getCalculatedEffect().getType() == EffType.Damage);
      }
   }

   @Test
   public static void fortitudeHeal() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      int prevHp = TestUtils.getState(f, h).getHp();
      int prevMaxHp = TestUtils.getState(f, h).getMaxHp();
      TestUtils.hit(f, h, new EffBill().heal(1).bEff(), false);
      TestRunner.assertEquals("hp should not change", prevHp, TestUtils.getState(f, h).getHp());
      TestRunner.assertEquals("max hp should not change", prevMaxHp, TestUtils.getState(f, h).getMaxHp());
      TestUtils.hit(f, h, new EffBill().heal(2).keywords(Keyword.vitality).bEff(), false);
      TestRunner.assertEquals("hp should be +2", prevHp + 2, TestUtils.getState(f, h).getHp());
      TestRunner.assertEquals("max hp should be +2", prevMaxHp + 2, TestUtils.getState(f, h).getMaxHp());
      prevHp = TestUtils.getState(f, h).getHp();
      prevMaxHp = TestUtils.getState(f, h).getMaxHp();
      TestUtils.hit(f, h, new EffBill().shield(1).keywords(Keyword.vitality).bEff(), false);
      TestRunner.assertEquals("hp should not change", prevHp, TestUtils.getState(f, h).getHp());
      TestRunner.assertEquals("max hp should +1", prevMaxHp + 1, TestUtils.getState(f, h).getMaxHp());
      prevHp = TestUtils.getState(f, h).getHp();
      prevMaxHp = TestUtils.getState(f, h).getMaxHp();
      TestUtils.hit(f, h, new EffBill().heal(10).bEff(), false);
      TestRunner.assertEquals("hp should be +1", prevHp + 1, TestUtils.getState(f, h).getHp());
      TestRunner.assertEquals("max hp should not change", prevMaxHp, TestUtils.getState(f, h).getMaxHp());
      prevHp = TestUtils.getState(f, h).getHp();
      prevMaxHp = TestUtils.getState(f, h).getMaxHp();
   }

   @Test
   public static void copycatManagain() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.rollHit(f, h, m, ESB.dmgCopycat.val(1));
      Tann.assertEquals("Should have 0 mana", 0, f.getSnapshot(FightLog.Temporality.Present).getTotalMana());
      TestUtils.rollHit(f, h, h, ESB.shieldMana.val(1));
      Tann.assertEquals("Should have 1 mana", 1, f.getSnapshot(FightLog.Temporality.Present).getTotalMana());
      TestUtils.rollHit(f, h, m, ESB.dmgCopycat.val(1));
      Tann.assertEquals("Should have 2 mana", 2, f.getSnapshot(FightLog.Temporality.Present).getTotalMana());
   }

   @Test
   public static void pair() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.rollHit(f, h, null, ESB.manaPair.val(1));
      Tann.assertEquals("Should have 1 mana", 1, f.getSnapshot(FightLog.Temporality.Present).getTotalMana());
      TestUtils.rollHit(f, h, null, ESB.manaPair.val(1));
      Tann.assertEquals("Should have 3 mana", 3, f.getSnapshot(FightLog.Temporality.Present).getTotalMana());
      TestUtils.rollHit(f, h, null, ESB.manaPair.val(2));
      Tann.assertEquals("Should have 7 mana", 7, f.getSnapshot(FightLog.Temporality.Present).getTotalMana());
      TestUtils.rollHit(f, h, null, ESB.manaPair.val(2));
      Tann.assertEquals("Should have 9 mana", 9, f.getSnapshot(FightLog.Temporality.Present).getTotalMana());
   }

   @Test
   public static void goblinFlee() {
      FightLog f = TestUtils.setupFight(MonsterTypeLib.byName("goblin"), MonsterTypeLib.byName("goblin"));
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.rollHit(f, h, m, ESB.dmg.val(100));
      Tann.assertTrue("Should be goblinflee", f.getSnapshot(FightLog.Temporality.Present).isVictory());
   }

   @Test
   public static void petrifyAboveSix() {
      FightLog f = TestUtils.setupFight(HeroTypeLib.byName("statue").makeEnt(), MonsterTypeLib.byName("alpha"));
      Hero h = TestUtils.heroes.get(0);
      TestUtils.rollHit(f, TestUtils.monsters.get(0), h, EntSidesBlobBig.slimeTriple.val(7).withKeyword(Keyword.petrify));
      Tann.assertEquals("Should be 6 sides petrified", 6, TestUtils.getState(f, h).getTotalPetrification());
      TestUtils.hit(f, h, ESB.healCleanse.val(6).getBaseEffect());
      Tann.assertEquals("Should be 1 side petrified", 1, TestUtils.getState(f, h).getTotalPetrification());
   }
}
