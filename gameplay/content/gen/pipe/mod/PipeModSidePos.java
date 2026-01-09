package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNSidePos;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.changeHero.GlobalChangeHeroAll;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalAllEntities;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AffectByIndex;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AffectSideEffect;
import com.tann.dice.util.Tann;
import com.tann.dice.util.tp.TP;
import java.util.Random;

public class PipeModSidePos extends PipeRegexNamed<Modifier> {
   public PipeModSidePos() {
      super(SIDE_POSITION, prnS("\\."), MODIFIER);
   }

   protected Modifier internalMake(String[] groups) {
      String posName = groups[0];
      String modName = groups[1];
      SpecificSidesType type = SpecificSidesType.byName(posName);
      Modifier mod = ModifierLib.byName(modName);
      return !mod.isMissingno() && type != null ? make(type, mod) : null;
   }

   private static float getTier(Modifier mod, SpecificSidesType from, SpecificSidesType to) {
      return mod.getTier() * to.getFactor() / from.getFactor();
   }

   private static String name(SpecificSidesType sst, String modName) {
      return sst.getShortName() + "." + modName;
   }

   public static Modifier make(SpecificSidesType sst, Modifier mod) {
      if (!sst.validForPipe()) {
         return null;
      } else {
         Global g = mod.getSingleGlobalOrNull();
         if (g == null) {
            return null;
         } else {
            TP<SpecificSidesType, Global> result = replaceWithNewSST(g, sst);
            if (result == null) {
               return null;
            } else {
               float tier = getTier(mod, result.a, sst);
               return new Modifier(tier, name(sst, mod.getName()), result.b);
            }
         }
      }
   }

   private static TP<SpecificSidesType, Global> replaceWithNewSST(Global g, SpecificSidesType rep) {
      if (g instanceof GlobalAllEntities) {
         GlobalAllEntities gae = (GlobalAllEntities)g;
         Personal p = gae.personal;
         if (p instanceof AffectSides) {
            AffectSides as = (AffectSides)p;
            if (as.getConditions().size() == 0) {
               return new TP<>(
                  SpecificSidesType.All,
                  new GlobalAllEntities(((GlobalAllEntities)g).getPlayer(), new AffectSides(rep, as.getEffects().toArray(new AffectSideEffect[0])))
               );
            }

            if (as.getConditions().size() != 1) {
               return null;
            }

            for (int i = 0; i < as.getEffects().size(); i++) {
               if (as.getEffects().get(i) instanceof AffectByIndex) {
                  return null;
               }
            }

            AffectSideCondition asc = as.getConditions().get(0);
            if (asc instanceof SpecificSidesCondition) {
               SpecificSidesType prev = ((SpecificSidesCondition)asc).specificSidesType;
               if (prev == rep) {
                  return null;
               }

               return new TP<>(
                  prev, new GlobalAllEntities(((GlobalAllEntities)g).getPlayer(), new AffectSides(rep, as.getEffects().toArray(new AffectSideEffect[0])))
               );
            }
         }
      } else if (g instanceof GlobalChangeHeroAll) {
         return null;
      }

      return null;
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return !wild;
   }

   @Override
   public float getRarity(boolean wild) {
      return 1.8F;
   }

   protected Modifier generateInternal(boolean wild) {
      return wild
         ? this.example()
         : make(SpecificSidesType.getNiceSidesType(new Random()), ModifierLib.byName(Tann.pick("blank", "blank", "blank", "jammed", "stuck")));
   }

   public Modifier example() {
      int attempts = 50;

      for (int i = 0; i < attempts; i++) {
         Modifier m = make(Tann.random(PRNSidePos.makeValids()), ModifierLib.random());
         if (m != null) {
            return m;
         }
      }

      return null;
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
