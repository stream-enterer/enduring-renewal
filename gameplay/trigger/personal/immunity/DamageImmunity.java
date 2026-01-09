package com.tann.dice.gameplay.trigger.personal.immunity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.lang.Words;

public class DamageImmunity extends Immunity {
   final boolean poison;
   final boolean regular;
   final boolean show;

   public DamageImmunity(boolean poison, boolean regular) {
      this(poison, regular, true);
   }

   public DamageImmunity(boolean poison, boolean regular, boolean show) {
      this.poison = poison;
      this.regular = regular;
      this.show = show;
   }

   @Override
   public boolean immuneToDamage(boolean poison) {
      return poison ? this.poison : this.regular;
   }

   @Override
   public boolean poisonSpecificImmunity() {
      return this.poison && !this.regular;
   }

   @Override
   public String getImageName() {
      if (!this.regular) {
         return "poisonImmunity";
      } else {
         return this.poison && this.regular ? "immune" : super.getImageName();
      }
   }

   @Override
   public boolean showInEntPanelInternal() {
      return this.show;
   }

   @Override
   public String describeForGiveBuff(Eff source) {
      return source.getTargetingType() == TargetingType.Group
         ? "All allies become " + this.describeForSelfBuff()
         : "Target " + Words.entName(source, null) + " becomes " + this.describeForSelfBuff().toLowerCase();
   }

   @Override
   public String describeForSelfBuff() {
      if (this.poison && this.regular) {
         return "Immune to damage";
      } else if (this.poison && !this.regular) {
         return "Immune to poison";
      } else {
         return this.regular && !this.poison ? "Immune to non-poison damage" : "Damage immunity gone wrong!";
      }
   }

   @Override
   public boolean isRecommended(EntState sourceState, EntState targetPresent, EntState targetFuture) {
      return this.regular
            && targetFuture.getBlockableDamageTaken() + targetFuture.getDamageBlocked()
               > targetPresent.getBlockableDamageTaken() + targetPresent.getDamageBlocked()
         || this.poison && targetFuture.getPoisonDamageTaken(true) > targetPresent.getPoisonDamageTaken(true);
   }

   @Override
   public void drawOnPanel(Batch batch, EntPanelCombat entPanelCombat) {
      if (this.regular && this.show) {
         float alpha = com.tann.dice.Main.pulsateFactor() * 0.06F + 0.3F;
         batch.setColor(Colours.withAlpha(Colours.grey, alpha));
         Draw.fillActor(batch, entPanelCombat);
      }
   }

   @Override
   public String[] getSound() {
      return Sounds.stealth;
   }

   @Override
   public float getPriority() {
      return 10.0F;
   }

   @Override
   public float getEffectTier(int pips, int tier) {
      return 0.2F + tier * 0.8F;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      if (this.poison && !this.regular) {
         return Collision.POISON | Collision.CURSED_MODE;
      } else {
         return this.poison ? Collision.POISON : super.getCollisionBits(player);
      }
   }

   @Override
   public boolean singular() {
      return this.poison && this.regular;
   }
}
