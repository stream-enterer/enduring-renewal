package com.tann.dice.screens.dungeon.panels.book.page;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.screens.dungeon.panels.book.Book;
import com.tann.dice.screens.dungeon.panels.book.SideBar;
import com.tann.dice.screens.dungeon.panels.book.TopTab;
import com.tann.dice.screens.dungeon.panels.book.page.helpPage.ContentPanel;
import com.tann.dice.screens.dungeon.panels.book.page.helpPage.HelpPage;
import com.tann.dice.screens.dungeon.panels.book.page.ledgerPage.LedgerPage;
import com.tann.dice.screens.dungeon.panels.book.page.stuffPage.StuffPage;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class BookPage extends Group {
   private int aboveTitlesTopArea;
   public final String title;
   protected SideBar sideBar;
   protected ContentPanel contentPanel;
   protected Map<String, Stat> allMergedStats;

   public BookPage(String title, Map<String, Stat> allMergedStats, int width, int height) {
      this.setSize(width, height);
      this.aboveTitlesTopArea = Book.getMainTitlesHeight();
      this.title = title;
      this.allMergedStats = allMergedStats;
      this.setTransform(false);
      this.setup(this.getAllListItems());
      this.setTouchable(Touchable.childrenOnly);
   }

   public Color getColour() {
      return Colours.light;
   }

   public TopTab makeTopTab() {
      return new TopTab(this, "[b]" + this.title);
   }

   public static List<BookPage> getAll(Map<String, Stat> allMergedStats, int width, int height) {
      return Arrays.asList(new HelpPage(width, height), new LedgerPage(allMergedStats, width, height), new StuffPage(allMergedStats, width, height));
   }

   public static String getChosenString(int chosen, int rejected) {
      if (chosen + rejected == 0) {
         return "[text]Not encountered yet...";
      } else {
         int prc = (int)((float)chosen / (chosen + rejected) * 100.0F);
         String prcString = prc + "%";
         String colourTag = "[orange]";
         if (prc < 50) {
            colourTag = "[red]";
         } else if (prc > 50) {
            colourTag = "[green]";
         }

         prcString = colourTag + prcString + "[cu]";
         return "[notranslate]" + com.tann.dice.Main.t("chosen " + chosen + "/" + (chosen + rejected)) + " ([h]" + prcString + "[h])";
      }
   }

   public static void push(Actor a) {
      Sounds.playSound(Sounds.pipSmall);
      com.tann.dice.Main.getCurrentScreen().push(a, true, true, true, 0.7F);
      Tann.center(a);
   }

   public void onFocus(String defaultSidebar) {
      if (defaultSidebar != null) {
         this.showDefaultSidebar(defaultSidebar);
      } else {
         String pg = com.tann.dice.Main.getSettings().getLastSpecificPage(this);
         if (pg != null) {
            TopTab sb = this.getSideBarItemFromString(pg);
            if (sb != null) {
               this.openSidebar(sb);
               return;
            }
         }

         this.openSidebar(this.sideBar.getItems().get(0));
      }
   }

   public static int getPadding() {
      return 1;
   }

   private int getContentPanelWidth() {
      return (int)(this.getWidth() - getSideWidth());
   }

   private int getContentWidth() {
      return this.getContentPanelWidth() - getPadding() * 2 + (this instanceof HelpPage ? -8 : 0);
   }

   private int getContentHeight() {
      return com.tann.dice.Main.isPortrait()
         ? (int)(this.getHeight() - this.aboveTitlesTopArea - this.sideBar.getHeight()) - getPadding() * 2 + 1
         : (int)this.getHeight() - getPadding() * 2 - 1;
   }

   private int getContentPanelHeight() {
      return this.getContentHeight() + getPadding() * 2;
   }

   protected abstract List<TopTab> getAllListItems();

   protected void setup(List<TopTab> items) {
      this.sideBar = new SideBar();
      this.sideBar.setDrawBorder(!com.tann.dice.Main.isPortrait());
      this.setSidebarItems(items);
      this.contentPanel = new ContentPanel(this.getContentPanelWidth(), this.getContentPanelHeight(), this);
      this.addActor(this.sideBar);
      this.addActor(this.contentPanel);
      if (com.tann.dice.Main.isPortrait()) {
         this.sideBar.setPosition(0.0F, this.getHeight() - this.sideBar.getHeight() - this.aboveTitlesTopArea + 1.0F);
      } else {
         this.sideBar.setPosition(0.0F, -1.0F);
      }

      this.setContentPosition();
   }

   public void setSidebarItems(List<TopTab> items) {
      for (final TopTab sbi : items) {
         this.sideBar.addItem(sbi);
         sbi.addListener(new TannListener() {
            @Override
            public boolean action(int button, int pointer, float x, float y) {
               Sounds.playSound(Sounds.pipSmall);
               BookPage.this.openSidebar(sbi);
               return true;
            }
         });
      }

      if (com.tann.dice.Main.isPortrait()) {
         this.sideBar.layoutPortrait((int)this.getWidth(), 18);
      } else {
         this.sideBar.layoutLandscape((int)this.getHeight() - this.aboveTitlesTopArea);
      }
   }

   public void showDefaultSidebar(String defaultSidebar) {
      TopTab sbi = this.getSideBarItemFromString(defaultSidebar);
      if (sbi != null) {
         this.openSidebar(sbi);
      }
   }

   private TopTab getSideBarItemFromString(String defaultSidebar) {
      for (TopTab item : this.sideBar.getItems()) {
         if (item.getTabName().toLowerCase().endsWith(defaultSidebar.toLowerCase())) {
            return item;
         }
      }

      return null;
   }

   protected void openSidebar(TopTab sbi) {
      this.sideBar.highlightItem(sbi);
      Actor a = this.getContentActorFromSidebar(sbi.getIdentifier(), this.getContentWidth());
      this.showThing(a, a.getName() != null && a.getName().contains("bot"));
      com.tann.dice.Main.getSettings().setLastAlmanacPage(this.title + "-" + sbi.getTabName());
   }

   public void showThing(Actor a) {
      this.showThing(a, false);
   }

   public void showThing(Actor a, boolean scrollToBottom) {
      this.contentPanel.setContent(a);
      this.contentPanel.layout();
      this.contentPanel.setScrollPercentY(scrollToBottom ? 1.0F : 0.0F);
      this.contentPanel.updateVisualScroll();
      this.setContentPosition();
      com.tann.dice.Main.stage.setScrollFocus(this.contentPanel);
   }

   private void setContentPosition() {
      int toX = (int)(getSideWidth() + this.getContentPanelWidth() / 2.0F - this.contentPanel.getWidth() / 2.0F);
      this.contentPanel.setX(toX);
      if (this.contentPanel.getHeight() < this.getContentPanelHeight() - getPadding()) {
         this.contentPanel.setY(this.getContentPanelHeight() - this.contentPanel.getHeight() - getPadding());
      } else {
         this.contentPanel.setY((int)(this.getContentPanelHeight() / 2.0F - this.contentPanel.getHeight() / 2.0F));
      }
   }

   public List<Actor> debugGiveAllActors() {
      List<Actor> acs = new ArrayList<>();

      for (TopTab allListItem : this.getAllListItems()) {
         acs.add(this.getContentActorFromSidebar(allListItem.getIdentifier(), this.getContentWidth()));
      }

      return acs;
   }

   protected abstract Actor getContentActorFromSidebar(Object var1, int var2);

   public static int getSideWidth() {
      return com.tann.dice.Main.isPortrait() ? 0 : SideBar.getSideWidth();
   }

   public SideBar getSideBar() {
      return this.sideBar;
   }
}
