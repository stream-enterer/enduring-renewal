package com.tann.dice.test;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.group.PartyLayoutType;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.cursed.CurseConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.ClassicConfig;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.ModifierPickContext;
import com.tann.dice.gameplay.modifier.ModifierPickUtils;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoicePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.RandomTieredChoosable;
import com.tann.dice.gameplay.save.settings.option.BOption;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.heroLevelupAffect.HeroGenType;
import com.tann.dice.gameplay.trigger.global.item.GlobalStartWithItem;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorDifficulty;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorModifierPick;
import com.tann.dice.test.util.SkipNonTann;
import com.tann.dice.test.util.Slow;
import com.tann.dice.test.util.Test;
import com.tann.dice.util.DebugUtilsUseful;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestModifierOffer {
   private static List<BOption> toSet = Arrays.asList(OptionLib.WILD_MODIFIERS, OptionLib.COMPLEX_HARD_EASY, OptionLib.MYRIAD_OFFERS);
   private static List<Boolean> oldVals = new ArrayList<>();

   private static void cleanOptions() {
      for (BOption bOption : toSet) {
         oldVals.add(bOption.c());
         bOption.setValue(false, false);
      }
   }

   private static void uncleanOptions() {
      for (int i = 0; i < toSet.size(); i++) {
         toSet.get(i).setValue(oldVals.get(i), false);
      }

      oldVals.clear();
   }

   @Test
   public static void checkWearinessPlusWanded() {
      Tann.assertTrue(
         "Wanded & Weariness should collide",
         ChoosableUtils.collides(ModifierLib.byName("Wanded"), ModifierLib.byName(GlobalStartWithItem.nameFor("Weariness")))
      );
   }

   @Test
   public static void checkExtraFewerRerolls() {
      Tann.assertTrue("Extra + Fewer rerolls should collide", ChoosableUtils.collides(ModifierLib.byName("-1 Reroll"), ModifierLib.byName("+2 Rerolls")));
   }

   @Test
   public static void checkMonsterWandPristine() {
      Tann.assertTrue(
         "Monster Wand/Pristine should collide",
         ChoosableUtils.collides(ModifierLib.byName("Monster " + Keyword.singleUse.getName()), ModifierLib.byName("Monster Pristine"))
      );
   }

   @Test
   public static void testCursedCurseLevelups() {
      int NUM_ATTEMPTS = 3;
      int NUM_MODIFIERS = 50;
      int upgradesOffered = 0;

      for (int attempt = 0; attempt < 3; attempt++) {
         DungeonContext dc = new CurseConfig().makeContext();

         for (int i = 0; i < 50; i++) {
            List<Modifier> mods = ModifierPickUtils.generateModifiers(-1, 3, ModifierPickContext.Cursed, dc);

            for (Modifier mod : mods) {
               if (mod.getTier() != -1) {
                  upgradesOffered++;
               }
            }

            Tann.random(mods).onChoose(dc, 0);
         }
      }

      Tann.assertTrue("Upgrades should be offered", upgradesOffered > 0);
   }

   @Test
   public static void testCursedNeverTwoWithSameEssence() {
      int NUM_ATTEMPTS = 3;
      int NUM_MODIFIERS = 50;

      for (int attempt = 0; attempt < 3; attempt++) {
         Set<String> essences = new HashSet<>();
         DungeonContext dc = new CurseConfig().makeContext();

         for (int i = 0; i < 50; i++) {
            List<Modifier> mods = ModifierPickUtils.generateModifiers(-1, 3, ModifierPickContext.Cursed, dc);
            List<Choosable> chch = new ArrayList<>(mods);
            PhaseGeneratorModifierPick.maybeTransformChoosablesCursed(dc, chch, -1);
            Tann.random(chch).onChoose(dc, 0);
         }

         for (Modifier currentModifier : dc.getCurrentModifiers()) {
            if (!currentModifier.getName().contains("\\.")) {
               String essence = currentModifier.getEssence();
               if (essence != null) {
                  Tann.assertTrue("not already: " + currentModifier + ":" + essence, !essences.contains(essence));
                  essences.add(essence);
               }
            }
         }
      }
   }

   @Test
   @SkipNonTann
   public static void basicOfferPerformance() {
      cleanOptions();
      int attempts = 10;
      long maxMSPer = 10L;
      long maxTotal = 100L;
      List<String> errors = new ArrayList<>();

      for (Difficulty d : Difficulty.values()) {
         ModifierLib.getCache().clear();
         long t = System.currentTimeMillis();

         for (int i = 0; i < 10; i++) {
            PhaseGeneratorDifficulty.getModifiersForChoiceDebug(d);
            if (i == 0) {
               long first = System.currentTimeMillis() - t;
               TannLog.log("First for: " + d + " took " + first);
            }
         }

         long delta = System.currentTimeMillis() - t;
         if (delta > 100L) {
            errors.add(d + " took " + delta + " for " + 10 + " (max " + 100L);
         }
      }

      uncleanOptions();
      Tann.assertTrue("Should be no errors: " + errors, errors.isEmpty());
   }

   @Test
   @Slow
   public static void checkForDuplicates() {
      cleanOptions();
      int attempts = 1000;
      List<String> errors = new ArrayList<>();
      Difficulty d = Difficulty.Unfair;

      for (int attemptIndex = 0; attemptIndex < 1000; attemptIndex++) {
         List<Modifier> mods = PhaseGeneratorDifficulty.getModifiersForChoiceDebug(d);

         for (int i = 0; i < mods.size(); i++) {
            for (int j = i + 1; j < mods.size(); j++) {
               if (mods.get(i).getName().equalsIgnoreCase(mods.get(j).getName())) {
                  errors.add(mods.get(i).getName());
               }
            }
         }
      }

      Tann.assertTrue("Should be no errors: " + errors, errors.isEmpty());
   }

   @Test
   @Slow
   public static void pickCurses() {
      DungeonContext dc = DebugUtilsUseful.dummyContext(new CurseConfig());
      RandomTieredChoosable rtc = new RandomTieredChoosable(-1, 1, ChoosableType.Modifier);
      int amt = 1000;

      for (int i = 0; i < 1000; i++) {
         rtc.onChoose(dc, 0);
      }

      int numMissingno = 0;
      List<String> names = new ArrayList<>();

      for (Modifier currentModifier : dc.getCurrentModifiers()) {
         if (currentModifier.isMissingno()) {
            numMissingno++;
         }

         names.add(currentModifier.getName());
      }

      Tann.assertEquals("Should be no missingno", 0, numMissingno);
      List<String> bad = new ArrayList<>();
      int pre = names.size();

      for (int i = 0; i < names.size(); i++) {
         for (int i1 = i + 1; i1 < names.size(); i1++) {
            if (names.get(i).equalsIgnoreCase(names.get(i1))) {
               bad.add(names.get(i));
               break;
            }
         }
      }

      Tann.uniquify(names);
      Tann.assertBads(bad);
      Tann.assertEquals("Should be all unique", pre, names.size());
      Tann.assertEquals("Should be all taken", 1000, names.size());
   }

   @Test
   public static void blessingEvent() {
      DungeonContext dc = DebugUtilsUseful.dummyContext();
      PhaseGeneratorModifierPick pgmp = new PhaseGeneratorModifierPick(3, 1, 3, true, ModifierPickContext.Difficulty_But_Midgame);
      int attempts = 50;

      for (int attemptIndex = 0; attemptIndex < 50; attemptIndex++) {
         List<Phase> ph = pgmp.generate(dc);
         ChoicePhase cp = (ChoicePhase)ph.get(0);
         List<Choosable> options = cp.getOptions();

         for (int blessIndex = 0; blessIndex < 2; blessIndex++) {
            Modifier option = (Modifier)options.get(blessIndex);
            Tann.assertTrue("should not be missingno bleseve", !option.isMissingno());
         }
      }
   }

   @Test
   @Slow
   public static void offerSpell() {
      Tann.assertTrue(hasSpellModifierOffer(PartyLayoutType.Basic));
      Tann.assertTrue(!hasSpellModifierOffer(PartyLayoutType.Force));
   }

   @Test
   @Slow
   public static void offerSpellItem() {
      Tann.assertTrue(hasSpellItemOffer(PartyLayoutType.Basic));
      Tann.assertTrue(!hasSpellItemOffer(PartyLayoutType.Force));
   }

   private static boolean hasSpellModifierOffer(PartyLayoutType layout) {
      DungeonContext dc = new DungeonContext(new ClassicConfig(Difficulty.Normal), Party.generate(0, HeroGenType.Normal, layout, new ArrayList<>()));
      int attempts = 50;
      boolean foundSpell = false;

      for (int i = 0; i < 50; i++) {
         for (Modifier mod : PhaseGeneratorDifficulty.getModifiersForChoiceDebug(Difficulty.Normal, dc)) {
            foundSpell |= (mod.getCollisionBits() & Collision.SPELL) > 0L;
         }
      }

      return foundSpell;
   }

   private static boolean hasSpellItemOffer(PartyLayoutType layout) {
      int attempts = 10;
      boolean foundSpell = false;

      for (int i = 0; i < 10; i++) {
         DungeonContext dc = new DungeonContext(new ClassicConfig(Difficulty.Normal), Party.generate(0, HeroGenType.Normal, layout, new ArrayList<>()));

         for (int l = 0; l < 19; l++) {
            dc.nextLevel();

            for (Choosable choosable : dc.getLootForPreviousLevel()) {
               foundSpell |= (choosable.getCollisionBits() & Collision.SPELL) > 0L;
            }
         }
      }

      return foundSpell;
   }

   @Test
   public static void testUnfairMyriad() {
      List<String> bad = new ArrayList<>();
      boolean prev = OptionLib.MYRIAD_OFFERS.c();
      boolean myri = false;
      OptionLib.MYRIAD_OFFERS.setValue(myri, false);
      DungeonContext dc = new DungeonContext(
         new ClassicConfig(Difficulty.Unfair), Party.generate(0, HeroGenType.Normal, PartyLayoutType.Basic, new ArrayList<>())
      );
      int attempts = 100;

      for (int i = 0; i < 100; i++) {
         List<Modifier> mods = PhaseGeneratorDifficulty.getModifiersForChoiceDebug(Difficulty.Unfair, dc);

         for (Modifier mod : mods) {
            for (Modifier mod2 : mods) {
               if (mod != mod2 && Collision.collides(mod.getCollisionBits(), mod2.getCollisionBits())) {
                  bad.add(mod + ":" + mod2);
               }
            }
         }
      }

      OptionLib.MYRIAD_OFFERS.setValue(prev, false);
      Tann.assertBads(bad);
   }
}
