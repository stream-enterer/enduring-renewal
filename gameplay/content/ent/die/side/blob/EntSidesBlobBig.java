package com.tann.dice.gameplay.content.ent.die.side.blob;

import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.EnSiBi;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.effect.eff.VisualEffectType;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import java.util.Arrays;
import java.util.List;

public class EntSidesBlobBig {
   public static final EntSide blankExerted = new EnSiBi()
      .size(EntSize.big)
      .image("blankExerted")
      .effect(new EffBill().nothing().overrideDescription("blank [purple](exerted)[cu]"))
      .noVal();
   public static final EntSide blankBug = new EnSiBi()
      .size(EntSize.big)
      .image("blankBug")
      .effect(new EffBill().nothing().overrideDescription("blank [purple](bug)[cu]"))
      .noVal();
   public static final EnSiBi haunt = new EnSiBi()
      .size(EntSize.big)
      .image("finger")
      .effect(new EffBill().damage(1).keywords(Keyword.eliminate).visual(VisualEffectType.Cross));
   public static final EnSiBi club = new EnSiBi()
      .size(EntSize.big)
      .image("club")
      .effect(new EffBill().damage(1).keywords(Keyword.cleave).visual(VisualEffectType.TrollThwack));
   public static final EnSiBi poisonAura = new EnSiBi()
      .size(EntSize.big)
      .image("poisonCloud")
      .effect(new EffBill().keywords(Keyword.cleave, Keyword.poison).damage(1).visual(VisualEffectType.PerlinPoison));
   public static final EnSiBi curse = new EnSiBi()
      .size(EntSize.big)
      .image("jinx")
      .effect(new EffBill().damage(1).keywords().visual(VisualEffectType.Flame).keywords(Keyword.inflictPain));
   public static final EnSiBi poison = new EnSiBi()
      .size(EntSize.big)
      .image("poison")
      .effect(new EffBill().damage(1).keywords(Keyword.poison).visual(VisualEffectType.PerlinPoison));
   public static final EnSiBi poisonApple = new EnSiBi()
      .size(EntSize.big)
      .image("poisonApple")
      .effect(new EffBill().damage(1).keywords(Keyword.poison).visual(VisualEffectType.PerlinPoison));
   public static final EnSiBi broomstick = new EnSiBi()
      .size(EntSize.big)
      .image("broomstick")
      .effect(new EffBill().damage(1).keywords(Keyword.heavy).visual(VisualEffectType.BroomThwack));
   public static final EnSiBi brew_group = new EnSiBi().size(EntSize.big).image("brew").effect(new EffBill().heal(1).group());
   public static final EnSiBi batSwarm = new EnSiBi().size(EntSize.big).image("bats").effect(new EffBill().damage(1).group().visual(VisualEffectType.BatSwarm));
   public static final EnSiBi chillingGaze = new EnSiBi()
      .size(EntSize.big)
      .image("gaze")
      .effect(new EffBill().damage(1).keywords(Keyword.weaken, Keyword.cleave).visual(VisualEffectType.Gaze));
   public static final EnSiBi weaken = new EnSiBi()
      .size(EntSize.big)
      .image("chain")
      .effect(new EffBill().damage(1).keywords(Keyword.weaken).visual(VisualEffectType.Frost));
   public static final EnSiBi rockSpray = new EnSiBi()
      .size(EntSize.big)
      .image("rockSpray")
      .effect(new EffBill().damage(1).group().visual(VisualEffectType.RockProjectile));
   public static final EnSiBi rockFist = new EnSiBi().size(EntSize.big).image("rockFist").effect(new EffBill().damage(1).visual(VisualEffectType.GolemPunch));
   public static final EnSiBi spikeSpray = new EnSiBi()
      .size(EntSize.big)
      .image("spikeSpray")
      .effect(new EffBill().damage(1).group().visual(VisualEffectType.SpikeProjectile));
   public static final EnSiBi stomp = new EnSiBi().size(EntSize.big).image("stomp").effect(new EffBill().damage(1).group().visual(VisualEffectType.Slam));
   public static final EnSiBi punch = new EnSiBi().size(EntSize.big).image("punch").effect(new EffBill().damage(1).visual(VisualEffectType.SpikerPunch));
   public static final EnSiBi claw = new EnSiBi()
      .size(EntSize.big)
      .image("claw")
      .effect(new EffBill().damage(1).keywords(Keyword.cleave).visual(VisualEffectType.Claw));
   public static final EnSiBi peck = new EnSiBi().size(EntSize.big).image("peck").effect(new EffBill().damage(1).visual(VisualEffectType.Beak));
   public static final EnSiBi summonSkeleton = new EnSiBi().size(EntSize.big).image("summonSkeleton").effect(new EffBill().summon("Bones", 1));
   public static final EnSiBi summonImp = new EnSiBi().size(EntSize.big).image("summonImp").effect(new EffBill().summon("Imp", 1));
   public static final EnSiBi decay = new EnSiBi()
      .size(EntSize.big)
      .image("decay")
      .effect(new EffBill().damage(1).keywords(Keyword.descend).visual(VisualEffectType.TriBolt));
   public static final EnSiBi slimeUpDown = new EnSiBi()
      .size(EntSize.big)
      .image("upDownBlob")
      .effect(new EffBill().damage(1).targetType(TargetingType.TopAndBot).visual(VisualEffectType.Slime));
   public static final EnSiBi slimeTriple = new EnSiBi()
      .size(EntSize.big)
      .image("threeBlobs")
      .effect(new EffBill().damage(1).keywords(Keyword.cleave).visual(VisualEffectType.Slime));
   public static final EnSiBi howl = new EnSiBi().size(EntSize.big).image("summonWolf").effect(new EffBill().summon("Wolf", 1));
   public static final EnSiBi rabidFrenzy = new EnSiBi()
      .size(EntSize.big)
      .image("maul")
      .effect(new EffBill().damage(1).keywords(Keyword.cleave).visual(VisualEffectType.BigClaw));
   public static final EnSiBi bite = new EnSiBi().size(EntSize.big).image("wolfBite").effect(new EffBill().damage(1).visual(VisualEffectType.AlphaBite));
   public static final EnSiBi swordCleave = new EnSiBi()
      .size(EntSize.big)
      .image("sword")
      .effect(new EffBill().damage(1).keywords(Keyword.cleave).visual(VisualEffectType.SwordBigCleave));
   public static final EnSiBi boarBite = new EnSiBi().size(EntSize.big).image("boar_bite").effect(new EffBill().damage(1).visual(VisualEffectType.BoarBite));
   public static final EnSiBi gore = new EnSiBi()
      .size(EntSize.big)
      .image("boar_gore")
      .effect(new EffBill().damage(1).targetType(TargetingType.TopAndBot).visual(VisualEffectType.TuskMulti));
   public static final EntSide blank = new EnSiBi().size(EntSize.big).image("blank").effect(new EffBill().nothing()).noVal();

   public static List<Object> makeAll() {
      return Arrays.asList(
         blankExerted,
         blankBug,
         haunt,
         club,
         poisonAura,
         curse,
         poison,
         poisonApple,
         broomstick,
         brew_group,
         batSwarm,
         chillingGaze,
         weaken,
         rockSpray,
         rockFist,
         spikeSpray,
         stomp,
         punch,
         claw,
         peck,
         summonSkeleton,
         summonImp,
         decay,
         slimeUpDown,
         slimeTriple,
         howl,
         rabidFrenzy,
         bite,
         swordCleave,
         boarBite,
         gore,
         blank
      );
   }
}
