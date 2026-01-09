package com.tann.dice.gameplay.trigger.personal.equipRestrict;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;

public class EquipRestrictNamed extends EquipRestrict {
   final String htn;

   public EquipRestrictNamed(String htn) {
      this.htn = htn;
   }

   public boolean allowEquip(Ent ent) {
      if (!(ent instanceof Hero)) {
         return false;
      } else {
         Hero h = (Hero)ent;
         return h.getHeroType().getName().equalsIgnoreCase(this.htn);
      }
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return new Pixl(3, 3).border(Colours.red).image(HeroTypeLib.byName(this.htn).portrait).pix();
   }

   @Override
   public String describeForSelfBuff() {
      return this.htn + " only";
   }

   @Override
   public boolean unequip(Ent ent) {
      return !this.allowEquip(ent);
   }
}
