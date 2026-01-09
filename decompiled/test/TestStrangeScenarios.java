package com.tann.dice.test;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobHuge;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.global.linked.GlobalNumberLimit;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.gameplay.trigger.personal.hp.MaxHP;
import com.tann.dice.gameplay.trigger.personal.onHit.Spiky;
import com.tann.dice.screens.dungeon.TargetingManager;
import com.tann.dice.test.util.Skip;
import com.tann.dice.test.util.Test;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.test.util.TestUtils;
import com.tann.dice.util.Tann;

public class TestStrangeScenarios {
   @Test
   public static void multiTargetStickiness() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{
            HeroTypeUtils.byName("Fighter"),
            HeroTypeUtils.byName("Fighter"),
            HeroTypeUtils.byName("Fighter"),
            HeroTypeUtils.byName("Fighter"),
            HeroTypeUtils.byName("Fighter")
         },
         new MonsterType[]{MonsterTypeLib.byName("testGoblin")}
      );
      Monster skelly = TestUtils.monsters.get(0);
      TestUtils.turnInto(f, skelly, ESB.dmgCleave.val(40), false);
      Ent target = null;

      for (int i = 0; i < 5; i++) {
         target = new TargetingManager(f).getRandomTargetForEnemy(skelly.getDie().getTargetable());
         TestRunner.assertTrue("should not target top or bottom", target != TestUtils.heroes.get(0) && target != TestUtils.heroes.get(4));
      }

      TestUtils.hit(f, skelly, target, ESB.dmgCleave.val(40), true);
      TestRunner.assertEquals("3 dying fighters", 3, TestUtils.countDyingHeroes(f));
      String deathHash = TestUtils.hashDeathState(f, TestUtils.heroes);
      TestRunner.assertTrue("death hash accurate", Tann.countCharsInString('d', deathHash) == 3);
      TestRunner.assertTrue("death hash accurate", Tann.countCharsInString('a', deathHash) == 2);
      TestRunner.assertEquals("death hash should be the same", deathHash, TestUtils.hashDeathState(f, TestUtils.heroes));

      for (int i = 0; i < 10; i++) {
         TestUtils.hit(f, TestUtils.heroes.get(0), skelly, ESB.dmg.val(1), false);
         TestRunner.assertEquals("death hash should be the same", deathHash, TestUtils.hashDeathState(f, TestUtils.heroes));
         TestUtils.undo(f);
         TestRunner.assertEquals("death hash should be the same", deathHash, TestUtils.hashDeathState(f, TestUtils.heroes));
      }

      TestUtils.hit(f, TestUtils.heroes.get(0), skelly, ESB.dmg.val(999), false);
      TestRunner.assertEquals("killing the skelly stops all damage", 0, TestUtils.countDyingHeroes(f));
   }

   @Test
   public static void targetingEfficienct() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Healer"), HeroTypeUtils.byName("Healer"), HeroTypeUtils.byName("Healer"), HeroTypeUtils.byName("Healer")},
         new MonsterType[]{MonsterTypeLib.byName("dragon")}
      );
      Monster dragon = TestUtils.monsters.get(0);
      dragon.getDie().setSide(1);

      for (int iter = 0; iter < 10; iter++) {
         TestRunner.assertEquals("nobody should be dying", 4, f.getSnapshot(FightLog.Temporality.Future).getAliveEntities(true).size());

         for (int i = 0; i < 4; i++) {
            Ent target = new TargetingManager(f).getRandomTargetForEnemy(dragon.getDie().getTargetable());
            TestUtils.rollHit(f, dragon, target, EntSidesBlobHuge.chomp.val(40), false);
         }

         TestRunner.assertEquals("all should be dying", 0, f.getSnapshot(FightLog.Temporality.Future).getAliveEntities(true).size());

         for (int i = 0; i < 8; i++) {
            TestUtils.undo(f);
         }
      }
   }

   @Test
   public static void painShouldKill() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Healer")}, new MonsterType[]{MonsterTypeLib.byName("testGoblin"), MonsterTypeLib.byName("testGoblin")}
      );
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.rollHit(f, h, m, ESB.dmgPain.val(3), false);
      TestRunner.assertEquals("hero should damaged for 3", h.getHeroType().hp - 3, TestUtils.getState(f, h, FightLog.Temporality.Present).getHp());
      TestRunner.assertEquals("hero should be alive", false, TestUtils.getState(f, h, FightLog.Temporality.Present).isDead());
      TestUtils.rollHit(f, h, m, ESB.dmgPain.val(30), false);
      TestRunner.assertEquals("hero should be dead", true, TestUtils.getState(f, h, FightLog.Temporality.Present).isDead());
   }

   @Test
   public static void maxHpFiddles() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      int startMaxHp = TestUtils.getState(f, h, FightLog.Temporality.Present).getMaxHp();
      TestUtils.hit(f, h, new MaxHP(1), false);
      TestRunner.assertEquals("max hp should be +1", startMaxHp + 1, TestUtils.getState(f, h, FightLog.Temporality.Present).getMaxHp());
      TestUtils.hit(f, h, new MaxHP(-4), false);
      TestRunner.assertEquals("max hp should be -3", startMaxHp - 3, TestUtils.getState(f, h, FightLog.Temporality.Present).getMaxHp());
   }

   @Test
   public static void maxHpLimit() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      int startMaxHp = TestUtils.getState(f, h, FightLog.Temporality.Present).getMaxHp();
      TestUtils.hit(f, h, new MaxHP(-startMaxHp), false);
      TestRunner.assertEquals("max hp should be limited to 1", 1, TestUtils.getState(f, h, FightLog.Temporality.Present).getMaxHp());
      TestUtils.hit(f, h, new MaxHP(-400), false);
      TestRunner.assertEquals("start max hp should be limited to 1", 1, TestUtils.getState(f, h, FightLog.Temporality.Present).getMaxHp());
   }

   @Test
   public static void revengeSpiky() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.hit(f, m, new EffBill().buff(new Buff(new Spiky(1))).bEff(), false);
      TestUtils.hit(f, h, new EffBill().damage(h.getHeroType().hp - 1).bEff(), false);
      TestRunner.assertEquals("hero should be on 1hp", 1, TestUtils.getState(f, h, FightLog.Temporality.Present).getHp());
      TestUtils.hit(f, m, h, ESB.dmg.val(3), true);
      TestRunner.assertEquals("hero should be on 1hp", 1, TestUtils.getState(f, h, FightLog.Temporality.Present).getHp());
      TestRunner.assertEquals("hero should be dying in the future", true, TestUtils.getState(f, h, FightLog.Temporality.Future).isDead());
      TestUtils.hit(f, h, h, ESB.shieldRepel.val(1), false);
      TestRunner.assertEquals("hero should be on 1hp", 1, TestUtils.getState(f, h, FightLog.Temporality.Present).getHp());
      TestRunner.assertEquals("monster should be damaged", m.entType.hp - 1, TestUtils.getState(f, m, FightLog.Temporality.Present).getHp());
   }

   @Test
   public static void painDrainNiceness() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Healer")}, new MonsterType[]{MonsterTypeLib.byName("testGoblin"), MonsterTypeLib.byName("testGoblin")}
      );
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.hit(f, h, new EffBill().buff(new Buff(new AffectSides(new AddKeyword(Keyword.selfHeal)))).bEff(), false);
      TestUtils.rollHit(f, h, m, ESB.dmgPain.val(3), false);
      TestRunner.assertEquals("hero should not be damaged", h.getHeroType().hp, TestUtils.getState(f, h, FightLog.Temporality.Present).getHp());
      TestRunner.assertEquals("hero should be alive", false, TestUtils.getState(f, h, FightLog.Temporality.Present).isDead());
      TestUtils.rollHit(f, h, m, ESB.dmgPain.val(30), false);
      TestRunner.assertEquals("hero should not be damaged", h.getHeroType().hp, TestUtils.getState(f, h, FightLog.Temporality.Present).getHp());
      TestRunner.assertEquals("hero should be alive", false, TestUtils.getState(f, h, FightLog.Temporality.Present).isDead());
   }

   @Test
   public static void numberLimit() {
      int limit = 99;
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Healer")},
         new MonsterType[]{MonsterTypeLib.byName("testGoblin"), MonsterTypeLib.byName("testGoblin")},
         new Modifier[]{new Modifier(-1.0F, "test", new GlobalNumberLimit(99))}
      );
      Hero h = TestUtils.heroes.get(0);
      TestUtils.rollHit(f, h, h, ESB.healBoost.val(100000000), false);
      TestRunner.assertEquals(
         "hero sides should be limited to 99", 99, f.getSnapshot(FightLog.Temporality.Present).getState(h).getSideState(0).getCalculatedEffect().getValue()
      );
   }

   @Test
   @Skip
   public static void ironHelmDiedLastTurn() {
      FightLog f = TestUtils.loadFromString(
         "`{v:2038i,d:{n:1,p:{h:[Healer~D~Iron Helm]},l:{m:[Goblin,Rat,Bee]}},s:11200232,p:[\"2{ps:[\\\"!{ct:{cs:Number,v:1},cs:[{am:1,cd:[{t:Levelup,n:Rogue}]},{am:1,cd:[{t:Levelup,n:Vampire}]}]}\\\"]}\",3]}`"
      );
      int hp = f.getSnapshot(FightLog.Temporality.Present).getStates(true, false).get(0).getHp();
      Tann.assertEquals("Should be on 8hp", 8, hp);
   }
}
