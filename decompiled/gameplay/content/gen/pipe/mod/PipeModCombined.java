package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.gameplay.trigger.global.Global;
import java.util.ArrayList;
import java.util.List;

public class PipeModCombined extends PipeRegexNamed<Modifier> {
   static final PRNPart SEP = prnS("&");

   public PipeModCombined() {
      super(MODIFIER, SEP, MODIFIER);
   }

   private static String name(Modifier a, Modifier b) {
      return a.getName() + "&" + b.getName();
   }

   protected Modifier internalMake(String[] groups) {
      String full = groups[0] + SEP + groups[1];
      String target = SEP.toString();
      int li = full.length();

      while ((li = full.lastIndexOf(target, li - 1)) != -1) {
         String modAS = full.substring(0, li);
         String modBs = full.substring(li + target.length());
         Modifier modA;
         Modifier modB;
         if (modAS.length() < modBs.length()
            ? !(modA = ModifierLib.byName(modAS)).isMissingno() && !(modB = ModifierLib.byName(modBs)).isMissingno()
            : !(modB = ModifierLib.byName(modBs)).isMissingno() && !(modA = ModifierLib.byName(modAS)).isMissingno()) {
            return this.make(modA, modB, true);
         }
      }

      return null;
   }

   private Modifier make(Modifier a, Modifier b, boolean allowCollision) {
      if (a == null || a.isMissingno() || b == null || b.isMissingno()) {
         return null;
      } else if (!allowCollision && ChoosableUtils.collides(a, b)) {
         return null;
      } else {
         float at = a.getFloatTier();
         float bt = b.getFloatTier();
         float tier = this.sumTiers(at, bt);
         return new Modifier(tier, name(a, b), this.combineGlobals(a, b));
      }
   }

   private List<Global> combineGlobals(Modifier a, Modifier b) {
      List<Global> total = new ArrayList<>();
      total.addAll(a.getGlobals());
      total.addAll(b.getGlobals());

      for (int i = total.size() - 1; i >= 0; i--) {
         if (total.get(i).metaOnly()) {
            total.remove(i);
         }
      }

      return total;
   }

   private float sumTiers(float at, float bt) {
      return at != -0.069F && bt != -0.069F ? at + bt : -0.069F;
   }

   public Modifier example() {
      Modifier m = ModifierLib.random();
      Modifier m2 = null;

      while (m2 == null || ChoosableUtils.collides(m, m2)) {
         m2 = ModifierLib.random();
      }

      return this.make(m, m2, false);
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return wild;
   }

   protected Modifier generateInternal(boolean wild) {
      return this.example();
   }

   @Override
   public float getRarity(boolean wild) {
      return 0.5F;
   }
}
