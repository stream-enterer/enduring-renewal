package com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.Item;
import javax.annotation.Nonnull;

public abstract class DataSource<T> {
   public final PRNPart srcPart;

   DataSource(PRNPart srcPart) {
      this.srcPart = srcPart;
   }

   public abstract T combine(T var1, AtlasRegion var2, String var3);

   @Nonnull
   public abstract T makeT(String var1);

   public abstract T exampleBase();

   public abstract AtlasRegion getImage(T var1);

   public static AtlasRegion wrap(TextureRegion image) {
      return new AtlasRegion(image);
   }

   public PRNPart getMRTPart() {
      if (this.srcPart == PipeRegexNamed.ITEM) {
         return this.getTag("item");
      } else if (this.srcPart == PipeRegexNamed.HERO) {
         return this.getTag("hero");
      } else {
         return this.srcPart == PipeRegexNamed.MODIFIER ? this.getTag("mod") : this.getTag("unk");
      }
   }

   private PRNPart getTag(String base) {
      return new PRNPref("rd" + base);
   }

   public abstract T upscale(T var1, int var2);

   public abstract T rename(T var1, String var2, String var3);

   public abstract T document(T var1, String var2, String var3);

   public abstract T retier(T var1, int var2, String var3);

   public abstract T makeIndexed(long var1);

   public abstract T renameUnderlying(T var1, String var2);

   public abstract T withItem(T var1, Item var2, String var3);
}
