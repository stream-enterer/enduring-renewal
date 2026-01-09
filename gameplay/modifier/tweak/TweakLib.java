package com.tann.dice.gameplay.modifier.tweak;

import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.global.GlobalFleeAvoid;
import com.tann.dice.gameplay.trigger.global.chance.Rarity;
import com.tann.dice.gameplay.trigger.global.eff.GlobalEndTurnEff;
import com.tann.dice.gameplay.trigger.global.item.GlobalItemQuality;
import com.tann.dice.gameplay.trigger.global.item.GlobalItemQuantity;
import com.tann.dice.gameplay.trigger.global.linked.GlobalSize;
import com.tann.dice.gameplay.trigger.global.linked.GlobalSpecificEntTypes;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalAllEntities;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalHeroes;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalMonsters;
import com.tann.dice.gameplay.trigger.global.roll.GlobalBonusRerolls;
import com.tann.dice.gameplay.trigger.global.roll.GlobalKeepRerolls;
import com.tann.dice.gameplay.trigger.global.roll.GlobalLockDiceLimit;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.GlobalTurnRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementFirst;
import com.tann.dice.gameplay.trigger.global.spell.GlobalSpellCostChange;
import com.tann.dice.gameplay.trigger.personal.CopySide;
import com.tann.dice.gameplay.trigger.personal.Undying;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.TypeCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AffectByIndex;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.CopyBaseFromHeroAbove;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.FlatBonus;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;
import com.tann.dice.gameplay.trigger.personal.hp.MaxHP;
import com.tann.dice.gameplay.trigger.personal.specialPips.pipLoc.PipLoc;
import com.tann.dice.gameplay.trigger.personal.specialPips.pipLoc.PipLocType;
import com.tann.dice.gameplay.trigger.personal.specialPips.resistive.StoneSpecialHp;
import com.tann.dice.gameplay.trigger.personal.startBuffed.StartPoisoned;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TweakLib {
   public static List<Modifier> makeAll() {
      List<Modifier> all = new ArrayList<>();
      all.addAll(
         Arrays.asList(
            new Modifier(0.0F, "No Flee", new GlobalFleeAvoid()),
            new Modifier(0.0F, "Crust", new GlobalMonsters(new MaxHP(-2)), new GlobalMonsters(new StoneSpecialHp(new PipLoc(PipLocType.RightmostN, 2)))),
            new Modifier(
               0.0F,
               "Roll Bank",
               new GlobalKeepRerolls(),
               new GlobalBonusRerolls(-2),
               new GlobalTurnRequirement(new TurnRequirementFirst(), new GlobalBonusRerolls(6))
            ),
            new Modifier(
               0.0F,
               "Knife School",
               new GlobalHeroes(new AffectSides(new TypeCondition(EffType.Blank), new ReplaceWith(ESB.dmgCantrip.val(1).withKeyword(Keyword.pain))))
            ),
            new Modifier(0.0F, "Power up", new GlobalHeroes(new AffectSides(new FlatBonus(1))), new GlobalMonsters(new AffectSides(new FlatBonus(2)))),
            new Modifier(0.0F, "Power down", new GlobalAllEntities(new AffectSides(new FlatBonus(-1)))),
            new Modifier(
                  0.0F,
                  "Historical Accuracy",
                  new GlobalSpecificEntTypes(new AffectSides(new AddKeyword(Keyword.ranged)), MonsterTypeLib.byName("archer"), MonsterTypeLib.byName("sniper"))
               )
               .rarity(Rarity.FIFTIETH),
            new Modifier(0.0F, "More hp", new GlobalAllEntities(null, new MaxHP(2))),
            new Modifier(0.0F, "Less hp", new GlobalMonsters(new MaxHP(-1)), new GlobalHeroes(new MaxHP(-2))),
            new Modifier(0.0F, "Keep Rolling", new GlobalBonusRerolls(2), new GlobalLockDiceLimit(0)),
            new Modifier(0.0F, "Mana Flux", new GlobalSpellCostChange(1), new GlobalHeroes(new AffectSides(new TypeCondition(EffType.Mana), new FlatBonus(1)))),
            new Modifier(0.0F, "Toxic Fog", new GlobalAllEntities(null, new StartPoisoned(1))),
            new Modifier(0.0F, "Tweaked Monsters", new GlobalMonsters(new MaxHP(-2)), new GlobalMonsters(new AffectSides(new FlatBonus(1)))),
            new Modifier(
               0.0F,
               "Specialist",
               new GlobalHeroes(new AffectSides(SpecificSidesType.LeftTwo, new AffectByIndex(new FlatBonus(1), new ReplaceWith(ESB.blankCurse))))
            ),
            new Modifier(0.0F, "Loan", new GlobalHeroes(new MaxHP(5)), new GlobalHeroes(new AffectSides(new AddKeyword(Keyword.pain)))),
            new Modifier(0.0F, "Huge Plague", new GlobalSize(EntSize.huge, new AffectSides(new AddKeyword(Keyword.poison, Keyword.pain)))),
            new Modifier(0.0F, "Small Sacrifice", new GlobalSize(EntSize.small, new AffectSides(new FlatBonus(3), new AddKeyword(Keyword.death)))),
            new Modifier(0.0F, "Monster Wands", new GlobalMonsters(new AffectSides(new AddKeyword(Keyword.singleUse), new FlatBonus(1)))),
            new Modifier(0.0F, "Topsy Turvy", new GlobalHeroes(new AffectSides(new CopyBaseFromHeroAbove(true)))),
            new Modifier(
               0.0F,
               "Crumbling Castle",
               new GlobalMonsters(new MaxHP(6)),
               new GlobalTurnRequirement(6, new GlobalEndTurnEff(new EffBill().flee().enemy().group().bEff()))
            ),
            new Modifier(0.0F, "Truce", new GlobalTurnRequirement(new TurnRequirementFirst(), new GlobalAllEntities(new Undying()))),
            new Modifier(0.0F, "Fingers Crossed", new GlobalHeroes(new AffectSides(SpecificSidesType.Right, new AddKeyword(Keyword.critical, Keyword.fumble)))),
            new Modifier(0.0F, "Threesy Street", new GlobalHeroes(new AffectSides(SpecificSidesType.RightMost, new AddKeyword(Keyword.threesy)))),
            new Modifier(0.0F, "Insidious Whispers", new GlobalHeroes(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.enduring, Keyword.evil)))),
            new Modifier(0.0F, "High Roller", new GlobalHeroes(new AffectSides(SpecificSidesType.Top, new AddKeyword(Keyword.lucky), new FlatBonus(2)))),
            new Modifier(
               0.0F,
               "Pungent Balm",
               new GlobalHeroes(new AffectSides(SpecificSidesType.Left, new AddKeyword(Keyword.selfCleanse))),
               new GlobalHeroes(new MaxHP(-1))
            ),
            new Modifier(0.0F, "Caped Monsters", new GlobalMonsters(new CopySide(SpecificSidesType.Left, SpecificSidesType.Row))),
            new Modifier(0.0F, "Strained Top", new GlobalHeroes(new AffectSides(SpecificSidesType.Top, new FlatBonus(2), new AddKeyword(Keyword.exert)))),
            new Modifier(
               0.0F,
               "Ting",
               new GlobalMonsters(new StoneSpecialHp(new PipLoc(PipLocType.RightmostN, 1))),
               new GlobalHeroes(new AffectSides(SpecificSidesType.MiddleTwo, new AddKeyword(Keyword.rampage)))
            ),
            new Modifier(0.0F, "Choosy", new GlobalItemQuality(-1), new GlobalItemQuantity(1)),
            new Modifier(0.0F, "Choozy", new GlobalItemQuality(1), new GlobalItemQuantity(-1))
         )
      );
      return all;
   }
}
