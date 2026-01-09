package com.tann.dice.gameplay.trigger.personal.affectSideModular.named;

import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;

public class Exerted extends AffectSides {
   public Exerted(EntSize size) {
      super(new ReplaceWith(size.getExerted()));
   }

   @Override
   public boolean autoLockLite() {
      return true;
   }

   @Override
   public String describeForSelfBuff() {
      return "Exerted";
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      return "exerted";
   }
}
