package com.tann.dice.screens.dungeon.panels.combatEffects.simplePanelImage;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectController;

public class SimpleGroupPanelImageController extends CombatEffectController {
   float impactDuration;
   float extraDuration;
   Targetable targetable;
   Ent target;
   TextureRegion image;
   FightLog fightLog;

   public SimpleGroupPanelImageController(float impactDuration, float extraDuration, Targetable targetable, Ent target, TextureRegion image, FightLog fightLog) {
      this.fightLog = fightLog;
      this.impactDuration = impactDuration;
      this.extraDuration = extraDuration;
      this.targetable = targetable;
      this.target = target;
      this.image = image;
   }

   @Override
   protected void start() {
      for (EntState es : this.fightLog
         .getSnapshot(FightLog.Temporality.Visual)
         .getActualTargets(this.target, this.targetable.getBaseEffect(), this.targetable.getSource())) {
         SimplePanelImage simplePanelImage = new SimplePanelImage(es.getEnt(), this.image, this.targetable.getBaseEffect());
         simplePanelImage.start(this.fightLog);
      }
   }

   @Override
   protected float getImpactDuration() {
      return this.impactDuration * OptionUtils.unkAnim();
   }

   @Override
   protected float getExtraDuration() {
      return this.extraDuration * OptionUtils.unkAnim();
   }
}
