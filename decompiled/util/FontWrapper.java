package com.tann.dice.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontWrapper {
   private final float scale;
   private final TannFont tannFont;
   private final BitmapFont regular;
   private final BitmapFont bold;
   private final BitmapFont italic;
   private final GlyphLayout glyphLayout = new GlyphLayout();
   private static FontWrapper TANN_FONT;
   private static Map<Integer, FontWrapper> LOADED_FONTS = new HashMap<>();

   private FontWrapper(float scale, TannFont tannFont, BitmapFont regular, BitmapFont bold, BitmapFont italic) {
      this.scale = scale;
      this.tannFont = tannFont;
      this.regular = regular;
      this.bold = bold;
      this.italic = italic;
      if (this.tannFont == null) {
         this.regular.setUseIntegerPositions(false);
         this.bold.setUseIntegerPositions(false);
         this.italic.setUseIntegerPositions(false);
      }
   }

   public static void clearAllStatics() {
      TANN_FONT = null;
      LOADED_FONTS.clear();
   }

   public static void disposeAll() {
      for (FontWrapper font : LOADED_FONTS.values()) {
         font.dispose();
      }

      LOADED_FONTS.clear();
   }

   public static FontWrapper getFont() {
      int fontOption = OptionLib.FONT.c();
      if (fontOption == 0) {
         return getTannFont();
      } else if (com.tann.dice.Main.self().translator.getLanguageCode().equals("ru")) {
         return getTannFont();
      } else if (LOADED_FONTS.containsKey(fontOption)) {
         return LOADED_FONTS.get(fontOption);
      } else {
         FontWrapper font = loadHDFont(fontOption);
         if (font != null) {
            LOADED_FONTS.put(fontOption, font);
            return font;
         } else {
            return getTannFont();
         }
      }
   }

   private static FontWrapper loadHDFont(int fontOption) {
      switch (fontOption) {
         case 1:
            return fromBitmapFonts(0.29F, "tuffy");
         case 2:
            return fromBitmapFonts(0.24F, "gargle");
         case 3:
            return fromBitmapFonts(0.23F, "karmasuture");
         case 4:
            return fromBitmapFonts(0.23F, "mesmerize");
         default:
            return null;
      }
   }

   public static FontWrapper getTannFont() {
      if (TANN_FONT == null) {
         TANN_FONT = fromTannFont(1.0F, TannFont.font);
      }

      return TANN_FONT;
   }

   private void dispose() {
      if (this.tannFont == null) {
         this.regular.dispose();
         this.bold.dispose();
         this.italic.dispose();
      }
   }

   private static FontWrapper fromTannFont(float scale, TannFont tannFont) {
      return new FontWrapper(scale, tannFont, null, null, null);
   }

   private static FontWrapper fromBitmapFonts(float scale, String fontName) {
      FileHandle baseFile = Gdx.files.internal("fonts/" + fontName + ".fnt");
      FileHandle boldFile = Gdx.files.internal("fonts/" + fontName + "_bold.fnt");
      FileHandle italicFile = Gdx.files.internal("fonts/" + fontName + "_italic.fnt");
      return fromBitmapFonts(
         scale, new BitmapFont(baseFile), new BitmapFont(boldFile.exists() ? boldFile : baseFile), new BitmapFont(italicFile.exists() ? italicFile : baseFile)
      );
   }

   private static FontWrapper fromBitmapFonts(float scale, BitmapFont regular, BitmapFont bold, BitmapFont italic) {
      return new FontWrapper(scale, null, regular, bold, italic);
   }

   public float getScale() {
      return this.scale;
   }

   public boolean isTannFont() {
      return this.tannFont != null;
   }

   public boolean isHDFont() {
      return this.tannFont == null || this.scale != 1.0F;
   }

   public void drawString(Batch batch, String text, float x, float y) {
      this.drawString(batch, text, x, y, false, false, false, false, false, false, false);
   }

   public void drawString(
      Batch batch, String text, float x, float y, boolean fixedWidth, boolean wiggle, boolean sin, boolean bold, boolean italics, boolean first, boolean glitch
   ) {
      if (this.tannFont != null) {
         this.tannFont.drawString(batch, text, x, y, fixedWidth, wiggle, sin, bold, italics, first, glitch, this.scale);
      } else {
         BitmapFont font = this.regular;
         if (bold) {
            font = this.bold;
         } else if (italics) {
            font = this.italic;
         }

         font.getData().setScale(this.scale);
         font.setColor(batch.getColor());
         font.draw(batch, text, x, y + font.getLineHeight());
      }
   }

   public float getSpaceWidth(boolean italics) {
      if (!this.isHDFont()) {
         return this.tannFont.getSpaceWidth(italics);
      } else {
         return this.tannFont != null ? this.tannFont.getSpaceWidth(italics) * this.scale : 8.0F * this.scale;
      }
   }

   public int getHeight() {
      return this.tannFont != null ? (int)(this.scale * this.tannFont.getHeight()) : (int)this.regular.getLineHeight();
   }

   public float getInterCharacterSpace() {
      if (!this.isHDFont()) {
         return 1.0F;
      } else {
         return this.tannFont != null ? this.scale : 1.0F * this.scale;
      }
   }

   public int getLineHeight() {
      return this.tannFont != null ? (int)(this.scale * this.tannFont.getLineHeight()) : (int)(this.regular.getLineHeight() + 1.0F);
   }

   public float getKerning(char pre, char post) {
      if (this.isTannFont()) {
         List<Character> map = this.tannFont.kerningPairs.get(pre);
         if (map == null) {
            return 0.0F;
         } else {
            return map.contains(post) ? -1.0F * this.scale : 0.0F;
         }
      } else {
         Glyph glyph = this.regular.getData().getGlyph(pre);
         return glyph == null ? 0.0F : glyph.getKerning(post) * this.scale;
      }
   }

   public boolean hasChar(char c) {
      if (this.tannFont != null) {
         return this.tannFont.hasChar(c);
      } else {
         Glyph glyph = this.regular.getData().getGlyph(c);
         return glyph != null;
      }
   }

   public float getWidth(String text) {
      return this.getWidth(text, false, false, false);
   }

   public float getWidth(String text, boolean bold, boolean italics, boolean first) {
      if (this.tannFont != null) {
         return this.scale * this.tannFont.getWidth(text, bold, italics, first);
      } else {
         BitmapFont font = this.regular;
         if (bold) {
            font = this.bold;
         } else if (italics) {
            font = this.italic;
         }

         font.getData().setScale(this.scale);
         this.glyphLayout.setText(font, text);
         return this.glyphLayout.width;
      }
   }
}
