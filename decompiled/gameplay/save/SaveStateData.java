package com.tann.dice.gameplay.save;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.util.VersionUtils;
import java.util.ArrayList;
import java.util.List;

public class SaveStateData {
   String v;
   DungeonContextData d;
   List<String> c = new ArrayList<>();
   String s;
   List<String> p = new ArrayList<>();
   public static String deadHeroTag = "D";

   public SaveStateData() {
   }

   public SaveStateData(DungeonContextData dungeonContextData, List<String> commandData, String sides, List<String> phases) {
      this.v = OptionLib.TINY_PASTE.c() ? null : VersionUtils.PASTE_VERSION;
      this.d = dungeonContextData;
      this.c = commandData;
      this.s = sides;
      this.p = phases;
   }

   public SaveState toState() {
      return new SaveState(this.makeContext(), this.c, this.s, this.p, this.v);
   }

   public DungeonContext makeContext() {
      return DungeonContext.fromData(this.d);
   }

   public SaveStateData trimContextDataForReport() {
      this.d.clearForReport();
      return this;
   }

   public List<String> getP() {
      return this.p;
   }
}
