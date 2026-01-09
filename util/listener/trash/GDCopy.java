package com.tann.dice.util.listener.trash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class GDCopy extends InputAdapter {
   final GDCopy.GestureListener listener;
   private float tapRectangleWidth;
   private float tapRectangleHeight;
   private long tapCountInterval;
   private float longPressSeconds;
   private long maxFlingDelay;
   private boolean inTapRectangle;
   private int tapCount;
   private long lastTapTime;
   private float lastTapX;
   private float lastTapY;
   private int lastTapButton;
   private int lastTapPointer;
   boolean longPressFired;
   private final GDCopy.VelocityTracker tracker = new GDCopy.VelocityTracker();
   private float tapRectangleCenterX;
   private float tapRectangleCenterY;
   private long touchDownTime;
   Vector2 pointer1 = new Vector2();
   private final Vector2 pointer2 = new Vector2();
   private final Vector2 initialPointer1 = new Vector2();
   private final Vector2 initialPointer2 = new Vector2();
   private final Task longPressTask = new Task() {
      public void run() {
         if (!GDCopy.this.longPressFired) {
            GDCopy.this.longPressFired = GDCopy.this.listener.longPress(GDCopy.this.pointer1.x, GDCopy.this.pointer1.y);
         }
      }
   };

   public GDCopy(GDCopy.GestureListener listener) {
      this(20.0F, 0.4F, 1.1F, 2.1474836E9F, listener);
   }

   public GDCopy(float halfTapSquareSize, float tapCountInterval, float longPressDuration, float maxFlingDelay, GDCopy.GestureListener listener) {
      this(halfTapSquareSize, halfTapSquareSize, tapCountInterval, longPressDuration, maxFlingDelay, listener);
   }

   public GDCopy(
      float halfTapRectangleWidth,
      float halfTapRectangleHeight,
      float tapCountInterval,
      float longPressDuration,
      float maxFlingDelay,
      GDCopy.GestureListener listener
   ) {
      if (listener == null) {
         throw new IllegalArgumentException("listener cannot be null.");
      } else {
         this.tapRectangleWidth = halfTapRectangleWidth;
         this.tapRectangleHeight = halfTapRectangleHeight;
         this.tapCountInterval = (long)(tapCountInterval * 1.0E9F);
         this.longPressSeconds = longPressDuration;
         this.maxFlingDelay = (long)(maxFlingDelay * 1.0E9F);
         this.listener = listener;
      }
   }

   public boolean touchDown(int x, int y, int pointer, int button) {
      return this.touchDown((float)x, (float)y, pointer, button);
   }

   public boolean touchDown(float x, float y, int pointer, int button) {
      this.longPressTask.cancel();
      this.pointer1.set(x, y);
      this.touchDownTime = Gdx.input.getCurrentEventTime();
      this.tracker.start(x, y, this.touchDownTime);
      this.inTapRectangle = true;
      this.longPressFired = false;
      this.tapRectangleCenterX = x;
      this.tapRectangleCenterY = y;
      if (!this.longPressTask.isScheduled()) {
         Timer.schedule(this.longPressTask, this.longPressSeconds);
      }

      return this.listener.touchDown(x, y, pointer, button);
   }

   public boolean touchDragged(int x, int y, int pointer) {
      return this.touchDragged((float)x, (float)y, pointer);
   }

   public boolean touchDragged(float x, float y, int pointer) {
      if (pointer > 1) {
         return false;
      } else if (this.longPressFired) {
         return false;
      } else {
         if (pointer == 0) {
            this.pointer1.set(x, y);
         } else {
            this.pointer2.set(x, y);
         }

         this.tracker.update(x, y, Gdx.input.getCurrentEventTime());
         if (this.inTapRectangle && !this.isWithinTapRectangle(x, y, this.tapRectangleCenterX, this.tapRectangleCenterY)) {
            this.longPressTask.cancel();
            this.inTapRectangle = false;
         }

         return !this.inTapRectangle ? this.listener.pan(x, y, this.tracker.deltaX, this.tracker.deltaY) : false;
      }
   }

   public boolean touchUp(int x, int y, int pointer, int button) {
      return this.touchUp((float)x, (float)y, pointer, button);
   }

   public boolean touchUp(float x, float y, int pointer, int button) {
      if (this.inTapRectangle && !this.isWithinTapRectangle(x, y, this.tapRectangleCenterX, this.tapRectangleCenterY)) {
         this.inTapRectangle = false;
      }

      this.longPressTask.cancel();
      if (!this.inTapRectangle) {
      }

      if (this.lastTapButton != button
         || this.lastTapPointer != pointer
         || TimeUtils.nanoTime() - this.lastTapTime > this.tapCountInterval
         || !this.isWithinTapRectangle(x, y, this.lastTapX, this.lastTapY)) {
         this.tapCount = 0;
      }

      this.tapCount++;
      this.lastTapTime = TimeUtils.nanoTime();
      this.lastTapX = x;
      this.lastTapY = y;
      this.lastTapButton = button;
      this.lastTapPointer = pointer;
      this.touchDownTime = 0L;
      return this.listener.tap(x, y, this.tapCount, button);
   }

   public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
      this.cancel();
      return super.touchCancelled(screenX, screenY, pointer, button);
   }

   public void cancel() {
      this.longPressTask.cancel();
      this.longPressFired = true;
   }

   public boolean isLongPressed() {
      return this.isLongPressed(this.longPressSeconds);
   }

   public boolean isLongPressed(float duration) {
      return this.touchDownTime == 0L ? false : TimeUtils.nanoTime() - this.touchDownTime > (long)(duration * 1.0E9F);
   }

   public void reset() {
      this.touchDownTime = 0L;
      this.inTapRectangle = false;
      this.tracker.lastTime = 0L;
   }

   private boolean isWithinTapRectangle(float x, float y, float centerX, float centerY) {
      return Math.abs(x - centerX) < this.tapRectangleWidth && Math.abs(y - centerY) < this.tapRectangleHeight;
   }

   public void invalidateTapSquare() {
      this.inTapRectangle = false;
   }

   public void setTapSquareSize(float halfTapSquareSize) {
      this.setTapRectangleSize(halfTapSquareSize, halfTapSquareSize);
   }

   public void setTapRectangleSize(float halfTapRectangleWidth, float halfTapRectangleHeight) {
      this.tapRectangleWidth = halfTapRectangleWidth;
      this.tapRectangleHeight = halfTapRectangleHeight;
   }

   public void setTapCountInterval(float tapCountInterval) {
      this.tapCountInterval = (long)(tapCountInterval * 1.0E9F);
   }

   public void setLongPressSeconds(float longPressSeconds) {
      this.longPressSeconds = longPressSeconds;
   }

   public void setMaxFlingDelay(long maxFlingDelay) {
      this.maxFlingDelay = maxFlingDelay;
   }

   public static class GestureAdapter implements GDCopy.GestureListener {
      @Override
      public boolean touchDown(float x, float y, int pointer, int button) {
         return false;
      }

      @Override
      public boolean tap(float x, float y, int count, int button) {
         return false;
      }

      @Override
      public boolean longPress(float x, float y) {
         return false;
      }

      @Override
      public boolean fling(float velocityX, float velocityY, int button) {
         return false;
      }

      @Override
      public boolean pan(float x, float y, float deltaX, float deltaY) {
         return false;
      }

      @Override
      public boolean panStop(float x, float y, int pointer, int button) {
         return false;
      }

      @Override
      public boolean zoom(float initialDistance, float distance) {
         return false;
      }

      @Override
      public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
         return false;
      }

      @Override
      public void pinchStop() {
      }
   }

   public interface GestureListener {
      boolean touchDown(float var1, float var2, int var3, int var4);

      boolean tap(float var1, float var2, int var3, int var4);

      boolean longPress(float var1, float var2);

      boolean fling(float var1, float var2, int var3);

      boolean pan(float var1, float var2, float var3, float var4);

      boolean panStop(float var1, float var2, int var3, int var4);

      boolean zoom(float var1, float var2);

      boolean pinch(Vector2 var1, Vector2 var2, Vector2 var3, Vector2 var4);

      void pinchStop();
   }

   static class VelocityTracker {
      int sampleSize = 10;
      float lastX;
      float lastY;
      float deltaX;
      float deltaY;
      long lastTime;
      int numSamples;
      float[] meanX = new float[this.sampleSize];
      float[] meanY = new float[this.sampleSize];
      long[] meanTime = new long[this.sampleSize];

      public void start(float x, float y, long timeStamp) {
         this.lastX = x;
         this.lastY = y;
         this.deltaX = 0.0F;
         this.deltaY = 0.0F;
         this.numSamples = 0;

         for (int i = 0; i < this.sampleSize; i++) {
            this.meanX[i] = 0.0F;
            this.meanY[i] = 0.0F;
            this.meanTime[i] = 0L;
         }

         this.lastTime = timeStamp;
      }

      public void update(float x, float y, long currTime) {
         this.deltaX = x - this.lastX;
         this.deltaY = y - this.lastY;
         this.lastX = x;
         this.lastY = y;
         long deltaTime = currTime - this.lastTime;
         this.lastTime = currTime;
         int index = this.numSamples % this.sampleSize;
         this.meanX[index] = this.deltaX;
         this.meanY[index] = this.deltaY;
         this.meanTime[index] = deltaTime;
         this.numSamples++;
      }

      public float getVelocityX() {
         float meanX = this.getAverage(this.meanX, this.numSamples);
         float meanTime = (float)this.getAverage(this.meanTime, this.numSamples) / 1.0E9F;
         return meanTime == 0.0F ? 0.0F : meanX / meanTime;
      }

      public float getVelocityY() {
         float meanY = this.getAverage(this.meanY, this.numSamples);
         float meanTime = (float)this.getAverage(this.meanTime, this.numSamples) / 1.0E9F;
         return meanTime == 0.0F ? 0.0F : meanY / meanTime;
      }

      private float getAverage(float[] values, int numSamples) {
         numSamples = Math.min(this.sampleSize, numSamples);
         float sum = 0.0F;

         for (int i = 0; i < numSamples; i++) {
            sum += values[i];
         }

         return sum / numSamples;
      }

      private long getAverage(long[] values, int numSamples) {
         numSamples = Math.min(this.sampleSize, numSamples);
         long sum = 0L;

         for (int i = 0; i < numSamples; i++) {
            sum += values[i];
         }

         return numSamples == 0 ? 0L : sum / numSamples;
      }

      private float getSum(float[] values, int numSamples) {
         numSamples = Math.min(this.sampleSize, numSamples);
         float sum = 0.0F;

         for (int i = 0; i < numSamples; i++) {
            sum += values[i];
         }

         return numSamples == 0 ? 0.0F : sum;
      }
   }
}
