package com.tann.dice.test;

import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.HasKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.FlatBonus;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.RemoveKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;
import com.tann.dice.test.util.Test;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.test.util.TestUtils;
import java.util.Arrays;

public class TestTriggerOrdering {
   @Test
   public static void testBuffReplacedSides() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.hit(f, TestUtils.heroes.get(0), new AffectSides(SpecificSidesType.RightMost, new ReplaceWith(ESB.arrow.val(1))), false);
      TestUtils.hit(f, TestUtils.heroes.get(0), new AffectSides(SpecificSidesType.RightMost, new FlatBonus(1)), false);
      TestUtils.roll(f, h, m, 5, false);
      TestRunner.assertEquals("2 dmg should be dealt", m.entType.hp - 2, TestUtils.getState(f, m, FightLog.Temporality.Present).getHp());
      TestUtils.hit(f, TestUtils.heroes.get(0), new AffectSides(new HasKeyword(Keyword.ranged), new FlatBonus(1)), false);
      TestUtils.roll(f, h, m, 5, false);
      TestRunner.assertEquals("+3 dmg should be dealt", m.entType.hp - 5, TestUtils.getState(f, m, FightLog.Temporality.Present).getHp());
   }

   @Test
   public static void testBrainGrips() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      Monster m = TestUtils.monsters.get(0);
      TestUtils.turnInto(f, h, ESB.wandSelfHeal.val(1), false);
      TestUtils.addTrigger(f, h, new AffectSides(new HasKeyword(Keyword.singleUse), new FlatBonus(1)));
      TestRunner.assertEquals("Should be 2 damage", 2, f.getState(FightLog.Temporality.Present, h).getSideState(0).getCalculatedEffect().getValue());
      TestUtils.addTrigger(f, h, new AffectSides(new RemoveKeyword(Keyword.singleUse)));
      TestRunner.assertEquals("Should be 2 damage", 2, f.getState(FightLog.Temporality.Present, h).getSideState(0).getCalculatedEffect().getValue());
   }

   @Test
   public static void testTriggerHPOrdering() {
      Modifier[] mods = new Modifier[]{ModifierLib.byName("a"), ModifierLib.byName("Monster Hp Down^2")};
      FightLog f = TestUtils.setupFight(new HeroType[]{HeroTypeUtils.byName("Mage")}, new MonsterType[]{MonsterTypeLib.byName("archer")}, mods);
      Monster m = TestUtils.monsters.get(0);
      TestRunner.assertEquals("hp should be at 1", 1, TestUtils.getState(f, m, FightLog.Temporality.Present).getHp());
      mods = new Modifier[]{ModifierLib.byName("Monster Hp Down^2"), ModifierLib.byName("a")};
      f = TestUtils.setupFight(new HeroType[]{HeroTypeUtils.byName("Mage")}, new MonsterType[]{MonsterTypeLib.byName("archer")}, mods);
      m = TestUtils.monsters.get(0);
      TestRunner.assertEquals("hp should be at 1", 1, TestUtils.getState(f, m, FightLog.Temporality.Present).getHp());
      mods = new Modifier[0];
      f = TestUtils.setupFight(new HeroType[]{HeroTypeUtils.byName("Mage")}, new MonsterType[]{MonsterTypeLib.byName("archer")}, mods);
      m = TestUtils.monsters.get(0);
      TestRunner.assertEquals("hp should be at 2", 2, TestUtils.getState(f, m, FightLog.Temporality.Present).getHp());
   }

   @Test
   public static void testCreakyJointsSword() {
      Hero h = HeroTypeUtils.byName("Healer").makeEnt();
      Hero h2 = HeroTypeUtils.byName("Thief").makeEnt();
      h2.addItem(ItemLib.byName("shortsword"));
      FightLog f = TestUtils.setupFight(
         Arrays.asList(h, h2), Arrays.asList(MonsterTypeLib.byName("testGoblin").makeEnt()), new Modifier[]{ModifierLib.byName("creaky joints")}
      );
      int side = 4;
      TestRunner.assertEquals("Should be mana side", EffType.Mana, TestUtils.getState(f, h).getSideState(side).getCalculatedEffect().getType());
      TestRunner.assertEquals("Should be sword side", EffType.Damage, TestUtils.getState(f, h2).getSideState(side).getCalculatedEffect().getType());
      TestRunner.assertEquals("Should be value 0", 0, TestUtils.getState(f, h).getSideState(side).getCalculatedEffect().getValue());
      TestRunner.assertEquals("Should be value 1", 1, TestUtils.getState(f, h2).getSideState(side).getCalculatedEffect().getValue());
      TestUtils.nextTurn(f);
      TestRunner.assertEquals("Should be value 1", 1, TestUtils.getState(f, h).getSideState(side).getCalculatedEffect().getValue());
      TestRunner.assertEquals("Should be value 1", 2, TestUtils.getState(f, h2).getSideState(side).getCalculatedEffect().getValue());
   }
}
