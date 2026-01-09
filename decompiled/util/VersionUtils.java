package com.tann.dice.util;

import com.badlogic.gdx.graphics.Color;

public class VersionUtils {
   public static final String version = "3.2.13";
   public static final VersionUtils.VersionType VERSION_TYPE = VersionUtils.VersionType.release;
   public static final String PASTE_VERSION = "3.2.13".replaceAll("\\.", "") + VERSION_TYPE.name().charAt(0);
   public static final String versionName = "v3.2.13" + (VERSION_TYPE != null ? VERSION_TYPE.versionString() : "");
   public static Color VERSION_COL;

   public static enum VersionType {
      internal("please do not share"),
      alpha("please do not share"),
      beta("enjoy the bugs"),
      release("hmm");

      final String msg;

      private VersionType(String msg) {
         this.msg = msg;
      }

      public String colName() {
         return this.getColTag() + this.name() + "[cu]";
      }

      private String getColTag() {
         switch (this) {
            case internal:
               return "[pink]";
            case alpha:
               return "[orange]";
            case beta:
               return "[yellow]";
            default:
               return "[grey]";
         }
      }

      public String versionString() {
         return this == release ? "" : "-" + this.colName();
      }
   }
}
