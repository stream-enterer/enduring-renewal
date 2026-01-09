package com.tann.dice.gameplay.content.item.blob;

import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.modifier.modBal.ModTierUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.trigger.personal.ForceEquip;
import com.tann.dice.gameplay.trigger.personal.Permadeath;
import com.tann.dice.gameplay.trigger.personal.SetStartingHp;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.FlatBonus;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWithBlank;
import com.tann.dice.gameplay.trigger.personal.death.DamageAdjacentsOnDeath;
import com.tann.dice.gameplay.trigger.personal.death.OnDeathEffect;
import com.tann.dice.gameplay.trigger.personal.death.OtherDeathEffect;
import com.tann.dice.gameplay.trigger.personal.eff.StartOfCombat;
import com.tann.dice.gameplay.trigger.personal.immunity.HealImmunity;
import com.tann.dice.gameplay.trigger.personal.immunity.ShieldImmunity;
import com.tann.dice.gameplay.trigger.personal.item.ItemSlots;
import com.tann.dice.gameplay.trigger.personal.spell.AfterUseAbility;
import com.tann.dice.gameplay.trigger.personal.startBuffed.StartPoisoned;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBlobNegative {
   public static List<ItBill> makeAll() {
      List<ItBill> result = new ArrayList<>();
      result.addAll(makeNegatives());
      return result;
   }

   private static List<ItBill> makeNegatives() {
      return Arrays.asList(
         new ItBill(-1, "Tracked").prs(new ForceEquip()).prs(new OnDeathEffect(new EffBill().summon("wolf", 2))),
         new ItBill(-1, "Broken Heart").prs(new ForceEquip()).prs(new HealImmunity()),
         new ItBill(-1, "Weariness").prs(new ForceEquip()).prs(new AffectSides(SpecificSidesType.LeftTwo, new AddKeyword(Keyword.exert))),
         new ItBill(-1, "Coiled Snake").prs(new ForceEquip()).prs(new StartPoisoned(1)),
         new ItBill(-1, "Brittle").prs(new ForceEquip()).prs(new Permadeath()),
         new ItBill(-1, "Martyr").prs(new ForceEquip()).prs(new AffectSides(SpecificSidesType.LeftTwo, new AddKeyword(Keyword.death), new FlatBonus(2))),
         new ItBill(-1, "Affliction").prs(new ForceEquip()).prs(new AffectSides(SpecificSidesType.LeftTwo, new AddKeyword(Keyword.pain))),
         new ItBill(-1, "Lead Weight").prs(new ForceEquip()).prs(new AffectSides(SpecificSidesType.Wings, new ReplaceWith(ESB.blankItem))),
         new ItBill(-1, "Handcuffs").prs(new ForceEquip()).prs(new ItemSlots(-1)),
         new ItBill(-1, "Mould").prs(new ForceEquip()).prs(new AffectSides(new AddKeyword(Keyword.decay))),
         new ItBill(-1, "D4").prs(new ForceEquip()).prs(new AffectSides(SpecificSidesType.Right, new ReplaceWith(ESB.dmgSelfCantrip.val(1)))),
         new ItBill(-1, "Barrel Hoops").prs(new ForceEquip()).prs(new DamageAdjacentsOnDeath(5)),
         new ItBill(-2, "Cursed Bolt").prs(new ForceEquip()).prs(new AfterUseAbility(null, new EffBill().damage(2).self().bEff())),
         new ItBill(-2, "Slimed").prs(new ForceEquip()).prs(new AffectSides(new AddKeyword(Keyword.sticky))),
         new ItBill(-2, "Soul Link").prs(new ForceEquip()).prs(new OtherDeathEffect("Soul Link", true, new EffBill().self().kill())),
         new ItBill(-2, "Wretched Crown").prs(new ForceEquip()).prs(new OnDeathEffect(new EffBill().friendly().group().kill())),
         new ItBill(-2, "Broken Spirit").prs(new ForceEquip()).prs(new HealImmunity()).prs(new ShieldImmunity()),
         new ItBill(-2, "Exhaustion").prs(new ForceEquip()).prs(new AffectSides(new AddKeyword(Keyword.exert))),
         new ItBill(-2, "Compulsion").prs(new ForceEquip()).prs(new AffectSides(new AddKeyword(Keyword.mandatory, Keyword.pain))),
         new ItBill(-2, "Conscience").prs(new ForceEquip()).prs(new OtherDeathEffect("Conscience", false, new EffBill().self().damage(1).bEff())),
         new ItBill(-2, "Parasite").prs(new ForceEquip()).prs(new SetStartingHp(1)),
         new ItBill(-2, "Pharaoh Curse").prs(new ForceEquip()).prs(new AffectSides(new FlatBonus(-1))),
         new ItBill(-4, "Backstab").prs(new ForceEquip()).prs(new AffectSides(SpecificSidesType.MiddleFour, new ReplaceWith(ESB.backstab.val(4)))),
         new ItBill(Math.round(ModTierUtils.blanked(0.8F)), "Amnesia").prs(new ForceEquip()).prs(new AffectSides(new ReplaceWithBlank(ChoosableType.Item))),
         new ItBill(Math.round(ModTierUtils.deadHero(0.85F)), "Dead Crow").prs(new ForceEquip()).prs(new StartOfCombat(new EffBill().self().kill().bEff())),
         new ItBill(-4, "Empathy").prs(new ForceEquip()).prs(new OtherDeathEffect("Conscience", false, new EffBill().self().kill().bEff()))
      );
   }
}
