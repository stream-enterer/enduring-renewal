package com.tann.dice.gameplay.effect.eff;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.command.DieCommand;
import com.tann.dice.gameplay.fightLog.command.TargetableCommand;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.combatEffects.BasicCombatEffectController;
import com.tann.dice.screens.dungeon.panels.combatEffects.BlankEffectActor;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffect;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectActor;
import com.tann.dice.screens.dungeon.panels.combatEffects.ParallelCombatEffectController;
import com.tann.dice.screens.dungeon.panels.combatEffects.batSwarm.BatSwarmController;
import com.tann.dice.screens.dungeon.panels.combatEffects.beam.BeamActor;
import com.tann.dice.screens.dungeon.panels.combatEffects.bite.BiteActor;
import com.tann.dice.screens.dungeon.panels.combatEffects.cross.CrossActor;
import com.tann.dice.screens.dungeon.panels.combatEffects.crush.CrushActor;
import com.tann.dice.screens.dungeon.panels.combatEffects.dragonBreath.DragonBreathController;
import com.tann.dice.screens.dungeon.panels.combatEffects.flame.FlameGroup;
import com.tann.dice.screens.dungeon.panels.combatEffects.gaze.GazeActor;
import com.tann.dice.screens.dungeon.panels.combatEffects.heal.HealController;
import com.tann.dice.screens.dungeon.panels.combatEffects.ice.FreezeActor;
import com.tann.dice.screens.dungeon.panels.combatEffects.ice.FrostActor;
import com.tann.dice.screens.dungeon.panels.combatEffects.lightning.LightningEffectController;
import com.tann.dice.screens.dungeon.panels.combatEffects.lock.LockController;
import com.tann.dice.screens.dungeon.panels.combatEffects.poison.PoisonCloudActor;
import com.tann.dice.screens.dungeon.panels.combatEffects.simplePanelImage.SimpleGroupPanelImageController;
import com.tann.dice.screens.dungeon.panels.combatEffects.simplePanelImage.SimplePanelImage;
import com.tann.dice.screens.dungeon.panels.combatEffects.simpleProjectile.RockProjectile;
import com.tann.dice.screens.dungeon.panels.combatEffects.simpleProjectile.SpikeProjectile;
import com.tann.dice.screens.dungeon.panels.combatEffects.simpleProjectile.StingProjectile;
import com.tann.dice.screens.dungeon.panels.combatEffects.simpleProjectile.arrow.ArrowActor;
import com.tann.dice.screens.dungeon.panels.combatEffects.simpleStrike.ElectroBladeActor;
import com.tann.dice.screens.dungeon.panels.combatEffects.simpleStrike.FireBladeActor;
import com.tann.dice.screens.dungeon.panels.combatEffects.simpleStrike.MultiStrike;
import com.tann.dice.screens.dungeon.panels.combatEffects.simpleStrike.SimpleStrike;
import com.tann.dice.screens.dungeon.panels.combatEffects.simpleThwack.SimpleThwack;
import com.tann.dice.screens.dungeon.panels.combatEffects.slam.SlamEffectActor;
import com.tann.dice.screens.dungeon.panels.combatEffects.slice.Slice;
import com.tann.dice.screens.dungeon.panels.combatEffects.slime.SlimeController;
import com.tann.dice.screens.dungeon.panels.combatEffects.summon.SummonController;
import com.tann.dice.screens.dungeon.panels.combatEffects.triBolt.TriBoltController;
import com.tann.dice.screens.shaderFx.DeathType;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.TannLog;
import java.util.List;

public enum VisualEffectType {
   None,
   Skip,
   HealBasic,
   Summon,
   Flee(DeathType.Flee),
   Sword(DeathType.Cut),
   Spear(DeathType.Cut),
   MultiBlade(DeathType.Cut),
   BoneThwack(DeathType.BloodSplatterEdge),
   SwordQuartz(DeathType.Cut),
   SwordBig(DeathType.Cut),
   SwordBigCleave(DeathType.CutDiagonal),
   Kriss(DeathType.Cut),
   Slice(DeathType.CutDiagonal),
   Claw(DeathType.CutDiagonal),
   BigClaw(DeathType.CutDiagonal),
   SingleTargetClaw(DeathType.CutDiagonal),
   Fork(DeathType.CutDiagonal),
   Arrow(DeathType.DelayedAlphaArrow),
   BeeSting(DeathType.DelayedAlphaArrow),
   TrollThwack(DeathType.Crush),
   BroomThwack(DeathType.Crush),
   HammerThwack(DeathType.Crush),
   Slam(DeathType.Crush),
   Crush(DeathType.Crush),
   Anvil(DeathType.Crush),
   Fang(DeathType.Acid),
   ZombiePunch(DeathType.Wipe),
   SpikerPunch(DeathType.Wipe),
   GolemPunch(DeathType.Wipe),
   ShieldBash(DeathType.Wipe),
   Revenge(DeathType.Wipe),
   Beam(DeathType.Ellipse),
   RedBeam(DeathType.Ellipse),
   Freeze(DeathType.Ellipse),
   Frost(DeathType.Ellipse),
   FrostFlank(DeathType.Ellipse),
   Slime(DeathType.Acid),
   PerlinPoison(DeathType.Acid),
   PoisonBreath(DeathType.Acid),
   TriBolt(DeathType.Ellipse),
   BatSwarm,
   Gaze(DeathType.Ellipse),
   Ellipse(DeathType.Ellipse),
   FireBreath(DeathType.Burn),
   Flame(DeathType.Burn),
   Taunt,
   Undying,
   Boost,
   BoostSmith,
   BoostHeal,
   Lightning(DeathType.Singularity),
   LightningBig(DeathType.Singularity),
   SpikeProjectile(DeathType.BloodSplatterEdge),
   RockProjectile(DeathType.BloodSplatterEdge),
   TuskSingle(DeathType.BloodSplatterEdge),
   TuskMulti(DeathType.BloodSplatterEdge),
   BoarBite(DeathType.BloodSplatterEdge),
   DragonBite(DeathType.BloodSplatterEdge),
   AlphaBite(DeathType.BloodSplatterEdge),
   RatBite(DeathType.BloodSplatterEdge),
   NibblePoison(DeathType.BloodSplatterEdge),
   WolfBite(DeathType.BloodSplatterEdge),
   TarantusBite(DeathType.BloodSplatterEdge),
   Beak(DeathType.BloodSplatterEdge),
   Singularity(DeathType.Singularity),
   Lock(DeathType.Ellipse),
   Cross(DeathType.Singularity),
   ElectroBlade,
   FireBlade,
   VoidStrike,
   MassBoost;

   final DeathType deathType;

   private VisualEffectType() {
      this.deathType = DeathType.Alpha;
   }

   private VisualEffectType(DeathType deathType) {
      this.deathType = deathType;
   }

   public static CombatEffect generate(TargetableCommand targetableCommand, Targetable targetable, Ent target, Ent source) {
      boolean player = !(source instanceof Monster);
      FightLog fightLog = DungeonScreen.get().getFightLog();
      Snapshot previousSnapshot = fightLog.getSnapshotBefore(targetableCommand);
      Snapshot afterSnapshot = fightLog.getSnapshotAfter(targetableCommand);
      Eff first = targetable.getDerivedEffects();
      if (targetableCommand instanceof DieCommand) {
         EntSideState ess = previousSnapshot.getSideState(targetableCommand);
         if (ess == null) {
            TannLog.error("Error finding ess with " + targetableCommand);
         } else {
            first = previousSnapshot.getSideState(targetableCommand).getCalculatedEffect();
         }
      }

      if (first.getType() == EffType.Or) {
         first = first.getOr(target.isPlayer());
      }

      CombatEffectActor actor = null;
      int value = first.getValue();
      List<EntState> actualTargets = previousSnapshot.getActualTargets(target, first, source);
      switch (first.getVisual()) {
         case Skip:
            return null;
         case None:
            if (source != null && !source.isPlayer()) {
               actor = new BlankEffectActor();
            }
            break;
         case Sword:
            actor = new SimpleStrike(target, value, Images.combatEffectSword, 0.35F, 0.3F, 0.2F);
            break;
         case SwordBig:
            actor = new SimpleStrike(target, value, Images.combatEffectswordBig, 0.35F, 0.3F, 0.2F);
            break;
         case SwordQuartz:
            actor = new SimpleStrike(target, value, Images.combatEffectSwordQuartz, 0.35F, 0.3F, 0.2F);
            break;
         case Spear:
            actor = new SimpleStrike(target, value, Images.combatEffectSpear, 0.15F, 0.45F, 0.15F);
            break;
         case Kriss:
            actor = new SimpleStrike(target, value, Images.combatEffectKriss, 0.25F, 0.15F, 0.25F);
            break;
         case ShieldBash:
            actor = new SimpleStrike(target, value, Images.combatEffectShieldBash, 0.35F, 0.3F, 0.2F, Sounds.clangs);
            break;
         case ElectroBlade:
            actor = new ElectroBladeActor(target, value, Images.combatEffectSword, 0.35F, 0.3F, 0.2F);
            break;
         case FireBlade:
            actor = new FireBladeActor(target, value, Images.combatEffectSword, 0.35F, 0.3F, 0.2F);
            break;
         case ZombiePunch:
            actor = new SimpleStrike(target, value, Images.combatEffectZombiePunch, 0.35F, 0.3F, 0.2F);
            break;
         case GolemPunch:
            actor = new SimpleStrike(target, value, Images.combatEffectGolemPunch, 0.35F, 0.3F, 0.2F);
            break;
         case SpikerPunch:
            actor = new SimpleStrike(target, value, Images.combatEffectSpikerPunch, 0.35F, 0.3F, 0.2F);
            break;
         case TuskSingle:
            Ent newTarget = target;
            if (target == null) {
               newTarget = previousSnapshot.getActualTargets(null, first, source).get(0).getEnt();
            }

            actor = new SimpleStrike(newTarget, value, Images.combatEffectTusk, 0.35F, 0.3F, 0.2F);
            break;
         case TuskMulti:
            actor = new MultiStrike(targetable, target, value, Images.combatEffectTusk, 0.35F, 0.3F, 0.2F, fightLog);
            break;
         case MultiBlade:
            actor = new MultiStrike(targetable, target, value, Images.combatEffectSword, 0.35F, 0.3F, 0.2F, fightLog);
            break;
         case HealBasic:
            return new CombatEffect(new HealController(targetableCommand, first), source);
         case Arrow:
            CombatEffectActor[] combatEffectActors = new CombatEffectActor[actualTargets.size()];

            for (int i = 0; i < actualTargets.size(); i++) {
               EntState es = actualTargets.get(i);
               boolean nowDead = afterSnapshot.getState(es.getEnt()).isDead();
               combatEffectActors[i] = new ArrowActor(source, es.getEnt(), value, nowDead);
            }

            if (combatEffectActors.length != 1) {
               return new CombatEffect(new ParallelCombatEffectController(combatEffectActors, fightLog), source);
            }

            actor = combatEffectActors[0];
            break;
         case Slice:
            actor = new Slice(first, target, null, 1, 0.15F, targetable, Colours.light, Colours.blue);
            break;
         case Claw:
            actor = new Slice(first, target, null, 3, 0.22F, targetable, Colours.red);
            break;
         case BigClaw:
            actor = new Slice(first, target, null, 3, 0.22F, targetable, Colours.red, Colours.red);
            break;
         case Fork:
            actor = new Slice(first, target, null, 3, 0.22F, targetable, Colours.red, Colours.grey);
            break;
         case SingleTargetClaw:
            actor = new Slice(first, target, null, 3, 0.13F, targetable, Colours.purple, Colours.grey);
            break;
         case Slam:
            actor = new SlamEffectActor(source);
            break;
         case Slime:
            return new CombatEffect(new SlimeController(targetable, target, source, fightLog), source);
         case RatBite:
            actor = new BiteActor(target, value, "rat", EntSize.small);
            break;
         case NibblePoison:
            actor = new BiteActor(target, value, "poisonNibble", EntSize.small);
            break;
         case WolfBite:
            actor = new BiteActor(target, value, "wolf", EntSize.reg);
            break;
         case DragonBite:
            actor = new BiteActor(target, value, "hugeChomp", EntSize.huge);
            break;
         case TarantusBite:
            actor = new BiteActor(target, value, "tarantus", EntSize.huge);
            break;
         case AlphaBite:
            actor = new BiteActor(target, value, "alpha", EntSize.big);
            break;
         case BoarBite:
            actor = new BiteActor(target, value, "boar", EntSize.big);
            break;
         case Beak:
            SimpleThwack a = new SimpleThwack(player, actualTargets, Images.combatEffectPeck, 0.25F, 0.08F, 0.25F);
            a.setSound(Sounds.biteReg);
            actor = a;
            break;
         case SwordBigCleave:
            actor = new SimpleThwack(player, actualTargets, Images.combatEffectSwordBigCleave, 0.45F, 0.15F, 0.25F);
            break;
         case Fang:
            actor = new SimpleThwack(player, actualTargets, Images.combatEffectFang, 0.15F, 0.1F, 0.15F);
            break;
         case TrollThwack:
            actor = new SimpleThwack(player, actualTargets, Images.combatEffectTrollThwack, 0.5F, 0.17F, 0.3F);
            break;
         case BroomThwack:
            actor = new SimpleThwack(player, actualTargets, Images.combatEffectBroom, 0.5F, 0.17F, 0.3F);
            break;
         case HammerThwack:
            actor = new SimpleThwack(player, actualTargets, Images.combatEffectHammer, 0.5F, 0.12F, 0.25F);
            break;
         case BoneThwack:
            actor = new SimpleThwack(player, actualTargets, Images.combatEffectBone, 0.4F, 0.1F, 0.1F);
            break;
         case PerlinPoison:
            actor = new PoisonCloudActor(actualTargets);
            break;
         case TriBolt:
            return new CombatEffect(new TriBoltController(targetable, target, source, fightLog), source);
         case BatSwarm:
            return new CombatEffect(new BatSwarmController(), source);
         case Gaze:
            actor = new GazeActor(targetable, source, target);
            break;
         case Summon:
            return new CombatEffect(new SummonController(source, MonsterTypeLib.byName(first.getSummonType())), source);
         case FireBreath:
            return new CombatEffect(
               new DragonBreathController(
                  source,
                  target,
                  targetable,
                  new Color[]{Colours.light, Colours.yellow, Colours.orange, Colours.orange, Colours.red, Colours.dark, Colours.dark},
                  100
               ),
               source
            );
         case PoisonBreath:
            return new CombatEffect(
               new DragonBreathController(source, target, targetable, new Color[]{Colours.green, Colours.purple, Colours.purple}, 50), source
            );
         case Taunt:
            actor = new SimplePanelImage(target, Images.combatEffectTaunt, first);
            break;
         case Freeze:
            actor = new FreezeActor(target);
            break;
         case Boost:
            actor = new SimplePanelImage(target, Images.combatEffectBoost, first, Sounds.boost);
            break;
         case BoostSmith:
            actor = new SimplePanelImage(target, Images.combatEffectBoost, first, Sounds.smith);
            break;
         case BoostHeal:
            actor = new SimplePanelImage(target, Images.combatEffectBoost, first, Sounds.heals);
            break;
         case Undying:
            actor = new SimplePanelImage(target, Images.combatEffectUndying, first);
            break;
         case Lightning:
            return new CombatEffect(new LightningEffectController(source, targetableCommand.getAllTargets(), 3), source);
         case LightningBig:
            return new CombatEffect(new LightningEffectController(source, targetableCommand.getAllTargets(), 8), source);
         case VoidStrike:
            return new CombatEffect(new SimpleGroupPanelImageController(0.3F, 0.2F, targetable, target, Images.combatEffectCross, fightLog), source);
         case MassBoost:
            return new CombatEffect(new SimpleGroupPanelImageController(0.0F, 0.0F, targetable, target, Images.combatEffectBoost, fightLog), source);
         case Beam:
            actor = new BeamActor(source, target, Colours.blue);
            break;
         case RedBeam:
            actor = new BeamActor(source, target, Colours.red);
            break;
         case Flame:
            CombatEffectActor[] combatEffectActors = new CombatEffectActor[actualTargets.size()];

            for (int i = 0; i < actualTargets.size(); i++) {
               EntState es = actualTargets.get(i);
               combatEffectActors[i] = new FlameGroup(es.getEnt());
            }

            if (combatEffectActors.length == 1) {
               actor = combatEffectActors[0];
            } else if (combatEffectActors.length > 1) {
               return new CombatEffect(new ParallelCombatEffectController(combatEffectActors, fightLog), source);
            }
            break;
         case Frost:
            actor = new FrostActor(target);
            break;
         case FrostFlank:
            CombatEffectActor[] combatEffectActors = new CombatEffectActor[actualTargets.size()];

            for (int i = 0; i < actualTargets.size(); i++) {
               EntState es = actualTargets.get(i);
               combatEffectActors[i] = new FrostActor(es.getEnt());
            }

            return new CombatEffect(new ParallelCombatEffectController(combatEffectActors, fightLog), source);
         case BeeSting:
            CombatEffectActor[] combatEffectActors = new CombatEffectActor[actualTargets.size()];

            for (int i = 0; i < actualTargets.size(); i++) {
               EntState es = actualTargets.get(i);
               combatEffectActors[i] = new StingProjectile(source, es.getEnt(), value);
            }

            return new CombatEffect(new ParallelCombatEffectController(combatEffectActors, fightLog), source);
         case SpikeProjectile:
            CombatEffectActor[] combatEffectActors = new CombatEffectActor[actualTargets.size()];

            for (int i = 0; i < actualTargets.size(); i++) {
               EntState es = actualTargets.get(i);
               combatEffectActors[i] = new SpikeProjectile(source, es.getEnt(), value);
            }

            return new CombatEffect(new ParallelCombatEffectController(combatEffectActors, fightLog), source);
         case RockProjectile:
            CombatEffectActor[] combatEffectActors = new CombatEffectActor[actualTargets.size()];

            for (int i = 0; i < actualTargets.size(); i++) {
               EntState es = actualTargets.get(i);
               combatEffectActors[i] = new RockProjectile(source, es.getEnt(), value);
            }

            return new CombatEffect(new ParallelCombatEffectController(combatEffectActors, fightLog), source);
         case Crush:
            if (actualTargets.size() == 1) {
               actualTargets.add(actualTargets.get(0));
            }

            if (actualTargets.size() != 2) {
               return null;
            }

            CombatEffectActor[] actors = new CombatEffectActor[2];

            for (int i = 0; i < 2; i++) {
               actors[i] = new CrushActor(i == 0, actualTargets.get(i).getEnt(), Images.combatEffectCrush);
            }

            return new CombatEffect(new ParallelCombatEffectController(actors, fightLog), source);
         case Anvil:
            actor = new CrushActor(true, actualTargets.get(0).getEnt(), Images.combatEffectAnvil);
            break;
         case Lock:
            return new CombatEffect(new LockController(source, target, first.getValue()), source);
         case Cross:
            actor = new CrossActor(target);
      }

      if (actor != null) {
         targetableCommand.linkActor(actor);
         return new CombatEffect(new BasicCombatEffectController(actor, fightLog), source);
      } else {
         return null;
      }
   }
}
