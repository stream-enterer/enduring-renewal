package com.tann.dice.gameplay.trigger.global.linked;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.util.Colours;

public class GlobalHeroTier extends GlobalLinked {
   final int levelRequired;
   final Personal linked;

   public GlobalHeroTier(int levelRequired, Personal linked) {
      super(linked);
      this.levelRequired = levelRequired;
      this.linked = linked;
   }

   @Override
   public String describeForSelfBuff() {
      return "Tier " + this.levelRequired + " heroes:[n]" + this.linked.describeForSelfBuff();
   }

   @Override
   public Personal getLinkedPersonal(EntState entState) {
      return entState.getEnt() instanceof Hero && ((Hero)entState.getEnt()).getHeroType().level == this.levelRequired
         ? this.linked
         : super.getLinkedPersonal(entState);
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return DipPanel.makeSidePanelGroup(big, EntPanelInventory.makeLevelTag(this.levelRequired, Colours.text), this.linked, Colours.green);
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return this.linked.getCollisionBits(true) | Collision.SPECIFIC_LEVEL;
   }

   @Override
   public GlobalLinked splice(Personal newCenter) {
      return new GlobalHeroTier(this.levelRequired, newCenter);
   }
}
