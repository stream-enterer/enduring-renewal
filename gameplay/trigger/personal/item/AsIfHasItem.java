package com.tann.dice.gameplay.trigger.personal.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.statics.sound.Sounds;
import java.util.List;

public class AsIfHasItem extends Personal {
   final Item item;

   public AsIfHasItem(Item item) {
      this.item = item;
   }

   @Override
   public String describeForSelfBuff() {
      return this.item.getDescription();
   }

   @Override
   public boolean skipTraitPanel() {
      for (Personal personalTrigger : this.item.getPersonals()) {
         if (personalTrigger.getAbility() != null) {
            return false;
         }
      }

      return true;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return big ? this.item.makeChoosableActor(big, 0) : this.item.makeImageActor();
   }

   @Override
   protected TextureRegion overrideImage() {
      return this.item.getImage();
   }

   @Override
   public List<Personal> getLinkedPersonalsNoSnapshot(EntState entState) {
      return this.item.getPersonals();
   }

   @Override
   public List<Personal> getLinkedPersonals(Snapshot snapshot, EntState entState) {
      return this.getLinkedPersonalsNoSnapshot(entState);
   }

   @Override
   public boolean isGenerated() {
      return true;
   }

   @Override
   public boolean skipCalc() {
      return true;
   }

   @Override
   public Ability getAbility() {
      return this.item.getAbility();
   }

   @Override
   public String[] getSound() {
      return Sounds.boost;
   }

   @Override
   public boolean isMultiplable() {
      return this.item.isMultiplable(true);
   }
}
