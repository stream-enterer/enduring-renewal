package com.tann.dice.screens.dungeon.panels.book.page.helpPage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.screens.dungeon.DungeonUtils;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Rectactor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.saves.Prefs;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.text.Normalizer;
import java.text.Normalizer.Form;

public class LanguageThing {
   private static String PREF_KEY = "languageThingOpened";

   public static Actor makeLanguageSection(YoutuberPanel.Language l, int contentWidth) {
      switch (l) {
         case English:
            return makeSimpleTranslated(
               contentWidth,
               YoutuberPanel.Language.English,
               "Sorry, the game is only available in English. It will be difficult if you can't read English text well.",
               "There are icons for some things, for example",
               "+3 hp",
               "replace the right two sides of the dice with 2-damage attacks",
               "this button bottom-left will undo most things- try experimenting"
            );
         case Korean:
            return makeSimpleTranslated(
               contentWidth,
               YoutuberPanel.Language.Korean,
               "joesonghabnida. geim-eun yeong-eoloman jegongdoebnida. yeong-eo wonmun-eul jal ilgji moshamyeon eolyeoul geos-ibnida.",
               "yeleul deul-eo myeoch gaji hangmog-e daehan aikon-i issseubnida.",
               "+3 choedae HP",
               "jusawiui oleunjjog yangmyeon-eul 2demiji gong-gyeog-eulo gyoche",
               "oenjjog hadan-e issneun i beoteun-eun daebubun-ui jag-eob-eul chwisohabnida. silheomhae boseyo."
            );
         case Japanese:
            return makeSimpleTranslated(
               contentWidth,
               YoutuberPanel.Language.Japanese,
               "Mōshiwakearimasenga, gēmu wa eigo de nomi riyō kanōdesu. Eigo no tekisuto o yoku yomenaito muzukashīdeshou.",
               "Tatoeba, ikutsu ka no aikon ga arimasu",
               "+3 Saidai HP",
               "Saikoro no migi 2-men o 2 damēji kōgeki ni okikaeru",
               "Kono hidarishita no botan wa hotondo no koto o gen ni modoshimasu - jikken shite mite kudasai"
            );
         case German:
            return makeSimpleTranslated(
               contentWidth,
               YoutuberPanel.Language.German,
               "Entschuldigung, das Spiel ist nur auf Englisch verfügbar. Schwierig wird es, wenn Sie überhaupt keinen englischen Text lesen können.",
               "Es gibt zum Beispiel Symbole für einige Dinge",
               "+3 max hp",
               "Ersetzen Sie die beiden rechten Seiten des Würfels durch Angriffe mit 2 Schadenspunkten",
               "Diese Schaltfläche unten links macht die meisten Dinge rückgängig - versuchen Sie es mit Experimenten"
            );
         case French:
            return makeSimpleTranslated(
               contentWidth,
               YoutuberPanel.Language.French,
               "Désolé, le jeu n'est disponible qu'en anglais. Ce sera difficile si vous ne pouvez pas bien lire le texte anglais.",
               "Il y a des icônes pour certaines choses, par exemple",
               "+3 HP maximum",
               "remplacer les deux côtés droits des dés par des attaques à 2 dégâts",
               "ce bouton en bas à gauche annulera la plupart des choses - essayez d'expérimenter"
            );
         case Spanish:
            return makeSimpleTranslated(
               contentWidth,
               YoutuberPanel.Language.Spanish,
               "Lo sentimos, el juego solo está disponible en inglés. Será difícil si no puedes leer bien el texto en inglés.",
               "Hay iconos para algunas cosas, por ejemplo",
               "+3 hp máx",
               "reemplaza los dos lados derechos de los dados con ataques de 2 daños",
               "este botón en la parte inferior izquierda deshará la mayoría de las cosas; intente experimentar"
            );
         case Polish:
            return makeSimpleTranslated(
               contentWidth,
               YoutuberPanel.Language.Polish,
               "Przykro nam, gra jest dostępna tylko w języku angielskim. Będzie to trudne, jeśli nie potrafisz dobrze czytać tekstu w języku angielskim.",
               "Istnieją ikony dla niektórych rzeczy, na przykład",
               "",
               "",
               "ten przycisk w lewym dolnym rogu cofa większość rzeczy – spróbuj poeksperymentować"
            );
         case Russian:
            return makeSimpleTranslated(
               contentWidth,
               YoutuberPanel.Language.Russian,
               "К сожалению, игра доступна только на английском языке. Будет сложно, если вы плохо читаете английский текст.",
               "Есть иконки для некоторых вещей, например",
               "+3 макс хп",
               "замените правые две стороны кубика атаками с 2 повреждениями",
               "эта кнопка внизу слева отменит большинство действий - попробуйте поэкспериментировать",
               false
            );
         case Portuguese:
            return makeSimpleTranslated(
               contentWidth,
               YoutuberPanel.Language.Portuguese,
               "Desculpe, o jogo só está disponível em inglês. Será difícil se você não conseguir ler bem o texto em inglês.",
               "Existem ícones para algumas coisas, por exemplo",
               "+3 hp máximo",
               "substitua os dois lados direitos dos dados por ataques de 2 de dano",
               "este botão no canto inferior esquerdo irá desfazer a maioria das coisas - tente experimentar"
            );
         case Chinese:
            return makeSimpleTranslated(
               contentWidth,
               YoutuberPanel.Language.Chinese,
               "Bàoqiàn, gāi yóuxì jǐn tígōng yīngwén bǎnběn. Rúguǒ nǐ bùnéng hěn hǎo de yuèdú yīngwén wénběn, zhè jiāng shì kùnnán de.",
               "Yǒuxiē dōngxī yǒu túbiāo, lìrú",
               "+3 zuìdà mǎlì",
               "jiāng shǎizi de yòu cè liǎng cè tìhuàn wèi 2 shānghài gōngjí",
               "zhège zuǒxià jiǎo de ànniǔ jiāng chèxiāo dà duōshù shìqíng - chángshì shìyàn"
            );
         default:
            return makeSimpleTranslated(contentWidth, YoutuberPanel.Language.English, "err", "err", "err", "err", "err");
      }
   }

   private static Actor makeSimpleTranslated(
      int contentWidth, YoutuberPanel.Language language, String topLine, String iconography, String threeMax, String replaceSides, String undo
   ) {
      return makeSimpleTranslated(contentWidth, language, topLine, iconography, threeMax, replaceSides, undo, true);
   }

   private static Actor makeSimpleTranslated(
      int contentWidth, YoutuberPanel.Language language, String topLine, String iconography, String threeMax, String replaceSides, String undo, boolean strip
   ) {
      int rectWidth = contentWidth - 6;
      if (strip) {
         topLine = fixLang(topLine);
         iconography = fixLang(iconography);
         threeMax = fixLang(threeMax);
         replaceSides = fixLang(replaceSides);
         undo = fixLang(undo);
      }

      topLine = "[text]" + topLine;
      iconography = "[text]" + iconography;
      threeMax = "[text]" + threeMax;
      replaceSides = "[text]" + replaceSides;
      undo = "[text]" + undo;
      Actor youtubers = new Pixl(1).rowedActors(language.makeYoutuberPanels()).pix();
      return new Pixl(2)
         .text(topLine, (int)(contentWidth - youtubers.getWidth() - 34.0F))
         .gap(7)
         .actor(youtubers)
         .row()
         .actor(new Rectactor(rectWidth, 1, Colours.grey))
         .row()
         .image(Images.hack_undo)
         .text(undo, (int)(contentWidth * 0.5))
         .pix();
   }

   private static String fixLang(String text) {
      return Normalizer.normalize(text, Form.NFD).replaceAll("[^\\p{ASCII}]", "");
   }

   public static Actor makeGlobeButton() {
      Group group = new Group();
      Actor ia = DungeonUtils.makeBasicButton(Images.globe);
      group.addActor(ia);
      group.setSize(ia.getWidth(), ia.getHeight());
      final Actor badge = new ImageActor(Images.badge);
      badge.setVisible(false);
      if (Prefs.getString(PREF_KEY, null) == null) {
         badge.setVisible(true);
      }

      badge.setPosition(ia.getWidth() - badge.getWidth() / 2.0F, ia.getHeight() - badge.getHeight() / 2.0F);
      group.addActor(badge);
      ia.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            Actor a = LanguageThing.makeLanguageContainer();
            Sounds.playSound(Sounds.pip);
            com.tann.dice.Main.getCurrentScreen().push(a, 0.8F);
            Tann.center(a);
            Prefs.setString(LanguageThing.PREF_KEY, "1");
            badge.setVisible(false);
            return true;
         }
      });
      return group;
   }

   private static Actor makeLanguageContainer() {
      int width = 140;
      Pixl p = new Pixl(4, 4).border(Colours.blue);

      for (final String lang : OptionLib.LANGUAGE.getOptions()) {
         StandardButton sb = new StandardButton(lang);
         sb.setRunnable(new Runnable() {
            @Override
            public void run() {
               OptionLib.LANGUAGE.setValue(lang, true);
            }
         });
         p.actor(sb, 140.0F);
      }

      return p.pix();
   }

   private static void showLanguagePanel(YoutuberPanel.Language l) {
      if (l == YoutuberPanel.Language.Chinese) {
         showChinesePage();
      } else {
         Sounds.playSound(Sounds.pip);
         Actor a = new Pixl(0, 3).border(Colours.blue).actor(makeLanguageSection(l, (int)(com.tann.dice.Main.width * 0.8F))).pix();
         com.tann.dice.Main.getCurrentScreen().push(a, true, true, false, 0.8F);
         Tann.center(a);
      }
   }

   private static void showChinesePage() {
      final Texture guide = new Texture(Gdx.files.internal("guide/chinese.png"));
      Sounds.playSound(Sounds.pip);
      Group chineseMiddleSection = new Group() {
         public void draw(Batch batch, float parentAlpha) {
            com.tann.dice.Main.self().stop2d(true);
            SpriteBatch bg = com.tann.dice.Main.self().startBackground();
            int scale = com.tann.dice.Main.scale;
            Draw.scaleRegion(bg, guide, (int)this.getX() * scale, (int)this.getY() * scale, (int)this.getWidth() * scale, (int)this.getHeight() * scale);
            com.tann.dice.Main.self().stopBackground();
            com.tann.dice.Main.self().start2d(true);
            super.draw(batch, parentAlpha);
         }
      };
      float ratio = (float)guide.getWidth() / guide.getHeight();
      int maxWidth = (int)(com.tann.dice.Main.width * 0.9F);
      int maxHeight = (int)(com.tann.dice.Main.height * 0.9F);
      int width = (int)Math.min((float)maxWidth, maxHeight * ratio);
      int height = (int)(width * (1.0F / ratio));
      chineseMiddleSection.setSize(width, height);
      float linkCenterX = 0.44F;
      float linkCenterY = 0.92F;
      float linkWidth = 0.43F;
      float linkHeight = 0.11F;
      Actor clickable = new Actor();
      clickable.setSize((int)(width * linkWidth), (int)(height * linkHeight));
      chineseMiddleSection.addActor(clickable);
      clickable.setPosition(
         chineseMiddleSection.getWidth() * (linkCenterX - linkWidth / 2.0F), chineseMiddleSection.getHeight() * (linkCenterY - linkHeight / 2.0F)
      );
      clickable.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            com.tann.dice.Main.getCurrentScreen().openUrl("https://www.bilibili.com/video/BV163411B7BU/");
            return true;
         }
      });
      Actor cont = new Pixl(0, 1).border(Colours.grey).actor(chineseMiddleSection).pix();
      com.tann.dice.Main.getCurrentScreen().push(cont, 0.8F);
      Tann.center(cont);
   }
}
