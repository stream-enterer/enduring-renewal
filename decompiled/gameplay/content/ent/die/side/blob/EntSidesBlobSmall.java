package com.tann.dice.gameplay.content.ent.die.side.blob;

import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.EnSiBi;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.VisualEffectType;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import java.util.Arrays;
import java.util.List;

public class EntSidesBlobSmall {
   public static final EntSide blankExerted = new EnSiBi()
      .size(EntSize.small)
      .image("blankExerted")
      .effect(new EffBill().nothing().overrideDescription("blank [purple](exerted)[cu]"))
      .noVal();
   public static final EntSide blankBug = new EnSiBi()
      .size(EntSize.small)
      .image("blankBug")
      .effect(new EffBill().nothing().overrideDescription("blank [purple](bug)[cu]"))
      .noVal();
   public static final EnSiBi petrify = new EnSiBi()
      .size(EntSize.small)
      .image("petrify")
      .effect(new EffBill().damage(1).keywords(Keyword.petrify).visual(VisualEffectType.Gaze));
   public static final EnSiBi selfHealVitality = new EnSiBi()
      .size(EntSize.small)
      .image("selfHealVitality")
      .effect(new EffBill().self().heal(1).keywords(Keyword.vitality).visual(VisualEffectType.HealBasic));
   public static final EnSiBi weaken = new EnSiBi()
      .size(EntSize.small)
      .image("weaken")
      .effect(new EffBill().damage(1).keywords(Keyword.weaken).visual(VisualEffectType.Freeze));
   public static final EnSiBi sting = new EnSiBi()
      .size(EntSize.small)
      .image("sting")
      .effect(new EffBill().damage(1).keywords(Keyword.death).visual(VisualEffectType.BeeSting));
   public static final EnSiBi arrow = new EnSiBi().size(EntSize.small).image("arrow").effect(new EffBill().damage(1).visual(VisualEffectType.Arrow));
   public static final EnSiBi arrowEliminate = new EnSiBi()
      .size(EntSize.small)
      .image("arrowEliminate")
      .effect(new EffBill().damage(1).keywords(Keyword.eliminate).visual(VisualEffectType.Arrow));
   public static final EnSiBi bone = new EnSiBi().size(EntSize.small).image("boneStrike").effect(new EffBill().damage(1).visual(VisualEffectType.BoneThwack));
   public static final EnSiBi nip = new EnSiBi().size(EntSize.small).image("nip").effect(new EffBill().damage(1).visual(VisualEffectType.RatBite));
   public static final EnSiBi nipPoison = new EnSiBi()
      .size(EntSize.small)
      .image("nipPoison")
      .effect(new EffBill().damage(1).keywords(Keyword.poison).visual(VisualEffectType.NibblePoison));
   public static final EnSiBi curse = new EnSiBi()
      .size(EntSize.small)
      .image("curse")
      .effect(new EffBill().damage(1).visual(VisualEffectType.Flame).keywords(Keyword.inflictPain));
   public static final EnSiBi summonBones = new EnSiBi().size(EntSize.small).image("summonBones").effect(new EffBill().summon("Bones", 1));
   public static final EnSiBi summonSlimelet = new EnSiBi().size(EntSize.small).image("summonSlimelet").effect(new EffBill().summon("Slimelet", 1));
   public static final EnSiBi summonHexia = new EnSiBi()
      .size(EntSize.small)
      .image("summonHexia")
      .effect(new EffBill().summon("Hexia", 1).keywords(Keyword.death));
   public static final EnSiBi slime = new EnSiBi().size(EntSize.small).image("slime").effect(new EffBill().damage(1).visual(VisualEffectType.Slime));
   public static final EnSiBi hatchCaw = new EnSiBi()
      .size(EntSize.small)
      .image("hatch")
      .effect(new EffBill().summon("Caw", 1).keywords(Keyword.death).visual(VisualEffectType.None));
   public static final EnSiBi growThorn = new EnSiBi()
      .size(EntSize.small)
      .image("grow")
      .effect(new EffBill().summon("Thorn", 1).keywords(Keyword.death).visual(VisualEffectType.None));
   public static final EntSide blank = new EnSiBi().size(EntSize.small).image("blank").effect(new EffBill().nothing()).noVal();

   public static List<Object> makeAll() {
      return Arrays.asList(
         blankExerted,
         blankBug,
         petrify,
         selfHealVitality,
         weaken,
         sting,
         arrow,
         arrowEliminate,
         bone,
         nip,
         nipPoison,
         curse,
         summonBones,
         summonSlimelet,
         summonHexia,
         slime,
         hatchCaw,
         growThorn,
         blank
      );
   }
}
