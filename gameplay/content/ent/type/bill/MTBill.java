package com.tann.dice.gameplay.content.ent.type.bill;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.trigger.global.chance.MonsterChance;
import com.tann.dice.gameplay.trigger.global.chance.Rarity;
import com.tann.dice.gameplay.trigger.personal.util.CalcStats;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import java.util.ArrayList;
import java.util.List;

public class MTBill extends ETBill<MTBill> {
   private String[] deathSound;
   private final List<Integer> bannedLevels = new ArrayList<>();
   private int maxInFight = 5000;
   private int minInFight = 0;
   private boolean unique;

   public MTBill(EntSize size) {
      this.size = size;
   }

   public MTBill death(String[] deathSound) {
      this.deathSound = deathSound;
      return this;
   }

   public MTBill banLevels(int... levels) {
      for (int i : levels) {
         this.bannedLevels.add(i);
      }

      return this;
   }

   public MTBill max(int max) {
      this.maxInFight = max;
      return this;
   }

   public MTBill min(int min) {
      this.minInFight = min;
      return this;
   }

   public MTBill makeUnique() {
      this.unique = true;
      return this;
   }

   public MTBill rarity(Rarity rarity) {
      return this.trait(new Trait(new MonsterChance(rarity), new CalcStats(0.0F, 0.0F), true));
   }

   public MonsterType bEntType() {
      this.setupOffsets();
      return new MonsterType(
         this.name,
         this.hp,
         this.makePortrait(),
         this.sides,
         this.traits,
         this.size,
         this.bannedLevels,
         this.deathSound,
         this.maxInFight,
         this.minInFight,
         this.unique,
         this.offsets
      );
   }

   @Override
   protected AtlasRegion makePortrait() {
      AtlasRegion preset = super.makePortrait();
      if (preset != null) {
         return preset;
      } else {
         String path = "portrait/monster/" + this.size.name() + "/";
         String monsterName = this.name.replaceAll("\\+", "");
         String fileName = monsterName.replaceAll(" ", "-").toLowerCase();
         List<AtlasRegion> regions = Tann.getRegionsStartingWith(com.tann.dice.Main.atlas, path + fileName);
         if (regions.size() == 0) {
            return this.fetchPlaceholder();
         } else if (regions.size() > 1) {
            TannLog.log("Multiple portraits for " + this.name);
            return this.fetchPlaceholder();
         } else {
            return regions.get(0);
         }
      }
   }

   @Override
   protected AtlasRegion fetchPlaceholder() {
      return ImageUtils.loadArExt("portrait/placeholder/monster/" + this.size.name());
   }
}
