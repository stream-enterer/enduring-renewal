package com.tann.dice.test;

import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.test.util.Test;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.test.util.TestUtils;

public class TestBasicEff {
   @Test
   public static void basicSanityTest() {
      FightLog f = TestUtils.setupFight();
      TestRunner.assertEquals("There should be 1 monster", 1, f.getSnapshot(FightLog.Temporality.Present).getAliveMonsterStates().size());
      TestRunner.assertEquals("There should be 1 hero", 1, f.getSnapshot(FightLog.Temporality.Present).getAliveHeroStates().size());
   }

   @Test
   public static void attackEnemy() {
      FightLog f = TestUtils.setupFight();
      EntState monsterState = f.getState(FightLog.Temporality.Future, TestUtils.monsters.get(0));
      TestRunner.assertEquals("Monster should be undamaged", monsterState.getMaxHp(), monsterState.getHp());
      TestUtils.attack(f, TestUtils.heroes.get(0), TestUtils.monsters.get(0), 1, false);
      monsterState = f.getState(FightLog.Temporality.Future, TestUtils.monsters.get(0));
      TestRunner.assertEquals("Monster should damaged", monsterState.getMaxHp() - 1, monsterState.getHp());
   }

   @Test
   public static void attackHero() {
      FightLog f = TestUtils.setupFight();
      EntState heroState = f.getState(FightLog.Temporality.Future, TestUtils.heroes.get(0));
      TestRunner.assertEquals("Hero should be undamaged", heroState.getMaxHp(), heroState.getHp());
      TestUtils.attack(f, TestUtils.monsters.get(0), TestUtils.heroes.get(0), 1, false);
      heroState = f.getState(FightLog.Temporality.Future, TestUtils.heroes.get(0));
      TestRunner.assertEquals("Hero should damaged", heroState.getMaxHp() - 1, heroState.getHp());
   }

   @Test
   public static void basicBlock() {
      FightLog f = TestUtils.setupFight();
      Hero hero = TestUtils.heroes.get(0);
      TestUtils.hit(f, hero, hero, ESB.shield.val(1), false);
      TestUtils.hit(f, hero, hero, ESB.dmg.val(2), false);
      EntState heroState = f.getState(FightLog.Temporality.Future, TestUtils.heroes.get(0));
      TestRunner.assertEquals("Hero should take 1 damage", heroState.getMaxHp() - 1, heroState.getHp());
      TestRunner.assertEquals("Hero should block 1 damage", 1, heroState.getDamageBlocked());
   }

   @Test
   public static void basicHeal() {
      FightLog f = TestUtils.setupFight();
      Hero hero = TestUtils.heroes.get(0);
      TestUtils.hit(f, hero, hero, ESB.dmg.val(2), false);
      TestUtils.hit(f, hero, hero, ESB.heal.val(3), false);
      EntState heroState = f.getState(FightLog.Temporality.Future, TestUtils.heroes.get(0));
      TestRunner.assertEquals("Hero should be on full hp", heroState.getMaxHp(), heroState.getHp());
   }

   @Test
   public static void reinforcements() {
      MonsterType[] mt = new MonsterType[30];

      for (int i = 0; i < mt.length; i++) {
         mt[i] = MonsterTypeLib.byName("testGoblin");
      }

      FightLog f = TestUtils.setupFight(new HeroType[]{HeroTypeUtils.byName("Healer")}, mt);
      int initialGoblinsPresent = f.getSnapshot(FightLog.Temporality.Present).getEntities(false, false).size();
      TestRunner.assertEquals("should be a few goblins at the start", true, initialGoblinsPresent > 3 && initialGoblinsPresent < 20);
      TestUtils.hit(f, null, new EffBill().damage(100).bEff(), false);
      TestRunner.assertEquals(
         "goblins present should not have changed", initialGoblinsPresent, f.getSnapshot(FightLog.Temporality.Present).getEntities(false, false).size()
      );
      TestUtils.hit(f, null, new EffBill().damage(100).group().bEff(), false);
      TestRunner.assertEquals(
         "goblins present should not have changed", initialGoblinsPresent, f.getSnapshot(FightLog.Temporality.Present).getEntities(false, false).size()
      );

      for (int i = 0; i < 8; i++) {
         TestUtils.hit(f, null, new EffBill().damage(100).group().bEff(), false);
      }

      TestRunner.assertEquals("goblins present should be 0", 0, f.getSnapshot(FightLog.Temporality.Present).getEntities(false, false).size());
      TestRunner.assertEquals("should be victorious", true, f.getSnapshot(FightLog.Temporality.Present).isVictory());
   }
}
