package com.tann.dice.test;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.group.PartyLayoutType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.difficultyConfig.ClassicConfig;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.trigger.global.heroLevelupAffect.HeroGenType;
import com.tann.dice.test.util.Skip;
import com.tann.dice.test.util.Test;
import com.tann.dice.util.Tann;
import java.util.ArrayList;

public class TestHeroes {
   @Test
   @Skip
   public static void testGlint() {
      HeroType ht = HeroTypeLib.byName("pockets");
      if (!UnUtil.isLocked(ht)) {
         int totalGlints = 0;

         for (int i = 0; i < 30; i++) {
            Party p = Party.generate(0, HeroGenType.Normal, PartyLayoutType.Greens, new ArrayList<>());
            new DungeonContext(new ClassicConfig(Difficulty.Normal), p);
            int glints = 0;

            for (Hero hero : p.getHeroes()) {
               if (hero.getName(false).equalsIgnoreCase("pockets")) {
                  glints += 2;
               }
            }

            totalGlints += glints;
            Tann.assertEquals("Should have pockets many items", glints, p.getItems(null).size());
         }

         Tann.assertTrue("Should have glint offered", totalGlints > 0);
      }
   }
}
