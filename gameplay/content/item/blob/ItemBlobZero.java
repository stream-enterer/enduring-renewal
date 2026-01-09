package com.tann.dice.gameplay.content.item.blob;

import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.VisualEffectType;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.TargetingRestriction;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellBill;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellLib;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.Tactic;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.TacticCost;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.TacticCostType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementN;
import com.tann.dice.gameplay.trigger.personal.CopySide;
import com.tann.dice.gameplay.trigger.personal.ForceEquip;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.ExactlyCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.HasKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.TextureCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.TypeCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AffectByIndex;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.BecomeIdentical;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ChangeType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.FlatBonus;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.MultiplyEffect;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.RemoveKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.RemoveKeywordColour;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWithBlank;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReturnToInnate;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.Shift;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.SwapSides;
import com.tann.dice.gameplay.trigger.personal.equipRestrict.EquipRestrictNamed;
import com.tann.dice.gameplay.trigger.personal.hp.EmptyMaxHp;
import com.tann.dice.gameplay.trigger.personal.hp.MaxHP;
import com.tann.dice.gameplay.trigger.personal.immunity.HealImmunity;
import com.tann.dice.gameplay.trigger.personal.item.ItemSlots;
import com.tann.dice.gameplay.trigger.personal.item.copyItem.CopyAlliedItems;
import com.tann.dice.gameplay.trigger.personal.linked.PersonalTurnRequirement;
import com.tann.dice.gameplay.trigger.personal.linked.perN.PersonalPerN;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.ColLink;
import com.tann.dice.gameplay.trigger.personal.weird.KeepName;
import com.tann.dice.gameplay.trigger.personal.weird.LevelUpInto;
import com.tann.dice.util.Colours;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBlobZero {
   public static List<ItBill> makeAll() {
      List<ItBill> result = new ArrayList<>();
      result.addAll(makeZeroes());
      return result;
   }

   private static List<ItBill> makeZeroes() {
      return Arrays.asList(
         new ItBill(0, "Monster Grin").prs(new AffectSides(SpecificSidesType.Wings, new AddKeyword(Keyword.possessed))),
         new ItBill(0, "Old Root").prs(new AffectSides(new TypeCondition(EffType.Heal, true), new FlatBonus(2))),
         new ItBill(0, "Stake").prs(new AffectSides(new TypeCondition(EffType.Damage), new AddKeyword(Keyword.terminal))),
         new ItBill(0, new Tactic("Oof", new TacticCost(TacticCostType.blank, 5), new EffBill().damage(5).visual(VisualEffectType.Sword).bEff())),
         new ItBill(0, "Knot").prs(new AffectSides(new TypeCondition(EffType.Blank), new AddKeyword(Keyword.permissive))),
         new ItBill(0, "Cholesterol").prs(new EmptyMaxHp(5)).prs(new HealImmunity()),
         new ItBill(0, "Twiddle").prs(new AffectSides(new SwapSides(SpecificSidesType.Top, SpecificSidesType.Bot))),
         new ItBill(0, "Cyanide Pill").prs(new AffectSides(SpecificSidesType.RightTwo, new AddKeyword(Keyword.cantrip, Keyword.death))),
         new ItBill(0, "Void"),
         new ItBill(0, "Yearn").prs(new AffectSides(new TypeCondition(EffType.Blank), new ReplaceWith(ESB.healShieldMana.val(0)))),
         new ItBill(0, "Splinter").prs(new MaxHP(-1)),
         new ItBill(0, "Scissors").prs(new AffectSides(SpecificSidesType.RightMost, new AddKeyword(Keyword.deathwish, Keyword.pain))),
         new ItBill(0, new SpellBill().title("Betray").cost(2).eff(new EffBill().kill().friendly())),
         new ItBill(0, new SpellBill().title("Waste").cost(1).eff(new EffBill().replaceBlanksWith(ESB.dmgDeath.val(1)))),
         new ItBill(0, new SpellBill().title("Aid").cost(1).eff(new EffBill().heal(2).enemy().group())),
         new ItBill(0, new SpellBill().title("Mana").cost(1).eff(new EffBill().mana(1).keywords(Keyword.singleCast))),
         new ItBill(0, new SpellBill().title("Hinder").cost(1).eff(new EffBill().friendly().damage(1).keywords(Keyword.weaken))),
         new ItBill(0, "Spanner").prs(new AffectSides(SpecificSidesType.RightMost, new ReturnToInnate())),
         new ItBill(0, "Peanut Shell").prs(new ForceEquip()).prs(new EmptyMaxHp(1)),
         new ItBill(0, "Extra Pocket").prs(new ItemSlots(1)),
         new ItBill(0, new Tactic("Burstic", new TacticCost(TacticCostType.basicMana, TacticCostType.basicMana), SpellLib.BURST.getBaseEffect())),
         new ItBill(0, "Brick").prs(new AffectSides(new TypeCondition(EffType.Shield, true), new AddKeyword(Keyword.petrify))),
         new ItBill(0, "Cracked Emerald").prs(new CopyAlliedItems(12)),
         new ItBill(0, "Fly").prs(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.growth, Keyword.decay))),
         new ItBill(0, "Snake Oil").prs(new AffectSides(new TypeCondition(EffType.Heal, true), new AddKeyword(Keyword.engage))),
         new ItBill(0, "Rusty Longsword").prs(new AffectSides(SpecificSidesType.Row, new ReplaceWith(ESB.dmg.val(1)))),
         new ItBill(0, "Bond Certificate").prs(new PersonalTurnRequirement(new TurnRequirementN(15), new AffectSides(new FlatBonus(true, 15)))),
         new ItBill(0, "Bent Spoon").prs(new AffectSides(SpecificSidesType.LeftTwo, new FlatBonus(0))),
         new ItBill(0, "Bent Fork").prs(new AffectSides(SpecificSidesType.RightTwo, new MultiplyEffect(1))),
         new ItBill(0, "Bent Spork").prs(new AffectSides(SpecificSidesType.Column, new MultiplyEffect(-1))),
         new ItBill(0, "Toy Sword").prs(new AffectSides(SpecificSidesType.RightMost, new ReplaceWith(ESB.dmg.val(0)))),
         new ItBill(0, "Fidget Spinner").prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(ESB.recharge.withKeyword(Keyword.duplicate)))),
         new ItBill(0, "Wooden Armour").prs(new MaxHP(2)).prs(new AffectSides(new FlatBonus(-1))),
         new ItBill(0, "Burred Shield").prs(new AffectSides(SpecificSidesType.Left, new ReplaceWith(ESB.shieldPain.val(5)))),
         new ItBill(0, "Hidden Strength").prs(new AffectSides(SpecificSidesType.Bot, new ReplaceWith(ESB.dmgAll.val(10).withKeyword(Keyword.manacost)))),
         new ItBill(0, "Rorrim Tekcop").prs(new CopySide(SpecificSidesType.RightMost, SpecificSidesType.Left)),
         new ItBill(0, "Trick Deck").prs(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.sept))),
         new ItBill(0, "Dull Wit")
            .prs(new AffectSides(new TypeCondition(EffType.Mana), new ChangeType(ESB.dmg, "basic damage"), new RemoveKeyword(Keyword.manaGain))),
         new ItBill(0, "Dead Branch").prs(new AffectSides(SpecificSidesType.Bot, new ReplaceWith(ESB.stick.val(1)))),
         new ItBill(0, "Two of Clubs")
            .prs(
               new AffectSides(
                  SpecificSidesType.All,
                  new AffectByIndex(new FlatBonus(-1), new FlatBonus(-1), new FlatBonus(-1), new FlatBonus(-1), new FlatBonus(-1), new FlatBonus(1))
               )
            ),
         new ItBill(0, "Flea").prs(new AffectSides(new ExactlyCondition(0), new AddKeyword(Keyword.pain), new FlatBonus(1))),
         new ItBill(0, "Cracked Phylactery").prs(new AffectSides(SpecificSidesType.LeftTwo, new AddKeyword(Keyword.selfHeal))).prs(new MaxHP(-6)),
         new ItBill(0, "Paper").prs(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.nothing))),
         new ItBill(0, "Sprinkles").prs(new ColLink(HeroCol.violet, new AffectSides(SpecificSidesType.Left, new FlatBonus(1)))),
         new ItBill(0, "Big Fish").prs(new AffectSides(new TypeCondition(EffType.Damage), new AddKeyword(Keyword.vitality))),
         new ItBill(0, "Card").prs(new AffectSides(SpecificSidesType.RightTwo, new AddKeyword(Keyword.inflictInflictNothing))),
         new ItBill(0, "Grass").prs(new AffectSides(new TypeCondition(EffType.Blank), new ReplaceWith(ESB.blankPetrified))),
         new ItBill(0, "Stoneskin").prs(new MaxHP(2)).prs(new AffectSides(new ReplaceWithBlank(ChoosableType.Item))),
         new ItBill(
            0,
            new SpellBill()
               .title("Niche")
               .cost(2)
               .eff(new EffBill().kill().restrict(TargetingRestriction.ExactlyValue).value(46).visual(VisualEffectType.Singularity))
         ),
         new ItBill(0, new SpellBill().title("Invoke").cost(1).eff(new EffBill().summon("demon", 1))),
         new ItBill(0, "Orbit").prs(PersonalPerN.basicMultiple(4, new AffectSides(new Shift()))),
         new ItBill(0, "Refactor").prs(new AffectSides(new TypeCondition(EffType.Recharge, false), new ReplaceWith(ESB.giveDoubleUse))),
         new ItBill(0, "Chalk").prs(new AffectSides(new TextureCondition(ESB.blankCurse, "[purple]curse blank[cu]"), new ReplaceWith(ESB.manaDeath.val(3)))),
         new ItBill(0, "Camomile").prs(new AffectSides(new RemoveKeyword(Keyword.cantrip))),
         new ItBill(0, "Overprepared").prs(new PersonalTurnRequirement(new TurnRequirementN(0), new AffectSides(new FlatBonus(true, 1)))),
         new ItBill(0, "Pin").prs(new AffectSides(new TypeCondition(EffType.Blank), new ReplaceWith(ESB.dmgSelfCantrip.val(1)))),
         new ItBill(0, "Atlas Stone").prs(new AffectSides(SpecificSidesType.Wings, new FlatBonus(1))).prs(new ItemSlots(-2)),
         new ItBill(0, "Huge Scabbard").prs(new AffectSides(new ExactlyCondition(10), new FlatBonus(1))),
         new ItBill(0, "Banana Peel").prs(new AffectSides(SpecificSidesType.RightTwo, new ReplaceWith(ESB.giveCantripJoke))),
         new ItBill(0, "Cigarette end").prs(new AffectSides(new HasKeyword(Keyword.singleUse), new AddKeyword(Keyword.doubleUse))),
         new ItBill(0, "Lawnmower").prs(new AffectSides(new RemoveKeywordColour(Colours.green))),
         new ItBill(0, "Scoundrel Stash").prs(new EquipRestrictNamed("Scoundrel")).prs(new AffectSides(SpecificSidesType.LeftTwo, new FlatBonus(1))),
         new ItBill(
            0,
            new SpellBill()
               .cost(2)
               .title("Tsrub")
               .eff(new EffBill().or(new EffBill().damage(2).friendly().visual(VisualEffectType.Flame), new EffBill().shield(2).enemy()))
         ),
         new ItBill(0, "Shroud").prs(new AffectSides(SpecificSidesType.Middle, new AddKeyword(Keyword.reborn))),
         new ItBill(0, "Dusty Emerald").prs(new CopyAlliedItems(0)),
         new ItBill(0, "Sleeper Agent").prs(new AffectSides(SpecificSidesType.RightMost, new ReplaceWith(ESB.backstab.val(5)))),
         new ItBill(0, "Tin Foil Hat").prs(new AffectSides(new BecomeIdentical())),
         new ItBill(0, "Potion Shard").prs(new AffectSides(SpecificSidesType.Bot, new ReplaceWith(ESB.dmgPainDiscard.val(3)))),
         new ItBill(0, "Stale Bread").prs(new AffectSides(SpecificSidesType.LeftTwo, new ReplaceWith(ESB.rerollCantrip.val(0), ESB.blank))),
         new ItBill(0, "Taxes").prs(new AffectSides(new AddKeyword(Keyword.mandatory))),
         new ItBill(0, new SpellBill().cost(4).title("bank").eff(new EffBill().mana(3).keywords(Keyword.future, Keyword.singleCast))),
         new ItBill(0, "Can").prs(new AffectSides(SpecificSidesType.RightTwo, new AddKeyword(Keyword.century))),
         new ItBill(0, "Name Tag").prs(new KeepName()),
         new ItBill(0, "Shovel Bite").prs(new LevelUpInto("Spade"))
      );
   }
}
