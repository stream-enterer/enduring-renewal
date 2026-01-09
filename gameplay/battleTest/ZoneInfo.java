package com.tann.dice.gameplay.battleTest;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.battleTest.template.BossTemplateLibrary;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.screens.dungeon.panels.book.views.MonsterLedgerView;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import java.util.ArrayList;
import java.util.List;

public class ZoneInfo {
   private static Object getMaxWidth() {
      return com.tann.dice.Main.width - 20;
   }

   private static Group makeExplainZoneActor(Zone zone) {
      return new Pixl(5, 4).text(zone.name()).row().actor(makeMonstersActor(zone)).row().actor(makeBossesActor(zone)).pix();
   }

   private static Actor makeMonstersActor(Zone zone) {
      return makeMonstersActor("Monsters", zone.validMonsters);
   }

   private static Actor makeBossesActor(Zone zone) {
      return makeMonstersActor("Bosses", BossTemplateLibrary.getAllBossMonsters(zone));
   }

   private static Actor makeMonstersActor(String topString, List<MonsterType> validMonsters) {
      return new Pixl(2, 0).text(topString).row().actor(makeMonsterGroup(validMonsters)).pix();
   }

   private static Actor makeMonsterGroup(List<MonsterType> validMonsters) {
      List<Actor> actors = new ArrayList<>();

      for (final MonsterType validMonster : validMonsters) {
         MonsterLedgerView mav = new MonsterLedgerView(validMonster, false);
         mav.addListener(new TannListener() {
            @Override
            public boolean action(int button, int pointer, float x, float y) {
               EntPanelInventory dp = new EntPanelInventory(validMonster.makeEnt(), true);
               com.tann.dice.Main.getCurrentScreen().pushAndCenter(dp, 0.8F);
               return true;
            }
         });
         actors.add(mav);
      }

      return Tann.layoutMinArea(actors, 3, (int)(com.tann.dice.Main.width * 0.97F), 5);
   }
}
