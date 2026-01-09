package com.tann.dice.util.tp;

public class TP<T1, T2> {
   public T1 a;
   public T2 b;

   public TP(T1 a, T2 b) {
      this.a = a;
      this.b = b;
   }

   @Override
   public String toString() {
      return this.a + ":" + this.b;
   }

   public boolean sameAs(TP<T1, T2> existing) {
      return this.a == existing.a && this.b == existing.b;
   }
}
