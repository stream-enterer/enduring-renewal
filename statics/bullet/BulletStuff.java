package com.tann.dice.statics.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.ObjectSet.ObjectSetIterator;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.die.Die;
import com.tann.dice.gameplay.content.ent.die.EntDie;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.gameplay.PlayerRollingPhase;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.Explanel.RollPanel;
import com.tann.dice.screens.dungeon.panels.tutorial.TutorialManager;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.MemUtils;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BulletStuff {
   public static float BOX_DISTANCE_FOR_PIXEL_SIZE;
   public static final short OBJECT_FLAG = 512;
   public static final short ALL_FLAG = -1;
   public static PerspectiveCamera cam;
   static CameraInputController camController;
   public static PerspectiveCamera spinCam;
   public static ObjectSet<ModelInstance> instances = new ObjectSet();
   public static List<CollisionObject> walls = new ArrayList<>();
   public static List<Die> dice = new ArrayList<>();
   static ModelBatch modelBatch;
   static Model model;
   static Shader shader;
   private static Vector3 dieClickPosition = new Vector3();
   static float camX = 0.0F;
   public static float camY = 20.0F;
   static float camZ = 8.5F;
   public static float BOX_HEIGHT_FOR_PIXEL_SIZE;
   public static float BOX_WIDTH_FOR_PIXEL_SIZE;
   private static float fullscreenTrayHeight;
   private static float fullscreenTrayWidth;
   static float fov = 20.0F;
   private static NoAutoDispose nad;
   private static boolean needsDispose;
   static final int bonusSide = 8;
   private static final float mb = 0.2F;
   public static Vector3 camVector = new Vector3();
   public static float camDist = 0.0F;
   static Vector3 center = new Vector3();
   static Rectangle cachedWallBounds;
   private static long last_empty_tap = 0L;
   private static final long DOUBLE_TAP_MAX = 400L;
   public static final long clickDur = 220L;
   public static final long holdDur = 221L;
   static EntDie clicked;
   static long lastDown;
   static int storedPointer;
   static Group mousedPanel = null;
   private static boolean lined = false;
   private static boolean simulating;
   private static final int slideAmount = 13;

   public static boolean isAlive() {
      return needsDispose;
   }

   public static void init() {
      TannLog.log("Bullet init start");
      if (needsDispose) {
         dispose();
      }

      needsDispose = true;
      Bullet.init();
      spinCam = new PerspectiveCamera(fov, com.tann.dice.Main.width, com.tann.dice.Main.height);
      nad = new NoAutoDispose();
      modelBatch = new ModelBatch();
      float magicNumber = 0.113F;
      BOX_DISTANCE_FOR_PIXEL_SIZE = magicNumber / com.tann.dice.Main.scale * Gdx.graphics.getHeight();
      BOX_HEIGHT_FOR_PIXEL_SIZE = calculateBoxHeightAtDistance(BOX_DISTANCE_FOR_PIXEL_SIZE);
      BOX_WIDTH_FOR_PIXEL_SIZE = BOX_HEIGHT_FOR_PIXEL_SIZE * getWidth3d() / getHeight3d();
      setupCamera();
      setupWalls();
      shader = new DieShader();
      initShader();
      TannLog.log("Bullet init end");
   }

   private static void setupCamera() {
      float actualDist = getBaseCamDist() * 1.6F;
      float diceAdjust = 0.0F;
      if (com.tann.dice.Main.getSettings() != null) {
         diceAdjust = OptionUtils.getDiceAdjust() * 2.0F;
      }

      actualDist = (float)(actualDist * Math.pow(0.9F, diceAdjust));
      if (com.tann.dice.Main.isPortrait()) {
         camY = actualDist * 2.5F;
         camZ = actualDist * 0.23F;
      } else {
         camY = actualDist * 2.3F;
         camZ = actualDist * 0.8F;
      }

      updateCamera();
   }

   private static float getBaseCamDist() {
      return CameraCalc.calcDistFromEstimatedBounds(getPlayWidth(), getPlayHeight());
   }

   public static Vector3 getMiddle() {
      return center.cpy();
   }

   public static void initShader() {
      shader.init();
   }

   public static boolean isWithinBox(Vector3 targetLoc, float radius) {
      Vector3 middle = getMiddle();
      Vector3 diff = targetLoc.cpy().sub(middle);
      float w = cachedWallBounds.width;
      float h = cachedWallBounds.height;
      return diff.x - radius > -w / 2.0F && diff.x + radius < w / 2.0F && diff.z - radius > -h / 2.0F && diff.z + radius < h * 0.4F;
   }

   private static float getPlayWidth() {
      float sides = getLeftSide() + getRightSide();
      return 1.0F - sides;
   }

   private static float getPlayHeight() {
      return 1.0F - getTopNelson() - getBottomNelson();
   }

   private static float getLeftSide() {
      if (com.tann.dice.Main.isPortrait()) {
         return 0.0F;
      } else {
         float v = (92.0F + com.tann.dice.Main.self().notch(3)) / com.tann.dice.Main.width;
         return Math.max(v, 0.2F);
      }
   }

   private static float getRightSide() {
      if (com.tann.dice.Main.isPortrait()) {
         return 0.0F;
      } else {
         float v = (92.0F + getPanelSlideAmount() + com.tann.dice.Main.self().notch(1)) / com.tann.dice.Main.width;
         return Math.max(v, 0.2F);
      }
   }

   public static void setupWalls() {
      fullscreenTrayHeight = calculateBoxHeightAtDistance(camDist);
      fullscreenTrayWidth = fullscreenTrayHeight * com.tann.dice.Main.width / com.tann.dice.Main.height;
      float bottomCutoff = getBottomCutoff();
      float topBar = getTopNelson() * fullscreenTrayHeight;
      setupWalls(new Rectangle(getLeftSide() * fullscreenTrayWidth, topBar, getPlayWidth() * fullscreenTrayWidth, fullscreenTrayHeight - topBar - bottomCutoff));
   }

   private static float getBottomNelson() {
      return com.tann.dice.Main.isPortrait() ? 0.59F : 33.0F / com.tann.dice.Main.height;
   }

   private static float getBottomCutoff() {
      return getBottomNelson() * fullscreenTrayHeight;
   }

   private static void setupWalls(Rectangle wallArea) {
      for (CollisionObject co : walls) {
         nad.dynamicsWorld.removeRigidBody(co.body);
      }

      walls = makeWalls(wallArea);

      for (CollisionObject co : walls) {
         co.initialUpdate();
         nad.dynamicsWorld.addRigidBody(co.body, 512, -1);
      }
   }

   private static float calculateBoxHeightAtDistance(float distance) {
      float cos = (float)Math.cos(Math.toRadians(fov / 2.0F));
      float hypot = distance / cos;
      float half = (float)Math.sqrt(hypot * hypot - distance * distance);
      return half * 2.0F;
   }

   public static void updateCamera() {
      cam = new PerspectiveCamera(fov, getWidth3d(), getHeight3d());
      cam.position.set(camX, camY, camZ);
      Vector3 lookAt = new Vector3(0.0F, 0.0F, 0.0F);
      cam.lookAt(lookAt);
      cam.update();
      camController = new CameraInputController(cam);
      camVector = cam.direction.cpy().nor();
      camDist = cam.position.cpy().sub(lookAt).len();
   }

   private static List<CollisionObject> makeWalls(Rectangle playArea) {
      cachedWallBounds = playArea;
      float thickness = 0.5F;
      float height = 20.0F;
      ModelBuilder mb = new ModelBuilder();
      List<CollisionObject> results = new ArrayList<>();
      float trX = -fullscreenTrayWidth / 2.0F + playArea.width / 2.0F + playArea.x;
      float trY = height / 2.0F;
      float trZ = -fullscreenTrayHeight / 2.0F + playArea.height / 2.0F + playArea.y;
      center = new Vector3(trX, 0.0F, trZ);
      mb.begin();
      mb.node().id = "ground";
      mb.part("ground", 4, 9L, new Material(new Attribute[]{new BlendingAttribute(770, 771), ColorAttribute.createDiffuse(new Color(0.3F, 0.7F, 0.4F, 0.5F))}))
         .box(0.0F, 0.0F, 0.0F);
      model = mb.end();
      float floorFriction = 100.0F;
      float wallFriction = 0.0F;

      for (int i = 0; i < 2; i++) {
         CollisionObject wall = new CollisionObject(
            model,
            "ground",
            new btBoxShape(new Vector3(playArea.width / 2.0F, thickness / 2.0F, playArea.height / 2.0F)),
            0.0F,
            i == 0 ? floorFriction : wallFriction
         );
         if (i == 0) {
            wall.userData = 5;
            wall.body.userData = 5;
         }

         wall.transform.trn(trX, trY + (i * 2 - 1) * (height / 2.0F + thickness / 2.0F), trZ);
         results.add(wall);
      }

      for (int i = 0; i < 2; i++) {
         CollisionObject wall = new CollisionObject(
            model, "ground", new btBoxShape(new Vector3(thickness, height / 2.0F, playArea.height / 2.0F)), 0.0F, wallFriction
         );
         wall.transform.trn(trX + (i * 2 - 1) * (playArea.width / 2.0F + thickness / 2.0F), trY, trZ);
         results.add(wall);
      }

      for (int i = 0; i < 2; i++) {
         CollisionObject wall = new CollisionObject(
            model, "ground", new btBoxShape(new Vector3(playArea.width / 2.0F, height / 2.0F, thickness / 2.0F)), 0.0F, wallFriction
         );
         wall.transform.trn(trX, trY, trZ + (i * 2 - 1) * (playArea.height / 2.0F + thickness / 2.0F));
         results.add(wall);
      }

      if (com.tann.dice.Main.isPortrait()) {
         float ratio = 60.0F / com.tann.dice.Main.height;
         float factor = 2.2F;
         float rad = playArea.height * ratio * factor;
         results.add(makeBlockerSphere(rad, trX - playArea.width / 2.0F - rad * 0.3F, 0, trZ - playArea.height / 2.0F));
      } else {
         float ratio = 35.0F / com.tann.dice.Main.width;
         float factor = 2.2F;
         float rad = playArea.height * ratio * factor;
         results.add(makeBlockerSphere(rad, 0.0F, 0, trZ + playArea.height / 2.0F + rad * 0.37F));
      }

      return results;
   }

   private static CollisionObject makeBlockerSphere(float rad, float trX, int trY, float trZ) {
      CollisionObject sphere = new CollisionObject(model, "ground", new btSphereShape(rad), 0.0F, 0.0F);
      sphere.transform.trn(trX, trY, trZ);
      return sphere;
   }

   public static void refreshEntities(List<Ent> entities) {
      clearAllDice();
      instances.clear();
      List<Die> entDice = new ArrayList<>();

      for (Ent e : entities) {
         entDice.add(e.getDie());
      }

      refreshDice(entDice);
   }

   public static void refreshDice(List<? extends Die> newDice) {
      dice.clear();
      dice.addAll(newDice);
   }

   public static void render() {
      camController.update();
      modelBatch.begin(cam);
      modelBatch.render(instances, shader);
      modelBatch.end();
   }

   private static void manualCameraControl() {
      float mul = Gdx.graphics.getDeltaTime() * 50.0F;
      if (Gdx.input.isKeyPressed(21)) {
         camZ -= mul;
      }

      if (Gdx.input.isKeyPressed(22)) {
         camZ += mul;
      }

      if (Gdx.input.isKeyPressed(19)) {
         camY -= mul;
      }

      if (Gdx.input.isKeyPressed(20)) {
         camY += mul;
      }

      updateCamera();
   }

   public static void drawSpinnyDie3(Die die, float x, float y, float size) {
      if (isAlive()) {
         float spinsPerSecond = 0.22F;
         float camDistScale = 0.27532798F;
         Vector3 v = new Vector3(-0.5F, 1.0F, -0.5F);
         v.scl(BOX_DISTANCE_FOR_PIXEL_SIZE);
         v.scl(0.27532798F);
         spinCam.position.set(v);
         spinCam.lookAt(-1.0F, 2.0F, -1.0F);
         spinCam.update();
         float initialSize = 5.0F * die.getPixelSize();
         float sizeFactor = size / initialSize;
         Gdx.gl
            .glViewport(
               (int)(x - com.tann.dice.Main.width * sizeFactor / 2.0F),
               (int)(y - com.tann.dice.Main.height * sizeFactor / 2.0F),
               (int)(com.tann.dice.Main.width * sizeFactor),
               (int)(com.tann.dice.Main.height * sizeFactor)
            );
         Matrix4 copy = die.physical.transform.cpy();
         boolean prev = die.flatDraw;
         die.flatDraw = false;
         die.physical.transform.setToRotation(Vector3.X, 0.0F);
         die.physical.transform.setToRotation(1.0F, 1.0F, 1.0F, com.tann.dice.Main.secs * 360.0F * 0.22F);
         modelBatch.begin(spinCam);
         modelBatch.render(die.physical, shader);
         modelBatch.end();
         Gdx.gl.glViewport(0, 0, com.tann.dice.Main.width, com.tann.dice.Main.height);
         die.physical.transform = copy;
         die.flatDraw = prev;
      }
   }

   public static boolean touchDown(float x, float y, int pointer, int button) {
      if (!PhaseManager.get().getPhase().canRoll()) {
         return false;
      } else {
         Die rawDie = getDieUnderMouse(Gdx.input.getX(pointer), Gdx.input.getY(pointer));
         if (rawDie == null || !((EntDie)rawDie).ent.isPlayer()) {
            long newTap = System.currentTimeMillis();
            long doubleTap = newTap - last_empty_tap;
            if (doubleTap <= 400L) {
               last_empty_tap = 0L;
               toggleDiceScatter();
            } else {
               last_empty_tap = newTap;
            }

            return false;
         } else if (button == 1) {
            attempToShowExplanel(rawDie, pointer);
            return true;
         } else {
            lastDown = System.currentTimeMillis();
            if (rawDie != null) {
               clicked = (EntDie)rawDie;
               storedPointer = pointer;
            }

            return true;
         }
      }
   }

   private static void attempToShowExplanel(Die rawDie, int pointer) {
      if (rawDie != null && rawDie.getState() == Die.DieState.Stopped) {
         showExplanel((EntDie)rawDie, pointer);
      }
   }

   public static void touchUp(int screenX, int screenY, int pointer, int button) {
      highlightPanelDice(null);
      if (mousedPanel != null) {
         mousedPanel.remove();
         mousedPanel = null;
      }

      Die rawDie = getDieUnderMouse(Gdx.input.getX(pointer), Gdx.input.getY(pointer));
      if (rawDie == null) {
         clicked = null;
      } else {
         long dur = System.currentTimeMillis() - lastDown;
         if ((dur < 220L || !com.tann.dice.Main.self().control.allowLongPress()) && clicked != null && clicked == rawDie) {
            clicked.toggleLock();
            DungeonScreen.get().onLock();
         }

         clicked = null;
         lastDown = -1L;
      }
   }

   private static void highlightPanelDice(Ent highlight) {
      if (DungeonScreen.get() != null) {
         for (Ent de : DungeonScreen.get().hero.getEntities()) {
            de.getEntPanel().setHighlightDice(de == highlight);
         }
      }
   }

   private static void setupExplanelPosition(Group group, int mouseX, int mouseY) {
      int explanelFullHeight = (int)group.getHeight();
      int distFromFinger = 8;
      int expY = com.tann.dice.Main.height - mouseY / com.tann.dice.Main.scale + distFromFinger;
      if (expY + group.getHeight() > com.tann.dice.Main.height) {
         expY -= distFromFinger * 2;
         expY -= explanelFullHeight;
      }

      int potentialX = (int)(mouseX / com.tann.dice.Main.scale - group.getWidth() / 2.0F);
      potentialX = (int)Math.max(0.0F, Math.min(com.tann.dice.Main.width - group.getWidth(), (float)potentialX));
      group.setPosition(potentialX, expY);
   }

   public static void update(float delta) {
      Phase p = PhaseManager.get().getPhase();
      if (p != null && p.updateDice()) {
         if (nad != null && !nad.invalid()) {
            int times = (int)(1.0F / OptionUtils.getRollSpeedMultiplier(p instanceof PlayerRollingPhase));

            for (int j = 0; j < times; j++) {
               singleTick(delta);
            }

            if (!allDiceStopped()) {
               com.tann.dice.Main.requestRendering();
            }

            long dur = System.currentTimeMillis() - lastDown;
            if (com.tann.dice.Main.self().control.allowLongPress() && dur >= 221L && clicked != null) {
               attempToShowExplanel(clicked, storedPointer);
               clicked = null;
            }
         } else {
            TannLog.error("something null in bullet");
         }
      }
   }

   private static void singleTick(float delta) {
      nad.dynamicsWorld.stepSimulation(delta, 5, 0.016666668F);

      for (int i = 0; i < dice.size(); i++) {
         dice.get(i).update(delta);
      }

      ObjectSetIterator var3 = instances.iterator();

      while (var3.hasNext()) {
         ModelInstance mi = (ModelInstance)var3.next();
         if (mi instanceof CollisionObject) {
            ((CollisionObject)mi).update();
         }
      }
   }

   private static void showExplanel(EntDie d, int pointer) {
      if (mousedPanel != null) {
         mousedPanel.remove();
      }

      highlightPanelDice(d.ent);
      Group rp = RollPanel.make(d.ent);
      mousedPanel = rp;
      com.tann.dice.Main.getCurrentScreen().addActor(mousedPanel);
      setupExplanelPosition(mousedPanel, Gdx.input.getX(pointer), Gdx.input.getY(pointer));
      DungeonScreen ds = DungeonScreen.get();
      if (ds != null) {
         ds.getTutorialManager().onAction(TutorialManager.TutorialAction.DieInfo);
      }
   }

   public static boolean isMouseOnDice() {
      return getDieUnderMouse(Gdx.input.getX(0), Gdx.input.getY(0)) != null;
   }

   public static Die getDieUnderMouse(int screenX, int screenY) {
      Ray ray = cam.getPickRay(screenX, screenY);
      Die result = null;
      float distance = -1.0F;

      for (Die d : dice) {
         CollisionObject instance = d.physical;
         instance.updateBounds();
         instance.transform.getTranslation(dieClickPosition);
         dieClickPosition.add(instance.center);
         float len = ray.direction.dot(dieClickPosition.x - ray.origin.x, dieClickPosition.y - ray.origin.y, dieClickPosition.z - ray.origin.z);
         if (!(len < 0.0F)) {
            float dist2 = dieClickPosition.dst2(
               ray.origin.x + ray.direction.x * len, ray.origin.y + ray.direction.y * len, ray.origin.z + ray.direction.z * len
            );
            if ((!(distance >= 0.0F) || !(dist2 > distance)) && dist2 <= instance.radius * instance.radius) {
               result = d;
               distance = dist2;
            }
         }
      }

      return result == null ? null : result;
   }

   public static boolean allDiceLockedOrLocking() {
      for (int i = 0; i < dice.size(); i++) {
         Die d = dice.get(i);
         if (!d.getState().isLockedOrLocking()) {
            return false;
         }
      }

      return true;
   }

   public static boolean allDiceStopped() {
      for (int i = 0; i < dice.size(); i++) {
         Die d = dice.get(i);
         if (d.getState() != Die.DieState.Stopped && d.getState() != Die.DieState.Locked) {
            return false;
         }
      }

      return true;
   }

   public static void reset() {
      clearAllDice();
      instances.clear();
   }

   public static void clearAllDice() {
      for (Die d : dice) {
         d.removeFromScreen();
      }

      dice.clear();
   }

   private static void clearAllStatics() {
      cam = null;
      camController = null;
      modelBatch = null;
      instances = new ObjectSet();
      walls = new ArrayList<>();
      dice = new ArrayList<>();
      nad = null;
      model = null;
      shader = null;
   }

   public static void dispose() {
      needsDispose = false;
      if (!OptionLib.DISABLE_3D_DISPOSE.c()) {
         MemUtils.disp(modelBatch, model, shader, nad);
         if (dice != null) {
            for (Die d : new ArrayList<>(dice)) {
               d.dispose();
            }
         }

         if (walls != null) {
            for (CollisionObject wall : walls) {
               wall.dispose();
            }
         }

         if (instances != null) {
            ObjectSetIterator var3 = instances.iterator();

            while (var3.hasNext()) {
               ModelInstance mi = (ModelInstance)var3.next();
               mi.model.dispose();
            }
         }

         if (walls != null) {
            for (CollisionObject co : walls) {
               co.dispose();
            }
         }
      }

      clearAllStatics();
   }

   public static float getWidth3d() {
      return (float)Gdx.graphics.getWidth() / com.tann.dice.Main.scale;
   }

   public static float getHeight3d() {
      return (float)Gdx.graphics.getHeight() / com.tann.dice.Main.scale;
   }

   public static List<EntDie> getDice(Boolean hero) {
      List<EntDie> result = new ArrayList<>();

      for (Ent de : DungeonScreen.get().getFightLog().getActiveEntities(hero)) {
         result.add(de.getDie());
      }

      return result;
   }

   public static void toggleDiceScatter() {
      List<EntDie> availableToRoll = DungeonScreen.get().rollManager.getHeroDiceAvailableToRoll();
      List<EntDie> heroDice = getDice(true);
      if (availableToRoll.size() != 0) {
         lined = !isLined();
         if (isLined()) {
            Sounds.playSound(Sounds.lock);
            EntDie.organiseDiceIntoLine(heroDice);
         } else {
            Sounds.playSound(Sounds.unlock);
            Die.scatterDice(availableToRoll);
         }
      }
   }

   public static boolean isLined() {
      return lined && !com.tann.dice.Main.isPortrait();
   }

   public static void resetAlignment() {
      lined = false;
   }

   private static float getTopNelson() {
      return 0.15F * (com.tann.dice.Main.isPortrait() ? 0.35F : 1.0F);
   }

   public static List<Integer> predictAndReset(List<EntDie> toRoll, boolean firstRoll, long rollSeed, boolean player) {
      List<Die> cpy = new ArrayList<>(dice);
      reset();
      refreshDice(cpy);
      nad.dynamicsWorld.getConstraintSolver().reset();
      if (!dice.containsAll(toRoll)) {
         dice.removeAll(toRoll);
         dice.addAll(toRoll);
         String err = "dice roll err";
         TannLog.error(err);
         DungeonScreen.get().showDialog(err);
         Sounds.playSound(Sounds.bats);
      }

      simulating = true;
      List<Matrix4> pos = new ArrayList<>();

      for (Die die : toRoll) {
         die.addToScreen();
         die.physical.update();
         pos.add(die.physical.transform.cpy());
      }

      Random r = Tann.makeStdRandom(rollSeed);

      for (EntDie entDie : toRoll) {
         entDie.roll(firstRoll, r);
      }

      boolean predictionFailed = false;
      int ticksLeft = 1000;
      boolean allLanded = false;

      while (!allLanded) {
         singleTick(0.033333335F);
         allLanded = true;

         for (Die die : toRoll) {
            allLanded &= die.getState() != Die.DieState.Rolling;
         }

         if (--ticksLeft <= 0) {
            TannLog.error("Failed to roll: " + toRoll);
            predictionFailed = true;
            break;
         }
      }

      List<Integer> rolledSides = new ArrayList<>();

      for (EntDie entDie : toRoll) {
         rolledSides.add(predictionFailed ? Tann.randomInt(6) : entDie.getSideIndex());
      }

      for (int i = 0; i < toRoll.size(); i++) {
         Die d = toRoll.get(i);
         d.physical.transform.set(pos.get(i));
         d.physical.body.setAngularVelocity(new Vector3());
         d.physical.body.setLinearVelocity(new Vector3());
         d.physical.body.getMotionState();
         d.physical.body.setWorldTransform(d.physical.transform);
         d.physical.calculateTransforms();
         d.addToScreen();
      }

      Tann.assertEquals("should have correct rtn amt", toRoll.size(), rolledSides.size());
      nad.dynamicsWorld.getConstraintSolver().reset();
      simulating = false;
      return rolledSides;
   }

   public static boolean isSimulating() {
      return simulating;
   }

   private static int countPlayerDice() {
      DungeonScreen ds = DungeonScreen.get();
      return ds == null ? 5 : ds.hero.getEntities().size();
   }

   public static boolean forcePrerandomiseDueToBounds() {
      boolean b = getPlayHeight() < 0.3F || getPlayWidth() < 0.2F || OptionUtils.shouldForceDiceRand() || countPlayerDice() > 10;
      if (b) {
         TannLog.error("Bad roll bounds " + com.tann.dice.Main.resolutionString());
      }

      return b;
   }

   public static boolean okSizeForLiningUp() {
      return getPlayWidth() > 0.3F && getPlayWidth() > 0.2F && dice.size() < 15;
   }

   public static void removeBody(btRigidBody body) {
      nad.dynamicsWorld.removeRigidBody(body);
      nad.dynamicsWorld.removeCollisionObject(body);
   }

   public static void addDiceBody(btRigidBody body) {
      nad.dynamicsWorld.addRigidBody(body, 512, -1);
   }

   public static int getPanelSlideAmount() {
      return (int)(13.0F * (com.tann.dice.Main.isPortrait() ? 0.6F : 1.0F));
   }
}
