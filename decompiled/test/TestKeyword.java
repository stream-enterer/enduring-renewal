package com.tann.dice.test;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.content.ent.die.Die;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.EntSidesLib;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.DieTargetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.command.DieCommand;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.test.util.Skip;
import com.tann.dice.test.util.Slow;
import com.tann.dice.test.util.Test;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.test.util.TestUtils;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class TestKeyword {
   @Test
   public static void bloodlust() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Healer")},
         new MonsterType[]{MonsterTypeLib.byName("testGoblin"), MonsterTypeLib.byName("testGoblin"), MonsterTypeLib.byName("testGoblin")}
      );
      Hero h = TestUtils.heroes.get(0);
      TestUtils.rollHit(f, h, TestUtils.monsters.get(0), ESB.dmgBloodlust.val(1), false);
      TestRunner.assertEquals(
         "monster should be hit for 1",
         TestUtils.monsters.get(0).entType.hp - 1,
         TestUtils.getState(f, TestUtils.monsters.get(0), FightLog.Temporality.Present).getHp()
      );
      TestUtils.rollHit(f, h, TestUtils.monsters.get(0), ESB.dmgBloodlust.val(1), false);
      TestRunner.assertEquals(
         "monster should be hit for 2",
         TestUtils.monsters.get(0).entType.hp - 3,
         TestUtils.getState(f, TestUtils.monsters.get(0), FightLog.Temporality.Present).getHp()
      );
      TestUtils.rollHit(f, h, TestUtils.monsters.get(1), ESB.dmgBloodlust.val(1), false);
      TestRunner.assertEquals(
         "monster should be hit for 2",
         TestUtils.monsters.get(1).entType.hp - 2,
         TestUtils.getState(f, TestUtils.monsters.get(1), FightLog.Temporality.Present).getHp()
      );
      TestUtils.rollHit(f, h, TestUtils.monsters.get(2), ESB.dmgBloodlust.val(1), false);
      TestRunner.assertEquals(
         "monster should be hit for 3",
         TestUtils.monsters.get(2).entType.hp - 3,
         TestUtils.getState(f, TestUtils.monsters.get(2), FightLog.Temporality.Present).getHp()
      );
   }

   @Test
   public static void growth() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      TestUtils.turnInto(f, h, ESB.shieldMana.val(1), false);
      TestUtils.hit(f, h, new EffBill().buff(new Buff(new AffectSides(new AddKeyword(Keyword.growth)))).bEff(), false);
      f.addCommand(new DieCommand(new DieTargetable(h, 0), h), false);
      TestRunner.assertEquals("shield should be 1", 1, TestUtils.getState(f, h, FightLog.Temporality.Present).getShields());
      TestRunner.assertEquals("mana should be 1", 1, f.getSnapshot(FightLog.Temporality.Present).getTotalMana());
      f.addCommand(new DieCommand(new DieTargetable(h, 0), h), false);
      TestRunner.assertEquals("shield should be 3", 3, TestUtils.getState(f, h, FightLog.Temporality.Present).getShields());
      TestRunner.assertEquals("mana should be 3", 3, f.getSnapshot(FightLog.Temporality.Present).getTotalMana());
      f.addCommand(new DieCommand(new DieTargetable(h, 0), h), false);
      TestRunner.assertEquals("shield should be 6", 6, TestUtils.getState(f, h, FightLog.Temporality.Present).getShields());
      TestRunner.assertEquals("mana should be 6", 6, f.getSnapshot(FightLog.Temporality.Present).getTotalMana());
   }

   @Test
   public static void rescue() {
      FightLog f = TestUtils.setupFight();
      TestRunner.assertEquals("should be 'ready'", false, TestUtils.getState(f, TestUtils.heroes.get(0), FightLog.Temporality.Present).isUsed());
      TestUtils.attack(f, null, TestUtils.heroes.get(0), 1, false);
      TestUtils.attack(f, null, TestUtils.heroes.get(0), TestUtils.heroes.get(0).getHeroType().hp - 1, true);
      TestUtils.rollHit(f, TestUtils.heroes.get(0), TestUtils.heroes.get(0), ESB.healRescue.val(1), false);
      TestRunner.assertEquals("should be 'ready'", false, TestUtils.getState(f, TestUtils.heroes.get(0), FightLog.Temporality.Present).isUsed());
      TestUtils.rollHit(f, TestUtils.heroes.get(0), TestUtils.heroes.get(0), ESB.healRescue.val(1), false);
      TestRunner.assertEquals("should be 'used'", true, TestUtils.getState(f, TestUtils.heroes.get(0), FightLog.Temporality.Present).isUsed());
   }

   @Test
   public static void poison() {
      FightLog f = TestUtils.setupFight();
      TestUtils.rollHit(f, TestUtils.heroes.get(0), TestUtils.monsters.get(0), ESB.dmgPoison.val(1), false);
      EntState present = TestUtils.getState(f, TestUtils.monsters.get(0), FightLog.Temporality.Present);
      TestRunner.assertEquals("should be damaged for 1", present.getMaxHp() - 1, present.getHp());
      EntState future = TestUtils.getState(f, TestUtils.monsters.get(0), FightLog.Temporality.Future);
      TestRunner.assertEquals("should be damaged by poison", future.getMaxHp() - 2, future.getHp());
   }

   @Test
   public static void precise() {
      FightLog f = TestUtils.setupFight();
      TestUtils.rollHit(f, TestUtils.heroes.get(0), TestUtils.monsters.get(0), ESB.dmgEngage.val(1), false);
      EntState present = TestUtils.getState(f, TestUtils.monsters.get(0), FightLog.Temporality.Present);
      TestRunner.assertEquals("should be damaged for 2", present.getMaxHp() - 2, present.getHp());
      TestUtils.rollHit(f, TestUtils.heroes.get(0), TestUtils.monsters.get(0), ESB.dmgEngage.val(1), false);
      present = TestUtils.getState(f, TestUtils.monsters.get(0), FightLog.Temporality.Present);
      TestRunner.assertEquals("should be damaged for 3", present.getMaxHp() - 3, present.getHp());
   }

   @Test
   public static void fierce() {
      FightLog f = TestUtils.setupFight();
      TestUtils.rollHit(f, TestUtils.heroes.get(0), TestUtils.monsters.get(0), ESB.dmgCruel.val(1), false);
      TestUtils.rollHit(f, TestUtils.heroes.get(0), TestUtils.monsters.get(0), ESB.dmgCruel.val(1), false);
      TestUtils.rollHit(f, TestUtils.heroes.get(0), TestUtils.monsters.get(0), ESB.dmgCruel.val(1), false);
      EntState present = TestUtils.getState(f, TestUtils.monsters.get(0), FightLog.Temporality.Present);
      TestRunner.assertEquals("should be damaged for 3", present.getMaxHp() - 3, present.getHp());
      TestUtils.rollHit(f, TestUtils.heroes.get(0), TestUtils.monsters.get(0), ESB.dmgCruel.val(1), false);
      present = TestUtils.getState(f, TestUtils.monsters.get(0), FightLog.Temporality.Present);
      TestRunner.assertEquals("should be damaged for 2", present.getMaxHp() - 5, present.getHp());
   }

   @Test
   public static void weaken() {
      FightLog f = TestUtils.setupFight();
      Monster monster = TestUtils.monsters.get(0);
      Hero hero = TestUtils.heroes.get(0);
      TestUtils.rollHit(f, monster, hero, ESB.dmg.val(3), true);
      EntState heroState = f.getState(FightLog.Temporality.Future, hero);
      TestRunner.assertEquals("hero should be hit for 1 damage", heroState.getMaxHp() - 3, heroState.getHp());
      TestUtils.rollHit(f, hero, monster, ESB.dmgWeaken.val(2), false);
      heroState = f.getState(FightLog.Temporality.Future, hero);
      TestRunner.assertEquals("damage should be reduced by 2", heroState.getMaxHp() - 1, heroState.getHp());
   }

   @Test
   public static void drain() {
      FightLog f = TestUtils.setupFight();
      TestUtils.rollHit(f, TestUtils.monsters.get(0), TestUtils.heroes.get(0), ESB.dmg.val(3), false);
      EntState present = TestUtils.getState(f, TestUtils.heroes.get(0), FightLog.Temporality.Future);
      TestRunner.assertEquals("should be damaged for 3", present.getMaxHp() - 3, present.getHp());
      TestUtils.rollHit(f, TestUtils.heroes.get(0), TestUtils.monsters.get(0), ESB.dmgSelfHeal.val(1), false);
      present = TestUtils.getState(f, TestUtils.heroes.get(0), FightLog.Temporality.Future);
      TestRunner.assertEquals("should be damaged for 2", present.getMaxHp() - 2, present.getHp());
   }

   @Test
   public static void lifestealVsInvincible() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      int heroMax = h.getHeroType().hp;
      TestUtils.attack(f, m, h, 3, false);
      TestRunner.assertEquals("hero should have taken 3 damage", heroMax - 3, TestUtils.getState(f, h, FightLog.Temporality.Present).getHp());
      TestUtils.rollHit(f, h, m, ESB.dmgSelfHeal.val(1), false);
      TestRunner.assertEquals("hero should have healed 1 damage", heroMax - 2, TestUtils.getState(f, h, FightLog.Temporality.Present).getHp());
      TestUtils.hit(f, m, m, ESB.dodge, false);
      TestUtils.rollHit(f, h, m, ESB.dmgSelfHeal.val(1), false);
      TestRunner.assertEquals("hero should still heal additional damage", heroMax - 1, TestUtils.getState(f, h, FightLog.Temporality.Present).getHp());
   }

   @Test
   public static void precisePlusMagic() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.rollHit(f, h, h, ESB.shieldMana.val(1), false);
      TestRunner.assertEquals("hero should be shielded for 1", 1, TestUtils.getState(f, h, FightLog.Temporality.Present).getShields());
      TestRunner.assertEquals("should have 1 mana", 1, f.getSnapshot(FightLog.Temporality.Present).getTotalMana());
      TestUtils.hit(f, h, new EffBill().buff(new Buff(new AffectSides(new AddKeyword(Keyword.engage)))).bEff(), false);
      TestUtils.rollHit(f, h, h, ESB.shieldMana.val(1), false);
      TestRunner.assertEquals("hero should be shielded for 2 more", 3, TestUtils.getState(f, h, FightLog.Temporality.Present).getShields());
      TestRunner.assertEquals("should have 3 mana", 3, f.getSnapshot(FightLog.Temporality.Present).getTotalMana());
   }

   @Test
   public static void regen() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.rollHit(f, m, h, ESB.dmgPain.val(3), false);
      TestRunner.assertEquals("hero should damaged for 3", h.getHeroType().hp - 3, TestUtils.getState(f, h, FightLog.Temporality.Present).getHp());
      TestUtils.rollHit(f, h, h, ESB.healRegen.val(1), false);
      TestRunner.assertEquals("hero should heal 1", h.getHeroType().hp - 2, TestUtils.getState(f, h, FightLog.Temporality.Present).getHp());
      TestUtils.nextTurn(f);
      TestRunner.assertEquals("hero should regen 1", h.getHeroType().hp - 1, TestUtils.getState(f, h, FightLog.Temporality.Present).getHp());
      TestUtils.nextTurn(f);
      TestRunner.assertEquals("hero should regen 2", h.getHeroType().hp, TestUtils.getState(f, h, FightLog.Temporality.Present).getHp());
      TestUtils.nextTurn(f);
      TestUtils.nextTurn(f);
      TestUtils.nextTurn(f);
      TestUtils.nextTurn(f);
      TestUtils.nextTurn(f);
      TestRunner.assertEquals("hero should not regen past full", h.getHeroType().hp, TestUtils.getState(f, h, FightLog.Temporality.Present).getHp());
   }

   @Test
   public static void pain() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.rollHit(f, h, m, ESB.dmgPain.val(3), false);
      TestRunner.assertEquals("hero should damaged for 3", h.getHeroType().hp - 3, TestUtils.getState(f, h, FightLog.Temporality.Present).getHp());
   }

   @Test
   @Slow
   public static void testAllAbilitiesAllKeywords() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Healer")},
         new MonsterType[]{MonsterTypeLib.byName("testGoblin"), MonsterTypeLib.byName("testGoblin"), MonsterTypeLib.byName("testGoblin")}
      );
      SpriteBatch batch = new SpriteBatch();
      batch.begin();
      List<EntSide> sides = EntSidesLib.getAllSidesWithValue();

      for (Keyword k : Keyword.values()) {
         if (!k.skipDebug()) {
            for (EntSide s : sides) {
               boolean added = false;
               Eff e = s.getBaseEffect().copy();
               if (KUtils.allowAddingKeyword(k, e)) {
                  e.addKeyword(k);
                  added = true;
               }

               if (added) {
                  Ent target = TestUtils.monsters.get(0);
                  if (e.isFriendly()) {
                     target = TestUtils.heroes.get(0);
                  }

                  if (!e.needsTarget()) {
                     target = null;
                  }

                  try {
                     TestUtils.rollHit(f, TestUtils.heroes.get(0), target, s, false);
                  } catch (Exception var13) {
                     System.err.println("error with " + s.getBaseEffect().describe() + " and " + k);
                     throw var13;
                  }

                  TestUtils.undo(f);
                  TestUtils.undo(f);
               }
            }
         }
      }

      batch.end();
      batch.dispose();
   }

   @Test
   @Slow
   public static void testCantripAllKeywords() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Healer"), HeroTypeUtils.byName("Defender")},
         new MonsterType[]{MonsterTypeLib.byName("testGoblin"), MonsterTypeLib.byName("testGoblin"), MonsterTypeLib.byName("testGoblin")}
      );
      Hero first = TestUtils.heroes.get(0);
      Hero second = TestUtils.heroes.get(1);
      second.getDie().setState(Die.DieState.Rolling);

      for (EntSide s : EntSidesLib.getAllSidesWithValue()) {
         boolean added = false;
         Eff e = s.getBaseEffect().copy();
         if (KUtils.allowAddingKeyword(Keyword.cantrip, e)) {
            e.addKeyword(Keyword.cantrip);
            added = true;
         }

         if (added) {
            TestUtils.turnInto(f, first, s, false);
            first.activateCantrip(f);
            TestUtils.undo(f);
         }
      }
   }

   @Test
   public static void testComboCruel() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Healer"), HeroTypeUtils.byName("Defender")}, new MonsterType[]{MonsterTypeLib.byName("dragon")}
      );
      Hero a = TestUtils.heroes.get(0);
      Hero b = TestUtils.heroes.get(1);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.turnInto(f, a, ESB.dmgCopycat.val(1), false);
      TestUtils.turnInto(f, b, ESB.dmgCruel.val(1), false);
      TestRunner.assertEquals(
         "Hero a's copycat side should only have 1 keyword", 1, TestUtils.getState(f, a).getSideState(0).getCalculatedEffect().getKeywords().size()
      );
      TestUtils.roll(f, b, m, 0, false);
      TestRunner.assertEquals(
         "Hero a's copycat side should have 2 keywords", 2, TestUtils.getState(f, a).getSideState(0).getCalculatedEffect().getKeywords().size()
      );
   }

   @Test
   public static void testComboCruelWand() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Healer"), HeroTypeUtils.byName("Defender")}, new MonsterType[]{MonsterTypeLib.byName("dragon")}
      );
      Hero a = TestUtils.heroes.get(0);
      Hero b = TestUtils.heroes.get(1);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.turnInto(f, a, ESB.dmgCopycat.val(1), false);
      TestUtils.turnInto(f, b, ESB.wandFire.val(1), false);
      TestRunner.assertEquals(
         "Hero a's copycat side should only have 1 keyword", 1, TestUtils.getState(f, a).getSideState(0).getCalculatedEffect().getKeywords().size()
      );
      TestUtils.roll(f, b, m, 0, false);
      TestRunner.assertEquals(
         "Hero a's copycat side should have 3 keywords", 3, TestUtils.getState(f, a).getSideState(0).getCalculatedEffect().getKeywords().size()
      );
   }

   @Test
   public static void rampageHeroKill() {
      FightLog f = TestUtils.setupFight(
         new HeroType[]{HeroTypeUtils.byName("Healer"), HeroTypeUtils.byName("Defender")},
         new MonsterType[]{MonsterTypeLib.byName("testGoblin"), MonsterTypeLib.byName("testGoblin")}
      );
      Hero a = TestUtils.heroes.get(0);
      Hero b = TestUtils.heroes.get(1);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.hit(f, m, new EffBill().damage(m.entType.hp - 1).bEff());
      TestUtils.hit(f, b, new EffBill().damage(b.getHeroType().hp - 2).bEff());
      Tann.assertTrue("dice should be unused", !TestUtils.getState(f, a).isUsed());
      TestUtils.rollHit(f, a, null, ESB.burningFlail.val(1), false);
      Tann.assertTrue("dice should be unused", !TestUtils.getState(f, a).isUsed());
      TestUtils.rollHit(f, a, null, ESB.burningFlail.val(1), false);
      Tann.assertTrue("dice should be unused", !TestUtils.getState(f, a).isUsed());
      TestUtils.rollHit(f, a, null, ESB.burningFlail.val(1), false);
      Tann.assertTrue("dice should be used", TestUtils.getState(f, a).isUsed());
   }

   @Test
   @Skip
   public static void noLightKeywordsWithoutIcon() {
      List<Keyword> bads = new ArrayList<>();

      for (Keyword value : Keyword.values()) {
         if (value.getColour() == Colours.light && ((AtlasRegion)value.getImage()).name.contains("special")) {
            bads.add(value);
         }
      }

      Tann.assertBads(bads);
   }
}
