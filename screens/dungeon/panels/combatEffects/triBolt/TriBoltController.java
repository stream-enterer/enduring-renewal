package com.tann.dice.screens.dungeon.panels.combatEffects.triBolt;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectActor;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectController;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.CrossActor;
import com.tann.dice.util.Tann;
import java.util.List;

public class TriBoltController extends CombatEffectController {
   static final int offset = 12;
   float hitTime = 0.6F;
   float finishTime = 0.4F;
   Targetable targetable;
   Ent target;
   Ent source;
   FightLog fightLog;

   public TriBoltController(Targetable targetable, Ent target, Ent source, FightLog fightLog) {
      this.targetable = targetable;
      this.target = target;
      this.source = source;
      this.fightLog = fightLog;
   }

   @Override
   protected void start() {
      Sounds.playSound(Sounds.tribolt);
      Sounds.playSoundDelayed(Sounds.slice, 1.0F, 1.0F, this.getImpactDuration());
      List<EntState> targets = this.fightLog
         .getSnapshot(FightLog.Temporality.Present)
         .getActualTargets(this.target, this.targetable.getBaseEffect(), this.source);
      float top = CombatEffectActor.getTopMost(targets);
      float bot = CombatEffectActor.getBotMost(targets);
      float mid = (top + bot) / 2.0F;
      EntPanelCombat sourcePanel = this.source.getEntPanel();
      Vector2 sourcePanelPos = Tann.getAbsoluteCoordinates(sourcePanel).cpy();
      Vector2 sourceVec = sourcePanelPos.add(0.0F, sourcePanel.getHeight() / 2.0F);
      EntPanelCombat targetPanel = targets.get(0).getEnt().getEntPanel();

      for (EntState es : targets) {
         if (!this.fightLog.getSnapshot(FightLog.Temporality.Visual).getState(es.getEnt()).isDead()) {
            final TriBoltActor newBolt = new TriBoltActor();
            DungeonScreen.get().addActor(newBolt);
            newBolt.setPosition(sourceVec.x, sourceVec.y);
            EntPanelCombat panel = es.getEnt().getEntPanel();
            Vector2 targetPanelPos = Tann.getAbsoluteCoordinates(panel);
            newBolt.addAction(
               Actions.sequence(
                  Actions.moveTo(
                     targetPanelPos.x + targetPanel.getWidth() - 12.0F,
                     targetPanelPos.y + targetPanel.getHeight() / 2.0F,
                     this.getImpactDuration(),
                     Interpolation.pow2In
                  ),
                  Actions.run(new Runnable() {
                     @Override
                     public void run() {
                        CrossActor crossActor = new CrossActor();
                        crossActor.setSize(20.0F, 20.0F);
                        crossActor.setPosition(newBolt.getX() - crossActor.getWidth() / 2.0F, newBolt.getY() - crossActor.getHeight() / 2.0F);
                        crossActor.setColor(Colours.purple);
                        crossActor.addAction(Actions.sequence(Actions.fadeOut(TriBoltController.this.getExtraDuration()), Actions.removeActor()));
                        DungeonScreen.get().addActor(crossActor);
                     }
                  }),
                  Actions.removeActor()
               )
            );
         }
      }
   }

   @Override
   protected float getImpactDuration() {
      return this.hitTime * OptionUtils.unkAnim();
   }

   @Override
   protected float getExtraDuration() {
      return this.finishTime * OptionUtils.unkAnim();
   }
}
