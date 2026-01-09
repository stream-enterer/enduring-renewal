package com.tann.dice.gameplay.content.ent.type.blob.monster;

import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobBig;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobHuge;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobSmall;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.bill.MTBill;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.event.entState.ChatStateEvent;
import com.tann.dice.gameplay.fightLog.event.entState.LinkEvent;
import com.tann.dice.gameplay.fightLog.event.entState.PanelHighlightEvent;
import com.tann.dice.gameplay.fightLog.event.snapshot.ManaGainEvent;
import com.tann.dice.gameplay.fightLog.event.snapshot.SoundSnapshotEvent;
import com.tann.dice.gameplay.trigger.global.GlobalFleeAvoid;
import com.tann.dice.gameplay.trigger.global.chance.Rarity;
import com.tann.dice.gameplay.trigger.global.linked.GlobalTopNonMagic;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalHeroes;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalMonsters;
import com.tann.dice.gameplay.trigger.personal.Armour;
import com.tann.dice.gameplay.trigger.personal.Chip;
import com.tann.dice.gameplay.trigger.personal.Cowardly;
import com.tann.dice.gameplay.trigger.personal.DeathAfterNumHits;
import com.tann.dice.gameplay.trigger.personal.FleeIfOnlyRemain;
import com.tann.dice.gameplay.trigger.personal.Gong;
import com.tann.dice.gameplay.trigger.personal.Mettley;
import com.tann.dice.gameplay.trigger.personal.OnDamage;
import com.tann.dice.gameplay.trigger.personal.Plague;
import com.tann.dice.gameplay.trigger.personal.RottenTrick;
import com.tann.dice.gameplay.trigger.personal.Stunned;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.SimpleKeywordTrait;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.FlatBonus;
import com.tann.dice.gameplay.trigger.personal.death.DamageAdjacentsOnDeath;
import com.tann.dice.gameplay.trigger.personal.death.OverkillFlee;
import com.tann.dice.gameplay.trigger.personal.eff.TargetShieldGain;
import com.tann.dice.gameplay.trigger.personal.eff.Truce;
import com.tann.dice.gameplay.trigger.personal.immunity.AbilityImmune;
import com.tann.dice.gameplay.trigger.personal.item.Chest;
import com.tann.dice.gameplay.trigger.personal.linked.TriggerPersonalToGlobal;
import com.tann.dice.gameplay.trigger.personal.merge.Regen;
import com.tann.dice.gameplay.trigger.personal.onHit.Mirror;
import com.tann.dice.gameplay.trigger.personal.onHit.PetrifyOnAttack;
import com.tann.dice.gameplay.trigger.personal.onHit.Spiky;
import com.tann.dice.gameplay.trigger.personal.position.BackRow;
import com.tann.dice.gameplay.trigger.personal.quest.Rotten;
import com.tann.dice.gameplay.trigger.personal.specialPips.DeathSpecialHp;
import com.tann.dice.gameplay.trigger.personal.specialPips.GhostHP;
import com.tann.dice.gameplay.trigger.personal.specialPips.TriggerHP;
import com.tann.dice.gameplay.trigger.personal.specialPips.TriggerHPSummon;
import com.tann.dice.gameplay.trigger.personal.specialPips.pipLoc.PipLoc;
import com.tann.dice.gameplay.trigger.personal.specialPips.pipLoc.PipLocType;
import com.tann.dice.gameplay.trigger.personal.specialPips.resistive.ResistSpecialHp;
import com.tann.dice.gameplay.trigger.personal.specialPips.resistive.StoneSpecialHp;
import com.tann.dice.gameplay.trigger.personal.spell.AfterUseAbility;
import com.tann.dice.gameplay.trigger.personal.spell.OnSpendAbilityCost;
import com.tann.dice.gameplay.trigger.personal.startBuffed.StartPoisoned;
import com.tann.dice.gameplay.trigger.personal.stats.CalcOnly;
import com.tann.dice.gameplay.trigger.personal.stats.CopyOtherPowerEstimate;
import com.tann.dice.gameplay.trigger.personal.util.CalcStats;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MonsterTypeBlobBasic {
   private static final PipLoc witchPip = new PipLoc(PipLocType.Specific, 3);

   public static List<MonsterType> make() {
      List<MonsterType> result = new ArrayList<>();
      result.add(
         new MTBill(EntSize.small)
            .name("Bones")
            .hp(4)
            .death(Sounds.deathPew)
            .max(4)
            .sides(
               EntSidesBlobSmall.bone.val(4),
               EntSidesBlobSmall.bone.val(4),
               EntSidesBlobSmall.bone.val(4),
               EntSidesBlobSmall.bone.val(4),
               EntSidesBlobSmall.bone.val(3),
               EntSidesBlobSmall.bone.val(3)
            )
            .trait(new Trait(new DamageAdjacentsOnDeath(1)))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.small)
            .name("Log")
            .hp(3)
            .death(Sounds.deathPew)
            .max(2)
            .rarity(Rarity.FIFTIETH)
            .sides(EntSidesBlobSmall.blank)
            .trait(new Trait(new FleeIfOnlyRemain("Log")))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.small)
            .name("Archer")
            .hp(2)
            .death(Sounds.deathCute)
            .max(4)
            .sides(
               EntSidesBlobSmall.arrow.val(3),
               EntSidesBlobSmall.arrow.val(3),
               EntSidesBlobSmall.arrow.val(2),
               EntSidesBlobSmall.arrow.val(2),
               EntSidesBlobSmall.arrow.val(2),
               EntSidesBlobSmall.arrow.val(2)
            )
            .trait(Trait.BACK_ROW)
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.small)
            .name("Sniper")
            .hp(3)
            .death(Sounds.deathCute)
            .max(4)
            .sides(
               EntSidesBlobSmall.arrow.val(5),
               EntSidesBlobSmall.arrow.val(5),
               EntSidesBlobSmall.arrow.val(5),
               EntSidesBlobSmall.arrow.val(5),
               EntSidesBlobSmall.arrowEliminate.val(4),
               EntSidesBlobSmall.arrowEliminate.val(4)
            )
            .trait(Trait.BACK_ROW)
            .trait(new CalcOnly(), new CalcStats(0.3F, 0.0F), false)
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.small)
            .name("Shade")
            .hp(5)
            .death(Sounds.deathCute)
            .max(2)
            .rarity(Rarity.HALF)
            .sides(
               EntSidesBlobSmall.arrowEliminate.val(5),
               EntSidesBlobSmall.arrowEliminate.val(5),
               EntSidesBlobSmall.arrowEliminate.val(4),
               EntSidesBlobSmall.arrowEliminate.val(4),
               EntSidesBlobSmall.arrowEliminate.val(3),
               EntSidesBlobSmall.arrowEliminate.val(3)
            )
            .trait(new GhostHP(PipLoc.all()))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.small)
            .name("Rat")
            .hp(3)
            .death(Sounds.deathSqueak)
            .sides(
               EntSidesBlobSmall.nip.val(3),
               EntSidesBlobSmall.nip.val(3),
               EntSidesBlobSmall.nip.val(2),
               EntSidesBlobSmall.nip.val(2),
               EntSidesBlobSmall.nip.val(2),
               EntSidesBlobSmall.nip.val(2)
            )
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.small)
            .name("Wisp")
            .hp(5)
            .death(Sounds.deathSqueak)
            .max(5)
            .sides(
               EntSidesBlobSmall.selfHealVitality.val(3),
               EntSidesBlobSmall.curse.val(2),
               EntSidesBlobSmall.curse.val(2),
               EntSidesBlobSmall.curse.val(2),
               EntSidesBlobSmall.summonBones.val(1),
               EntSidesBlobSmall.summonBones.val(1)
            )
            .trait(
               new Trait(
                  new TriggerHP(new EffBill().mana(1).bEff(), new ArrayList<>(), new ManaGainEvent(1, "Wisp"), Colours.blue, new PipLoc(PipLocType.Specific, 2)),
                  new CalcStats(0.0F, -0.9F)
               )
            )
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.small)
            .name("Slimelet")
            .hp(2)
            .death(Sounds.deathCute)
            .sides(
               EntSidesBlobSmall.slime.val(3),
               EntSidesBlobSmall.slime.val(3),
               EntSidesBlobSmall.slime.val(3),
               EntSidesBlobSmall.slime.val(3),
               EntSidesBlobSmall.blank,
               EntSidesBlobSmall.blank
            )
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.small)
            .name("Imp")
            .hp(4)
            .death(Sounds.deathCute)
            .sides(
               EntSidesBlobSmall.sting.val(8),
               EntSidesBlobSmall.nip.val(4),
               EntSidesBlobSmall.nip.val(4),
               EntSidesBlobSmall.nip.val(4),
               EntSidesBlobSmall.nipPoison.val(1),
               EntSidesBlobSmall.nipPoison.val(1)
            )
            .trait(new Trait(new Spiky(1)))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.small)
            .name("Spider")
            .hp(4)
            .death(Sounds.deathCute)
            .sides(
               EntSidesBlobSmall.nip.val(3),
               EntSidesBlobSmall.nip.val(3),
               EntSidesBlobSmall.nip.val(3),
               EntSidesBlobSmall.nip.val(3),
               EntSidesBlobSmall.nipPoison.val(1),
               EntSidesBlobSmall.nipPoison.val(1)
            )
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.small)
            .name("Bee")
            .hp(2)
            .death(Sounds.deathCute)
            .sides(
               EntSidesBlobSmall.sting.val(4).withKeyword(Keyword.death),
               EntSidesBlobSmall.sting.val(4).withKeyword(Keyword.death),
               EntSidesBlobSmall.nip.val(1),
               EntSidesBlobSmall.nip.val(1),
               EntSidesBlobSmall.nip.val(1),
               EntSidesBlobSmall.nip.val(1)
            )
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.small)
            .name("Grave")
            .hp(3)
            .death(Sounds.deathCute)
            .max(4)
            .sides(
               EntSidesBlobSmall.summonBones.val(1),
               EntSidesBlobSmall.summonBones.val(1),
               EntSidesBlobSmall.summonBones.val(1),
               EntSidesBlobSmall.summonBones.val(1),
               EntSidesBlobSmall.summonBones.val(1),
               EntSidesBlobSmall.summonBones.val(1)
            )
            .trait(new Trait(new StoneSpecialHp(PipLoc.all())))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.small)
            .name("Illusion")
            .hp(1)
            .death(Sounds.deathCute)
            .min(2)
            .max(4)
            .sides(
               EntSidesBlobSmall.weaken.val(2),
               EntSidesBlobSmall.weaken.val(1),
               EntSidesBlobSmall.petrify.val(1),
               EntSidesBlobSmall.petrify.val(1),
               EntSidesBlobSmall.weaken.val(1),
               EntSidesBlobSmall.weaken.val(1)
            )
            .rarity(Rarity.THIRD)
            .trait(new CalcOnly(), new CalcStats(0.0F, 0.4F), false)
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.small)
            .name("Chest")
            .hp(5)
            .death(Sounds.deathCute)
            .max(1)
            .rarity(Rarity.TENTH)
            .sides(
               EntSidesBlobSmall.nip.val(2),
               EntSidesBlobSmall.nip.val(2),
               EntSidesBlobSmall.petrify.val(1),
               EntSidesBlobSmall.petrify.val(1),
               EntSidesBlobSmall.summonSlimelet.val(1),
               EntSidesBlobSmall.summonSlimelet.val(1)
            )
            .trait(new StoneSpecialHp(new PipLoc(PipLocType.Specific, 4)))
            .trait(new Chest(1, 0, 2))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.small)
            .name("Thorn")
            .hp(2)
            .death(Sounds.deathCute)
            .max(2)
            .rarity(Rarity.TWO_THIRDS)
            .sides(
               EntSidesBlobSmall.arrowEliminate.val(4),
               EntSidesBlobSmall.arrowEliminate.val(4),
               EntSidesBlobSmall.petrify.val(2),
               EntSidesBlobSmall.petrify.val(2),
               EntSidesBlobSmall.petrify.val(1),
               EntSidesBlobSmall.petrify.val(1)
            )
            .trait(new AbilityImmune())
            .trait(new Spiky(5), new CalcStats(0.55F, 1.85F), true)
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.reg)
            .name("Goblin")
            .hp(5)
            .death(Sounds.deathReg)
            .max(2)
            .sides(ESB.dmg.val(4), ESB.dmg.val(4), ESB.dmgEliminate.val(3), ESB.dmgEliminate.val(3), ESB.dmgCleave.val(1), ESB.dmgCleave.val(1))
            .trait(new Cowardly())
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.reg)
            .name("Gnoll")
            .hp(3)
            .death(Sounds.deathReg)
            .sides(ESB.dmgHeavy.val(5), ESB.dmgHeavy.val(5), ESB.dmgExert.val(6), ESB.dmgExert.val(6), ESB.dmgHeavy.val(4), ESB.dmgHeavy.val(4))
            .trait(new Armour(1))
            .bEntType()
      );
      MonsterType zombie;
      result.add(
         zombie = new MTBill(EntSize.reg)
            .name("Zombie")
            .hp(10)
            .death(Sounds.deathReg)
            .max(3)
            .sides(ESB.dmgPoison.val(2), ESB.dmgPoison.val(2), ESB.dmgCleave.val(2), ESB.dmgCleave.val(2), ESB.dmgCleave.val(2), ESB.dmgCleave.val(2))
            .trait(new Trait(new Rotten(4)))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.reg)
            .name("Z0mbie")
            .hp(10)
            .death(Sounds.deathReg)
            .rarity(Rarity.FIVE_HUNDREDTH)
            .sides(zombie.getNiceSides())
            .trait(new Trait(new RottenTrick(4)))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.reg)
            .name("Wolf")
            .hp(6)
            .death(Sounds.deathOof)
            .sides(ESB.bite.val(4), ESB.bite.val(4), ESB.bite.val(3), ESB.bite.val(3), ESB.slash.val(1), ESB.slash.val(1))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.reg)
            .name("Grandma")
            .hp(6)
            .death(Sounds.deathOof)
            .rarity(Rarity.HUNDREDTH)
            .sides(ESB.bite.val(4), ESB.bite.val(4), ESB.bite.val(3), ESB.bite.val(3), ESB.slash.val(1), ESB.slash.val(1))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.reg)
            .name("Snake")
            .hp(5)
            .death(Sounds.deathOof)
            .max(4)
            .sides(
               ESB.snakePoison.val(2), ESB.snakePoison.val(2), ESB.snakePoison.val(1), ESB.snakePoison.val(1), ESB.snakePoison.val(1), ESB.snakePoison.val(1)
            )
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.reg)
            .name("Quartz")
            .hp(7)
            .death(Sounds.deathOof)
            .sides(
               ESB.dmgInflictSingle.val(5), ESB.dmgInflictSingle.val(5), ESB.dmgWeaken.val(2), ESB.dmgWeaken.val(2), ESB.dmgWeaken.val(2), ESB.dmgWeaken.val(2)
            )
            .trait(new DeathSpecialHp(new PipLoc(PipLocType.Specific, 2)))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.reg)
            .name("Sudul")
            .hp(7)
            .death(Sounds.deathOof)
            .rarity(Rarity.THIRD)
            .sides(ESB.dmg.val(2), ESB.dmg.val(1), ESB.dmg.val(4), ESB.dmg.val(3), ESB.dmg.val(5), ESB.dmg.val(6))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.reg)
            .name("Fanatic")
            .hp(13)
            .death(Sounds.deathOof)
            .max(4)
            .sides(ESB.dmgPain.val(8), ESB.dmgPain.val(8), ESB.dmgPain.val(6), ESB.dmgPain.val(6), ESB.dmgPain.val(4), ESB.dmgPain.val(4))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.reg)
            .name("Saber")
            .hp(10)
            .death(Sounds.deathPew)
            .sides(ESB.dmgDeath.val(12), ESB.dmg.val(5), ESB.dmgExert.val(8), ESB.dmgExert.val(8), ESB.dmg.val(5), ESB.dmg.val(5))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.reg)
            .name("Militia")
            .hp(7)
            .death(Sounds.deathPew)
            .max(2)
            .sides(ESB.dmg.val(4), ESB.dmg.val(4), ESB.dmg.val(4), ESB.dmg.val(4), ESB.dmg.val(4), ESB.dmg.val(4))
            .trait(new TargetShieldGain(new EffBill().flee().self().bEff(), 5), new CalcStats(0.0F, -0.7F))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.reg)
            .name("Carrier")
            .hp(10)
            .death(Sounds.deathPew)
            .rarity(Rarity.HALF)
            .sides(ESB.dmg.val(5), ESB.dmg.val(5), ESB.dmgPoison.val(2), ESB.dmgPoison.val(2), ESB.dmg.val(5), ESB.poisonAll.val(1))
            .trait(new StartPoisoned(2).overrideShow(true), new CalcStats(0.0F, -4.0F))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.reg)
            .name("Bandit")
            .hp(8)
            .death(Sounds.deathReg)
            .sides(ESB.dmg.val(6), ESB.dmg.val(6), ESB.dmg.val(5), ESB.dmg.val(5), ESB.dmgPoison.val(2), ESB.dmgPoison.val(1))
            .trait(new OverkillFlee(2))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.reg)
            .name("Barrel")
            .hp(6)
            .death(Sounds.deathExplosion)
            .max(1)
            .rarity(Rarity.TWO_THIRDS)
            .sides(ESB.blank, ESB.blank, ESB.blank, ESB.blank, ESB.blank, ESB.blank)
            .trait(new Trait(new DamageAdjacentsOnDeath(5)))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.reg)
            .name("Golem")
            .hp(2)
            .death(Sounds.deathExplosion)
            .rarity(Rarity.THIRD)
            .sides(ESB.dmgSteel.val(0), ESB.dmgSteel.val(0), ESB.dmg.val(5), ESB.dmg.val(5), ESB.dmgSelfShield.val(2), ESB.dmgSelfShield.val(2))
            .trait(new Trait(new Mettley(8)))
            .trait(new CalcOnly(), new CalcStats(2.6666667F, 0.0F), false)
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.reg)
            .name("Fountain")
            .hp(6)
            .death(Sounds.deathExplosion)
            .max(1)
            .rarity(Rarity.TENTH)
            .sides(ESB.blank)
            .trait(
               new Trait(
                  new TriggerHP(new EffBill().mana(1).bEff(), new ArrayList<>(), new ManaGainEvent(1, "Fountain"), Colours.blue, PipLoc.all()),
                  new CalcStats(0.0F, -7.5F)
               )
            )
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.reg)
            .name("Warchief")
            .hp(6)
            .max(1)
            .death(Sounds.deathOof)
            .sides(ESB.dmg.val(2), ESB.dmg.val(2), ESB.dmg.val(2), ESB.dmg.val(2), ESB.dmgCleave.val(0), ESB.dmgCleave.val(0))
            .trait(new Trait(new TriggerPersonalToGlobal(new GlobalMonsters(new AffectSides(new FlatBonus(1))), "badge"), new CalcStats(7.4F, 0.0F), true))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.reg)
            .name("Blind")
            .hp(5)
            .death(Sounds.deathOof)
            .max(1)
            .sides(ESB.dmgAll.val(2), ESB.dmgAll.val(2), ESB.dmgAll.val(1), ESB.dmgAll.val(1), ESB.dmgAll.val(1), ESB.dmgAll.val(1))
            .trait(new Trait(new Truce(), new CalcStats(-0.45F, -0.45F), true))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.big)
            .name("Wizz")
            .hp(5)
            .death(Sounds.deathReg)
            .max(1)
            .sides(
               EntSidesBlobBig.weaken.val(4),
               EntSidesBlobBig.brew_group.val(3),
               EntSidesBlobBig.batSwarm.val(2),
               EntSidesBlobBig.batSwarm.val(2),
               EntSidesBlobBig.summonSkeleton.val(2),
               EntSidesBlobBig.summonSkeleton.val(2)
            )
            .trait(Trait.BACK_ROW)
            .trait(
               new TriggerHP(
                  new EffBill().self().buff(new Buff(1, new Stunned())).bEff(),
                  new LinkEvent(SoundSnapshotEvent.clang),
                  Colours.light,
                  new PipLoc(PipLocType.Specific, 3)
               )
            )
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.big)
            .name("Slimer")
            .hp(7)
            .death(Sounds.deathAlien)
            .banLevels(6, 8)
            .sides(
               EntSidesBlobBig.slimeTriple.val(2),
               EntSidesBlobBig.slimeTriple.val(2),
               EntSidesBlobBig.slimeUpDown.val(3),
               EntSidesBlobBig.slimeUpDown.val(3),
               EntSidesBlobBig.slimeTriple.val(2),
               EntSidesBlobBig.slimeTriple.val(2)
            )
            .trait(new Trait(new TriggerHPSummon("slimelet", 1, SoundSnapshotEvent.slime, PipLoc.offsetEvery(5, 4))))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.big)
            .name("Alpha")
            .hp(13)
            .death(Sounds.deathHorse)
            .sides(
               EntSidesBlobBig.rabidFrenzy.val(2),
               EntSidesBlobBig.rabidFrenzy.val(2),
               EntSidesBlobBig.bite.val(6),
               EntSidesBlobBig.bite.val(6),
               EntSidesBlobBig.howl.val(1),
               EntSidesBlobBig.howl.val(1)
            )
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.big)
            .name("Ogre")
            .hp(10)
            .death(Sounds.deathBig)
            .sides(
               EntSidesBlobBig.stomp.val(1),
               EntSidesBlobBig.stomp.val(1),
               EntSidesBlobBig.swordCleave.val(2),
               EntSidesBlobBig.swordCleave.val(2),
               EntSidesBlobBig.stomp.val(1),
               EntSidesBlobBig.stomp.val(1)
            )
            .trait(
               new Trait(
                  new TriggerHP(
                     new EffBill().self().buff(new Buff(new AffectSides(new FlatBonus(true, 1)))).bEff(),
                     ChatStateEvent.Provoke,
                     Colours.yellow,
                     PipLoc.offsetEvery(5, 2)
                  )
               )
            )
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.big)
            .name("Demon")
            .hp(12)
            .death(Sounds.deathSpawn)
            .sides(
               EntSidesBlobBig.curse.val(6),
               EntSidesBlobBig.curse.val(6),
               EntSidesBlobBig.batSwarm.val(2),
               EntSidesBlobBig.batSwarm.val(2),
               EntSidesBlobBig.summonImp.val(1),
               EntSidesBlobBig.summonImp.val(1)
            )
            .trait(new Trait(new ResistSpecialHp(ResistSpecialHp.DamageType.DiceOnly, new PipLoc(PipLocType.EveryN, 10))))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.big)
            .name("Chomp")
            .hp(10)
            .death(Sounds.deathSpawn)
            .sides(
               EntSidesBlobBig.bite.val(7),
               EntSidesBlobBig.bite.val(7),
               EntSidesBlobBig.peck.val(5),
               EntSidesBlobBig.peck.val(5),
               EntSidesBlobBig.boarBite.val(4),
               EntSidesBlobBig.boarBite.val(4)
            )
            .trait(
               new Trait(
                  new TriggerHP(
                     new EffBill().damage(1).targetType(TargetingType.Bot).bEff(),
                     Arrays.asList(PanelHighlightEvent.witchHex, new LinkEvent(SoundSnapshotEvent.witchHex)),
                     null,
                     Images.hp_arrow_down,
                     Colours.orange,
                     new PipLoc(PipLocType.LeftmostN, 5)
                  )
               )
            )
            .trait(
               new Trait(
                  new TriggerHP(
                     new EffBill().damage(1).targetType(TargetingType.Top).bEff(),
                     Arrays.asList(PanelHighlightEvent.witchHex, new LinkEvent(SoundSnapshotEvent.witchHex)),
                     null,
                     Images.hp_arrow_up,
                     Colours.orange,
                     new PipLoc(PipLocType.RightmostN, 5)
                  )
               )
            )
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.big)
            .name("Ghost")
            .hp(6)
            .death(Sounds.deathWeird)
            .sides(
               EntSidesBlobBig.haunt.val(6),
               EntSidesBlobBig.haunt.val(6),
               EntSidesBlobBig.decay.val(4),
               EntSidesBlobBig.decay.val(4),
               EntSidesBlobBig.poison.val(2),
               EntSidesBlobBig.poison.val(2)
            )
            .trait(new Trait(new GhostHP(new PipLoc(PipLocType.Specific, 4))))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.big)
            .name("Boar")
            .hp(7)
            .death(Sounds.deathBig)
            .sides(
               EntSidesBlobBig.boarBite.val(4),
               EntSidesBlobBig.boarBite.val(4),
               EntSidesBlobBig.boarBite.val(4),
               EntSidesBlobBig.boarBite.val(4),
               EntSidesBlobBig.gore.val(2),
               EntSidesBlobBig.gore.val(2)
            )
            .trait(new Trait(new StoneSpecialHp(new PipLoc(PipLocType.LeftmostN, 1))))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.big)
            .name("Spiker")
            .hp(13)
            .death(Sounds.deathHorse)
            .max(1)
            .sides(
               EntSidesBlobBig.punch.val(7),
               EntSidesBlobBig.punch.val(7),
               EntSidesBlobBig.punch.val(7),
               EntSidesBlobBig.punch.val(7),
               EntSidesBlobBig.spikeSpray.val(2),
               EntSidesBlobBig.spikeSpray.val(2)
            )
            .trait(new Trait(new Spiky(2)))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.big)
            .name("Slate")
            .hp(5)
            .death(Sounds.deathExplosion)
            .sides(
               EntSidesBlobBig.rockFist.val(7),
               EntSidesBlobBig.rockFist.val(7),
               EntSidesBlobBig.rockFist.val(7),
               EntSidesBlobBig.rockFist.val(7),
               EntSidesBlobBig.rockSpray.val(2),
               EntSidesBlobBig.rockSpray.val(2)
            )
            .trait(new Trait(new StoneSpecialHp(PipLoc.all())))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.big)
            .name("Troll")
            .hp(15)
            .death(Sounds.deathBig)
            .sides(
               EntSidesBlobBig.club.val(3),
               EntSidesBlobBig.club.val(3),
               EntSidesBlobBig.stomp.val(2),
               EntSidesBlobBig.stomp.val(2),
               EntSidesBlobBig.club.val(2),
               EntSidesBlobBig.poisonAura.val(1)
            )
            .trait(new Trait(new Regen(1)))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.big)
            .name("Caw")
            .hp(7)
            .death(Sounds.deathHorse)
            .max(4)
            .sides(
               EntSidesBlobBig.peck.val(7),
               EntSidesBlobBig.peck.val(7),
               EntSidesBlobBig.peck.val(7),
               EntSidesBlobBig.peck.val(7),
               EntSidesBlobBig.claw.val(3),
               EntSidesBlobBig.claw.val(3)
            )
            .trait(new Trait(new OnDamage(new EffBill().self().buff(new Buff(1, new BackRow(false))).bEff(), true, new LinkEvent(SoundSnapshotEvent.flap))))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.big)
            .name("Magrat")
            .hp(7)
            .death(Sounds.deathWeird)
            .makeUnique()
            .sides(
               EntSidesBlobBig.decay.val(3),
               EntSidesBlobBig.decay.val(3),
               EntSidesBlobBig.decay.val(3),
               EntSidesBlobBig.decay.val(3),
               EntSidesBlobBig.poisonApple.val(2),
               EntSidesBlobBig.poisonApple.val(2)
            )
            .trait(
               new Trait(
                  new TriggerHP(
                     new EffBill().damage(2).targetType(TargetingType.Bot).bEff(),
                     Arrays.asList(PanelHighlightEvent.witchHex, new LinkEvent(SoundSnapshotEvent.witchHex)),
                     null,
                     Images.hp_arrow_down,
                     Colours.orange,
                     witchPip
                  )
               )
            )
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.big)
            .name("Gytha")
            .hp(7)
            .death(Sounds.deathWeird)
            .makeUnique()
            .sides(
               EntSidesBlobBig.broomstick.val(7),
               EntSidesBlobBig.broomstick.val(7),
               EntSidesBlobBig.broomstick.val(7),
               EntSidesBlobBig.broomstick.val(7),
               EntSidesBlobBig.chillingGaze.val(1),
               EntSidesBlobBig.chillingGaze.val(1)
            )
            .trait(
               new Trait(
                  new TriggerHP(
                     new EffBill().damage(2).targetType(TargetingType.Mid).bEff(),
                     Arrays.asList(PanelHighlightEvent.witchHex, new LinkEvent(SoundSnapshotEvent.witchHex)),
                     null,
                     Images.hp_arrow_left,
                     Colours.orange,
                     witchPip
                  )
               )
            )
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.big)
            .name("Agnes")
            .hp(7)
            .death(Sounds.deathWeird)
            .makeUnique()
            .sides(
               EntSidesBlobBig.batSwarm.val(1),
               EntSidesBlobBig.batSwarm.val(1),
               EntSidesBlobBig.batSwarm.val(1),
               EntSidesBlobBig.batSwarm.val(1),
               EntSidesBlobBig.howl.val(1),
               EntSidesBlobBig.howl.val(1)
            )
            .trait(
               new Trait(
                  new TriggerHP(
                     new EffBill().damage(2).targetType(TargetingType.Top).bEff(),
                     Arrays.asList(PanelHighlightEvent.witchHex, new LinkEvent(SoundSnapshotEvent.witchHex)),
                     null,
                     Images.hp_arrow_up,
                     Colours.orange,
                     witchPip
                  )
               )
            )
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.big)
            .name("Basilisk")
            .hp(12)
            .death(Sounds.deathHorse)
            .max(2)
            .sides(
               EntSidesBlobBig.claw.val(2),
               EntSidesBlobBig.claw.val(2),
               EntSidesBlobBig.poison.val(2),
               EntSidesBlobBig.poison.val(2),
               EntSidesBlobBig.claw.val(2),
               EntSidesBlobBig.chillingGaze.val(1)
            )
            .trait(new PetrifyOnAttack())
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.big)
            .name("Hydra")
            .hp(20)
            .death(Sounds.deathHorse)
            .max(2)
            .sides(
               EntSidesBlobBig.claw.val(5),
               EntSidesBlobBig.claw.val(5),
               EntSidesBlobBig.bite.val(10),
               EntSidesBlobBig.bite.val(10),
               EntSidesBlobBig.claw.val(5),
               EntSidesBlobBig.chillingGaze.val(2)
            )
            .trait(new DeathAfterNumHits(5))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.big)
            .name("Cyclops")
            .hp(15)
            .death(Sounds.deathHorse)
            .max(2)
            .sides(
               EntSidesBlobBig.stomp.val(3),
               EntSidesBlobBig.stomp.val(3),
               EntSidesBlobBig.club.val(4),
               EntSidesBlobBig.club.val(4),
               EntSidesBlobBig.stomp.val(2),
               EntSidesBlobBig.stomp.val(2)
            )
            .trait(
               new TriggerHP(
                  new EffBill().self().buff(new Buff(1, new Stunned())).bEff(),
                  new LinkEvent(SoundSnapshotEvent.clang),
                  Colours.light,
                  new PipLoc(PipLocType.EveryFraction, 2)
               )
            )
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.big)
            .name("Bramble")
            .hp(11)
            .death(Sounds.deathBig)
            .sides(
               EntSidesBlobBig.bite.val(5),
               EntSidesBlobBig.bite.val(5),
               EntSidesBlobBig.club.val(2),
               EntSidesBlobBig.club.val(2),
               EntSidesBlobBig.poison.val(2),
               EntSidesBlobBig.poison.val(2)
            )
            .trait(
               new Trait(
                  new TriggerPersonalToGlobal(new GlobalHeroes(new AffectSides(new AddKeyword(Keyword.singleUse)).monsterPassivePriority()), "bark"),
                  new CalcStats(1.4F, 4.0F),
                  true
               )
            )
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.big)
            .name("Banshee")
            .hp(10)
            .death(Sounds.deathBig)
            .max(1)
            .sides(
               EntSidesBlobBig.decay.val(5),
               EntSidesBlobBig.decay.val(5),
               EntSidesBlobBig.weaken.val(4),
               EntSidesBlobBig.weaken.val(4),
               EntSidesBlobBig.poison.val(3),
               EntSidesBlobBig.poison.val(3)
            )
            .trait(
               new Trait(
                  new AfterUseAbility(
                     0,
                     new EffBill().group().damage(1).bEff(),
                     new EffBill().self().event(ChatStateEvent.BansheeWail).bEff(),
                     new EffBill().snapshotEvent(SoundSnapshotEvent.bansheeWail).bEff()
                  ),
                  new CalcStats(2.0F, 3.0F),
                  true
               )
            )
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.huge)
            .name("Slime Queen")
            .hp(13)
            .death(Sounds.deathWeird)
            .makeUnique()
            .sides(
               EntSidesBlobHuge.chomp.val(9),
               EntSidesBlobHuge.slimeTriple.val(4),
               EntSidesBlobHuge.slimeUpDown.val(5),
               EntSidesBlobHuge.slimeUpDown.val(5),
               EntSidesBlobHuge.slimeTriple.val(3),
               EntSidesBlobHuge.slimeTriple.val(3)
            )
            .trait(new Trait(new TriggerHPSummon("slimer", 1, SoundSnapshotEvent.slime, PipLoc.offsetEvery(5, 4))))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.huge)
            .name("Lich")
            .hp(20)
            .death(Sounds.deathDemon)
            .sides(
               EntSidesBlobHuge.petrifyStaff.val(2),
               EntSidesBlobHuge.petrifyStaff.val(2),
               EntSidesBlobHuge.summonBones.val(2),
               EntSidesBlobHuge.summonBones.val(2),
               EntSidesBlobHuge.chill.val(1),
               EntSidesBlobHuge.chill.val(1)
            )
            .trait(new Trait(new TriggerHPSummon("bones", 1, SoundSnapshotEvent.resurrectSound, PipLoc.offsetEvery(5, 4))))
            .trait(Trait.BACK_ROW)
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.huge)
            .name("Troll King")
            .hp(20)
            .death(Sounds.deathBig)
            .makeUnique()
            .sides(
               EntSidesBlobHuge.club.val(5),
               EntSidesBlobHuge.club.val(5),
               EntSidesBlobHuge.stomp.val(3),
               EntSidesBlobHuge.stomp.val(3),
               EntSidesBlobHuge.club.val(5),
               EntSidesBlobHuge.poisonBreath.val(2)
            )
            .trait(new Trait(new Regen(2)))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.huge)
            .name("Basalt")
            .hp(35)
            .death(Sounds.deathBig)
            .makeUnique()
            .sides(
               EntSidesBlobHuge.chainFlank.val(4),
               EntSidesBlobHuge.chainFlank.val(4),
               EntSidesBlobHuge.deathBeam.val(9),
               EntSidesBlobHuge.deathBeam.val(9),
               EntSidesBlobHuge.summonSlate.val(1),
               EntSidesBlobHuge.summonSlate.val(1)
            )
            .trait(new Chip(1))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.huge)
            .name("Tarantus")
            .hp(25)
            .death(Sounds.deathAlien)
            .makeUnique()
            .sides(
               EntSidesBlobHuge.devour.val(12),
               EntSidesBlobHuge.devour.val(12),
               EntSidesBlobHuge.infect.val(3),
               EntSidesBlobHuge.infect.val(3),
               EntSidesBlobHuge.summonSpider.val(3),
               EntSidesBlobHuge.summonSpider.val(2)
            )
            .trait(
               new Trait(
                  new TriggerHP(
                     new EffBill().kill().targetType(TargetingType.Top).bEff(), Images.hp_arrow_up, Colours.orange, new PipLoc(PipLocType.Specific, 9)
                  )
               )
            )
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.huge)
            .name("Rotten")
            .hp(23)
            .death(Sounds.deathDemon)
            .makeUnique()
            .sides(
               EntSidesBlobHuge.chomp.val(6),
               EntSidesBlobHuge.chomp.val(6),
               EntSidesBlobHuge.summonImps.val(1),
               EntSidesBlobHuge.summonImps.val(1),
               EntSidesBlobHuge.poisonBreath.val(1),
               EntSidesBlobHuge.poisonBreath.val(1)
            )
            .trait(new Plague())
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.huge)
            .name("Hexia")
            .hp(30)
            .death(Sounds.deathDemon)
            .makeUnique()
            .sides(
               EntSidesBlobHuge.tentacle.val(7),
               EntSidesBlobHuge.tentacle.val(7),
               EntSidesBlobHuge.petrifyStaff.val(4),
               EntSidesBlobHuge.petrifyStaff.val(4),
               EntSidesBlobHuge.summonDemon.val(1),
               EntSidesBlobHuge.summonDemon.val(1)
            )
            .trait(new Trait(new Mirror()))
            .trait(new Trait(new OnSpendAbilityCost()))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.huge)
            .name("Dragon")
            .hp(40)
            .death(Sounds.deathDragonLong)
            .sides(
               EntSidesBlobHuge.flame.val(5),
               EntSidesBlobHuge.flame.val(5),
               EntSidesBlobHuge.chomp.val(15),
               EntSidesBlobHuge.chomp.val(15),
               EntSidesBlobHuge.poisonBreath.val(3),
               EntSidesBlobHuge.poisonBreath.val(3)
            )
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.huge)
            .name("Baron")
            .hp(25)
            .death(Sounds.deathWeird)
            .makeUnique()
            .sides(
               EntSidesBlobHuge.inflictExert.val(2),
               EntSidesBlobHuge.inflictExert.val(2),
               EntSidesBlobHuge.summonBones.val(2),
               EntSidesBlobHuge.summonBones.val(2),
               EntSidesBlobHuge.inflictExert.val(2),
               EntSidesBlobHuge.poisonBreath.val(1)
            )
            .trait(
               new Trait(new TriggerHP(new EffBill().mana(1).bEff(), new ArrayList<>(), new ManaGainEvent(1, "Baron"), Colours.blue, PipLoc.offsetEvery(2, 1)))
            )
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.huge)
            .name("Sarcophagus")
            .hp(15)
            .death(Sounds.deathExplosion)
            .sides(
               EntSidesBlobHuge.chomp.val(7),
               EntSidesBlobHuge.chomp.val(7),
               EntSidesBlobHuge.slimeTriple.val(3),
               EntSidesBlobHuge.slimeTriple.val(3),
               EntSidesBlobHuge.summonSpider.val(2),
               EntSidesBlobHuge.summonBones.val(2)
            )
            .trait(new StoneSpecialHp(new PipLoc(PipLocType.RightmostN, 3)))
            .trait(new Chest(3, 3, 5))
            .trait(new TriggerPersonalToGlobal(new GlobalFleeAvoid()), new CalcStats(0.0F, 0.0F))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.huge)
            .name("Bell")
            .hp(21)
            .death(Sounds.deathWeird)
            .makeUnique()
            .sides(
               EntSidesBlobHuge.deafen.val(3),
               EntSidesBlobHuge.deafen.val(3),
               EntSidesBlobHuge.deafen.val(2),
               EntSidesBlobHuge.deafen.val(2),
               EntSidesBlobHuge.summonImps.val(1),
               EntSidesBlobHuge.summonImps.val(1)
            )
            .trait(new Trait(new Gong()))
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.huge)
            .name("Madness")
            .hp(14)
            .death(Sounds.deathHorse)
            .sides(
               EntSidesBlobHuge.chomp.val(7),
               EntSidesBlobHuge.chomp.val(7),
               EntSidesBlobHuge.chomp.val(7),
               EntSidesBlobHuge.chomp.val(7),
               EntSidesBlobHuge.wendigoSkull.val(5),
               EntSidesBlobHuge.wendigoSkull.val(5)
            )
            .trait(
               new Trait(
                  new TriggerPersonalToGlobal(
                        new GlobalTopNonMagic(new AffectSides(new AddKeyword(Keyword.possessed, Keyword.mandatory)).monsterPassivePriority())
                     )
                     .overrideShow(true),
                  new CalcStats(4.0F, 4.0F)
               )
            )
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.huge)
            .name("Inevitable")
            .hp(30)
            .death(Sounds.deathExplosion)
            .makeUnique()
            .sides(
               EntSidesBlobHuge.chompSelfHeal.val(6),
               EntSidesBlobHuge.chompSelfHeal.val(6),
               EntSidesBlobHuge.inflictExert.val(3),
               EntSidesBlobHuge.inflictExert.val(3),
               EntSidesBlobHuge.chainFlank.val(3),
               EntSidesBlobHuge.chainFlank.val(3)
            )
            .trait(new Trait(new GhostHP(PipLoc.offsetEvery(5, 2))))
            .trait(new SimpleKeywordTrait(Keyword.era))
            .trait(new CalcOnly(), new CalcStats(7.0F, 8.0F), false)
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.huge)
            .name("The Hand")
            .hp(45)
            .death(Sounds.deathWeird)
            .makeUnique()
            .sides(
               EntSidesBlobHuge.handKill,
               EntSidesBlobHuge.club.val(10),
               EntSidesBlobHuge.summonSaber.val(3),
               EntSidesBlobHuge.summonSaber.val(3),
               EntSidesBlobHuge.flame.val(7),
               EntSidesBlobHuge.poisonBreath.val(4)
            )
            .trait(
               new Trait(
                  new TriggerPersonalToGlobal(new GlobalHeroes(new AffectSides(new FlatBonus(1)).monsterPassivePriority()), "badge"),
                  new CalcStats(-6.0F, -12.0F),
                  true
               )
            )
            .bEntType()
      );
      MonsterType cawEgg = new MTBill(EntSize.small)
         .name("Caw Egg")
         .hp(4)
         .death(Sounds.deathCute)
         .rarity(Rarity.FIFTH)
         .sides(
            EntSidesBlobSmall.hatchCaw.val(1),
            EntSidesBlobSmall.hatchCaw.val(1),
            EntSidesBlobSmall.blank,
            EntSidesBlobSmall.blank,
            EntSidesBlobSmall.hatchCaw.val(1),
            EntSidesBlobSmall.blank
         )
         .bEntType();
      result.add(cawEgg);
      result.add(
         new MTBill(EntSize.small)
            .name("Seed")
            .hp(1)
            .death(Sounds.deathCute)
            .rarity(Rarity.TENTH)
            .min(2)
            .sides(EntSidesBlobSmall.growThorn.val(1), EntSidesBlobSmall.growThorn.val(1), EntSidesBlobSmall.growThorn.val(1))
            .trait(new CalcOnly(), new CalcStats(0.0F, 0.15F), false)
            .bEntType()
      );
      result.add(
         new MTBill(EntSize.reg)
            .name("Dragon Egg")
            .hp(6)
            .death(Sounds.deathCute)
            .rarity(Rarity.FIFTIETH)
            .sides(ESB.hatchDragon.val(1), ESB.hatchDragon.val(1), ESB.blank, ESB.blank, ESB.blank, ESB.blank)
            .trait(new CopyOtherPowerEstimate(cawEgg, 1.82F), false)
            .bEntType()
      );
      return result;
   }
}
