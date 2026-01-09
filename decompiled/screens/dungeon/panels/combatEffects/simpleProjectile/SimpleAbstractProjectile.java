package com.tann.dice.screens.dungeon.panels.combatEffects.simpleProjectile;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectActor;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.util.Tann;

public abstract class SimpleAbstractProjectile extends CombatEffectActor {
   protected float travelTime;
   public static final float MAX_BONUS_X = 3.0F;
   public static final float BONUS_Y_GAP = 4.0F;
   private int damage;
   protected Ent target;
   protected Vector2 source;
   protected boolean blocked;
   protected boolean dodged;
   protected int endX;
   protected int endY;
   protected float travelRotation;
   protected float currentTravel;
   protected boolean player;
   public static final float DEFAULT_DELAY = 0.0F;
   EntPanelCombat targetPanel;
   float bonusEndX;
   float bonusEndY;

   public SimpleAbstractProjectile(Ent source, Ent target, int damage, float duration) {
      this.player = source == null || source.isPlayer();
      if (source == null) {
         this.source = new Vector2(-10.0F, com.tann.dice.Main.height / 2);
      } else {
         EntPanelCombat sourcePan = source.getEntPanel();
         Vector2 panelPos = Tann.getAbsoluteCoordinates(sourcePan);
         this.source = new Vector2(
            panelPos.x + (this.player ? sourcePan.getWidth() : 0.0F) - this.getImage().getRegionWidth() / 2, panelPos.y + sourcePan.getHeight() / 2.0F
         );
      }

      this.target = target;
      this.damage = damage;
      this.travelTime = duration * (com.tann.dice.Main.isPortrait() ? 0.66F : 1.0F);
   }

   public SimpleAbstractProjectile(Vector2 source, Ent target, int damage, float duration) {
      this.player = true;
      this.source = source;
      Tann.intify(source);
      this.target = target;
      this.damage = damage;
      this.travelTime = duration;
   }

   @Override
   public final void start(FightLog fightLog) {
      EntState es = fightLog.getSnapshot(FightLog.Temporality.Visual).getState(this.target);
      if (es != null && !es.isDead()) {
         Tann.delay(
            0.0F,
            new Runnable() {
               @Override
               public void run() {
                  SimpleAbstractProjectile.this.setSize(
                     SimpleAbstractProjectile.this.getImage().getRegionWidth(), SimpleAbstractProjectile.this.getImage().getRegionHeight()
                  );
                  SimpleAbstractProjectile.this.setupTargetVector(SimpleAbstractProjectile.this.target.getEntPanel());
                  SimpleAbstractProjectile.this.travelRotation = (float)Math.toDegrees(
                     Math.atan2(
                        SimpleAbstractProjectile.this.endY - SimpleAbstractProjectile.this.source.y,
                        SimpleAbstractProjectile.this.endX - SimpleAbstractProjectile.this.source.x
                     )
                  );
                  SimpleAbstractProjectile.this.dodged = CombatEffectActor.isDodged(SimpleAbstractProjectile.this.target);
                  SimpleAbstractProjectile.this.blocked = CombatEffectActor.isBlocked(
                     SimpleAbstractProjectile.this.damage, SimpleAbstractProjectile.this.target
                  );
                  float distMultiplier = 0.0F;
                  if (SimpleAbstractProjectile.this.dodged) {
                     distMultiplier = -10.0F;
                  } else if (SimpleAbstractProjectile.this.blocked) {
                     distMultiplier = com.tann.dice.Main.isPortrait() ? 0.3F : 1.0F;
                  }

                  if (SimpleAbstractProjectile.this.player) {
                     SimpleAbstractProjectile.this.endX = (int)(SimpleAbstractProjectile.this.endX - 12.0F * distMultiplier);
                  } else {
                     SimpleAbstractProjectile.this.endX = (int)(SimpleAbstractProjectile.this.endX + 10.0F * distMultiplier);
                  }

                  SimpleAbstractProjectile.this.setPosition(-500.0F, 0.0F);
                  DungeonScreen.get().addActor(SimpleAbstractProjectile.this);
                  SimpleAbstractProjectile.this.internalStart();
               }
            }
         );
      }
   }

   protected abstract void internalStart();

   protected void setupTargetVector(EntPanelCombat targetPan) {
      this.targetPanel = targetPan;
      this.bonusEndX = (Tann.random() * 3.0F + 1.0F) * (this.player ? 1 : -1);
      this.bonusEndY = 4.0F + (targetPan.getHeight() - 8.0F) * Tann.random();
      this.updateTargetVector();
   }

   protected void updateTargetVector() {
      Vector2 endPos = Tann.getAbsoluteCoordinates(this.targetPanel);
      if (!this.targetPanel.hasParent() || this.target.getFightLog().getState(FightLog.Temporality.Visual, this.targetPanel.ent) == null) {
         endPos = new Vector2(DungeonScreen.get().getWidth() - 20.0F, DungeonScreen.get().getHeight() - 20.0F);
      }

      this.endX = (int)(
         endPos.x
            - this.getImage().getRegionWidth()
            + this.bonusEndX
            + (this.player ? 0.0F : this.targetPanel.getWidth())
            + this.getBonusX() * (this.target.isPlayer() ? -1 : 1)
      );
      this.endY = (int)(endPos.y + this.bonusEndY - this.getImage().getRegionHeight() / 2.0F);
   }

   @Override
   protected float getImpactDurationInternal() {
      return this.travelTime;
   }

   public void act(float delta) {
      super.act(delta);
      if (this.currentTravel < this.travelTime) {
         this.currentTravel += delta;
         this.setPosition(
            (int)(this.source.x + (this.endX - this.source.x) * this.currentTravel / this.travelTime),
            (int)(this.source.y + (this.endY - this.source.y) * this.currentTravel / this.travelTime)
         );
         if (this.currentTravel >= this.travelTime) {
            this.currentTravel = this.travelTime;
            this.setPosition(
               (int)(this.source.x + (this.endX - this.source.x) * this.currentTravel / this.travelTime),
               (int)(this.source.y + (this.endY - this.source.y) * this.currentTravel / this.travelTime)
            );
            this.arrowImpact();
         }
      }
   }

   protected abstract TextureRegion getImage();

   protected abstract void arrowImpact();

   public void draw(Batch batch, float parentAlpha) {
      super.draw(batch, parentAlpha);
      batch.setColor(this.getColor());
      batch.draw(
         this.getImage(),
         (int)this.getX(),
         (int)this.getY(),
         this.getImage().getRegionWidth() / 2.0F,
         this.getImage().getRegionHeight() / 2.0F,
         this.getImage().getRegionWidth(),
         this.getImage().getRegionHeight(),
         1.0F,
         1.0F,
         this.travelRotation
      );
   }

   protected float getBonusX() {
      return 0.0F;
   }
}
