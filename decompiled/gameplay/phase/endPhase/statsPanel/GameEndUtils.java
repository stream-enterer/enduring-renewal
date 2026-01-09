package com.tann.dice.gameplay.phase.endPhase.statsPanel;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierType;
import com.tann.dice.gameplay.modifier.SmallModifierPanel;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.endOfFight.HeroDeath;
import com.tann.dice.gameplay.progress.stats.stat.endRound.DamageTakenStat;
import com.tann.dice.gameplay.progress.stats.stat.endRound.TurnsTakenStat;
import com.tann.dice.gameplay.progress.stats.stat.miscStat.UndoCountStat;
import com.tann.dice.screens.dungeon.panels.book.views.HeroLedgerView;
import com.tann.dice.screens.dungeon.panels.entPanel.ItemHeroPanel;
import com.tann.dice.statics.Images;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.TwoCol;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameEndUtils {
   public static Actor makeTop(DungeonContext dungeonContext, boolean victory) {
      String congrat = victory ? "[yellow]Victory" : "[purple]Defeat";
      String progString = dungeonContext.getLevelProgressString(true);
      Pixl p = new Pixl(2).text("[notranslate]" + dungeonContext.getContextConfig().getEndTitle() + " - " + com.tann.dice.Main.t(congrat));
      if (progString != null) {
         p.row().text(progString);
      }

      return p.pix();
   }

   public static Actor makeModifiers(DungeonContext context) {
      boolean hasBlessings = false;
      boolean hasCurses = false;
      List<Modifier> modCopy = new ArrayList<>(context.getCurrentModifiers());

      for (int i = modCopy.size() - 1; i >= 0; i--) {
         if (modCopy.get(i).getName().contains("Hidden")) {
            modCopy.remove(i);
         }
      }

      for (Modifier m : modCopy) {
         hasBlessings |= m.getMType() == ModifierType.Blessing;
         hasCurses |= m.getMType() == ModifierType.Curse;
      }

      Pixl p = new Pixl(1);
      String desc = "";
      if (hasBlessings) {
         desc = desc + com.tann.dice.Main.t("[green]Blessings[cu]");
      }

      if (hasCurses) {
         if (desc.length() > 0) {
            desc = desc + " / ";
         }

         desc = desc + com.tann.dice.Main.t("[purple]Curses[cu]");
      }

      if (desc.isEmpty()) {
         desc = com.tann.dice.Main.t("[yellow]Tweaks") + "?";
      }

      p.text("[notranslate]" + desc).row();

      for (Modifier m : modCopy) {
         SmallModifierPanel smp = new SmallModifierPanel(m);
         smp.addBasicListener();
         p.actor(smp, 164.0F);
      }

      return p.pix();
   }

   public static Actor makeHeroes(DungeonContext dungeonContext) {
      Pixl p = new Pixl(1);
      Map<String, Stat> map = dungeonContext.getStatsManager().getStatsMap();
      List<Hero> heroes = dungeonContext.getParty().getHeroes();
      int rowAt = 5000;
      if (heroes.size() > 6) {
         rowAt = heroes.size() / 2;
      }

      for (int i = 0; i < heroes.size(); i++) {
         if (rowAt == i) {
            p.row();
         }

         Hero h = heroes.get(i);
         Pixl heroPix = new Pixl(1);
         HeroLedgerView hav = new HeroLedgerView((HeroType)h.entType, true);
         heroPix.actor(hav).row();
         hav.basicListener();
         ImageActor ia = new ImageActor(Images.skullTiny);
         ia.setColor(h.getColour());
         Stat s = map.get(HeroDeath.getNameFromIndex(i));
         if (s == null) {
            TannLog.log("Error getting stat for " + h);
         } else {
            heroPix.actor(ia).text(h.getColourTag() + "x" + s.getValue()).row();
         }

         for (Item e : h.getItems()) {
            heroPix.actor(new ItemHeroPanel(e, null));
         }

         p.actor(heroPix.pix());
      }

      return p.pix(2);
   }

   public static Actor makeLeft(DungeonContext dc) {
      Map<String, Stat> map = dc.getStatsManager().getStatsMap();
      return new TwoCol()
         .addRow("Time:", Tann.parseSeconds(dc.getFinalTimeSeconds(), false))
         .addRow("Turns:", map.get(TurnsTakenStat.NAME).getValue() + "")
         .addRow("Undos:", map.get(UndoCountStat.NAME).getValue() + "")
         .addRow("Rolls:", map.get("dice-rolled").getValue() + "")
         .addRow("[red]X[cu][h]s rolled:", map.get("crosses-rolled").getValue() + "")
         .pix(1);
   }

   public static Actor makeRight(DungeonContext context) {
      Map<String, Stat> map = context.getStatsManager().getStatsMap();
      return new TwoCol()
         .addRow("[purple]Kills:", "[purple]" + map.get("total-kills").getValue())
         .addRow("[orange]Dmg Taken:", "[orange]" + map.get(DamageTakenStat.NAME).getValue())
         .addRow("[grey]Blocked:", "[grey]" + map.get("total-blocked").getValue())
         .addRow("[red]Healed:", "[red]" + map.get("total-healing").getValue())
         .addRow("[blue]Abilities:", "[blue]" + map.get("spells-cast").getValue())
         .pix(1);
   }
}
