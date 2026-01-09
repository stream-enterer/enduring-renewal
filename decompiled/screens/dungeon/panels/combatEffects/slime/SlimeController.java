package com.tann.dice.screens.dungeon.panels.combatEffects.slime;

import com.badlogic.gdx.math.Vector2;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectController;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Tann;
import java.util.List;

public class SlimeController extends CombatEffectController {
   Ent source;
   List<EntState> targets;
   int damage;
   FightLog fightLog;

   public SlimeController(Targetable targetable, Ent target, Ent source, FightLog fightLog) {
      this.fightLog = fightLog;
      this.damage = targetable.getBaseEffect().getValue();
      this.source = source;
      this.targets = fightLog.getSnapshot(FightLog.Temporality.Present).getActualTargets(target, targetable.getBaseEffect(), targetable.getSource());
      if (this.targets.size() == 0 && target != null) {
         this.targets.add(fightLog.getSnapshot(FightLog.Temporality.Present).getState(target));
      }
   }

   @Override
   protected void start() {
      boolean shielded = true;

      for (EntState es : this.targets) {
         if (this.fightLog.getSnapshot(FightLog.Temporality.Visual).getState(es.getEnt()).getShields() < this.damage) {
            shielded = false;
            break;
         }
      }

      String[] baseImpactSound = this.source.getSize() == EntSize.huge ? Sounds.impacts : Sounds.punches;
      String[] impactSound = shielded ? Sounds.clangs : baseImpactSound;
      switch (this.source.getSize()) {
         case small:
         default:
            Sounds.playSound(Sounds.slimeMovesmall);
            Sounds.playSoundDelayed(impactSound, 0.4F, 2.0F, this.getImpactDuration());
            break;
         case big:
            Sounds.playSound(Sounds.slimeMoveBig);
            Sounds.playSoundDelayed(impactSound, 0.7F, 1.5F, this.getImpactDuration());
            break;
         case huge:
            Sounds.playSound(Sounds.slimeMoveHuge);
            Sounds.playSoundDelayed(impactSound, 1.0F, 1.0F, this.getImpactDuration());
      }

      EntPanelCombat panel = this.source.getEntPanel();
      Vector2 sourcePanel = Tann.getAbsoluteCoordinates(panel, Tann.TannPosition.Left).cpy();

      for (EntState target : this.targets) {
         if (!this.fightLog.getSnapshot(FightLog.Temporality.Visual).getState(target.getEnt()).isDead()) {
            SlimeActor slimeActor = new SlimeActor(
               sourcePanel,
               Tann.getAbsoluteCoordinates(target.getEnt().getEntPanel(), Tann.TannPosition.Right).add(-15.0F, 0.0F).cpy(),
               target.getEnt(),
               this.damage,
               this.source.entType.size,
               this.targets.size() == 1
            );
            DungeonScreen.get().addActor(slimeActor);
            slimeActor.start(this.fightLog);
         }
      }
   }

   @Override
   protected float getExtraDuration() {
      return SlimeActor.GET_EXTRA_DURATION(this.source.getSize()) * OptionUtils.unkAnim();
   }

   @Override
   protected float getImpactDuration() {
      return SlimeActor.GET_IMPACT_DURATION(this.source.getSize()) * OptionUtils.unkAnim();
   }
}
