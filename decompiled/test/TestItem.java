package com.tann.dice.test;

import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.item.copyItem.CopyAlliedItems;
import com.tann.dice.test.util.Test;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.test.util.TestUtils;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class TestItem {
   @Test
   public static void testGauntlet() {
      FightLog f = TestUtils.setupFight();
      Hero source = TestUtils.heroes.get(0);
      source.addItem(ItemLib.byName("Gauntlet"));
      TestUtils.rollHit(f, source, TestUtils.monsters.get(0), source.getDie().getSide(0), false);
      EntState monsterState = f.getState(FightLog.Temporality.Future, TestUtils.monsters.get(0));
      TestRunner.assertEquals("Monster should be damaged for 2", monsterState.getMaxHp() - 2, monsterState.getHp());
   }

   @Test
   public static void testSteelHeart() {
      FightLog f = TestUtils.setupFight(new HeroType[]{HeroTypeUtils.byName("Fighter")}, new MonsterType[]{MonsterTypeLib.byName("archer")});
      Hero warrior = TestUtils.heroes.get(0);
      warrior.addItem(ItemLib.byName("Faint Halo"));
      int initialMaxHp = warrior.getHeroType().hp;
      TestUtils.rollHit(f, TestUtils.heroes.get(0), TestUtils.heroes.get(0), ESB.shield.val(1), false);
      TestRunner.assertEquals(
         "max hp shouldn't increase from just shielding", initialMaxHp, TestUtils.getState(f, warrior, FightLog.Temporality.Present).getMaxHp()
      );
      TestUtils.attack(f, null, warrior, initialMaxHp + 1, true);
      TestRunner.assertEquals("should be dying", 0, TestUtils.getState(f, warrior, FightLog.Temporality.Future).getHp());
      TestRunner.assertEquals("even if he's then dying after", initialMaxHp, TestUtils.getState(f, warrior, FightLog.Temporality.Present).getMaxHp());
      TestUtils.rollHit(f, TestUtils.heroes.get(0), TestUtils.heroes.get(0), ESB.shield.val(1), false);
      TestRunner.assertEquals(
         "hp should increase when you get saved", initialMaxHp + 1, TestUtils.getState(f, warrior, FightLog.Temporality.Present).getMaxHp()
      );
   }

   @Test
   public static void testPipeAndStudsWithHealShield() {
      FightLog f = TestUtils.setupFight(new HeroType[]{HeroTypeUtils.byName("Fighter")}, new MonsterType[]{MonsterTypeLib.byName("archer")});
      Hero warrior = TestUtils.heroes.get(0);
      warrior.addItem(ItemLib.byName("Dragon Pipe"));
      warrior.addItem(ItemLib.byName("Metal Studs"));
      TestUtils.hit(f, warrior, new EffBill().damage(4).bEff(), false);
      TestRunner.assertEquals("warrior should be on 1hp", 1, TestUtils.getState(f, warrior, FightLog.Temporality.Present).getHp());
      TestUtils.rollHit(f, warrior, warrior, ESB.healShield.val(1), false);
      TestRunner.assertEquals("warrior should be on 4hp", 4, TestUtils.getState(f, warrior, FightLog.Temporality.Present).getHp());
      TestRunner.assertEquals("warrior should have 3 shields", 3, TestUtils.getState(f, warrior, FightLog.Temporality.Present).getShields());
   }

   @Test
   public static void testBonusIncomingWithHealShields() {
      FightLog f = TestUtils.setupFight(new HeroType[]{HeroTypeUtils.byName("Fighter")}, new MonsterType[]{MonsterTypeLib.byName("archer")});
      Hero warrior = TestUtils.heroes.get(0);
      warrior.addItem(ItemLib.byName("Iron Pendant"));
      warrior.addItem(ItemLib.byName("Garnet"));
      TestUtils.hit(f, warrior, new EffBill().damage(4).bEff(), false);
      TestRunner.assertEquals("warrior should be on 1hp", 1, TestUtils.getState(f, warrior, FightLog.Temporality.Present).getHp());
      TestUtils.rollHit(f, warrior, warrior, ESB.healShield.val(1), false);
      TestRunner.assertEquals("warrior should be on 3hp", 3, TestUtils.getState(f, warrior, FightLog.Temporality.Present).getHp());
      TestRunner.assertEquals("warrior should have 2 shields", 2, TestUtils.getState(f, warrior, FightLog.Temporality.Present).getShields());
   }

   @Test
   public static void noPotionsCopiable() {
      List<Integer> copiables = new ArrayList<>();
      List<Integer> potions = new ArrayList<>();

      for (Item item : ItemLib.getMasterCopy()) {
         if (item.getTier() != 0) {
            if (item.isPotion()) {
               potions.add(item.getTier());
            }

            for (Personal personalTrigger : item.getPersonals()) {
               if (personalTrigger instanceof CopyAlliedItems) {
                  CopyAlliedItems cai = (CopyAlliedItems)personalTrigger;

                  for (int i = cai.minTier; i <= cai.maxTier; i++) {
                     copiables.add(i);
                  }
               }
            }
         }
      }

      Tann.uniquify(potions);
      Tann.uniquify(copiables);
      Tann.assertTrue("Should be no shared potion items", !Tann.anySharedItems(potions, copiables));
   }
}
