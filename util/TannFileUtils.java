package com.tann.dice.util;

import java.io.File;

public class TannFileUtils {
   public static void clearPackCache() {
      File folder = new File("misc");
      if (!folder.isDirectory()) {
         throw new RuntimeException("?? help");
      } else {
         for (File f : folder.listFiles()) {
            if (f.getName().contains("hash")) {
               f.delete();
            }
         }
      }
   }
}
