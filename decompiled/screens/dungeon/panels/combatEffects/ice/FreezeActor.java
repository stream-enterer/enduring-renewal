package com.tann.dice.screens.dungeon.panels.combatEffects.ice;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectActor;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;

public class FreezeActor extends CombatEffectActor {
   static final float IN = 0.1F;
   static final float HOLD = 0.3F;
   static final float OUT = 0.3F;
   static final float ALPHA = 0.65F;
   Ent target;

   public FreezeActor(Ent target) {
      this.target = target;
   }

   @Override
   protected void start(FightLog fightLog) {
      Sounds.playSoundDelayed(Sounds.deboost, 1.0F, 1.0F, this.getImpactDuration() * 0.3F);
      Actor a = new Actor() {
         public void draw(Batch batch, float parentAlpha) {
            batch.setColor(this.getColor());
            Images.icePatch.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
         }
      };
      int border = 3;
      EntPanelCombat targetPanel = this.target.getEntPanel();
      a.setSize(targetPanel.getWidth() + border * 2, targetPanel.getHeight() + border * 2);
      targetPanel.addActor(a);
      a.setPosition(-border, -border);
      Color col = Colours.shiftedTowards(Colours.light, Colours.blue, 0.5F);
      a.setColor(Colours.withAlpha(col, 0.0F));
      a.addAction(Actions.sequence(Actions.alpha(0.65F, 0.1F), Actions.delay(0.3F), Tann.fadeAndRemove(0.3F)));
   }

   @Override
   protected float getImpactDurationInternal() {
      return 0.25F;
   }

   @Override
   protected float getExtraDurationInternal() {
      return 0.45000002F;
   }
}
