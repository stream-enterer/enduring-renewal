package com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.ModifierPickContext;
import com.tann.dice.gameplay.modifier.ModifierPickUtils;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoicePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoiceType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.progress.chievo.unlock.Feature;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.util.DebugUtilsUseful;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhaseGeneratorDifficulty extends PhaseGenerator {
   final Difficulty difficulty;

   public PhaseGeneratorDifficulty(Difficulty difficulty) {
      this.difficulty = difficulty;
   }

   @Override
   public List<Phase> generate(DungeonContext dc) {
      List<Modifier> mods = getModifiersForChoice(this.difficulty, dc);
      if (mods.size() == 0) {
         return new ArrayList<>();
      } else {
         ModifierLib.getCache().decache(mods);
         Phase p = getPhaseFor(mods, this.difficulty);
         return (List<Phase>)(p == null ? new ArrayList<>() : Arrays.asList(p));
      }
   }

   private static Phase getPhaseFor(List<Modifier> mods, Difficulty d) {
      if (d == Difficulty.Normal && UnUtil.isLocked(Feature.NORMAL_TWEAKS)) {
         return null;
      } else {
         String topmsg = d.getTopChoiceText();
         switch (d) {
            case Heaven:
            case Unfair:
            case Brutal:
            case Hell:
               return pb(d.getTargetModifierValue(), mods, topmsg);
            case Hard:
            case Easy:
            case Normal:
               if (OptionLib.COMPLEX_HARD_EASY.c()) {
                  return pb(d.getTargetModifierValue(), mods, topmsg);
               }

               return chooseOne(mods, topmsg);
            default:
               throw new RuntimeException("No phase for " + d);
         }
      }
   }

   public static List<Modifier> getModifiersForChoiceDebug(Difficulty difficulty) {
      return getModifiersForChoice(difficulty, DebugUtilsUseful.dummyContext());
   }

   public static List<Modifier> getModifiersForChoiceDebug(Difficulty difficulty, DungeonContext dc) {
      return getModifiersForChoice(difficulty, dc);
   }

   public static List<Modifier> getModifiersForChoice(Difficulty difficulty, DungeonContext dc) {
      int addUpTo = difficulty.getTargetModifierValue();
      int amt = OptionUtils.getRealModifierAmt(difficulty);
      boolean singlePick = difficulty.singlePick() && !OptionLib.COMPLEX_HARD_EASY.c();
      boolean wildCard = !singlePick;
      List<Modifier> mods = new ArrayList<>();
      if (difficulty == Difficulty.Normal) {
         mods.add(ModifierLib.byName("skip"));
      }

      if (singlePick) {
         int dd = 0;
         int du = 0;
         switch (difficulty) {
            case Hard:
               du = OptionLib.MYRIAD_OFFERS.c() ? 1 : 0;
               break;
            case Easy:
               dd = 1;
               du = 1;
         }

         mods.addAll(ModifierPickUtils.generateModifiers(addUpTo - dd, addUpTo + du, amt, ModifierPickContext.Difficulty, dc));
      } else {
         mods.addAll(ModifierPickUtils.getModifiersAddingUpTo(amt, addUpTo, ModifierPickContext.Difficulty, wildCard, dc));
      }

      return mods;
   }

   private static Phase chooseOne(List<Modifier> mods, String name) {
      List<Choosable> ch = new ArrayList<>(mods);
      return new ChoicePhase(new ChoiceType(ChoiceType.ChoiceStyle.Number, 1), ch, name);
   }

   private static Phase pb(int points, List<Modifier> curses, String name) {
      return new ChoicePhase(new ChoiceType(ChoiceType.ChoiceStyle.PointBuy, points), new ArrayList<>(curses), name);
   }

   @Override
   public String describe() {
      return this.difficulty.getColourTaggedName() + "[cu] modifiers";
   }
}
