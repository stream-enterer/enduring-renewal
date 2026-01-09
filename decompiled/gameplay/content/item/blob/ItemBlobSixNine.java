package com.tann.dice.gameplay.content.item.blob;

import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.effect.eff.VisualEffectType;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellBill;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.trigger.global.roll.GlobalBonusRerolls;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementFirst;
import com.tann.dice.gameplay.trigger.personal.CopySide;
import com.tann.dice.gameplay.trigger.personal.IncomingEffBonus;
import com.tann.dice.gameplay.trigger.personal.OnOverheal;
import com.tann.dice.gameplay.trigger.personal.OnRescue;
import com.tann.dice.gameplay.trigger.personal.Permadeath;
import com.tann.dice.gameplay.trigger.personal.Undying;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.EvennessCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.ExactlyCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.HasValue;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.HighestCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.NoKeywordsCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.OrMoreCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.TargetedCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.TypeCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddAllKeywordsFromOtherSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.BonusForIdentical;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ChangeType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.CopyBaseFromHeroAbove;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.FlatBonus;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.MultiplyEffect;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.RemoveAllKeywords;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.RemoveKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWithBlank;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.SetToHighest;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.SetValue;
import com.tann.dice.gameplay.trigger.personal.eff.StartOfTurnSelf;
import com.tann.dice.gameplay.trigger.personal.hp.BonusHpPerBase;
import com.tann.dice.gameplay.trigger.personal.hp.MaxHP;
import com.tann.dice.gameplay.trigger.personal.immunity.DamageImmunity;
import com.tann.dice.gameplay.trigger.personal.item.copyItem.CopyAlliedItems;
import com.tann.dice.gameplay.trigger.personal.linked.OnMyTurn;
import com.tann.dice.gameplay.trigger.personal.linked.PersonalTurnRequirement;
import com.tann.dice.gameplay.trigger.personal.linked.TriggerPersonalToGlobal;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.ColLink;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.StateConditionType;
import com.tann.dice.gameplay.trigger.personal.spell.AfterUseAbility;
import com.tann.dice.gameplay.trigger.personal.startBuffed.StartRegenned;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBlobSixNine {
   public static List<ItBill> makeAll() {
      List<ItBill> result = new ArrayList<>();
      result.addAll(makeSixToNine());
      return result;
   }

   private static List<ItBill> makeSixToNine() {
      return Arrays.asList(
         new ItBill(5, "Nunchaku").prs(new AffectSides(SpecificSidesType.Wings, new AddKeyword(Keyword.chain))),
         new ItBill(5, "Early Grave").prs(new AffectSides(new OrMoreCondition(3), new AddKeyword(Keyword.pain, Keyword.cantrip))),
         new ItBill(5, "Bandana").prs(new AffectSides(new HasValue(false), new AddKeyword(Keyword.cantrip))),
         new ItBill(6, "Blindfold").prs(new AffectSides(new RemoveAllKeywords())),
         new ItBill(5, "Determination").prs(new PersonalTurnRequirement(new TurnRequirementFirst(), new Undying())),
         new ItBill(6, "Leaden Handle").prs(new AffectSides(new TargetedCondition(), new AddKeyword(Keyword.heavy), new FlatBonus(1))),
         new ItBill(6, "Urn").prs(new AffectSides(new AddKeyword(Keyword.enduring))),
         new ItBill(6, "Braids").prs(new CopySide(SpecificSidesType.Left, SpecificSidesType.Middle)),
         new ItBill(6, "Kite Shield").prs(new AffectSides(new TypeCondition(EffType.Shield), new SetValue(3))),
         new ItBill(6, "Wedding Rings").prs(new AffectSides(new TypeCondition(EffType.Heal), new AddKeyword(Keyword.pair))),
         new ItBill(7, "Jewel Loupe").prs(new AffectSides(new ExactlyCondition(1), new AddKeyword(Keyword.manaGain))),
         new ItBill(6, "Honeycomb").prs(new AffectSides(SpecificSidesType.RightThree, new AddKeyword(Keyword.sixth))),
         new ItBill(6, "Lich Eye").prs(new AffectSides(new AddKeyword(Keyword.manaGain, Keyword.death), new FlatBonus(2))),
         new ItBill(7, "Eucalyptus").prs(new AffectSides(SpecificSidesType.Right, new ReplaceWith(ESB.healCleanse.val(1).withKeyword(Keyword.quadUse)))),
         new ItBill(6, "Tie").prs(new AffectSides(SpecificSidesType.Middle, new AddKeyword(Keyword.pristine))),
         new ItBill(6, "Karma")
            .prs(new AffectSides(new TypeCondition(EffType.Heal, false, false), new AddKeyword(Keyword.selfHeal)))
            .prs(new AffectSides(new TypeCondition(EffType.Shield, false, false), new AddKeyword(Keyword.selfShield)))
            .prs(new AffectSides(new TypeCondition(EffType.Damage, false, false), new AddKeyword(Keyword.pain))),
         new ItBill(5, "Candle").prs(new AffectSides(new AddKeyword(Keyword.vigil))),
         new ItBill(6, "Silver Pendant").prs(new IncomingEffBonus(2, EffType.Shield)),
         new ItBill(6, "Ghost Shield").prs(new StartOfTurnSelf(new EffBill().shield(2).self().bEff())),
         new ItBill(6, "Wrench").prs(new AffectSides(SpecificSidesType.Middle, new FlatBonus(1))),
         new ItBill(6, "Blood Amulet").prs(new AffectSides(SpecificSidesType.LeftTwo, new AddKeyword(Keyword.selfHeal))),
         new ItBill(6, "Catnip").prs(new AffectSides(SpecificSidesType.RightMost, new AddKeyword(Keyword.cantrip))),
         new ItBill(6, "Tooth Necklace").prs(new AffectSides(SpecificSidesType.LeftTwo, new AddKeyword(Keyword.cruel))),
         new ItBill(6, "Crescent Shield").prs(new AffectSides(SpecificSidesType.Left, new ReplaceWith(ESB.shieldCrescent.val(1)))),
         new ItBill(6, "Ocular Amulet").prs(new AffectSides(SpecificSidesType.Left, new FlatBonus(1))),
         new ItBill(6, "InfiniHeal").prs(new AffectSides(SpecificSidesType.RightTwo, new ChangeType(ESB.healAll, "heal all"))),
         new ItBill(5, "Water").prs(new AffectSides(SpecificSidesType.Row, new FlatBonus(1, -1, 1, -1))),
         new ItBill(6, "Blinding Bolt").prs(new AfterUseAbility(null, new EffBill().shield(2).self().bEff())),
         new ItBill(7, "Sparks").prs(new AffectSides(new ExactlyCondition(1), new AddKeyword(Keyword.cantrip))),
         new ItBill(6, "Scales").prs(new AffectSides(SpecificSidesType.RightMost, new SetToHighest())),
         new ItBill(6, "Jump")
            .prs(new AffectSides(SpecificSidesType.Wings, new AddAllKeywordsFromOtherSides(SpecificSidesType.Left)))
            .prs(new AffectSides(SpecificSidesType.Left, new ReplaceWithBlank(ChoosableType.Item))),
         new ItBill(6, new SpellBill().title("Luck").cost(3).eff(new EffBill().keywords(Keyword.future, Keyword.cooldown).reroll(8))),
         new ItBill(6, "Demonic Deal").prs(new AffectSides(new AddKeyword(Keyword.pain), new FlatBonus(2))),
         new ItBill(6, "Twisted Flax").prs(new AffectSides(SpecificSidesType.Column, new FlatBonus(1, -1, 1))),
         new ItBill(6, "Wand of Stun").prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(ESB.wandStun))),
         new ItBill(7, "Hourglass").prs(new PersonalTurnRequirement(new TurnRequirementFirst(), new AffectSides(new FlatBonus(true, 1)))),
         new ItBill(5, "Cauldron").prs(new AffectSides(SpecificSidesType.Middle, new AddKeyword(Keyword.rite), new FlatBonus(1))),
         new ItBill(6, "Conduit").prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(ESB.manaDuplicate.val(2)))),
         new ItBill(5, "Enhance Wand").prs(new AffectSides(SpecificSidesType.Top, new ReplaceWith(ESB.wandFightBonus.val(1)))),
         new ItBill(8, "Ornate Hilt").prs(new AffectSides(new TypeCondition(EffType.Damage), new AddKeyword(Keyword.selfShield))),
         new ItBill(7, "Duck").prs(new AffectSides(SpecificSidesType.RightTwo, new AddKeyword(Keyword.echo))),
         new ItBill(6, "Troll Blood").prs(new StartRegenned(2)),
         new ItBill(7, "Iron Helm").prs(new MaxHP(6)),
         new ItBill(6, "Wooden Bracelet").prs(new AffectSides(new NoKeywordsCondition(), new FlatBonus(1))),
         new ItBill(7, "Two Reeds").prs(new AffectSides(new EvennessCondition(true), new FlatBonus(1))),
         new ItBill(7, "Twisted Bar").prs(new AffectSides(new SetValue(2))),
         new ItBill(7, "Thimble").prs(new OnMyTurn(new DamageImmunity(true, true, false), "thimble")),
         new ItBill(7, "Pair of Kings").prs(new AffectSides(SpecificSidesType.Wings, new AddKeyword(Keyword.pair))),
         new ItBill(7, "Ichor Chalice").prs(new OnOverheal(new EffBill().damage(1).targetType(TargetingType.Top))),
         new ItBill(7, "Second Chance").prs(new AffectSides(SpecificSidesType.RightMost, new ReplaceWith(ESB.recharge))),
         new ItBill(7, "Sharp Wit")
            .prs(new AffectSides(new TypeCondition(EffType.Mana), new ChangeType(ESB.dmg, "basic damage", 3), new RemoveKeyword(Keyword.manaGain))),
         new ItBill(7, "Ogre Blood").prs(new AffectSides(SpecificSidesType.Middle, new AddKeyword(Keyword.bloodlust))),
         new ItBill(7, "Dragon Pipe").prs(new AffectSides(new TypeCondition(EffType.Heal), new FlatBonus(1))),
         new ItBill(7, "Metal Studs").prs(new AffectSides(new TypeCondition(EffType.Shield), new FlatBonus(1))),
         new ItBill(6, "Demon Claw").prs(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.rampage), new FlatBonus(-1))),
         new ItBill(7, "Pocket Mirror").prs(new CopySide(SpecificSidesType.Left, SpecificSidesType.RightMost)),
         new ItBill(8, "Boots of Speed")
            .prs(new TriggerPersonalToGlobal(new GlobalBonusRerolls(1)))
            .prs(new AffectSides(SpecificSidesType.Left, new ReplaceWith(ESB.dodge))),
         new ItBill(7, "Mushroom").prs(new AffectSides(new AddKeyword(Keyword.decay), new FlatBonus(1))),
         new ItBill(7, "Flute").prs(new AffectSides(SpecificSidesType.RightMost, new ReplaceWith(ESB.flute.val(1)))),
         new ItBill(7, "Wandcraft").prs(new AffectSides(new ChangeType(ESB.wandMana, "single-use mana"))),
         new ItBill(7, "Charge Link").prs(new AffectSides(SpecificSidesType.Wings, new AddKeyword(Keyword.charged, Keyword.singleUse))),
         new ItBill(7, "Broadsword").prs(new AffectSides(SpecificSidesType.Column, new ReplaceWith(ESB.dmg.val(4)))),
         new ItBill(7, "Anvil").prs(new AffectSides(SpecificSidesType.Middle, new AddKeyword(Keyword.steel))),
         new ItBill(7, "Botany").prs(new AffectSides(new TypeCondition(EffType.Heal), new AddKeyword(Keyword.groupGrowth))),
         new ItBill(7, new SpellBill().cost(2).title("charge").eff(new EffBill().shield(2).keywords(Keyword.boost, Keyword.singleCast).friendly())),
         new ItBill(7, "Glass Heart").prs(new Permadeath()).prs(new AffectSides(new FlatBonus(1))),
         new ItBill(6, "Burning Halo").prs(new OnRescue(new EffBill().group().damage(1).bEff())),
         new ItBill(7, "Tentacle").prs(new AffectSides(SpecificSidesType.RightTwo, new AddKeyword(Keyword.repel))),
         new ItBill(8, "Holy Book").prs(new AffectSides(SpecificSidesType.Row, new AddKeyword(Keyword.cleanse, Keyword.selfCleanse))),
         new ItBill(8, new SpellBill().title("Spark").cost(4).eff(new EffBill().friendly().specialAddKeyword(Keyword.manaGain))),
         new ItBill(9, "Poison Dip").prs(new AffectSides(SpecificSidesType.Wings, new AddKeyword(Keyword.poison))),
         new ItBill(8, "Sickle").prs(new AffectSides(new HighestCondition(false), new FlatBonus(1))),
         new ItBill(8, "Tourmaline Paraiba").prs(new AffectSides(new TypeCondition(EffType.Mana), new AddKeyword(Keyword.era))),
         new ItBill(8, "Tusk")
            .prs(new AffectSides(new TypeCondition(EffType.Heal), new FlatBonus(1)))
            .prs(new AffectSides(new TypeCondition(EffType.Shield), new FlatBonus(1))),
         new ItBill(8, "Bullseye").prs(new AffectSides(SpecificSidesType.Column, new AddKeyword(Keyword.engage))),
         new ItBill(6, "Katar").prs(new AffectSides(new TypeCondition(EffType.Damage), new AddKeyword(Keyword.trio))),
         new ItBill(8, "Prism").prs(new AffectSides(SpecificSidesType.Middle, new AddKeyword(Keyword.rainbow))),
         new ItBill(8, "Mirror Mask").prs(new AffectSides(new CopyBaseFromHeroAbove(true))),
         new ItBill(8, "Scorpion Tail").prs(new AffectSides(SpecificSidesType.Wings, new AddKeyword(Keyword.weaken, Keyword.pain))),
         new ItBill(8, "Greatsword").prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(ESB.dmgInspire.val(5)))),
         new ItBill(8, "Deadly Bolt").prs(new AfterUseAbility(null, new EffBill().damage(1).targetType(TargetingType.Top).bEff())),
         new ItBill(8, "Sapphire Ring").prs(new AffectSides(new TypeCondition(EffType.Mana), new FlatBonus(1))),
         new ItBill(8, "Faerie Dust").prs(new AffectSides(new TypeCondition(EffType.Blank), new ReplaceWith(ESB.mana.val(3)))),
         new ItBill(8, "Singularity").prs(new AffectSides(SpecificSidesType.RightTwo, new FlatBonus(2))),
         new ItBill(9, "Serration").prs(new AffectSides(SpecificSidesType.Wings, new AddKeyword(Keyword.vulnerable))),
         new ItBill(8, "Iron Crown").prs(new ColLink(HeroCol.grey, new AffectSides(SpecificSidesType.Left, new FlatBonus(4)))),
         new ItBill(8, "Standard").prs(new ColLink(HeroCol.yellow, new AffectSides(new FlatBonus(1)))),
         new ItBill(8, "Olympian Trident").prs(new AffectSides(SpecificSidesType.Middle, new AddKeyword(Keyword.cleave))),
         new ItBill(8, "Sushi").prs(new AffectSides(SpecificSidesType.Left, new ReplaceWith(ESB.rerollCantrip.val(2)))),
         new ItBill(7, "Brimstone").prs(new AffectSides(SpecificSidesType.RightMost, new MultiplyEffect(3))),
         new ItBill(8, "Ironblood Pendant").prs(new IncomingEffBonus(3, EffType.Heal, EffType.Shield)),
         new ItBill(8, "Gauntlet").prs(new AffectSides(new TypeCondition(EffType.Damage), new FlatBonus(1))),
         new ItBill(8, "Lion").prs(new AffectSides(SpecificSidesType.LeftTwo, new AddKeyword(Keyword.fierce))),
         new ItBill(9, "Wax Seal").prs(new AffectSides(new BonusForIdentical())),
         new ItBill(9, "Boarhide Bracers").prs(new AffectSides(SpecificSidesType.Wings, new FlatBonus(2))),
         new ItBill(9, "Timestone").prs(new AffectSides(SpecificSidesType.RightTwo, new AddKeyword(Keyword.cantrip))),
         new ItBill(9, "Triple Shuriken").prs(new AffectSides(new AddKeyword(Keyword.ranged, Keyword.chain))),
         new ItBill(9, "Collar").prs(new AffectSides(SpecificSidesType.Wings, new AddKeyword(Keyword.copycat))),
         new ItBill(9, "Silk Cape").prs(new CopySide(SpecificSidesType.Left, SpecificSidesType.Row)),
         new ItBill(9, "Emerald Mirror").prs(new CopyAlliedItems(6, 8)),
         new ItBill(9, "Horned Viper").prs(new AffectSides(SpecificSidesType.LeftTwo, new AddKeyword(Keyword.poison))),
         new ItBill(9, "Angel Feather").prs(new AffectSides(SpecificSidesType.Wings, new AddKeyword(Keyword.rescue))),
         new ItBill(9, "Dumbbell").prs(new AffectSides(new OrMoreCondition(4), new FlatBonus(4))),
         new ItBill(9, "Chaos Wand").prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(ESB.wandChaos.val(1)))),
         new ItBill(9, "Charged Hammer").prs(new AffectSides(SpecificSidesType.Left, new ReplaceWith(ESB.chargedHammer.val(10)))),
         new ItBill(9, "Eye of Horus").prs(new AffectSides(new FlatBonus(1))),
         new ItBill(9, "Helm of Power").prs(new AffectSides(SpecificSidesType.Left, new MultiplyEffect(2))),
         new ItBill(9, "Second Heart").prs(new BonusHpPerBase(1, 1)),
         new ItBill(9, "Overflowing Chalice").prs(SpecificSidesType.RightThree, new AddKeyword(Keyword.fizz)),
         new ItBill(
            10, new SpellBill().title("Abyss").cost(5).eff(new EffBill().kill().restrict(StateConditionType.HalfOrLessHP).visual(VisualEffectType.Singularity))
         ),
         new ItBill(9, "Spike Stone").prs(new AffectSides(SpecificSidesType.RightFive, new AddKeyword(Keyword.fumble), new FlatBonus(3)))
      );
   }
}
