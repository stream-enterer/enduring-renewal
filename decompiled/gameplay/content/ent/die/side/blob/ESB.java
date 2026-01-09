package com.tann.dice.gameplay.content.ent.die.side.blob;

import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.EnSiBi;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.effect.eff.VisualEffectType;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.TargetingRestriction;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.trigger.personal.Dodge;
import com.tann.dice.gameplay.trigger.personal.Stunned;
import com.tann.dice.gameplay.trigger.personal.Undying;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ESB {
   public static final EntSide blank = new EnSiBi().image("blank/basic").effect(new EffBill().basic().nothing()).noVal();
   public static final EntSide blankBug = new EnSiBi()
      .image("blank/bug")
      .effect(new EffBill().basic().nothing().overrideDescription("blank [purple](bugged)"))
      .noVal();
   public static final EntSide blankUnset = new EnSiBi()
      .image("blank/unset")
      .effect(new EffBill().nothing().overrideDescription("blank [pink](unset)"))
      .noVal();
   public static final EntSide blankPetrified = new EnSiBi()
      .image("blank/petrified")
      .effect(new EffBill().nothing().overrideDescription("blank [yellow](petrified)"))
      .noVal();
   public static final EntSide blankSingleUsed = new EnSiBi()
      .image("blank/wand")
      .effect(new EffBill().nothing().overrideDescription("blank [orange](used)"))
      .noVal();
   public static final EntSide blankItem = new EnSiBi().image("blank/item").effect(new EffBill().nothing()).noVal();
   public static final EntSide blankCurse = new EnSiBi().image("blank/curse").effect(new EffBill().nothing()).noVal();
   public static final EntSide blankStasis = new EnSiBi().image("blank/stasis").effect(new EffBill().nothing().keywords(Keyword.stasis)).noVal();
   public static final EntSide blankStuck = new EnSiBi().image("blank/sticky").effect(new EffBill().nothing().keywords(Keyword.sticky)).noVal();
   public static final EntSide blankExerted = new EnSiBi()
      .image("blank/exerted")
      .effect(new EffBill().nothing().overrideDescription("blank [purple](exerted)[cu]"))
      .noVal();
   public static final EntSide blankFumble = new EnSiBi()
      .image("blank/fumble")
      .effect(new EffBill().nothing().overrideDescription("blank [grey](fumbled)[cu]"))
      .noVal();
   public static final EnSiBi backstab = new EnSiBi()
      .image("item/backstab")
      .effect(new EffBill().damage(1).friendly().keywords(Keyword.mandatory, Keyword.generous, Keyword.stasis).visual(VisualEffectType.Sword));
   public static final EnSiBi dmgSelfCantrip = new EnSiBi()
      .image("dmgSelfCantrip")
      .effect(new EffBill().damage(1).self().keywords(Keyword.cantrip).visual(VisualEffectType.Flame));
   public static final EnSiBi deathCantrip = new EnSiBi()
      .image("pinkSkull")
      .effect(new EffBill().self().kill().keywords(Keyword.cantrip).visual(VisualEffectType.Flame));
   public static final EnSiBi dmgSelfMandatory = new EnSiBi()
      .image("dmgSelfMandatory")
      .effect(new EffBill().damage(1).self().keywords(Keyword.mandatory).visual(VisualEffectType.Flame));
   public static final EnSiBi dmg = new EnSiBi().image("sword").effect(new EffBill().damage(1).basic().visual(VisualEffectType.Sword));
   public static final EnSiBi damageGrowth = new EnSiBi()
      .image("dmgGrowth")
      .effect(new EffBill().damage(1).keywords(Keyword.growth).visual(VisualEffectType.Sword));
   public static final EnSiBi dmgCritical = new EnSiBi()
      .image("dmgCritical")
      .effect(new EffBill().damage(1).keywords(Keyword.critical).visual(VisualEffectType.Slice));
   public static final EnSiBi dmgEngage = new EnSiBi()
      .image("dmgEngage")
      .effect(new EffBill().damage(1).keywords(Keyword.engage).visual(VisualEffectType.Spear));
   public static final EnSiBi dmgMana = new EnSiBi()
      .image("swordMagic")
      .effect(new EffBill().damage(1).visual(VisualEffectType.Sword).keywords(Keyword.manaGain));
   public static final EnSiBi dmgPain = new EnSiBi().image("dmgPain").effect(new EffBill().damage(1).visual(VisualEffectType.Sword).keywords(Keyword.pain));
   public static final EnSiBi dmgDeathwish = new EnSiBi()
      .image("swordDeathwish")
      .effect(new EffBill().damage(1).visual(VisualEffectType.Sword).keywords(Keyword.deathwish));
   public static final EnSiBi dmgDeath = new EnSiBi().image("dmgDeath").effect(new EffBill().damage(1).visual(VisualEffectType.Sword).keywords(Keyword.death));
   public static final EnSiBi dmgSerrated = new EnSiBi()
      .image("dmgSerrated")
      .effect(new EffBill().damage(1).visual(VisualEffectType.Sword).keywords(Keyword.serrated));
   public static final EnSiBi dmgExert = new EnSiBi()
      .image("swordExert")
      .effect(new EffBill().damage(1).visual(VisualEffectType.Sword).keywords(Keyword.exert));
   public static final EnSiBi dmgDouble = new EnSiBi()
      .image("dmgDouble")
      .effect(new EffBill().damage(1).visual(VisualEffectType.Sword).keywords(Keyword.doubleUse));
   public static final EnSiBi dmgQuad = new EnSiBi()
      .image("swordQuad")
      .effect(new EffBill().damage(1).visual(VisualEffectType.Sword).keywords(Keyword.quadUse));
   public static final EnSiBi dmgBloodlust = new EnSiBi()
      .image("dmgBloodlust")
      .effect(new EffBill().damage(1).keywords(Keyword.bloodlust).visual(VisualEffectType.Slice));
   public static final EnSiBi dmgCopycat = new EnSiBi()
      .image("dmgCopycat")
      .effect(new EffBill().damage(1).keywords(Keyword.copycat).visual(VisualEffectType.Sword));
   public static final EnSiBi dmgPristine = new EnSiBi()
      .image("swordPristine")
      .effect(new EffBill().damage(1).keywords(Keyword.pristine).visual(VisualEffectType.Sword));
   public static final EnSiBi dmgGuilt = new EnSiBi().image("dmgGuilt").effect(new EffBill().damage(1).keywords(Keyword.guilt).visual(VisualEffectType.Sword));
   public static final EnSiBi dmgCleaveTrio = new EnSiBi()
      .image("dmgTrio")
      .effect(new EffBill().damage(1).keywords(Keyword.cleave, Keyword.trio).visual(VisualEffectType.Slice));
   public static final EnSiBi dmgDefy = new EnSiBi()
      .image("dmgLucky")
      .effect(new EffBill().damage(1).keywords(Keyword.defy).visual(VisualEffectType.ZombiePunch));
   public static final EnSiBi dmgCruel = new EnSiBi().image("kriss").effect(new EffBill().damage(1).keywords(Keyword.cruel).visual(VisualEffectType.Kriss));
   public static final EnSiBi dmgShifter = new EnSiBi()
      .image("shadowDagger")
      .effect(new EffBill().damage(1).keywords(Keyword.shifter).visual(VisualEffectType.Kriss));
   public static final EnSiBi dmgFocus = new EnSiBi()
      .image("swordFocus")
      .effect(new EffBill().damage(1).keywords(Keyword.focus).visual(VisualEffectType.Sword));
   public static final EnSiBi dmgInspire = new EnSiBi()
      .image("swordInspire")
      .effect(new EffBill().damage(1).keywords(Keyword.inspired).visual(VisualEffectType.Sword));
   public static final EnSiBi dmgAll = new EnSiBi().image("swordAll").effect(new EffBill().damage(1).group().visual(VisualEffectType.Slice));
   public static final EnSiBi dmgCleave = new EnSiBi()
      .image("swordCleave")
      .effect(new EffBill().damage(1).keywords(Keyword.cleave).visual(VisualEffectType.Fork));
   public static final EnSiBi dmgDescend = new EnSiBi()
      .image("swordDescend")
      .effect(new EffBill().damage(1).keywords(Keyword.descend).visual(VisualEffectType.Fork));
   public static final EnSiBi dmgCleaveChain = new EnSiBi()
      .image("dmgCleaveChain")
      .effect(new EffBill().damage(1).keywords(Keyword.cleave, Keyword.chain).visual(VisualEffectType.Fork));
   public static final EnSiBi dmgHeavy = new EnSiBi()
      .image("hammer")
      .effect(new EffBill().damage(1).keywords(Keyword.heavy).visual(VisualEffectType.HammerThwack));
   public static final EnSiBi dmgInflictSingle = new EnSiBi()
      .image("quartzSlow")
      .effect(new EffBill().damage(1).keywords(Keyword.inflictSingleUse).visual(VisualEffectType.SwordQuartz));
   public static final EnSiBi dmgSteel = new EnSiBi()
      .image("shieldBash")
      .effect(new EffBill().type(EffType.Damage, 1).keywords(Keyword.steel).visual(VisualEffectType.ShieldBash));
   public static final EnSiBi dmgCharged = new EnSiBi()
      .image("dmgCharged")
      .effect(new EffBill().type(EffType.Damage, 1).keywords(Keyword.charged).visual(VisualEffectType.Sword));
   public static final EntSide stun = new EnSiBi()
      .image("stun")
      .effect(new EffBill().buff(new Buff(1, new Stunned())).restrict(TargetingRestriction.LessOrEqualHpThanMe))
      .noVal();
   public static final EnSiBi dmgVuln = new EnSiBi()
      .image("swordVulnerable")
      .effect(new EffBill().damage(1).keywords(Keyword.vulnerable).visual(VisualEffectType.Sword));
   public static final EnSiBi dmgEra = new EnSiBi().image("dmgEra").effect(new EffBill().damage(1).keywords(Keyword.era).visual(VisualEffectType.Sword));
   public static final EnSiBi arrow = new EnSiBi().image("arrow").effect(new EffBill().damage(1).keywords(Keyword.ranged).visual(VisualEffectType.Arrow));
   public static final EnSiBi arrowPoison = new EnSiBi()
      .image("arrowPoison")
      .effect(new EffBill().damage(1).keywords(Keyword.ranged, Keyword.poison).visual(VisualEffectType.Arrow));
   public static final EnSiBi arrowDuplicate = new EnSiBi()
      .image("arrowDuplicate")
      .effect(new EffBill().damage(1).keywords(Keyword.ranged, Keyword.duplicate).visual(VisualEffectType.Arrow));
   public static final EnSiBi arrowCleave = new EnSiBi()
      .image("arrowCleave")
      .effect(new EffBill().damage(1).keywords(Keyword.ranged, Keyword.cleave).visual(VisualEffectType.Arrow));
   public static final EnSiBi arrowCopycat = new EnSiBi()
      .image("arrowCopycat")
      .effect(new EffBill().damage(1).keywords(Keyword.ranged, Keyword.copycat).visual(VisualEffectType.Arrow));
   public static final EnSiBi dmgSelfShield = new EnSiBi()
      .image("swordShield")
      .effect(new EffBill().damage(1).keywords(Keyword.selfShield).visual(VisualEffectType.Sword));
   public static final EnSiBi dmgSelfHeal = new EnSiBi()
      .image("swordHeal")
      .effect(new EffBill().damage(1).keywords(Keyword.selfHeal).visual(VisualEffectType.Sword));
   public static final EnSiBi dmgPoison = new EnSiBi()
      .image("dmgPoison")
      .effect(new EffBill().damage(1).keywords(Keyword.poison).visual(VisualEffectType.Sword));
   public static final EnSiBi poisonAll = new EnSiBi()
      .image("plague")
      .effect(new EffBill().damage(1).targetType(TargetingType.ALL).keywords(Keyword.poison).visual(VisualEffectType.PerlinPoison));
   public static final EnSiBi dosePoison = new EnSiBi()
      .image("dmgPoisonDose")
      .effect(new EffBill().visual(VisualEffectType.PerlinPoison).damage(1).keywords(Keyword.poison, Keyword.plague));
   public static final EnSiBi shield = new EnSiBi().image("shield").effect(new EffBill().shield(1).basic());
   public static final EnSiBi shieldFlesh = new EnSiBi().image("shieldFlesh").effect(new EffBill().shield(1).keywords(Keyword.flesh));
   public static final EnSiBi shieldGrowth = new EnSiBi().image("shieldGrowth").effect(new EffBill().shield(1).keywords(Keyword.growth));
   public static final EnSiBi shieldEngage = new EnSiBi().image("shieldPrecise").effect(new EffBill().shield(1).keywords(Keyword.engage));
   public static final EnSiBi shieldEnduringDeath = new EnSiBi()
      .image("shieldEnduringDeath")
      .effect(new EffBill().shield(1).keywords(Keyword.death, Keyword.enduring));
   public static final EnSiBi shieldMana = new EnSiBi().image("shieldMagic").effect(new EffBill().shield(1).keywords(Keyword.manaGain));
   public static final EnSiBi shieldDouble = new EnSiBi().image("shieldDoubleUse").effect(new EffBill().shield(1).keywords(Keyword.doubleUse));
   public static final EnSiBi shieldSteel = new EnSiBi().image("shieldSteel").effect(new EffBill().shield(1).keywords(Keyword.steel));
   public static final EnSiBi shieldRescue = new EnSiBi().image("shieldRescue").effect(new EffBill().shield(1).keywords(Keyword.rescue));
   public static final EnSiBi shieldPristine = new EnSiBi().image("shieldPristine").effect(new EffBill().shield(1).keywords(Keyword.pristine));
   public static final EnSiBi shieldCantrip = new EnSiBi().image("shieldCantrip").effect(new EffBill().shield(1).keywords(Keyword.cantrip));
   public static final EnSiBi shieldCopycat = new EnSiBi().image("shieldCopycat").effect(new EffBill().shield(1).keywords(Keyword.copycat));
   public static final EnSiBi shieldFocus = new EnSiBi().image("shieldFocus").effect(new EffBill().shield(1).keywords(Keyword.focus));
   public static final EnSiBi shieldCleave = new EnSiBi().image("shieldPlusAdjacent").effect(new EffBill().shield(1).keywords(Keyword.cleave));
   public static final EnSiBi shieldCharged = new EnSiBi().image("shieldCharged").effect(new EffBill().shield(1).keywords(Keyword.charged));
   public static final EnSiBi shieldCleanse = new EnSiBi().image("shieldCure").effect(new EffBill().shield(1).keywords(Keyword.cleanse));
   public static final EnSiBi shieldAll = new EnSiBi().image("wardingChord").effect(new EffBill().shield(1).group());
   public static final EnSiBi flute = new EnSiBi().image("flute").effect(new EffBill().shield(1).group().keywords(Keyword.cantrip));
   public static final EnSiBi healShield = new EnSiBi().image("shieldHeart").effect(new EffBill().healAndShield(1).visual(VisualEffectType.HealBasic));
   public static final EnSiBi shieldSmith = new EnSiBi().image("smith").effect(new EffBill().shield(1).keywords(Keyword.smith));
   public static final EnSiBi mana = new EnSiBi().image("mana").effect(new EffBill().basic().mana(1));
   public static final EnSiBi manaCantrip = new EnSiBi().image("manaCantrip").effect(new EffBill().mana(1).keywords(Keyword.cantrip));
   public static final EnSiBi manaCantripBoned = new EnSiBi().image("manaCantripBoned").effect(new EffBill().mana(1).keywords(Keyword.cantrip, Keyword.boned));
   public static final EnSiBi manaGrowth = new EnSiBi().image("manaGrowth").effect(new EffBill().mana(1).keywords(Keyword.growth));
   public static final EnSiBi manaDecay = new EnSiBi().image("manaDecay").effect(new EffBill().mana(1).keywords(Keyword.decay));
   public static final EnSiBi manaDeath = new EnSiBi().image("manaDeath").effect(new EffBill().mana(1).keywords(Keyword.death));
   public static final EnSiBi manaPain = new EnSiBi().image("manaPain").effect(new EffBill().mana(1).keywords(Keyword.pain));
   public static final EnSiBi manaLust = new EnSiBi().image("manaBloodlust").effect(new EffBill().mana(1).keywords(Keyword.bloodlust));
   public static final EnSiBi manaPair = new EnSiBi().image("manaPair").effect(new EffBill().mana(1).keywords(Keyword.pair));
   public static final EnSiBi manaTriple = new EnSiBi().image("manaTriple").effect(new EffBill().mana(1).keywords(Keyword.trio));
   public static final EnSiBi healShieldMana = new EnSiBi().image("healShieldMana").effect(new EffBill().healAndShield(1).keywords(Keyword.manaGain));
   public static final EnSiBi manaDouble = new EnSiBi().image("manaDouble").effect(new EffBill().doubleMana());
   public static final EnSiBi wandCharged = new EnSiBi()
      .image("wandCharged")
      .effect(new EffBill().damage(1).keywords(Keyword.charged, Keyword.singleUse).visual(VisualEffectType.Lightning));
   public static final EnSiBi wandInflictPain = new EnSiBi()
      .image("wandJinx")
      .effect(new EffBill().damage(1).keywords(Keyword.inflictPain, Keyword.singleUse).visual(VisualEffectType.Flame));
   public static final EnSiBi wandFire = new EnSiBi()
      .image("wandFire")
      .effect(new EffBill().damage(1).keywords(Keyword.cruel, Keyword.singleUse).visual(VisualEffectType.Flame));
   public static final EnSiBi wandPoison = new EnSiBi()
      .image("wandPoison")
      .effect(new EffBill().damage(1).keywords(Keyword.poison, Keyword.singleUse).visual(VisualEffectType.PerlinPoison));
   public static final EnSiBi wandSelfHeal = new EnSiBi()
      .image("wandBlood")
      .effect(new EffBill().damage(1).keywords(Keyword.singleUse, Keyword.selfHeal).visual(VisualEffectType.HealBasic));
   public static final EnSiBi wandMana = new EnSiBi().image("wandMana").effect(new EffBill().mana(1).keywords(Keyword.singleUse));
   public static final EnSiBi wandFightBonus = new EnSiBi()
      .image("wandFightBonus")
      .effect(new EffBill().friendly().shield(1).keywords(Keyword.singleUse, Keyword.permaBoost));
   public static final EnSiBi wandWeaken = new EnSiBi()
      .image("wandWeaken")
      .effect(new EffBill().damage(1).keywords(Keyword.weaken, Keyword.singleUse).visual(VisualEffectType.Frost));
   public static final EnSiBi wandFierce = new EnSiBi()
      .image("wandFierce")
      .effect(new EffBill().damage(1).keywords(Keyword.fierce, Keyword.singleUse).visual(VisualEffectType.Slice));
   public static final EnSiBi wandEcho = new EnSiBi()
      .image("wandEcho")
      .effect(new EffBill().damage(1).keywords(Keyword.echo, Keyword.singleUse).visual(VisualEffectType.Slice));
   public static final EnSiBi wandDispel = new EnSiBi()
      .image("wandDispel")
      .effect(new EffBill().damage(1).keywords(Keyword.dispel, Keyword.singleUse).visual(VisualEffectType.Slice));
   public static final EnSiBi wandInfExer = new EnSiBi()
      .image("wandResilient")
      .effect(new EffBill().damage(1).keywords(Keyword.inflictExert, Keyword.singleUse).visual(VisualEffectType.Slice));
   public static final EntSide wandStun = new EnSiBi()
      .image("wandStun")
      .effect(new EffBill().keywords(Keyword.singleUse).buff(new Buff(1, new Stunned())))
      .noVal();
   public static final EnSiBi wandChaos = new EnSiBi()
      .image("wandChaos")
      .effect(
         new EffBill()
            .damage(1)
            .visual(VisualEffectType.LightningBig)
            .keywords(Keyword.singleUse, Keyword.cleave, Keyword.engage, Keyword.selfHeal, Keyword.weaken, Keyword.vulnerable)
      );
   public static final EnSiBi scepter = new EnSiBi()
      .image("item/sceptre")
      .effect(new EffBill().damage(1).keywords(Keyword.lead).visual(VisualEffectType.Slice));
   public static final EnSiBi heal = new EnSiBi().image("heal").effect(new EffBill().heal(1).basic());
   public static final EnSiBi wandHeal = new EnSiBi().image("wandHeal").effect(new EffBill().heal(1).keywords(Keyword.singleUse));
   public static final EnSiBi healVitality = new EnSiBi().image("boon").effect(new EffBill().heal(1).keywords(Keyword.vitality));
   public static final EnSiBi healRescue = new EnSiBi().image("healRescue").effect(new EffBill().heal(1).keywords(Keyword.rescue));
   public static final EnSiBi healAll = new EnSiBi().image("healAll").effect(new EffBill().heal(1).group());
   public static final EnSiBi healBoost = new EnSiBi().image("healBuff").effect(new EffBill().heal(1).friendly().keywords(Keyword.boost));
   public static final EnSiBi healCleave = new EnSiBi().image("healCleave").effect(new EffBill().heal(1).keywords(Keyword.cleave));
   public static final EnSiBi healRegen = new EnSiBi().image("healRegen").effect(new EffBill().heal(1).keywords(Keyword.regen));
   public static final EnSiBi healCleanse = new EnSiBi().image("healUncurse").effect(new EffBill().heal(1).keywords(Keyword.cleanse));
   public static final EnSiBi healMana = new EnSiBi().image("healMagic").effect(new EffBill().heal(1).keywords(Keyword.manaGain));
   public static final EnSiBi healGroooooowth = new EnSiBi().image("healGroooooowth").effect(new EffBill().heal(1).keywords(Keyword.groooooowth));
   public static final EnSiBi healDouble = new EnSiBi().image("healDouble").effect(new EffBill().heal(1).keywords(Keyword.doubleUse));
   public static final EnSiBi stick = new EnSiBi().image("stick").effect(new EffBill().damage(1).keywords(Keyword.singleUse).visual(VisualEffectType.Slice));
   public static final EnSiBi kill = new EnSiBi().image("kill").effect(new EffBill().kill().restrict(TargetingRestriction.OrLessHp).value(1));
   public static final EntSide undying = new EnSiBi()
      .image("undying")
      .effect(new EffBill().friendly().buff(new Buff(1, new Undying())).visual(VisualEffectType.Undying))
      .noVal();
   public static final EnSiBi redirect = new EnSiBi()
      .image("taunt")
      .effect(new EffBill().redirectIncoming().visual(VisualEffectType.Taunt).value(1).keywords(Keyword.selfShield));
   public static final EnSiBi shieldRepel = new EnSiBi()
      .image("revenge")
      .effect(new EffBill().shield(1).keywords(Keyword.repel).visual(VisualEffectType.Revenge));
   public static final EnSiBi shieldCrescent = new EnSiBi()
      .image("shieldCrescent")
      .effect(new EffBill().shield(1).keywords(Keyword.repel, Keyword.rampage, Keyword.rescue).visual(VisualEffectType.Revenge));
   public static final EnSiBi shieldPain = new EnSiBi().image("shieldPain").effect(new EffBill().shield(1).keywords(Keyword.pain));
   public static final EnSiBi headshot = new EnSiBi()
      .image("headshot")
      .effect(new EffBill().kill().restrict(TargetingRestriction.OrLessHp).keywords(Keyword.ranged).visual(VisualEffectType.Arrow).value(1));
   public static final EntSide dodge = new EnSiBi().image("dodge").effect(new EffBill().self().buff(new Buff(1, new Dodge()))).noVal();
   public static final EntSide dodgeCantrip = new EnSiBi()
      .image("dodgeCantrip")
      .effect(new EffBill().keywords(Keyword.cantrip).self().buff(new Buff(1, new Dodge())))
      .noVal();
   public static final EnSiBi rerollCantrip = new EnSiBi().image("rerollCantrip").effect(new EffBill().reroll(1).keywords(Keyword.cantrip));
   public static final EnSiBi dmgCantrip = new EnSiBi()
      .image("dmgCantrip")
      .effect(new EffBill().damage(1).keywords(Keyword.cantrip).visual(VisualEffectType.Sword));
   public static final EnSiBi swordRoulette = new EnSiBi()
      .image("swordRoulette")
      .effect(new EffBill().damage(1).keywords(Keyword.sticky, Keyword.mandatory, Keyword.death).visual(VisualEffectType.Sword));
   public static final EnSiBi dmgAllRampage = new EnSiBi()
      .image("flurry")
      .effect(new EffBill().damage(1).group().keywords(Keyword.rampage, Keyword.pain).visual(VisualEffectType.Slice));
   public static final EnSiBi dmgRangedRampage = new EnSiBi()
      .image("needle")
      .effect(new EffBill().keywords(Keyword.ranged, Keyword.charged, Keyword.rampage, Keyword.pain).visual(VisualEffectType.Lightning).damage(1));
   public static final EntSide recharge = new EnSiBi().image("recharge").effect(new EffBill().recharge()).noVal();
   public static final EnSiBi dmgWeaken = new EnSiBi()
      .image("dmgWeaken")
      .effect(new EffBill().damage(1).keywords(Keyword.weaken).visual(VisualEffectType.Sword));
   public static final EnSiBi dmgDuplicate = new EnSiBi()
      .image("swordDuplicate")
      .effect(new EffBill().damage(1).keywords(Keyword.duplicate).visual(VisualEffectType.Sword));
   public static final EnSiBi shieldDuplicate = new EnSiBi().image("shieldDuplicate").effect(new EffBill().shield(1).keywords(Keyword.duplicate));
   public static final EnSiBi manaDuplicate = new EnSiBi().image("manaDuplicate").effect(new EffBill().mana(1).keywords(Keyword.duplicate));
   public static final EnSiBi rangedEngage = new EnSiBi()
      .image("rangedEngage")
      .effect(new EffBill().keywords(Keyword.ranged, Keyword.engage).damage(1).visual(VisualEffectType.Arrow));
   public static final EnSiBi resurrect = new EnSiBi().image("resurrect").effect(new EffBill().resurrect(1));
   public static final EnSiBi resurrectMana = new EnSiBi().image("resurrectMana").effect(new EffBill().resurrect(1).keywords(Keyword.manaGain));
   public static final EnSiBi dmgRampage = new EnSiBi()
      .image("dmgRampage")
      .effect(new EffBill().damage(1).keywords(Keyword.rampage).visual(VisualEffectType.Slice));
   public static final EntSide giveDoubleUse = makeAddKeywordSide(Keyword.doubleUse);
   public static final EntSide giveCantripJoke = makeAddKeywordSide(Keyword.cantrip);
   public static final EntSide giveNothing = makeAddKeywordSide(Keyword.nothing);
   public static final EntSide giveCopycat = makeAddKeywordSide(Keyword.copycat);
   public static final EntSide giveCleaveWand = makeAddKeywordSide(Keyword.cleave, Keyword.singleUse);
   public static final EntSide giveCruelDeathwish = makeAddKeywordSide(Keyword.cruel, Keyword.deathwish);
   public static final EntSide giveManaGain = makeAddKeywordSide(Keyword.manaGain);
   public static final EntSide givePoison = makeAddKeywordSide(Keyword.poison);
   public static final EntSide giveSelfShield = makeAddKeywordSide(Keyword.selfShield);
   public static final EntSide giveSelfHeal = makeAddKeywordSide(Keyword.selfHeal);
   public static final EntSide giveSelfShieldSelfHeal = makeAddKeywordSide(Keyword.selfHeal, Keyword.selfShield);
   public static final EntSide giveManaGainPain = makeAddKeywordSide(Keyword.manaGain, Keyword.pain);
   public static final EntSide giveEngage = makeAddKeywordSide(Keyword.engage);
   public static final EntSide giveCleanseSelfCleanse = makeAddKeywordSide(Keyword.cleanse, Keyword.selfCleanse);
   public static final EntSide giveGrowth = makeAddKeywordSide(Keyword.growth);
   public static final EntSide genericShield = new EnSiBi().image("special/generic/shield").effect(new EffBill().nothing()).noVal().generic();
   public static final EntSide genericSword = new EnSiBi().image("special/generic/sword").effect(new EffBill().nothing()).noVal().generic();
   public static final EntSide genericMana = new EnSiBi().image("special/generic/mana").effect(new EffBill().nothing()).noVal().generic();
   public static final EntSide genericSummon = new EnSiBi().image("special/generic/summon").effect(new EffBill().nothing()).noVal().generic();
   public static final EntSide genericHeal = new EnSiBi().image("special/generic/heal").effect(new EffBill().nothing()).noVal().generic();
   public static final EntSide redFlag = new EnSiBi()
      .image("item/redFlag")
      .effect(new EffBill().redirectIncoming().visual(VisualEffectType.Taunt).keywords(Keyword.cleave))
      .noVal();
   public static final EnSiBi burningFlail = new EnSiBi()
      .image("scythe")
      .effect(new EffBill().damage(1).targetType(TargetingType.ALL).keywords(Keyword.rampage).visual(VisualEffectType.Slice));
   public static final EnSiBi dmgFleshPain = new EnSiBi()
      .image("item/swordFleshPain")
      .effect(new EffBill().damage(1).keywords(Keyword.flesh, Keyword.pain).visual(VisualEffectType.Sword));
   public static final EnSiBi manaBomb = new EnSiBi()
      .image("item/manaBomb")
      .effect(new EffBill().damage(1).targetType(TargetingType.ALL).keywords(Keyword.charged, Keyword.manacost).visual(VisualEffectType.Slice));
   public static final EnSiBi wandOfWand = new EnSiBi()
      .size(EntSize.reg)
      .image("item/wand-of-wand")
      .effect(new EffBill().damage(1).keywords(Keyword.singleUse, Keyword.inflictSingleUse).visual(VisualEffectType.Slice));
   public static final EnSiBi demonHorn = new EnSiBi()
      .size(EntSize.reg)
      .image("item/demon-horn")
      .effect(new EffBill().heal(2).keywords(Keyword.boost, Keyword.inflictPain));
   public static final EnSiBi chargedHammer = new EnSiBi()
      .size(EntSize.reg)
      .image("item/charged-hammer")
      .effect(new EffBill().damage(1).keywords(Keyword.heavy, Keyword.charged).visual(VisualEffectType.HammerThwack));
   public static final EnSiBi infusedHerbs = new EnSiBi()
      .size(EntSize.reg)
      .image("item/infused-herbs")
      .effect(new EffBill().heal(1).keywords(Keyword.cleanse, Keyword.boost, Keyword.manacost));
   public static final EnSiBi dmgPainDiscard = new EnSiBi()
      .image("item/potion-shard")
      .effect(new EffBill().damage(1).visual(VisualEffectType.Slice).keywords(Keyword.pain, Keyword.potion));
   public static final EnSiBi potionRevive = new EnSiBi()
      .size(EntSize.reg)
      .image("item/revive-potion")
      .effect(new EffBill().resurrect(1).keywords(Keyword.potion));
   public static final EnSiBi manaPotion = new EnSiBi().size(EntSize.reg).image("item/mana-potion").effect(new EffBill().mana(1).keywords(Keyword.potion));
   public static final EnSiBi dmgEliminate = new EnSiBi()
      .image("dmgEliminate")
      .effect(new EffBill().damage(1).keywords(Keyword.eliminate).visual(VisualEffectType.Sword));
   public static final EnSiBi snakePoison = new EnSiBi()
      .image("poisonFang")
      .effect(new EffBill().damage(1).keywords(Keyword.poison).visual(VisualEffectType.Fang));
   public static final EnSiBi bite = new EnSiBi().image("wolfBite").effect(new EffBill().damage(1).visual(VisualEffectType.WolfBite));
   public static final EnSiBi slash = new EnSiBi().image("slash").effect(new EffBill().damage(1).keywords(Keyword.cleave).visual(VisualEffectType.Claw));
   public static final EnSiBi hatchDragon = new EnSiBi()
      .size(EntSize.reg)
      .image("hatch")
      .effect(new EffBill().summon("Dragon", 1).keywords(Keyword.death).visual(VisualEffectType.None));
   public static final EntSide justTargetAlly = makeJt(false, false, false).noVal();
   public static final EnSiBi justTargetAllyPips = makeJt(false, true, false);
   public static final EnSiBi justTargetAllyPipsAll = makeJt(false, true, true);
   public static final EnSiBi justTargetAllyAll = makeJt(false, false, true);
   public static final EntSide justTargetEnemy = makeJt(true, false, false).noVal();
   public static final EnSiBi justTargetEnemyPips = makeJt(true, true, false);
   public static final EnSiBi justTargetEnemyPipsAll = makeJt(true, true, true);
   public static final EnSiBi justTargetEnemyAll = makeJt(true, false, true);
   public static final EnSiBi justTargetAllPips = makeJt(null, true, false);
   public static final EnSiBi justTargetAllAll = makeJt(null, false, true);
   public static final EnSiBi justSelf = makeJt(false, false, null);
   public static final EnSiBi justSelfPips = makeJt(false, true, null);

   private static EnSiBi makeJt(Boolean enemy, boolean pips, Boolean all) {
      String en = all == null ? "Self" : (enemy == null ? "Any" : (enemy ? "Enemy" : "Ally"));
      EffBill eb = new EffBill().type(EffType.JustTarget);
      if (pips) {
         en = en + "Pips";
         eb.value(1);
      }

      if (enemy == null) {
         eb.targetType(TargetingType.ALL);
      } else {
         if (enemy) {
            eb.visual(VisualEffectType.Slice);
         } else {
            eb.friendly();
         }

         if (all == null) {
            eb.targetType(TargetingType.Self);
         } else if (all) {
            eb.targetType(TargetingType.Group);
            en = en + "All";
         }
      }

      if (Boolean.TRUE.equals(enemy) && Boolean.FALSE.equals(all)) {
         eb.visual(VisualEffectType.Beam);
      }

      EnSiBi esb = new EnSiBi().image("special/target/justTarget" + en).effect(eb);
      if (pips) {
         esb.val(1);
      } else {
         esb.noVal();
      }

      return esb;
   }

   private static EntSide makeAddKeywordSide(boolean friend, Keyword... keywordInput) {
      List<Keyword> keywordList = new ArrayList<>(Arrays.asList(keywordInput));
      Collections.sort(keywordList, new Comparator<Keyword>() {
         public int compare(Keyword o1, Keyword o2) {
            return o1.getName().compareTo(o2.getName());
         }
      });
      String imgPath = "special/addKeyword/";

      for (Keyword k : keywordList) {
         imgPath = imgPath + k.name().toLowerCase();
      }

      EffBill eb = new EffBill();
      if (friend) {
         eb.friendly();
      }

      eb.specialAddKeyword(keywordList.toArray(new Keyword[0]));
      switch (keywordInput[0]) {
         case selfHeal:
            eb.visual(VisualEffectType.BoostHeal);
            break;
         case selfShield:
            eb.visual(VisualEffectType.BoostSmith);
      }

      return new EnSiBi().image(imgPath).effect(eb).noVal();
   }

   private static EntSide makeAddKeywordSide(Keyword... keywordInput) {
      return makeAddKeywordSide(true, keywordInput);
   }

   public static List<Object> makeAllSidesReg() {
      return Arrays.asList(
         blank,
         blankUnset,
         blankPetrified,
         blankSingleUsed,
         blankItem,
         blankCurse,
         blankStasis,
         blankStuck,
         blankExerted,
         blankFumble,
         giveCleanseSelfCleanse,
         backstab,
         dmgSelfCantrip,
         deathCantrip,
         dmgSelfMandatory,
         dmg,
         damageGrowth,
         dmgEngage,
         dmgMana,
         dmgPain,
         dmgDeathwish,
         dmgDeath,
         dmgSerrated,
         dmgExert,
         dmgDouble,
         dmgQuad,
         dmgBloodlust,
         dmgCopycat,
         dmgPristine,
         dmgGuilt,
         dmgCruel,
         dmgShifter,
         dmgFocus,
         dmgInspire,
         dmgAll,
         resurrectMana,
         dmgCleave,
         dmgDescend,
         dmgCleaveChain,
         dmgHeavy,
         dmgInflictSingle,
         dmgSteel,
         dmgCharged,
         stun,
         dmgVuln,
         dmgEra,
         arrow,
         arrowPoison,
         arrowDuplicate,
         arrowCleave,
         arrowCopycat,
         dmgSelfShield,
         dmgSelfHeal,
         dmgPoison,
         poisonAll,
         dosePoison,
         shield,
         shieldFlesh,
         shieldGrowth,
         shieldEngage,
         shieldEnduringDeath,
         shieldMana,
         shieldDouble,
         shieldSteel,
         shieldRescue,
         shieldPristine,
         shieldCantrip,
         shieldCopycat,
         shieldFocus,
         shieldCleave,
         shieldCharged,
         shieldCleanse,
         shieldAll,
         flute,
         healShield,
         shieldSmith,
         mana,
         manaCantrip,
         manaCantripBoned,
         manaGrowth,
         manaDecay,
         manaDeath,
         manaPain,
         manaLust,
         manaPair,
         manaTriple,
         healShieldMana,
         manaDouble,
         wandCharged,
         wandInflictPain,
         wandFire,
         wandPoison,
         wandSelfHeal,
         wandMana,
         wandFightBonus,
         wandWeaken,
         wandFierce,
         wandEcho,
         wandDispel,
         wandInfExer,
         wandStun,
         wandChaos,
         scepter,
         heal,
         wandHeal,
         healVitality,
         healRescue,
         healAll,
         healBoost,
         healCleave,
         healRegen,
         healCleanse,
         healMana,
         healGroooooowth,
         healDouble,
         stick,
         kill,
         undying,
         redirect,
         shieldRepel,
         shieldCrescent,
         shieldPain,
         headshot,
         dodge,
         dodgeCantrip,
         rerollCantrip,
         dmgCantrip,
         swordRoulette,
         dmgAllRampage,
         dmgRangedRampage,
         recharge,
         dmgWeaken,
         dmgDuplicate,
         shieldDuplicate,
         manaDuplicate,
         rangedEngage,
         resurrect,
         dmgRampage,
         giveDoubleUse,
         giveCantripJoke,
         giveNothing,
         giveCopycat,
         giveCleaveWand,
         giveCruelDeathwish,
         giveManaGain,
         givePoison,
         giveSelfShield,
         giveSelfHeal,
         giveSelfShieldSelfHeal,
         giveManaGainPain,
         giveEngage,
         giveGrowth,
         genericShield,
         genericSword,
         genericMana,
         genericSummon,
         genericHeal,
         redFlag,
         burningFlail,
         dmgFleshPain,
         manaBomb,
         wandOfWand,
         demonHorn,
         chargedHammer,
         infusedHerbs,
         dmgPainDiscard,
         potionRevive,
         manaPotion,
         dmgEliminate,
         snakePoison,
         bite,
         slash,
         hatchDragon,
         dmgCleaveTrio,
         dmgDefy,
         dmgCritical,
         justTargetAlly,
         justTargetAllyPips,
         justTargetAllyPipsAll,
         justTargetAllyAll,
         justTargetEnemy,
         justTargetEnemyPips,
         justTargetEnemyPipsAll,
         justTargetEnemyAll,
         justTargetAllPips,
         justTargetAllAll,
         justSelf,
         justSelfPips
      );
   }
}
