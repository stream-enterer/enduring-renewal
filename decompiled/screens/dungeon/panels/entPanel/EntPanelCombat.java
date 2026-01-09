package com.tann.dice.screens.dungeon.panels.entPanel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.content.ent.StateCache;
import com.tann.dice.gameplay.content.ent.die.Die;
import com.tann.dice.gameplay.content.ent.die.DieContainer;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.entState.StateEvent;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.merge.Merge;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.TargetingManager;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ConcisePanel;
import com.tann.dice.screens.dungeon.panels.entPanel.heartsHolder.HPHolder;
import com.tann.dice.screens.dungeon.panels.tutorial.TutorialManager;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.bullet.BulletStuff;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.CrossActor;
import com.tann.dice.util.Draw;
import com.tann.dice.util.ShakeAction;
import com.tann.dice.util.SpeechBubble;
import com.tann.dice.util.SpeechGarbler;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannFont;
import com.tann.dice.util.TextWisp;
import com.tann.dice.util.listener.MultitapListener;
import com.tann.dice.util.ui.TextMarquee;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EntPanelCombat extends Group implements DieContainer {
   public Ent ent;
   public boolean holdsDie = false;
   public HPHolder HPHolder;
   static final int n = 5;
   static NinePatch panelBorder = new NinePatch(Images.panelBorder, 5, 5, 5, 5);
   static NinePatch panelBorderSmall = new NinePatch(Images.panelBorderSmall, 5, 5, 5, 5);
   static NinePatch panelBorderLeft = new NinePatch(Images.panelBorderLeft, 5, 5, 5, 5);
   static NinePatch panelBorderRight = new NinePatch(Images.panelBorderRight, 5, 5, 5, 5);
   static NinePatch panelBorderRightHighlight = new NinePatch(Images.panelBorderRightHighlight, 5, 5, 5, 5);
   static NinePatch panelBorderColour = new NinePatch(Images.panelBorderColour, 5, 5, 5, 5);
   public static final float WIDTH = 84.0F;
   int borderSize = 4;
   int textBorderSize = 3;
   private final int gap;
   boolean huge;
   private static final float MIN_SHAKE_TIME = 0.2F;
   private static final float SHAKE_EXTRA = 0.2F;
   private ShieldHolder shieldHolder;
   private List<Actor> underneathActors = new ArrayList<>();
   private StateCache stateCache;
   public Actor title;
   public DieHolder dieHolder;
   private boolean highlightDice;
   Vector2 portraitPosition = new Vector2();
   List<Ent> targets = new ArrayList<>();
   List<Color> tmpColours = new ArrayList<>();
   private boolean possibleTarget;
   private List<Keyword> possibleTargetBonus;
   float fadeSpeed = 0.0F;
   private float intensity;
   boolean slidOutToTarget;
   int stateEventIndex = 0;
   boolean animating;
   List<Personal> describables;
   private static final long TIME_BETWEEN_BUBBLES = 2000L;
   private static long lastSpeechBubble;
   List<Actor> overActors = new ArrayList<>();
   int preferredY = 10;
   Actor marginActor;
   float savedMargin = -1.0F;

   public EntPanelCombat(final Ent ent) {
      this.setTransform(false);
      this.ent = ent;
      this.stateCache = new StateCache(ent, FightLog.Temporality.Visual, FightLog.Temporality.Present, FightLog.Temporality.Future);
      if (ent.getSize() == EntSize.small) {
         this.gap = 1;
         this.borderSize = 2;
         this.textBorderSize = 2;
      } else {
         this.gap = 2;
      }

      this.huge = ent.getSize() == EntSize.huge;
      this.addListener(
         new MultitapListener() {
            @Override
            public boolean info(int button, float x, float y) {
               DungeonScreen ds = DungeonScreen.get();
               if (ds != null) {
                  ds.targetingManager.clicked(EntPanelCombat.this.ent, false);
                  ds.getTutorialManager()
                     .onAction(ent.isPlayer() ? TutorialManager.TutorialAction.HeroPanelInfo : TutorialManager.TutorialAction.MonsterPanelInfo, ent);
                  return true;
               } else {
                  return false;
               }
            }

            @Override
            public boolean action(int button, int pointer, float x, float y) {
               DungeonScreen ds = DungeonScreen.get();
               if (ds != null) {
                  if (!ent.isPlayer()) {
                     ds.getTutorialManager().onAction(TutorialManager.TutorialAction.SelectMonster);
                  }

                  ds.targetingManager.clicked(EntPanelCombat.this.ent, true);
               }

               return true;
            }
         }
      );
      this.setPosition(0.0F, com.tann.dice.Main.height);
   }

   public void addUnderneathActor(Actor actor) {
      if (actor instanceof Group) {
         ((Group)actor).setTransform(false);
      }

      this.underneathActors.add(actor);
   }

   public void layout() {
      this.clearUnderneathActors();
      this.describables = null;
      this.clearChildren();
      TriggerPanel buffHolder = new TriggerPanel(this.ent, false);
      this.addActor(buffHolder);
      this.dieHolder = new DieHolder(this.ent);
      this.HPHolder = new HPHolder(this.ent);
      this.addActor(this.HPHolder);
      int titleSize = 43;
      if (this.ent.getSize() == EntSize.huge) {
         titleSize = 53;
      }

      int oldTitleSize = titleSize;
      if (com.tann.dice.Main.self().translator.longEntityNames()) {
         if (this.ent.getSize() != EntSize.small && this.ent.getSize() != EntSize.reg) {
            titleSize = (int)(84.0F - this.borderSize);
         } else {
            titleSize = (int)(84.0F - this.borderSize - this.dieHolder.getWidth());
         }
      }

      this.title = ConcisePanel.makeTitle("[notranslate]" + ConcisePanel.entName(this.ent), this.ent.getColour(), titleSize, false);
      if (this.title instanceof TextMarquee) {
         this.title = ConcisePanel.makeTitle("[notranslate]" + ConcisePanel.entName(this.ent), this.ent.getColour(), oldTitleSize, false);
      }

      this.addActor(this.title);
      this.setWidth(84.0F);
      if (this.huge) {
         this.setHeight(this.ent.getPixelSize() + this.gap * 2 + this.title.getHeight() + 18.0F + this.borderSize * 2);
      } else {
         this.setHeight(this.ent.getPixelSize() + this.borderSize * 2);
      }

      this.addActor(this.dieHolder);
      boolean player = this.ent.isPlayer();
      this.dieHolder.setY(this.borderSize);
      this.title.setY((int)(this.getHeight() - this.textBorderSize - TannFont.font.getHeight()));
      this.HPHolder.setY((int)(this.title.getY() - this.gap - this.HPHolder.getHeight()));
      int heartsMiddleX = 0;
      if (player) {
         this.dieHolder.setX(this.getWidth() - this.borderSize - this.dieHolder.getWidth());
         heartsMiddleX = Tann.between(this.ent.getPortraitWidth(true), this.dieHolder.getX() - this.borderSize - 1.0F) - 1;
      } else {
         this.dieHolder.setX(this.borderSize);
         if (this.huge) {
            heartsMiddleX = (int)Math.max(this.title.getWidth() / 2.0F + 3.0F, 8.0F + this.HPHolder.getWidth() / 2.0F);
         } else {
            heartsMiddleX = Tann.between(this.dieHolder.getX() + this.dieHolder.getWidth() + this.borderSize, this.getWidth() - this.ent.getPortraitWidth(true));
            int minX = (int)(this.dieHolder.getX() + this.dieHolder.getWidth() + buffHolder.getWidth() + this.HPHolder.getWidth() / 2.0F) + 1;
            heartsMiddleX = Math.max(minX, heartsMiddleX);
         }
      }

      if ((this.ent.getSize() == EntSize.small || this.ent.getSize() == EntSize.reg) && !this.HPHolder.smallPips()) {
         heartsMiddleX = 38;
      }

      heartsMiddleX += this.ent.getPortraitShift();
      this.title.setX((int)(heartsMiddleX - (this.title.getWidth() / 2.0F - 0.5F)));
      if (com.tann.dice.Main.self().translator.longEntityNames() && !(this.title instanceof TextMarquee)) {
         if (player) {
            int maxTitleX = (int)(this.getWidth() - this.title.getWidth() - this.borderSize - this.borderSize - this.dieHolder.getWidth() - 1.0F);
            if (maxTitleX < 0) {
               maxTitleX = 5;
            }

            if (this.title.getX() > maxTitleX) {
               this.title.setX(maxTitleX);
            }
         } else if (this.ent.getSize() == EntSize.small || this.ent.getSize() == EntSize.reg) {
            int minTitleX = (int)(this.dieHolder.getWidth() + this.borderSize + 1.0F);
            if (this.title.getX() < minTitleX) {
               this.title.setX(minTitleX);
            }
         } else if (this.title.getX() < this.borderSize) {
            this.title.setX(this.borderSize);
         }
      }

      this.HPHolder.setX((int)(heartsMiddleX - (this.HPHolder.getWidth() / 2.0F - 0.5F)));
      int buffHolderX;
      if (!this.ent.isPlayer()) {
         buffHolderX = (int)(this.HPHolder.getX() - buffHolder.getWidth() - 1.0F);
      } else {
         buffHolderX = (int)(this.HPHolder.getX() + this.HPHolder.getWidth() + 1.0F);
      }

      EntSize size = this.ent.getEntType().size;
      int buffHolderY;
      if (!this.HPHolder.smallPips() || size != EntSize.big && size != EntSize.reg) {
         buffHolderY = (int)(this.HPHolder.getY() + this.HPHolder.getHeight() - buffHolder.getHeight());
      } else {
         buffHolderY = (int)(this.HPHolder.getY() + this.HPHolder.getHeight() - buffHolder.getHeight());
      }

      buffHolder.setPosition(buffHolderX, buffHolderY);
      this.shieldHolder = new ShieldHolder(this.ent);
      this.shieldHolder.setPosition(this.getShieldHolderPosition(1.0F), (int)(this.getHeight() / 2.0F - this.shieldHolder.getHeight() / 2.0F));
      this.addActor(this.shieldHolder);
      if (this.savedMargin != -1.0F) {
         this.setMargin(this.savedMargin);
      }
   }

   private void clearUnderneathActors() {
      this.underneathActors.clear();
   }

   private int getShieldHolderPosition(float dist) {
      boolean small = ShieldHolder.useSmallShieldHolder();
      return this.ent.isPlayer()
         ? (int)(this.getWidth() + this.shieldHolder.getWidth() * (dist - 1.0F) + (small ? -5 : 3))
         : (int)(-this.shieldHolder.getWidth() + this.shieldHolder.getWidth() * (1.0F - dist) + (small ? 3 : -5));
   }

   public float getPreferredX() {
      if (this.animating) {
         return this.getX();
      } else {
         int deadAmount = (int)(this.getWidth() + 20.0F);
         if (this.ent.getState(FightLog.Temporality.Visual).isDead()) {
            return this.ent.isPlayer() ? -deadAmount : deadAmount;
         } else if (this.ent.isPlayer()) {
            return this.slidOutToTarget ? BulletStuff.getPanelSlideAmount() : 0;
         } else {
            return this.ent.getFightLog().anyHidingVisual() && this.ent.getState(FightLog.Temporality.Visual).isForwards()
               ? -BulletStuff.getPanelSlideAmount()
               : 0.0F;
         }
      }
   }

   public void act(float delta) {
      super.act(delta);
      this.updatePanelStateCache();
      this.tickUnderneathActors(this.underneathActors, delta);
      this.tickUnderneathActors(this.overActors, delta);
      if (this.fadeSpeed > 0.0F) {
         this.intensity = Math.max(0.0F, this.intensity - delta * this.fadeSpeed);
      }
   }

   public void updatePanelStateCache() {
      EntState oldVisual = this.stateCache.get(FightLog.Temporality.Visual);
      EntState oldFuture = this.stateCache.get(FightLog.Temporality.Future);
      if (this.stateCache.update()) {
         this.describables = null;
         this.setNewVisualState(oldVisual, this.stateCache.get(FightLog.Temporality.Visual));
         this.setNewFutureState(oldFuture, this.stateCache.get(FightLog.Temporality.Future));
      }
   }

   private void tickUnderneathActors(List<Actor> actors, float delta) {
      for (int i = actors.size() - 1; i >= 0; i--) {
         Actor a = actors.get(i);
         a.act(delta);
         if (a.getColor().a <= 0.0F) {
            actors.remove(a);
         }
      }
   }

   public void draw(Batch batch, float parentAlpha) {
      if (this.possibleTarget && !this.possibleTargetBonus.isEmpty()) {
         com.tann.dice.Main.requestRendering();
      }

      this.drawUnderneathActors(batch);
      int x = (int)Math.floor(this.getX());
      int y = (int)Math.floor(this.getY());
      batch.setColor(Colours.dark);
      this.drawCutout(batch);
      if (this.stateCache.get(FightLog.Temporality.Visual).isDead()) {
         Draw.fillActor(batch, this);
      }

      batch.setColor(Colours.z_white);
      int npWiggle = 1;
      int borderX = (int)(this.dieHolder.getX() + (this.ent.isPlayer() ? -this.borderSize : this.dieHolder.getWidth() + this.borderSize - 1.0F));
      if (!this.ent.isPlayer()) {
         NinePatch pb = panelBorder;
         if (this.ent.getSize() == EntSize.small) {
            pb = panelBorderSmall;
         }

         pb.draw(batch, x - npWiggle, y - npWiggle, this.getWidth() + npWiggle * 2, this.getHeight() + npWiggle * 2);
      } else {
         panelBorderRight.draw(batch, x + borderX, y - npWiggle, this.getWidth() - borderX + npWiggle, this.getHeight() + npWiggle * 2);
         panelBorderLeft.draw(batch, x - npWiggle, y - npWiggle, borderX + npWiggle * 2, this.getHeight() + npWiggle * 2);
      }

      if (this.shouldDeathFlash() && (OptionLib.DYING_FLASH.c() == 2 || OptionLib.DYING_FLASH.c() == 0)) {
         if (OptionUtils.pulsatingDyingFlash()) {
            com.tann.dice.Main.requestRendering();
         }

         batch.setColor(getDeathFlashColour(this.isFleeing()));
         this.drawCutout(batch);
      }

      if (this.ent.isPlayer()) {
         batch.setColor(this.ent.getColour());
         panelBorderRightHighlight.draw(batch, x + borderX, y - npWiggle, this.getWidth() - borderX + npWiggle, this.getHeight() + npWiggle * 2);
      }

      TargetingManager targetingManager = null;
      if (DungeonScreen.get() != null) {
         targetingManager = DungeonScreen.get().targetingManager;
      }

      if (targetingManager != null) {
         if (this.possibleTarget) {
            if (!com.tann.dice.Main.getSettings().isHasSworded()
               && targetingManager.getSelectedTargetable() != null
               && targetingManager.getSelectedTargetable().getBaseEffect().getType() == EffType.Damage) {
               batch.setColor(Colours.withAlpha(Colours.green, com.tann.dice.Main.pulsateFactor() * 0.4F));
               Draw.fillRectangle(batch, x - npWiggle, y - npWiggle, this.getWidth() + npWiggle * 2, this.getHeight() + npWiggle * 2);
            }

            batch.setColor(Colours.light);
            panelBorderColour.draw(batch, x - npWiggle, y - npWiggle, this.getWidth() + npWiggle * 2, this.getHeight() + npWiggle * 2);
            if (this.possibleTargetBonus != null && this.possibleTargetBonus.size() > 0) {
               int thickness = 2;

               for (int i = 0; i < this.possibleTargetBonus.size(); i++) {
                  this.drawPossibleTargetRectangle(batch, this.possibleTargetBonus.get(i).getColour(), i * 2, i * 2 + 2 - 1);
               }
            }
         } else if (this.ent.isPlayer()
            && PhaseManager.get().getPhase() != null
            && PhaseManager.get().getPhase().canTarget()
            && targetingManager.getSelectedTargetable() == null
            && this.stateCache.get(FightLog.Temporality.Present).canUse()
            && this.notBurningUp()
            && targetingManager.isUsable(this.ent.getDie().getTargetable(), true)) {
            if (!com.tann.dice.Main.getSettings().isHasSworded() && this.ent.getDie().getCurrentSide().getBaseEffect().getType() == EffType.Damage) {
               batch.setColor(Colours.withAlpha(Colours.green, com.tann.dice.Main.pulsateFactor() * 0.7F));
               Draw.fillRectangle(batch, x + borderX, y - npWiggle, this.getWidth() - borderX + npWiggle, this.getHeight() + npWiggle * 2);
            }

            batch.setColor(Colours.light);
            panelBorderRightHighlight.draw(batch, x + borderX, y - npWiggle, this.getWidth() - borderX + npWiggle, this.getHeight() + npWiggle * 2);
         }
      }

      if (this.isHighlightDice()) {
         batch.setColor(Colours.light);
         int border = 1;
         panelBorderColour.draw(batch, this.getX() - border, this.getY() - border, this.getWidth() + border * 2, this.getHeight() + border * 2);
      }

      this.drawArrows(batch);
      if (!this.ent.isPlayer()) {
         this.drawTargetingBox(batch, 0, 4);
         this.drawTargetingBox(batch, 1, 6);
         this.drawTargetingBox(batch, 2, 8);
      }

      batch.setColor(Colours.z_white);
      AtlasRegion region = this.ent.entType.portrait;
      if (region != null) {
         Vector2 portraitDraw = this.getPortraitPosition();
         Draw.drawScaled(batch, region, x + portraitDraw.x, y + portraitDraw.y, this.ent.isPlayer() ? 1.0F : -1.0F, 1.0F);
      }

      boolean deathFlash = this.shouldDeathFlash();
      if (deathFlash) {
         switch (OptionLib.DYING_FLASH.c()) {
            case 1:
               batch.setColor(Colours.withAlpha(getDeathFlashColour(this.isFleeing()), 1.0F));
               if (this.isFleeing()) {
                  batch.setColor(Colours.withAlpha(batch.getColor(), 0.3F));
                  int dst = 1;

                  for (int dx = -1; dx <= 1; dx++) {
                     for (int dy = -1; dy <= 1; dy++) {
                        int dxm = 1;
                        batch.draw(this.getGlowImage(this.ent), this.getX() + dx * dxm, this.getY() + dy * dxm);
                     }
                  }
               } else {
                  float dx = this.getX();
                  float dy = this.getY();
                  if (this.ent.isPlayer()) {
                     int gap = 2;
                     dx -= gap;
                     dy -= gap;
                  }

                  batch.draw(this.getGlowImage(this.ent), dx, dy);
               }
         }

         if (OptionUtils.dyingCrossTitle()) {
            CrossActor.drawCross(batch, Colours.red, this.title, Tann.getAbsoluteCoordinates(this));
         }
      }

      super.draw(batch, parentAlpha);
      this.drawBuffs(batch);
      Die.DieState dieState = this.ent.getDie().getState();
      if (dieState == Die.DieState.Rolling
         || this.stateCache.get(FightLog.Temporality.Present).isSummonedSoNotAttacking()
            && !this.ent.isPlayer()
            && com.tann.dice.Main.getCurrentScreen() instanceof DungeonScreen) {
         batch.setColor(this.ent.getColour());
         int gap = 0;
         float holderX = this.getX() + this.dieHolder.getX() + gap;
         float holderY = this.getY() + this.dieHolder.getY() + gap;
         Draw.fillRectangle(batch, holderX, holderY, this.dieHolder.getWidth() - gap * 2, this.dieHolder.getWidth() - gap * 2);
         batch.setColor(Colours.dark);
         int var23 = 1;
         Draw.fillRectangle(
            batch,
            this.getX() + this.dieHolder.getX() + var23,
            this.getY() + this.dieHolder.getY() + var23,
            this.dieHolder.getWidth() - var23 * 2,
            this.dieHolder.getWidth() - var23 * 2
         );
         batch.setColor(Colours.z_white);
         batch.draw(this.ent.get2DLapel(), holderX, holderY);
      }
   }

   private boolean notBurningUp() {
      return this.getParent() != null;
   }

   private TextureRegion getGlowImage(Ent ent) {
      switch (ent.getSize()) {
         case small:
            return Images.borderGlowSmall;
         case big:
            return Images.borderGlowBig;
         case huge:
            return Images.borderGlowHuge;
         case reg:
         default:
            return ent instanceof Monster ? Images.borderGlowRegMonster : Images.borderGlowRegHero;
      }
   }

   private void drawPossibleTargetRectangle(Batch batch, Color col, int from, int to) {
      batch.setColor(Colours.withAlpha(col, 0.5F + com.tann.dice.Main.pulsateFactor() * 0.5F));

      for (int i = from; i <= to; i++) {
         Draw.drawRectangle(batch, (int)this.getX() - i, (int)this.getY() - i, this.getWidth() + i * 2, this.getHeight() + i * 2, 1);
      }
   }

   private boolean isHighlightDice() {
      return this.highlightDice;
   }

   public void setHighlightDice(boolean highlightDice) {
      this.highlightDice = highlightDice;
   }

   private Vector2 getPortraitPosition() {
      int offsetX = this.ent.getPortraitOffsetX();
      int offsetY = this.ent.getPortraitOffsetY();
      return this.ent.isPlayer() ? this.portraitPosition.set(offsetX, offsetY) : this.portraitPosition.set(this.getWidth() + offsetX, offsetY);
   }

   public Vector2 getPortraitCenter() {
      int portraitWidth = this.ent.getPortraitWidth(false);
      int portraitHeight = this.ent.getPortrait().getRegionHeight();
      return this.getPortraitPosition().add(portraitWidth * (this.ent.isPlayer() ? 0.5F : -0.5F), portraitHeight / 2);
   }

   private void drawBuffs(Batch batch) {
      List<Personal> personals = this.stateCache.get(FightLog.Temporality.Visual).getActivePersonals();

      for (int i = 0; i < personals.size(); i++) {
         personals.get(i).drawOnPanel(batch, this);
      }
   }

   private void drawUnderneathActors(Batch batch) {
      if (this.underneathActors.size() > 0) {
         for (Actor a : this.underneathActors) {
            a.draw(batch, 1.0F);
         }
      }
   }

   private void setupTargeters() {
      FightLog.Temporality t = FightLog.Temporality.Present;
      if (this.stateCache.get(t).getSnapshot() != null) {
         this.targets = new ArrayList<>(this.stateCache.get(t).getSnapshot().getAllTargeters(this.ent, false));
      }
   }

   private void drawTargetingBox(Batch batch, int dist, int gap) {
      this.tmpColours.clear();

      for (int i = 0; i < this.targets.size(); i++) {
         Ent de = this.targets.get(i);
         if (de.isPlayer()) {
            this.tmpColours.add(de.getColour());
         }
      }

      int recWidth = 1;
      int h = (int)(this.getHeight() - gap * 2);

      for (int ix = 0; ix < this.tmpColours.size(); ix++) {
         batch.setColor(this.tmpColours.get(ix));
         Draw.fillRectangle(batch, (int)(this.getX() - recWidth - dist), (int)(this.getY() + gap), recWidth, h - ix * (1.0F / this.tmpColours.size()) * h);
      }
   }

   private void drawCutout(Batch batch) {
      int x = (int)this.getX();
      int y = (int)this.getY();
      int gap = 1;
      Draw.fillRectangle(batch, x + gap, y + gap, this.dieHolder.getX() - gap, this.getHeight() - gap * 2);
      Draw.fillRectangle(batch, x + this.dieHolder.getX(), y, this.dieHolder.getWidth(), this.dieHolder.getY());
      Draw.fillRectangle(
         batch,
         x + this.dieHolder.getX(),
         y + this.dieHolder.getY() + this.dieHolder.getHeight(),
         this.dieHolder.getWidth(),
         this.getHeight() - this.dieHolder.getY() - this.dieHolder.getHeight()
      );
      Draw.fillRectangle(
         batch,
         x + this.dieHolder.getX() + this.dieHolder.getWidth(),
         y + gap,
         this.getWidth() - this.dieHolder.getX() - this.dieHolder.getWidth() - gap,
         this.getHeight() - gap * 2
      );
   }

   public void setPossibleTarget(boolean b) {
      this.setPossibleTarget(b, null);
   }

   public void setPossibleTarget(boolean b, List<Keyword> bonus) {
      this.possibleTarget = b;
      this.possibleTargetBonus = bonus;
   }

   @Override
   public Vector2 getDieHolderLocation(boolean instantUpdate) {
      Vector2 retn = Tann.getAbsoluteCoordinates(this.dieHolder);
      retn.x = (int)retn.x;
      retn.y = (int)retn.y;
      if (instantUpdate) {
         retn.y = retn.y + (this.getPreferredY() - this.getY());
      }

      return retn;
   }

   @Override
   public void startLockingDie() {
      this.holdsDie = true;
   }

   @Override
   public void lockDie() {
      this.ent.getDie().flatDraw = true;
   }

   @Override
   public void unlockDie() {
      this.holdsDie = false;
   }

   public void drawBackground(Batch batch) {
      Vector2 loc = this.getDieHolderLocation(false);
      loc.x = (int)loc.x;
      loc.y = (int)loc.y;
      if (this.hasParent()) {
         batch.setColor(this.dieHolder.getColor());
         Draw.drawRectangle(batch, loc.x, loc.y, this.dieHolder.getWidth(), this.dieHolder.getHeight(), 1);
         int middle = 1;
         batch.setColor(Colours.dark);
         Draw.fillRectangle(
            batch, (int)(loc.x + middle), (int)(loc.y + middle), (int)(this.dieHolder.getWidth() - middle * 2), (int)(this.dieHolder.getHeight() - middle * 2)
         );
      }
   }

   private void drawArrows(Batch batch) {
      if (this.intensity != 0.0F && this.getParent() != null) {
         Snapshot s = this.stateCache.get(FightLog.Temporality.Present).getSnapshot();
         if (s != null) {
            List<Ent> targs = s.getAllTargeters(this.ent, this.ent.isPlayer());
            if (targs.size() != 0) {
               batch.setColor(Colours.withAlpha(this.ent.getColour(), this.intensity));
               com.tann.dice.Main.requestRendering();

               for (Ent de : targs) {
                  if (de.isPlayer() != this.ent.isPlayer()) {
                     EntPanelCombat ep = de.getEntPanel();
                     if (de.isPlayer()) {
                        batch.setColor(Colours.withAlpha(de.getColour(), this.intensity));
                     }

                     Vector2 me = new Vector2(this.getX(), this.getY());
                     Vector2 them = Tann.getAbsoluteCoordinates(ep);
                     them.x = them.x - this.getParent().getX();
                     them.y = them.y - this.getParent().getY();
                     Monster m;
                     if (de instanceof Monster) {
                        m = (Monster)de;
                     } else {
                        if (!(this.ent instanceof Monster)) {
                           return;
                        }

                        m = (Monster)this.ent;
                     }

                     if (m.getDie().getCurrentSide() != null) {
                        float width = Math.max(1, m.getDie().getCurrentSide().getBaseEffect().getValue()) + 1;
                        width = Math.min(40.0F, width);
                        float segmentSize = 5.0F;
                        float gapSize = 2.0F;
                        float speed = 2.0F;
                        if (this.ent.isPlayer()) {
                           Draw.drawDottedLine(
                              batch,
                              them.x,
                              them.y + ep.getHeight() / 2.0F,
                              me.x + this.getWidth(),
                              me.y + this.getHeight() / 2.0F,
                              width,
                              segmentSize,
                              gapSize,
                              speed
                           );
                        } else {
                           Draw.drawDottedLine(
                              batch,
                              me.x,
                              me.y + this.getHeight() / 2.0F,
                              them.x + ep.getWidth(),
                              them.y + ep.getHeight() / 2.0F,
                              width,
                              segmentSize,
                              gapSize,
                              speed
                           );
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void setArrowIntensity(float intensity, float fadeSpeed) {
      this.intensity = intensity;
      this.fadeSpeed = fadeSpeed;
   }

   public void slideOutToTarget() {
      this.slidOutToTarget = true;
      Tann.finishAllActions(this);
      this.addAction(Actions.moveTo(this.getPreferredX(), this.getPreferredY(), 0.3F, Interpolation.pow2Out));
   }

   public void slideBack() {
      this.slidOutToTarget = false;
      this.addAction(Actions.moveTo(this.getPreferredX(), this.getPreferredY(), 0.3F, Interpolation.pow2Out));
   }

   public void shakeShield() {
      this.shieldHolder.shake();
   }

   public void breakShield() {
      this.shieldHolder.crack();
   }

   public void showVisualDamage(EntState oldState, EntState newState, float shakeFactor, boolean allowShake) {
      if (!DungeonScreen.get().isLoading()) {
         this.HPHolder.addDamageFlibs(oldState, newState);
         if (allowShake) {
            this.addAction(new ShakeAction(8.0F * shakeFactor, 20.0F, 0.2F + 0.2F * shakeFactor, Interpolation.linear));
         }
      }
   }

   public void showNewBlockedDamage(EntState newState, int blocked) {
      if (!newState.isDead()) {
         if (newState.getShields() > 0) {
            this.shakeShield();
         } else {
            this.breakShield();
         }
      }
   }

   public void showSpikes() {
      if (!this.ent.getState(FightLog.Temporality.Present).isDead()) {
         Sounds.playSound(Sounds.spike);
         Group spikeGroup = Tann.makeGroup();
         spikeGroup.setWidth(Images.spike.getRegionWidth());
         int numSpikes = 1;

         for (int i = 0; i < numSpikes; i++) {
            Image image = new Image(Images.spike);
            image.setSize(Images.spike.getRegionWidth(), Images.spike.getRegionHeight());
            spikeGroup.addActor(image);
            if (this.ent.isPlayer()) {
               image.setX(this.getWidth() - image.getWidth());
            } else {
               image.setScale(-1.0F, 1.0F);
               image.setX(image.getWidth());
            }

            image.setY((int)(this.getHeight() * 1.0F / 3.0F - image.getHeight() / 2.0F));
         }

         Vector2 absPos = Tann.getAbsoluteCoordinates(this);
         spikeGroup.setPosition(spikeGroup.getX() + absPos.x, spikeGroup.getY() + absPos.y);
         float m = OptionUtils.enemyAnim();
         spikeGroup.addAction(
            Actions.sequence(
               Actions.moveBy(spikeGroup.getWidth() * (this.ent.isPlayer() ? 1 : -1), 0.0F, 0.1F * m, Interpolation.pow2Out),
               Actions.delay(0.3F * m),
               Actions.moveBy(spikeGroup.getWidth() * (this.ent.isPlayer() ? -1 : 1), 0.0F, 0.5F * m, Interpolation.pow2Out),
               Actions.alpha(0.0F)
            )
         );
         this.addUnderneathActor(spikeGroup);
      }
   }

   public void setNewVisualState(EntState oldState, EntState newState) {
      this.shieldHolder.reset();
      if (oldState != null) {
         if (oldState.getMaxHp() != newState.getMaxHp()) {
            this.layout();
         }

         if (oldState.getShields() < newState.getShields()) {
            this.shieldHolder.flash();
         }

         int hpChange = newState.getHp() - oldState.getHp();
         if (hpChange > 0) {
            this.HPHolder.addHeartFlibs(oldState, newState);
         }

         boolean newStun = !oldState.isStunned() && newState.isStunned();
         if (hpChange < 0 || newStun) {
            float shakeFactor = Math.min(1.0F, (float)(-hpChange) / newState.getMaxHp());
            if (newStun) {
               shakeFactor += 0.4F;
            }

            this.showVisualDamage(oldState, newState, shakeFactor, true);
         }

         int newDamageBlocked = newState.getDamageBlocked() - oldState.getDamageBlocked();
         if (newDamageBlocked > 0) {
            this.showNewBlockedDamage(newState, newDamageBlocked);
         }

         if (oldState.isDead() != newState.isDead()) {
            if (newState.isDead()) {
               if (newState.isFled()) {
                  Sounds.playSound(Sounds.flee);
               } else {
                  this.ent.deathSound();
               }

               this.ent.getDie().removeFromScreen();
            } else {
               this.lockDie();
               this.ent.getDie().setupInitialLocation();
               this.ent.getDie().removeFromPhysics();
            }
         }

         this.HPHolder.newState(newState, FightLog.Temporality.Visual);
         List<StateEvent> stateEvents = newState.getStateEvents();

         for (int i = this.stateEventIndex; i < stateEvents.size(); i++) {
            stateEvents.get(i).act(this);
         }

         this.stateEventIndex = stateEvents.size();
      }
   }

   public void addMessage(String text) {
      if (!DungeonScreen.get().isLoading()) {
         TextWisp tw = new TextWisp(text, 0, 6, 0.5F);
         this.addActor(tw);
         tw.setPosition((int)(this.getWidth() / 2.0F - tw.getWidth() / 2.0F), (int)(this.getHeight() / 2.0F - tw.getHeight() / 2.0F));
      }
   }

   public void setNewFutureState(EntState oldFutureState, EntState newFutureState) {
      this.setupTargeters();
      if (oldFutureState != null) {
         this.HPHolder.newState(newFutureState, FightLog.Temporality.Future);
      }
   }

   public void setAnimating(boolean animating) {
      this.animating = animating;
   }

   public List<Personal> getAllDescribableTriggers() {
      if (this.describables == null) {
         EntState visual = this.stateCache.get(FightLog.Temporality.Visual);
         EntState future = this.stateCache.get(FightLog.Temporality.Future);
         this.describables = new ArrayList<>();
         this.describables.addAll(future.getActivePersonals());
         int visualAddIndex = 0;

         for (Personal t : visual.getActivePersonals()) {
            if (this.describables.contains(t)) {
               visualAddIndex = this.describables.indexOf(t) + 1;
            } else {
               boolean canMerge = false;
               Iterator var7 = this.describables.iterator();

               while (true) {
                  if (var7.hasNext()) {
                     Personal f = (Personal)var7.next();
                     if (!(f instanceof Merge) || !((Merge)f).canMerge(t) || !t.buff.canMerge(f.buff)) {
                        continue;
                     }

                     canMerge = true;
                  }

                  if (!canMerge) {
                     this.describables.add(visualAddIndex++, t);
                  }
                  break;
               }
            }
         }

         for (int i = this.describables.size() - 1; i >= 0; i--) {
            Trait tx = this.describables.get(i).getTrait();
            if ((tx == null || !tx.visible) && !this.describables.get(i).showInEntPanel() && !this.describables.get(i).showInDiePanel()) {
               this.describables.remove(i);
            }
         }
      }

      return this.describables;
   }

   public void addSpeechBubble(String chatText) {
      EntState vis = this.stateCache.get(FightLog.Temporality.Visual);

      for (Personal activeTrigger : vis.getActivePersonals()) {
         chatText = activeTrigger.transformChat(chatText, vis);
      }

      if (this.ent.getColour() == Colours.green) {
         chatText = SpeechGarbler.garble(this.ent, chatText);
      }

      long now = System.currentTimeMillis();
      if (!DungeonScreen.get().isLoading() && (OptionLib.TRIPLE_CHAT.c() || !this.ent.isPlayer() || now - lastSpeechBubble >= 2000L)) {
         lastSpeechBubble = now;
         SpeechBubble sb = new SpeechBubble(TextWriter.getTag(this.ent.getColour()) + chatText, !this.ent.isPlayer());
         sb.setColor(this.ent.getColour());
         Vector2 portraitCenter = this.getPortraitCenter();
         portraitCenter.add(9 * (this.ent.isPlayer() ? 1 : -1), -4.0F);
         this.addActor(sb);
         sb.setPosition((int)portraitCenter.x, (int)portraitCenter.y);
         Vector2 actualPos = Tann.getAbsoluteCoordinates(sb, Tann.TannPosition.Top);
         if (actualPos.y > com.tann.dice.Main.height) {
            sb.setY((int)(sb.getY() - (actualPos.y - com.tann.dice.Main.height)));
         }

         sb.addAction(Actions.sequence(Actions.delay(1.4F, Actions.fadeOut(0.2F)), Actions.removeActor()));
      }
   }

   public boolean hasSpeechBubble() {
      return System.currentTimeMillis() - lastSpeechBubble <= 2000L;
   }

   public void postDraw(Batch batch) {
      if (this.hasParent()) {
         for (int i = 0; i < this.overActors.size(); i++) {
            Actor a = this.overActors.get(i);
            Vector2 myPos = Tann.getAbsoluteCoordinates(this);
            Tann.intify(myPos);
            a.setPosition(a.getX() + myPos.x, a.getY() + myPos.y);
            a.draw(batch, 1.0F);
            a.setPosition(a.getX() - myPos.x, a.getY() - myPos.y);
         }
      }
   }

   public void setPreferredY(int prefY) {
      this.preferredY = prefY;
   }

   public float getPreferredY() {
      return this.preferredY;
   }

   public boolean shouldDeathFlash() {
      return this.stateCache.get(FightLog.Temporality.Future).isDead() && !this.stateCache.get(FightLog.Temporality.Visual).isDead();
   }

   private boolean isFleeing() {
      return this.stateCache.get(FightLog.Temporality.Future).isFled();
   }

   public static Color getDeathFlashColour(boolean flee) {
      return OptionUtils.pulsatingDyingFlash()
         ? Colours.withAlpha(flee ? Colours.grey : Colours.red, 0.2F + com.tann.dice.Main.pulsateFactor() * 0.5F)
         : Colours.withAlpha(flee ? Colours.grey : Colours.red, 0.55F);
   }

   public void setMargin(float margin) {
      this.savedMargin = margin;
      if (this.marginActor != null) {
         this.marginActor.remove();
      }

      if (!(margin < 0.0F)) {
         float maxGap = 10.0F;
         margin = Math.min(margin, maxGap);
         this.marginActor = new Actor() {};
         this.marginActor.setSize(this.getWidth() + margin, this.getHeight() + margin * 2.0F);
         this.addActor(this.marginActor);
         this.marginActor.setPosition(-margin, -margin);
      }
   }
}
