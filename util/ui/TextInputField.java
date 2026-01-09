package com.tann.dice.util.ui;

import com.badlogic.gdx.Input.TextInputListener;
import com.tann.dice.util.Colours;
import com.tann.dice.util.listener.TannListener;

public class TextInputField extends TextWriter {
   Runnable textUpdateRunnable;

   public TextInputField(String def) {
      this(def, 999);
   }

   public TextInputField(String def, final int maxCharacters) {
      super("", 999, Colours.green, 3);
      this.updateText(def);
      this.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            com.tann.dice.Main.self().control.textInput(new TextInputListener() {
               public void input(String text) {
                  TextInputField.this.updateText(text.substring(0, Math.min(text.length(), maxCharacters)));
                  if (TextInputField.this.textUpdateRunnable != null) {
                     TextInputField.this.textUpdateRunnable.run();
                  }
               }

               public void canceled() {
               }
            }, "enter highscore name", "", "");
            return true;
         }
      });
   }

   private void updateText(String text) {
      this.setText(text);
   }

   public void setOnTextUpdate(Runnable runnable) {
      this.textUpdateRunnable = runnable;
   }
}
