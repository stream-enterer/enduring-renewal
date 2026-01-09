package com.tann.dice.util;

import com.badlogic.gdx.scenes.scene2d.Group;

public class TwoCol {
   Pixl left;
   Pixl right;

   public TwoCol() {
      this(new Pixl(1), new Pixl(1));
   }

   public TwoCol(Pixl left, Pixl right) {
      this.left = left;
      this.right = right;
   }

   public TwoCol addRow(String l, String r) {
      this.left.text(l).row();
      this.right.text(r).row();
      return this;
   }

   public Group pix(int gap) {
      return new Pixl(gap).actor(this.left.pix(8)).actor(this.right.pix(16)).pix();
   }
}
