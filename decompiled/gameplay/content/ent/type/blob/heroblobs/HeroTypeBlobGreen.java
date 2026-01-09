package com.tann.dice.gameplay.content.ent.type.blob.heroblobs;

import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.bill.HTBill;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.VisualEffectType;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ParamCondition;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.Tactic;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.TacticCost;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.TacticCostType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.AndChoosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.RandomTieredChoosable;
import com.tann.dice.gameplay.trigger.personal.ForeverYoung;
import com.tann.dice.gameplay.trigger.personal.SkipLateStart;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.SimpleKeywordTrait;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWithOtherHeroSideInOrder;
import com.tann.dice.gameplay.trigger.personal.chat.ReplaceChat;
import com.tann.dice.gameplay.trigger.personal.choosable.AddChoosableOnPick;
import com.tann.dice.gameplay.trigger.personal.choosable.TwinPersonal;
import com.tann.dice.gameplay.trigger.personal.death.OnDeathEffect;
import com.tann.dice.gameplay.trigger.personal.equipRestrict.NoEquip;
import com.tann.dice.gameplay.trigger.personal.item.ItemSlots;
import com.tann.dice.gameplay.trigger.personal.item.copyItem.CopyItemsFromSingleHero;
import com.tann.dice.gameplay.trigger.personal.onHit.Spiky;
import com.tann.dice.gameplay.trigger.personal.specialPips.GhostHP;
import com.tann.dice.gameplay.trigger.personal.specialPips.pipLoc.PipLoc;
import com.tann.dice.gameplay.trigger.personal.specialPips.pipLoc.PipLocType;
import com.tann.dice.gameplay.trigger.personal.specialPips.resistive.StoneSpecialHp;
import com.tann.dice.gameplay.trigger.personal.util.CalcStats;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HeroTypeBlobGreen {
   static final int TWIN_HP = 4;

   public static List<HeroType> makeDesigned() {
      HeroCol c = HeroCol.green;
      List<HTBill> htb = new ArrayList<>(
         Arrays.asList(
            new HTBill(c, 1)
               .name("Tinder")
               .hp(5)
               .sides(ESB.dmgPain.val(2), ESB.dmgPain.val(2), ESB.dmgSelfMandatory.val(3), ESB.dmgSelfMandatory.val(3), ESB.stick.val(1))
               .trait(new OnDeathEffect(new EffBill().group().damage(1)), new CalcStats(1.07F, 1.0F)),
            new HTBill(c, 1)
               .name("Reflection")
               .hp(2)
               .sides(ESB.wandInflictPain.val(1), ESB.wandEcho.val(0), ESB.wandEcho.val(0))
               .tactic(new Tactic("Thrike", new TacticCost(TacticCostType.pips3), new EffBill().damage(3).visual(VisualEffectType.Sword).bEff())),
            new HTBill(c, 1)
               .name("Housecat")
               .hp(3)
               .sides(ESB.bite.val(3), ESB.slash.val(1), ESB.slash.val(1))
               .trait(new ForeverYoung(), new CalcStats(0.0F, 0.0F))
               .trait(
                  new ReplaceChat(
                     "Meow", "Meow", "Meow", "Mew", "Mauu", "Miaaaauu", "Mew mew mew", "purr purr", "prrr", "Yowwwwl", "chirp", "hiss!", "Oh Long Johnson"
                  )
               ),
            new HTBill(c, 1)
               .name("Primrose")
               .hp(5)
               .sides(ESB.damageGrowth.val(2), ESB.healGroooooowth.val(1), ESB.shieldGrowth.val(1), ESB.shieldGrowth.val(1), ESB.giveGrowth)
               .tactic(
                  new Tactic(
                     "Leaf",
                     new TacticCost(TacticCostType.keyword, TacticCostType.blank),
                     new EffBill().friendly().justTarget().value(1).keywords(Keyword.boost)
                  )
               ),
            new HTBill(c, 1)
               .name("Spade")
               .hp(6)
               .sides(ESB.resurrect.val(1), ESB.resurrect.val(1), ESB.resurrect.val(1), ESB.resurrect.val(1), ESB.resurrect.val(1), ESB.resurrect.val(1))
               .tactic(new Tactic("Mulch", new TacticCost(TacticCostType.wild), new EffBill().heal(2).bEff(), 1.3F)),
            new HTBill(c, 1)
               .name("Pockets")
               .hp(4)
               .sides(ESB.wandFierce.val(2), ESB.wandInfExer.val(1), ESB.stick.val(1), ESB.wandHeal.val(1), ESB.wandSelfHeal.val(1))
               .trait(
                  new AddChoosableOnPick(
                     new AndChoosable(new RandomTieredChoosable(1, 1, ChoosableType.Item), new RandomTieredChoosable(0, 2, ChoosableType.Item))
                  )
               ),
            new HTBill(c, 2)
               .name("Presence")
               .hp(7)
               .sides(ESB.giveCruelDeathwish, ESB.givePoison, ESB.giveSelfShieldSelfHeal, ESB.giveCleanseSelfCleanse, ESB.giveEngage, ESB.giveGrowth)
               .trait(new GhostHP(PipLoc.all()), new CalcStats(0.0F, 3.0F)),
            new HTBill(c, 2)
               .name("Spine")
               .hp(8)
               .sides(ESB.heal.val(4), ESB.heal.val(4), ESB.dmgCantrip.val(1), ESB.dmgCantrip.val(1))
               .trait(new Spiky(1), new CalcStats(0.5F, 0.7F)),
            new HTBill(c, 2)
               .name("Granite")
               .hp(5)
               .sides(ESB.undying, ESB.dmgHeavy.val(3), ESB.dmgHeavy.val(2), ESB.dmgHeavy.val(2))
               .trait(new StoneSpecialHp(new PipLoc(PipLocType.LeftmostN, 2)), new CalcStats(0.0F, 7.0F)),
            new HTBill(c, 2).name("Statue").hp(25).sides(ESB.blank, ESB.blank, ESB.blank, ESB.blank, ESB.blank, ESB.blank),
            new HTBill(c, 2)
               .name("Mimic")
               .hp(10)
               .sides(ESB.blankUnset, ESB.blankUnset, ESB.blankUnset, ESB.blankUnset, ESB.blankUnset, ESB.blankUnset)
               .trait(
                  new Trait(
                     new AffectSides(new ReplaceWithOtherHeroSideInOrder(4)).mimicPriority(), new CalcStats(HeroTypeUtils.getEffectTierFor(2) * 0.85F, 0.0F)
                  )
               ),
            new HTBill(c, 2)
               .name("Sphere")
               .hp(9)
               .sides(ESB.stun, ESB.rerollCantrip.val(1), ESB.blank, ESB.blank, ESB.rerollCantrip.val(1))
               .tactic(
                  new Tactic(
                     "Slam",
                     new TacticCost(TacticCostType.keyword, TacticCostType.keyword),
                     new EffBill().damage(4).keywords(Keyword.heavy).visual(VisualEffectType.Anvil)
                  )
               ),
            new HTBill(c, 2)
               .name("Coffin")
               .hp(7)
               .sides(ESB.shieldRepel.val(1), ESB.stick.val(2), ESB.stick.val(1), ESB.stick.val(1), ESB.shieldPain.val(4), ESB.shieldPain.val(4))
               .trait(new AddChoosableOnPick(new RandomTieredChoosable(0, 1, ChoosableType.Hero))),
            new HTBill(c, 2)
               .name("Alien")
               .hp(8)
               .tactic(
                  new Tactic(
                     "Devoid",
                     new TacticCost(TacticCostType.blank, TacticCostType.basicSword, TacticCostType.basicSword),
                     new EffBill()
                        .group()
                        .kill()
                        .restrict(new ParamCondition(ParamCondition.ParamConType.OrLessHp, 2, false))
                        .visual(VisualEffectType.Singularity)
                        .bEff()
                  )
               )
               .sides(
                  ESB.justTargetEnemyPips.val(2).withKeyword(Keyword.poison),
                  ESB.justTargetEnemyPips.val(3).withKeyword(Keyword.damage),
                  ESB.justTargetAllyPips.val(3).withKeyword(Keyword.shield),
                  ESB.justTargetAllyPips.val(3).withKeyword(Keyword.shield),
                  ESB.justTargetAllyPips.val(2).withKeyword(Keyword.cleanse),
                  ESB.blank
               ),
            new HTBill(c, 3)
               .name("Tainted")
               .hp(12)
               .sides(ESB.blankStasis, ESB.dodge, ESB.dmgCleave.val(3), ESB.dmgCleave.val(3), ESB.dmgPoison.val(4), ESB.shieldCleave.val(3))
               .trait(new AddChoosableOnPick(new RandomTieredChoosable(-3, 1, ChoosableType.Modifier))),
            new HTBill(c, 3)
               .name("Luggage")
               .hp(5)
               .sides(ESB.shieldCleave.val(1), ESB.shieldCleave.val(1))
               .trait(
                  new AddChoosableOnPick(
                     new AndChoosable(
                        new RandomTieredChoosable(6, 1, ChoosableType.Item),
                        new RandomTieredChoosable(7, 1, ChoosableType.Item),
                        new RandomTieredChoosable(8, 1, ChoosableType.Item)
                     )
                  )
               ),
            new HTBill(c, 3)
               .name("Vessel")
               .hp(5)
               .sides(ESB.healCleanse.val(5), ESB.shield.val(5), ESB.dmgDeath.val(5), ESB.dmgDeath.val(5))
               .trait(new AddChoosableOnPick(new RandomTieredChoosable(3, 1, ChoosableType.Modifier))),
            new HTBill(c, 3)
               .name("Jumble")
               .hp(10)
               .sides(ESB.dmg.val(4), ESB.dmg.val(3), ESB.shield.val(2), ESB.shield.val(2), ESB.heal.val(1), ESB.heal.val(1))
               .trait(new SimpleKeywordTrait(Keyword.fluctuate)),
            new HTBill(c, 3)
               .name("Dice")
               .hp(12)
               .sides(ESB.dmg.val(6), ESB.dmg.val(6), ESB.shieldCantrip.val(6), ESB.shieldCantrip.val(6), ESB.heal.val(6), ESB.heal.val(6))
               .trait(new SimpleKeywordTrait(Keyword.lucky)),
            new HTBill(c, 3)
               .name("Robot")
               .hp(14)
               .sides(
                  ESB.shieldCrescent.val(1), ESB.flute.val(1), ESB.wandOfWand.val(1), ESB.demonHorn.val(1), ESB.arrowDuplicate.val(1), ESB.shieldPain.val(1)
               )
               .trait(new ItemSlots(2), new CalcStats(0.2F, 0.0F)),
            new HTBill(c, 3)
               .name("Twin")
               .hp(4)
               .sides(HeroTypeUtils.makeTwinSides())
               .trait(
                  new Trait(
                     new TwinPersonal(
                        new HTBill(c, 3)
                           .name("Tw1n")
                           .hp(4)
                           .sides(HeroTypeUtils.makeTwinSides())
                           .trait(new Trait(new NoEquip(), false))
                           .trait(new Trait(new CopyItemsFromSingleHero("Twin"), false))
                           .bEntType()
                     )
                  )
               )
               .hiddenNoCalc(new SkipLateStart())
         )
      );
      List<HeroType> result = new ArrayList<>();

      for (HTBill htBill : htb) {
         result.add(htBill.bEntType());
      }

      return result;
   }
}
