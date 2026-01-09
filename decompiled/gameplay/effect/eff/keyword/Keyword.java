package com.tann.dice.gameplay.effect.eff.keyword;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.ConditionalBonus;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.ConditionalBonusType;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.InvertBonus;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.EnumConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.NotRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ParamCondition;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.TargetingRestriction;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.StateConditionType;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum Keyword {
   heal(Colours.red, "heal the target for " + KUtils.describeN(), null, KeywordAllowType.TARG_PIPS),
   shield(Colours.grey, "shield the target for " + KUtils.describeN(), null, KeywordAllowType.TARG_PIPS),
   damage(Colours.orange, "damage the target for " + KUtils.describeN(), null, KeywordAllowType.TARG_PIPS),
   manaGain(Colours.blue, "gain " + KUtils.describeN() + " " + Words.manaString(), null, KeywordAllowType.PIPS_ONLY),
   engage(Colours.yellow, "with full hp", StateConditionType.FullHP, false),
   pristine(Colours.light, "have full hp", StateConditionType.FullHP, true),
   cruel(Colours.orange, "on half or less hp", StateConditionType.HalfOrLessHP, false),
   deathwish(Colours.purple, "am dying " + KUtils.describeThisTurn(), StateConditionType.Dying, true),
   armoured(Colours.light, "have shields", StateConditionType.HasShields, true),
   wham(Colours.light, "with shields", StateConditionType.HasShields, false),
   moxie(Colours.yellow, "have the least hp of all", StateConditionType.LeastHP, true),
   bully(Colours.orange, "have the most hp of all", StateConditionType.MostHP, true),
   squish(Colours.yellow, "with the least hp of all", StateConditionType.LeastHP, false),
   uppercut(Colours.orange, "with the most hp of all", StateConditionType.MostHP, false),
   terminal(Colours.purple, "on 1 hp", new ParamCondition(ParamCondition.ParamConType.ExactlyHp, 1, false)),
   ego(Colours.yellow, "myself", EnumConditionalRequirement.SelfTarget),
   serrated(Colours.red, "who have gained no shields this turn", StateConditionType.GainedNoShields, false),
   reborn(Colours.yellow, "died this fight", StateConditionType.Died, true),
   century(Colours.grey, "with 100+ hp", new ParamCondition(ParamCondition.ParamConType.OrMoreHp, 100, false)),
   sixth(Colours.orange, "this is the 6th dice you use this turn", EnumConditionalRequirement.SixthDiceUsed),
   duel(Colours.blue, "those targeting me this turn", EnumConditionalRequirement.TargetTargetingMe),
   chain(Colours.pink, "this shares a keyword with the " + KUtils.describePreviousDice(), EnumConditionalRequirement.PreviousAbilitySharesKeyword),
   inspired(Colours.green, "the " + KUtils.describePreviousDice() + " had more pips", EnumConditionalRequirement.PreviousAbilityHigher),
   tall(Colours.orange, "the topmost target", EnumConditionalRequirement.Top),
   underdog(Colours.yellow, "targets with more hp than me", EnumConditionalRequirement.LessHpThanTarget),
   overdog(Colours.yellow, "targets with less hp than me", EnumConditionalRequirement.MoreHpThanTarget),
   dog(Colours.yellow, "targets with equal hp to me", EnumConditionalRequirement.SameHpAsTarget),
   hyena(Colours.yellow, "targets with whom our hp is co-prime", EnumConditionalRequirement.CoprimeWithTarget),
   patient(Colours.light, "I was not used last turn", EnumConditionalRequirement.UnusedLastTurn),
   focus(Colours.orange, "the target of the " + KUtils.describePreviousDice(), EnumConditionalRequirement.PreviousAbilitySameTarget),
   sloth(Colours.light, "targets with fewer blank sides", EnumConditionalRequirement.MoreBlanksThanTarget),
   first(Colours.light, "no dice have been used this turn", EnumConditionalRequirement.NoOtherDice),
   step(
      Colours.grey,
      "x2 if " + KUtils.descStraight(2),
      "[notranslate]" + com.tann.dice.Main.t("eg") + " 12, 65",
      new ConditionalBonus(EnumConditionalRequirement.RunOfTwo, ConditionalBonusType.Multiply, 2)
   ),
   run(
      Colours.yellow,
      "x3 if " + KUtils.descStraight(3),
      "[notranslate]" + com.tann.dice.Main.t("eg") + " 123, 654",
      new ConditionalBonus(EnumConditionalRequirement.RunOfThree, ConditionalBonusType.Multiply, 3)
   ),
   sprint(
      Colours.orange,
      "x5 if " + KUtils.descStraight(5),
      "[notranslate]" + com.tann.dice.Main.t("eg") + " 12345, 65432",
      new ConditionalBonus(EnumConditionalRequirement.RunOfFive, ConditionalBonusType.Multiply, 5)
   ),
   revDiff(Colours.red, "inverted pip delta", null, KeywordAllowType.PIPS_ONLY),
   doubDiff(Colours.yellow, "doubled pip delta", null, KeywordAllowType.PIPS_ONLY),
   pair(2),
   trio(3),
   quin(5),
   sept(7),
   rampage(Colours.purple, "can be reused if it was lethal", null, KeywordAllowType.DEATHCHECK),
   rescue(Colours.yellow, "can be reused if it saves a hero", null, KeywordAllowType.DEATHCHECK),
   guilt(Colours.orange, "if this is lethal, I die", null, KeywordAllowType.DEATHCHECK),
   evil(Colours.red, "if this saves a hero, I die", null, KeywordAllowType.DEATHCHECK),
   cantrip(
      Colours.pink,
      "activates during rolling if it lands face-up",
      "Targets chosen randomly. This one is a bit weird... Whilst rolling dice, if this side lands face-up, it gets used instantly without taking up that hero's turn. You can still reroll it and use it as normal.",
      KeywordAllowType.CANTRIP
   ),
   stasis(Colours.blue, "this side cannot change", null, KeywordAllowType.YES),
   fizz(Colours.blue, KUtils.describePipBonus("ability used this turn"), null, new ConditionalBonus(ConditionalBonusType.AbilitiesUsed)),
   skill(Colours.light, KUtils.describePipBonus("equal to my level"), null, new ConditionalBonus(ConditionalBonusType.MyTier)),
   bloodlust(
      Colours.red,
      KUtils.describePipBonus("damaged " + Words.entName(true, false, null)),
      "Pips on this side are increased by 1 for each damaged " + Words.entName(true, false, null),
      new ConditionalBonus(ConditionalBonusType.DamagedEnemies)
   ),
   defy(Colours.yellow, KUtils.describePipBonus("incoming damage"), null, new ConditionalBonus(ConditionalBonusType.IncomingDamage)),
   charged(
      Colours.blue,
      KUtils.describePipBonus("stored " + Words.manaString()),
      "Pips on this side are increased by 1 for each mana you have stored",
      new ConditionalBonus(ConditionalBonusType.CurrentMana)
   ),
   steel(
      Colours.light,
      KUtils.describePipBonus("shield I have"),
      "Pips on this side are increased by an amount equal to my current shields",
      new ConditionalBonus(ConditionalBonusType.MyShields)
   ),
   flesh(Colours.red, KUtils.describePipBonus("hp I have"), null, new ConditionalBonus(ConditionalBonusType.CurrentHP)),
   rainbow(Colours.light, KUtils.describePipBonus("keyword on this side"), null, new ConditionalBonus(ConditionalBonusType.NumKeywords)),
   hoard(Colours.orange, KUtils.describePipBonus("unequipped item"), null, new ConditionalBonus(ConditionalBonusType.BagItems)),
   plague(Colours.green, KUtils.describePipBonus("poison on all characters"), "", new ConditionalBonus(ConditionalBonusType.TotalPoison)),
   acidic(Colours.green, KUtils.describePipBonus("poison on me"), "", new ConditionalBonus(ConditionalBonusType.MyPoison)),
   vigil(Colours.light, KUtils.describePipBonus("defeated ally"), null, new ConditionalBonus(ConditionalBonusType.DeadAllies)),
   flurry(Colours.orange, KUtils.describePipBonus("time I have been used this turn"), null, new ConditionalBonus(ConditionalBonusType.TimesUsedThisTurn)),
   fashionable(
      Colours.blue,
      KUtils.describePipBonus("equal to the total tier of all items I have equipped"),
      null,
      new ConditionalBonus(ConditionalBonusType.TotalItemTier)
   ),
   equipped(Colours.grey, KUtils.describePipBonus("item I have equipped"), null, new ConditionalBonus(ConditionalBonusType.ItemsEquipped)),
   buffed(Colours.light, KUtils.describePipBonus("buff I have"), null, new ConditionalBonus(ConditionalBonusType.Buffs)),
   affected(Colours.purple, KUtils.describePipBonus("'thing' affecting me"), null, new ConditionalBonus(ConditionalBonusType.Triggers)),
   rite(Colours.green, "+1 for each unused ally, they become used", null, new ConditionalBonus(ConditionalBonusType.UnusedAllies)),
   growth(
      Colours.green,
      "gets " + KUtils.describeBeingIncreased() + " " + KUtils.describeThisFight() + " after use",
      "Every time you use this side, the pips on it increase by 1 for this fight only",
      KeywordAllowType.PIPS_ONLY
   ),
   hyperGrowth(Colours.green, "gets " + KUtils.describeN(true) + " pips " + KUtils.describeThisFight() + " after use", null, KeywordAllowType.PIPS_ONLY),
   undergrowth(
      Colours.green, "After use, the opposite side gets " + KUtils.describeBeingIncreased() + " " + KUtils.describeThisFight(), null, KeywordAllowType.NONBLANK
   ),
   groooooowth(
      Colours.green, "After use, [b]all[b] my sides get " + KUtils.describeBeingIncreased() + " " + KUtils.describeThisFight(), null, KeywordAllowType.NONBLANK
   ),
   decay(Colours.purple, "gets " + KUtils.describeBeingDecreased() + " " + KUtils.describeThisFight() + " after use", null, KeywordAllowType.PIPS_ONLY),
   era(Colours.blue, KUtils.describePipBonus("elapsed turn"), null, new ConditionalBonus(ConditionalBonusType.ElapsedTurns)),
   lead(
      Colours.yellow,
      "After use, other hero's sides that share a type with this get "
         + KUtils.describeN(true)
         + " "
         + KUtils.describeThisTurn()
         + " [grey](eg damage/heal...)",
      null,
      KeywordAllowType.PIPS_ONLY
   ),
   vulnerable(
      Colours.orange,
      "target takes " + KUtils.describeN(true) + " damage from dice & " + Words.spabKeyword(true) + " " + KUtils.describeOneTurn(),
      "The amount of extra damage taken is equal to the pips on this side.",
      KeywordAllowType.UNKIND_TARG_PIPS
   ),
   regen(
      Colours.red,
      "heal for " + KUtils.describeN() + " at the end of each turn",
      "The amount of health regenerated is equal to the pips on the side.",
      KeywordAllowType.KIND_TARG_PIPS
   ),
   poison(
      Colours.green,
      "also inflicts " + KUtils.describeN() + " unblockable damage at the end of each turn",
      "The amount of poison damage is equal to the pips on the side. The poison part of poison damage is unblockable. You can't prevent yourself from being poisoned except by using cleanse or killing the attacking monster.",
      KeywordAllowType.UNKIND_TARG_PIPS
   ),
   weaken(
      Colours.green,
      "target gets " + KUtils.describeN(false) + " to all pips " + KUtils.describeOneTurn(),
      "The amount decreased is equal to the pips on this side.",
      KeywordAllowType.UNKIND_TARG_PIPS
   ),
   boost(
      Colours.blue,
      "target gets " + KUtils.describeN(true) + " to all pips " + KUtils.describeOneTurn(),
      "The increase is equal to the pips on this side.",
      KeywordAllowType.KIND_TARG_PIPS
   ),
   smith(
      Colours.light, "target gets " + KUtils.describeN(true) + " to damage and shield sides " + KUtils.describeOneTurn(), null, KeywordAllowType.KIND_TARG_PIPS
   ),
   permaBoost(Colours.pink, "target gets " + KUtils.describeN(true) + " to all pips " + KUtils.describeThisFight(), null, KeywordAllowType.KIND_TARG_PIPS),
   petrify(
      Colours.yellow,
      "transforms " + KUtils.describeN() + " sides to stone " + KUtils.describeThisFight(),
      "Petrification order: [white][petrify-diagram]",
      KeywordAllowType.UNKIND_TARG_PIPS
   ),
   hypnotise(Colours.orange, "set target's damage sides to 0 " + KUtils.describeThisTurn(), null, KeywordAllowType.UNKIND_TARG),
   dispel(Colours.pink, "remove all traits from the target " + KUtils.describeThisFight(), "", KeywordAllowType.UNKIND_TARG),
   eliminate(
      Colours.red,
      "target must have the least hp",
      "if a monster has this, they include incoming damage in their calculations (and their targets are locked)",
      TargetingRestriction.LeastHp
   ),
   heavy(
      Colours.yellow,
      "target must have the most hp",
      "if a monster has this, they include incoming damage in their calculations (and their targets are locked)",
      TargetingRestriction.MostHealth
   ),
   generous(Colours.light, "cannot target myself", null, TargetingRestriction.NotMe),
   scared(Colours.purple, "target must have " + KUtils.describeN() + " or less hp", null, TargetingRestriction.OrLessHp),
   picky(Colours.blue, "target must have exactly " + KUtils.describeN() + " hp", null, TargetingRestriction.ExactlyValue),
   unusable(Colours.grey, "cannot be used (manually)", cantrip.getColourTaggedString() + " is still allowed", KeywordAllowType.NONBLANK),
   vitality(Colours.light, "grants the target " + KUtils.describeN(true) + " empty hp " + KUtils.describeThisFight(), null, KeywordAllowType.KIND_TARG_PIPS),
   wither(Colours.green, "grants the target " + KUtils.describeN(false) + " empty hp " + KUtils.describeThisFight(), null, KeywordAllowType.UNKIND_TARG_PIPS),
   ranged(
      Colours.light,
      "can target enemies in the back row and avoids [orange]On-hit[cu] passives",
      "Some enemies start in the back row and others can move to the back row during combat. These can only be targeted by ranged abilities. It's also useful against certain enemy passives.",
      KeywordAllowType.ENEMY_TARG
   ),
   fierce(Colours.yellow, "target flees if they have " + KUtils.describeN() + " or less hp [grey](after)", null, KeywordAllowType.UNKIND_TARG_PIPS),
   fault(Colours.blue, KUtils.describeOthersSeeingNPips("[purple]-1[cu]"), null, KeywordAllowType.PIPS_ONLY),
   plus(Colours.grey, KUtils.describeOthersSeeingNPips(KUtils.describeNP1()), null, KeywordAllowType.PIPS_ONLY),
   doubled(Colours.blue, KUtils.describeOthersSeeingNPips(KUtils.describe2N()), null, KeywordAllowType.PIPS_ONLY),
   squared(Colours.pink, KUtils.describeOthersSeeingNPips(KUtils.describeNSQ()), null, KeywordAllowType.PIPS_ONLY),
   onesie(Colours.grey, KUtils.describeOthersSeeingNPips(KUtils.describeOne()), null, KeywordAllowType.PIPS_ONLY),
   threesy(Colours.orange, KUtils.describeOthersSeeingNPips(KUtils.describeThree()), null, KeywordAllowType.PIPS_ONLY),
   zeroed(Colours.grey, KUtils.describeOthersSeeingNPips("[grey]0[cu]"), null, KeywordAllowType.PIPS_ONLY),
   treble(Colours.light, "Other keywords x2 -> x3", null, KeywordAllowType.PIPS_ONLY),
   pain(
      Colours.red,
      "I take " + KUtils.describeN() + " damage",
      "Pain damage can be blocked. Damage is resolved in an order favourable to the player always (I hope)",
      KeywordAllowType.PIPS_ONLY
   ),
   boned(Colours.light, "Summon a bones", null, KeywordAllowType.PIPS_ONLY),
   hyperBoned(Colours.light, "Summon " + KUtils.describeN() + " bones", null, KeywordAllowType.PIPS_ONLY),
   potion(Colours.purple, "I discard my topmost 'potion' item permanently", null, KeywordAllowType.NONBLANK),
   singleUse(Colours.orange, "after I use this side, replace it with a blank " + KUtils.describeThisFight(), null, KeywordAllowType.NONBLANK),
   doubleUse(Colours.orange, "can be used twice in a turn", null, KeywordAllowType.NONBLANK_TOUCHUSABLE),
   quadUse(Colours.orange, "can be used 4 times in a turn", null, KeywordAllowType.NONBLANK_TOUCHUSABLE),
   hyperUse(Colours.orange, "can be used " + KUtils.describeN() + " times in a turn", null, KeywordAllowType.PIPS_TOUCHUSABLE),
   cleave(
      Colours.light,
      "also hits both sides of the target",
      "can be used to hit " + Words.entName(false, true) + " in the back row indirectly",
      KeywordAllowType.SINGLE_TARGET
   ),
   descend(Colours.light, "also hits below the target", null, KeywordAllowType.SINGLE_TARGET),
   repel(Colours.orange, KUtils.describeN() + " damage to all " + Words.entName(false, true) + " attacking the target", null, KeywordAllowType.ALLY_TARG_PIPS),
   cleanse(
      Colours.light,
      "reduce target's negative effects by "
         + KUtils.describeN()
         + "[n][grey]("
         + Tann.commaList(Arrays.asList("poisoned", "weakened", "petrified", "inflicted"), ", ", " and ")
         + ")",
      "Cleanse can remove poisoned, weakened, petrified and inflicted. It also affects incoming negative effects.",
      KeywordAllowType.TARG_PIPS
   ),
   duplicate(
      Colours.blue,
      "copy this onto all allied sides " + KUtils.describeOneTurn(),
      "this one's a bit complicated, you'll have figure it out yourself. Bonuses are usually copied, but not for static bonuses like "
         + charged.getColourTaggedString()
         + " (or something...)",
      KeywordAllowType.NONBLANK
   ),
   selfRepel(repel, new long[0]),
   selfPetrify(petrify, new long[0]),
   selfPoison(poison, new long[0]),
   selfRegen(regen, new long[0]),
   selfCleanse(cleanse, new long[0]),
   selfVulnerable(vulnerable, new long[0]),
   selfShield(Colours.light, "shield myself for " + KUtils.describeN(), null, KeywordAllowType.PIPS_ONLY),
   selfHeal(Colours.red, "heal myself for " + KUtils.describeN(), null, KeywordAllowType.PIPS_ONLY),
   death(Colours.light, "I die", "everyone has to some day", KeywordAllowType.NONBLANK),
   exert(Colours.purple, "replace all sides with blanks until the end of next turn", null, KeywordAllowType.NONBLANK),
   mandatory(Colours.red, "must be used (if possible)", null, KeywordAllowType.NONBLANK),
   manacost(Colours.purple, "costs " + KUtils.describeN() + " mana", null, KeywordAllowType.PIPS_ONLY),
   permissive(Colours.pink, "any keyword can be added to this", null, KeywordAllowType.YES),
   sticky(Colours.purple, "cannot be rerolled", null, KeywordAllowType.YES),
   enduring(Colours.grey, KUtils.describedRemainingWhenReplaced("keywords"), null, KeywordAllowType.YES),
   dogma(Colours.yellow, "Only pips change when the side is replaced", null, KeywordAllowType.YES),
   resilient(Colours.orange, KUtils.describedRemainingWhenReplaced("pips"), null, KeywordAllowType.YES),
   spy(Colours.grey, "copy all keywords from the first enemy attack", "I don't know", KeywordAllowType.NONBLANK),
   dejavu(Colours.pink, "copy the keywords from the sides I used last turn", null, KeywordAllowType.NONBLANK),
   echo(Colours.blue, "copy the pips of the " + KUtils.describePreviousDice(), null, KeywordAllowType.PIPS_ONLY),
   copycat(Colours.light, "copy the keywords from the " + KUtils.describePreviousDice(), null, KeywordAllowType.NONBLANK),
   resonate(
      Colours.pink,
      "copy the effect of the " + KUtils.describePreviousDice() + ", retaining this side's pips and the [pink]resonate[cu] keyword",
      null,
      KeywordAllowType.YES
   ),
   share(Colours.light, "targets gain all my keywords " + KUtils.describeThisTurn() + " [grey](except share)", null, KeywordAllowType.TARGET_ONLY_NOT_SELF),
   annul(Colours.grey, "targets lose all keywords this turn", null, KeywordAllowType.TARGET_ONLY),
   possessed(Colours.purple, "targets as if used by the other side", null, KeywordAllowType.TARGET_ONLY_NOT_SELF),
   shifter(Colours.pink, "this side has a random extra keyword, changes each turn", null, KeywordAllowType.NONBLANK),
   lucky(Colours.red, "pips are randomised to between 0 and current pips, changes each turn", null, KeywordAllowType.PIPS_ONLY),
   critical(Colours.yellow, "50% chance for +1, rechecks each turn", null, KeywordAllowType.PIPS_ONLY),
   fluctuate(Colours.blue, "changes to a random side each turn, retaining keywords and pips", null, KeywordAllowType.YES),
   fumble(Colours.grey, "50% chance to be blank each turn", null, KeywordAllowType.NONBLANK),
   nothing(Colours.grey, "this keyword has no effect", null, KeywordAllowType.YES),
   tactical(Colours.orange, "counts twice for tactic costs", null, KeywordAllowType.YES),
   groupExert(exert, false),
   groupGrowth(growth, true),
   groupDecay(decay, true),
   groupSingleUse(singleUse, true),
   groupGroooooowth(groooooowth, false),
   doubleGrowth(growth, 0.0F, 0.0F),
   halveEngage(engage, new float[0]),
   halveDeathwish(deathwish, new float[0]),
   halveDuel(duel, new float[0]),
   antiEngage(engage, 1.0),
   antiPristine(pristine, 1.0),
   antiDog(dog, 1.0),
   antiDeathwish(deathwish, 1.0),
   antiPair(pair, 1.0),
   swapDeathwish(deathwish, new int[0]),
   swapCruel(cruel, new int[0]),
   swapEngage(engage, new int[0]),
   swapTerminal(terminal, new int[0]),
   minusFlesh(flesh, 1.0F),
   minusEra(era, 1.0F),
   engarged(engage, charged, KeywordCombineType.ConditionBonus),
   cruesh(cruel, flesh, KeywordCombineType.ConditionBonus),
   pristeel(pristine, steel, KeywordCombineType.ConditionBonus),
   deathlust(deathwish, bloodlust, KeywordCombineType.ConditionBonus),
   trill(trio, skill, KeywordCombineType.ConditionBonus),
   duegue(duel, plague, KeywordCombineType.ConditionBonus),
   engine(engage, pristine, KeywordCombineType.TC4X),
   underocus(underdog, focus, KeywordCombineType.TC4X),
   priswish(pristine, deathwish, KeywordCombineType.TC4X),
   paxin(pair, chain, KeywordCombineType.XOR),
   inflictSelfShield(selfShield),
   inflictBoned(boned),
   inflictExert(exert),
   inflictPain(pain),
   inflictDeath(death),
   inflictSingleUse(singleUse),
   inflictNothing(nothing),
   inflictInflictNothing(inflictNothing),
   inflictInflictDeath(inflictDeath),
   singleCast(Colours.purple, "can only be cast once each fight", null, KeywordAllowType.SPELL),
   cooldown(Colours.orange, "can only be cast once each turn", null, KeywordAllowType.SPELL),
   deplete(Colours.orange, "costs +1 mana each time it is cast", null, KeywordAllowType.SPELL),
   channel(Colours.green, "costs -1 mana each time it is cast (minimum 1)", null, KeywordAllowType.SPELL),
   spellRescue(Colours.yellow, "cost is refunded if it saves a hero", null, KeywordAllowType.SPELL),
   future(Colours.blue, "effect is delayed until the start of next turn", null, KeywordAllowType.SPELL),
   removed(Colours.light, "something has been removed, you probably shouldn't be seeing this...", null, KeywordAllowType.NO);

   private final KeywordAllowType allowType;
   private final String name;
   private final Color col;
   private final String rules;
   private final String extraRules;
   private final TextureRegion corner;
   private final TextureRegion cornerTiny;
   private final boolean flipCorner;
   private boolean doubleAct;
   private ConditionalRequirement targetingRequirement;
   private ConditionalBonus conditionalBonus;
   private Keyword inflict;
   private Keyword groupAct;
   private Keyword metaKeyword;
   private Keyword metaKeyword2;

   private Keyword(Color col, String rules, String extraRules, KeywordAllowType kat) {
      if (kat == null) {
         throw new RuntimeException("Invalid keyword kat: " + this.name());
      } else {
         this.allowType = kat;
         this.name = this.name();
         this.rules = rules;
         this.col = col;
         this.extraRules = extraRules;
         String texName = this.name().toLowerCase();
         List<AtlasRegion> regions = Tann.getRegionsStartingWith(com.tann.dice.Main.atlas_3d, "keyword/" + texName.replaceAll(" ", "-"));
         if (regions.size() == 1) {
            this.corner = (TextureRegion)regions.get(0);
            this.flipCorner = !regions.get(0).name.contains("xx");
         } else if (regions.size() == 0) {
            this.corner = KUtils.makePlaceholderCorner(this);
            this.flipCorner = true;
         } else {
            Collections.sort(regions, new Comparator<AtlasRegion>() {
               public int compare(AtlasRegion o1, AtlasRegion o2) {
                  return o1.name.length() - o2.name.length();
               }
            });
            this.corner = (TextureRegion)regions.get(0);
            this.flipCorner = !regions.get(0).name.contains("xx");
         }

         this.cornerTiny = ImageUtils.loadExt3d("keyword/small/" + TextWriter.getNameForColour(col));
      }
   }

   private Keyword(Color col, String rules, String extraRules, ConditionalBonus bonus) {
      this(col, rules, extraRules, bonus, katFromBonus(bonus));
   }

   private static KeywordAllowType katFromBonus(ConditionalBonus bonus) {
      if (bonus.requirement == EnumConditionalRequirement.SelfTarget) {
         return KeywordAllowType.ALLY_TARG_PIPS;
      } else if (bonus.requirement == EnumConditionalRequirement.TargetTargetingMe) {
         return KeywordAllowType.ENEMY_TARG_PIPS;
      } else {
         return bonus.requirement.preCalculate() ? KeywordAllowType.PIPS_ONLY : KeywordAllowType.TARG_PIPS;
      }
   }

   private Keyword(Color col, String rules, String extraRules, ConditionalBonus bonus, KeywordAllowType kat) {
      this(col, rules, extraRules, kat);
      this.conditionalBonus = bonus;
   }

   private Keyword(Keyword toInvertBonus, float sigInvertBonusAmount) {
      this(toInvertBonus.col, NotRequirement.transform(toInvertBonus.rules), null, new InvertBonus(toInvertBonus.conditionalBonus.bonusType));
      this.metaKeyword = toInvertBonus;
   }

   private Keyword(Keyword toAntiRequirement, double sigAntiRequirement) {
      this(toAntiRequirement.col, NotRequirement.transform(toAntiRequirement.rules), null, toAntiRequirement.getConditionalBonus().antiRequirement());
      this.metaKeyword = toAntiRequirement;
   }

   private Keyword(Keyword toHalve, float[] sigHalve) {
      this(
         toHalve.col,
         "[notranslate]" + com.tann.dice.Main.t(toHalve.getRules()).replaceAll("x2", "x0.5 (" + com.tann.dice.Main.t("rounded down") + ")"),
         null,
         toHalve.getConditionalBonus().halveVersion()
      );
      this.metaKeyword = toHalve;
   }

   private Keyword(Keyword toSwap, int[] sigSwap) {
      this(toSwap.col, KUtils.swapRules(toSwap), null, KUtils.getSwapBonus(toSwap), KUtils.getSwapRequirement(toSwap));
      this.metaKeyword = toSwap;
   }

   private Keyword(Keyword inflict) {
      this(inflict.col, KUtils.describeAdding(inflict), null, null, KeywordAllowType.TARGET_ONLY);
      this.inflict = inflict;
      this.metaKeyword = inflict;
   }

   private Keyword(Keyword groupAct, boolean side) {
      this(groupAct.col, KUtils.describeGroupActivate(groupAct, side), null, null, KeywordAllowType.NONBLANK);
      this.groupAct = groupAct;
      this.metaKeyword = groupAct;
   }

   private Keyword(Keyword toDouble, float siga, float sigb) {
      this(toDouble.col, "Like " + toDouble.getColourTaggedString() + " but activates twice", null, toDouble.allowType);
      this.doubleAct = true;
      this.metaKeyword = toDouble;
   }

   private Keyword(Keyword metaHitSelf, long[] hitSelfSig) {
      this(metaHitSelf.col, "Like " + metaHitSelf.getColourTaggedString() + " but hits me instead of the target", "", KeywordAllowType.PIPS_ONLY);
      this.metaKeyword = metaHitSelf;
   }

   private Keyword(Keyword a, Keyword b, KeywordCombineType kct) {
      this(a.col, KUtils.describeCombination(a, b, kct), null, KUtils.getConditionalBonus(a, b, kct));
      this.metaKeyword = a;
      this.metaKeyword2 = b;
   }

   private Keyword(int ofAKind) {
      this(
         Colours.light,
         "x" + ofAKind + " if " + KUtils.describeHavingSameValueAsLastNDice(ofAKind - 1),
         null,
         new ConditionalBonus(EnumConditionalRequirement.previousNSame(ofAKind - 1), ConditionalBonusType.Multiply, ofAKind)
      );
   }

   private Keyword(Color col, String rules, String extraRules, ConditionalRequirement targetingRequirement) {
      this(col, rules, extraRules, null, KUtils.getKATFromTargetingRequirement(targetingRequirement));
      this.targetingRequirement = targetingRequirement;
   }

   private Keyword(Color col, String x2if, StateConditionType condType, boolean source) {
      this(col, source ? "x2 if I " + x2if : "x2 vs targets " + x2if, null, new ConditionalBonus(condType, source));
   }

   private Keyword(Color col, String x2if, ParamCondition x2Condition) {
      this(col, x2Condition.isSource() ? "x2 if I " + x2if : "x2 vs targets " + x2if, null, new ConditionalBonus(x2Condition));
   }

   private Keyword(Color col, String x2if, EnumConditionalRequirement x2Condition) {
      this(col, (x2Condition.preCalculate() ? "x2 if " : "x2 vs ") + x2if, null, new ConditionalBonus(x2Condition));
   }

   public static List<Keyword> search(String text) {
      text = text.toLowerCase();
      List<Keyword> result = new ArrayList<>();

      for (Keyword k : values()) {
         if (k.name.toLowerCase().contains(text)) {
            result.add(k);
         }
      }

      return result;
   }

   public static Keyword byName(String text) {
      text = text.toLowerCase();

      for (Keyword k : values()) {
         if (k.name.toLowerCase().equals(text)) {
            return k;
         }

         if (k.name().toLowerCase().equals(text)) {
            return k;
         }
      }

      return text.equalsIgnoreCase("alliteration") ? sloth : null;
   }

   @Override
   public String toString() {
      return this.name != null ? this.name : super.toString();
   }

   public String getRules() {
      return this.getRules(null);
   }

   public String getRules(Eff source) {
      String colTag = TextWriter.getTag(this.getColour());
      if (source != null && source.getValue() != -999) {
         int val = KUtils.getValue(source);
         if (this.rules.contains("ther keywords see ")) {
            val = source.getValue();
         }

         String tmp = this.rules;
         if (source.hasKeyword(treble) && this != treble) {
            tmp = tmp.replaceAll("x2", "x3");
         }

         return tmp.replace("!N!", colTag + val + "[cu]")
            .replace("!2N!", colTag + val * 2 + "[cu]")
            .replace("!NP1!", colTag + (val + 1) + "[cu]")
            .replace("!N2!", colTag + val * val + "[cu]");
      } else {
         return this.rules
            .replaceAll("!N!", colTag + "N[cu]")
            .replaceAll("!2N!", colTag + "2N[cu]")
            .replaceAll("!NP1!", colTag + "N+1[cu]")
            .replaceAll("!N2!", colTag + "N-squared[cu]");
      }
   }

   public String getColourTaggedString() {
      if (this == rainbow) {
         String sep = "[cu]";
         String[] cols = new String[]{"[red]", "[orange]", "[yellow]", "[green]", "[blue]", "[pink]", "[purple]"};
         String word = "rainbow";
         String result = "";

         for (int i = 0; i < "rainbow".length(); i++) {
            result = result + cols[i] + "rainbow".charAt(i) + "[cu]";
         }

         return result;
      } else {
         String n = this.getName();
         if (n.startsWith("self")) {
            return "[grey]self[cu]" + TextWriter.getTag(this.getColour()) + n.substring(4).toLowerCase() + "[cu]";
         } else if (this.metaKeyword2 != null) {
            return KUtils.makeName(n, this.metaKeyword, this.metaKeyword2);
         } else {
            List<TP<String, String>> rep = Arrays.asList(new TP<>("hyper", "[pink]"));

            for (int i = 0; i < rep.size(); i++) {
               TP<String, String> tp = rep.get(i);
               if (n.startsWith(tp.a)) {
                  String secondPart = n.substring(tp.a.length());
                  return tp.b + tp.a + "[cu]" + TextWriter.getTag(this.getColour()) + secondPart.toLowerCase() + "[cu]";
               }
            }

            if (this.metaKeyword != null) {
               rep = Arrays.asList(
                  new TP<>("group", "[yellow]"),
                  new TP<>("anti", "[blue]"),
                  new TP<>("halve", "[grey]"),
                  new TP<>("swap", "[light]"),
                  new TP<>("minus", "[green]"),
                  new TP<>("inflict", "[purple]"),
                  new TP<>("double", "[orange]"),
                  new TP<>("hyper", "[pink]")
               );

               for (int ix = 0; ix < rep.size(); ix++) {
                  TP<String, String> tp = rep.get(ix);
                  if (n.startsWith(tp.a)) {
                     return tp.b + tp.a + "[cu]" + this.metaKeyword.getColourTaggedString();
                  }
               }

               System.out.println("hmm unable to find autometa: " + n);
            }

            String tag = TextWriter.getTag(this.getColour());
            return tag + this + "[cu]";
         }
      }
   }

   public boolean abilityOnly() {
      return this.extraAbilityOnly() || this.reallySpellOnly();
   }

   public boolean reallySpellOnly() {
      return this == deplete || this == channel || this == spellRescue;
   }

   private boolean extraAbilityOnly() {
      return this == singleCast || this == cooldown || this == future;
   }

   public boolean skipStats() {
      return this == removed;
   }

   public boolean skipDebug() {
      return this.abilityOnly() || this == removed;
   }

   public String getExtraRules() {
      return this.extraRules;
   }

   public TextureRegion getImage() {
      return this.getImage(EntSize.reg);
   }

   public TextureRegion getImage(EntSize size) {
      return size == EntSize.small ? this.cornerTiny : this.corner;
   }

   public Color getColour() {
      return this.col;
   }

   public String getName() {
      return this.name;
   }

   public ConditionalBonus getConditionalBonus() {
      return this.conditionalBonus;
   }

   public ConditionalRequirement getTargetingConditionalRequirement() {
      return this.targetingRequirement;
   }

   public boolean isFlipCorner() {
      return this.flipCorner;
   }

   public Keyword getInflict() {
      return this.inflict;
   }

   public Keyword getGroupAct() {
      return this.groupAct;
   }

   public long getCollisionBits() {
      switch (this) {
         case regen:
            return Collision.REGEN;
         case heal:
         case selfHeal:
            return Collision.HEAL;
         case shield:
         case selfShield:
         case steel:
            return Collision.SHIELD;
         case poison:
         case plague:
            return Collision.POISON;
         case fizz:
         case charged:
         case manacost:
         case manaGain:
            return Collision.SPELL;
         case ranged:
         case heavy:
            return Collision.RANGED;
         default:
            return 0L;
      }
   }

   public KeywordAllowType getAllowType() {
      return this.allowType;
   }

   public Keyword getMetaKeyword() {
      return this.metaKeyword;
   }

   public boolean isDoubleAct() {
      return this.doubleAct;
   }

   public int getSortPriority() {
      switch (this) {
         case fumble:
         case fluctuate:
         case resonate:
            return -3;
         case copycat:
         case shifter:
         case spy:
         case dejavu:
            return -1;
         case doubDiff:
         case revDiff:
         case lucky:
         case critical:
         case echo:
            return 1;
         default:
            if (this.conditionalBonus == null) {
               return 0;
            } else if (this.conditionalBonus.requirement == null) {
               return 1;
            } else {
               return this.conditionalBonus.requirement.preCalculate() ? 1 : 2;
            }
      }
   }
}
