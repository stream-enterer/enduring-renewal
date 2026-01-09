package com.tann.dice.gameplay.content.gen.pipe.mod.keyword;

import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.modBal.ModTierUtils;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalHeroes;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.util.Tann;

public class PipeModKeywordSide extends PipeRegexNamed<Modifier> {
   private Keyword[] goodKeywords = new Keyword[]{Keyword.pain, Keyword.death};

   public PipeModKeywordSide() {
      super(SIDE_POSITION, prnS("\\."), KEYWORD);
   }

   protected Modifier internalMake(String[] groups) {
      String pos = groups[0];
      String kw = groups[1];
      if (bad(pos, kw)) {
         return null;
      } else {
         SpecificSidesType sst = SpecificSidesType.byName(pos);
         Keyword k = Keyword.byName(kw);
         return this.make(sst, k);
      }
   }

   private Modifier make(SpecificSidesType sst, Keyword k) {
      if (k != null && sst != null && this.validSSt(sst) && !k.abilityOnly()) {
         Modifier m = ModifierLib.byName("hero." + k.getName());
         float allTier;
         if (!m.isMissingno() && m.getTier() != 0) {
            allTier = m.getTier();
         } else {
            allTier = guessBaseVal(k);
         }

         float tier = this.getTier(sst, allTier);
         if (tier == 0.0F) {
            tier = -0.069F;
         }

         return new Modifier(tier, sst.getShortName() + "." + k.getName(), new GlobalHeroes(new AffectSides(sst, new AddKeyword(k))));
      } else {
         return null;
      }
   }

   private float getTier(SpecificSidesType sst, float kv) {
      return ModTierUtils.keywordToSides(sst, kv);
   }

   public static float guessBaseVal(Keyword k) {
      float baseMult = 5.0F;
      switch (k) {
         case chain:
            return 0.0F;
         case pain:
            return ModTierUtils.painKeyword(5.0F);
         case death:
            return ModTierUtils.deathKeyword(5.0F);
         default:
            float dmgPw = ESB.dmg.val(1).withKeyword(k).getApproxTotalEffectTier(HeroTypeUtils.defaultHero(1));
            float shieldPw = ESB.shield.val(1).withKeyword(k).getApproxTotalEffectTier(HeroTypeUtils.defaultHero(1));
            return (Math.max(dmgPw, shieldPw) - 1.0F) * 19.0F;
      }
   }

   private boolean validSSt(SpecificSidesType sst) {
      return !sst.isWeird();
   }

   public Modifier example() {
      return this.make(Tann.random(SpecificSidesType.values()), Tann.random(Keyword.values()));
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return true;
   }

   @Override
   public float getRarity(boolean wild) {
      return wild ? 1.0F : 0.1F;
   }

   protected Modifier generateInternal(boolean wild) {
      SpecificSidesType sst = Tann.random(SpecificSidesType.values());
      return this.make(sst, this.randomKeyword(wild));
   }

   private Keyword randomKeyword(boolean wild) {
      if (!wild) {
         return Tann.random(this.goodKeywords);
      } else {
         Keyword k;
         do {
            k = Tann.random(Keyword.values());
         } while (Tann.contains(this.goodKeywords, k));

         return k;
      }
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
