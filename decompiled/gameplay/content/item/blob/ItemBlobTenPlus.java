package com.tann.dice.gameplay.content.item.blob;

import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.effect.eff.VisualEffectType;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellBill;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementFirstN;
import com.tann.dice.gameplay.trigger.personal.Undying;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.HighestCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.OrMoreCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.TypeCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddAllKeywordsFromOtherSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AffectByIndex;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ChangeToAboveType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ChangeType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.FlatBonus;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.MultiplyEffect;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.SetValue;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.SwapSides;
import com.tann.dice.gameplay.trigger.personal.death.OnDeathEffect;
import com.tann.dice.gameplay.trigger.personal.hp.BonusHpPerBase;
import com.tann.dice.gameplay.trigger.personal.hp.MaxHP;
import com.tann.dice.gameplay.trigger.personal.item.copyItem.CopyAlliedItems;
import com.tann.dice.gameplay.trigger.personal.item.copyItem.CopyInvItems;
import com.tann.dice.gameplay.trigger.personal.linked.PersonalTurnRequirement;
import com.tann.dice.gameplay.trigger.personal.specialPips.GhostHP;
import com.tann.dice.gameplay.trigger.personal.specialPips.pipLoc.PipLoc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBlobTenPlus {
   public static List<ItBill> makeAll() {
      List<ItBill> result = new ArrayList<>();
      result.addAll(makeTenFifteen());
      result.addAll(makeSixteenPlus());
      return result;
   }

   private static List<ItBill> makeTenFifteen() {
      return Arrays.asList(
         new ItBill(10, "Puzzle Box")
            .prs(new AffectSides(SpecificSidesType.LeftTwo, new AddKeyword(Keyword.enduring)))
            .prs(new AffectSides(new SwapSides(SpecificSidesType.Left, SpecificSidesType.Middle))),
         new ItBill(10, "Antlers")
            .prs(new AffectSides(new TypeCondition(EffType.Heal), new FlatBonus(1)))
            .prs(new AffectSides(new TypeCondition(EffType.Shield), new FlatBonus(1)))
            .prs(new AffectSides(new TypeCondition(EffType.Damage), new FlatBonus(1)))
            .prs(new AffectSides(new TypeCondition(EffType.Mana), new FlatBonus(1))),
         new ItBill(9, "Egg Basket")
            .prs(
               new AffectSides(
                  SpecificSidesType.All,
                  new AffectByIndex(
                     new FlatBonus(6),
                     new ReplaceWith(ESB.blankItem),
                     new ReplaceWith(ESB.blankItem),
                     new ReplaceWith(ESB.blankItem),
                     new ReplaceWith(ESB.blankItem),
                     new ReplaceWith(ESB.blankItem)
                  )
               )
            ),
         new ItBill(10, "Stream").prs(new AffectSides(SpecificSidesType.Right, new AddAllKeywordsFromOtherSides())),
         new ItBill(10, "Mithril Shields").prs(new AffectSides(new TypeCondition(EffType.Shield, false), new AddKeyword(Keyword.cantrip))),
         new ItBill(11, "Heart of Light")
            .prs(new AffectSides(new TypeCondition(EffType.Blank), new ReplaceWith(ESB.healCleanse.val(10).withKeyword(Keyword.quadUse)))),
         new ItBill(9, "Shiny Gauntlets").prs(new AffectSides(SpecificSidesType.Wings, new AddKeyword(Keyword.pristine))),
         new ItBill(11, "Telescope").prs(new AffectSides(SpecificSidesType.LeftTwo, new AddKeyword(Keyword.spy))),
         new ItBill(11, "Mana Potion").prs(new AffectSides(SpecificSidesType.Top, new ReplaceWith(ESB.manaPotion.val(20)))),
         new ItBill(11, "Stilts").prs(new AffectSides(SpecificSidesType.Column, new AddKeyword(Keyword.tall))),
         new ItBill(11, "Full Plate").prs(new MaxHP(14)),
         new ItBill(11, "Diamond Skull").prs(new MaxHP(2)).prs(new OnDeathEffect(new EffBill().friendly().group().buff(new AffectSides(new FlatBonus(1))))),
         new ItBill(11, "Poseidon Charm").prs(new AffectSides(SpecificSidesType.Wings, new AddKeyword(Keyword.cleave))),
         new ItBill(11, "Bismuth").prs(new AffectSides(new TypeCondition(EffType.Blank), new ReplaceWith(ESB.giveCopycat))),
         new ItBill(11, "Economancy").prs(new AffectSides(SpecificSidesType.Column, new AddKeyword(Keyword.squared))),
         new ItBill(12, "Shining Emerald").prs(new CopyAlliedItems(13, 20)),
         new ItBill(12, "Broomstick").prs(new AffectSides(SpecificSidesType.Wings, new AddKeyword(Keyword.manaGain))),
         new ItBill(12, "Locket").prs(new AffectSides(new AddKeyword(Keyword.resilient))),
         new ItBill(12, "Ruby Shards")
            .prs(new AffectSides(new TypeCondition(Arrays.asList(EffType.Heal, EffType.Shield), false), new AddKeyword(Arrays.asList(Keyword.cleave)))),
         new ItBill(12, "Coffee").prs(new AffectSides(new HighestCondition(), new AddKeyword(Keyword.cantrip))),
         new ItBill(12, "Conjuring Rings").prs(new AffectSides(SpecificSidesType.Top, new AddKeyword(Keyword.duplicate))),
         new ItBill(13, "Bronze Bell").prs(new AffectSides(SpecificSidesType.Wings, new AddKeyword(Keyword.resonate))),
         new ItBill(13, "Titanbane Amulet").prs(new AffectSides(new OrMoreCondition(5), new AddKeyword(Keyword.dispel))),
         new ItBill(13, "Demon Heart").prs(new AffectSides(SpecificSidesType.Column, new AddKeyword(Keyword.inflictInflictDeath))),
         new ItBill(13, "Illegal")
            .prs(new AffectSides(SpecificSidesType.LeftTwo, new AffectByIndex(new ReplaceWith(ESB.rerollCantrip.val(2)), new ReplaceWith(ESB.recharge)))),
         new ItBill(14, "Ethereal Cloak").prs(new AffectSides(new ReplaceWith(ESB.dodge))).prs(new GhostHP(PipLoc.all())),
         new ItBill(14, "Huge Sword").prs(new AffectSides(new TypeCondition(EffType.Damage), new AddKeyword(Keyword.cleave))),
         new ItBill(14, "Fertiliser").prs(new AffectSides(new AddKeyword(Keyword.growth))),
         new ItBill(14, "Whirlwind").prs(new AffectSides(SpecificSidesType.Column, new AddKeyword(Keyword.cantrip))),
         new ItBill(14, "Golden D6")
            .prs(
               new AffectSides(
                  SpecificSidesType.All,
                  new AffectByIndex(new SetValue(6), new SetValue(5), new SetValue(4), new SetValue(3), new SetValue(1), new SetValue(2))
               )
            ),
         new ItBill(14, "Third Heart").prs(new BonusHpPerBase(2, 1)),
         new ItBill(15, "Archmage Orb").prs(new AffectSides(SpecificSidesType.Middle, new AddKeyword(Keyword.charged))),
         new ItBill(15, new SpellBill().cost(13).title("infinity").eff(new EffBill().kill().visual(VisualEffectType.Singularity))),
         new ItBill(15, "Banned")
            .prs(new AffectSides(SpecificSidesType.LeftTwo, new AffectByIndex(new AddKeyword(Keyword.cleave), new AddKeyword(Keyword.growth)))),
         new ItBill(15, "Diamond Ring")
            .prs(new AffectSides(new TypeCondition(EffType.Damage, false), new ChangeType(ESB.healShieldMana, "heal, shield, managain"))),
         new ItBill(15, "Dolphin").prs(new AffectSides(new AddKeyword(Keyword.echo))),
         new ItBill(15, "Titan Blade").prs(new AffectSides(SpecificSidesType.Left, new ReplaceWith(ESB.dmgDeath.val(100)))),
         new ItBill(15, "Emerald Satchel").prs(new CopyInvItems())
      );
   }

   private static List<ItBill> makeSixteenPlus() {
      return Arrays.asList(
         new ItBill(16, "Pentagram").prs(new PersonalTurnRequirement(new TurnRequirementFirstN(5), new Undying())),
         new ItBill(16, "Farewell").prs(new OnDeathEffect(new EffBill().kill().targetType(TargetingType.Top))),
         new ItBill(16, "Blue Skink").prs(new AffectSides(new ChangeToAboveType(false))),
         new ItBill(16, "Chocolate Bar")
            .prs(new AffectSides(new TypeCondition(Arrays.asList(EffType.Damage, EffType.Shield), false), new AddKeyword(Keyword.doubleUse))),
         new ItBill(16, "Whirlpool").prs(new AffectSides(new AddAllKeywordsFromOtherSides())),
         new ItBill(17, "Taboo").prs(new AffectSides(new TypeCondition(EffType.Heal), new AddKeyword(Keyword.hyperUse))),
         new ItBill(17, "Obsidian Edge").prs(new AffectSides(new TypeCondition(EffType.Damage), new MultiplyEffect(3))),
         new ItBill(17, "Face of Horus").prs(new AffectSides(new FlatBonus(2)))
      );
   }
}
