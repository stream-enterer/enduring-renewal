package com.tann.dice.statics.sound.music;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.TextWriter;

public enum MusicFormat {
   MP3(Colours.orange),
   WAV(Colours.red),
   OGG(Colours.blue);

   public final Color c = JukeboxUtils.SOUND_COL;

   private MusicFormat(Color c) {
   }

   public static String getNiceName(String filename) {
      String[] fol = filename.split("/");
      String niceName = fol[fol.length - 1].split("\\.")[0];
      niceName = niceName.replace("_", "'");

      for (MusicFormat value : values()) {
         if (filename.toLowerCase().endsWith(value.name().toLowerCase())) {
            niceName = TextWriter.getTag(value.c) + niceName;
            break;
         }
      }

      return niceName;
   }
}
