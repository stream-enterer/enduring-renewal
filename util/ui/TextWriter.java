package com.tann.dice.util.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array.ArrayIterator;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.item.facade.FacadeUtils;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.FontWrapper;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Rectactor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannFont;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.TannStage;
import com.tann.dice.util.image.Img64;
import com.tann.dice.util.tp.TP;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TextWriter extends Group {
   String[] split;
   public String text;
   private static final int LINE_GAP = 1;
   boolean justAddedSpace;
   float x;
   int y;
   int numLines = 0;
   int lineHeight = 0;
   Color borderCol;
   int borderSize;
   TextureRegion image;
   private final boolean overrideMaxWidth;
   private final boolean tannFontOverride;
   private static Map<String, TextureRegion> textureMap = new HashMap<>();
   private static Map<String, Color> colorMap = new HashMap<>();
   int max = 0;
   static final float maxRatio = 0.5F;
   int guessedMax;
   private Color overrideColor;

   public TextWriter(String text) {
      this(text, Integer.MAX_VALUE);
   }

   public TextWriter(String text, boolean overrideLimit) {
      this(text, Integer.MAX_VALUE, null, 0, null, overrideLimit);
   }

   public TextWriter(String text, TextureRegion image) {
      this(text, Integer.MAX_VALUE, null, 0, image);
   }

   public TextWriter(String text, TextureRegion image, int width) {
      this(text, width, null, 0, image);
   }

   public TextWriter(String text, int width) {
      this(text, width, null, 0);
   }

   public TextWriter(String text, int width, Color borderCol, int borderSize) {
      this(text, width, borderCol, borderSize, null);
   }

   public TextWriter(String text, int width, Color borderCol, int borderSize, TextureRegion image) {
      this(text, width, borderCol, borderSize, image, false);
   }

   public TextWriter(String text, int width, Color borderCol, int borderSize, TextureRegion image, boolean overrideMaxWidth) {
      this(text, width, borderCol, borderSize, image, overrideMaxWidth, false);
   }

   public TextWriter(String text, int width, Color borderCol, int borderSize, TextureRegion image, boolean overrideMaxWidth, boolean tannFontOverride) {
      this.setTransform(false);
      text = text.replace("[dot]", ".").replace("[comma]", ",");
      this.overrideMaxWidth = overrideMaxWidth;
      this.text = com.tann.dice.Main.t(text);
      this.borderSize = borderSize;
      this.borderCol = borderCol;
      this.image = image;
      this.tannFontOverride = tannFontOverride;
      this.setWidth(width);
      this.layout();
   }

   public static TextWriter withTannFontOverride(String text) {
      return new TextWriter(text, Integer.MAX_VALUE, null, 0, null, false, true);
   }

   public static TextWriter withTannFontOverride(String text, int forceWidth) {
      return new TextWriter(text, forceWidth, null, 0, null, false, true);
   }

   public int getNumLines() {
      return this.numLines;
   }

   public static void setup() {
      textureMap.put("fullHeart", Images.fullHeart);
      textureMap.put("confirmSkull", Images.confirmSkull);
      textureMap.put("hp", Images.hp);
      textureMap.put("hp-plus", Images.hp_plus);
      textureMap.put("hp-hole", Images.hp_hole);
      textureMap.put("hp-girder", Images.hp_girder);
      textureMap.put("hp-cross", Images.hp_cross);
      textureMap.put("hp-square", Images.hp_square);
      textureMap.put("hp-diamond", Images.hp_diamond);
      textureMap.put("hp-bar", Images.hp_bar);
      textureMap.put("hp-bracket", Images.hp_bracket);
      textureMap.put("hp-glider", Images.hp_glider);
      textureMap.put("hp-reverse", Images.hp_reverse);
      textureMap.put("hp-arrow_left", Images.hp_arrow_left);
      textureMap.put("hp-arrow_up", Images.hp_arrow_up);
      textureMap.put("hp-arrow_down", Images.hp_arrow_down);
      textureMap.put("mana", Images.mana);
      textureMap.put("checkbox", Images.checkbox);
      textureMap.put("checkboxTicked", Images.checkbox_ticked);
      textureMap.put("info", Images.info);
      textureMap.put("pips", Images.pips);
      textureMap.put("plus", Images.plusBig);
      textureMap.put("tick", Images.tickSmall);
      textureMap.put("infinite", Images.infinite);
      textureMap.put("plusfive", Images.plusFive);
      textureMap.put("minus", Images.minusBig);
      textureMap.put("equals", Images.equalsBig);
      textureMap.put("petrify-diagram", Images.itemDiagramPetrify);
      textureMap.put("mysteryVoice", Images.mv);
      textureMap.put("cog", Images.cog);
      textureMap.put("hash", Images.hash);
      textureMap.put("tinyDice", Images.tinyDice);
      colorMap.put("white", Colours.z_white);
      colorMap.put("red", Colours.red);
      colorMap.put("brown", Colours.brown);
      colorMap.put("purple", Colours.purple);
      colorMap.put("blue", Colours.blue);
      colorMap.put("dark", Colours.dark);
      colorMap.put("light", Colours.light);
      colorMap.put("yellow", Colours.yellow);
      colorMap.put("orange", Colours.orange);
      colorMap.put("grey", Colours.grey);
      colorMap.put("ultragrey", Colours.shiftedTowards(Colours.text, Colours.dark, 0.6F).cpy());
      colorMap.put("secret", Colours.shiftedTowards(Colours.text, Colours.dark, 0.25F).cpy());
      colorMap.put("green", Colours.green);
      colorMap.put("pink", Colours.pink);
      colorMap.put("shift", Colours.SHIFTER);
      colorMap.put("text", Colours.text);
      colorMap.put("blurple", Colours.BLURPLE);

      for (HeroCol value : HeroCol.values()) {
         if (colorMap.get(value.colName) == null) {
            colorMap.put(value.colName, value.col);
         }
      }
   }

   public static String getNameForColour(Color c) {
      for (Entry<String, Color> entry : colorMap.entrySet()) {
         if (entry.getValue().equals(c)) {
            return entry.getKey();
         }
      }

      return null;
   }

   public static String getTag(Color c) {
      String name = getNameForColour(c);
      return name == null ? "" : "[" + name + "]";
   }

   public static String getTag(TextureRegion a) {
      for (Entry<String, TextureRegion> stringTextureRegionEntry : textureMap.entrySet()) {
         if (stringTextureRegionEntry.getValue() == a) {
            return "[" + stringTextureRegionEntry.getKey() + "]";
         }
      }

      return "invalid image";
   }

   public void layout() {
      try {
         this.layoutInternal();
      } catch (Exception var2) {
         var2.printStackTrace();
         TannLog.error(var2, "tw: " + this.text);
         this.clearChildren();
         Tann.become(this, new Rectactor(5, 5, Colours.pink));
      }
   }

   private FontWrapper getFont() {
      return this.tannFontOverride ? FontWrapper.getTannFont() : FontWrapper.getFont();
   }

   private void layoutInternal() {
      FontWrapper font = this.getFont();
      this.guessedMax = TannFont.guessMaxTextLength(0.5F);
      this.max = 0;
      if (this.text == null) {
         this.text = "?? null ??";
      }

      this.split = this.text.split("[\\[\\]]");
      Color currentColour = Colours.text;
      Color previousColour = Colours.pink;
      Color p2Colour = Colours.pink;
      this.clearChildren();
      int index = 0;
      this.justAddedSpace = false;
      this.x = 0.0F;
      this.y = 0;
      boolean wiggle = false;
      boolean sin = false;
      boolean bold = false;
      boolean italics = false;
      boolean glitch = false;
      List<Actor> lineActors = new ArrayList<>();
      List<Actor> wordActors = new ArrayList<>();

      for (String s : this.split) {
         if (s.isEmpty()) {
            index++;
         } else {
            if (index % 2 == 0) {
               String[] words = s.split(" ", -1);
               if (words.length == 0) {
                  this.x = this.x + font.getSpaceWidth(italics);
                  this.justAddedSpace = true;
               }

               for (int i = 0; i < words.length; i++) {
                  if (i > 0) {
                     this.addWord(wordActors, lineActors);
                     this.x = this.x + font.getSpaceWidth(italics);
                     this.justAddedSpace = true;
                  }

                  String word = words[i];
                  TextBox tb = new TextBox(word, this.getFont(), wiggle, sin, bold, italics, i == 0, glitch);
                  if (tb.getWidth() > com.tann.dice.Main.width * 0.5F && !this.overrideMaxWidth) {
                     tb = new TextBox(Tann.makeEllipses(word, this.guessedMax), this.getFont(), wiggle, sin, bold, italics, i == 0, glitch);
                  }

                  tb.setColor(this.overrideColor == null ? currentColour : this.overrideColor);
                  wordActors.add(tb);
               }
            } else {
               boolean image = false;
               switch (s) {
                  case "notranslate":
                  case "notranslateall":
                     break;
                  case "h":
                     this.addWord(wordActors, lineActors);
                     this.x = this.x + font.getSpaceWidth(italics) / 2.0F;
                     this.justAddedSpace = true;
                     break;
                  case "nbp": {
                     Actor spacer = new Actor();
                     spacer.setSize(font.getInterCharacterSpace(), this.lineHeight);
                     wordActors.add(spacer);
                     this.justAddedSpace = true;
                     break;
                  }
                  case "nbs": {
                     Actor spacer = new Actor();
                     spacer.setSize(font.getSpaceWidth(false), this.lineHeight);
                     wordActors.add(spacer);
                     this.justAddedSpace = true;
                     break;
                  }
                  case "p":
                     this.addWord(wordActors, lineActors);
                     this.x = this.x + font.getInterCharacterSpace();
                     this.justAddedSpace = true;
                     break;
                  case "q":
                     this.addWord(wordActors, lineActors);
                     this.x = this.x - font.getInterCharacterSpace();
                     break;
                  case "n":
                     this.addWord(wordActors, lineActors);
                     this.nextLine(lineActors);
                     break;
                  case "n2":
                     this.addWord(wordActors, lineActors);
                     this.lineHeight += 2;
                     this.nextLine(lineActors);
                     break;
                  case "nh":
                     this.addWord(wordActors, lineActors);
                     this.nextLine(lineActors);
                     this.y = this.y - font.getLineHeight() / 3;
                     break;
                  case "wiggle":
                     wiggle = !wiggle;
                     break;
                  case "sin":
                     sin = !sin;
                     break;
                  case "b":
                     bold = !bold;
                     break;
                  case "g":
                     glitch = !glitch;
                     break;
                  case "i":
                     italics = !italics;
                     break;
                  case "cu":
                     currentColour = previousColour;
                     previousColour = p2Colour;
                     break;
                  case "blank":
                     wordActors.add(new TextWriter(""));
                     break;
                  default:
                     image = true;
               }

               Color c = getMapCol(s);
               if (c != null) {
                  p2Colour = previousColour;
                  previousColour = currentColour;
                  currentColour = c;
                  image = false;
               }

               if (image) {
                  Actor tr = this.fetchActorFromTag(s, this.overrideColor == null ? currentColour : this.overrideColor);
                  if (tr == null) {
                     String cont = "{" + s + "}";
                     TextBox tb = new TextBox(cont, this.getFont());
                     if (tb.getWidth() > com.tann.dice.Main.width * 0.5F) {
                        tb = new TextBox(Tann.makeEllipses(cont, this.guessedMax), this.getFont(), wiggle, sin, bold, italics, false, glitch);
                     }

                     tb.setColor(currentColour);
                     wordActors.add(tb);
                  } else {
                     wordActors.add(tr);
                  }
               }
            }

            index++;
         }
      }

      if (wordActors.size() > 0) {
         this.addWord(wordActors, lineActors);
      }

      if (lineActors.size() > 0) {
         this.nextLine(lineActors);
      }

      this.setSize(this.max + this.borderSize * 2, -this.y + this.borderSize * 2);
      ArrayIterator var22 = this.getChildren().iterator();

      while (var22.hasNext()) {
         Actor a = (Actor)var22.next();
         a.setX(a.getX() + this.borderSize);
         a.setY((int)(this.getHeight() + a.getY() - a.getHeight() / 2.0F - this.borderSize));
      }
   }

   public static Color getMapCol(String s) {
      String COL_STR = "col";
      if (s.startsWith("col")) {
         String rem = s.substring(3);
         if (colorMap.get(rem) != null) {
            return colorMap.get(rem);
         }

         if (rem.length() == 3 || rem.length() == 6) {
            Color c = Colours.fromHex(rem);
            if (c == Colours.pink) {
               return null;
            }

            colorMap.put(rem, c);
            return c;
         }
      }

      return colorMap.get(s);
   }

   private static TextureRegion fetchImageFromTag(String s) {
      Item i = ItemLib.byName(s);
      if (!i.isMissingno()) {
         return i.getImage();
      } else {
         EntType e = EntTypeUtils.byName(s);
         if (!e.isMissingno()) {
            return e.portrait;
         } else {
            Texture t = Img64.fromStringCached(s);
            if (t != null) {
               return new TextureRegion(t);
            } else {
               if (FacadeUtils.matches(s)) {
                  TP<String, TextureRegion> res = FacadeUtils.indexedFullData(s.substring(0, 3), Integer.parseInt(s.substring(3)));
                  if (res != null) {
                     return res.b;
                  }
               }

               return null;
            }
         }
      }
   }

   private Actor fetchActorFromTag(String s, Color currentCol) {
      if (textureMap.get(s) != null) {
         return new ImageActor(textureMap.get(s), currentCol);
      } else if (!s.equals("image") && !s.equals("img")) {
         String VAL_STR = "val";
         if (s.startsWith("val")) {
            DungeonContext dc = DungeonScreen.getCurrentContextIfInGame();
            if (dc != null) {
               String key = s.substring(3);
               Integer v = dc.getValue(key);
               if (v != null) {
                  return new TextWriter(getTag(currentCol) + v);
               }
            }
         }

         TextureRegion tr = fetchImageFromTag(s);
         return tr != null ? new ImageActor(tr) : null;
      } else {
         return this.image == null ? null : new ImageActor(this.image, currentCol);
      }
   }

   private void addWord(List<Actor> wordActors, List<Actor> lineActors) {
      FontWrapper font = this.getFont();
      Actor a = this.makeActorFrom(wordActors);
      if (a != null) {
         Actor last = wordActors.get(wordActors.size() - 1);
         boolean it = last instanceof TextBox && ((TextBox)last).italics;
         wordActors.clear();
         if (this.x != 0.0F && this.x + a.getWidth() > this.getWidth()) {
            this.nextLine(lineActors);
         } else if (!lineActors.isEmpty()) {
            Actor previousOnLine = lineActors.get(lineActors.size() - 1);
            if (previousOnLine instanceof TextBox && a instanceof TextBox && !this.justAddedSpace) {
               this.x = this.x + font.getInterCharacterSpace();
            }
         }

         a.setPosition(this.x, this.y);
         this.x = this.x + a.getWidth();
         this.max = (int)Math.max(this.x + (it ? 2 : 0), (float)this.max);
         this.lineHeight = Math.max(this.lineHeight, font.getLineHeight() - 1);

         for (int i = 0; i < wordActors.size(); i++) {
            this.lineHeight = (int)Math.max((float)this.lineHeight, wordActors.get(i).getHeight());
         }

         lineActors.add(a);
         this.justAddedSpace = false;
      }
   }

   private Actor makeActorFrom(List<Actor> wordActors) {
      if (wordActors.size() == 0) {
         return null;
      } else if (wordActors.size() == 1) {
         return wordActors.get(0);
      } else {
         FontWrapper font = this.getFont();
         Group g = Tann.makeGroup();
         float x = 0.0F;

         for (Actor a : wordActors) {
            g.setHeight(Math.max(g.getHeight(), a.getHeight()));
         }

         Actor previousActor = null;

         for (Actor a : wordActors) {
            g.addActor(a);
            if (previousActor instanceof TextBox && a instanceof TextBox) {
               String pre = ((TextBox)previousActor).text;
               String post = ((TextBox)a).text;
               if (pre != null && pre.length() > 0 && post != null && post.length() > 0) {
                  x += font.getInterCharacterSpace() + font.getKerning(pre.charAt(pre.length() - 1), post.charAt(0));
               }
            }

            a.setPosition(this.getFont().isHDFont() ? Math.round(x) : (int)x, (int)((g.getHeight() - a.getHeight()) / 2.0F));
            x += a.getWidth();
            previousActor = a;
         }

         g.setWidth(x);
         return g;
      }
   }

   private void nextLine(List<Actor> lineActors) {
      this.x = 0.0F;
      if (this.y != 0) {
         this.y--;
      }

      for (Actor a : lineActors) {
         this.lineHeight = (int)Math.max((float)this.lineHeight, a.getHeight());
      }

      for (Actor a : lineActors) {
         this.addActor(a);
         a.setY((int)(this.y - this.lineHeight / 2.0F));
      }

      this.y = this.y - this.lineHeight;
      lineActors.clear();
      this.lineHeight = 0;
      this.numLines++;
   }

   public void draw(Batch batch, float parentAlpha) {
      if (this.borderCol != null) {
         Draw.fillActor(batch, this, Colours.dark, this.borderCol, 1);
      }

      super.draw(batch, parentAlpha);
   }

   public void setText(String text) {
      this.text = text;
      this.setWidth(2.1474836E9F);
      this.layout();
   }

   public void setAlpha(float alpha) {
      TannStage.alphaSetRecursive(this, alpha);
   }

   public void setOverrideColour(Color color) {
      this.overrideColor = color;
      this.layout();
   }

   public static String stripTags(String text) {
      return text.replaceAll("\\[.*?\\]", "");
   }

   public static String rebracketTags(String input) {
      return input.replaceAll("\\[", "{").replaceAll("\\]", "}");
   }

   public float getWidth() {
      return (float)Math.ceil(super.getWidth());
   }
}
