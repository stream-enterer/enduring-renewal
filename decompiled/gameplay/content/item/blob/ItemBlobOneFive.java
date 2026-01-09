package com.tann.dice.gameplay.content.item.blob;

import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.effect.eff.VisualEffectType;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.TargetingRestriction;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellBill;
import com.tann.dice.gameplay.fightLog.event.snapshot.ManaGainEvent;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.trigger.global.GlobalAllowDeadHeroSpells;
import com.tann.dice.gameplay.trigger.global.GlobalFleeAt;
import com.tann.dice.gameplay.trigger.global.GlobalMaxMana;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalHeroes;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalMonsters;
import com.tann.dice.gameplay.trigger.global.roll.GlobalBonusRerolls;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementEveryN;
import com.tann.dice.gameplay.trigger.personal.AvoidDeathPenalty;
import com.tann.dice.gameplay.trigger.personal.CopySide;
import com.tann.dice.gameplay.trigger.personal.IncomingEffBonus;
import com.tann.dice.gameplay.trigger.personal.KeepShields;
import com.tann.dice.gameplay.trigger.personal.LostOnDeath;
import com.tann.dice.gameplay.trigger.personal.OnOverheal;
import com.tann.dice.gameplay.trigger.personal.OnRescue;
import com.tann.dice.gameplay.trigger.personal.Permadeath;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.ExactlyCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.NotCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.TargetedCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.TypeCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AffectByIndex;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ChangeToMyPosition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ChangeType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.CopyBaseFromHeroAbove;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.FlatBonus;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.Flip;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.RemoveAllKeywords;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.RemoveKeywordColour;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWithBlank;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWithEnt;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReturnToInnate;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.Rotate;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.SetValue;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.Shift;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.SwapSides;
import com.tann.dice.gameplay.trigger.personal.death.OnDeathEffect;
import com.tann.dice.gameplay.trigger.personal.death.OtherDeathEffect;
import com.tann.dice.gameplay.trigger.personal.eff.StartOfCombat;
import com.tann.dice.gameplay.trigger.personal.eff.StartOfTurnSelf;
import com.tann.dice.gameplay.trigger.personal.hp.EmptyMaxHp;
import com.tann.dice.gameplay.trigger.personal.hp.MaxHP;
import com.tann.dice.gameplay.trigger.personal.hp.MaxHpSet;
import com.tann.dice.gameplay.trigger.personal.immunity.DamageImmunity;
import com.tann.dice.gameplay.trigger.personal.immunity.HealImmunity;
import com.tann.dice.gameplay.trigger.personal.immunity.ShieldImmunity;
import com.tann.dice.gameplay.trigger.personal.item.ItemSlots;
import com.tann.dice.gameplay.trigger.personal.item.copyItem.CopyAlliedItems;
import com.tann.dice.gameplay.trigger.personal.linked.PersonalTurnRequirement;
import com.tann.dice.gameplay.trigger.personal.linked.TriggerPersonalToGlobal;
import com.tann.dice.gameplay.trigger.personal.linked.snapshotCondition.SnapshotCondition;
import com.tann.dice.gameplay.trigger.personal.linked.snapshotCondition.SnapshotConditionType;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.ColLink;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.PersonalConditionLink;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.StateConditionType;
import com.tann.dice.gameplay.trigger.personal.spell.AfterUseAbility;
import com.tann.dice.gameplay.trigger.personal.startBuffed.StartPetrified;
import com.tann.dice.gameplay.trigger.personal.startBuffed.StartPoisoned;
import com.tann.dice.gameplay.trigger.personal.startBuffed.StartRegenned;
import com.tann.dice.util.Colours;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBlobOneFive {
   public static List<ItBill> makeAll() {
      List<ItBill> result = new ArrayList<>();
      result.addAll(makeOneToFive());
      return result;
   }

   private static List<ItBill> makeOneToFive() {
      return Arrays.asList(
         new ItBill(1, "Arrow").prs(new AffectSides(new TypeCondition(EffType.Damage), new ReplaceWith(ESB.headshot.val(2)))),
         new ItBill(1, "Memory").prs(new AffectSides(SpecificSidesType.Left, new ReturnToInnate())),
         new ItBill(1, "Balisong").prs(new AffectSides(SpecificSidesType.Left, new ReplaceWith(ESB.dmgCantrip.val(1)))),
         new ItBill(1, "Corset").prs(new AffectSides(SpecificSidesType.LeftTwo, new AffectByIndex(new ReplaceWithBlank(ChoosableType.Item), new FlatBonus(1)))),
         new ItBill(1, "Tankard").prs(SpecificSidesType.Middle, new AddKeyword(Keyword.acidic)),
         new ItBill(1, "Doll")
            .prs(new AffectSides(SpecificSidesType.Left, new CopyBaseFromHeroAbove(true)))
            .prs(new AffectSides(SpecificSidesType.RightFive, new FlatBonus(-1))),
         new ItBill(1, "Wand of Wand").prs(new AffectSides(SpecificSidesType.Left, new ReplaceWith(ESB.wandOfWand.val(1)))),
         new ItBill(1, "Sorcery Notes").prs(new AffectSides(SpecificSidesType.LeftTwo, new ReplaceWith(ESB.manaCantrip.val(1), ESB.blankItem))),
         new ItBill(1, "Quiver").prs(new AffectSides(SpecificSidesType.RightFive, new ReplaceWith(ESB.arrow.val(1)))),
         new ItBill(1, "Revive Potion").prs(new AffectSides(SpecificSidesType.Right, new ReplaceWith(ESB.potionRevive.val(5)))),
         new ItBill(1, "Courage Potion").prs(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.potion), new FlatBonus(3))),
         new ItBill(1, "Pendulum").prs(new AffectSides(new SwapSides(SpecificSidesType.Left, SpecificSidesType.Middle))),
         new ItBill(1, "Tattered Robes")
            .prs(new AffectSides(SpecificSidesType.RightThree, new ReplaceWith(ESB.mana.val(1), ESB.mana.val(1), ESB.mana.val(1))))
            .prs(new TriggerPersonalToGlobal(new GlobalMaxMana(1))),
         new ItBill(1, "Leather Vest").prs(new MaxHP(1)),
         new ItBill(1, "Healing Wand").prs(new AffectSides(SpecificSidesType.RightMost, new ReplaceWith(ESB.wandHeal.val(4)))),
         new ItBill(1, "Castor Root").prs(new AffectSides(new TypeCondition(EffType.Blank), new ReplaceWith(ESB.heal.val(1)))),
         new ItBill(1, "Seedling").prs(new AffectSides(SpecificSidesType.Left, new ReplaceWith(ESB.damageGrowth.val(2)))),
         new ItBill(1, "Reagents").prs(new AffectSides(SpecificSidesType.RightTwo, new ReplaceWith(ESB.blankItem, ESB.healRegen.val(1)))),
         new ItBill(1, "Rusty Plate").prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(ESB.blankItem))).prs(new MaxHP(3)),
         new ItBill(1, "Anchor").prs(new StartOfCombat(new EffBill().shield(1).self().bEff())),
         new ItBill(1, "Big Shield").prs(new AffectSides(SpecificSidesType.Left, new ReplaceWith(ESB.shield.val(4)))),
         new ItBill(1, "Cloak").prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(ESB.dodge))),
         new ItBill(1, "Scar").prs(new EmptyMaxHp(5)),
         new ItBill(1, "Ballet Shoes").prs(new AffectSides(new SwapSides(SpecificSidesType.Left, SpecificSidesType.RightMost))),
         new ItBill(1, "Titanbane Potion").prs(new AffectSides(SpecificSidesType.Middle, new AddKeyword(Keyword.dispel, Keyword.potion))),
         new ItBill(1, "Change of Heart").prs(new AffectSides(new TypeCondition(EffType.Heal), new ChangeType(ESB.shield, "basic shield"))),
         new ItBill(1, "Emerald Shard").prs(new CopyAlliedItems(2)),
         new ItBill(1, "Barkskin").prs(new ShieldImmunity()).prs(new MaxHP(2)),
         new ItBill(1, "Knife Bag").prs(new AffectSides(new TypeCondition(EffType.Blank), new ReplaceWith(ESB.dmgCantrip.val(1).withKeyword(Keyword.pain)))),
         new ItBill(1, "Big Heart").prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(ESB.heal.val(7)))),
         new ItBill(1, "Compass").prs(new AffectSides(new Rotate())),
         new ItBill(1, "Bone Charm").prs(new AvoidDeathPenalty()),
         new ItBill(1, "Infused Herbs").prs(new AffectSides(SpecificSidesType.Bot, new ReplaceWith(ESB.infusedHerbs.val(2)))),
         new ItBill(1, new SpellBill().title("Poultice").cost(1).eff(new EffBill().heal(2).keywords(Keyword.singleCast))),
         new ItBill(1, "Cheating Sleeves").prs(new AffectSides(SpecificSidesType.Wings, new AddKeyword(Keyword.quin))),
         new ItBill(1, "Copper Ring").prs(new EmptyMaxHp(1)).prs(new TriggerPersonalToGlobal(new GlobalMaxMana(1))),
         new ItBill(1, "Wolf Ears").prs(new MaxHpSet(6)),
         new ItBill(1, "Basilisk Scale").prs(new StartPetrified(1)).prs(new AffectSides(SpecificSidesType.Top, new FlatBonus(3))),
         new ItBill(1, "Glass Helm").prs(new MaxHP(3)).prs(new Permadeath()),
         new ItBill(1, "Iron Heart").prs(new AffectSides(new AddKeyword(Keyword.exert), new FlatBonus(1))),
         new ItBill(1, "Bowl").prs(new AffectSides(SpecificSidesType.Column, new ReturnToInnate())),
         new ItBill(1, "Coin").prs(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.fumble), new FlatBonus(1))),
         new ItBill(1, "Trowel").prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(ESB.resurrect.val(1)))),
         new ItBill(1, "Doom Blade").prs(new AffectSides(new TypeCondition(EffType.Blank), new ReplaceWith(ESB.dmgDeath.val(3)))),
         new ItBill(1, "Tincture").prs(new AffectSides(SpecificSidesType.RightMost, new AddKeyword(Keyword.cleanse))),
         new ItBill(1, "Whey").prs(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.vitality))),
         new ItBill(1, "Necromancer Tome")
            .prs(new TriggerPersonalToGlobal(new GlobalAllowDeadHeroSpells()))
            .prs(new AffectSides(SpecificSidesType.Left, new ReplaceWith(ESB.manaPain.val(3)))),
         new ItBill(1, "Clumsy Shoes").prs(new AffectSides(new TargetedCondition(), new AddKeyword(Keyword.heavy, Keyword.eliminate), new FlatBonus(1))),
         new ItBill(2, new SpellBill().cost(2).title("Wings").eff(new EffBill().heal(3).targetType(TargetingType.TopAndBot))),
         new ItBill(2, "Needle").prs(new AffectSides(new TargetedCondition(), new AddKeyword(Keyword.picky), new FlatBonus(2))),
         new ItBill(2, "Ice Cube").prs(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.stasis))),
         new ItBill(2, "Worn Arms").prs(new AffectSides(SpecificSidesType.Wings, new ReplaceWith(ESB.dmg.val(2), ESB.shield.val(2)))),
         new ItBill(3, "Foil").prs(new AffectSides(SpecificSidesType.Right, new ReplaceWith(ESB.dmg.val(0).withKeyword(Keyword.skill)))),
         new ItBill(3, "Kilt").prs(new CopySide(SpecificSidesType.Right, SpecificSidesType.RightMost)),
         new ItBill(2, "Static Tome").prs(new AffectSides(SpecificSidesType.Wings, new ReplaceWith(ESB.manaPair.val(1)))),
         new ItBill(3, "Polearm").prs(new AffectSides(new TypeCondition(EffType.Damage), new ChangeToMyPosition(SpecificSidesType.Top))),
         new ItBill(3, "Flickering Blade").prs(new AffectSides(SpecificSidesType.RightMost, new ReplaceWith(ESB.dmgCopycat.val(1)))),
         new ItBill(2, "Origami").prs(new AffectSides(new Flip())),
         new ItBill(2, "Statuette").prs(new AffectSides(SpecificSidesType.Row, new ReplaceWith(ESB.blankItem))).prs(new MaxHP(6)),
         new ItBill(2, "Spinach").prs(new StartOfCombat(new EffBill().heal(3).self().bEff())),
         new ItBill(3, "Buckler").prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(ESB.shield.val(3).withKeyword(Keyword.bloodlust)))),
         new ItBill(2, "Sapphire").prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(ESB.mana.val(2)))),
         new ItBill(2, "Ace of Spades")
            .prs(
               new AffectSides(
                  SpecificSidesType.All,
                  new AffectByIndex(new FlatBonus(2), new FlatBonus(-2), new FlatBonus(-2), new FlatBonus(-2), new FlatBonus(-2), new FlatBonus(-2))
               )
            ),
         new ItBill(2, "Garnet").prs(new IncomingEffBonus(1, EffType.Heal)),
         new ItBill(2, "Powdered Mana").prs(new AffectSides(new TypeCondition(EffType.Blank), new ReplaceWith(ESB.mana.val(1)))),
         new ItBill(2, "Burning Blade").prs(new AffectSides(SpecificSidesType.Left, new ReplaceWith(ESB.dmg.val(4)))).prs(new MaxHP(-2)),
         new ItBill(2, "Citrine Ring").prs(new AffectSides(SpecificSidesType.RightMost, new FlatBonus(1))),
         new ItBill(2, "Big Hammer").prs(new AffectSides(SpecificSidesType.LeftTwo, new ReplaceWith(ESB.dmgHeavy.val(5), ESB.blankItem))),
         new ItBill(1, "Autumn Leaf").prs(new AffectSides(SpecificSidesType.RightMost, new AddKeyword(Keyword.growth))),
         new ItBill(2, "Blessed Water").prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(ESB.healVitality.val(3)))),
         new ItBill(2, "Blessed Ring").prs(new AffectSides(SpecificSidesType.RightMost, new ReplaceWith(ESB.shieldRescue.val(1)))),
         new ItBill(2, "Twin Daggers").prs(new AffectSides(SpecificSidesType.Wings, new ReplaceWith(ESB.dmgCantrip.val(1)))),
         new ItBill(2, "Rain of Arrows").prs(new AffectSides(SpecificSidesType.Top, new ReplaceWith(ESB.arrowDuplicate.val(1)))),
         new ItBill(2, "Liqueur")
            .prs(new AffectSides(new SwapSides(SpecificSidesType.Top, SpecificSidesType.RightMost)))
            .prs(new TriggerPersonalToGlobal(new GlobalMaxMana(2))),
         new ItBill(2, "Peaked Cap").prs(new CopySide(SpecificSidesType.Middle, SpecificSidesType.Left)),
         new ItBill(2, "Sapphire Skull").prs(new OnDeathEffect(new EffBill().mana(2).bEff(), new ManaGainEvent(2, "Sapphire Skull"))),
         new ItBill(2, "First aid Kit").prs(new ColLink(HeroCol.orange, new AffectSides(SpecificSidesType.Bot, new ReplaceWith(ESB.healCleanse.val(2))))),
         new ItBill(2, "Friendship Bracelet").prs(new EmptyMaxHp(2)).prs(new TriggerPersonalToGlobal(new GlobalFleeAt(5), "friendship")),
         new ItBill(2, "Clumsy Hammer").prs(new AffectSides(SpecificSidesType.Top, new ReplaceWith(ESB.dmgHeavy.val(5).withKeyword(Keyword.eliminate)))),
         new ItBill(2, "Tower Shield").prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(ESB.shield.val(10).withKeyword(Keyword.heavy)))),
         new ItBill(2, "Square Wheel")
            .prs(new AffectSides(SpecificSidesType.RightThree, new ReplaceWith(ESB.rerollCantrip.val(1), ESB.blankItem, ESB.blankItem))),
         new ItBill(2, "Silver Imp").prs(new AffectSides(SpecificSidesType.Wings, new ReplaceWith(ESB.rerollCantrip.val(1).withKeyword(Keyword.pain)))),
         new ItBill(1, "Fletching").prs(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.ranged))),
         new ItBill(2, "Wandify").prs(new AffectSides(SpecificSidesType.RightTwo, new AddKeyword(Keyword.singleUse), new FlatBonus(1))),
         new ItBill(2, "Leather Gloves").prs(new CopySide(SpecificSidesType.RightMost, SpecificSidesType.Wings)),
         new ItBill(
            2,
            new SpellBill().cost(1).title("remedy").eff(new EffBill().heal(1).visual(VisualEffectType.HealBasic).keywords(Keyword.cleanse, Keyword.singleCast))
         ),
         new ItBill(2, "Golden Thread")
            .prs(new ColLink(HeroCol.yellow, new AffectSides(new NotCondition(new TypeCondition(EffType.Damage)), new FlatBonus(1)))),
         new ItBill(2, "Clef").prs(new AffectSides(SpecificSidesType.LeftTwo, new AddKeyword(Keyword.treble))),
         new ItBill(2, "Rejuvenation Wand").prs(new AffectSides(SpecificSidesType.Wings, new ReplaceWith(ESB.wandHeal.val(10)))),
         new ItBill(2, "Quicksilver").prs(new KeepShields()),
         new ItBill(1, "Pillow").prs(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.patient))),
         new ItBill(2, "Faint Halo").prs(new OnRescue(new EffBill().self().buff(new Buff(new MaxHP(1))).bEff())),
         new ItBill(3, "Terrarium").prs(new AffectSides(SpecificSidesType.RightTwo, new ReplaceWith(ESB.shieldGrowth.val(2), ESB.manaGrowth.val(1)))),
         new ItBill(3, new SpellBill().title("Flare").cost(4).eff(new EffBill().damage(5).visual(VisualEffectType.Flame))),
         new ItBill(4, "Shortsword").prs(new AffectSides(SpecificSidesType.RightTwo, new ReplaceWith(ESB.dmg.val(2)))),
         new ItBill(3, "Longbow").prs(new AffectSides(SpecificSidesType.Wings, new ReplaceWith(ESB.arrow.val(2)))),
         new ItBill(3, "Incense").prs(new AffectSides(SpecificSidesType.Top, new AddKeyword(Keyword.rite))),
         new ItBill(3, "Juice").prs(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.selfCleanse))),
         new ItBill(3, "Golden Cup").prs(new EmptyMaxHp(100)).prs(new TriggerPersonalToGlobal(new GlobalMaxMana(100))),
         new ItBill(4, "Wristblade").prs(new AffectSides(new TypeCondition(EffType.Blank), new ReplaceWith(ESB.dmgDouble.val(1)))),
         new ItBill(3, "Ash").prs(new AffectSides(SpecificSidesType.MiddleTwo, new AddKeyword(Keyword.enduring))),
         new ItBill(3, "Pure Heart Pendant").prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(ESB.healCleanse.val(3)))),
         new ItBill(3, new SpellBill().cost(3).title("sprout").eff(new EffBill().heal(3).keywords(Keyword.channel).visual(VisualEffectType.HealBasic))),
         new ItBill(3, "Scalpel").prs(new ColLink(HeroCol.red, new AffectSides(new TypeCondition(EffType.Damage), new FlatBonus(1)))),
         new ItBill(3, "Lead Boots").prs(new AffectSides(new AddKeyword(Keyword.sticky), new FlatBonus(1))),
         new ItBill(3, "Abacus").prs(new AffectSides(new Shift())).prs(new MaxHP(1)),
         new ItBill(3, "Lightning Rod").prs(new AffectSides(SpecificSidesType.Left, new ReplaceWith(ESB.dmgRangedRampage.val(0)))),
         new ItBill(3, "Iron Pendant").prs(new IncomingEffBonus(1, EffType.Shield)),
         new ItBill(3, "Droopy Hat").prs(new MaxHP(2)).prs(new TriggerPersonalToGlobal(new GlobalMaxMana(2))),
         new ItBill(4, "Enchanted Shield").prs(new StartOfTurnSelf(new EffBill().shield(1).self().bEff())),
         new ItBill(3, "Whetstone").prs(new AffectSides(new TypeCondition(EffType.Damage, true), new FlatBonus(1))),
         new ItBill(3, "Pocket Phylactery").prs(new AffectSides(SpecificSidesType.LeftTwo, new AddKeyword(Keyword.pain))).prs(new MaxHP(5)),
         new ItBill(3, "Ladder").prs(new AffectSides(SpecificSidesType.Middle, new CopyBaseFromHeroAbove(true))),
         new ItBill(3, "Magnet").prs(new AffectSides(SpecificSidesType.RightTwo, new AddKeyword(Keyword.wham))),
         new ItBill(3, "Blood Chalice").prs(new OnOverheal(new EffBill().shield(1).self())),
         new ItBill(3, "Smelly Manure")
            .prs(
               new AffectSides(
                  new SpecificSidesCondition(SpecificSidesType.All),
                  new AffectByIndex(
                     new AddKeyword(Keyword.decay),
                     new AddKeyword(Keyword.decay),
                     new AddKeyword(Keyword.growth),
                     new AddKeyword(Keyword.growth),
                     new AddKeyword(Keyword.decay),
                     new AddKeyword(Keyword.decay)
                  )
               )
            ),
         new ItBill(3, "Lich Finger").prs(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.manaGain, Keyword.death))),
         new ItBill(3, "Unholy Strength").prs(new ShieldImmunity()).prs(new HealImmunity()).prs(new AffectSides(SpecificSidesType.Row, new FlatBonus(1))),
         new ItBill(3, "Three of a Kind").prs(new AffectSides(SpecificSidesType.RightMost, new AddKeyword(Keyword.trio))),
         new ItBill(2, "Clover").prs(new AffectSides(SpecificSidesType.LeftTwo, new FlatBonus(1, -2))),
         new ItBill(3, "Diving Suit").prs(new AffectSides(SpecificSidesType.RightTwo, new CopyBaseFromHeroAbove(false))),
         new ItBill(3, "Poem")
            .prs(new AffectSides(SpecificSidesType.Column, new ReplaceWith(ESB.shieldCantrip.val(1), ESB.shieldAll.val(1), ESB.dmgCantrip.val(1)))),
         new ItBill(2, "Syringe")
            .prs(new PersonalConditionLink(new SnapshotCondition(SnapshotConditionType.OrFewerHeroes, 2), new AffectSides(new AddKeyword(Keyword.cantrip)))),
         new ItBill(3, "Aegis").prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(ESB.shieldDuplicate.val(1).withKeyword(Keyword.steel)))),
         new ItBill(2, "Duvet").prs(new AffectSides(SpecificSidesType.RightThree, new AddKeyword(Keyword.patient))),
         new ItBill(3, "Siphon").prs(new AffectSides(new TypeCondition(EffType.Mana), new AddKeyword(Keyword.pain), new FlatBonus(1))),
         new ItBill(3, "Ritual Dagger").prs(new AffectSides(SpecificSidesType.RightMost, new ReplaceWith(ESB.resurrect.val(4).withKeyword(Keyword.death)))),
         new ItBill(4, "Harpoon").prs(new AffectSides(SpecificSidesType.RightMost, new ReplaceWith(ESB.dmgVuln.val(1)))),
         new ItBill(2, "Wild Seeds").prs(new AffectSides(SpecificSidesType.LeftTwo, new AddKeyword(Keyword.undergrowth))),
         new ItBill(3, "Mana Bomb").prs(new AffectSides(SpecificSidesType.RightMost, new ReplaceWith(ESB.manaBomb.val(0)))),
         new ItBill(3, "Viscera").prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(ESB.dmgFleshPain.val(0)))),
         new ItBill(
            3,
            new SpellBill()
               .cost(3)
               .title("heat")
               .eff(new EffBill().keywords().group().heal(3).keywords(Keyword.cleanse).restrict(StateConditionType.HasShields))
         ),
         new ItBill(3, "Relic").prs(new AddKeyword(Keyword.swapTerminal)),
         new ItBill(4, "Corruption").prs(new AffectSides(new TypeCondition(EffType.Heal), new AddKeyword(Keyword.evil), new FlatBonus(1))),
         new ItBill(4, "Pulley").prs(new AffectSides(new Shift(false))).prs(new ItemSlots(2)),
         new ItBill(4, new SpellBill().cost(4).title("Mark").eff(new EffBill().damage(2).keywords(Keyword.vulnerable).visual(VisualEffectType.Slice))),
         new ItBill(4, "Chakram").prs(new AffectSides(new TypeCondition(EffType.Shield), new ChangeType(ESB.arrow, "ranged damage"))),
         new ItBill(5, "Cracked Plate").prs(new MaxHP(-4)).prs(new StartOfTurnSelf(new EffBill().shield(4).self().bEff())),
         new ItBill(4, "Splitting Arrows").prs(new AffectSides(SpecificSidesType.RightTwo, new ReplaceWith(ESB.arrowCleave.val(1)))),
         new ItBill(4, "Hissing Ring").prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(ESB.dmgPoison.val(2)))),
         new ItBill(5, "Magic Staff").prs(new AffectSides(new TypeCondition(EffType.Mana), new SetValue(2))),
         new ItBill(3, "Obol").prs(new AffectSides(SpecificSidesType.RightTwo, new AddKeyword(Keyword.deathwish))),
         new ItBill(4, "Demon Eye").prs(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.pain), new FlatBonus(2))),
         new ItBill(4, "Glass Blade").prs(new AffectSides(SpecificSidesType.Left, new ReplaceWith(ESB.dmg.val(5)))).prs(new LostOnDeath("Glass Blade")),
         new ItBill(4, new SpellBill().cost(2).title("hack").eff(new EffBill().replaceBlanksWith(ESB.dmg.val(4)))),
         new ItBill(3, "Antivenom").prs(new DamageImmunity(true, false)),
         new ItBill(4, "Chainmail").prs(new MaxHP(3)),
         new ItBill(
            4,
            new SpellBill()
               .cost(3)
               .title("hex")
               .eff(
                  new EffBill()
                     .kill()
                     .value(6)
                     .keywords(Keyword.ranged, Keyword.singleCast)
                     .restrict(TargetingRestriction.ExactlyValue)
                     .visual(VisualEffectType.Cross)
                     .value(6)
               )
         ),
         new ItBill(4, new SpellBill().cost(4).title("invest").eff(new EffBill().mana(6).keywords(Keyword.future, Keyword.cooldown))),
         new ItBill(4, "Life Bolt").prs(new AfterUseAbility(null, new EffBill().heal(1).self().bEff())),
         new ItBill(5, "Soup").prs(new AffectSides(new TypeCondition(EffType.Shield), new ChangeType(ESB.healShield, "heal and shield"))),
         new ItBill(5, "Tiara").prs(new AffectSides(new TypeCondition(EffType.Mana), new AddKeyword(Keyword.selfHeal))),
         new ItBill(4, "Charged Skull").prs(new OnDeathEffect(new EffBill().mana(4).bEff(), new ManaGainEvent(4, "Charged Skull"))),
         new ItBill(4, "Apple").prs(new AffectSides(SpecificSidesType.LeftTwo, new AddKeyword(Keyword.ego))),
         new ItBill(4, "Mana Jelly").prs(new AffectSides(new TypeCondition(EffType.Mana), new AddKeyword(Keyword.singleUse, Keyword.cantrip))),
         new ItBill(4, "Jester Cap").prs(new AffectSides(SpecificSidesType.All, new ReplaceWithEnt(HeroTypeUtils.byName("jester")))),
         new ItBill(5, "Natural").prs(new ColLink(HeroCol.blue, new AffectSides(SpecificSidesType.RightTwo, new ReplaceWith(ESB.mana.val(2))))),
         new ItBill(4, "Updog").prs(new AffectSides(SpecificSidesType.Top, new AddKeyword(Keyword.overdog))),
         new ItBill(4, "Flawed Diamond").prs(new AffectSides(SpecificSidesType.LeftTwo, new AddKeyword(Keyword.pristine))).prs(new EmptyMaxHp(4)),
         new ItBill(4, "Duelling Pistol").prs(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.duel))),
         new ItBill(4, "Ink Bottle").prs(new AffectSides(new TypeCondition(EffType.Blank), new ReplaceWith(ESB.shieldCantrip.val(1)))),
         new ItBill(4, "Eyepatch").prs(new AffectSides(SpecificSidesType.Left, new RemoveAllKeywords())),
         new ItBill(2, "PowerStone")
            .prs(new ColLink(HeroCol.grey, new AffectSides(SpecificSidesType.Middle, new ReplaceWith(ESB.mana.val(0).withKeyword(Keyword.steel))))),
         new ItBill(4, "Red flag").prs(new MaxHP(3)).prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(ESB.redFlag))),
         new ItBill(4, "Inner Strength").prs(new AffectSides(new RemoveKeywordColour(Colours.red, Colours.purple))),
         new ItBill(4, "Alembic").prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(ESB.giveManaGainPain))),
         new ItBill(4, "Bonesaw").prs(new ColLink(HeroCol.red, new AffectSides(SpecificSidesType.Wings, new ReplaceWith(ESB.dmgCruel.val(3))))),
         new ItBill(3, "Full Moon")
            .prs(
               new PersonalTurnRequirement(
                  new TurnRequirementEveryN(2), new AffectSides(SpecificSidesType.All, new ReplaceWithEnt(MonsterTypeLib.byName("wolf")))
               )
            ),
         new ItBill(4, "Shuriken").prs(new AffectSides(SpecificSidesType.RightTwo, new AddKeyword(Keyword.ranged, Keyword.chain))),
         new ItBill(4, "Troll Nose").prs(new StartRegenned(1)),
         new ItBill(5, "Dragonhide Gloves").prs(new CopySide(SpecificSidesType.Middle, SpecificSidesType.Wings)),
         new ItBill(4, "Glowing Egg").prs(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.growth))),
         new ItBill(0, "Eggshell").prs(new AffectSides(SpecificSidesType.Right, new AddKeyword(Keyword.zeroed), new FlatBonus(-1))),
         new ItBill(4, "Whiskers")
            .prs(new AffectSides(SpecificSidesType.RightThree, new ReplaceWith(ESB.arrowCopycat.val(1), ESB.dmgCopycat.val(1), ESB.shieldCopycat.val(1)))),
         new ItBill(4, "Faerie Pact").prs(new AffectSides(SpecificSidesType.RightMost, new ReplaceWith(ESB.mana.val(4)))).prs(new MaxHP(-4)),
         new ItBill(3, "Fangs").prs(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.selfHeal))),
         new ItBill(4, "Cart").prs(new TriggerPersonalToGlobal(new GlobalHeroes(new ItemSlots(1)))).prs(new ItemSlots(-2)),
         new ItBill(
            4, new SpellBill().title("beam").cost(5).eff(new EffBill().damage(7).keywords(Keyword.singleCast, Keyword.ranged).visual(VisualEffectType.Beam))
         ),
         new ItBill(3, "Dynamo").prs(new AffectSides(SpecificSidesType.RightTwo, new AddKeyword(Keyword.singleUse, Keyword.era))),
         new ItBill(5, "Cracked Wheel").prs(new AffectSides(new AddKeyword(Keyword.sticky))).prs(new TriggerPersonalToGlobal(new GlobalBonusRerolls(1))),
         new ItBill(4, "Cocoon").prs(new AffectSides(new AddKeyword(Keyword.era), new FlatBonus(-1))),
         new ItBill(5, "Shimmering Halo").prs(new OnRescue(new EffBill().self().buff(new Buff(new MaxHP(2))).bEff())),
         new ItBill(5, "Wand Grips").prs(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.singleUse), new FlatBonus(2))),
         new ItBill(5, "Lens").prs(new AffectSides(new TypeCondition(EffType.Heal), new AddKeyword(Keyword.focus))),
         new ItBill(5, "Poodle").prs(new AffectSides(SpecificSidesType.Column, new AddKeyword(Keyword.dog))),
         new ItBill(5, "Decree").prs(new AffectSides(new AddKeyword(Keyword.dogma))),
         new ItBill(5, "Erythrocyte").prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(ESB.shieldFlesh.val(0)))),
         new ItBill(5, "Mini Crossbow").prs(new AffectSides(SpecificSidesType.Top, new ReplaceWith(ESB.rangedEngage.val(2)))),
         new ItBill(5, "Crystallise").prs(new AffectSides(new AddKeyword(Keyword.stasis))),
         new ItBill(5, "Longsword").prs(new AffectSides(SpecificSidesType.Row, new ReplaceWith(ESB.dmg.val(3)))),
         new ItBill(5, "Demon Horn").prs(new AffectSides(SpecificSidesType.RightMost, new ReplaceWith(ESB.demonHorn.val(2)))),
         new ItBill(4, "Pauldron").prs(new AffectSides(SpecificSidesType.Wings, new FlatBonus(2, -2))),
         new ItBill(5, "Sack of Mana").prs(new AffectSides(SpecificSidesType.Column, new ReplaceWith(ESB.blankItem, ESB.mana.val(4), ESB.blankItem))),
         new ItBill(5, "Enchanted Harp").prs(new AffectSides(SpecificSidesType.Middle, new ReplaceWith(ESB.shieldAll.val(2)))),
         new ItBill(5, "Ambrosia").prs(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.rescue))),
         new ItBill(5, "Fearless").prs(new MaxHP(1)).prs(new AffectSides(SpecificSidesType.RightMost, new ReplaceWith(ESB.stun))),
         new ItBill(5, "Ordinary Triangle").prs(new AffectSides(new ExactlyCondition(3), new FlatBonus(1))),
         new ItBill(5, "Monocle").prs(new AffectSides(SpecificSidesType.Middle, new AddKeyword(Keyword.engage))),
         new ItBill(5, "Justice").prs(new AffectSides(new TypeCondition(EffType.Damage), new AddKeyword(Keyword.guilt), new FlatBonus(1))),
         new ItBill(5, "Simplicity")
            .prs(new AffectSides(new TypeCondition(Arrays.asList(EffType.Heal, EffType.Mana, EffType.Damage, EffType.Shield), true), new FlatBonus(1))),
         new ItBill(5, "Glyph of Purity").prs(new AffectSides(SpecificSidesType.Wings, new AddKeyword(Keyword.cleanse))),
         new ItBill(5, "Door")
            .prs(
               new PersonalConditionLink(
                  new SnapshotCondition(SnapshotConditionType.OrMoreMonsters, 4), new AffectSides(new TypeCondition(EffType.Shield), new FlatBonus(2))
               )
            ),
         new ItBill(5, "Ruby").prs(new IncomingEffBonus(2, EffType.Heal)).prs(new EmptyMaxHp(2)),
         new ItBill(5, "Whiskey").prs(new MaxHP(3)).prs(new EmptyMaxHp(6)),
         new ItBill(5, "Polished Emerald").prs(new CopyAlliedItems(2, 4)),
         new ItBill(5, "Treasure Chest").prs(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.hoard, Keyword.singleUse))),
         new ItBill(5, "Sling").prs(new AffectSides(SpecificSidesType.Bot, new AddKeyword(Keyword.underdog))),
         new ItBill(5, "Shining Bow").prs(new AffectSides(new TypeCondition(EffType.Damage), new AddKeyword(Keyword.ranged))),
         new ItBill(6, "Sponge").prs(new OtherDeathEffect("Sponge", false, new EffBill().self().buff(new Buff(1, new AffectSides(new FlatBonus(1)))))),
         new ItBill(5, "Sceptre").prs(new AffectSides(SpecificSidesType.Left, new ReplaceWith(ESB.scepter.val(1)))),
         new ItBill(5, "Bag of Holding").prs(new ItemSlots(2)).prs(new MaxHP(2)),
         new ItBill(7, "Wine").prs(new StartPoisoned(1)).prs(new TriggerPersonalToGlobal(new GlobalMonsters(new StartPoisoned(1)))),
         new ItBill(4, "Gizmo").prs(SpecificSidesType.RightTwo, new AddKeyword(Keyword.shifter))
      );
   }
}
