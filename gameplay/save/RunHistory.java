package com.tann.dice.gameplay.save;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.SmallModifierPanel;
import com.tann.dice.gameplay.phase.gameplay.EnemyRollingPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.LevelEndPhase;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.screens.dungeon.panels.book.views.HeroLedgerView;
import com.tann.dice.screens.dungeon.panels.entPanel.ItemHeroPanel;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RunHistory {
   public static final int MAX_STORED = 5000;
   public static final int MAX_WIDTH = 164;
   private long d;
   private boolean v;
   private boolean n;
   private String dd;
   private String m;
   private List<String> mo;
   private PartyData p;
   private LevelData f;
   private static final int DATE_DIV = 10000;

   public RunHistory() {
   }

   public RunHistory(
      long date, boolean victory, String modeName, String modeAndDifficultyDescription, List<String> modifierNames, PartyData partyData, LevelData finalLevel
   ) {
      this.d = date / 10000L;
      this.dd = modeAndDifficultyDescription;
      this.v = victory;
      this.p = partyData;
      this.f = finalLevel;
      this.m = modeName;
      this.mo = modifierNames;
   }

   public static Group makeGroup(List<RunHistory> runs) {
      Pixl runPixl = new Pixl(3);
      int added = 0;
      int MAX = 20;

      for (int i = runs.size() - 1; i >= 0; i--) {
         RunHistory runHistory = runs.get(i);
         runPixl.actor(runHistory.makeActor()).row(4);
         if (++added >= 20) {
            break;
         }
      }

      return runPixl.pix();
   }

   public long getDate() {
      return this.d;
   }

   public String getModeAndDifficultyDescription() {
      return this.dd;
   }

   public PartyData getPartyData() {
      return this.p;
   }

   public LevelData getFinalLevel() {
      return this.f;
   }

   public Actor makeActor() {
      Pixl p = new Pixl(3, 3).border(this.v ? Colours.green : Colours.red);
      String ddTrans = "";
      String[] words = this.dd.split(" ");

      for (String w : words) {
         if (ddTrans.length() > 0) {
            ddTrans = ddTrans + " ";
         }

         ddTrans = ddTrans + com.tann.dice.Main.t(w);
      }

      p.text("[notranslate]" + ddTrans + " " + com.tann.dice.Main.t(this.v ? "[green]Victory!" : "[purple]Defeat"))
         .row()
         .text(Tann.getTimeDescription(Tann.format.format(new Date(this.d * 10000L))))
         .row();
      if (this.mo != null && this.mo.size() > 0) {
         p.row(4).text("[text]Modifiers:").row();

         for (String s : this.mo) {
            Modifier m = ModifierLib.byName(s);
            SmallModifierPanel smp = new SmallModifierPanel(m);
            smp.addBasicListener();
            p.actor(smp, 164.0F);
         }

         p.row(4);
      }

      Pixl allHeroPix = new Pixl(3);
      int row = 500;
      if (this.p.h.size() > 6) {
         row = this.p.h.size() / 2;
      }

      for (int i = 0; i < this.p.h.size(); i++) {
         if (i == row) {
            allHeroPix.row();
         }

         final Hero h = HeroTypeUtils.makeHeroFromString(this.p.h.get(i));
         Pixl heroPix = new Pixl();
         HeroLedgerView hav = new HeroLedgerView(h.getHeroType(), true);
         heroPix.actor(hav).row();
         hav.addListener(new TannListener() {
            @Override
            public boolean info(int button, float x, float y) {
               EntPanelInventory dp = new EntPanelInventory(h);
               Sounds.playSound(Sounds.pip);
               com.tann.dice.Main.getCurrentScreen().push(dp, true, true, true, 0.0F);
               Tann.center(dp);
               return true;
            }
         });

         for (Item e : h.getItems()) {
            heroPix.actor(new ItemHeroPanel(e, null));
         }

         Group heroGroup = heroPix.pix();
         allHeroPix.actor(heroGroup);
      }

      p.actor(allHeroPix.pix(2));
      p.row();
      p.text(this.v ? "[green]final fight" : "[red]defeated by").row();

      for (String monsterType : this.f.m) {
         final MonsterType mt = MonsterTypeLib.byName(monsterType);
         ImageActor ia = new ImageActor(mt.portrait, true);
         ia.addListener(new TannListener() {
            @Override
            public boolean info(int button, float x, float y) {
               EntPanelInventory dp = new EntPanelInventory(mt.makeEnt());
               Sounds.playSound(Sounds.pip);
               com.tann.dice.Main.getCurrentScreen().push(dp, true, true, true, 0.0F);
               Tann.center(dp);
               return true;
            }
         });
         p.actor(ia);
      }

      Group g = p.pix();
      if (!UnUtil.isLocked(Mode.PASTE)) {
      }

      return g;
   }

   private String makePasteString() {
      return makePasteString(this);
   }

   public static String makePasteString(RunHistory rh) {
      return makePasteString(rh.getPartyData(), rh.getFinalLevel(), rh.getModifierStrings());
   }

   private static String makePasteString(PartyData partyData, LevelData levelData, List<String> modifierNames) {
      int numEntities = partyData.h.size() + levelData.m.size();
      List<String> l = new ArrayList<>();

      for (int i = 0; i < numEntities; i++) {
         l.add("0");
      }

      int estimatedLevel = Math.max(1, Math.min(20, new Party(partyData).getProbableLevel()));
      SaveStateData ssd = new SaveStateData(
         new DungeonContextData(
            null,
            null,
            partyData,
            estimatedLevel,
            estimatedLevel,
            modifierNames,
            new ArrayList<>(),
            0L,
            levelData,
            new ArrayList<>(),
            0,
            false,
            new ArrayList<>()
         ),
         new ArrayList<>(),
         Tann.commaList(l, ",", ","),
         Arrays.asList(new LevelEndPhase().serialise(), new EnemyRollingPhase().serialise())
      );
      ssd.trimContextDataForReport();
      return ssd.toState().getSaveString();
   }

   public String getModeName() {
      return this.m;
   }

   public boolean isVictory() {
      return this.v;
   }

   public List<String> getModifierStrings() {
      return this.mo;
   }

   public List<Modifier> getModifiers() {
      List<Modifier> result = new ArrayList<>();

      for (String mn : this.mo) {
         result.add(ModifierLib.byName(mn));
      }

      return result;
   }

   public void markNightmare() {
      this.n = true;
   }

   public boolean nightmareConsumed() {
      return this.n;
   }

   public boolean isInDateForNightmare() {
      return this.getDate() > (System.currentTimeMillis() - 604800000L) / 10000L;
   }
}
