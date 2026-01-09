package com.tann.dice.test;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.screens.dungeon.panels.book.Book;
import com.tann.dice.screens.dungeon.panels.book.page.BookPage;
import com.tann.dice.test.util.Test;
import com.tann.dice.util.FontWrapper;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class TestBook {
   @Test
   public static void renderBook() {
      if (!FontWrapper.getFont().isHDFont()) {
         Batch batch = new SpriteBatch();
         batch.begin();
         List<String> bads = new ArrayList<>();
         Book b = new Book();
         b.draw(batch, 1.0F);

         for (BookPage bookPage : BookPage.getAll(com.tann.dice.Main.self().masterStats.createMergedStats(), 400, 500)) {
            try {
               bookPage.draw(batch, 1.0F);
               b.focusPage(bookPage);
               b.draw(batch, 1.0F);

               for (Actor page : bookPage.debugGiveAllActors()) {
                  page.draw(batch, 1.0F);
               }
            } catch (Exception var7) {
               var7.printStackTrace();
               bads.add(bookPage.title);
            }
         }

         batch.end();
         batch.dispose();
         Tann.assertBads(bads);
      }
   }
}
