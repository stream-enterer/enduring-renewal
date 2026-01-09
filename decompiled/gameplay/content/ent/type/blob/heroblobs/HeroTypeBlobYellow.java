package com.tann.dice.gameplay.content.ent.type.blob.heroblobs;

import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.bill.HTBill;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.VisualEffectType;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.Tactic;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.TacticCost;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.TacticCostType;
import com.tann.dice.gameplay.trigger.personal.chat.DyingShouty;
import java.util.ArrayList;
import java.util.List;

public class HeroTypeBlobYellow {
   public static List<HeroType> makeDesigned() {
      List<HTBill> bills = new ArrayList<>();
      HeroCol c = HeroCol.yellow;
      int lv = 1;
      bills.add(
         new HTBill(c, lv).name("Fighter").hp(5).sides(ESB.dmg.val(2), ESB.dmg.val(2), ESB.dmg.val(1), ESB.dmg.val(1), ESB.shield.val(1), ESB.shield.val(1))
      );
      bills.add(
         new HTBill(c, lv)
            .name("Brigand")
            .hp(5)
            .sides(ESB.dmgExert.val(3), ESB.dmgExert.val(3), ESB.dmgSelfShield.val(1), ESB.dmgSelfShield.val(1), ESB.dmg.val(1), ESB.dmg.val(1))
      );
      bills.add(new HTBill(c, lv).name("Lazy").hp(6).sides(ESB.dmg.val(3), ESB.shield.val(3)));
      bills.add(
         new HTBill(c, lv).name("Ruffian").hp(4).sides(ESB.dmgPain.val(5), ESB.dmgCleave.val(1), ESB.dmg.val(1), ESB.dmg.val(1), ESB.shield.val(2), ESB.blank)
      );
      bills.add(
         new HTBill(c, lv)
            .name("Hoarder")
            .hp(6)
            .sides(ESB.dmgGuilt.val(2), ESB.dmgHeavy.val(2), ESB.dmgExert.val(2), ESB.stick.val(2), ESB.dmgPain.val(2), ESB.dmgDeath.val(2))
      );
      int var6 = 2;
      bills.add(
         new HTBill(c, var6)
            .name("Berserker")
            .hp(8)
            .sides(ESB.dmgDeathwish.val(3), ESB.dmgCleave.val(1), ESB.dmgPain.val(4), ESB.dmgPain.val(4), ESB.blank, ESB.blank)
            .trait(new DyingShouty())
      );
      bills.add(
         new HTBill(c, var6)
            .name("Brute")
            .hp(8)
            .sides(ESB.dmgSelfShield.val(2), ESB.dmgSelfShield.val(2), ESB.dmgHeavy.val(3), ESB.dmgHeavy.val(3), ESB.stun, ESB.blank)
      );
      bills.add(
         new HTBill(c, var6)
            .name("Collector")
            .hp(8)
            .sides(
               ESB.dmgDeathwish.val(2), ESB.dmgDuplicate.val(1), ESB.damageGrowth.val(2), ESB.dmgSelfShield.val(2), ESB.dmgCleave.val(1), ESB.dmgFocus.val(1)
            )
      );
      bills.add(
         new HTBill(c, var6)
            .name("Gladiator")
            .hp(7)
            .sides(ESB.dmgEngage.val(2), ESB.dmgEngage.val(1), ESB.dmgSelfShield.val(2), ESB.dmgSelfShield.val(2), ESB.shield.val(2), ESB.blank)
      );
      bills.add(
         new HTBill(c, var6).name("Soldier").hp(7).sides(ESB.dmg.val(3), ESB.dmg.val(3), ESB.dmg.val(2), ESB.dmg.val(2), ESB.shield.val(2), ESB.shield.val(2))
      );
      bills.add(
         new HTBill(c, var6)
            .name("Whirl")
            .hp(8)
            .sides(ESB.dmgAll.val(1), ESB.dmg.val(3), ESB.dmgCleave.val(1), ESB.dmgCleave.val(1), ESB.shieldCleave.val(1), ESB.blank)
      );
      bills.add(new HTBill(c, var6).name("Scrapper").hp(7).sides(ESB.dmgBloodlust.val(1), ESB.dmgBloodlust.val(1), ESB.dmgSteel.val(1), ESB.dmgSteel.val(1)));
      bills.add(
         new HTBill(c, var6)
            .name("Sinew")
            .hp(8)
            .sides(ESB.dmgCleaveChain.val(1), ESB.shield.val(2), ESB.dmgExert.val(4), ESB.dmgExert.val(4), ESB.shield.val(2), ESB.blank)
      );
      var6 = 3;
      bills.add(
         new HTBill(c, var6)
            .name("Barbarian")
            .hp(10)
            .sides(ESB.dmgDeath.val(10), ESB.dmgPain.val(8), ESB.dmgBloodlust.val(2), ESB.dmgBloodlust.val(2), ESB.dmgPain.val(6), ESB.dmgPain.val(4))
      );
      bills.add(
         new HTBill(c, var6)
            .name("Brawler")
            .hp(9)
            .sides(ESB.dmgSteel.val(3), ESB.dmgSelfShield.val(3), ESB.dmgRampage.val(2), ESB.dmgRampage.val(2), ESB.dmgSelfShield.val(3), ESB.blank)
      );
      bills.add(
         new HTBill(c, var6)
            .name("Curator")
            .hp(9)
            .sides(ESB.dmgEngage.val(3), ESB.dmgRampage.val(2), ESB.dmgCharged.val(1), ESB.dmgMana.val(1), ESB.dmgSteel.val(1), ESB.dmgEra.val(1))
      );
      bills.add(new HTBill(c, var6).name("Leader").hp(9).sides(ESB.recharge, ESB.dmgDuplicate.val(2), ESB.dmg.val(3), ESB.dmg.val(3), ESB.shieldSmith.val(2)));
      bills.add(
         new HTBill(c, var6).name("Veteran").hp(11).sides(ESB.dmg.val(4), ESB.dmg.val(4), ESB.dmg.val(3), ESB.dmg.val(3), ESB.shield.val(3), ESB.shield.val(3))
      );
      bills.add(
         new HTBill(c, var6).name("Bash").hp(10).sides(ESB.dmgExert.val(7), ESB.dmgSteel.val(2), ESB.dmgHeavy.val(5), ESB.dmgHeavy.val(5), ESB.stun, ESB.blank)
      );
      bills.add(new HTBill(c, var6).name("Eccentric").hp(9).sides(ESB.blank, ESB.blank, ESB.dmg.val(4), ESB.blank, ESB.dmg.val(4), ESB.dmgDescend.val(4)));
      bills.add(
         new HTBill(c, var6)
            .name("Captain")
            .hp(10)
            .sides(ESB.dmgFocus.val(3), ESB.dmgCleaveChain.val(1), ESB.shield.val(4), ESB.shield.val(4), ESB.dmgCleaveChain.val(1))
            .tactic(
               new Tactic(
                  "Formation",
                  new TacticCost(
                     TacticCostType.basicSword,
                     TacticCostType.basicSword,
                     TacticCostType.basicSword,
                     TacticCostType.basicShield,
                     TacticCostType.basicShield,
                     TacticCostType.basicShield
                  ),
                  new EffBill().group().damage(2).visual(VisualEffectType.MultiBlade).bonusUntargeted(new EffBill().group().shield(2))
               )
            )
      );
      bills.add(
         new HTBill(c, var6)
            .name("Wanderer")
            .hp(10)
            .sides(ESB.dmgDefy.val(1), ESB.shieldCopycat.val(2), ESB.dmgEra.val(3), ESB.dmgEra.val(3), ESB.shieldCopycat.val(2))
      );
      List<HeroType> result = new ArrayList<>();

      for (HTBill htBill : bills) {
         result.add(htBill.bEntType());
      }

      return result;
   }
}
