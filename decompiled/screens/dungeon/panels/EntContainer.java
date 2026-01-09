package com.tann.dice.screens.dungeon.panels;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.background.BackgroundHolder;
import com.tann.dice.screens.dungeon.panels.entPanel.EntPanelCombat;
import com.tann.dice.screens.shaderFx.DeathType;
import com.tann.dice.statics.bullet.BulletStuff;
import com.tann.dice.util.Chrono;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannFont;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.Flasher;
import com.tann.dice.util.ui.TextEfficientGroup;
import com.tann.dice.util.ui.action.PixAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntContainer extends TextEfficientGroup {
   public static final int width = 84;
   static final int BUTT_GAP = 2;
   private List<Ent> entities = new ArrayList<>();
   private Map<Ent, Boolean> forwardsMap;
   private List<Monster> reinforcementsCache;
   boolean friendly;
   public static final int MARGIN = 2;
   final boolean cachedAv;
   final boolean cachedAvTop;
   boolean entitiesSetBefore;
   float nextRearrange = 0.0F;
   long lastTargetableHeld;
   long deathAnimFinish = 0L;
   boolean inPlace = true;
   Runnable inPlaceRunnable;

   public EntContainer(boolean friendly) {
      this.setTransform(false);
      this.friendly = friendly;
      float height = com.tann.dice.Main.height - DungeonScreen.getBottomButtonHeight() - 2;
      if (com.tann.dice.Main.isPortrait()) {
         height = (float)(height * 0.5);
      }

      this.cachedAv = OptionLib.GAP.c() > 0;
      this.cachedAvTop = OptionLib.GAP.c() == 2;
      if (!friendly && this.cachedAv) {
         height *= 0.75F;
      }

      this.setSize(84.0F, (int)height);
      this.setPosition(this.getPreferredX(), this.getPreferredY());
   }

   public float getPreferredX() {
      int extraGap = getPanelTuckback(this.friendly) * (this.friendly ? -1 : 1);
      return (this.friendly ? 2 + com.tann.dice.Main.self().notch(3) : com.tann.dice.Main.width - 84 - 2 - com.tann.dice.Main.self().notch(1)) + extraGap;
   }

   public float getPreferredY() {
      if (this.friendly || !this.cachedAv) {
         return DungeonScreen.getBottomButtonHeight() + 2;
      } else {
         return this.cachedAvTop ? 0.0F : com.tann.dice.Main.height - this.getHeight();
      }
   }

   public void slideIn(Vector2 dist, final Runnable after) {
      this.clearActions();
      dist.scl(1.0F / com.tann.dice.Main.scale);
      this.setPosition(this.getPreferredX() + dist.x, this.getPreferredY() + dist.y);
      this.addAction(
         Actions.sequence(
            PixAction.moveTo((int)this.getPreferredX(), (int)this.getPreferredY(), BackgroundHolder.GET_MOVE_SPD(), BackgroundHolder.MOVE_TERP),
            Actions.delay(0.05F)
         )
      );
      if (after != null) {
         this.addAction(Actions.after(new RunnableAction() {
            public void run() {
               after.run();
            }
         }));
      }
   }

   public void slideAway() {
      Tann.slideAway(this, this.getSlideDir(), BulletStuff.getPanelSlideAmount() + 5, false);
   }

   private Tann.TannPosition getSlideDir() {
      return this.friendly ? Tann.TannPosition.Left : Tann.TannPosition.Right;
   }

   private void setupPreferredPositions(List<Ent> alive, boolean hasReinforcement) {
      float heightAvailable = this.getHeight();

      for (Ent de : alive) {
         heightAvailable -= de.getEntPanel().getHeight();
      }

      if (hasReinforcement) {
         heightAvailable -= TannFont.font.getHeight() + 4;
      }

      float gap = heightAvailable / (alive.size() + 1 + (hasReinforcement ? 1 : 0));
      float currentY = gap;

      for (int i = alive.size() - 1; i >= 0; i--) {
         Ent de = alive.get(i);
         EntPanelCombat panel = de.getEntPanel();
         panel.setMargin(gap / 2.0F - 3.0F);
         panel.setPreferredY((int)currentY);
         currentY += gap;
         currentY += panel.getHeight();
      }
   }

   private void rearrangeEntities(boolean setup, final List<Monster> reinforcements) {
      List<Ent> alive = new ArrayList<>();

      for (Ent de : this.entities) {
         if (!de.getState(FightLog.Temporality.Visual).isDead()) {
            alive.add(de);
         }
      }

      this.setupPreferredPositions(alive, reinforcements.size() > 0);
      if (!(this.nextRearrange > 0.0F)) {
         float TARGETABLE_HOLD_DURATION = 0.1F;
         float timeSinceHeld = (float)(System.currentTimeMillis() - this.lastTargetableHeld) / 1000.0F;
         if (timeSinceHeld < 0.1F) {
            this.nextRearrange = 0.1F - timeSinceHeld;
         } else {
            this.clearChildren();
            float duration = setup ? 0.0F : 0.25F;

            for (int i = 0; i < alive.size(); i++) {
               Ent dex = alive.get(i);
               EntPanelCombat panel = dex.getEntPanel();
               this.addActor(panel);
               if (dex.isPlayer()) {
                  panel.clearActions();
               }

               panel.addAction(PixAction.moveTo((int)panel.getPreferredX(), (int)panel.getPreferredY(), duration, Chrono.i));
            }

            for (Ent dex : alive) {
               Ent summoner = dex.getSummonedBy();
               if (summoner != null) {
                  EntPanelCombat panel = dex.getEntPanel();
                  panel.toBack();
                  EntPanelCombat summonerPanel = summoner.getEntPanel();
                  panel.setPosition(summonerPanel.getX(), summonerPanel.getY());
                  dex.setSummonedBy(null);
               }
            }

            if (reinforcements.size() > 0) {
               Group reinforcementsPanel = new Pixl(2, 2).border(Colours.purple).text("[grey]Reinforcements: " + reinforcements.size()).pix();
               this.addActor(reinforcementsPanel);
               reinforcementsPanel.addListener(new TannListener() {
                  @Override
                  public boolean action(int button, int pointer, float x, float y) {
                     String reinfString = "";

                     for (int i = reinforcements.size() - 1; i >= 0; i--) {
                        Monster reinforcement = reinforcements.get(i);
                        reinfString = reinfString + reinforcement.getName(true) + "[n]";
                     }

                     if (reinforcements.size() > 1) {
                        reinfString = reinfString + "[grey]^next";
                     }

                     Pixl p = new Pixl(3, 3).border(Colours.purple).text(reinfString);
                     Actor a = p.pix();
                     com.tann.dice.Main.getCurrentScreen().pushAndCenter(a);
                     return true;
                  }
               });
               int x = (int)(this.getWidth() / 2.0F - reinforcementsPanel.getWidth() / 2.0F);
               reinforcementsPanel.setPosition(x, this.getHeight() - reinforcementsPanel.getHeight() - 1.0F);
               if (this.reinforcementsCache != null && this.reinforcementsCache.size() > 0 && reinforcements.size() != this.reinforcementsCache.size()) {
                  reinforcementsPanel.addActor(new Flasher(reinforcementsPanel, Colours.light));
               }
            }
         }
      }
   }

   public void setEntities(Snapshot snapshot, List<? extends Ent> entities, List<Monster> reinforcements) {
      Map<Ent, Boolean> newForwards = this.getForwardMap(snapshot, entities);
      if (!this.entities.equals(entities) || !newForwards.equals(this.forwardsMap) || !this.reinforcementsCache.equals(reinforcements)) {
         for (Monster m : reinforcements) {
            m.setSummonedBy(null);
         }

         List<Ent> newlyDead = null;
         if (this.forwardsMap != null) {
            newlyDead = this.getNewlyDead(this.forwardsMap, newForwards);
            newlyDead.removeAll(reinforcements);
         }

         this.forwardsMap = newForwards;
         this.reinforcementsCache = reinforcements;
         this.entities.clear();
         this.entities.addAll(entities);
         if (newlyDead != null && newlyDead.size() > 0) {
            this.deathAnimation(newlyDead, snapshot);
         } else {
            this.rearrangeEntities(!this.entitiesSetBefore, reinforcements);
         }

         this.entitiesSetBefore = true;
      }
   }

   public void act(float delta) {
      if (DungeonScreen.get().targetingManager.getSelectedTargetable() != null) {
         Targetable t = DungeonScreen.get().targetingManager.getSelectedTargetable();
         if (!t.getDerivedEffects().isFriendly()) {
            this.lastTargetableHeld = System.currentTimeMillis();
         }
      }

      if (this.nextRearrange > 0.0F) {
         this.nextRearrange -= delta;
         if (this.nextRearrange <= 0.0F) {
            this.rearrangeEntities(false, this.reinforcementsCache);
         }
      }

      super.act(delta);
   }

   private void deathAnimation(List<Ent> newlyDead, Snapshot snapshot) {
      for (Ent de : newlyDead) {
         EntState es = snapshot.getState(de);
         DeathType dt = DeathType.Alpha;
         if (es != null) {
            dt = es.getDeathType();
         }

         float time = dt.activate(de.getEntPanel());
         this.nextRearrange = Math.max(this.nextRearrange, time);
         this.deathAnimFinish = Math.max(this.deathAnimFinish, System.currentTimeMillis() + (long)(time * 1000.0F));
      }
   }

   private List<Ent> getNewlyDead(Map<Ent, Boolean> forwardsMap, Map<Ent, Boolean> newForwards) {
      List<Ent> result = new ArrayList<>();

      for (Ent de : forwardsMap.keySet()) {
         if (forwardsMap.get(de) != null && newForwards.get(de) == null) {
            result.add(de);
         }
      }

      return result;
   }

   private Map<Ent, Boolean> getForwardMap(Snapshot snapshot, List<? extends Ent> entities) {
      Map<Ent, Boolean> results = new HashMap<>();

      for (Ent de : entities) {
         EntState visual = snapshot.getState(de);
         if (visual.isDead()) {
            results.put(de, null);
         } else {
            results.put(de, visual.isForwards() && de.getFightLog().anyHidingVisual() && entities.size() > 0);
         }
      }

      return results;
   }

   public List<Ent> getEntities() {
      return this.entities;
   }

   public void assumeHoldsDie() {
      for (Ent de : this.getEntities()) {
         de.getDie().flatDraw = true;
         de.getEntPanel().holdsDie = true;
      }
   }

   public void setInPlace(boolean inPlace) {
      this.inPlace = inPlace;
      if (inPlace && this.inPlaceRunnable != null) {
         this.inPlaceRunnable.run();
         this.inPlaceRunnable = null;
      }
   }

   public boolean inPlace() {
      return this.inPlace;
   }

   public void afterInPlace(Runnable runnable) {
      if (this.inPlace()) {
         runnable.run();
      } else {
         this.inPlaceRunnable = runnable;
      }
   }

   @Override
   public void draw(Batch batch, float parentAlpha) {
      float px = this.getX();
      float py = this.getY();
      this.setPosition((int)Math.ceil(px), (int)Math.ceil(py));
      boolean hasSpeechBubble = false;

      for (int i = 0; i < this.entities.size(); i++) {
         Ent treebeard = this.entities.get(i);
         if (treebeard.getEntPanel().hasSpeechBubble()) {
            hasSpeechBubble = true;
            break;
         }
      }

      super.draw(batch, parentAlpha, !hasSpeechBubble);
      this.setPosition(px, py);
   }

   public void clearAfterInPlace() {
      this.inPlaceRunnable = null;
   }

   public boolean anyDeathAnimationsOngoing() {
      return System.currentTimeMillis() < this.deathAnimFinish;
   }

   public static int getPanelTuckback(boolean friendly) {
      int extraGap = 22;
      int remainder = (int)(com.tann.dice.Main.width - 168.0F - extraGap);
      int result = Math.max(0, -remainder / 2);
      float friendlyAmt = 0.8F;
      return friendly ? (int)(result * friendlyAmt) : (int)(result * (2.0F - friendlyAmt));
   }
}
