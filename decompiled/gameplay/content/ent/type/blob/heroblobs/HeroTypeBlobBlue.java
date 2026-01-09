package com.tann.dice.gameplay.content.ent.type.blob.heroblobs;

import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.bill.HTBill;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.effect.eff.VisualEffectType;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.TargetingRestriction;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellBill;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.FlatBonus;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.StateConditionType;
import java.util.ArrayList;
import java.util.List;

public class HeroTypeBlobBlue {
   public static List<HeroType> makeDesigned() {
      List<HTBill> bills = new ArrayList<>();
      HeroCol col = HeroCol.blue;
      int lv = 1;
      bills.add(
         new HTBill(col, lv)
            .name("Mage")
            .hp(4)
            .sides(ESB.mana.val(2), ESB.mana.val(2), ESB.mana.val(1), ESB.mana.val(1), ESB.blank, ESB.blank)
            .spell(new SpellBill().title("Poke").cost(1).eff(new EffBill().damage(1).keywords(Keyword.cooldown).visual(VisualEffectType.Slice)))
      );
      bills.add(
         new HTBill(col, lv)
            .name("Prodigy")
            .hp(4)
            .sides(ESB.resurrectMana.val(1), ESB.wandMana.val(2), ESB.manaPair.val(1), ESB.manaPair.val(1), ESB.dmgSelfMandatory.val(2))
            .spell(
               new SpellBill().title("Scorch").cost(2).eff(new EffBill().damage(1).keywords(Keyword.cleave, Keyword.singleCast).visual(VisualEffectType.Flame))
            )
      );
      bills.add(
         new HTBill(col, lv)
            .name("Meddler")
            .hp(6)
            .sides(ESB.mana.val(3), ESB.manaCantrip.val(1), ESB.manaPain.val(2), ESB.manaPain.val(2), ESB.manaCantripBoned.val(1), ESB.manaCantripBoned.val(1))
            .spell(
               new SpellBill()
                  .title("Slay")
                  .cost(3)
                  .eff(new EffBill().kill().restrict(TargetingRestriction.ExactlyValue).value(3).visual(VisualEffectType.Ellipse))
            )
      );
      bills.add(
         new HTBill(col, lv)
            .name("Student")
            .hp(5)
            .sides(ESB.recharge, ESB.shieldMana.val(1), ESB.shieldMana.val(1))
            .spell(new SpellBill().title("Slice").cost(3).eff(new EffBill().damage(1).group().visual(VisualEffectType.Slice)))
      );
      bills.add(
         new HTBill(col, lv)
            .name("Initiate")
            .hp(4)
            .sides(ESB.manaCantrip.val(1), ESB.manaGrowth.val(1), ESB.wandMana.val(1), ESB.wandMana.val(1), ESB.wandMana.val(1), ESB.blank)
            .spell(new SpellBill().title("gather").cost(2).eff(new EffBill().replaceBlanksWith(ESB.mana.val(2))))
      );
      bills.add(
         new HTBill(col, lv)
            .name("Cultist")
            .hp(5)
            .sides(ESB.manaPain.val(3), ESB.manaPain.val(3), ESB.wandSelfHeal.val(1), ESB.wandSelfHeal.val(1), ESB.manaPain.val(2), ESB.manaPain.val(2))
            .spell(
               new SpellBill()
                  .title("cut")
                  .cost(1)
                  .eff(
                     new EffBill()
                        .damage(3)
                        .friendly()
                        .keywords(Keyword.cooldown)
                        .visual(VisualEffectType.Slice)
                        .bonusUntargeted(new EffBill().damage(2).targetType(TargetingType.Top).visual(VisualEffectType.Slice))
                  )
            )
      );
      int var6 = 2;
      bills.add(
         new HTBill(col, var6)
            .name("Caldera")
            .hp(6)
            .sides(ESB.wandFire.val(3), ESB.mana.val(2), ESB.dmgMana.val(1), ESB.dmgMana.val(1), ESB.mana.val(2))
            .spell(
               new SpellBill().title("scald").cost(3).eff(new EffBill().damage(2).group().restrict(StateConditionType.Damaged).visual(VisualEffectType.Flame))
            )
      );
      bills.add(
         new HTBill(col, var6)
            .name("Evoker")
            .hp(10)
            .sides(ESB.mana.val(3), ESB.mana.val(2), ESB.mana.val(1))
            .spell(new SpellBill().title("drop").cost(3).eff(new EffBill().damage(4).targetType(TargetingType.Top).visual(VisualEffectType.Anvil)))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Glacia")
            .hp(9)
            .sides(ESB.wandWeaken.val(1), ESB.mana.val(2), ESB.shieldMana.val(1), ESB.shieldMana.val(1), ESB.mana.val(1), ESB.mana.val(1))
            .spell(
               new SpellBill().title("chill").cost(2).eff(new EffBill().damage(2).keywords(Keyword.weaken, Keyword.singleCast).visual(VisualEffectType.Frost))
            )
      );
      bills.add(
         new HTBill(col, var6)
            .name("Jester")
            .hp(7)
            .sides(ESB.mana.val(3), ESB.rerollCantrip.val(1), ESB.wandMana.val(1), ESB.wandMana.val(1), ESB.dodge, ESB.blank)
            .spell(
               new SpellBill().title("flick").cost(1).eff(new EffBill().keywords(Keyword.engage, Keyword.cooldown).damage(1).visual(VisualEffectType.Slice))
            )
      );
      bills.add(
         new HTBill(col, var6)
            .name("Sparky")
            .hp(7)
            .sides(ESB.dmgMana.val(2), ESB.mana.val(1), ESB.wandCharged.val(0), ESB.wandCharged.val(0), ESB.mana.val(1), ESB.blank)
            .spell(
               new SpellBill()
                  .title("zap")
                  .cost(1)
                  .eff(
                     new EffBill().keywords(Keyword.cooldown).kill().restrict(TargetingRestriction.ExactlyValue).value(2).visual(VisualEffectType.LightningBig)
                  )
            )
      );
      bills.add(
         new HTBill(col, var6)
            .name("Myco")
            .hp(8)
            .sides(ESB.manaDecay.val(3), ESB.manaDecay.val(2), ESB.shieldGrowth.val(1), ESB.damageGrowth.val(1), ESB.manaDecay.val(1), ESB.resurrect.val(1))
            .spell(
               new SpellBill()
                  .title("Spore")
                  .cost(1)
                  .eff(new EffBill().keywords(Keyword.cooldown).friendly().buff(new Buff(1, new AffectSides(new AddKeyword(Keyword.decay), new FlatBonus(1)))))
            )
      );
      bills.add(
         new HTBill(col, var6)
            .name("Seer")
            .hp(7)
            .sides(ESB.manaGrowth.val(1), ESB.wandPoison.val(1), ESB.wandMana.val(3), ESB.wandMana.val(3), ESB.dodge, ESB.blank)
            .spell(new SpellBill().title("Foretell").cost(3).eff(new EffBill().mana(4).keywords(Keyword.future)))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Fiend")
            .hp(6)
            .sides(ESB.manaCantrip.val(1), ESB.manaPain.val(3), ESB.wandSelfHeal.val(2), ESB.wandSelfHeal.val(2), ESB.manaPain.val(2), ESB.blank)
            .spell(
               new SpellBill()
                  .title("Burn")
                  .cost(1)
                  .eff(new EffBill().damage(1).targetType(TargetingType.ALL).keywords(Keyword.cooldown).visual(VisualEffectType.Flame))
            )
      );
      var6 = 3;
      bills.add(
         new HTBill(col, var6)
            .name("Artificer")
            .hp(8)
            .sides(ESB.wandSelfHeal.val(3), ESB.wandCharged.val(2), ESB.wandMana.val(4), ESB.wandMana.val(4), ESB.wandPoison.val(2), ESB.wandHeal.val(10))
            .spell(new SpellBill().title("blades").cost(4).eff(new EffBill().damage(2).visual(VisualEffectType.MultiBlade).group()))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Weaver")
            .hp(10)
            .sides(ESB.manaDouble.val(0), ESB.mana.val(3), ESB.wandMana.val(3), ESB.wandMana.val(3), ESB.dodge, ESB.blank)
            .spell(new SpellBill().title("crush").cost(3).eff(new EffBill().damage(3).targetType(TargetingType.TopAndBot).visual(VisualEffectType.Crush)))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Sorcerer")
            .hp(7)
            .sides(ESB.manaCantrip.val(1), ESB.rerollCantrip.val(1), ESB.manaCantrip.val(1), ESB.manaCantrip.val(1), ESB.blank, ESB.blank)
            .spell(
               new SpellBill()
                  .title("miasma")
                  .cost(3)
                  .eff(new EffBill().damage(1).keywords(Keyword.poison, Keyword.cleave).visual(VisualEffectType.PerlinPoison))
            )
      );
      bills.add(
         new HTBill(col, var6)
            .name("Chronos")
            .hp(7)
            .sides(ESB.recharge, ESB.rerollCantrip.val(1), ESB.manaGrowth.val(2), ESB.manaGrowth.val(2), ESB.dodge, ESB.blank)
            .spell(new SpellBill().title("tick").cost(4).eff(new EffBill().damage(1).visual(VisualEffectType.Freeze).keywords(Keyword.weaken, Keyword.cleave)))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Warlock")
            .hp(7)
            .sides(ESB.manaLust.val(2), ESB.wandSelfHeal.val(3), ESB.manaPain.val(4), ESB.manaPain.val(4), ESB.blank, ESB.blank)
            .spell(new SpellBill().title("blaze").cost(6).eff(new EffBill().damage(13).visual(VisualEffectType.Flame)))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Ace")
            .hp(8)
            .sides(ESB.manaTriple.val(3), ESB.manaPair.val(2), ESB.mana.val(1), ESB.mana.val(1), ESB.mana.val(1))
            .spell(new SpellBill().title("Draw").cost(1).eff(new EffBill().justTarget().friendly().value(1).keywords(Keyword.boost, Keyword.deplete)))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Ghast")
            .hp(8)
            .sides(ESB.manaDeath.val(5), ESB.mana.val(2), ESB.wandWeaken.val(2), ESB.wandWeaken.val(2), ESB.mana.val(1), ESB.blank)
            .spell(
               new SpellBill()
                  .title("Harvest")
                  .cost(1)
                  .eff(
                     new EffBill()
                        .keywords(Keyword.cooldown)
                        .kill()
                        .restrict(TargetingRestriction.ExactlyValue)
                        .visual(VisualEffectType.Singularity)
                        .value(1)
                        .bonusUntargeted(new EffBill().mana(3))
                  )
            )
      );
      bills.add(
         new HTBill(col, var6)
            .name("Wizard")
            .hp(9)
            .sides(ESB.wandFightBonus.val(1), ESB.manaCantrip.val(1), ESB.shieldMana.val(1), ESB.shieldMana.val(1), ESB.dmgMana.val(1), ESB.dmgMana.val(1))
            .spell(new SpellBill().cost(4).title("Inspire").eff(new EffBill().recharge().keywords(Keyword.cooldown)))
      );
      List<HeroType> result = new ArrayList<>();

      for (HTBill htBill : bills) {
         result.add(htBill.bEntType());
      }

      return result;
   }
}
