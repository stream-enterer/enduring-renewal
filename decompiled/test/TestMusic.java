package com.tann.dice.test;

import com.tann.dice.statics.sound.music.MusicData;
import com.tann.dice.statics.sound.music.MusicDebugUtils;
import com.tann.dice.statics.sound.music.MusicManager;
import com.tann.dice.test.util.SkipNonTann;
import com.tann.dice.test.util.Test;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestMusic {
   @Test
   @SkipNonTann
   public static void noMusicDiscrep() {
      List<String> paths = MusicDebugUtils.getMusicPaths();
      List<MusicData> data = MusicManager.getDataList();
      List<String> dataPaths = new ArrayList<>();

      for (MusicData datum : data) {
         dataPaths.add(datum.path);
      }

      Collections.sort(paths);
      Collections.sort(dataPaths);
      List<String> cpy = new ArrayList<>(paths);
      cpy.removeAll(dataPaths);
      List<String> cpy2 = new ArrayList<>(dataPaths);
      cpy2.removeAll(paths);
      cpy.addAll(cpy2);
      Tann.assertTrue("Should be no bads " + cpy, cpy.isEmpty());
      Tann.assertEquals("Should be no music discrep", paths, dataPaths);
   }
}
