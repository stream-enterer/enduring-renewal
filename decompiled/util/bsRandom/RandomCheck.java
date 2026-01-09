package com.tann.dice.util.bsRandom;

import java.util.List;

public abstract class RandomCheck {
   private static final int DEFAULT_MAX_ATTEMPTS = 200;

   public static <T> T checkedRandom(List<T> list, Checker<T> checker, T def) {
      return checkedRandom(new RandomCheck.RandomListSupplier<>(list), checker, def);
   }

   public static <T> T checkedRandom(Supplier<T> supplier, Checker<T> checker, T def) {
      return checkedRandom(supplier, checker, 200, def);
   }

   public static <T> T checkedRandomNull(Supplier<T> supplier) {
      return checkedRandom(supplier, nullChecker(), 200, null);
   }

   private static <T> Checker<T> nullChecker() {
      return new Checker<T>() {
         @Override
         public boolean check(T t) {
            return t != null;
         }
      };
   }

   public static <T> T checkedRandom(Supplier<T> supplier, Checker<T> checker, int maxAttempts, T def) {
      for (int i = 0; i < maxAttempts; i++) {
         T t = supplier.supply();
         if (checker.check(t)) {
            return t;
         }
      }

      return def;
   }

   private static class RandomListSupplier<T> implements Supplier {
      final List<T> t;

      public RandomListSupplier(List<T> t) {
         this.t = t;
      }

      @Override
      public Object supply() {
         return this.t.get((int)(Math.random() * this.t.size()));
      }
   }
}
