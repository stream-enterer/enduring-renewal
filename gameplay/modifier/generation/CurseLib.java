package com.tann.dice.gameplay.modifier.generation;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.GSCConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ParamCondition;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellLib;
import com.tann.dice.gameplay.fightLog.event.snapshot.SoundSnapshotEvent;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierPickContext;
import com.tann.dice.gameplay.modifier.ModifierUtils;
import com.tann.dice.gameplay.modifier.generation.tierMaker.TierMakerPreset;
import com.tann.dice.gameplay.modifier.modBal.ModTierUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.GlobalDescribeOnly;
import com.tann.dice.gameplay.trigger.global.GlobalMaxMana;
import com.tann.dice.gameplay.trigger.global.chance.GlobalRarity;
import com.tann.dice.gameplay.trigger.global.chance.Rarity;
import com.tann.dice.gameplay.trigger.global.changeHero.GlobalChangeHeroAll;
import com.tann.dice.gameplay.trigger.global.changeHero.effects.ChangeHeroEffect;
import com.tann.dice.gameplay.trigger.global.changeHero.effects.LevelupHero;
import com.tann.dice.gameplay.trigger.global.eff.GlobalEndTurnEff;
import com.tann.dice.gameplay.trigger.global.eff.GlobalStartTurnEff;
import com.tann.dice.gameplay.trigger.global.item.GlobalItemQuality;
import com.tann.dice.gameplay.trigger.global.item.GlobalItemQuantity;
import com.tann.dice.gameplay.trigger.global.item.GlobalLevelupQuantity;
import com.tann.dice.gameplay.trigger.global.level.GlobalAddMonster;
import com.tann.dice.gameplay.trigger.global.level.GlobalSetMonsters;
import com.tann.dice.gameplay.trigger.global.linked.GlobalAllEntitiesRestricted;
import com.tann.dice.gameplay.trigger.global.linked.GlobalAllMonstersExcept;
import com.tann.dice.gameplay.trigger.global.linked.GlobalEveryNthDice;
import com.tann.dice.gameplay.trigger.global.linked.GlobalHeroTier;
import com.tann.dice.gameplay.trigger.global.linked.GlobalPositional;
import com.tann.dice.gameplay.trigger.global.linked.GlobalSize;
import com.tann.dice.gameplay.trigger.global.linked.GlobalTopNonMagic;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalAllEntities;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalHeroes;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalMonsters;
import com.tann.dice.gameplay.trigger.global.linked.perN.PerDefeatedBossGlobal;
import com.tann.dice.gameplay.trigger.global.phase.GlobalSkipPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.GlobalAddPhase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorModifierPick;
import com.tann.dice.gameplay.trigger.global.roll.GlobalLockDiceLimit;
import com.tann.dice.gameplay.trigger.global.roll.GlobalNotMoreRolls;
import com.tann.dice.gameplay.trigger.global.scaffolding.HeroPosition;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementBeforeBoss;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementBoss;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementEach;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementRange;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.GlobalTurnRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementAll;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementFirst;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementFirstN;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementN;
import com.tann.dice.gameplay.trigger.global.spell.GlobalAbilitiesLimit;
import com.tann.dice.gameplay.trigger.global.spell.GlobalForgetSpell;
import com.tann.dice.gameplay.trigger.global.spell.GlobalSpellCostChange;
import com.tann.dice.gameplay.trigger.global.spell.GlobalSpellKeyword;
import com.tann.dice.gameplay.trigger.personal.IncomingEffBonus;
import com.tann.dice.gameplay.trigger.personal.OnDamage;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.OrMoreCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.TypeCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.FlatBonus;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.MultiplyEffect;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWithBlank;
import com.tann.dice.gameplay.trigger.personal.death.OnDeathEffect;
import com.tann.dice.gameplay.trigger.personal.finalLayer.NumberLimit;
import com.tann.dice.gameplay.trigger.personal.hp.BonusHpPerBase;
import com.tann.dice.gameplay.trigger.personal.hp.MaxHP;
import com.tann.dice.gameplay.trigger.personal.hp.MaxHpSet;
import com.tann.dice.gameplay.trigger.personal.hp.StartDamagedPer;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.GenericStateCondition;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.PersonalConditionLink;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.StateConditionType;
import com.tann.dice.gameplay.trigger.personal.onHit.Spiky;
import com.tann.dice.gameplay.trigger.personal.specialPips.GhostHP;
import com.tann.dice.gameplay.trigger.personal.specialPips.TriggerHPSummon;
import com.tann.dice.gameplay.trigger.personal.specialPips.pipLoc.PipLoc;
import com.tann.dice.gameplay.trigger.personal.specialPips.pipLoc.PipLocType;
import com.tann.dice.gameplay.trigger.personal.specialPips.resistive.ResistSpecialHp;
import com.tann.dice.gameplay.trigger.personal.specialPips.resistive.StoneSpecialHp;
import com.tann.dice.gameplay.trigger.personal.specialPips.resistive.ToughSpecialHp;
import com.tann.dice.gameplay.trigger.personal.startBuffed.StartPoisoned;
import com.tann.dice.gameplay.trigger.personal.startBuffed.StartRegenned;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ModifierPanel;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Tann;
import com.tann.dice.util.tp.TP;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CurseLib {
   public static List<Modifier> makeAll() {
      List<Modifier> result = new ArrayList<>();
      result.addAll(CurseSingles.makeSingles());
      result.addAll(makeAmends());
      result.addAll(makeAllWorseItems());
      result.addAll(makeTriggerPips());
      result.addAll(makeDoom());
      result.addAll(makeRightPlus());
      result.addAll(makeRightTwoPlus());
      result.addAll(makePerBoss());
      result.addAll(makeSkipPhases());
      result.addAll(makeT0Replace());
      result.addAll(makeColumn());
      result.addAll(makeRow());
      result.addAll(makeMonsterFlatBonus());
      result.addAll(makeExposeds());
      result.addAll(makeTypeMaluses());
      result.addAll(makePositionSetHp1());
      result.addAll(makeSmallBonus());
      result.addAll(makeShieldResponse());
      result.addAll(makeCaltrops());
      result.addAll(makeMortality());
      result.addAll(makeBasicChains());
      result.addAll(makeMeta());
      result.addAll(makeNth());
      result.addAll(GlobalSetMonsters.makeDesigned());
      return result;
   }

   private static List<Modifier> makeNth() {
      List<Modifier> result = new ArrayList<>();
      result.add(GlobalEveryNthDice.makeNthKeyword(4, Keyword.death));
      result.add(GlobalEveryNthDice.makeNthKeyword(3, Keyword.death));
      result.add(GlobalEveryNthDice.makeNthKeyword(2, Keyword.death));
      result.add(GlobalEveryNthDice.makeNthKeyword(5, Keyword.pain));
      result.add(GlobalEveryNthDice.makeNthKeyword(3, Keyword.pain));
      result.add(GlobalEveryNthDice.makeNthKeyword(2, Keyword.pain));
      result.add(GlobalEveryNthDice.makeNthKeyword(4, Keyword.singleUse));
      result.add(GlobalEveryNthDice.makeNthKeyword(3, Keyword.singleUse));
      result.add(GlobalEveryNthDice.makeNthKeyword(2, Keyword.singleUse));
      result.add(GlobalEveryNthDice.makeNthKeyword(4, Keyword.exert));
      result.add(GlobalEveryNthDice.makeNthKeyword(3, Keyword.exert));
      result.add(GlobalEveryNthDice.makeNthKeyword(2, Keyword.exert));
      return result;
   }

   public static List<Modifier> makeDoom() {
      return GenUtils.cChain("Doom", new TierMakerPreset(-1, -3, -4, -6, -9, -13, -25), new MMS(Rarity.FIFTH) {
         @Override
         public Global ms(int i) {
            return new GlobalTurnRequirement(new TurnRequirementN(7 - i), new GlobalEndTurnEff(new EffBill().friendly().group().damage(8).bEff()));
         }
      });
   }

   private static List<Modifier> makeBasicChains() {
      List<Modifier> result = new ArrayList<>();
      result.addAll(
         GenUtils.cChain(
            "Quick Nap",
            new TierMakerPreset(-17, -40, -90),
            new MMS() {
               @Override
               public Global ms(int i) {
                  return new GlobalTurnRequirement(
                     (TurnRequirement)(i == 0 ? new TurnRequirementFirst() : new TurnRequirementFirstN(i + 1)),
                     new GlobalHeroes(new AffectSides(new ReplaceWithBlank(ChoosableType.Modifier)).buffPriority())
                  );
               }
            }
         )
      );
      result.addAll(GenUtils.cChain("Slippery Dice", new TierMakerPreset(-1, -2, -4, -6), new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalLockDiceLimit(3 - i);
         }
      }));
      result.add(new Modifier(-2.0F, "Hefty", new GlobalTopNonMagic(new AffectSides(new AddKeyword(Keyword.heavy)))));
      result.addAll(GenUtils.cChain("Heavy Dice", new TierMakerPreset(-2, -3, -8), new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalNotMoreRolls(3 - i);
         }
      }));
      result.addAll(GenUtils.cChain("Tough Hp", 4, new TierMaker() {
         @Override
         public float makeTier(int i) {
            return ModTierUtils.getBonusMonsterHpFlat((float)(Math.pow(i + 1, 0.9F) * 1.22F));
         }
      }, new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalMonsters(new ToughSpecialHp(2, new PipLoc(PipLocType.LeftmostN, i + 1)));
         }
      }));
      result.addAll(GenUtils.cChain("Big Hitter", 6, new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalMonsters(new AffectSides(new OrMoreCondition((int)((10 - i) * 1.1F)), new MultiplyEffect(2)));
         }
      }));
      result.addAll(GenUtils.cChain("Death Shield", 2, new TierMaker() {
         @Override
         public float makeTier(int i) {
            return ModTierUtils.monsterShieldEachTurn(0.38F * (i + 1) + 0.0F);
         }
      }, new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalMonsters(new OnDeathEffect(new EffBill().shield(i + 1).group().bEff()));
         }
      }));
      result.addAll(
         GenUtils.cChain(
            "Sickly",
            new TierMakerPreset(-2, -4, -6),
            new MMS() {
               @Override
               public Global ms(int i) {
                  return new GlobalAllEntitiesRestricted(
                     true, new ParamCondition(ParamCondition.ParamConType.OrLessHp, i + 1), new AffectSides(new AddKeyword(Keyword.pain)).buffPriority()
                  );
               }
            }
         )
      );
      result.addAll(GenUtils.cChain("Mana Debt", 7, new MMS() {
         @Override
         public Global ms(int i) {
            switch (i) {
               case 0:
               default:
                  return new GlobalSpellCostChange(1, 3);
               case 1:
                  return new GlobalSpellCostChange(1, 1);
               case 2:
                  return new GlobalSpellCostChange(3, 3);
               case 3:
                  return new GlobalSpellCostChange(2, 1);
               case 4:
                  return new GlobalSpellCostChange(3, 1);
               case 5:
                  return new GlobalSpellCostChange(5, 2);
               case 6:
                  return new GlobalSpellCostChange(4, 1);
            }
         }
      }));
      result.addAll(GenUtils.cChain("Tower", 4, new MMS(Rarity.THIRD) {
         @Override
         public Global ms(int i) {
            return new GlobalSize(EntSize.huge, new MaxHP((int)Math.round(Math.pow(2.7F * (i + 1), 1.18F))));
         }
      }));
      result.addAll(GenUtils.cChain("Bottom Poison", 4, new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalPositional(HeroPosition.BOT, new StartPoisoned(1 + i + i / 3));
         }
      }));
      result.addAll(
         GenUtils.cChain(
            "Skulk",
            new TierMakerPreset(-1, -2, -4),
            new ModMaker() {
               @Override
               public List<Global> make(int level) {
                  return Arrays.asList(
                     new GlobalTurnRequirement(
                        new TurnRequirementN(4 - level - 1),
                        new GlobalStartTurnEff(new EffBill().group().buff(new Buff(1, new AffectSides(new FlatBonus(1)).show(true))).bEff())
                     )
                  );
               }
            }
         )
      );
      result.addAll(GenUtils.cChain("Slow Spells", 4, new TierMaker() {
         @Override
         public float makeTier(int i) {
            switch (i) {
               case 0:
                  return -5.0F;
               case 1:
                  return -3.0F;
               case 2:
                  return -2.0F;
               case 3:
                  return -1.0F;
               default:
                  return 999.0F;
            }
         }
      }, new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalAbilitiesLimit(i + 1);
         }
      }));
      result.addAll(GenUtils.cChain("Monster Shields", 3, new TierMaker() {
         @Override
         public float makeTier(int i) {
            return ModTierUtils.monsterShieldEachTurn(i + 1);
         }
      }, new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalStartTurnEff(new EffBill().shield(i + 1).enemy().group().bEff());
         }
      }));
      result.addAll(GenUtils.cChain("Ghostly Monsters", new TierMakerPreset(-4, -10), new MMS() {
         @Override
         public Global ms(int i) {
            switch (i) {
               case 0:
                  return new GlobalMonsters(new GhostHP(PipLoc.offsetEvery(5, 3)));
               case 1:
                  return new GlobalMonsters(new GhostHP(PipLoc.all()));
               default:
                  throw new RuntimeException("eep");
            }
         }
      }));
      result.addAll(GenUtils.cChain(2, new NameMaker() {
         @Override
         public String name(int i, List<Global> globals) {
            return 2 + i + " less max mana";
         }
      }, new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalMaxMana(-2 - i);
         }
      }));
      result.addAll(
         GenUtils.cChain(
            "Boss Armour",
            2,
            new MMS() {
               @Override
               public Global ms(int i) {
                  return new GlobalLevelRequirement(
                     new LevelRequirementBoss(),
                     new GlobalTurnRequirement(new TurnRequirementFirst(), new GlobalStartTurnEff(new EffBill().shield((i + 1) * 2).enemy().group().bEff()))
                  );
               }
            }
         )
      );
      result.addAll(GenUtils.cChain("Rushed", new TierMakerPreset(-1, -2), new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalItemQuantity(-(i + 1));
         }
      }));
      result.addAll(GenUtils.cChain("Hurried", new TierMakerPreset(-1, -2), new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalLevelupQuantity(-(i + 1));
         }
      }));
      result.addAll(GenUtils.cChain("Tunnel Vision", new TierMakerPreset(-2, -4), new ModMaker() {
         @Override
         public List<Global> make(int i) {
            return Arrays.asList(new GlobalLevelupQuantity(-(i + 1)), new GlobalItemQuantity(-(i + 1)));
         }
      }));
      result.addAll(GenUtils.cChain("Arthritis", 2, new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalHeroTier(3, new MaxHP(-i - 1));
         }
      }));
      final List<TP<Integer, Integer>> data = Arrays.asList(
         new TP<>(1, 7),
         new TP<>(1, 5),
         new TP<>(2, 7),
         new TP<>(1, 4),
         new TP<>(3, 6),
         new TP<>(1, 3),
         new TP<>(2, 4),
         new TP<>(3, 5),
         new TP<>(1, 2),
         new TP<>(3, 4),
         new TP<>(2, 3)
      );
      result.addAll(GenUtils.cChain("Monster HP Per", data.size(), new MMS() {
         @Override
         public Global ms(int i) {
            TP<Integer, Integer> datum = data.get(i);
            return new GlobalMonsters(new BonusHpPerBase(datum.a, datum.b));
         }
      }));
      result.addAll(makeMonsterFlatHP(false));
      result.addAll(GenUtils.cChain("Spiky Monsters", new TierMakerPreset(-1, -2, -3, -4, -6, -13), new MMS() {
         @Override
         public Global ms(int i) {
            switch (i) {
               case 0:
                  return new GlobalAllEntitiesRestricted(false, new ParamCondition(ParamCondition.ParamConType.OrMoreMaxHp, 20), new Spiky(1));
               case 1:
                  return new GlobalAllEntitiesRestricted(false, new ParamCondition(ParamCondition.ParamConType.OrMoreMaxHp, 8), new Spiky(1));
               case 2:
                  return new GlobalAllEntitiesRestricted(false, new ParamCondition(ParamCondition.ParamConType.OrMoreMaxHp, 5), new Spiky(1));
               case 3:
                  return new GlobalMonsters(new Spiky(1));
               case 4:
                  return new GlobalMonsters(new Spiky(2));
               case 5:
                  return new GlobalMonsters(new Spiky(5));
               default:
                  throw new RuntimeException();
            }
         }
      }));
      result.addAll(GenUtils.cChain("Start Damaged", 8, new MMS() {
         @Override
         public Global ms(int i) {
            Personal sdp;
            switch (i) {
               case 0:
                  sdp = new StartDamagedPer(6);
                  break;
               case 1:
                  sdp = new StartDamagedPer(4);
                  break;
               case 2:
                  sdp = new StartDamagedPer(2, 6);
                  break;
               case 3:
                  sdp = new StartDamagedPer(1, 3);
                  break;
               case 4:
                  sdp = new StartDamagedPer(2, 4);
                  break;
               case 5:
                  sdp = new StartDamagedPer(2);
                  break;
               case 6:
                  sdp = new StartDamagedPer(2, 3);
                  break;
               case 7:
                  sdp = new StartDamagedPer(3, 4);
                  break;
               default:
                  sdp = new StartDamagedPer(420);
            }

            return new GlobalHeroes(sdp);
         }
      }));
      result.addAll(GenUtils.cChain("Wurst", 5, new MMS() {
         @Override
         public Global ms(int i) {
            switch (i) {
               case 0:
                  return new GlobalTurnRequirement(2, new GlobalForgetSpell(SpellLib.BURST));
               case 1:
                  return new GlobalSpellKeyword("burst", Keyword.cooldown);
               case 2:
                  return new GlobalSpellKeyword("burst", Keyword.singleCast);
               case 3:
                  return new GlobalTurnRequirement(new TurnRequirementFirstN(2), new GlobalForgetSpell(SpellLib.BURST));
               case 4:
                  return new GlobalForgetSpell(SpellLib.BURST);
               default:
                  throw new RuntimeException();
            }
         }
      }));
      result.addAll(GenUtils.cChain("Armour", 5, new MMS() {
         @Override
         public Global ms(int i) {
            int shieldAmt = i != 0 && i != 1 ? (i == 3 ? 2 : 3) : 1;
            int turn = i != 0 && i != 2 ? 1 : 2;
            return new GlobalTurnRequirement(new TurnRequirementN(turn), new GlobalStartTurnEff(new EffBill().shield(shieldAmt).enemy().group().bEff()));
         }
      }));
      result.add(
         new Modifier(-2.0F, "Restless Bones", new GlobalHeroes(new OnDeathEffect(new EffBill().summon("bones", 2).bEff(), null, false))).rarity(Rarity.THIRD)
      );
      result.addAll(GenUtils.cChain("Monster Left", 5, new MMS(Rarity.HALF) {
         @Override
         public Global ms(int i) {
            return new GlobalMonsters(new AffectSides(SpecificSidesType.Left, new FlatBonus(i + 1)));
         }
      }));
      result.addAll(GenUtils.cChain("Sandstorm", new TierMakerPreset(-6, -12, -17), new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalTurnRequirement(new TurnRequirementAll(), new GlobalEndTurnEff(new EffBill().friendly().group().damage(i + 1)));
         }
      }));
      result.addAll(GenUtils.cChain("Lightning", new TierMakerPreset(-1, -3, -4), new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalTurnRequirement(new TurnRequirementN(3), new GlobalEndTurnEff(new EffBill().friendly().group().damage(i + 1)));
         }
      }));
      result.addAll(makeAllPoisoned());
      return result;
   }

   public static List<Modifier> makeMonsterFlatHP(final boolean bless) {
      return GenUtils.cChain(bless ? "Monster Hp Down" : "Monster HP", 5, new TierMaker() {
         @Override
         public float makeTier(int i) {
            return ModTierUtils.getBonusMonsterHpFlat((i + 1) * (bless ? -1 : 1));
         }
      }, new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalMonsters(new MaxHP((i + 1) * (bless ? -1 : 1)));
         }
      });
   }

   public static List<Modifier> makeAllPoisoned() {
      return GenUtils.cChain("All Poisoned", 5, new TierMaker() {
         @Override
         public float makeTier(int index) {
            return ModTierUtils.startPoisoned(index + 1) * 5.0F;
         }
      }, new MMS() {
         @Override
         public Global ms(int i) {
            int poisonAmt = 1 + i;
            return new GlobalHeroes(new StartPoisoned(poisonAmt));
         }
      });
   }

   public static List<Modifier> makeAllWorseItems() {
      return GenUtils.cChain("Worse Items", new TierMakerPreset(-1.2F, -3.3F, -7.0F, -11.2F), new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalItemQuality(-i - 1);
         }
      });
   }

   public static Collection<Modifier> makeRightPlus() {
      return GenUtils.cChain("Monster Right", 2, new TierMaker() {
         @Override
         public float makeTier(int index) {
            return (index + 1) * -2;
         }
      }, new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalMonsters(new AffectSides(SpecificSidesType.RightMost, new FlatBonus(i + 1)));
         }
      });
   }

   public static Collection<Modifier> makeRightTwoPlus() {
      return GenUtils.cChain("Monster Rights", 3, new TierMaker() {
         @Override
         public float makeTier(int index) {
            return (index + 1) * -3;
         }
      }, new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalMonsters(new AffectSides(SpecificSidesType.RightTwo, new FlatBonus(i + 1)));
         }
      });
   }

   public static Collection<Modifier> makeColumn() {
      return GenUtils.cChain("Monster Column", 2, new TierMaker() {
         @Override
         public float makeTier(int index) {
            return (index + 1) * -5;
         }
      }, new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalAllEntities(false, new AffectSides(SpecificSidesType.Column, new FlatBonus(i + 1)));
         }
      });
   }

   public static Collection<Modifier> makeRow() {
      return GenUtils.cChain("Monster Row", 2, new TierMaker() {
         @Override
         public float makeTier(int index) {
            return ModTierUtils.monsterPlus(index + 1) * 4.0F / 6.0F * 1.06F;
         }
      }, new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalAllEntities(false, new AffectSides(SpecificSidesType.Row, new FlatBonus(i + 1)));
         }
      });
   }

   public static List<Modifier> makeMonsterFlatBonus() {
      List<Modifier> result = new ArrayList<>();

      for (int i = 0; i < 3; i++) {
         int bonus = i + 1;
         Global g = new GlobalAllEntities(false, new AffectSides(new FlatBonus(bonus)));
         result.add(new Modifier(ModTierUtils.monsterPlus(bonus), ModifierUtils.makeName("Monster Bonus", i, g), g));
      }

      return result;
   }

   public static List<Modifier> makeExposeds() {
      String name = "Exposed";
      Personal conLink = new PersonalConditionLink(
         new GSCConditionalRequirement(new GenericStateCondition(StateConditionType.GainedNoShields), true), new IncomingEffBonus(2, true, EffType.Damage)
      );
      GlobalRarity rarity = GlobalRarity.fromRarity(Rarity.TWO_THIRDS);
      return Arrays.asList(
         new Modifier(-1.0F, name + " middle", new GlobalPositional(HeroPosition.MIDDLE, conLink), rarity),
         new Modifier(-3.0F, name + " edges", new GlobalPositional(HeroPosition.TOP_AND_BOTTOM, conLink), rarity),
         new Modifier(-9.0F, name, new GlobalHeroes(conLink), rarity)
      );
   }

   public static List<Modifier> makeAmends() {
      return GenUtils.cChain(
         "Monster Regen",
         5,
         new MMS() {
            @Override
            public Global ms(int i) {
               Personal p = new StartRegenned(i / 3 + 1);
               if (i == 0) {
                  return new GlobalAllEntitiesRestricted(false, new ParamCondition(ParamCondition.ParamConType.OrLessMaxHp, 5), p);
               } else {
                  i--;
                  return (Global)(i % 2 == 0
                     ? new GlobalAllEntitiesRestricted(false, new ParamCondition(ParamCondition.ParamConType.OrLessMaxHp, 10), p)
                     : new GlobalMonsters(p));
               }
            }
         }
      );
   }

   public static List<Modifier> makePerBoss() {
      List<Modifier> result = new ArrayList<>();
      result.add(new Modifier(-7.0F, "Reanimated Bosses", new PerDefeatedBossGlobal(new GlobalAddMonster(MonsterTypeLib.byName("bones")))));
      result.add(new Modifier(-15.0F, "Boss Spirits", new PerDefeatedBossGlobal(new GlobalAddMonster(MonsterTypeLib.byName("ghost")))));
      return result;
   }

   public static List<Modifier> makeSkipPhases() {
      List<Modifier> result = new ArrayList<>();
      String prefix = "Skip rewards ";
      Rarity r = Rarity.THIRD;
      GlobalSkipPhase gsp = new GlobalSkipPhase();
      result.add(new Modifier(-2.0F, prefix + 4, new GlobalLevelRequirement(new LevelRequirementRange(4), gsp)).rarity(r));
      result.add(new Modifier(-2.0F, prefix + "end", new GlobalLevelRequirement(new LevelRequirementRange(19, 20), gsp)).rarity(r));
      result.add(new Modifier(-15.0F, prefix + "later", new GlobalLevelRequirement(new LevelRequirementRange(11, 20), gsp)).rarity(r));
      result.add(new Modifier(-40.0F, "Skip Rewards", gsp).rarity(r));
      return result;
   }

   public static List<Modifier> makeT0Replace() {
      List<Modifier> result = new ArrayList<>();
      String prefix = "Delevel";
      ChangeHeroEffect dh = new LevelupHero(-1);
      result.add(new Modifier(-16.5F, prefix, new GlobalChangeHeroAll(dh)));
      return result;
   }

   public static List<Modifier> makeTriggerPips() {
      new ArrayList();
      return Arrays.asList(
         new Modifier(-18.0F, "Stone Monsters", new GlobalMonsters(new StoneSpecialHp(PipLoc.all()))),
         new Modifier(-5.0F, "Sparkly Monsters", new GlobalMonsters(new ResistSpecialHp(ResistSpecialHp.DamageType.Spell, new PipLoc(PipLocType.Specific, 2)))),
         new Modifier(
            -3.0F,
            "Slimedemic",
            new GlobalAllMonstersExcept(
               MonsterTypeLib.byName("slimelet"), new TriggerHPSummon("slimelet", 1, new SoundSnapshotEvent(Sounds.slime), new PipLoc(PipLocType.Specific, 2))
            ),
            GlobalRarity.fromRarity(Rarity.HALF)
         ),
         new Modifier(
            -22.0F, "Ghoststone", new GlobalMonsters(new StoneSpecialHp(PipLoc.offsetEvery(2, 0))), new GlobalMonsters(new GhostHP(PipLoc.offsetEvery(2, 1)))
         )
      );
   }

   public static List<Modifier> makeTypeMaluses() {
      List<Modifier> result = new ArrayList<>();
      String suffix = " Reduced";
      GlobalRarity gr = GlobalRarity.fromRarity(Rarity.TENTH);

      for (TP<EffType, Float> tp : new TP[]{
         new TP<>(EffType.Mana, -5.0F), new TP<>(EffType.Damage, -6.0F), new TP<>(EffType.Shield, -3.0F), new TP<>(EffType.Heal, -2.0F)
      }) {
         result.add(new Modifier(tp.b, tp.a + suffix, new GlobalHeroes(new AffectSides(new TypeCondition(tp.a), new FlatBonus(-1))), gr));
      }

      return result;
   }

   public static List<Modifier> makePositionSetHp1() {
      List<Modifier> result = new ArrayList<>();
      String suffix = " 1 hp";
      GlobalRarity r = GlobalRarity.fromRarity(Rarity.FIFTH);

      for (HeroPosition hp : new HeroPosition[]{HeroPosition.BOT, HeroPosition.TOP, HeroPosition.MIDDLE}) {
         result.add(
            new Modifier(ModTierUtils.setSingleHeroOneHp(1.0F), hp.describe().replaceAll(" hero", "") + suffix, new GlobalPositional(hp, new MaxHpSet(1)), r)
         );
      }

      return result;
   }

   public static List<Modifier> makeSmallBonus() {
      return GenUtils.cChain("Small Bonus", new TierMakerPreset(-2, -4, -5), new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalSize(EntSize.small, new AffectSides(new FlatBonus(i + 1)));
         }
      });
   }

   public static List<Modifier> makeShieldResponse() {
      return GenUtils.cChain("Shield Response", 2, new TierMaker() {
         @Override
         public float makeTier(int i) {
            return ModTierUtils.monsterShieldEachTurn(0.9F * (i + 1));
         }
      }, new MMS() {
         @Override
         public Global ms(int i) {
            return new GlobalMonsters(new OnDamage(new EffBill().self().shield(i + 1).bEff(), false, null).overrideShow(false));
         }
      });
   }

   public static List<Modifier> makeCaltrops() {
      List<Modifier> result = new ArrayList<>();
      String prefix = "Caltrops";

      for (SpecificSidesType value : Arrays.asList(SpecificSidesType.RightMost, SpecificSidesType.Left, SpecificSidesType.Bot, SpecificSidesType.RightTwo)) {
         if (!value.isWeird()) {
            float tier = ModTierUtils.blanked(value.getFactor() * 5.0F * 0.8F) + value.sideIndices.length * -3.2F;
            String name = value.name() + " " + prefix;
            result.add(new Modifier(tier, name, new GlobalHeroes(new AffectSides(value, new ReplaceWith(ESB.dmgSelfCantrip.val(1))))));
         }
      }

      return result;
   }

   public static List<Modifier> makeMortality() {
      List<Modifier> result = new ArrayList<>();
      int index = 0;
      int max = 12;

      for (int i = 12; i > 0; i--) {
         float maxStr = 0.6F;
         float mortalFactor = Tann.niceTerp(i - 1, 11.0F, 0.6F, 0.34F);
         float floatTier = ModTierUtils.deadHero(0.6F - mortalFactor) * 5.0F;
         if (i <= 8) {
            floatTier *= 1.0F + 0.4F * Tann.splang(i, 8.0F, 3.0F);
         }

         if (ModTierUtils.validForTier(floatTier, 0.3F)) {
            Global g = new GlobalHeroes(new NumberLimit(i));
            if (i == 10) {
               g = new GlobalHeroes(new NumberLimit(13));
               floatTier = -1.0F;
            }

            result.add(new Modifier(floatTier, ModifierUtils.makeName("Mortal", index++, g), g).rarity(Rarity.TWENTIETH));
         }
      }

      return result;
   }

   private static List<Modifier> makeMeta() {
      List<Modifier> result = new ArrayList<>();
      result.addAll(
         GenUtils.cChain(
            "Many Curses",
            4,
            new TierMaker() {
               @Override
               public float makeTier(int index) {
                  return CurseDistribution.getEachLevelAdd(-1 - index);
               }
            },
            new MMS() {
               @Override
               public Global ms(int i) {
                  return new GlobalLevelRequirement(
                     new LevelRequirementEach(),
                     new GlobalAddPhase(new PhaseGeneratorModifierPick(3, 1, -1 - i, true, ModifierPickContext.Difficulty_But_Midgame))
                  );
               }
            }
         )
      );
      result.addAll(
         GenUtils.cChain(
            "First 10 Curses",
            4,
            new TierMaker() {
               @Override
               public float makeTier(int index) {
                  return CurseDistribution.getMultLevelUntilEndEnd(1, 10, -1 - index);
               }
            },
            new MMS() {
               @Override
               public Global ms(int i) {
                  return new GlobalLevelRequirement(
                     new LevelRequirementRange(1, 10),
                     new GlobalAddPhase(new PhaseGeneratorModifierPick(3, 1, -1 - i, true, ModifierPickContext.Difficulty_But_Midgame))
                  );
               }
            }
         )
      );
      result.addAll(
         GenUtils.cChain(
            "Early Curses",
            4,
            new TierMaker() {
               @Override
               public float makeTier(int index) {
                  return CurseDistribution.getMultLevelUntilEndEnd(1, 4, -1 - index);
               }
            },
            new MMS() {
               @Override
               public Global ms(int i) {
                  return new GlobalLevelRequirement(
                     new LevelRequirementRange(1, 4),
                     new GlobalAddPhase(new PhaseGeneratorModifierPick(3, 1, -1 - i, true, ModifierPickContext.Difficulty_But_Midgame))
                  );
               }
            }
         )
      );
      result.addAll(
         GenUtils.cChain(
            "Boss Curses",
            4,
            new TierMaker() {
               @Override
               public float makeTier(int index) {
                  return CurseDistribution.getBossLevelsAdd(-1 - index);
               }
            },
            new MMS() {
               @Override
               public Global ms(int i) {
                  return new GlobalLevelRequirement(
                     new LevelRequirementBeforeBoss(),
                     new GlobalAddPhase(new PhaseGeneratorModifierPick(3, 1, -i - 1, true, ModifierPickContext.Difficulty_But_Midgame))
                  );
               }
            }
         )
      );
      result.add(addCurseAfter(8, -5));
      result.add(addCurseAfter(14, -8));
      return result;
   }

   private static Modifier addCurseAfter(int level, int curseValue) {
      float tier = CurseDistribution.getMultLevelAndAfter(level, curseValue);
      return new Modifier(
         tier,
         "Fight " + level + " curse",
         new GlobalLevelRequirement(
            new LevelRequirementRange(level),
            new GlobalAddPhase(new PhaseGeneratorModifierPick(3, 1, curseValue, true, ModifierPickContext.Difficulty_But_Midgame, 1))
         )
      );
   }

   public static Actor makeChooseModifierPanel(float tier) {
      boolean bless = tier > 0.0F;
      String coltag = bless ? "[green]" : "[purple]";
      return new ModifierPanel(new Modifier(tier, bless ? "blessing" : "curse", new GlobalDescribeOnly(coltag + "choose")), false);
   }
}
