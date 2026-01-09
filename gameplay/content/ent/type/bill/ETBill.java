package com.tann.dice.gameplay.content.ent.type.bill;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.util.CalcStats;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ETBill<t> {
   protected int hp;
   protected EntSize size = EntSize.reg;
   protected EntSide[] sides;
   protected List<Trait> traits = new ArrayList<>();
   protected String name;
   protected String texturePath;
   protected Map<String, Integer> offsets;
   AtlasRegion textureOverride;

   public t hp(int amount) {
      this.hp = amount;
      return (t)this;
   }

   public t texture(String path) {
      this.texturePath = path;
      this.offsets = null;
      return (t)this;
   }

   public t sides() {
      return this.sides(this.size.getBlank());
   }

   public t sides(List<EntSide> sides) {
      int blanksToAdd = 6 - sides.size();

      for (int i = 0; i < blanksToAdd; i++) {
         sides.add(this.size.getBlank());
      }

      return this.sides(sides.toArray(new EntSide[0]));
   }

   public t sides(EntSide a) {
      return this.sides(a, this.size.getBlank());
   }

   public t sides(EntSide a, EntSide b) {
      return this.sides(a, b, this.size.getBlank());
   }

   public t sides(EntSide a, EntSide b, EntSide c) {
      return this.sides(a, b, this.size.getBlank(), this.size.getBlank(), c, this.size.getBlank());
   }

   public t sides(EntSide a, EntSide b, EntSide c, EntSide d) {
      return this.sides(a, b, c, d, this.size.getBlank(), this.size.getBlank());
   }

   public t sides(EntSide a, EntSide b, EntSide c, EntSide d, EntSide e) {
      return this.sides(a, b, c, d, e, this.size.getBlank());
   }

   public t sides(EntSide... sides) {
      EntType.niceToReal(sides);
      return this.sidesRaw(sides);
   }

   public t sidesRaw(EntSide... sides) {
      this.sides = sides;
      return (t)this;
   }

   public t trait(Trait trait) {
      this.traits.add(trait);
      return (t)this;
   }

   public t trait(Personal personal, boolean visible) {
      return this.trait(new Trait(personal, visible));
   }

   public t hiddenNoCalc(Personal personal) {
      return this.trait(new Trait(personal, new CalcStats(0.0F, 0.0F), false));
   }

   public t trait(Personal personal, CalcStats calcStats, boolean visible) {
      return this.trait(new Trait(personal, calcStats, visible));
   }

   public t trait(Personal personal, CalcStats calcStats) {
      return this.trait(personal, calcStats, true);
   }

   public t trait(Personal personal) {
      return this.trait(new Trait(personal));
   }

   public t name(String name) {
      this.name = name;
      return (t)this;
   }

   public abstract EntType bEntType();

   public EntSide[] getSides() {
      return this.sides;
   }

   protected AtlasRegion makeSetTexture() {
      if (this.texturePath != null) {
         this.texturePath = this.texturePath.replaceAll("\\+", "");
         this.texturePath = this.texturePath.replaceAll(" ", "-").toLowerCase();
         String start = "portrait/";
         if (this instanceof HTBill) {
            start = start + "hero";
         } else {
            start = start + "monster/" + this.size.name().toLowerCase();
         }

         start = start + "/" + this.texturePath.toLowerCase();
         List<AtlasRegion> regions = Tann.getRegionsStartingWith(start);
         if (regions.size() == 0) {
            TannLog.error("error finding texture for entity " + this.texturePath);
            return this.fetchPlaceholder();
         } else if (regions.size() == 1) {
            return regions.get(0);
         } else {
            Collections.sort(regions, new Comparator<AtlasRegion>() {
               public int compare(AtlasRegion o1, AtlasRegion o2) {
                  return o1.name.length() - o2.name.length();
               }
            });
            return regions.get(0);
         }
      } else {
         return null;
      }
   }

   protected abstract AtlasRegion fetchPlaceholder();

   public t arOverride(AtlasRegion ar) {
      this.textureOverride = ar;
      return (t)this;
   }

   public static int getIntFromFilename(String token, AtlasRegion ar) {
      String imageName = ar.name;
      if (imageName != null && ar.name.contains(token)) {
         String subString = imageName.substring(imageName.indexOf(token) + 1);
         subString = subString.split("\\D")[0];
         return Integer.valueOf(subString);
      } else {
         return 0;
      }
   }

   protected void setupOffsets() {
      if (this.offsets == null) {
         AtlasRegion ar = this.makePortrait();
         if (ar != null && this.offsets == null) {
            this.offsets = this.getOffsets(ar);
         }
      }
   }

   private Map<String, Integer> getOffsets(AtlasRegion ar) {
      Map<String, Integer> result = new HashMap<>();

      for (char c : "LURDS".toCharArray()) {
         result.put("" + c, getIntFromFilename("" + c, ar));
      }

      return result;
   }

   public t offsetOverride(Map<String, Integer> offsets) {
      this.offsets = offsets;
      return (t)this;
   }

   protected AtlasRegion makePortrait() {
      return this.textureOverride != null ? this.textureOverride : this.makeSetTexture();
   }

   public t removeTrait(Trait t) {
      this.traits.remove(t);
      return (t)this;
   }

   public t resetOffsets() {
      this.offsets = null;
      return (t)this;
   }

   public t clearTraits() {
      this.traits.clear();
      return (t)this;
   }
}
