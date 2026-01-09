package com.tann.dice.gameplay.trigger.personal;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;

public class Dodge extends Personal {
   @Override
   public boolean dodgeAttack() {
      return true;
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      return "stealth";
   }

   @Override
   public String describeForSelfBuff() {
      return "Dodge all damage and enemy effects " + KUtils.describeThisTurn();
   }

   @Override
   public String[] getSound() {
      return Sounds.stealth;
   }

   @Override
   public float getEffectTier(int pips, int tier) {
      return 0.2F + tier * 0.8F;
   }

   @Override
   public void drawOnPanel(Batch batch, EntPanelCombat entPanelCombat) {
      float alpha = com.tann.dice.Main.pulsateFactor() * 0.06F + 0.3F;
      batch.setColor(Colours.withAlpha(Colours.grey, alpha));
      Draw.fillActor(batch, entPanelCombat);
   }

   @Override
   public boolean isRecommended(EntState sourceState, EntState targetPresent, EntState targetFuture) {
      return targetFuture.getBlockableDamageTaken() > 0 && !targetPresent.hasPersonal(Dodge.class);
   }

   @Override
   public boolean singular() {
      return true;
   }
}
