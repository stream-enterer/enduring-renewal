package com.tann.dice.gameplay.content.gen.pipe.entity.hero;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.generation.SpellGeneration;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellBill;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.Tactic;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.TacticCost;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.TacticCostType;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.trigger.personal.spell.learn.LearnSpell;
import com.tann.dice.gameplay.trigger.personal.spell.learn.LearnTactic;
import java.util.ArrayList;
import java.util.List;

public class PipeHeroAbility extends PipeRegexNamed<HeroType> {
   static final PRNPart SEP = new PRNMid("abilitydata");

   public PipeHeroAbility() {
      super(HERO, SEP, HERO);
   }

   public HeroType example() {
      return this.make(HeroTypeUtils.random(), HeroTypeUtils.byName("(whirl.i.pendulum)"));
   }

   protected HeroType internalMake(String[] groups) {
      String src = groups[0];
      String data = groups[1];
      return this.make(HeroTypeLib.byName(src), HeroTypeLib.byName(data));
   }

   private HeroType make(HeroType src, HeroType data) {
      if (!src.isMissingno() && !data.isMissingno()) {
         String realHeroName = src.getName() + SEP + data.getName();
         SpellBill sb = new SpellBill();
         sb.title(data.getName(true, false));
         List<EntSideState> states = data.makeEnt().getBlankState().getAllSideStates();
         Eff base = states.get(2).getCalculatedEffect().copy();
         transformCopyForAbEff(base);
         if (!this.okForAbilityMaybe(base) && !this.okForAbilityMaybe(base)) {
            return null;
         } else {
            EffBill eb = new EffBill(base);
            Eff bonus = states.get(4).getCalculatedEffect();
            if (bonus.getType() != EffType.Blank) {
               if (!okForAbilityUntargetedSecondMaybe(bonus)) {
                  return null;
               }

               eb.bonusUntargeted(bonus);
            }

            sb.eff(eb.bEff());
            if (states.get(3).getCalculatedEffect().hasValue()) {
               sb.cost(states.get(3).getCalculatedEffect().getValue());
               sb.overrideImage(data.portrait);
               base = new LearnSpell(sb.bSpell());
               return HeroTypeUtils.withPassive(src, realHeroName, base, "Custom spell");
            } else {
               List<Eff> calcEffs = new ArrayList<>();

               for (int i : new int[]{0, 1, 5}) {
                  calcEffs.add(states.get(i).getCalculatedEffect());
               }

               List<TacticCostType> costList = new ArrayList<>();

               for (Eff calcEff : calcEffs) {
                  TacticCostType a = TacticCostType.getValidTypeTextmod(calcEff);
                  if (a != null) {
                     int amt = a.pippy ? calcEff.getValue() : 1;

                     for (int i = 0; i < amt; i++) {
                        costList.add(a);
                     }
                  }
               }

               sb.cost(states.get(3).getCalculatedEffect().getValue());
               Tactic t = new Tactic(sb.bSpell(), new TacticCost(costList));
               if (data.getName().contains(".img.")) {
                  t = new Tactic(sb.bSpell(), new TacticCost(costList), data.portrait);
               }

               LearnTactic lt = new LearnTactic(t);
               return HeroTypeUtils.withPassive(src, realHeroName, lt, "Custom tactic");
            }
         }
      } else {
         return null;
      }
   }

   private static void transformCopyForAbEff(Eff base) {
      List<Keyword> ks = base.getKeywords();

      for (int i = ks.size() - 1; i >= 0; i--) {
         Keyword k = ks.get(i);
         Keyword kr = getSpellVersion(k);
         if (kr != null) {
            ks.set(i, kr);
         }
      }
   }

   private static Keyword getSpellVersion(Keyword k) {
      switch (k) {
         case singleUse:
            return Keyword.singleCast;
         case growth:
            return Keyword.channel;
         case decay:
            return Keyword.deplete;
         case rescue:
            return Keyword.spellRescue;
         case exert:
            return Keyword.cooldown;
         default:
            return null;
      }
   }

   private boolean okForAbilityMaybe(Eff base) {
      return base.getType() == EffType.Mana && base.getKeywords().isEmpty() ? true : SpellGeneration.okForAbilityMaybe(base);
   }

   public static boolean okForAbilityUntargetedSecondMaybe(Eff bonus) {
      if (bonus.needsTarget()) {
         return false;
      } else {
         return bonus.getType() == EffType.Mana && bonus.getKeywords().isEmpty() ? true : SpellGeneration.okForAbilityMaybe(bonus);
      }
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
