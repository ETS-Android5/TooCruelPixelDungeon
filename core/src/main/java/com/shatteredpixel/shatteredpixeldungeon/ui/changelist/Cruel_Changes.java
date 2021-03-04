/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AlbinoSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlackjackkeeperSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RotHeartSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SwarmSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class Cruel_Changes {
	
	public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){
		add_v0_4_0_Changes(changeInfos);
		add_v0_3_0_Changes(changeInfos);
		add_v0_2_0_Changes(changeInfos);
		add_v0_1_0_Changes(changeInfos);
	}
	
	public static void add_v0_4_0_Changes(ArrayList<ChangeInfo> changeInfos) {
		ChangeInfo changes = new ChangeInfo( "v0.4.0", true, "");
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);
		
		changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"),false,null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);
		
		changes.addButton( new ChangeButton(Icons.get(Icons.INFO), "Developer Commentary",
				"_-_ Released 05.03.2021\n" +
						"_-_ 181 days after v0.3.0" +
						"\n" +
						"Dev commentary will be added here in the future."));
		
		
		
	}
	public static void add_v0_3_0_Changes(ArrayList<ChangeInfo> changeInfos) {
		ChangeInfo changes = new ChangeInfo( "v0.3.0", true, "");
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);
		
		changes = new ChangeInfo("v0.3.0b",false,null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);
		
		changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
				"Fixed (caused by v0.3.0):\n" +
						"_-_ Even more crashes related to Rook challenge\n" +
						"_-_ Crashes related to Blackjack"));
		
		changes.addButton(new ChangeButton(new SwarmSprite(),Messages.get(Challenges.class,"horde_hell")+" and "+Messages.get(Challenges.class,"horde_hell2"),
				"_-_ Legion is now T3 challenge\n" +
						"_-_ Invasion is now T2 challenge\n" +
						"_-_ Invasion is now weaker on first 2 floors\n" +
						"_-_ Legion waves delay is increased"));
		
		changes.addButton(new ChangeButton(Icons.CHALLENGE_OFF.get(),Messages.get(Challenges.class,"rook"),
				"_-_ Diagonal ranged player interactions are now impossible."));
		
		changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.POTION_AMBER),Messages.get(Challenges.class,"no_healing_hell2"),
				"_-_ Increased intoxication gained from taking damage."));
		
		changes = new ChangeInfo("v0.3.0a",false,null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);
		
		changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
				"Fixed (caused by v0.3.0):\n" +
						"_-_ Various crashes related to Rook challenge"));
		
		changes.addButton(new ChangeButton(new SwarmSprite(),Messages.get(Challenges.class,"horde_hell"),
				"_-_ Now seals the level for 20 turns after start of each wave."));
		
		changes.addButton(new ChangeButton(new Ankh(){
			@Override
			public ItemSprite.Glowing glowing() {
				return new ItemSprite.Glowing(0xFF0000);
			}
		},Messages.get(Challenges.class,"resurrection_hell2"),
				"_-_ Tweaked mob rebirth chance"));
		
		changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_OFF),Messages.get(Challenges.class,"amnesia"),
				"_-_ Magically-mapped cells quickly becomes unknown."));
		
		changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"),false,null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);
		
		changes.addButton( new ChangeButton(Icons.get(Icons.INFO), "Developer Commentary",
				"_-_ Released 05.09.2020\n" +
						"_-_ 315 days after v0.2.0" +
						"\n" +
						"Dev commentary will be added here in the future."));
		changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
				"Fixed (caused by v0.2.0):\n" +
						"_-_ Racing the Death crash\n" +
						"_-_ Missing Intoxication description"));
		
		changes.addButton( new ChangeButton(Icons.get(Icons.CHALLENGE_HELL2), "Challenges",
				"_-_ Added even-more harder version to 3 challenges\n" +
						"_-_ Added Rook challenge"
		));
		
		changes.addButton(new ChangeButton(new Ankh().bless(),Messages.get(Challenges.class,"resurrection_hell"),
				"_-_ Renamed from Resurrection to Rebirth\n" +
						"_-_ Chance of mob rebirth is now increases with difference between player level and mob max level"));
		
		changes.addButton(new ChangeButton(new AlbinoSprite(),Messages.get(Challenges.class,"mutagen"),
				"_-_ Chance of spawning rare enemy variation reduced from 50% to 25%"));
		
		changes.addButton(new ChangeButton(new SwarmSprite(),Messages.get(Challenges.class,"horde_hell"),
				"_-_ No longer increases chances of spawning mobs from lower floors."));
		
		changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.POTION_AMBER),Messages.get(Challenges.class,"no_healing_hell"),
				"_-_ Made debuffs duration random\n" +
						"_-_ Added more possible debuffs.\n" +
						"_-_ Increased debuff applying delay on lower intoxication levels"));
		
		changes = new ChangeInfo("New content",false,null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);
		
		changes.addButton(new ChangeButton(new Ankh(){
			@Override
			public ItemSprite.Glowing glowing() {
				return new ItemSprite.Glowing(0xFF0000);
			}
		},Messages.get(Challenges.class,"resurrection_hell2"),Messages.get(Challenges.class,"resurrection_hell2_desc")));
		
		changes.addButton(new ChangeButton(new SwarmSprite(),Messages.get(Challenges.class,"horde_hell2"),Messages.get(Challenges.class,"horde_hell2_desc")));
		changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.POTION_AMBER),Messages.get(Challenges.class,"no_healing_hell2"),Messages.get(Challenges.class,"no_healing_hell2_desc")));
		changes.addButton(new ChangeButton(Icons.CHALLENGE_OFF.get(),Messages.get(Challenges.class,"rook"),Messages.get(Challenges.class,"rook_desc")));
		
	}
	public static void add_v0_2_0_Changes(ArrayList<ChangeInfo> changeInfos){
		ChangeInfo changes = new ChangeInfo( "v0.2.0", true, "");
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);
		
		changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"),false,null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);
		
		changes.addButton( new ChangeButton(Icons.get(Icons.INFO), "Developer Commentary",
				"_-_ Released 26.10.2019\n" +
						"_-_ 75 days after v0.1.0" +
						"\n" +
						"Dev commentary will be added here in the future."));
		
		changes.addButton( new ChangeButton(Icons.get(Icons.CHALLENGE_HELL), "Challenges",
				"_-_ Added nightmare version to 9 challenges"
		));
		changes.addButton( new ChangeButton(BadgeBanner.image(Badges.Badge.CHAMPION_4.image),"Badges",
				"Added 4 new badges for winning with 9 challenges + 1 nightmare, 12 challenges + 3 nightmare, 15 challenges + 6 nightmare and 18 challenges + 9 nightmare"
				));
		
		changes = new ChangeInfo("Challenges",false,null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);
		
		changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.POTION_AMBER),Messages.get(Challenges.class,"no_healing_hell"),Messages.get(Challenges.class,"no_healing_hell_desc")));
		changes.addButton(new ChangeButton(new RotHeartSprite(),Messages.get(Challenges.class,"swarm_intelligence_hell"),Messages.get(Challenges.class,"swarm_intelligence_hell_desc")));
		changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.TORCH),Messages.get(Challenges.class,"darkness_hell"),Messages.get(Challenges.class,"darkness_hell_desc")));
		changes.addButton(new ChangeButton(Icons.CHALLENGE_HELL.get(),Messages.get(Challenges.class,"amnesia_hell"),Messages.get(Challenges.class,"amnesia_hell_desc")));
		changes.addButton(new ChangeButton(new SwarmSprite(),Messages.get(Challenges.class,"horde_hell"),Messages.get(Challenges.class,"horde_hell_desc")));
		changes.addButton(new ChangeButton(new Image(Assets.Interfaces.BUFFS_LARGE, 224, 32, 16, 16),Messages.get(Challenges.class,"countdown_hell"),Messages.get(Challenges.class,"countdown_hell_desc")));
		changes.addButton(new ChangeButton(new AlbinoSprite(),Messages.get(Challenges.class,"mutagen_hell"),Messages.get(Challenges.class,"mutagen_hell_desc")));
		changes.addButton(new ChangeButton(new Ankh().bless(),Messages.get(Challenges.class,"resurrection_hell"),Messages.get(Challenges.class,"resurrection_hell_desc")));
		changes.addButton(new ChangeButton(new Image(Assets.Environment.TERRAIN_FEATURES, 112, 96, 16, 16),Messages.get(Challenges.class,"extreme_caution_hell"),Messages.get(Challenges.class,"extreme_caution_hell_desc")));
	}
	
	public static void add_v0_1_0_Changes(ArrayList<ChangeInfo> changeInfos){
		ChangeInfo changes = new ChangeInfo( "v0.1.0 - release", true, "");
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);
		
		changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"),false,null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);
		
		changes.addButton( new ChangeButton(Icons.get(Icons.INFO), "Developer Commentary",
				"_-_ Released August 12th, 2019\n" +
						"_-_ First release\n" +
						"_-_ 25 days after Shattered Pixel Dungeon v0.7.4" +
						"\n" +
						"Dev commentary will be added here in the future."));
		
		changes.addButton( new ChangeButton(Icons.get(Icons.CHALLENGE_ON), "Challenges",
				"_-_ Added 11 new challenges\n" +
						"_-_ Challenges are unlocked by default"));
		changes.addButton(new ChangeButton(new Image(Assets.Sprites.HUNTRESS, 0, 90, 12, 15),"Classes","All hero classes are unlocked by default"));
		
		changes = new ChangeInfo("Challenges",false,null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);
		
		changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_OFF),Messages.get(Challenges.class,"amnesia"),Messages.get(Challenges.class,"amnesia_desc")));
		changes.addButton(new ChangeButton(new Embers(),Messages.get(Challenges.class,"cursed"),Messages.get(Challenges.class,"cursed_desc")));
		changes.addButton(new ChangeButton(new BlackjackkeeperSprite(),Messages.get(Challenges.class,"blackjack"),Messages.get(Challenges.class,"blackjack_desc")));
		changes.addButton(new ChangeButton(new SwarmSprite(),Messages.get(Challenges.class,"horde"),Messages.get(Challenges.class,"horde_desc")));
		changes.addButton(new ChangeButton(new Image(Assets.Interfaces.BUFFS_LARGE, 224, 32, 16, 16),Messages.get(Challenges.class,"countdown"),Messages.get(Challenges.class,"countdown_desc")));
		changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_OFF),Messages.get(Challenges.class,"analgesia"),Messages.get(Challenges.class,"analgesia_desc")));
		changes.addButton(new ChangeButton(new Image(Assets.Sprites.ITEM_ICONS, 56, 16, 7, 7),Messages.get(Challenges.class,"big_levels"),Messages.get(Challenges.class,"big_levels_desc")));
		changes.addButton(new ChangeButton(new AlbinoSprite(),Messages.get(Challenges.class,"mutagen"),Messages.get(Challenges.class,"mutagen_desc")));
		changes.addButton(new ChangeButton(new Ankh(),Messages.get(Challenges.class,"resurrection"),Messages.get(Challenges.class,"resurrection_desc")));
		changes.addButton(new ChangeButton(new Image(Assets.Environment.TERRAIN_FEATURES, 112, 0, 16, 16),Messages.get(Challenges.class,"extreme_caution"),Messages.get(Challenges.class,"extreme_caution_desc")));
		changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_OFF),Messages.get(Challenges.class,"extermination"),Messages.get(Challenges.class,"extermination_desc")));
	}
	
	
}
