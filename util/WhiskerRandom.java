package com.tann.dice.util;

import java.util.Random;

public class WhiskerRandom extends Random {
   protected long stateA;
   protected long stateB;
   protected long stateC;
   protected long stateD;

   public WhiskerRandom(long seed) {
      this.setSeed(seed);
   }

   public WhiskerRandom(long stateA, long stateB, long stateC, long stateD) {
      this.stateA = stateA;
      this.stateB = stateB;
      this.stateC = stateC;
      this.stateD = stateD;
   }

   @Override
   public void setSeed(long seed) {
      this.stateA = seed ^ -4126379630918253789L;
      this.stateB = seed ^ 4126379630918253788L;
      seed ^= seed >>> 32;
      seed *= -4710160504952957587L;
      seed ^= seed >>> 29;
      seed *= -4710160504952957587L;
      seed ^= seed >>> 32;
      seed *= -4710160504952957587L;
      seed ^= seed >>> 29;
      this.stateC = ~seed;
      this.stateD = seed;
   }

   @Override
   public long nextLong() {
      long fa = this.stateA;
      long fb = this.stateB;
      long fc = this.stateC;
      long fd = this.stateD;
      this.stateA = fd * -1065810590584100411L;
      this.stateB = fa << 44 | fa >>> 20;
      this.stateC = fb + -7046029254386353131L;
      return this.stateD = fa ^ fc;
   }

   @Override
   public int next(int bits) {
      long fa = this.stateA;
      long fb = this.stateB;
      long fc = this.stateC;
      long fd = this.stateD;
      this.stateA = fd * -1065810590584100411L;
      this.stateB = fa << 44 | fa >>> 20;
      this.stateC = fb + -7046029254386353131L;
      return (int)(this.stateD = fa ^ fc) >>> 32 - bits;
   }

   public WhiskerRandom copy() {
      return new WhiskerRandom(this.stateA, this.stateB, this.stateC, this.stateD);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         WhiskerRandom that = (WhiskerRandom)o;
         return this.stateA == that.stateA && this.stateB == that.stateB && this.stateC == that.stateC && this.stateD == that.stateD;
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return "WhiskerRandom{stateA=" + this.stateA + "L, stateB=" + this.stateB + "L, stateC=" + this.stateC + "L, stateD=" + this.stateD + "L}";
   }
}
