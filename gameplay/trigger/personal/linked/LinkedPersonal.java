package com.tann.dice.gameplay.trigger.personal.linked;

import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.trigger.personal.Personal;
import java.util.List;

public abstract class LinkedPersonal extends Personal {
   private final Personal link;

   public LinkedPersonal(Personal link) {
      this.link = link;
   }

   @Override
   public List<Keyword> getReferencedKeywords() {
      return this.link.getReferencedKeywords();
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return this.link.getCollisionBits();
   }

   @Override
   public boolean isMultiplable() {
      return this.link.isMultiplable();
   }

   public Personal getLinkDebug() {
      return this.link;
   }

   @Override
   public boolean allTurnsOnly() {
      return this.link.allTurnsOnly();
   }

   public abstract Personal splice(Personal var1);
}
