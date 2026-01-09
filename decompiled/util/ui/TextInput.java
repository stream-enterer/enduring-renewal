package com.tann.dice.util.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.FontWrapper;
import com.tann.dice.util.TannFont;

public class TextInput extends Group {
   private static final float CARET_FLASH_SPEED = 4.5F;
   private static final float CARET_FORCE_DURATION = 0.3F;
   public static final int MAX_LENGTH = 2000;
   private static final int MAX_CHARS_TO_DISPLAY = 200;
   String text = "";
   private final FontWrapper font;
   private final TextBox textBox;
   int caretIndex = 0;
   int caretEndIndex = -1;
   TextInputListener listener;
   static final int HOME_END = 9999999;
   float forceCaret;

   public TextInput(TextInputListener listener) {
      this.font = FontWrapper.getFont();
      this.textBox = new TextBox("", this.font);
      this.textBox.setColor(Colours.text);
      this.addActor(this.textBox);
      this.setSize(Math.min(190.0F, com.tann.dice.Main.width * 0.8F), TannFont.font.getHeight());
      this.listener = listener;
      this.setTransform(false);
      this.addListener(new InputListener() {
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            TextInput.this.caretIndex = TextInput.this.getCaretIndex(x);
            TextInput.this.caretEndIndex = TextInput.this.caretIndex;
            TextInput.this.forceShowCaret();
            return true;
         }

         public void touchDragged(InputEvent event, float x, float y, int pointer) {
            TextInput.this.caretIndex = TextInput.this.getCaretIndex(x);
         }
      });
   }

   private int getCaretIndex(float x) {
      if (x <= 0.0F) {
         return 0;
      } else {
         int max = this.text.length();
         if (!(x >= this.getWidth()) && max != 0) {
            for (int i = max; i >= 1; i--) {
               if (x > this.font.getWidth(this.text.substring(0, i))) {
                  return i;
               }
            }

            return 0;
         } else {
            return max;
         }
      }
   }

   public void setText(String text) {
      if (text == null) {
         text = "";
      }

      this.text = text;
      this.updateText();
      this.moveCaretToEnd();
   }

   private void moveCaretToEnd() {
      this.caretIndex = this.text.length();
   }

   public boolean notify(Event event, boolean capture) {
      if (!(event instanceof InputEvent)) {
         return super.notify(event, capture);
      } else {
         InputEvent event1 = (InputEvent)event;
         boolean ctrl = Gdx.input.isKeyPressed(129) || Gdx.input.isKeyPressed(63);
         boolean shift = Gdx.input.isKeyPressed(59) || Gdx.input.isKeyPressed(60);
         if (event1.getType() == Type.keyDown) {
            int kc = event1.getKeyCode();
            switch (kc) {
               case 3:
                  this.moveCaret(-9999999, false, shift);
                  break;
               case 21:
                  this.moveCaret(-1, ctrl, shift);
                  break;
               case 22:
                  this.moveCaret(1, ctrl, shift);
                  break;
               case 31:
                  if (this.text.length() > 0 && ctrl) {
                     if (this.caretIndex != this.caretEndIndex && this.caretEndIndex >= 0) {
                        int c1 = Math.min(this.caretIndex, this.caretEndIndex);
                        int c2 = Math.max(this.caretIndex, this.caretEndIndex);
                        ClipboardUtils.copyWithSoundAndToast(this.text.substring(c1, c2));
                     } else {
                        ClipboardUtils.copyWithSoundAndToast(this.text);
                     }
                  }
                  break;
               case 50:
                  if (ctrl) {
                     String clip = ClipboardUtils.pasteSafer();
                     if (clip != null) {
                        this.insertTextAtCaret(clip);
                     }
                  }
                  break;
               case 111:
                  com.tann.dice.Main.getCurrentScreen().popSingleMedium();
                  break;
               case 123:
                  this.moveCaret(9999999, false, shift);
            }
         } else {
            if (event1.getType() != Type.keyTyped) {
               return super.notify(event, capture);
            }

            char c = event1.getCharacter();
            switch (c) {
               case '\b':
                  this.deleteChar();
                  break;
               case '\n':
                  com.tann.dice.Main.getCurrentScreen().popSingleMedium();
                  this.listener.input(this.text);
                  break;
               case '\u007f':
                  if (this.caretIndex != this.caretEndIndex && this.caretEndIndex >= 0) {
                     this.deleteChar();
                  } else if (this.caretIndex < this.text.length()) {
                     this.text = this.text.substring(0, this.caretIndex) + this.text.substring(this.caretIndex + 1, this.text.length());
                     this.updateText();
                     this.forceShowCaret();
                  }
                  break;
               default:
                  this.addChar(c);
            }

            event1.cancel();
         }

         event1.cancel();
         event1.stop();
         return true;
      }
   }

   private void insertTextAtCaret(String c) {
      if (this.caretIndex != this.caretEndIndex && this.caretEndIndex >= 0) {
         int c1 = Math.min(this.caretIndex, this.caretEndIndex);
         int c2 = Math.max(this.caretIndex, this.caretEndIndex);
         this.text = this.text.substring(0, c1) + c + this.text.substring(c2);
         this.caretIndex = c1;
      } else {
         this.text = this.text.substring(0, this.caretIndex) + c + this.text.substring(this.caretIndex);
         this.caretIndex = this.caretIndex + c.length();
      }

      this.caretEndIndex = -1;
      this.updateText();
   }

   private void deleteChar() {
      if (this.caretIndex != this.caretEndIndex && this.caretEndIndex >= 0) {
         int c1 = Math.min(this.caretIndex, this.caretEndIndex);
         int c2 = Math.max(this.caretIndex, this.caretEndIndex);
         this.text = this.text.substring(0, c1) + this.text.substring(c2);
         this.caretEndIndex = -1;
         this.caretIndex = c1;
      } else {
         this.text = this.text.substring(0, Math.max(0, this.caretIndex - 1)) + this.text.substring(this.caretIndex);
         this.moveCaret(-1);
      }

      this.updateText();
      this.forceShowCaret();
   }

   private void moveCaret(int delta) {
      this.moveCaret(delta, false, false);
   }

   private boolean isAlphanum(char c) {
      return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9';
   }

   private void moveCaret(int delta, boolean jumpWord, boolean extendSelection) {
      if (extendSelection) {
         if (this.caretEndIndex < 0) {
            this.caretEndIndex = this.caretIndex;
         }
      } else {
         this.caretEndIndex = -1;
      }

      char cleared;
      char next;
      if (!jumpWord) {
         this.caretIndex += delta;
      } else {
         do {
            int oldCaret = this.caretIndex;
            this.caretIndex += delta > 0 ? 1 : -1;
            int newCaret = this.caretIndex;
            if (this.caretIndex < 0 || this.caretIndex > this.text.length()) {
               break;
            }

            if (delta < 0) {
               oldCaret--;
               newCaret--;
            }

            cleared = oldCaret >= 0 && oldCaret < this.text.length() ? this.text.charAt(oldCaret) : 32;
            next = newCaret >= 0 && newCaret < this.text.length() ? this.text.charAt(newCaret) : 32;
         } while (this.isAlphanum(cleared) && this.isAlphanum(next));
      }

      this.caretIndex = Math.max(0, Math.min(this.text.length(), this.caretIndex));
      this.forceShowCaret();
   }

   private void forceShowCaret() {
      this.forceCaret = 0.3F;
   }

   private void addChar(char c) {
      if (this.font.hasChar(c) && this.text.length() < 2000) {
         this.insertTextAtCaret(c + "");
         this.forceShowCaret();
      }
   }

   private void updateText() {
      this.textBox.text = this.text.substring(0, Math.min(200, this.text.length()));
   }

   public void act(float delta) {
      com.tann.dice.Main.self();
      com.tann.dice.Main.requestRendering();
      this.forceCaret -= delta;
      super.act(delta);
   }

   public void draw(Batch batch, float parentAlpha) {
      if (this.caretEndIndex >= 0 && this.caretEndIndex != this.caretIndex) {
         float w1 = this.font.getWidth(this.text.substring(0, this.caretIndex));
         float w2 = this.font.getWidth(this.text.substring(0, this.caretEndIndex));
         float startX = Math.min(w1, w2);
         float width = Math.abs(w1 - w2);
         batch.setColor(Colours.blue);
         Draw.fillRectangle(batch, this.getX() + startX, this.getY(), width, 8.0F);
      }

      if (Math.sin(com.tann.dice.Main.secs * 4.5F) > 0.0 && this.text.length() < 200 || this.forceCaret > 0.0F) {
         float w = this.font.getWidth(this.text.substring(0, this.caretIndex));
         batch.setColor(Colours.grey);
         Draw.fillRectangle(batch, this.getX() + w, this.getY(), 1.0F, 8.0F);
      }

      super.draw(batch, parentAlpha);
   }

   public String getText() {
      return this.text;
   }

   public void clearTextInput() {
      this.text = "";
      this.updateText();
      this.moveCaretToEnd();
      this.caretEndIndex = -1;
   }
}
