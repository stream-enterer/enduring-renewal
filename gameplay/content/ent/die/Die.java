package com.tann.dice.gameplay.content.ent.die;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.tann.dice.gameplay.content.ent.die.side.Side;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.statics.bullet.BulletStuff;
import com.tann.dice.statics.bullet.CollisionObject;
import com.tann.dice.statics.bullet.RollFx;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Chrono;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Maths;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import java.util.List;
import java.util.Random;

public abstract class Die<SideType extends Side> {
   private static final TextureRegion blank = ImageUtils.loadExt3d("blank");
   private static float BASE_SIZE = 0.02F;
   private float cantripFlashDecay;
   private float cantripFlash;
   public static final float MAX_AIRTIME = 3.7F;
   protected Die.DieState state = Die.DieState.Stopped;
   protected int lockedSide = -1;
   protected int prevLockedSide;
   protected float dist = 0.0F;
   protected float currentInterpSpeed;
   protected static Material MATERIAL;
   public CollisionObject physical;
   protected static int dieIndex = 0;
   protected float dieSize;
   protected static final int ATTRIBUTES = 29;
   protected float timeInAir;
   protected Runnable moveRunnable;
   public boolean flatDraw = true;
   protected btBoxShape disposeMe;
   protected SideType[] sides;
   private static final float e1Rot = 12.3F;
   private static final float ge2Rot = 12.1F;
   protected Vector3 temp = new Vector3();
   protected Vector3 positionTest = new Vector3();
   protected static float[] rotFactors = new float[3];
   protected static float[] rotActuals = new float[3];
   protected Vector3 startPos = new Vector3();
   protected Vector3 targetPos = new Vector3();
   protected Quaternion startQuat = new Quaternion();
   protected Quaternion targetQuat = new Quaternion();
   protected Quaternion originalRotation = new Quaternion();
   static final Quaternion[] d6Quats = new Quaternion[]{
      new Quaternion().setEulerAngles(0.0F, 180.0F, 270.0F),
      new Quaternion().setEulerAngles(0.0F, 0.0F, 90.0F),
      new Quaternion().setEulerAngles(90.0F, 180.0F, 270.0F),
      new Quaternion().setEulerAngles(90.0F, 0.0F, 90.0F),
      new Quaternion().setEulerAngles(0.0F, 90.0F, 270.0F),
      new Quaternion().setEulerAngles(0.0F, 270.0F, 270.0F)
   };
   protected float[] faceLocs = null;
   protected Vector2 lapelLocs = null;
   protected float[] keywordLocs = null;
   protected float[] hsl = null;
   boolean disposed;
   private static final float BASE_INTERP_SPEED = 0.33F;

   public float getSafeY() {
      return this.physical.radius / 2.0F + 0.01F;
   }

   public void setSide(int side) {
      this.lockedSide = side;
      this.prevLockedSide = side;
      this.setupInitialLocation();
   }

   public void setSideOverride(int side) {
      this.lockedSide = side;
      this.prevLockedSide = side;
   }

   public void setCantripFlash(float glow, float decayTime) {
      this.cantripFlash = glow;
      this.cantripFlashDecay = glow / decayTime;
   }

   public float getCantripFlash() {
      return this.cantripFlash;
   }

   public static void clearAllStatics() {
      MATERIAL = null;
   }

   public Die(int startingSide) {
      this.lockedSide = startingSide;
      this.prevLockedSide = startingSide;
   }

   protected void init() {
      this.dieSize = this.getPixelSize() * BASE_SIZE;
      this.resetSides();
      this.construct();
      this.setupInitialLocation();
   }

   public void resetSides() {
      this.sides = this.initSides();
      this.clearTextureCache();
   }

   protected abstract SideType[] initSides();

   public void setState(Die.DieState state) {
      Die.DieState previousState = this.state;
      this.state = state;
      switch (state) {
         case Rolling:
            this.lockedSide = -1;
            break;
         case Stopped:
            this.damp();
            if (previousState == Die.DieState.Rolling) {
               this.lockedSide = this.getSideIndex();
               this.prevLockedSide = this.lockedSide;
               if (this.lockedSide == -1) {
                  throw new RuntimeException("Failed to get side for die: " + this.getState());
               }

               this.stopped();
            }
            break;
         case Locked:
            this.locked();
            break;
         case Locking:
         case SlidingToMiddle:
         case Unlocking:
            this.removeFromPhysics();
      }
   }

   protected abstract void locked();

   protected abstract void stopped();

   public Die.DieState getState() {
      return this.state;
   }

   public void update(float delta) {
      this.cantripFlash = Math.max(0.0F, this.cantripFlash - this.cantripFlashDecay * delta);
      switch (this.state) {
         case Rolling:
            Phase p = PhaseManager.get().getPhase();
            if (p != null && p.updateDice()) {
               this.outOfBoundsCheck();
               if (this.isStopped()) {
                  this.setState(Die.DieState.Stopped);
               } else {
                  this.timeInAir += delta;
                  if (this.timeInAir > 3.7F) {
                     try {
                        if (((EntDie)this).ent.getState(FightLog.Temporality.Present).isDead()) {
                           return;
                        }
                     } catch (Exception var6) {
                        TannLog.error(var6, "jiggle");
                     }

                     this.jiggle();
                  }
               }
            }
         case Stopped:
         case Locked:
         default:
            break;
         case Locking:
         case SlidingToMiddle:
         case Unlocking:
            this.dist = this.dist + delta / this.currentInterpSpeed;
            if (this.dist >= 1.0F) {
               this.dist = 1.0F;
               if (this.state == Die.DieState.Unlocking) {
                  this.addToPhysics();
                  this.undamp();
                  this.setState(Die.DieState.Stopped);
               } else if (this.state == Die.DieState.Locking) {
                  this.setState(Die.DieState.Locked);
               } else if (this.state == Die.DieState.SlidingToMiddle) {
                  this.setState(Die.DieState.Stopped);
               }
            }

            this.dist = Math.min(1.0F, this.dist);
            float interp = Chrono.i.apply(this.dist);
            this.physical.transform.setToRotation(0.0F, 0.0F, 0.0F, 0.0F);
            Vector3 thisFrame = this.startPos.cpy().lerp(this.targetPos, interp);
            this.physical.transform.setToTranslation(thisFrame);
            this.physical.transform.rotate(this.startQuat.cpy().slerp(this.targetQuat, interp));
            this.physical.body.setWorldTransform(this.physical.transform);
            if (this.dist == 1.0F && this.moveRunnable != null) {
               this.moveRunnable.run();
            }
      }
   }

   public void roll(boolean firstRoll) {
      this.roll(firstRoll, Tann.makeStdRandom());
   }

   public void roll(boolean firstRoll, Random r) {
      if (this.getState() == Die.DieState.Stopped) {
         this.flatDraw = false;
         this.setState(Die.DieState.Rolling);
         this.undamp();
         this.timeInAir = 0.0F;
         this.resetSpeed();
         float up = 21.0F;
         float upRand = 0.0F;
         float side = 0.0F;
         float sideRand = 0.8F;
         float rotRand = 0.0F;
         float center = 0.2F;
         float centerRand = 0.4F;
         if (com.tann.dice.Main.isPortrait()) {
            float mult = 3.0F;
            center *= mult;
            centerRand *= mult;
            side *= mult;
            sideRand *= mult;
            up *= 1.05F;
         }

         if (firstRoll) {
            center = (float)(center - 0.04);
         }

         float mult = 1.0F;
         float rot = 12.1F;
         if (BulletStuff.dice.size() == 1) {
            rot = 12.3F;
         }

         if (OptionUtils.shouldPreRandomise()) {
            this.physical.transform.rotate(this.getSideQuaternion(r.nextInt(6), false));
            this.physical.body.setWorldTransform(this.physical.transform);
         }

         this.randomise(up, upRand, side, sideRand, rot, rotRand, center, centerRand, mult, r);
      }
   }

   public int getSideIndex() {
      switch (this.state) {
         case Rolling:
            return -1;
         case Stopped:
            if (this.lockedSide >= 0) {
               return this.lockedSide;
            } else if (!this.isStopped()) {
               return -1;
            } else {
               this.physical.update();
               this.physical.updateBounds();
               Quaternion rot = new Quaternion();
               this.physical.transform.getRotation(rot);
               Vector3 direction = new Vector3();
               direction.set(Vector3.Z);
               direction.mul(rot);
               float dot = Vector3.Y.dot(direction);
               if (dot > 0.9F) {
                  return 1;
               } else if (dot < -0.9F) {
                  return 0;
               } else {
                  direction.set(Vector3.X);
                  direction.mul(rot);
                  dot = Vector3.Y.dot(direction);
                  if (dot > 0.9F) {
                     return 5;
                  } else if (dot < -0.9F) {
                     return 4;
                  } else {
                     direction.set(Vector3.Y);
                     direction.mul(rot);
                     dot = Vector3.Y.dot(direction);
                     if (dot > 0.9F) {
                        return 3;
                     } else {
                        if (dot < -0.9F) {
                           return 2;
                        }

                        TannLog.log("Failed to find a valid side for the die, returning 0", TannLog.Severity.error);
                        return 0;
                     }
                  }
               }
            }
         case Locked:
         case Locking:
         case SlidingToMiddle:
         case Unlocking:
            return this.lockedSide;
         default:
            return -1;
      }
   }

   protected void jiggle() {
      boolean player = true;
      RollFx.addRollSFX(1, false, true, player);
      this.timeInAir = 0.0F;
      this.randomise(5.0F, 0.0F, 2.7F, 0.0F, 0.06F, 0.0F, 0.0F, 0.0F);
   }

   protected void damp() {
      this.physical.body.setDamping(2.0F, 50.0F);
   }

   public void undamp() {
      this.physical.body.setDamping(0.0F, 0.0F);
   }

   public void addToScreen() {
      if (!BulletStuff.instances.contains(this.physical)) {
         BulletStuff.instances.add(this.physical);
         this.resetSpeed();
         this.addToPhysics();
         this.physical.body.setContactCallbackFlag(512);
         this.physical.body.setContactCallbackFilter(0);
      }
   }

   public void removeFromScreen() {
      if (BulletStuff.instances.remove(this.physical)) {
         this.removeFromPhysics();
      }
   }

   public void removeFromPhysics() {
      BulletStuff.removeBody(this.physical.body);
   }

   public void addToPhysics() {
      this.removeFromPhysics();
      BulletStuff.addDiceBody(this.physical.body);
   }

   protected void resetSpeed() {
      this.physical.body.setLinearVelocity(new Vector3());
      this.physical.body.setAngularVelocity(new Vector3());
   }

   protected void outOfBoundsCheck() {
      this.getPosition(this.temp);
      if (this.temp.y < -100.0F) {
         this.physical.transform.setToTranslation(BulletStuff.getMiddle().add(0.0F, 5.0F, 0.0F));
         this.physical.body.setWorldTransform(this.physical.transform);
         this.physical.body.setLinearVelocity(new Vector3(0.0F, 0.0F, 0.0F));
         this.randomise(0.0F, 0.0F, 0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F);
      }
   }

   protected void getPosition(Vector3 out) {
      if (this.getState() != Die.DieState.Locking && this.getState() != Die.DieState.Unlocking && this.getState() != Die.DieState.SlidingToMiddle) {
         this.physical.transform.getTranslation(out);
      } else {
         out.set(this.targetPos);
      }
   }

   protected void randomise(float up, float upRand, float side, float sideRand, float rot, float rotRand, float centeringMult, float centeringRand) {
      this.randomise(up, upRand, side, sideRand, rot, rotRand, centeringMult, centeringRand, 1.0F, new Random());
   }

   protected void randomise(
      float up, float upRand, float side, float sideRand, float rot, float rotRand, float centeringMult, float centeringRand, float mult, Random r
   ) {
      for (int i = 0; i < 10; i++) {
         r.nextInt();
      }

      float x = (side + Maths.factor(sideRand, r)) * Maths.mult(r);
      float y = up + Maths.factor(upRand, r);
      float z = (side + Maths.factor(sideRand, r)) * Maths.mult(r);
      float totalRot = rot + r.nextFloat() * rotRand;
      float totalRotFactor = 0.0F;

      for (int i = 0; i < rotFactors.length; i++) {
         rotFactors[i] = 0.8F;
         if (i != 1) {
            totalRotFactor += rotFactors[i];
         }
      }

      for (int ix = 0; ix < rotFactors.length; ix++) {
         rotFactors[ix] = rotFactors[ix] / totalRotFactor;
      }

      for (int ix = 0; ix < rotFactors.length; ix++) {
         rotActuals[ix] = rotFactors[ix] * totalRot * Maths.mult(r);
      }

      float totalCentering = Maths.factor(centeringRand, r) + centeringMult;
      this.getPosition(this.temp);
      Vector3 middle = BulletStuff.getMiddle();
      x += (middle.x - this.temp.x) * totalCentering;
      z += (middle.z - this.temp.z) * totalCentering;
      this.applyForces(x * mult, y * mult, z * mult, rotActuals[0] * mult, rotActuals[1] * mult, rotActuals[2] * mult);
   }

   protected void applyForces(float x, float y, float z, float r1, float r2, float r3) {
      this.physical.body.setLinearVelocity(new Vector3(x, y, z));
      this.physical.body.setAngularVelocity(new Vector3(r1, r2, r3));
   }

   protected boolean isStopped() {
      this.physical.transform.getTranslation(this.temp);
      return !this.isMoving() && this.temp.y < -(0.0F - this.dieSize - 0.05F);
   }

   protected boolean isMoving() {
      return this.physical.isMoving();
   }

   protected static float getFloat(TextureRegion tr) {
      return getFloat(tr.getRegionX() / 128, tr.getRegionY() / 128);
   }

   protected static float getFloat(int x, int y) {
      int num = x + 16 * y;
      return num / 255.0F + 0.002F;
   }

   public abstract float getPixelSize();

   protected Quaternion getSideQuaternion(boolean panel) {
      return this.getSideQuaternion(panel, false);
   }

   protected Quaternion getSideQuaternion(boolean panel, boolean randomYaw) {
      int side = this.lockedSide == -1 ? this.prevLockedSide : this.lockedSide;
      if (panel) {
         double angle = Math.atan(BulletStuff.cam.position.y / BulletStuff.cam.position.z);
         return new Quaternion().setEulerAnglesRad(0.0F, (float)(-angle), 0.0F).mul(d6Quats[side]);
      } else {
         return this.getSideQuaternion(side, randomYaw);
      }
   }

   protected Quaternion getSideQuaternion(int side, boolean randomYaw) {
      return new Quaternion().setEulerAnglesRad(randomYaw ? (float)(Math.random() * 1000.0) : 0.0F, (float) (-Math.PI / 2), 0.0F).mul(d6Quats[side]);
   }

   public void moveTo(Vector2 position, Runnable runnable, float interpSpeed) {
      this.moveTo(position.x, position.y, runnable, interpSpeed);
   }

   protected void moveTo(float screenX, float screenY, Runnable runnable, float interpSpeed) {
      this.setState(Die.DieState.Locking);
      this.moveTo(this.screenTo3D(screenX, screenY), this.getSideQuaternion(true), runnable, interpSpeed);
   }

   public void slideToPhysicalSpace(Vector3 position) {
      this.setState(Die.DieState.SlidingToMiddle);
      this.moveTo(position, this.getSideQuaternion(false, true), null, 0.22F);
   }

   protected void moveTo(Vector3 position, Quaternion rotation, Runnable runnable, float interpSpeed) {
      this.currentInterpSpeed = interpSpeed;
      this.moveRunnable = runnable;
      this.dist = 0.0F;
      this.physical.update();
      this.startPos = this.physical.transform.getTranslation(this.startPos);
      this.targetPos.set(position);
      this.physical.transform.getRotation(this.startQuat);
      this.targetQuat = rotation;
   }

   protected Vector3 getFreeSpot() {
      float dist = 0.0F;
      float angle = 0.0F;
      float yPos = this.getSafeY();
      float rand = 0.5F;
      float startX = Tann.random(-0.5F, 0.5F);
      float startZ = Tann.random(-0.5F, 0.5F);
      if (com.tann.dice.Main.isPortrait()) {
         startX += 0.15F;
      }

      int attemps = 20000;

      for (int i = 0; i < 20000; i++) {
         this.positionTest.set(BulletStuff.getMiddle().add((float)Math.sin(angle) * dist + startX, yPos, (float)Math.cos(angle) * dist + startZ));
         if (this.isPositionValid(this.positionTest)) {
            return this.positionTest;
         }

         dist += 0.004F;
         angle++;
      }

      return this.makeFailsafeFreeSpot();
   }

   private Vector3 makeFailsafeFreeSpot() {
      float randDist = 0.8F;
      return BulletStuff.getMiddle().cpy().add(Tann.random(-randDist, randDist), 0.5F, Tann.random(-randDist, randDist));
   }

   private boolean isPositionValid(Vector3 positionTest) {
      for (int i = 0; i < BulletStuff.dice.size(); i++) {
         Die d = BulletStuff.dice.get(i);
         if (d != this) {
            d.getPosition(this.temp);
            float xDiff = this.temp.x - positionTest.x;
            float zDiff = this.temp.z - positionTest.z;
            float dieDist = (float)Math.sqrt(xDiff * xDiff + zDiff * zDiff);
            if (dieDist < 1.4 * (this.dieSize + d.dieSize)) {
               return false;
            }

            if (!BulletStuff.isWithinBox(positionTest, this.physical.radius * 0.9F)) {
               return false;
            }
         }
      }

      return true;
   }

   public static void scatterDice(List<EntDie> dice) {
      if (dice.size() == 0) {
         Sounds.playSound(Sounds.error);
      } else {
         for (Die d : dice) {
            d.returnToPlay(null, false, 0.3F);
         }
      }
   }

   protected Vector3 screenTo3D(float screenX, float screenY) {
      double angle = -Math.atan2(BulletStuff.cam.position.y, BulletStuff.cam.position.z);
      float factor = BulletStuff.BOX_WIDTH_FOR_PIXEL_SIZE / BulletStuff.getWidth3d();
      float yzFactor = factor * (screenY + this.getPixelSize() / 2.0F - BulletStuff.getHeight3d() / 2.0F);
      return BulletStuff.cam
         .position
         .cpy()
         .add(
            BulletStuff.camVector
               .cpy()
               .scl(BulletStuff.BOX_DISTANCE_FOR_PIXEL_SIZE + this.physical.dimensions.y / 2.0F)
               .add(
                  screenX * factor - BulletStuff.BOX_WIDTH_FOR_PIXEL_SIZE / 2.0F + this.physical.dimensions.y / 2.0F,
                  (float)(Math.cos(angle) * yzFactor),
                  (float)(Math.sin(angle) * yzFactor)
               )
         );
   }

   public void slideToPanel() {
      this.removeFromPhysics();
      this.physical.transform.getRotation(this.originalRotation);
      this.getDieContainer().startLockingDie();
      this.moveTo(this.getDieContainer().getDieHolderLocation(true), new Runnable() {
         @Override
         public void run() {
            Die.this.getDieContainer().lockDie();
         }
      }, this.getLockSpeed());
   }

   protected float getLockSpeed() {
      return getBaseInterpSpeed() * 0.75F;
   }

   public void returnToPlay(Runnable runnable, boolean applyNewQuaternion, float interpSpeed) {
      if (this.getState() == Die.DieState.Locked) {
         this.setupInitialLocation();
      }

      this.addToScreen();
      this.setState(Die.DieState.Unlocking);
      int newSide = this.getSideIndex();
      if (newSide == -1) {
         newSide = this.prevLockedSide;
      }

      if (applyNewQuaternion) {
         newSide = (int)(this.getSideIndex() + Math.random() * 6.0) % 6;
      }

      Vector3 targetLocation = this.getFreeSpot();
      Quaternion newQuat = this.getSideQuaternion(newSide, true);
      if (BulletStuff.isLined() && BulletStuff.okSizeForLiningUp()) {
         EntDie ed = (EntDie)this;
         if (ed.ent.isPlayer()) {
            targetLocation = EntDie.getLineLoc(ed);
            newQuat = this.getSideQuaternion(newSide, false);
         }
      }

      this.moveTo(targetLocation, newQuat, runnable, interpSpeed);
      this.flatDraw = false;
      this.undamp();
   }

   public void setupInitialLocation() {
      if (this.disposed) {
         TannLog.error("trying to setup initial location when disposed?");
      }

      Vector2 dHol = new Vector2();
      if (this.getDieContainer() != null) {
         dHol = this.getDieContainer().getDieHolderLocation(true);
      }

      this.physical.transform.setToTranslation(this.screenTo3D(dHol.x, dHol.y));
      this.physical.transform.rotate(this.getSideQuaternion(true, true));
      this.physical.body.setWorldTransform(this.physical.transform);
      this.originalRotation = this.getSideQuaternion(false, false);
   }

   public float getMass() {
      return (float)Math.pow(this.getPixelSize(), 3.0);
   }

   public void clearTextureCache() {
      this.faceLocs = null;
      this.lapelLocs = null;
      this.keywordLocs = null;
      this.hsl = null;
   }

   public float[] getFaceLocs() {
      if (this.faceLocs != null) {
         return this.faceLocs;
      } else {
         this.faceLocs = new float[12];
         float width = this.getSideTexture(0).getTexture().getWidth();
         float height = this.getSideTexture(0).getTexture().getHeight();

         for (int i = 0; i < 6; i++) {
            TextureRegion face = this.getSideTexture(i);
            if (face != null) {
               this.faceLocs[2 * i + 0] = face.getRegionX() / width;
               this.faceLocs[2 * i + 1] = face.getRegionY() / height;
            }
         }

         return this.faceLocs;
      }
   }

   public void getPipsAsFloats(float[] pips) {
      for (int i = 0; i < 6; i++) {
         pips[i] = this.getPipFloat(i, false);
      }
   }

   public void getPipBonus(float[] pips) {
      for (int i = 0; i < 6; i++) {
         pips[i] = this.getPipFloat(i, true);
      }

      for (int i = 0; i < 6; i++) {
         pips[6 + i] = this.getBonusPipFloat(i);
      }
   }

   protected float getBonusPipFloat(int side) {
      return 0.0F;
   }

   public Vector2 getLapelLocs() {
      if (this.lapelLocs != null) {
         return this.lapelLocs;
      } else {
         float width = this.getSideTexture(0).getTexture().getWidth();
         float height = this.getSideTexture(0).getTexture().getHeight();
         TextureRegion tr = this.getLapelTexture();
         if (tr == null) {
            tr = ImageUtils.loadExt3d("misc/bigempty");
         }

         this.lapelLocs = new Vector2(tr.getRegionX() / width, tr.getRegionY() / height);
         return this.lapelLocs;
      }
   }

   public float[] getKeywordLocs() {
      return null;
   }

   public float[] getHSLData() {
      return null;
   }

   protected abstract DieContainer getDieContainer();

   public SideType getCurrentSide() {
      int side = this.getSideIndex();
      return side >= 0 ? this.getSide(side) : null;
   }

   public Color getColour() {
      return Colours.green;
   }

   protected TextureRegion getSideTexture(int side) {
      return this.getSide(side).getTexture();
   }

   protected float getPipFloat(int side, boolean bonus) {
      return -1.0F;
   }

   protected TextureRegion getLapelTexture() {
      return blank;
   }

   public void dispose() {
      if (!this.disposed) {
         BulletStuff.dice.remove(this);
         BulletStuff.instances.remove(this.physical);
         this.removeFromScreen();
         this.disposeMe.dispose();
         this.physical.dispose();
         this.disposed = true;
      }
   }

   public void reset() {
      this.lockedSide = -1;
   }

   public final SideType getSide(int index) {
      return this.sides[index];
   }

   protected void construct() {
      ModelBuilder mb = new ModelBuilder();
      mb.begin();
      mb.node().id = "dieIndex";
      if (MATERIAL == null) {
         MATERIAL = new Material(new Attribute[]{TextureAttribute.createDiffuse(this.getSideTexture(0).getTexture())});
      }

      MeshPartBuilder mpb = mb.part("dieIndex", 4, 29L, MATERIAL);
      float normalX = 0.0F;
      float normalY = 0.0F;
      float[] f = new float[]{getFloat(4, 5)};
      float inner = f[(int)(Math.random() * f.length)];

      for (int i = 0; i < 6; i++) {
         normalX = i;
         SideType side = this.getSide(i);
         TextureRegion base = side.getTexture();
         mpb.setColor(getFloat(base), 0.0F, inner, dieIndex / 5.0F + 0.1F);
         switch (i) {
            case 0:
               mpb.rect(
                  -this.dieSize,
                  -this.dieSize,
                  -this.dieSize,
                  -this.dieSize,
                  this.dieSize,
                  -this.dieSize,
                  this.dieSize,
                  this.dieSize,
                  -this.dieSize,
                  this.dieSize,
                  -this.dieSize,
                  -this.dieSize,
                  normalX,
                  normalY,
                  -1.0F
               );
               break;
            case 1:
               mpb.rect(
                  -this.dieSize,
                  this.dieSize,
                  this.dieSize,
                  -this.dieSize,
                  -this.dieSize,
                  this.dieSize,
                  this.dieSize,
                  -this.dieSize,
                  this.dieSize,
                  this.dieSize,
                  this.dieSize,
                  this.dieSize,
                  normalX,
                  normalY,
                  1.0F
               );
               break;
            case 2:
               mpb.rect(
                  -this.dieSize,
                  -this.dieSize,
                  this.dieSize,
                  -this.dieSize,
                  -this.dieSize,
                  -this.dieSize,
                  this.dieSize,
                  -this.dieSize,
                  -this.dieSize,
                  this.dieSize,
                  -this.dieSize,
                  this.dieSize,
                  normalX,
                  normalY,
                  0.0F
               );
               break;
            case 3:
               mpb.rect(
                  -this.dieSize,
                  this.dieSize,
                  -this.dieSize,
                  -this.dieSize,
                  this.dieSize,
                  this.dieSize,
                  this.dieSize,
                  this.dieSize,
                  this.dieSize,
                  this.dieSize,
                  this.dieSize,
                  -this.dieSize,
                  normalX,
                  normalY,
                  0.0F
               );
               break;
            case 4:
               mpb.rect(
                  -this.dieSize,
                  -this.dieSize,
                  this.dieSize,
                  -this.dieSize,
                  this.dieSize,
                  this.dieSize,
                  -this.dieSize,
                  this.dieSize,
                  -this.dieSize,
                  -this.dieSize,
                  -this.dieSize,
                  -this.dieSize,
                  normalX,
                  normalY,
                  0.0F
               );
               break;
            case 5:
               mpb.rect(
                  this.dieSize,
                  -this.dieSize,
                  -this.dieSize,
                  this.dieSize,
                  this.dieSize,
                  -this.dieSize,
                  this.dieSize,
                  this.dieSize,
                  this.dieSize,
                  this.dieSize,
                  -this.dieSize,
                  this.dieSize,
                  normalX,
                  normalY,
                  0.0F
               );
         }
      }

      Model model = mb.end();
      this.disposeMe = new btBoxShape(new Vector3(this.dieSize, this.dieSize, this.dieSize));
      CollisionObject co = new CollisionObject(model, "dieIndex", this.disposeMe, this.getMass(), 0.005F);
      this.physical = co;
      co.body.setCollisionFlags(co.body.getCollisionFlags() | 8);
      this.physical.body.setActivationState(4);
      co.body.setCollisionFlags(512);
      co.body.setContactCallbackFlag(512);
      co.body.setContactCallbackFilter(512);
      co.body.setActivationState(4);
      dieIndex++;
      co.body.userData = this;
      this.physical.userData = this;
      this.physical.updateBounds();
      this.physical.transform.setTranslation(new Vector3(500.0F, 500.0F, 500.0F));
   }

   public static float getBaseInterpSpeed() {
      return 0.33F * (com.tann.dice.Main.isPortrait() ? 1.0F : 1.0F);
   }

   public static enum DieState {
      Rolling,
      Stopped,
      Locked,
      Locking,
      Unlocking,
      SlidingToMiddle;

      public boolean isLockedOrLocking() {
         return this == Locked || this == Locking;
      }
   }
}
