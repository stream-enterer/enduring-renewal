package com.tann.dice.util.tp;

public class TTP<T1, T2, T3> {
   public final T1 a;
   public final T2 b;
   public final T3 c;

   public TTP(T1 a, T2 b, T3 c) {
      this.a = a;
      this.b = b;
      this.c = c;
   }

   @Override
   public String toString() {
      return this.a + ":" + this.b + ":" + this.c;
   }

   public boolean sameAs(TTP<T1, T2, T3> existing) {
      return this.a == existing.a && this.b == existing.b && this.c == existing.c;
   }
}
