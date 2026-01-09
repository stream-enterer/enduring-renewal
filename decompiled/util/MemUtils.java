package com.tann.dice.util;

import com.badlogic.gdx.utils.Disposable;

public class MemUtils {
   public static void disp(Disposable d) {
      if (d != null) {
         d.dispose();
      }
   }

   public static void disp(Disposable... disposables) {
      for (Disposable disposable : disposables) {
         disp(disposable);
      }
   }
}
