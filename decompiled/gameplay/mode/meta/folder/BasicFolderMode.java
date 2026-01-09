package com.tann.dice.gameplay.mode.meta.folder;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.List;

public class BasicFolderMode extends FolderMode {
   final FolderType ft;

   public BasicFolderMode(FolderType ft) {
      super(MN(ft), ft.col);
      this.ft = ft;
   }

   private static String MN(FolderType ft) {
      String tag = "/";
      return ft.name() + tag;
   }

   public static BasicFolderMode get(FolderType ft) {
      return (BasicFolderMode)Mode.getModeFromString(MN(ft));
   }

   @Override
   public String[] getDescriptionLines() {
      String[] override = this.ft.getSpecificDesc();
      return override != null ? override : new String[]{TextWriter.getTag(this.col) + this.ft.name() + "[cu] modes folder"};
   }

   public static List<BasicFolderMode> makeAll() {
      List<BasicFolderMode> result = new ArrayList<>();

      for (FolderType ft : FolderType.values()) {
         if (ft != FolderType.unfinished) {
            result.add(new BasicFolderMode(ft));
         }
      }

      return result;
   }

   @Override
   public List<Mode> getContainedModes() {
      List<Mode> result = new ArrayList<>();

      for (Mode m : Mode.getAllModes()) {
         if (m.getFolderType() == this.ft) {
            result.add(m);
         }
      }

      return result;
   }

   @Override
   public boolean isDebug() {
      return this.ft == FolderType.debug;
   }

   @Override
   public TextureRegion getBackground() {
      return getBackgroundFromFolderType(this.ft);
   }
}
