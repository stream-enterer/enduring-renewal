package com.tann.dice.screens.dungeon.panels.book;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.screens.dungeon.DungeonUtils;
import com.tann.dice.screens.dungeon.panels.ExplanelReposition;
import com.tann.dice.screens.dungeon.panels.Explanel.Explanel;
import com.tann.dice.screens.dungeon.panels.book.page.BookPage;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.KeyListen;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.PostPop;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.listener.TannListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Book extends Group implements PostPop, KeyListen, ExplanelReposition {
   public static final Color BOOK_COL = Colours.purple;
   private static final int LEFT_BORDER_WIDTH = 0;
   private static final int TT_HEIGHT = 16;
   private static final int TOP_BORDER_HEIGHT = 0;
   private static final int BOTTOM_BORDER_HEIGHT = 1;
   private static final int OUTER_BORDER = 20;
   List<BookPage> pages = new ArrayList<>();
   SideBar sideBar;
   BookPage focused;

   public Book() {
      this.setTransform(false);
      int ALMANAC_WIDTH = com.tann.dice.Main.width - 40;
      int ALMANAC_HEIGHT = com.tann.dice.Main.height - 40;
      if (com.tann.dice.Main.isPortrait()) {
         ALMANAC_WIDTH = com.tann.dice.Main.width - 6;
         ALMANAC_HEIGHT = (int)(com.tann.dice.Main.height * 0.74F);
      }

      this.setSize(ALMANAC_WIDTH, ALMANAC_HEIGHT);
      Map<String, Stat> allMergedStats = com.tann.dice.Main.self().masterStats.createMergedStats();
      this.pages.addAll(BookPage.getAll(allMergedStats, ALMANAC_WIDTH - 0, ALMANAC_HEIGHT - 0 - 1));
      Pixl p = new Pixl(0);
      this.sideBar = new SideBar();

      for (int i = 0; i < this.pages.size(); i++) {
         final BookPage page = this.pages.get(i);
         TopTab tt = page.makeTopTab();
         tt.addListener(new TannListener() {
            @Override
            public boolean action(int button, int pointer, float x, float y) {
               Sounds.playSound(Sounds.pipSmall);
               Book.this.focusPage(page);
               return true;
            }
         });
         int width;
         if (com.tann.dice.Main.isPortrait()) {
            width = (int)this.getWidth();
         } else {
            width = SideBar.getSideWidth();
         }

         tt.setSize(width, 16.0F);
         this.sideBar.addItem(tt);
      }

      if (com.tann.dice.Main.isPortrait()) {
         this.sideBar.layoutPortrait((int)this.getWidth(), getMainTitlesHeight());
      } else {
         this.sideBar.layoutLandscape(getMainTitlesHeight() + 2);
      }

      this.addActor(this.sideBar);
      this.sideBar.setY(this.getHeight() - this.sideBar.getHeight());
      Actor topBorder = p.pix();
      this.addActor(topBorder);
      topBorder.setY(this.getHeight() - topBorder.getHeight());
      BookPage bp = this.getRecommendedDefaultPage();
      if (bp == null) {
         this.focusPage(this.pages.get(0));
      } else {
         this.focusPage(bp, getSavedItemString());
      }

      this.addListener(new InputListener() {
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            event.cancel();
            return true;
         }
      });
   }

   public static int getMainTitlesHeight() {
      return com.tann.dice.Main.isPortrait() ? 28 : (int)(com.tann.dice.Main.height * 0.32F);
   }

   private static String getSavedItemString() {
      String focus = com.tann.dice.Main.getSettings().getLastAlmanacPage();
      return focus != null && focus.contains("-") ? focus.split("-")[1] : null;
   }

   private static String getSavedPageString() {
      String focus = com.tann.dice.Main.getSettings().getLastAlmanacPage();
      return focus != null && focus.contains("-") ? focus.split("-")[0] : null;
   }

   public static boolean inBook() {
      return Tann.findByClass(com.tann.dice.Main.stage, Book.class) != null;
   }

   private BookPage getRecommendedDefaultPage() {
      String focus = getSavedPageString();
      return focus != null ? this.getPage(focus) : null;
   }

   private BookPage getPage(String name) {
      for (BookPage page : this.pages) {
         if (page.title.toLowerCase().contains(name)) {
            return page;
         }
      }

      return null;
   }

   public void focusPage(BookPage page) {
      this.focusPage(page, null);
   }

   public void focusPage(BookPage page, String defaultSidebar) {
      if (this.focused != null) {
         this.focused.remove();
         this.focused = null;
      }

      this.focused = page;
      this.addActor(page);
      page.setY(1.0F);
      if (!com.tann.dice.Main.isPortrait()) {
         page.setX(0.0F);
      }

      this.sideBar.highlightAssoc(page);
      com.tann.dice.Main.stage.setScrollFocus(page);
      page.onFocus(defaultSidebar);
   }

   public void draw(Batch batch, float parentAlpha) {
      Draw.fillActor(batch, this, Colours.dark, BOOK_COL, 1);
      SideBar exsb = this.pages.get(0).getSideBar();
      exsb = this.focused.getSideBar();
      if (com.tann.dice.Main.isPortrait()) {
         int yy = (int)exsb.getY() + 1;
         int hh = (int)exsb.getHeight();
         Draw.fillRectangle(batch, this.getX(), this.getY() + yy, this.sideBar.getWidth(), hh, Colours.dark, BOOK_COL, 1);
      } else {
         Draw.fillRectangle(batch, this.getX(), this.getY(), this.sideBar.getWidth(), this.getHeight(), Colours.dark, BOOK_COL, 1);
         int ry = (int)(this.getHeight() - exsb.getY() - exsb.getHeight());
         int uy = (int)this.sideBar.getHeight();
         int amt = ry - uy - 1;
         if (amt > 0) {
            int yy = (int)(this.getY() + exsb.getHeight());
            batch.setColor(Colours.dark);
            Draw.fillRectangle(batch, this.getX() + 1.0F, yy, exsb.getWidth() - 2.0F, amt);
         }
      }

      super.draw(batch, parentAlpha);
   }

   public static Actor makeAlmanacButton() {
      Actor almanacButton = DungeonUtils.makeBasicButton(Images.almanac);
      almanacButton.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            Book.openBook(true);
            return true;
         }
      });
      return almanacButton;
   }

   public static void openBook(boolean manual) {
      openBook(null, null, manual);
   }

   public static void openBook(String path) {
      String[] parts = path.split("-");
      if (parts.length != 2) {
         TannLog.error("Error opening book from path: " + path);
      }

      openBook(parts[0], parts[1], true);
   }

   public String getPath() {
      return "menu/system";
   }

   public static void openBook(String pageName, String sidebar, boolean manual) {
      com.tann.dice.Main.getCurrentScreen().popAllMedium();
      if (manual) {
         Sounds.playSound(Sounds.pip);
      }

      Book a = new Book();
      if (pageName != null) {
         BookPage p = a.getPage(pageName);
         if (p != null) {
            a.focusPage(p, sidebar);
         }
      }

      a.setPosition((int)(com.tann.dice.Main.width / 2 - a.getWidth() / 2.0F), (int)(com.tann.dice.Main.height / 2 - a.getHeight() / 2.0F));
      com.tann.dice.Main.getCurrentScreen().push(a, true, true, false, 0.6F);
   }

   @Override
   public void postPop() {
      boolean wasInMenu = Tann.findByClass(com.tann.dice.Main.stage, DungeonUtils.CogTag.class) != null;
      if (wasInMenu) {
         DungeonUtils.showCogMenu();
      }
   }

   @Override
   public boolean keyPress(int keycode) {
      switch (keycode) {
         case 69:
         case 70:
         case 111:
            return false;
         default:
            return true;
      }
   }

   @Override
   public void repositionExplanel(Explanel a) {
      a.setPosition((int)(com.tann.dice.Main.width / 2 - a.getWidth() / 2.0F), (int)(com.tann.dice.Main.height / 2 - a.getHeight() / 2.0F));
   }
}
