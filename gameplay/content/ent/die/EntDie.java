package com.tann.dice.gameplay.content.ent.die;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.HSL;
import com.tann.dice.gameplay.content.gen.pipe.item.facade.FacadeUtils;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.DieTargetable;
import com.tann.dice.gameplay.effect.targetable.ability.ui.AbilityHolder;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.RollManager;
import com.tann.dice.statics.bullet.BulletStuff;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntDie extends Die<EntSide> {
   public final Ent ent;
   Map<Integer, Integer> glowMap = new HashMap<>();
   private static final float[] BLANK_HSL = new float[18];
   DieTargetable dieTargetable;

   public EntDie(Ent ent) {
      super((int)(Math.random() * 6.0));
      this.ent = ent;
      this.init();
   }

   public static int opposite(int sideIndex, boolean nice) {
      if (!nice) {
         switch (sideIndex) {
            case 0:
               return 1;
            case 1:
               return 0;
            case 2:
               return 3;
            case 3:
               return 2;
            case 4:
               return 5;
            case 5:
               return 4;
            default:
               return -69;
         }
      } else {
         switch (sideIndex) {
            case 0:
               return 4;
            case 1:
               return 5;
            case 2:
               return 3;
            case 3:
               return 2;
            case 4:
               return 0;
            case 5:
               return 1;
            default:
               return -69;
         }
      }
   }

   protected EntSide[] initSides() {
      return Arrays.copyOf(this.ent.getSides(), 6);
   }

   public EntSide getCurrentSide() {
      int side = this.getSideIndex();
      return side >= 0 ? this.getSide(side) : null;
   }

   @Override
   public Color getColour() {
      return this.ent == null ? Colours.dark : this.ent.getColour();
   }

   @Override
   protected TextureRegion getSideTexture(int side) {
      return this.ent.getState(FightLog.Temporality.Visual).getSideState(this.getSide(side)).getCalculatedTexture();
   }

   @Override
   protected float getPipFloat(int side, boolean bonus) {
      EntSideState ess = this.ent.getState(FightLog.Temporality.Visual).getSideState(this.getSide(side));
      if (bonus) {
         return ess.getTotalBonus();
      } else {
         Eff e = ess.getCalculatedEffect();
         if (e.hasValue()) {
            return Math.max(0, e.getValue());
         } else {
            return bonus ? 0.0F : -1.0F;
         }
      }
   }

   @Override
   protected float getBonusPipFloat(int side) {
      if (this.glowMap.get(side) == null) {
         EntSideState ess = this.ent.getState(FightLog.Temporality.Visual).getSideState(this.getSide(side));
         this.glowMap.put(side, ess.getBonusColIndex());
      }

      return this.glowMap.get(side).intValue();
   }

   @Override
   public void clearTextureCache() {
      super.clearTextureCache();
      this.glowMap = new HashMap<>();
   }

   @Override
   public float[] getHSLData() {
      if (this.hsl != null) {
         return this.hsl;
      } else if (!FacadeUtils.isFacaded(this.ent)) {
         this.hsl = BLANK_HSL;
         return this.hsl;
      } else {
         this.hsl = new float[18];
         EntState es = this.ent.getState(FightLog.Temporality.Visual);

         for (int sideIndex = 0; sideIndex < 6; sideIndex++) {
            HSL found = es.getSideState(sideIndex).getHsl();
            if (found != null) {
               int mainIndex = sideIndex * 3;
               this.hsl[mainIndex + 0] = found.a;
               this.hsl[mainIndex + 1] = found.b;
               this.hsl[mainIndex + 2] = found.c;
            }
         }

         return this.hsl;
      }
   }

   @Override
   public float[] getKeywordLocs() {
      if (this.keywordLocs != null) {
         return this.keywordLocs;
      } else {
         this.keywordLocs = new float[96];
         EntState es = this.ent.getState(FightLog.Temporality.Visual);
         float regionWidth = 1024.0F;
         float regionHeight = 1024.0F;

         for (int sideIndex = 0; sideIndex < 6; sideIndex++) {
            List<Keyword> bonuses = es.getSideState(sideIndex).getBonusKeywords();

            for (int keywordIndex = 0; keywordIndex < 4 && keywordIndex < bonuses.size(); keywordIndex++) {
               Keyword k = bonuses.get(keywordIndex);
               int mainIndex = sideIndex * 16 + keywordIndex * 4;
               TextureRegion image = k.getImage(this.ent.getSize());
               this.keywordLocs[mainIndex + 0] = image.getRegionX() / 1024.0F;
               this.keywordLocs[mainIndex + 1] = image.getRegionY() / 1024.0F;
               this.keywordLocs[mainIndex + 2] = this.ent.getSize() != EntSize.small && !k.isFlipCorner() ? 0.0F : 1.0F;
               this.keywordLocs[mainIndex + 3] = image.getRegionHeight() * 20 + image.getRegionWidth();
            }
         }

         return this.keywordLocs;
      }
   }

   @Override
   protected TextureRegion getLapelTexture() {
      return this.ent.getLapel();
   }

   @Override
   protected void locked() {
      this.ent.locked();
   }

   @Override
   protected void stopped() {
      this.ent.stopped();
   }

   @Override
   public float getPixelSize() {
      return this.ent.getPixelSize();
   }

   @Override
   protected DieContainer getDieContainer() {
      return this.ent.getEntPanel();
   }

   @Override
   public void setState(Die.DieState state) {
      this.dieTargetable = null;
      super.setState(state);
   }

   public DieTargetable getTargetable() {
      if (this.dieTargetable == null) {
         this.dieTargetable = new DieTargetable(this.ent, this.getSideIndex());
      }

      return this.dieTargetable;
   }

   public void toggleLock() {
      if (PhaseManager.get().getPhase().canRoll()) {
         if (!com.tann.dice.Main.getSettings().hasAttemptedLevel()) {
            RollManager rm = DungeonScreen.get().rollManager;
            if (!rm.allDiceNotState(Die.DieState.Rolling) || !rm.allDiceNotState(Die.DieState.Unlocking)) {
               return;
            }
         }

         boolean allowToggle = true;
         boolean currentlyLocked = this.getState().isLockedOrLocking();
         EntState es = this.ent.getState(FightLog.Temporality.Present);
         if (es.getSnapshot().getRolls() != 0 || !this.getState().isLockedOrLocking()) {
            if (currentlyLocked) {
               allowToggle &= !es.isAutoLock();
            }

            for (Global gt : es.getSnapshot().getGlobals()) {
               allowToggle &= gt.allowToggleLock(currentlyLocked, es.getSnapshot().getStates(true, false));
            }

            if (!currentlyLocked && DungeonScreen.get().rollManager.getHeroDiceAvailableToRoll().size() == 1) {
               allowToggle = true;
            }

            if (!allowToggle) {
               Sounds.playSound(Sounds.error);
               String action = currentlyLocked ? "reroll" : "lock";
               String msg = this.ent.getName(true) + " can't be " + action + "ed";
               AbilityHolder.showInfo(msg, Colours.red);
            } else {
               switch (this.getState()) {
                  case Stopped:
                  case Unlocking:
                     if (this.getSideIndex() == -1) {
                        return;
                     }

                     Sounds.playSound(Sounds.lock);
                     this.slideToPanel();
                     break;
                  case Locked:
                  case Locking:
                     Sounds.playSound(Sounds.unlock);
                     this.getDieContainer().unlockDie();
                     this.returnToPlay(null, false, getBaseInterpSpeed());
               }

               if (DungeonScreen.get().rollManager.allDiceStoppedEnoughToSave()) {
                  DungeonScreen.get().mildSave();
               }
            }
         }
      }
   }

   public static void organiseDiceIntoLine(List<EntDie> dice) {
      if (dice.size() == 0) {
         Sounds.playSound(Sounds.error);
      } else {
         int numDice = dice.size();

         for (int i = 0; i < numDice; i++) {
            EntDie d = dice.get(i);
            if (d != null && d.getState() == Die.DieState.Stopped) {
               d.setState(Die.DieState.SlidingToMiddle);
               d.moveTo(getLineLoc(d), d.getSideQuaternion(d.getSideIndex(), false), null, 0.3F);
            }
         }
      }
   }

   public static Vector3 getLineLoc(EntDie d) {
      float dieRadius = d.physical.radius;
      float dieGap = dieRadius * 1.13F;
      Vector3 middle = BulletStuff.getMiddle();
      middle.y = middle.y + d.getSafeY();
      List<EntDie> dice = BulletStuff.getDice(true);
      int numDice = dice.size();
      int i = dice.indexOf(d);
      float zDistanceMult = i % 5 - 2;
      float xDistMult = i / 5 - (numDice - 1) / 5 / 2.0F;
      return middle.cpy().add(xDistMult * dieGap, 0.0F, zDistanceMult * dieGap);
   }

   public boolean isDisposed() {
      return this.disposed;
   }
}
