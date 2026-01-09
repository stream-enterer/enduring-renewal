package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.effect.targetable.ability.AbilityUtils;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.trigger.global.chance.Rarity;
import com.tann.dice.gameplay.trigger.global.eff.GlobalEndTurnSpell;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.StateConditionType;

public class PipeModEndTurnSpell extends PipeRegexNamed<Modifier> {
   static final PRNPart PREF = new PRNPref("ea");

   public PipeModEndTurnSpell() {
      super(PREF, ABILITY);
   }

   public Modifier example() {
      Ability a = AbilityUtils.random();
      return this.make(a, a.getTitle());
   }

   protected Modifier internalMake(String[] groups) {
      String tag = groups[0];
      return this.make(AbilityUtils.byName(tag), tag);
   }

   private Modifier make(Ability spell, String tag) {
      if (spell != null && this.isSpellOk(spell)) {
         float likeHeroTier = AbilityUtils.likeFromHeroTier(spell);
         float blessTier;
         if (Float.isNaN(likeHeroTier)) {
            blessTier = 0.0F;
         } else {
            blessTier = spell.getCostFactorInActual(Math.round(likeHeroTier)) * 2.2F;
         }

         String title = PREF + tag;
         return new Modifier(blessTier, title, new GlobalEndTurnSpell(spell)).rarity(Rarity.TENTH);
      } else {
         return null;
      }
   }

   private boolean isSpellOk(Ability s) {
      boolean ok = true;
      Eff e = s.getBaseEffect();
      ok &= !e.hasRestriction(StateConditionType.Dying);
      ok &= !e.needsTarget();
      switch (e.getType()) {
         case Resurrect:
         case Recharge:
            ok = false;
            break;
         case Buff:
            ok &= !(e.getBuff().personal instanceof AffectSides);
      }

      return ok;
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return !wild;
   }

   protected Modifier generateInternal(boolean wild) {
      Ability s = AbilityUtils.random();
      return ModifierLib.byName("et3-es-" + s.getTitle());
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
