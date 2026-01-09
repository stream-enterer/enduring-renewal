package com.tann.dice.gameplay.save;

public class LoadCrashException extends RuntimeException {
   public LoadCrashException(Exception e) {
      super(e);
   }
}
