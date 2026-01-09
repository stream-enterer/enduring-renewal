package com.tann.dice.gameplay.trigger.personal.equipRestrict;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.util.ImageActor;

public class EquipRestrictCol extends EquipRestrict {
   final HeroCol restrictedTo;

   public EquipRestrictCol(HeroCol restrictedTo) {
      this.restrictedTo = restrictedTo;
   }

   public boolean allowEquip(Ent ent) {
      if (!(ent instanceof Hero)) {
         return false;
      } else {
         Hero h = (Hero)ent;
         return h.getHeroType().heroCol == this.restrictedTo;
      }
   }

   @Override
   public boolean unequip(Ent ent) {
      return !this.allowEquip(ent);
   }

   @Override
   public String describeForSelfBuff() {
      return this.restrictedTo.colourTaggedName(true) + " [purple]heroes only[cu]";
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return restrActor(this.restrictedTo);
   }

   public static Actor restrActor(HeroCol restrictedTo) {
      TextureRegion tr = getImage(restrictedTo);
      return tr != null
         ? new ImageActor(getImage(restrictedTo))
         : new ImageActor(ImageUtils.loadExt("trigger/equip-stuff/restriction/default"), restrictedTo.col);
   }

   public static TextureRegion getImage(HeroCol col) {
      return ImageUtils.loadExtNull("trigger/equip-stuff/restriction/" + col.colName);
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return this.restrictedTo.getCollisionBit();
   }
}
