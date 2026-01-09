package com.tann.dice.gameplay.content.ent.type.blob.heroblobs;

import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.bill.HTBill;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.VisualEffectType;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellBill;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.Tactic;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.TacticCost;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.TacticCostType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HeroTypeBlobGrey {
   public static List<HeroType> makeDesigned() {
      List<HTBill> bills = new ArrayList<>();
      HeroCol col = HeroCol.grey;
      int lv = 1;
      bills.add(
         new HTBill(col, lv).name("Defender").hp(7).sides(ESB.shield.val(3), ESB.shield.val(2), ESB.dmg.val(1), ESB.dmg.val(1), ESB.shield.val(1), ESB.blank)
      );
      bills.add(
         new HTBill(col, lv)
            .name("Buckle")
            .hp(6)
            .sides(ESB.shieldPristine.val(2), ESB.dmgHeavy.val(2), ESB.shield.val(2), ESB.shield.val(2), ESB.blank, ESB.blank)
      );
      bills.add(
         new HTBill(col, lv)
            .name("Squire")
            .hp(5)
            .sides(ESB.shieldFocus.val(2), ESB.dmgFocus.val(1), ESB.redirect.val(2), ESB.redirect.val(2), ESB.shield.val(1), ESB.shield.val(1))
      );
      bills.add(
         new HTBill(col, lv)
            .name("Alloy")
            .hp(4)
            .sides(
               ESB.shieldCopycat.val(1),
               ESB.shieldRepel.val(1),
               ESB.shieldCleave.val(1),
               ESB.healShield.val(1),
               ESB.shieldCleanse.val(1),
               ESB.shieldCantrip.val(1)
            )
      );
      bills.add(new HTBill(col, lv).name("Wallop").hp(5).sides(ESB.undying, ESB.dmgSelfShield.val(2), ESB.stun, ESB.stun, ESB.dmgSelfShield.val(1)));
      int var6 = 2;
      bills.add(
         new HTBill(col, var6)
            .name("Knight")
            .hp(7)
            .sides(ESB.shieldSteel.val(3), ESB.shieldSteel.val(3), ESB.dmgExert.val(3), ESB.dmgExert.val(3), ESB.blank, ESB.blank)
            .tactic(new Tactic("Parry", new TacticCost(TacticCostType.basicSword, TacticCostType.basicSword), new EffBill().shield(3).bEff(), 1.75F))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Armorer")
            .hp(9)
            .sides(ESB.dmgHeavy.val(3), ESB.shield.val(4), ESB.shieldCleave.val(1), ESB.shieldCleave.val(1), ESB.shieldSmith.val(1), ESB.shieldSmith.val(1))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Bard")
            .hp(6)
            .sides(ESB.shieldCantrip.val(2), ESB.rerollCantrip.val(1), ESB.shieldAll.val(1), ESB.shieldAll.val(1), ESB.dmgCantrip.val(1))
            .tactic(new Tactic("Else", new TacticCost(TacticCostType.blank), new EffBill().shield(1).keywords(Keyword.cleanse).bEff()))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Cleric")
            .hp(8)
            .sides(ESB.healShield.val(3), ESB.shieldCleanse.val(2), ESB.wandMana.val(2), ESB.wandMana.val(2))
            .spell(new SpellBill().cost(1).title("light").eff(new EffBill().shield(1).keywords(Keyword.cleanse, Keyword.cleave, Keyword.singleCast)))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Guardian")
            .hp(8)
            .sides(ESB.shieldCleave.val(2), ESB.dmgCleave.val(1), ESB.shieldEngage.val(3), ESB.shieldEngage.val(3), ESB.blank, ESB.blank)
      );
      bills.add(new HTBill(col, var6).name("Pilgrim").hp(8).sides(ESB.recharge, ESB.undying, ESB.giveSelfShield, ESB.giveSelfShield, ESB.stun, ESB.stun));
      bills.add(
         new HTBill(col, var6)
            .name("Monk")
            .hp(9)
            .sides(ESB.shieldCleanse.val(2), ESB.shieldRepel.val(1), ESB.redirect.val(3), ESB.redirect.val(3), ESB.shieldRepel.val(1), ESB.blank)
      );
      bills.add(
         new HTBill(col, var6)
            .name("Warden")
            .hp(10)
            .sides(ESB.shield.val(4), ESB.shield.val(3), ESB.dmg.val(2), ESB.dmg.val(2), ESB.shield.val(2), ESB.shield.val(1))
      );
      var6 = 3;
      bills.add(
         new HTBill(col, var6)
            .name("Keeper")
            .hp(10)
            .sides(ESB.shieldRepel.val(2), ESB.shieldRepel.val(2), ESB.shieldSteel.val(5), ESB.shieldSteel.val(5), ESB.giveSelfShieldSelfHeal, ESB.blank)
      );
      bills.add(
         new HTBill(col, var6)
            .name("Paladin")
            .hp(11)
            .sides(ESB.shieldCleanse.val(4), ESB.dmgHeavy.val(4), ESB.healShield.val(3), ESB.healShield.val(3), ESB.dmgHeavy.val(4), ESB.dmgHeavy.val(4))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Prince")
            .hp(9)
            .sides(ESB.dmgInspire.val(3), ESB.shieldDuplicate.val(3), ESB.healShield.val(3), ESB.healShield.val(3))
            .tactic(
               new Tactic(
                  "Unite",
                  new TacticCost(TacticCostType.basicSword, TacticCostType.basicShield, TacticCostType.basicHeal, TacticCostType.blank),
                  new EffBill().damage(15).visual(VisualEffectType.Flame).bEff()
               )
            )
      );
      bills.add(
         new HTBill(col, var6)
            .name("Stalwart")
            .hp(11)
            .sides(
               ESB.shieldCleanse.val(3),
               ESB.shieldCleanse.val(3),
               ESB.shieldCleave.val(2),
               ESB.shieldCleave.val(2),
               ESB.shieldDouble.val(2),
               ESB.dmgExert.val(5)
            )
      );
      bills.add(
         new HTBill(col, var6)
            .name("Poet")
            .hp(8)
            .sides(ESB.manaCantrip.val(1), ESB.shieldAll.val(2), ESB.shieldCharged.val(2), ESB.shieldCharged.val(2), ESB.shieldCantrip.val(2))
            .spell(new SpellBill().cost(4).title("clink").eff(new EffBill().shield(1).group().keywords(Keyword.boost, Keyword.singleCast)))
      );
      bills.addAll(
         Arrays.asList(
            new HTBill(col, var6)
               .name("Valkyrie")
               .hp(9)
               .sides(ESB.dmgDeathwish.val(4), ESB.shieldRescue.val(2), ESB.undying, ESB.undying, ESB.resurrect.val(2), ESB.resurrect.val(1)),
            new HTBill(col, var6)
               .name("Stoic")
               .hp(15)
               .sides(ESB.stun, ESB.redirect.val(3), ESB.dmgSelfShield.val(2), ESB.dmgSelfShield.val(2), ESB.redirect.val(2), ESB.redirect.val(1))
         )
      );
      List<HeroType> result = new ArrayList<>();

      for (HTBill htBill : bills) {
         result.add(htBill.bEntType());
      }

      return result;
   }
}
