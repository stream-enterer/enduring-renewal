package com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.GlobalDescribeOnly;
import com.tann.dice.gameplay.trigger.global.linked.GlobalMulti;
import com.tann.dice.gameplay.trigger.global.weird.GlobalRename;
import java.util.ArrayList;
import java.util.List;

public class DataSourceModifier extends DataSource<Modifier> {
   public DataSourceModifier() {
      super(PipeRegexNamed.MODIFIER);
   }

   public Modifier combine(Modifier modifier, AtlasRegion ar, String realName) {
      return null;
   }

   public Modifier makeT(String name) {
      return ModifierLib.byName(name);
   }

   public Modifier exampleBase() {
      return ModifierLib.random();
   }

   public AtlasRegion getImage(Modifier modifier) {
      return null;
   }

   public Modifier upscale(Modifier modifier, int multiplier) {
      return make(modifier, multiplier);
   }

   public Modifier rename(Modifier modifier, String rename, String realName) {
      List<Global> globals = new ArrayList<>(modifier.getGlobals());
      globals.add(new GlobalRename(rename));
      return new Modifier(modifier.getFloatTier(), realName, globals);
   }

   public Modifier document(Modifier modifier, String documentation, String realName) {
      List<Global> globals = new ArrayList<>(modifier.getGlobals());
      globals.add(0, new GlobalDescribeOnly(documentation));
      return new Modifier(modifier.getTier(), realName, globals);
   }

   public Modifier retier(Modifier modifier, int newTier, String realName) {
      return new Modifier(newTier, realName, modifier.getGlobals());
   }

   public Modifier makeIndexed(long val) {
      List<Modifier> cpy = ModifierLib.getAll();
      return val >= 0L && val < cpy.size() ? cpy.get((int)val) : null;
   }

   public Modifier renameUnderlying(Modifier modifier, String rename) {
      return new Modifier(modifier.getFloatTier(), rename, modifier.getGlobals());
   }

   public Modifier withItem(Modifier modifier, Item i, String s) {
      return null;
   }

   public static Modifier make(Modifier mod, int mult) {
      if (!mod.isMultiplable(true)) {
         return null;
      } else {
         Global g = mod.getSingleGlobalOrNull();
         return g == null ? null : new Modifier(mod.getFloatTier() * mult, "x" + mult + "." + mod.getName(), new GlobalMulti(g, mult));
      }
   }
}
