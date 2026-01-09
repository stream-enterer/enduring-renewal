package com.tann.dice.gameplay.content.ent.type.bill;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellBill;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.Tactic;
import com.tann.dice.gameplay.trigger.personal.spell.learn.LearnSpell;
import com.tann.dice.gameplay.trigger.personal.spell.learn.LearnTactic;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.util.Tann;
import java.util.List;

public class HTBill extends ETBill<HTBill> {
   private HeroCol heroCol;
   private int lvl;
   private static AtlasRegion ph = ImageUtils.loadArExt("portrait/placeholder/hero/reg");

   public HTBill(HeroCol heroCol, int lvl) {
      this.heroCol = heroCol;
      this.lvl = lvl;
   }

   public HTBill col(HeroCol col) {
      this.heroCol = col;
      return this;
   }

   public HTBill tier(int tier) {
      this.lvl = tier;
      return this;
   }

   public HTBill spell(SpellBill sb) {
      return this.spell(sb.bSpell());
   }

   public HTBill spell(Spell spell) {
      spell.setCol(this.heroCol.col);
      return this.trait(new Trait(new LearnSpell(spell)));
   }

   public HTBill tactic(Tactic tactic) {
      tactic.setCol(this.heroCol.col);
      return this.trait(new Trait(new LearnTactic(tactic)));
   }

   public HeroType bEntType() {
      this.setupOffsets();
      return new HeroType(this.name, this.hp, this.makePortrait(), this.sides, this.traits, this.size, this.heroCol, this.lvl, this.offsets);
   }

   @Override
   protected AtlasRegion makePortrait() {
      AtlasRegion override = super.makePortrait();
      if (override != null) {
         return override;
      } else if (this.texturePath != null) {
         return this.makeSetTexture();
      } else if (this.name.contains(".")) {
         return this.fetchPlaceholder();
      } else {
         List<AtlasRegion> regions = Tann.getRegionsStartingWith(
            com.tann.dice.Main.atlas, "portrait/hero/" + this.heroCol.colName + "/" + this.name.toLowerCase()
         );
         return regions.size() == 0 ? this.fetchPlaceholder() : regions.get(0);
      }
   }

   public static AtlasRegion placeholder() {
      return ph;
   }

   @Override
   public AtlasRegion fetchPlaceholder() {
      return placeholder();
   }

   public void setSide(int sideIndex, EntSide newSide) {
      this.sides[sideIndex] = newSide;
   }
}
