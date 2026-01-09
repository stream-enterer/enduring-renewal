package com.tann.dice.util;

import com.badlogic.gdx.Gdx;
import java.util.ArrayList;
import java.util.List;

public class TannLog {
   public static final List<String> logs = new ArrayList<>();

   public static void error(String log) {
      log(log, TannLog.Severity.error);
   }

   public static void log(String log) {
      log(log, TannLog.Severity.info);
   }

   public static void log(String log, TannLog.Severity severity) {
      if (!logs.contains(log)) {
         switch (severity) {
            case info:
               Gdx.app.log("SliceDice", log);
               break;
            case error:
               Gdx.app.error("SliceDice", log);
         }

         if (logs.size() > 9) {
            logs.remove(logs.size() - 1);
         }

         logs.add(log);
      }
   }

   public static String newlinedLogs() {
      String result = "";

      for (String s : logs) {
         result = result + s + "\n";
      }

      return result;
   }

   public static void error(Exception e) {
      error(e, "");
   }

   public static void error(Exception e, String ctx) {
      if (e != null) {
         String errString = e.getClass().getSimpleName() + ":" + e.getMessage() + " " + ctx;
         StackTraceElement[] st = e.getStackTrace();
         if (st != null && st.length > 2) {
            errString = errString + " " + e.getStackTrace()[0] + " " + e.getStackTrace()[1];
         }

         error(errString);
      }
   }

   public static enum Severity {
      info,
      error;
   }
}
