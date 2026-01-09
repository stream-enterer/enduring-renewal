package com.tann.dice.util.ui.resolver;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.content.gen.pipe.mod.PipeMod;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.util.Colours;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class ModifierResolver extends Resolver<Modifier> {
   public ModifierResolver() {
      super(new Comparator<Modifier>() {
         public int compare(Modifier o1, Modifier o2) {
            return o1.getName().compareTo(o2.getName());
         }
      });
   }

   protected Modifier byName(String text) {
      Modifier m = ModifierLib.byName(text);
      if (m != null && !m.isMissingno()) {
         return m;
      } else {
         Modifier of = this.onFail(text);
         return of != null ? of : null;
      }
   }

   protected Modifier byCache(String text) {
      return PipeMod.byCache(text);
   }

   @Override
   protected List<Modifier> search(String text) {
      Pipe.setupChecks();
      List<Modifier> mods = ModifierLib.search(text);
      Pipe.disableChecks();
      if (mods != null && !mods.isEmpty()) {
         return mods;
      } else {
         Pipe.setupChecks();
         List<Modifier> fs = this.failSearch(text);
         Pipe.disableChecks();
         return fs != null && !fs.isEmpty() ? fs : makeBlank();
      }
   }

   protected List<Modifier> failSearch(String text) {
      return makeBlank();
   }

   protected static List<Modifier> makeBlank() {
      return new ArrayList<>();
   }

   @Override
   protected String getTypeName() {
      return "a modifier";
   }

   @Override
   protected Color getCol() {
      return Colours.green;
   }

   public Modifier onFail(String failString) {
      return null;
   }
}
