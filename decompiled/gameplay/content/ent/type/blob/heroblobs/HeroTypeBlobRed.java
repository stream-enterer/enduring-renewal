package com.tann.dice.gameplay.content.ent.type.blob.heroblobs;

import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.bill.HTBill;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.VisualEffectType;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellBill;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.Flip;
import com.tann.dice.gameplay.trigger.personal.immunity.DamageImmunity;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.StateConditionType;
import java.util.ArrayList;
import java.util.List;

public class HeroTypeBlobRed {
   public static List<HeroType> makeDesigned() {
      List<HTBill> bills = new ArrayList<>();
      HeroCol col = HeroCol.red;
      int lv = 1;
      bills.add(
         new HTBill(col, lv)
            .name("Healer")
            .hp(5)
            .sides(ESB.mana.val(2), ESB.mana.val(1), ESB.heal.val(4), ESB.heal.val(4), ESB.heal.val(2), ESB.heal.val(2))
            .spell(new SpellBill().cost(2).title("Mend").eff(new EffBill().friendly().visual(VisualEffectType.HealBasic).setToHp(5)))
      );
      bills.add(
         new HTBill(col, lv)
            .name("Gardener")
            .hp(5)
            .sides(ESB.mana.val(2), ESB.healGroooooowth.val(1), ESB.heal.val(2), ESB.heal.val(2), ESB.healMana.val(1))
            .spell(
               new SpellBill()
                  .title("hemlock")
                  .cost(2)
                  .eff(new EffBill().damage(1).keywords(Keyword.poison, Keyword.singleCast).visual(VisualEffectType.Slice))
            )
      );
      bills.add(
         new HTBill(col, lv)
            .name("Acolyte")
            .hp(5)
            .sides(ESB.healVitality.val(3), ESB.mana.val(1), ESB.healCleanse.val(1), ESB.healCleanse.val(1), ESB.mana.val(1), ESB.mana.val(1))
            .spell(new SpellBill().cost(2).title("restore").eff(new EffBill().heal(1).group().visual(VisualEffectType.HealBasic)))
      );
      bills.add(
         new HTBill(col, lv)
            .name("Mystic")
            .hp(4)
            .sides(ESB.healShieldMana.val(1), ESB.healMana.val(1), ESB.healMana.val(1), ESB.healMana.val(1), ESB.mana.val(1))
            .spell(new SpellBill().cost(1).title("gaze").eff(new EffBill().reroll(1).keywords(Keyword.future)))
      );
      bills.add(
         new HTBill(col, lv)
            .name("Splint")
            .hp(4)
            .sides(ESB.wandMana.val(3), ESB.wandMana.val(2), ESB.wandHeal.val(5), ESB.wandHeal.val(5), ESB.wandMana.val(1), ESB.blank)
            .spell(new SpellBill().cost(2).title("bandage").eff(new EffBill().healAndShield(1).keywords(Keyword.singleCast, Keyword.cleave)))
      );
      int var6 = 2;
      bills.add(
         new HTBill(col, var6)
            .name("Druid")
            .hp(7)
            .sides(ESB.damageGrowth.val(2), ESB.shieldGrowth.val(2), ESB.mana.val(2), ESB.mana.val(2), ESB.healCleanse.val(2), ESB.blank)
            .spell(
               new SpellBill()
                  .cost(3)
                  .title("balance")
                  .eff(new EffBill().damage(1).group().visual(VisualEffectType.Slice).bonusUntargeted(new EffBill().heal(1).group()))
            )
      );
      bills.add(
         new HTBill(col, var6)
            .name("Herbalist")
            .hp(7)
            .sides(ESB.mana.val(2), ESB.mana.val(2), ESB.healRegen.val(1), ESB.healRegen.val(1), ESB.wandPoison.val(1), ESB.giveGrowth)
            .spell(
               new SpellBill()
                  .title("vine")
                  .cost(1)
                  .power(1.75F)
                  .eff(new EffBill().or(new EffBill().heal(1).visual(VisualEffectType.HealBasic), new EffBill().damage(1).visual(VisualEffectType.Slice)))
            )
      );
      bills.add(
         new HTBill(col, var6)
            .name("Medic")
            .hp(7)
            .sides(ESB.healRegen.val(2), ESB.mana.val(2), ESB.healCleave.val(2), ESB.healCleave.val(2))
            .spell(new SpellBill().title("renew").cost(1).eff(new EffBill().friendly().setToHp(4).visual(VisualEffectType.HealBasic)))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Priestess")
            .hp(5)
            .sides(ESB.healVitality.val(4), ESB.healVitality.val(4), ESB.healMana.val(1), ESB.healMana.val(1), ESB.healAll.val(1), ESB.healAll.val(1))
            .spell(
               new SpellBill()
                  .title("pray")
                  .cost(1)
                  .eff(new EffBill().group().healAndShield(1).restrict(StateConditionType.Dying).keywords(Keyword.deplete).visual(VisualEffectType.HealBasic))
            )
      );
      bills.add(
         new HTBill(col, var6)
            .name("Vampire")
            .hp(7)
            .sides(ESB.mana.val(2), ESB.mana.val(2), ESB.dmgSelfHeal.val(2), ESB.dmgSelfHeal.val(2), ESB.heal.val(5), ESB.blank)
            .spell(new SpellBill().cost(2).title("infuse").eff(new EffBill().group().heal(1).keywords(Keyword.terminal).visual(VisualEffectType.HealBasic)))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Enchanter")
            .hp(7)
            .sides(ESB.healShieldMana.val(2), ESB.giveEngage, ESB.giveSelfShieldSelfHeal, ESB.giveSelfShieldSelfHeal, ESB.giveSelfHeal, ESB.giveSelfHeal)
            .spell(new SpellBill().title("flip").cost(2).eff(new EffBill().friendly().group().buff(new Buff(1, new AffectSides(new Flip())))))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Disciple")
            .hp(8)
            .sides(ESB.healDouble.val(2), ESB.healDouble.val(2), ESB.mana.val(2), ESB.mana.val(2), ESB.resurrect.val(1), ESB.blank)
            .spell(new SpellBill().cost(1).title("Glow").eff(new EffBill().replaceBlanksWith(ESB.healShield.val(2))))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Fey")
            .hp(6)
            .sides(ESB.dmgWeaken.val(1), ESB.dmgWeaken.val(1), ESB.healMana.val(1), ESB.healMana.val(1), ESB.healBoost.val(1), ESB.healBoost.val(1))
            .spell(
               new SpellBill()
                  .cost(2)
                  .title("circle")
                  .eff(new EffBill().friendly().keywords(Keyword.singleCast).group().specialAddKeyword(Keyword.selfHeal).visual(VisualEffectType.HealBasic))
            )
      );
      var6 = 3;
      bills.add(
         new HTBill(col, var6)
            .name("Doctor")
            .hp(9)
            .sides(ESB.poisonAll.val(1), ESB.healMana.val(2), ESB.healRegen.val(2), ESB.healRegen.val(2), ESB.healMana.val(2), ESB.blank)
            .spell(
               new SpellBill().cost(3).power(1.15F).title("liquor").eff(new EffBill().keywords(Keyword.cleanse).heal(10).visual(VisualEffectType.HealBasic))
            )
      );
      bills.add(
         new HTBill(col, var6)
            .name("Forsaken")
            .hp(9)
            .sides(ESB.resurrect.val(3), ESB.dmgSelfHeal.val(3), ESB.healAll.val(2), ESB.healAll.val(2), ESB.dmgSelfHeal.val(3), ESB.manaPain.val(3))
            .spell(
               new SpellBill()
                  .title("bind")
                  .cost(3)
                  .eff(new EffBill().friendly().keywords(Keyword.deplete).visual(VisualEffectType.HealBasic).buff(new Buff(1, new DamageImmunity(true, true))))
            )
      );
      bills.add(
         new HTBill(col, var6)
            .name("Prophet")
            .hp(8)
            .sides(ESB.healRescue.val(3), ESB.healVitality.val(5), ESB.healMana.val(2), ESB.healMana.val(2), ESB.healVitality.val(5), ESB.blank)
            .spell(new SpellBill().title("soothe").cost(4).eff(new EffBill().heal(1).visual(VisualEffectType.HealBasic).group().keywords(Keyword.regen)))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Shaman")
            .hp(11)
            .sides(ESB.healVitality.val(5), ESB.heal.val(10), ESB.damageGrowth.val(3), ESB.shieldGrowth.val(3), ESB.healMana.val(2), ESB.blank)
            .spell(
               new SpellBill().title("ritual").cost(3).eff(new EffBill().visual(VisualEffectType.HealBasic).heal(2).keywords(Keyword.cleanse, Keyword.cleave))
            )
      );
      bills.add(
         new HTBill(col, var6)
            .name("Witch")
            .hp(10)
            .sides(ESB.mana.val(3), ESB.healCleanse.val(5), ESB.healBoost.val(1), ESB.dmgWeaken.val(1), ESB.healCleave.val(3), ESB.blank)
            .spell(new SpellBill().cost(1).title("salve").eff(new EffBill().visual(VisualEffectType.HealBasic).heal(2)))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Wraith")
            .hp(9)
            .sides(ESB.dmgSelfHeal.val(4), ESB.giveSelfShieldSelfHeal, ESB.mana.val(2), ESB.mana.val(2), ESB.dodgeCantrip, ESB.blank)
            .spell(
               new SpellBill()
                  .cost(1)
                  .title("Leech")
                  .eff(new EffBill().friendly().keywords(Keyword.cooldown).kill().bonusUntargeted(new EffBill().heal(5).group()))
            )
      );
      bills.add(
         new HTBill(col, var6)
            .name("Surgeon")
            .hp(9)
            .sides(ESB.healMana.val(2), ESB.healMana.val(2), ESB.healShield.val(3), ESB.healShield.val(3), ESB.healDouble.val(3), ESB.healDouble.val(3))
            .spell(new SpellBill().cost(3).title("Operate").eff(new EffBill().keywords(Keyword.deplete).resurrect(1)))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Fate")
            .hp(7)
            .sides(ESB.healShieldMana.val(2), ESB.healShieldMana.val(2), ESB.wandMana.val(2), ESB.wandMana.val(2), ESB.dodge, ESB.blank)
            .spell(new SpellBill().cost(4).title("Strand").eff(new EffBill().friendly().heal(2).keywords(Keyword.spellRescue)))
      );
      List<HeroType> result = new ArrayList<>();

      for (HTBill htBill : bills) {
         result.add(htBill.bEntType());
      }

      return result;
   }
}
