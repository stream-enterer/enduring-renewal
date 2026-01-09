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

public class EntSidesBlobHuge {
   public static final EntSide blank = new EnSiBi().size(EntSize.huge).image("blank").effect(new EffBill().nothing()).noVal();
   public static final EntSide blankBug = new EnSiBi()
      .size(EntSize.huge)
      .image("blankBug")
      .effect(new EffBill().nothing().overrideDescription("blank [purple](bug)[cu]"))
      .noVal();
   public static final EntSide blankExerted = new EnSiBi()
      .size(EntSize.huge)
      .image("blankExerted")
      .effect(new EffBill().nothing().overrideDescription("blank [purple](exerted)[cu]"))
      .noVal();
   public static final EnSiBi club = new EnSiBi()
      .size(EntSize.huge)
      .image("club")
      .effect(new EffBill().damage(1).keywords(Keyword.cleave).visual(VisualEffectType.Slice));
   public static final EnSiBi stomp = new EnSiBi().size(EntSize.huge).image("stomp").effect(new EffBill().damage(1).group().visual(VisualEffectType.Slam));
   public static final EnSiBi chomp = new EnSiBi()
      .size(EntSize.huge)
      .image("chomp")
      .effect(new EffBill().damage(1).keywords(Keyword.heavy).visual(VisualEffectType.DragonBite));
   public static final EnSiBi chompSelfHeal = new EnSiBi()
      .size(EntSize.huge)
      .image("chompSelfHeal")
      .effect(new EffBill().damage(1).keywords(Keyword.heavy, Keyword.selfHeal).visual(VisualEffectType.DragonBite));
   public static final EnSiBi devour = new EnSiBi().size(EntSize.huge).image("devour").effect(new EffBill().damage(1).visual(VisualEffectType.TarantusBite));
   public static final EnSiBi infect = new EnSiBi()
      .size(EntSize.huge)
      .image("infect")
      .effect(new EffBill().damage(1).keywords(Keyword.poison, Keyword.weaken).visual(VisualEffectType.DragonBite));
   public static final EnSiBi tentacle = new EnSiBi()
      .size(EntSize.huge)
      .image("hexia")
      .effect(new EffBill().damage(1).keywords(Keyword.descend, Keyword.inflictPain).visual(VisualEffectType.Flame));
   public static final EnSiBi chill = new EnSiBi()
      .size(EntSize.huge)
      .image("chill")
      .effect(new EffBill().damage(1).group().keywords(Keyword.weaken).visual(VisualEffectType.Gaze));
   public static final EnSiBi flame = new EnSiBi()
      .size(EntSize.huge)
      .image("flame")
      .effect(new EffBill().damage(1).group().visual(VisualEffectType.FireBreath));
   public static final EnSiBi summonImps = new EnSiBi().size(EntSize.huge).image("summonImp").effect(new EffBill().summon("Imp", 1));
   public static final EnSiBi summonDemon = new EnSiBi().size(EntSize.huge).image("summonDemon").effect(new EffBill().summon("Demon", 1));
   public static final EnSiBi summonBones = new EnSiBi().size(EntSize.huge).image("summonBones").effect(new EffBill().summon("Bones", 1));
   public static final EnSiBi summonSpider = new EnSiBi().size(EntSize.huge).image("summonSpider").effect(new EffBill().summon("Spider", 1));
   public static final EnSiBi summonSaber = new EnSiBi().size(EntSize.huge).image("summonSaber").effect(new EffBill().summon("Saber", 1));
   public static final EnSiBi summonSlate = new EnSiBi().size(EntSize.huge).image("summonSlate").effect(new EffBill().summon("Slate", 1));
   public static final EnSiBi poisonBreath = new EnSiBi()
      .size(EntSize.huge)
      .image("poisonBreath")
      .effect(new EffBill().damage(1).keywords(Keyword.poison, Keyword.cleave).visual(VisualEffectType.PoisonBreath));
   public static final EnSiBi petrifyStaff = new EnSiBi()
      .size(EntSize.huge)
      .image("staff")
      .effect(new EffBill().damage(3).keywords(Keyword.petrify).visual(VisualEffectType.Gaze));
   public static final EnSiBi slimeUpDown = new EnSiBi()
      .size(EntSize.huge)
      .image("upDownBlob")
      .effect(new EffBill().damage(1).targetType(TargetingType.TopAndBot).visual(VisualEffectType.Slime));
   public static final EnSiBi chainFlank = new EnSiBi()
      .size(EntSize.huge)
      .image("weakenFlanking")
      .effect(new EffBill().damage(1).keywords(Keyword.weaken).targetType(TargetingType.TopAndBot).visual(VisualEffectType.FrostFlank));
   public static final EnSiBi deathBeam = new EnSiBi()
      .size(EntSize.huge)
      .image("deathBeam")
      .effect(new EffBill().damage(1).keywords(Keyword.inflictDeath).visual(VisualEffectType.RedBeam));
   public static final EnSiBi slimeTriple = new EnSiBi()
      .size(EntSize.huge)
      .image("threeBlobs")
      .effect(new EffBill().damage(1).keywords(Keyword.cleave).visual(VisualEffectType.Slime));
   public static final EnSiBi inflictExert = new EnSiBi()
      .size(EntSize.huge)
      .image("groupInflictExert")
      .effect(new EffBill().damage(1).keywords(Keyword.inflictExert, Keyword.cleave).visual(VisualEffectType.Gaze));
   public static final EnSiBi deafen = new EnSiBi()
      .size(EntSize.huge)
      .image("ear")
      .effect(new EffBill().group().damage(1).keywords(Keyword.exert).visual(VisualEffectType.Gaze));
   public static final EntSide handKill = new EnSiBi()
      .size(EntSize.huge)
      .image("handSkull")
      .effect(new EffBill().kill().targetType(TargetingType.Top).visual(VisualEffectType.Slice))
      .noVal();
   public static final EnSiBi wendigoSkull = new EnSiBi()
      .size(EntSize.huge)
      .image("inevitableSkull")
      .effect(new EffBill().damage(1).keywords(Keyword.bloodlust, Keyword.eliminate).visual(VisualEffectType.RedBeam));

   public static List<Object> makeAll() {
      return Arrays.asList(
         blank,
         blankBug,
         blankExerted,
         club,
         stomp,
         chomp,
         devour,
         infect,
         tentacle,
         chill,
         flame,
         summonImps,
         summonDemon,
         summonBones,
         summonSpider,
         summonSaber,
         summonSlate,
         poisonBreath,
         petrifyStaff,
         slimeUpDown,
         chainFlank,
         deathBeam,
         slimeTriple,
         inflictExert,
         deafen,
         handKill,
         wendigoSkull,
         chompSelfHeal
      );
   }
}
