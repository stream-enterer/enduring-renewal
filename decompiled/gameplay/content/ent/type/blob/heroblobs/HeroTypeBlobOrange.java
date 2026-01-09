package com.tann.dice.gameplay.content.ent.type.blob.heroblobs;

import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.bill.HTBill;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.VisualEffectType;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellBill;
import com.tann.dice.gameplay.fightLog.event.entState.ChatStateEvent;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.TypeCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.FlatBonus;
import com.tann.dice.gameplay.trigger.personal.chat.KillSpecificMonster;
import com.tann.dice.gameplay.trigger.personal.chatty.DieStopped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HeroTypeBlobOrange {
   public static final int LUDUS_HP = 7;

   public static List<HeroType> makeDesigned() {
      List<HTBill> bills = new ArrayList<>();
      HeroCol col = HeroCol.orange;
      int lv = 1;
      bills.add(new HTBill(col, lv).name("Thief").hp(4).sides(ESB.arrow.val(2), ESB.dmg.val(2), ESB.blank, ESB.blank, ESB.dmg.val(2), ESB.blank));
      bills.add(new HTBill(col, lv).name("Scoundrel").hp(7).sides(ESB.dmgAll.val(1), ESB.dmgVuln.val(1)));
      bills.add(
         new HTBill(col, lv)
            .name("Lost")
            .hp(3)
            .sides(ESB.arrowPoison.val(1), ESB.dmgCruel.val(2), ESB.dmgCruel.val(1), ESB.dmgCruel.val(1), ESB.dodge, ESB.blank)
      );
      bills.add(
         new HTBill(col, lv).name("Dabble").hp(6).sides(ESB.heal.val(5), ESB.shield.val(2), ESB.mana.val(1), ESB.mana.val(1), ESB.dmg.val(2), ESB.dmg.val(1))
      );
      bills.add(
         new HTBill(col, lv)
            .name("Clumsy")
            .hp(6)
            .sides(
               ESB.dmgCantrip.val(1), ESB.dmgSelfCantrip.val(1), ESB.dmgCleave.val(1), ESB.dmgCleave.val(1), ESB.dmgCantrip.val(1), ESB.dmgSelfCantrip.val(1)
            )
      );
      int var6 = 2;
      bills.add(
         new HTBill(col, var6)
            .name("Dabbler")
            .hp(8)
            .sides(ESB.dmg.val(3), ESB.dmg.val(2), ESB.mana.val(2), ESB.heal.val(4), ESB.shield.val(2), ESB.shield.val(2))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Gambler")
            .hp(7)
            .sides(ESB.dmg.val(5), ESB.dmg.val(3), ESB.blank, ESB.blank, ESB.dmg.val(1))
            .trait(new Trait(new DieStopped(false, null, EffType.Damage, true, ChatStateEvent.GamblerCheer), false))
            .trait(new Trait(new DieStopped(false, 0, EffType.Blank, null, ChatStateEvent.GamblerBoo), false))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Ranger")
            .hp(7)
            .sides(ESB.rangedEngage.val(2), ESB.arrowCleave.val(1), ESB.arrowCleave.val(1), ESB.arrowCleave.val(1), ESB.arrow.val(2), ESB.blank)
      );
      bills.add(
         new HTBill(col, var6)
            .name("Rogue")
            .hp(7)
            .sides(ESB.dmgCruel.val(2), ESB.dmgCantrip.val(1), ESB.dmgPoison.val(1), ESB.dmgPoison.val(1), ESB.dmgCantrip.val(1), ESB.dodge)
      );
      bills.add(new HTBill(col, var6).name("Trapper").hp(8).sides(ESB.headshot.val(4), ESB.dodge, ESB.dmgVuln.val(1), ESB.dmgVuln.val(1), ESB.dodge));
      bills.add(
         new HTBill(col, var6)
            .name("Spellblade")
            .hp(7)
            .sides(ESB.dmgCopycat.val(1), ESB.dmgPoison.val(1), ESB.dmgMana.val(1), ESB.dmgMana.val(1), ESB.mana.val(1), ESB.mana.val(1))
            .spell(
               new SpellBill()
                  .title("imbue")
                  .cost(1)
                  .eff(
                     new EffBill()
                        .visual(VisualEffectType.BoostSmith)
                        .friendly()
                        .keywords(Keyword.singleCast)
                        .buff(new Buff(1, new AffectSides(new TypeCondition(EffType.Damage), new FlatBonus(1))))
                  )
                  .power(1.2F)
            )
      );
      bills.add(
         new HTBill(col, var6)
            .name("Ninja")
            .hp(6)
            .sides(ESB.dmgDouble.val(2), ESB.dmgCopycat.val(1), ESB.dmgCopycat.val(1), ESB.dmgCopycat.val(1), ESB.dodge, ESB.blank)
      );
      bills.add(
         new HTBill(col, var6)
            .name("Juggler")
            .hp(7)
            .sides(ESB.dmgCantrip.val(2), ESB.dmgCantrip.val(2), ESB.dmgCantrip.val(1), ESB.dmgCantrip.val(1), ESB.dmgSelfCantrip.val(1), ESB.blank)
      );
      bills.addAll(Arrays.asList());
      var6 = 3;
      bills.add(
         new HTBill(col, var6)
            .name("Ludus")
            .hp(7)
            .sides(ESB.dmg.val(6), ESB.dmg.val(5), ESB.dmg.val(4), ESB.dmg.val(3), ESB.dmg.val(1), ESB.dmg.val(2))
            .trait(new Trait(new DieStopped(false, null, null, true, ChatStateEvent.LudusCheer), false))
            .trait(new Trait(new KillSpecificMonster("sudul", ChatStateEvent.Brother), false))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Assassin")
            .hp(9)
            .sides(ESB.dmgCruel.val(4), ESB.dmgPoison.val(2), ESB.rangedEngage.val(2), ESB.rangedEngage.val(2), ESB.dodge)
      );
      bills.add(
         new HTBill(col, var6)
            .name("Dancer")
            .hp(8)
            .sides(ESB.dmgAllRampage.val(1), ESB.rerollCantrip.val(1), ESB.dmgCantrip.val(1), ESB.dmgCantrip.val(1), ESB.dmg.val(4), ESB.dodge)
      );
      bills.add(
         new HTBill(col, var6).name("Fencer").hp(9).sides(ESB.dmgPristine.val(3), ESB.dmgAll.val(1), ESB.dmgDouble.val(2), ESB.dmgDouble.val(2), ESB.dodge)
      );
      bills.add(
         new HTBill(col, var6)
            .name("Sharpshot")
            .hp(8)
            .sides(ESB.headshot.val(6), ESB.arrow.val(3), ESB.arrow.val(3), ESB.arrow.val(3), ESB.arrowCopycat.val(2), ESB.blank)
      );
      bills.add(new HTBill(col, var6).name("Venom").hp(8).sides(ESB.dosePoison.val(1), ESB.healCleanse.val(3), ESB.arrowPoison.val(2), ESB.arrowPoison.val(2)));
      bills.add(
         new HTBill(col, var6)
            .name("Roulette")
            .hp(8)
            .sides(ESB.swordRoulette.val(7), ESB.dmg.val(5), ESB.dmgCantrip.val(2), ESB.dmgCantrip.val(2), ESB.dmgCleave.val(2), ESB.dmgCleave.val(2))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Dabblest")
            .hp(9)
            .sides(ESB.mana.val(3), ESB.dmg.val(4), ESB.dmg.val(3), ESB.dmg.val(3), ESB.shield.val(5), ESB.heal.val(5))
      );
      bills.add(
         new HTBill(col, var6)
            .name("Agent")
            .hp(8)
            .sides(ESB.wandInflictPain.val(1), ESB.dmgQuad.val(1), ESB.dmgShifter.val(3), ESB.dmgShifter.val(3), ESB.dmgQuad.val(1), ESB.blank)
      );
      List<HeroType> result = new ArrayList<>();

      for (HTBill htBill : bills) {
         result.add(htBill.bEntType());
      }

      return result;
   }
}
