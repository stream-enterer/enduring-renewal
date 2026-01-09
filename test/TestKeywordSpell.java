package com.tann.dice.test;

import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellBill;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.test.util.Test;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.test.util.TestUtils;

public class TestKeywordSpell {
   @Test
   public static void engage() {
      FightLog f = TestUtils.setupFight();
      Monster m = TestUtils.monsters.get(0);
      TestRunner.assertEquals("monster should be undamaged", m.entType.hp, TestUtils.getState(f, m, FightLog.Temporality.Present).getHp());
      Spell s = new SpellBill().debug().eff(new EffBill().damage(1).keywords(Keyword.engage)).bSpell();
      TestUtils.spell(f, s, m);
      TestRunner.assertEquals("monster should be damaged for 2", m.entType.hp - 2, TestUtils.getState(f, m, FightLog.Temporality.Present).getHp());
      TestUtils.spell(f, s, m);
      TestRunner.assertEquals("monster should be damaged for 1 more (3)", m.entType.hp - 3, TestUtils.getState(f, m, FightLog.Temporality.Present).getHp());
   }

   @Test
   public static void cruel() {
      FightLog f = TestUtils.setupFight();
      Hero h = TestUtils.heroes.get(0);
      TestUtils.hit(f, h, new EffBill().damage((int)Math.ceil(h.entType.hp / 2.0F)).bEff(), false);
      int hp = TestUtils.getState(f, h).getHp();
      TestRunner.assertTrue("hp should be less than half", hp <= h.getHeroType().hp / 2.0F);
      Spell s = new SpellBill().debug().eff(new EffBill().heal(1).keywords(Keyword.cruel)).bSpell();
      TestUtils.spell(f, s, h);
      TestRunner.assertEquals("hero should be healed for 2", hp + 2, TestUtils.getState(f, h).getHp());
      TestUtils.spell(f, s, h);
      TestRunner.assertEquals("hero should be healed for 1 more", hp + 3, TestUtils.getState(f, h).getHp());
   }
}
