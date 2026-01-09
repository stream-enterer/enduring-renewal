package com.tann.dice.screens.dungeon.panels.combatEffects;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;

public class CombatEffect {
   private static final int ENEMY_MOVE_DISTANCE = 10;
   private long startedTime;
   protected boolean started;
   protected boolean impacted;
   protected boolean finished;
   Ent source;
   public CombatEffectController controller;

   public CombatEffect(CombatEffectController combatEffectGroup, Ent source) {
      this.controller = combatEffectGroup;
      this.source = source;
   }

   public void internalStart() {
      this.started = true;
      this.startedTime = System.currentTimeMillis();
      if (this.source != null && !this.source.isPlayer()) {
         this.enemyStart();
      } else {
         this.playerStart();
      }
   }

   protected void enemyStart() {
      EntPanelCombat sourcePanel = this.source.getEntPanel();
      Tann.finishAllActions(sourcePanel);
      this.source.getEntPanel().setAnimating(true);
      if (OptionUtils.skipForwardsBackForEnemies()) {
         sourcePanel.addAction(Actions.sequence(Actions.run(new Runnable() {
            @Override
            public void run() {
               CombatEffect.this.controller.start();
            }
         }), Actions.delay(this.controller.getImpactDuration()), Actions.run(this.impactRunnable()), Actions.run(this.finishRunnable())));
      } else {
         sourcePanel.addAction(
            Actions.sequence(
               new Action[]{
                  Actions.moveTo(
                     sourcePanel.getPreferredX() - 10.0F, sourcePanel.getPreferredY(), this.getEnemyMoveTime(this.source.getSize()), Interpolation.pow2Out
                  ),
                  Actions.run(new Runnable() {
                     @Override
                     public void run() {
                        CombatEffect.this.controller.start();
                     }
                  }),
                  Actions.delay(this.controller.getImpactDuration()),
                  Actions.run(this.impactRunnable()),
                  Actions.delay(this.controller.getExtraDuration()),
                  Actions.run(this.moveBackRunnable())
               }
            )
         );
      }
   }

   private Runnable moveBackRunnable() {
      return new Runnable() {
         @Override
         public void run() {
            EntPanelCombat sourcePanel = CombatEffect.this.source.getEntPanel();
            sourcePanel.setAnimating(false);
            sourcePanel.addAction(
               Actions.sequence(
                  Actions.moveTo(
                     sourcePanel.getPreferredX(),
                     sourcePanel.getPreferredY(),
                     CombatEffect.this.getEnemyMoveTime(CombatEffect.this.source.getSize()),
                     Interpolation.pow2Out
                  ),
                  Actions.run(CombatEffect.this.finishRunnable())
               )
            );
         }
      };
   }

   protected void playerStart() {
      try {
         this.controller.start();
      } catch (Exception var2) {
         com.tann.dice.Main.getCurrentScreen().showDialog("anim err - " + var2.getClass().getSimpleName());
         var2.printStackTrace();
      }

      Tann.delay(this.controller.getImpactDuration(), new Runnable() {
         @Override
         public void run() {
            CombatEffect.this.impacted = true;
         }
      });
      Tann.delay(this.controller.getImpactDuration() + this.controller.getExtraDuration(), new Runnable() {
         @Override
         public void run() {
            CombatEffect.this.finished = true;
         }
      });
   }

   private Runnable impactRunnable() {
      return new Runnable() {
         @Override
         public void run() {
            CombatEffect.this.impacted = true;
         }
      };
   }

   private Runnable finishRunnable() {
      return new Runnable() {
         @Override
         public void run() {
            EntPanelCombat a = CombatEffect.this.source.getEntPanel();
            if (a != null) {
               a.setAnimating(false);
            }

            CombatEffect.this.finished = true;
         }
      };
   }

   public boolean isStarted() {
      return this.started;
   }

   public boolean isImpacted() {
      return this.impacted;
   }

   public boolean isFinished() {
      if (this.finished) {
         return true;
      } else if (this.source != null && !this.source.getEntPanel().hasParent() && !DungeonScreen.get().anyDeathAnimationsOngoing()) {
         return true;
      } else if (this.stalled()) {
         this.finished = true;
         TannLog.error("Stalled command, skipping: " + this + this.source + this.controller);
         return true;
      } else {
         return this.finished;
      }
   }

   private boolean stalled() {
      return this.started && System.currentTimeMillis() - this.startedTime > 4000L;
   }

   private float getEnemyMoveTime(EntSize size) {
      float mult = OptionUtils.enemyAnim();
      switch (size) {
         case small:
            return 0.2F * mult;
         case reg:
            return 0.25F * mult;
         case big:
            return 0.35F * mult;
         case huge:
            return 0.4F * mult;
         default:
            return 3.0F * mult;
      }
   }
}
