package com.tann.dice.util.bsRandom;

public class BasicSupplier<T> implements Supplier<T> {
   final T t;

   public BasicSupplier(T t) {
      this.t = t;
   }

   @Override
   public T supply() {
      return this.t;
   }
}
