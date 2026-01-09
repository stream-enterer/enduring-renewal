package com.tann.dice.util;

import com.tann.dice.util.tp.TP;
import java.util.ArrayList;
import java.util.List;

public class TimerUtil {
   static List<TP<Float, Runnable>> rs = new ArrayList<>();

   public static void clearStatics() {
      rs = new ArrayList<>();
   }

   public static void tick(float delta) {
      if (!rs.isEmpty()) {
         com.tann.dice.Main.requestRendering();
      }

      for (int i = rs.size() - 1; i >= 0; i--) {
         TP<Float, Runnable> rr = rs.get(i);
         rr.a = rr.a - delta;
         if (rr.a <= 0.0F) {
            rr.b.run();
            rs.remove(rr);
         }
      }
   }

   public static void delay(float delay, Runnable runnable) {
      if (delay == 0.0F) {
         runnable.run();
      } else {
         rs.add(new TP<>(delay, runnable));
      }
   }
}
