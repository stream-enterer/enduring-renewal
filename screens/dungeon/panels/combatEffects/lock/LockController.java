package com.tann.dice.screens.dungeon.panels.combatEffects.lock;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectController;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.LockOverlay;

public class LockController extends CombatEffectController {
   Ent source;
   Ent target;
   int damage;

   public LockController(Ent source, Ent target, int damage) {
      this.source = source;
      this.target = target;
      this.damage = damage;
   }

   @Override
   protected void start() {
      LockOverlay lo = new LockOverlay(this.target.getEntPanel(), true);
      lo.setBackground(new Color(1.0F, 1.0F, 1.0F, 0.0F));
      lo.setColor(Colours.withAlpha(Colours.grey, 0.0F));
      lo.addAction(Actions.sequence(Actions.color(Colours.grey, this.getImpactDuration()), Actions.run(new Runnable() {
         @Override
         public void run() {
            Sounds.playSound(Sounds.iceExplode);
         }
      }), Actions.color(Colours.withAlpha(Colours.grey, 0.0F), this.getExtraDuration()), Actions.removeActor()));
   }

   @Override
   protected float getImpactDuration() {
      return 0.15F * OptionUtils.unkAnim();
   }

   @Override
   protected float getExtraDuration() {
      return 0.4F * OptionUtils.unkAnim();
   }
}
