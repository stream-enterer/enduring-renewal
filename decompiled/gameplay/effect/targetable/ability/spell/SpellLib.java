package com.tann.dice.gameplay.effect.targetable.ability.spell;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.effect.eff.VisualEffectType;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.TargetingRestriction;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.Undying;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.StateConditionType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpellLib {
   public static final String MISSINGNO_NAME = "Mishap";
   public static final Spell BURST = new SpellBill()
      .cost(2)
      .title("Burst")
      .eff(new EffBill().or(new EffBill().shield(2), new EffBill().damage(2).visual(VisualEffectType.Flame)))
      .bSpell();
   public static final Spell MISSINGNO = new SpellBill().cost(8).title("Mishap").eff(new EffBill().shield(998)).bSpell();

   private static List<Spell> makeTestingSpells() {
      List<SpellBill> bills = Arrays.asList(
         new SpellBill().title("Lash").cost(4).eff(new EffBill().damage(2).keywords(Keyword.cruel, Keyword.channel).visual(VisualEffectType.Slice)),
         new SpellBill().title("Slammo").cost(3).eff(new EffBill().damage(4).keywords(Keyword.cooldown).visual(VisualEffectType.Ellipse)),
         new SpellBill().title("Spikes").cost(3).eff(new EffBill().shield(1).keywords(Keyword.cleave, Keyword.repel)),
         new SpellBill().cost(2).title("Resolve").eff(new EffBill().heal(10).keywords(Keyword.singleCast)),
         new SpellBill().cost(2).title("Gristle").eff(new EffBill().specialAddKeyword(Keyword.cleave, Keyword.death)),
         new SpellBill()
            .cost(1)
            .title("End")
            .eff(new EffBill().damage(3).group().visual(VisualEffectType.Flame).bonusUntargeted(new EffBill().kill().friendly().group())),
         new SpellBill().cost(4).title("Gong").eff(new EffBill().targetType(TargetingType.ALL).shield(2).keywords(Keyword.boost)),
         new SpellBill().cost(3).title("Sting").eff(new EffBill().damage(1).keywords(Keyword.weaken, Keyword.poison)),
         new SpellBill().cost(3).title("Dispell").eff(new EffBill().damage(1).keywords(Keyword.dispel)),
         new SpellBill().cost(3).title("Intervene").eff(new EffBill().heal(1).group().restrict(StateConditionType.Dying).keywords(Keyword.cleanse)),
         new SpellBill().cost(3).title("Replenish2").eff(new EffBill().healAndShield(3)),
         new SpellBill().cost(1).title("t3blue00").eff(new EffBill().group().kill().restrict(TargetingRestriction.ExactlyValue).value(3)),
         new SpellBill().title("2dmgclv").cost(4).eff(new EffBill().damage(2).keywords(Keyword.cleave).visual(VisualEffectType.Slice)),
         new SpellBill().cost(2).title("spotless").eff(new EffBill().shield(2).group().restrict(StateConditionType.FullHP)),
         new SpellBill().cost(2).title("duo").eff(new EffBill().specialAddKeyword(Keyword.pair)),
         new SpellBill().cost(4).title("Splorb").eff(new EffBill().heal(2).keywords(Keyword.cleanse, Keyword.regen, Keyword.boost)),
         new SpellBill()
            .title("Touch")
            .cost(2)
            .eff(new EffBill().friendly().keywords(Keyword.deplete).buff(new Buff(1, new Undying())).visual(VisualEffectType.Undying)),
         new SpellBill()
            .title("Shards")
            .cost(2)
            .eff(new EffBill().damage(2).group().keywords(Keyword.hyperBoned, Keyword.cooldown).visual(VisualEffectType.Slice)),
         new SpellBill().cost(3).title("Speed").eff(new EffBill().specialAddKeywordPermanent(Keyword.cantrip)),
         new SpellBill().cost(5).title("Tire").eff(new EffBill().specialAddKeyword(Keyword.exert).group().enemy().visual(VisualEffectType.PerlinPoison)),
         new SpellBill()
            .title("Varhest")
            .cost(1)
            .eff(
               new EffBill()
                  .keywords(Keyword.cooldown, Keyword.threesy, Keyword.manaGain)
                  .kill()
                  .restrict(TargetingRestriction.ExactlyValue)
                  .visual(VisualEffectType.Singularity)
                  .value(1)
            ),
         new SpellBill()
            .title("Beep")
            .cost(3)
            .eff(new EffBill().damage(2).keywords(Keyword.heavy).bonusUntargeted(new EffBill().friendly().group().heal(1).keywords(Keyword.boost))),
         new SpellBill().cost(3).title("Paradox").eff(new EffBill().damage(1).keywords(Keyword.spellRescue))
      );
      List<Spell> spells = new ArrayList<>();

      for (SpellBill sb : bills) {
         sb.title("DS" + sb.title);
         spells.add(sb.bSpell());
      }

      return spells;
   }

   public static List<Spell> makeAllSpellsList() {
      return makeAllSpellsList(true);
   }

   public static List<Spell> makeAllSpellsList(boolean includeDebug) {
      return makeAllSpellsList(includeDebug, true, true);
   }

   public static List<Spell> makeAllSpellsList(boolean includeDebug, boolean includeMods, boolean includeItems) {
      List<Spell> allSpells = new ArrayList<>();
      allSpells.add(BURST);
      List<Personal> allPersonals = new ArrayList<>();

      for (HeroType ht : HeroTypeLib.getMasterCopy()) {
         for (Trait t : ht.traits) {
            allPersonals.add(t.personal);
         }
      }

      if (includeItems) {
         for (Item e : ItemLib.getMasterCopy()) {
            allPersonals.addAll(e.getPersonals());
         }
      }

      for (Personal t : allPersonals) {
         if (t.getSpell() != null) {
            allSpells.add(t.getSpell());
         }
      }

      if (includeMods) {
         for (Modifier m : ModifierLib.getAll()) {
            for (Global gt : m.getGlobals()) {
               if (gt.getGlobalSpell() != null) {
                  allSpells.add(gt.getGlobalSpell());
               }
            }
         }
      }

      if (includeDebug) {
         allSpells.addAll(makeTestingSpells());
      }

      return allSpells;
   }

   public static List<Spell> getSpellsWithKeyword(Keyword keyword) {
      List<Spell> result = new ArrayList<>();

      for (Spell s : makeAllSpellsList()) {
         if (s.getBaseEffect().hasKeyword(keyword)) {
            result.add(s);
         }
      }

      return result;
   }
}
