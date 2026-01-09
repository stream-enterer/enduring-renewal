package com.tann.dice.test;

import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.group.PartyLayoutType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.trigger.global.heroLevelupAffect.HeroGenType;
import com.tann.dice.test.util.Test;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class TestParty {
   @Test
   public static void noDuplicateHeroes() {
      int attempts = 50;

      for (int i = 0; i < 50; i++) {
         Party p = Party.generate(1, HeroGenType.Normal, PartyLayoutType.Force, new ArrayList<>());
         List<HeroType> types = new ArrayList<>();

         for (Hero hero : p.getHeroes()) {
            types.add(hero.getHeroType());
         }

         List<HeroType> unDupe = new ArrayList<>(types);
         Tann.uniquify(unDupe);
         Tann.assertEquals("Both should have 5 heroes: " + types, types.size(), unDupe.size());
      }
   }

   @Test
   public static void partyLayoutsValid() {
      int attempts = 10;
      List<PartyLayoutType> fails = new ArrayList<>();

      for (PartyLayoutType value : PartyLayoutType.values()) {
         if (!value.isLockedMeta()) {
            for (int i = 0; i < 10; i++) {
               Party p = Party.generate(1, HeroGenType.Normal, value, new ArrayList<>());

               for (Hero hero : p.getHeroes()) {
                  if (hero.isMissingno()) {
                     fails.add(value);
                  }
               }
            }
         }
      }

      Tann.assertTrue("Should be no fails: " + fails, fails.isEmpty());
   }
}
