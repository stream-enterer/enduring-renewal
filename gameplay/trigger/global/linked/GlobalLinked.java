package com.tann.dice.gameplay.trigger.global.linked;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.trigger.Trigger;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import java.util.List;

public abstract class GlobalLinked extends Global {
   final Trigger link;

   protected GlobalLinked(Trigger link) {
      this.link = link;
   }

   @Override
   public boolean isOnPick() {
      return this.link.isOnPick();
   }

   @Override
   public final boolean allTurnsOnly() {
      return this.overrideAllTurnsOnly() || this.link.allTurnsOnly();
   }

   protected boolean overrideAllTurnsOnly() {
      return false;
   }

   @Override
   public final boolean allLevelsOnly() {
      return this instanceof GlobalLevelRequirement || this.link.allLevelsOnly();
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return this.link.getCollisionBits(player);
   }

   @Override
   public final List<Keyword> getReferencedKeywords() {
      return this.link.getReferencedKeywords();
   }

   @Override
   public final TextureRegion getSpecialImage() {
      return this.link instanceof Personal ? ((Personal)this.link).getSpecialImage() : null;
   }

   @Override
   public boolean isMultiplable() {
      return this.link.isMultiplable();
   }

   @Override
   public String hyphenTag() {
      return this.link.hyphenTag();
   }

   @Override
   public boolean afterItems() {
      return this.link instanceof AffectSides && this.link.getPriority() > -10.0F;
   }

   public Trigger linkDebug() {
      return this.link;
   }

   public GlobalLinked splice(Global newCenter) {
      return null;
   }

   public GlobalLinked splice(Personal newCenter) {
      return null;
   }
}
