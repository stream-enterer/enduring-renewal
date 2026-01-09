package com.tann.dice.screens.dungeon.panels.book.page.ledgerPage;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.screens.dungeon.panels.book.page.cogPage.menuPanel.CreditsPanel;
import com.tann.dice.statics.sound.music.MusicBlob;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PatchBlob {
   static final int BIG_ROW = 5;
   final String version;
   final String date;
   final List<String> top;
   final List<String> features;
   final List<String> balance;
   final List<String> bugs;
   final List<String> misc;

   public PatchBlob(String version, String date, List<String> top, List<String> features, List<String> balance, List<String> bugs, List<String> misc) {
      this.version = version;
      this.date = date;
      this.top = top;
      this.features = features;
      this.balance = balance;
      this.bugs = bugs;
      this.misc = misc;
   }

   public Actor makeActor(int forceWidth) {
      int border = 5;
      Pixl p = new Pixl(2, border - 1).forceWidth(forceWidth - border * 2).border(Colours.dark, Colours.purple, 1);
      int reportMaxWidth = forceWidth - border * 2 - 1 - 3;
      p.text("[notranslate][yellow]" + this.version + "[grey] - " + this.date);
      this.addLines(p, "", this.top, reportMaxWidth);
      this.addLines(p, "[green]features", this.features, reportMaxWidth);
      this.addLines(p, "[blue]balance", this.balance, reportMaxWidth);
      this.addLines(p, "[purple]bugs", this.bugs, reportMaxWidth);
      this.addLines(p, "[blue]misc", this.misc, reportMaxWidth);
      return p.pix(8);
   }

   public String getExternalString() {
      String result = this.version + " - " + this.date + "\n";
      List<List<String>> asList = Arrays.asList(this.features, this.balance, this.bugs, this.misc);

      for (int i = 0; i < asList.size(); i++) {
         List<String> strings = asList.get(i);
         result = result + "\n";
         result = result + Arrays.asList("features", "balance", "bugs", "misc").get(i).toUpperCase() + " -\n";

         for (String se : strings) {
            result = result + se + "\n";
         }
      }

      return result;
   }

   private void addLines(Pixl p, String title, List<String> list, int maxWidth) {
      if (list.size() != 0) {
         p.row(5);
         p.text(title).row();

         for (String s : list) {
            p.text("[notranslate][text]- " + s, maxWidth).row();
         }
      }
   }

   public static PatchBlob[] getRawData() {
      return new PatchBlob[]{
         new PatchBlob(
            "3.2.9",
            "2025-05-12",
            Arrays.asList("translation and bugfixes"),
            Arrays.asList(
               "translated into Portuguese, Spanish, Russian, French, Italian",
               "thanks to " + Tann.commaList(Arrays.asList(CreditsPanel.TRANSLATORS)),
               "more community art",
               "new keyword sloth to replace alliteration",
               "poem item changed",
               "a few extra togs",
               "some alternate scribbles for ritem",
               "re-enable jinx"
            ),
            Arrays.asList("some graph and itemgen changes", "ting nerf"),
            Arrays.asList(
               "increase hero limit to 36 and attempt to enforce it better",
               "name tag fix",
               "hopefully fixed bad caching causing textmod dev nightmare",
               "textmod seqphase ui fix",
               "fix custom party and raid speedrun",
               "messed with shifter on monsters",
               "tinker with genfailrandom",
               "green hero texture chat crash fix"
            ),
            Arrays.asList("various wording changes to allow for translation")
         ),
         new PatchBlob(
            "3.1.20",
            "2024-11-23",
            Arrays.asList("small patch after 3.1, mostly bugfixes"),
            new ArrayList<>(),
            Arrays.asList(
               "graph changes",
               "heroes changes [yellow]prodigy",
               "modifiers changed [purple]strained top, double monster hp, skip rewards 4, death shield, mortal, *stone*, barrel time",
               "ritem slight nerf",
               "level up bless nerf",
               "bench the idols"
            ),
            Arrays.asList("better achievement migration", "fix for error rerolling curses", "fix monster fluctuate", "custom mode scrolling stats"),
            Arrays.asList(
               "replica now keeps names",
               "more community art",
               "togorf & togunt",
               "removed blessing and hero random rewards",
               "richtext facade, .img.facade",
               "20 heroes max",
               "failsafe negative item",
               "ban heavy dice from cursed",
               "show passives and spells in sides view",
               "[nokeywords] doesn't affect later keywords",
               "no tweaks in speedruns (except skip)"
            )
         ),
         new PatchBlob(
            "3.1.13",
            "2024-11-19",
            Arrays.asList("QoL", "Balance", "Modding stuff", "Bugfixes", "Leaderboard reset"),
            Arrays.asList(
               "-30% battery usage",
               "steam achievements",
               "hd Fonts",
               "blyptra mode",
               "roll speed",
               "landscape lock",
               "lots more probably",
               "--modding stuff--",
               "custom tactics, custom-er side effects, summon heroes",
               "lots more, textmod v1.1 I guess",
               "broke half the 3.0 mods, sorry"
            ),
            Arrays.asList(
               "adjustments to the graph",
               "nerfed 'myriad options'",
               "tinkered with generated heroes",
               "[yellow]heroes changed: lazy whirl scrapper barbarian eccentric wanderer scoundrel clumsy dabbler gambler ludus agent alloy knight bard prince poet valkyrie fey surgeon fate prodigy cultist evoker jester seer warlock ace primrose spade presence statue sphere coffin alien tainted vessel jumble pilgrim doctor",
               "[orange]monsters changed: the hand, sudul",
               "[purple]modifiers changed: hero immunity, reduced defence, 4hp, 3 pip pain, barricade, expensive spells, immune monsters, lowest exert, heavy weapons, static blanks, back to basics, double monster hp, stone first, dooms, monster rows, mortals, caltrops, heavy dice, tough hp, big hitter, death shield, sickly, mana debt, tower, monster shields, monster hp, spiky monsters, wurst, nth death, manymore",
               "[grey]items changed: color-restricted items, tracked, cursed bolt, wand of wand, anchor, infused herbs, coin, trowel, foil, kilt, polearm, flickering blade, buckler, autumn leaf, clumsy hammer, tower shields, fletching, clef, pillow, terrarium, shortsword, wristblade, enchanted shield, pocket phylactery, clover, syringe, duvet, harpoon, wild seeds, relic, cracked plate, magic staff, obol, idol of aiiu, antivenom, soup, tiara, natural, powerstone, full moon, dragonhide gloves, eggshell, fangs, dynamo, cocoon, pauldron, sponge, gizmo, determination, jewel loupe, eucalyptus, candle, water, sparks, hourglass, cauldron, enhance wand, ornate hilt, troll blood, demon claw, boots of speed, burning halo, poison dip, katar, serration, brimstone, wax seal, spike stone, egg basket"
            ),
            Arrays.asList("fixed some exploits", "double permadeath crash", "added some new bugs to balance out the ones I fixed"),
            Arrays.asList(
               "more party layouts for cursed modes",
               "removed rise, grave spam, poison immunity from cursed modes",
               "messed with achievements",
               "changed some names",
               "messed with command limit",
               "renamed heroes keep their new name when levelling up"
            )
         ),
         new PatchBlob(
            "3.0.18",
            "2024-03-28",
            Arrays.asList(
               "Just bugfixes mostly, no real balance changes until 3.1 probably. Hopefully your save files will work, but green generated heroes will be scrambled."
            ),
            Arrays.asList(
               "Dice scale",
               "New monster- golem!",
               "Now submits current save when using 'report bug' from Titlescreen",
               "Added missing nightmare transition tile",
               "Smarter warnings for max mana",
               "Textmod custom spells (but it's weird)"
            ),
            Arrays.asList(
               "Stop green hats from being offered", "Stop greens generating with modifier-adding passives", "Ban modifier-adding heroes from cursed modes"
            ),
            Arrays.asList(
               "Fixed crash on launch for Turkish language (maybe other languages too)",
               "Fixed textmod savebricking bug",
               "Improved levelling up interactions with custom hero pools, still some bugs I think though...",
               "Fixed a bug that could cause enemies to skip a turn",
               "Remove broken 'custom party masochist' achievement",
               "Sarcophagus cannot flee (also 'no flee' modifier)",
               "Allow jinx.sandstorm^1",
               "Fixed some scrollwheel stuff",
               "Improved v2-v3 save migration (too late for most!)",
               "Fixed resurrecting heroes not working under some circumstances",
               "Can under/over-pick modifiers by a little more",
               "Changed Bag of Holding because it's too much for the game to handle combined with other meta-items",
               "Bell/baron passive now work better with generated monsters",
               "Fixed selfheal breaking keywords like underdog",
               "Minimap fixed",
               "Iphone notch tutorial issue fixed",
               "Some more too boring to mention"
            ),
            Arrays.asList()
         ),
         new PatchBlob(
            "3.0",
            "2024-03-20",
            Arrays.asList("Steam + iOS", "Music", "'Modding'", "Green heroes", "Portrait orientation", "Everything++"),
            Arrays.asList(
               "Loads, I can't remember",
               "More items, enemies, heroes, keywords, modes",
               "Music from " + MusicBlob.getMusicianFeature(),
               "+[infinite] modifiers",
               "Tactics (a new thing like spells)",
               "Custom mode, for choosing your curses or playing 'mods'",
               "Generated monsters & items",
               "Two kinds of generated modifiers, some are weird and must be enabled in options",
               "Various UI improvements",
               "More events",
               "More options",
               "Expanded book",
               "Pips can go negative",
               "A bit more hero chat stuff",
               "Portrait layout",
               "Adjustable UI to leave space for streamer avatar/etc",
               "Better support for alternate party types",
               "Cruel eg now works with damage-all, headshot etc",
               "Keywords are more-permissive in general, eg heal poison",
               "Item offers now have collision"
            ),
            Arrays.asList("Lots changed", "Shields and heals a bit stronger", "Blessings stronger too", "Events system improved, allowing for powerful events"),
            Arrays.asList("Most bugs from 2.0 fixed", "Exciting new bugs to discover"),
            Arrays.asList()
         ),
         new PatchBlob(
            "2.0.0",
            "2022-10-02",
            Arrays.asList(
               "Thanks so much from me and a3um to everyone who bought the game! You have changed my life and funded this big update <3",
               "This version uses a new save file, hopefully it will prompt you to restore some achievements"
            ),
            Arrays.asList(
               "+40 new heroes (including alternate t1 heroes)",
               "+11 new modes",
               "+213 new items",
               "+23 new monsters (including 5 bosses)",
               "+52 new keywords",
               "+175 new blessings/curses (+474 more kinda)",
               "+80 new achievements",
               "+20,000 more heroes???",
               "More events (like challenge)",
               "Almanac expanded & improved",
               "Improved item iconography",
               "Tutorial tweaked",
               "Confirmation dialogs everywhere",
               "More options",
               "Achievements rework",
               "Fullscreen",
               "Still no music",
               "Blessings/curses for difficulty modes now randomly generated",
               "Level generation tweaked",
               "Removed shield sides from monsters",
               "'Unlock all' option",
               "Item ordering",
               "Keywords now all have unique icons",
               "Undo back to roll phase (if you still have rerolls left)",
               "Double-tap to sort dice",
               "Shareable copy/paste level state (it's a mode)",
               "Extra hard difficulty modes",
               "Skip hero levelup",
               "Leaderboards reset",
               "More leaderboards and improved leaderboard UI",
               "Enemies flee if losing badly",
               "Lots of art tweaks",
               "Lots more I forgot to write..."
            ),
            Arrays.asList(
               "Changed most things, it's probably easiest to look yourself...",
               "Removed/changed some items that are too specific for all the extra stuff",
               "No more dragon egg sorcerer, you'll have to work harder than that for an infinite (I hope!)"
            ),
            Arrays.asList("Fix cantrip leader crash", "Fix victory-load crash", "Many many other misc bug fixes", "Shader memory leak fix", "Added 47 new bugs"),
            Arrays.asList(
               "Video playthrough/explanation links in help section",
               "Rudimentary language section",
               "Targets no longer reposition whilst you are targeting",
               "Added confirmation popup to all choices and links",
               "Removed copy progress, now you can only copy achievements",
               "3d camera adjusted",
               "Keyword description improvements",
               "Cruel/engage etc now act on a different layer and can interact with other keywords like pain",
               "Capped most numbers to prevent overflow",
               "Reset stats/achievements buttons",
               "Leaderboards display platform",
               "[b]Bold font![b]",
               "Can now use more abilities when not-recommended (eg healing)"
            )
         ),
         new PatchBlob(
            "1.0.5",
            "2021-07-25",
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            Arrays.asList("Fixed mode progress resetting on play store", "Added $€¥ to the font", "Improved crash log reporting"),
            new ArrayList<>()
         ),
         new PatchBlob(
            "1.0.4",
            "2021-07-23",
            new ArrayList<>(),
            Arrays.asList(
               "Finally updated google play store to full version! Single IAP to unlock the game (same price as itch.io)",
               "Improved between-levels minimap thing"
            ),
            Arrays.asList(
               "Syringe from tier 3 to 4", "Enchanted Harp from tier 4 to 5", "Cursed Bolt from tier 1 to 2", "Mithril shields from tier 8 to 9", "Nerfed ???"
            ),
            Arrays.asList("Fixed coin bug (and related issues)"),
            Arrays.asList("Extra legs for tarantus", "Renamed weakness blessing to hamstring")
         ),
         new PatchBlob(
            "1.0.3",
            "2021-07-19",
            new ArrayList<>(),
            Arrays.asList(
               "5 new items",
               "Automated bug reports, just click 'report bug' and click the green button!",
               "Improved level generation: so you don't always fight wolf+boar on level 1!"
            ),
            Arrays.asList(
               "Made leaderboard achievements easier (except bones leaderboard)",
               "Starting curse-mode curses are a pre-set list of 4",
               "Moved sapphire item to tier 2",
               "Changed castor root to heal 1",
               "Changed big shield",
               "Changed anvil to affect middle side",
               "Renamed Brawn to Determination and moved to tier 3",
               "Nerfed brute force blessing",
               "Changed medical arts blessing",
               "+2 infinity spell cost"
            ),
            Arrays.asList(
               "A few crash fixes, including the invalid target crash!",
               "Hopefully fixed invisible dice on android",
               "Fixed wisps thanking themselves",
               "Excluded run history from clipboard save transfer"
            ),
            Arrays.asList(
               "Challenge icon!",
               "Tweak which keywords can be added to which sides (mostly for clarity)",
               "Minor wording changes",
               "Only check for updates once",
               "Remove annoying rotten chats",
               "Only store the last 100 runs in run history"
            )
         ),
         new PatchBlob(
            "1.0.2",
            "2021-07-10",
            new ArrayList<>(),
            Arrays.asList(
               "8 new items",
               "Added 3 curse-mode items to the main pool",
               "New 'Challenge' reward",
               "Added save-transfer system to title-screen menu",
               "Store run-history in stats",
               "Added patch notes to manual (right here!)",
               "More curse-mode blessings/curses",
               "Can now open inventory whilst picking rewards",
               "Added title-screen update notification"
            ),
            Arrays.asList(
               "Slate nerfed (hopefully will make troll king fight less-deadly!)",
               "Rotten buffed",
               "Curse-mode first curse is now a curse-mode curse (curse curse curse)",
               "Leader +1 hp",
               "Collector +1 hp -mana +hammer",
               "Guardian reworked",
               "Brawler -shield +stun ",
               "Sorcerer -1 hp, more mana and shield cantrip",
               "Venom -cleanse +poison, triple-poison now buffable",
               "Mender +1 spell cost",
               "Warlock +1 spell cost",
               "Sapphire -1 tier",
               "Short sword +1 tier",
               "Infinity spell +1 cost",
               "Castor root now replaces blank sides with heal 2",
               "Rusty place replaces middle side and gives +3 max hp",
               "Iron soul -1 shield",
               "Reagents changes different sides",
               "Boarhide Bracers changes different sides",
               "Remove contentious undo-limit curses",
               "Removed Heavy Spells & Mana Arrears curse-mode curses"
            ),
            Arrays.asList(
               "Hopefully fixed android mode duplication",
               "Hopefully fixed android re-open issues",
               "Bonus to incoming healing now affects regen even if counteracted by poison",
               "Stop dodge from dodging chat events/other friendly effects",
               "Can now use redirect with value 0",
               "Can no longer re-use wands vs basilisk if you cleanse",
               "Fixed enemies getting stuck in the wrong place if you end turn quickly",
               "Fixed some achievements popping up at the wrong time",
               "Fixed instant mode using the wrong backgrounds",
               "Fixed negative rerolls glitch",
               "Fixed dice remaining in roll area if they die during the rolling phase",
               "Fixed rare dragon fight crash",
               "Fixed shortcut mode allowing duplicate items",
               "Fixed broken curse completion achievements"
            ),
            Arrays.asList(
               "Some more keyboard shortcuts, check the manual under 'tips'",
               "Swapped Start Poisoned/Expensive Spells curses for Hard/Unfair mode",
               "Made some achievements easier to get",
               "Removed Vitality keyword",
               "Replaced drain keyword with self-heal",
               "Renamed 'slow' keyword to 'heavy'",
               "Renamed heavy hammer to big hammer",
               "Renamed Shock spell to Zap",
               "Burst spell is now always on the left",
               "Can click locked mode to show required achievement",
               "Changed the unequipped items warning system",
               "Made unfair-streak achievement show up in challenges",
               "Reordered item effects to avoid/allow some edge-cases",
               "Various UI tweaks/other minor fixes"
            )
         ),
         new PatchBlob(
            "1.0.1",
            "2021-06-19",
            new ArrayList<>(),
            Arrays.asList("Added button to the top to see your current level & mode"),
            Arrays.asList(
               "Changed some tier 3 blue hero hp",
               "Buffed big shield, extra arrow, twin daggers",
               "Trapper shield growth replaced with sword growth",
               "Liquor spell now heals for 20 (from 10)",
               "New spell for mender class"
            ),
            Arrays.asList(
               "Fixed 'nth spell is free' effect",
               "Fixed bug with Better Items blessing",
               "Fixed deaths stat not counting if you resurrect ",
               "Portrait transparency",
               "Vitality bonus hp can no longer be dodged",
               "Fixed undo limit curse"
            ),
            new ArrayList<>()
         ),
         new PatchBlob(
            "1.0.0",
            "2021-06-16",
            new ArrayList<>(),
            Arrays.asList(
               "Game finally released on itch for [green]£££££",
               "A3um has added art for all items, heroes, monsters, sides, spells, everything!",
               "Saves from older versions will not be carried over, sorry! I will try to maintain saves through all future updates now though",
               "All your favourite things have been changed, all the things you hated are still here",
               "More items, and tweaked the equip UI",
               "More monsters",
               "More achievements, and they now all unlock something",
               "Many heroes and spells and monsters have been changed",
               "Level generation improved, bones nerfed a bit!",
               "Improved collection display",
               "Added leaderboards",
               "Added stats at the end of each run",
               "Added bug report menu and UI scale option",
               "Changed input on desktop to use right-click and android to use long-tap",
               "Keyboard shortcuts for selecting spells & heroes",
               "Added a bunch of special modes to the paid version. I recommend trying curse mode!"
            ),
            new ArrayList<>(),
            Arrays.asList(
               "Fixed lots of bugs (thanks for reporting them!)", "Added lots of bugs (oops)", "Fixed visual glitches, changed animations, added sfx etc"
            ),
            Arrays.asList(
               "Spells have titles again at the bottom (if they fit)",
               "Changed difficulty names and curses available",
               "Some secrets (??)",
               "Probably a bunch more things I forgot to mention"
            )
         )
      };
   }
}
