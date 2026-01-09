package com.tann.dice.gameplay.trigger.global.chance;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.statics.Images;
import com.tann.dice.util.ImageActor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalRarity extends Global {
   final Rarity rarity;
   private static final Map<Rarity, GlobalRarity> map = init();

   private GlobalRarity(Rarity rarity) {
      this.rarity = rarity;
   }

   public static float listChance(List<Global> globals) {
      float chance = 1.0F;

      for (int i = 0; i < globals.size(); i++) {
         chance *= globals.get(i).chance();
      }

      return chance;
   }

   @Override
   public float chance() {
      return this.rarity.value;
   }

   @Override
   public String describeForSelfBuff() {
      return OptionLib.SHOW_RARITY.c() ? "[notranslate][text]" + com.tann.dice.Main.t("Rarity") + ": [cu]" + this.rarity.toColourTaggedString() : null;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return new ImageActor(Images.rarity_star, this.rarity.col);
   }

   @Override
   public boolean skipEquipImage() {
      return !OptionLib.SHOW_RARITY.c();
   }

   @Override
   public boolean metaOnly() {
      return true;
   }

   private static Map<Rarity, GlobalRarity> init() {
      Map<Rarity, GlobalRarity> result = new HashMap<>();

      for (Rarity r : Rarity.values()) {
         result.put(r, new GlobalRarity(r));
      }

      return result;
   }

   public static GlobalRarity fromRarity(Rarity r) {
      return map.get(r);
   }
}
