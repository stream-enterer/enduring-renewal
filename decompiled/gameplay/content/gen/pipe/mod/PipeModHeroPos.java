package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.ModifierType;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.changeHero.GlobalChangeHeroAll;
import com.tann.dice.gameplay.trigger.global.changeHero.GlobalChangeHeroPos;
import com.tann.dice.gameplay.trigger.global.changeHero.effects.ChangeHeroEffect;
import com.tann.dice.gameplay.trigger.global.linked.GlobalPositional;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalAllEntities;
import com.tann.dice.gameplay.trigger.global.scaffolding.HeroPosition;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class PipeModHeroPos extends PipeRegexNamed<Modifier> {
   static final PRNPart PREF = new PRNPref("h");

   public PipeModHeroPos() {
      super(PREF, HERO_POSITION, prnS("\\."), MODIFIER);
   }

   protected Modifier internalMake(String[] groups) {
      String posName = groups[0];
      String modName = groups[1];
      HeroPosition hp = HeroPosition.byName(posName);
      Modifier mod = ModifierLib.byName(modName);
      return mod.isMissingno() ? null : make(hp, mod);
   }

   public static List<Modifier> makePositionalVariations(List<Modifier> input) {
      List<Modifier> result = new ArrayList<>();

      for (Modifier mod : input) {
         if (getInnerGlobalAll(mod) != null) {
            for (HeroPosition hp : HeroPosition.values()) {
               float newTier = getTier(mod, hp);
               if (!(Math.abs(Math.abs(newTier - Math.round(newTier)) / newTier) > 0.1F)) {
                  result.add(make(hp, mod));
               }
            }
         }
      }

      return result;
   }

   private static float getTier(Modifier mod, HeroPosition hp) {
      int num = hp.getRawPosition().length;
      return mod.getFloatTier() / 5.0F * num;
   }

   private static String name(HeroPosition p, String modName) {
      return PREF + p.veryShortName() + "." + modName;
   }

   public static Modifier make(HeroPosition hp, Modifier mod) {
      if (mod.isMissingno()) {
         return null;
      } else {
         float newTier = getTier(mod, hp);
         String name = name(hp, mod.getName());
         Personal p = getInnerGlobalAll(mod);
         if (p != null) {
            Global[] globals = new Global[]{new GlobalPositional(hp, p)};
            return new Modifier(newTier, name, globals);
         } else {
            ChangeHeroEffect che = getInnerGlobalCHE(mod);
            if (che != null) {
               return mod.getMType() == ModifierType.Unrated
                  ? new Modifier(name, new GlobalChangeHeroPos(hp, che))
                  : new Modifier(mod.getFloatTier() * hp.getRawPosition().length / 5.0F, name, new GlobalChangeHeroPos(hp, che));
            } else {
               return null;
            }
         }
      }
   }

   private static Personal getInnerGlobalAll(Modifier mod) {
      for (Global g : mod.getGlobals()) {
         if (g instanceof GlobalAllEntities) {
            GlobalAllEntities gae = (GlobalAllEntities)g;
            if (gae.getPlayer() != null && gae.getPlayer()) {
               return ((GlobalAllEntities)g).personal;
            }

            return null;
         }
      }

      return null;
   }

   private static ChangeHeroEffect getInnerGlobalCHE(Modifier mod) {
      for (Global g : mod.getGlobals()) {
         if (g instanceof GlobalChangeHeroPos) {
            GlobalChangeHeroPos gae = (GlobalChangeHeroPos)g;
            return gae.getChangeHeroEffect();
         }

         if (g instanceof GlobalChangeHeroAll) {
            return ((GlobalChangeHeroAll)g).getChangeHeroEffect();
         }
      }

      return null;
   }

   public Modifier example() {
      int attempts = 20;
      List<Modifier> mods = new ArrayList<>();

      for (int i = 0; i < 20; i++) {
         mods.add(ModifierLib.random());
      }

      List<Modifier> potential = makePositionalVariations(mods);
      return potential.size() > 0 ? Tann.random(potential) : null;
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return true;
   }

   protected Modifier generateInternal(boolean wild) {
      return wild ? this.example() : make(Tann.random(HeroPosition.values()), ModifierLib.byName(Tann.pick("ascend", "level up", "delevel", "missing")));
   }

   @Override
   public float getRarity(boolean wild) {
      return wild ? 1.0F : 0.7F;
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
