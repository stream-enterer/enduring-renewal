package com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.ModifierPickContext;
import com.tann.dice.gameplay.modifier.ModifierPickUtils;
import com.tann.dice.gameplay.modifier.ModifierType;
import com.tann.dice.gameplay.modifier.ModifierUtils;
import com.tann.dice.gameplay.modifier.generation.CurseLib;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoicePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoiceType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.RandomTieredChoosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.ReplaceChoosable;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhaseGeneratorModifierPick extends PhaseGenerator {
   final int numOptions;
   final int amt;
   final int tier;
   final int range;
   final boolean lastRandom;
   final ModifierPickContext context;

   public PhaseGeneratorModifierPick(int numOptions, int amt, int tier, boolean lastRandom, ModifierPickContext context) {
      this(numOptions, amt, tier, lastRandom, context, 0);
   }

   public PhaseGeneratorModifierPick(int numOptions, int amt, int tier, boolean lastRandom, ModifierPickContext context, int range) {
      this.numOptions = numOptions;
      this.amt = amt;
      this.tier = tier;
      this.lastRandom = lastRandom;
      this.context = context;
      this.range = range;
   }

   @Override
   public List<Phase> generate(DungeonContext dc) {
      int actualNumToGenerate = this.numOptions + (this.lastRandom ? -1 : 0);
      List<Modifier> mods = ModifierPickUtils.generateModifiers(
         this.tier - this.range / 2, this.tier + (this.range + 1) / 2, actualNumToGenerate, this.context, dc
      );
      List<Choosable> ch = new ArrayList<>(mods);
      if (this.lastRandom) {
         ch.add(new RandomTieredChoosable(this.tier, 1, ChoosableType.Modifier));
      }

      ModifierLib.getCache().decacheChoosables(ch);
      if (this.context == ModifierPickContext.Cursed) {
         maybeTransformChoosablesCursed(dc, ch, this.tier);
      }

      return Arrays.asList(new ChoicePhase(new ChoiceType(ChoiceType.ChoiceStyle.Number, this.amt), ch));
   }

   public static void maybeTransformChoosablesCursed(DungeonContext dc, List<Choosable> ch, int tierMaybe) {
      if (tierMaybe != 0) {
         List<Modifier> mods = dc.getCurrentModifiers();
         if (!mods.isEmpty()) {
            for (Choosable choosable : ch) {
               if (choosable instanceof Modifier) {
                  Modifier m = (Modifier)choosable;
                  int t = m.getTier();
                  if ((tierMaybe <= 0 || t >= tierMaybe * 2) && (tierMaybe >= 0 || t <= (1 + tierMaybe) * 2)) {
                     String essence = m.getEssence();
                     if (essence != null) {
                        for (int i = 0; i < mods.size(); i++) {
                           Modifier ex = mods.get(i);
                           if (Math.abs(t - (ex.getTier() + tierMaybe)) <= 1) {
                              String ess2 = ex.getEssence();
                              if (ess2 != null && ess2.equalsIgnoreCase(essence)) {
                                 ch.set(ch.indexOf(choosable), new ReplaceChoosable(ex, m));
                                 break;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public String describe() {
      return "Choose " + (this.amt > 0 ? "a" : this.amt) + " " + this.mpTierString() + " " + ModifierUtils.describe(this.tier > 0);
   }

   private String mpTierString() {
      String tag = TextWriter.getTag(ModifierType.fromTier(this.tier).getC());
      return "tier "
         + tag
         + (this.range == 0 ? Math.abs(this.tier) : Math.abs(this.tier - this.range / 2) + "-" + Math.abs(this.tier + (this.range + 1) / 2))
         + "[cu]";
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.MODIFIER;
   }

   @Override
   public Actor makePanel() {
      String text = "+";
      if (this.amt > 1) {
         text = text + this.amt;
      }

      return new Pixl().text(text).gap(2).actor(CurseLib.makeChooseModifierPanel(this.tier)).pix();
   }

   @Override
   public String hyphenTag() {
      return Math.abs(this.tier) + "";
   }
}
