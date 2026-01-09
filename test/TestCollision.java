package com.tann.dice.test;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.group.PartyLayoutType;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.difficultyConfig.ClassicConfig;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.gameplay.trigger.global.heroLevelupAffect.HeroGenType;
import com.tann.dice.test.util.Skip;
import com.tann.dice.test.util.Slow;
import com.tann.dice.test.util.Test;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class TestCollision {
   @Test
   @Skip
   public static void consistencyPainEdges() {
      shouldCollide(ModifierLib.byName("consistency"), ModifierLib.byName("left.pain"));
   }

   @Test
   public static void fullMoonDemonEyeShouldNotCollide() {
      shouldNotCollide(ItemLib.byName("demon eye"), ItemLib.byName("full moon"));
   }

   private static void shouldNotCollide(Item a, Item b) {
      Tann.assertTrue(a.getName() + " should not collide with " + b.getName(), !ChoosableUtils.collides(a, b));
   }

   private static void shouldCollide(Modifier a, Modifier b) {
      Tann.assertTrue(a.getName() + " should collide with " + b.getName(), ChoosableUtils.collides(a, b));
   }

   @Test
   @Slow
   public static void ensureNoBannedItems() {
      DungeonContext dc = null;
      List<Choosable> bads = new ArrayList<>();
      int attempts = 200;

      for (int i = 0; i < 200; i++) {
         if (i % 19 == 0) {
            dc = new DungeonContext(new ClassicConfig(Difficulty.Normal), Party.generate(5, HeroGenType.Normal, PartyLayoutType.Force, new ArrayList<>()));
         }

         dc.nextLevel();
         List<Choosable> loot = dc.getLootForPreviousLevel();

         for (int i1 = 0; i1 < loot.size(); i1++) {
            Choosable ch = loot.get(i1);
            if (loot.get(i1).getName().equalsIgnoreCase("bonesaw")) {
               throw new RuntimeException("no bonesaw");
            }

            if (!ch.getName().contains("-") && ChoosableUtils.collides(ch, dc.getBannedCollisionBits())) {
               bads.add(ch);
            }
         }
      }

      Tann.assertBads(bads);
      Tann.assertTrue("should have collbit", dc.getBannedCollisionBits() != 0L);
   }

   @Test
   public static void noDabbleInPhysical() {
      for (int i = 0; i < 10; i++) {
         Party p = Party.generate(0, HeroGenType.Normal, PartyLayoutType.Force, new ArrayList<>());

         for (Hero hero : p.getHeroes()) {
            Tann.assertTrue("should not be dabble", !hero.getName(false).equalsIgnoreCase("dabble"));
         }
      }
   }
}
