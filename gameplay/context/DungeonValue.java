package com.tann.dice.gameplay.context;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.ui.TextWriter;

public class DungeonValue {
   public static final String SEPARATOR = "V";
   private String key;
   private int value;

   public DungeonValue(String key, int value) {
      this.key = TextWriter.stripTags(key);
      this.value = value;
   }

   public DungeonValue() {
   }

   public String getKey() {
      return this.key;
   }

   public int getValue() {
      return this.value;
   }

   public void setValue(int value) {
      this.value = value;
   }

   @Override
   public String toString() {
      return this.key + ":" + this.value;
   }

   public String desc(boolean delta) {
      return this.key + this.descDelta(delta);
   }

   private String descDelta(boolean delta) {
      return (delta ? (this.value > 0 ? "+" : "") : ":") + this.value;
   }

   public Actor getActor(boolean big, boolean delta) {
      return new Pixl(0, 3).text(this.descDelta(delta) + " [orange]" + this.key).border(Colours.orange).pix();
   }

   public void add(int delta) {
      this.value += delta;
   }

   public String toSaveString() {
      return this.key + "V" + this.value;
   }

   public DungeonValue copy() {
      return new DungeonValue(this.key, this.value);
   }
}
