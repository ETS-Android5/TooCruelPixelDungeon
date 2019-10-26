package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SacrificialParticle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.PointF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RacingTheDeath extends Buff {
	{
		type = buffType.NEUTRAL;
		resetTrail();
	}
	
	private List<Integer> trailCells;
	
	private ArrayList<Image> trail;
	
	private int lastDepth = -1;
	
	private static final int TRAIL_LENGTH = 10;
	
	@Override
	public void detach() {
		//This buff can't be detached
	}
	
	@Override
	public boolean attachTo(Char target) {
		if(target instanceof Hero) {
			lastDepth=Dungeon.depth;
			return super.attachTo(target);
		}
		return false;
	}
	
	@Override
	public boolean act() {
		
		if(Dungeon.depth!=lastDepth){
			resetTrail();
			lastDepth=Dungeon.depth;
		}
		
		int baseDMG = (Statistics.deepestFloor/5+1)*3;
		
		trailCells.remove(TRAIL_LENGTH - 1);
		
		if(trailCells.contains(target.pos) && trailCells.indexOf(target.pos)!=0){
			
			if(trailCells.get(3)==target.pos){
				target.damage(baseDMG*4, Countdown.class);
				burst(target.pos,30);
			} else {
				target.damage(baseDMG*2, Countdown.class);
				burst(target.pos,15);
			}
		} else {
			int frequency = Collections.frequency(trailCells,target.pos);
			int damage = (frequency-3)*baseDMG*3/2;
			if (damage>0) {
				target.damage(damage, Countdown.class);
				burst(target.pos,7*(frequency-3));
			}
		}
		
		trailCells.add(0, target.pos);
		addTrailSegment(target.pos);
		
		updateTrail();
		
		spend(TICK);
		return true;
	}
	
	@Override
	public void fx(boolean on) {
		if(on){
			createTrail();
		} else {
			eraseTrail();
		}
	}
	
	Emitter.Factory particles = PurpleParticle.BURST;
	
	private void burst(int pos, int amount){
		if(trail==null)return;
		int i = trailCells.indexOf(pos);
		if(i==-1)return;
		
		Image sprite = trail.get(i);
		
		Emitter emitter = GameScene.emitter();
		emitter.pos( new PointF( sprite.x + sprite.width / 2, sprite.y + sprite.height / 2 ) );
		
		emitter.burst(particles,amount);
		
		Sample.INSTANCE.play(Assets.SND_LIGHTNING);
		
	}
	
	private void resetTrail(){
		trailCells = new ArrayList<>();
		for (int i=0;i<TRAIL_LENGTH;i++){
			trailCells.add(0);
		}
		//to make buff create first piece of trail right away
		spend(-1);
		//To make sure that buff wont proc multiple times in function called more than once
		postpone(-1);
		
		createTrail();
	}
	
	private void updateTrail(){
		if(trail==null) return;
		
		for(int i=0;i<TRAIL_LENGTH;i++){
			Image segment = trail.get(i);
			int pos = trailCells.get(i);
			
			segment.invert();
			
			segment.alpha(.3f);
			if(i==4) {
				segment.alpha(.9f);
			}
			
			if(Actor.findChar(pos)!=null)segment.alpha(segment.alpha()/2);
			
			segment.visible=pos!=Dungeon.hero.pos && Dungeon.level.visited[pos];
			
		}
	}
	
	private void createTrail(){
		if(trail!=null)
			eraseTrail();
		
		if(Dungeon.depth!=lastDepth){
			lastDepth=Dungeon.depth;
			resetTrail();
			return;
		}
		trail = new ArrayList<>();
		
		for(int i=0;i<TRAIL_LENGTH;i++){
			trail.add(new Image());
		}
		for(int i=0;i<TRAIL_LENGTH;i++){
			int pos = trailCells.get(i);
			if(pos!=0){
				setTrailSegment(i,pos);
			}
		}
		
		updateTrail();
	}
	
	private void addTrailSegment(int pos){
		if (trail==null)return;
		
		Image segment = HeroSprite.avatar(Dungeon.hero.heroClass,Dungeon.hero.tier());
		
		trail.get(TRAIL_LENGTH-1).killAndErase();
		trail.remove(TRAIL_LENGTH-1);
		
		segment.point(worldToCamera(pos,segment));
		
		GameScene.effect(segment);
		trail.add(0,segment);
	}
	
	private void setTrailSegment(int i, int pos){
		if(trail==null)return;
		
		Image segment = HeroSprite.avatar(Dungeon.hero.heroClass,Dungeon.hero.tier());
		
		trail.get(i).killAndErase();
		trail.remove(i);
		
		segment.point(worldToCamera(pos,segment));
		
		GameScene.effect(segment);
		trail.add(i,segment);
	}
	
	private void eraseTrail(){
		for(Image img : trail){
			img.killAndErase();
		}
		trail = null;
	}
	
	public PointF worldToCamera(int cell, Image segment ) {
		
		final int csize = DungeonTilemap.SIZE;
		
		return new PointF(
				PixelScene.align(Camera.main, ((cell % Dungeon.level.width()) + 0.5f) * csize - segment.width * 0.5f),
				PixelScene.align(Camera.main, ((cell / Dungeon.level.width()) + 1.0f) * csize - segment.height - csize * (6 / 16f))
		);
	}
	
	private static final String TRAIL_CELLS = "trail_cells";
	private static final String DEPTH = "last_depth";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		
		int[] ret = new int[TRAIL_LENGTH];
		for (int i = 0; i < TRAIL_LENGTH; i++) {
			ret[i] = trailCells.get(i);
		}
		
		bundle.put(TRAIL_CELLS, ret);
		
		bundle.put(DEPTH,lastDepth);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		
		int[] cells = bundle.getIntArray(TRAIL_CELLS);
		trailCells = new ArrayList<>();
		for(int i=0;i<TRAIL_LENGTH;i++){
			if(i>=cells.length){
				trailCells.add(0);
				continue;
			}
			trailCells.add(cells[i]);
		}
		
		lastDepth=bundle.getInt(DEPTH);
	}
}
