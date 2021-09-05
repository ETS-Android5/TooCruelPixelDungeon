/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ISwarm;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.Deck;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public abstract class ChampionEnemy extends Buff implements DamageAmplificationBuff, AttackAmplificationBuff {

    protected int color;

    {
        type = buffType.POSITIVE;
    }

    {
        immunities.add(Corruption.class);
    }

    public static String description(Class<? extends ChampionEnemy> cl) {
        String desc = Messages.get(cl, "cdesc");
        if (desc.equals(Messages.NOT_FOUND)) desc = Messages.get(cl, "desc");
        return String.format("_%s (%s):_ %s",
                Messages.get(cl, "name"),
                Messages.get(cl, "color"),
                desc
        );
    }

    public static String description(Class<? extends ChampionEnemy>[] classes) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Class<? extends ChampionEnemy> cl : classes) {
            if (first) {
                first = false;
            } else {
                sb.append("\n");
            }
            sb.append(description(cl));
        }
        return sb.toString();
    }

    public static void rollForChampion(Mob m, HashSet<Mob> existing) {

        Dungeon.mobsToChampion++;

        int existingChamps = 0;
        if (Challenges.ELITE_CHAMPIONS.enabled()) {
            for (Mob e : existing) {
                if (!e.buffs(ChampionEnemy.class).isEmpty()) {
                    existingChamps++;
                }
                //elite champions counts as 2
                if (!e.buffs(EliteChampion.class).isEmpty()) {
                    existingChamps++;
                }
            }
        }

        int added = 0;

        // Every 8'th enemy is a champion.
        int nthChampion = 8;


        if (Challenges.ELITE_CHAMPIONS.enabled()) {
            //100% champion spawn chance when no champions are left while t2 is enabled, otherwise every 5'th enemy is a champion
            nthChampion = existingChamps == 0 ? 1 : 5;
        }

        boolean elite = false;

        if (Dungeon.mobsToChampion % nthChampion == 0) {
            //Every 3'rd champion is an elite champion
            if (Challenges.ELITE_CHAMPIONS.enabled() && Dungeon.mobsToChampion % 3 == 0) {
                Buff.append(m, randomElite());
                elite = true;
                added += 2;
                //Every elite champion is also a regular champion at t3
                if (Challenges.DUNGEON_OF_CHAMPIONS.enabled()) {
                    Buff.append(m, randomChampion());
                    added++;
                }
            } else {
                Buff.append(m, randomChampion());
                added++;
            }
            m.state = m.WANDERING;
        }

        //go crazy at t3
        if (Challenges.DUNGEON_OF_CHAMPIONS.enabled()) {
            while (added < 8 && Random.Int(3 + added) <= 0) {
                if (Random.Int(2) == 0) {
                    Buff.append(m, randomElite());
                    elite = true;
                    added += 2;
                } else {
                    Buff.append(m, randomChampion());
                    added++;
                }
                m.state = m.WANDERING;
            }
        }

        if (elite) {
            Buff.affect(m, Revealing.class, 2f);
        }
    }

    public static Class<? extends ChampionEnemy> randomChampion() {
        return Dungeon.level.extraData.normalChampionsDeck.get();
    }

    public static Class<? extends ChampionEnemy> randomElite() {
        return Dungeon.level.extraData.eliteChampionsDeck.get();
    }

    @Override
    public int icon() {
        return BuffIndicator.CORRUPT;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(color);
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.aura(getClass(), color, 1, true);
        else target.sprite.clearAura(getClass());
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }

    public void onAttackProc(Char enemy) {

    }

    public void onDamageProc(int damage) {

    }

    public boolean canAttackWithExtraReach(Char enemy) {
        return false;
    }


    @Override
    public float damageMultiplier() {
        return damageTakenFactor();
    }

    @Override
    public int damageFactor(int dmg) {
        return (int) (dmg * meleeDamageFactor());
    }

    protected float meleeDamageFactor() {
        return 1f;
    }

    protected float damageTakenFactor() {
        return 1f;
    }

    public float evasionAndAccuracyFactor() {
        return 1f;
    }

    public static class NormalChampionsDeck extends Deck<Class<? extends ChampionEnemy>> {
        {
            filler().defaultWeight(3f)
                    .add(Blazing.class)
                    .add(Projecting.class)
                    .add(AntiMagic.class)
                    .add(Giant.class)
                    .add(Blessed.class)
                    .add(Growing.class)
                    .add(Flowing.class)
                    .add(Stone.class)
                    .apply(new Class[0]);
        }
    }

    public static class EliteChampionsDeck extends Deck<Class<? extends ChampionEnemy>> {
        {
            filler().defaultWeight(2f)
                    .add(Sacrificial.class)
                    .add(Timebending.class)
                    .add(Restoring.class)
                    .add(Infectious.class)
                    .add(Toxic.class)
                    .add(Seeking.class)
                    .add(Swarming.class)

                    // Summoning and Citadel are less common
                    .put(Summoning.class, 1f)
                    .put(Citadel.class, 1f)
                    .apply(new Class[0]);
        }
    }

    //region Regular
    public static class Blazing extends ChampionEnemy {

        {
            color = 0xFF8800;
        }

        {
            immunities.add(Burning.class);
        }

        @Override
        public void onAttackProc(Char enemy) {
            Buff.affect(enemy, Burning.class).reignite(enemy);
        }

        @Override
        public void detach() {
            for (int i : PathFinder.NEIGHBOURS9) {
                if (!Dungeon.level.solid[target.pos() + i]) {
                    GameScene.add(Blob.seed(target.pos() + i, 2, Fire.class));
                }
            }
            super.detach();
        }

        @Override
        public float meleeDamageFactor() {
            return 1.25f;
        }
    }

    public static class Projecting extends ChampionEnemy {

        {
            color = 0x8800FF;
        }

        @Override
        public float meleeDamageFactor() {
            return 1.25f;
        }

        @Override
        public boolean canAttackWithExtraReach(Char enemy) {
            return target.fieldOfView[enemy.pos()]; //if it can see it, it can attack it.
        }
    }

    public static class AntiMagic extends ChampionEnemy {

        {
            color = 0x00FF00;
        }

        {
            immunities.addAll(com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic.RESISTS);
        }

        @Override
        public float damageTakenFactor() {
            return 0.75f;
        }

    }

    public static class Giant extends ChampionEnemy {

        {
            color = 0x0088FF;
        }

        @Override
        public float damageTakenFactor() {
            return 0.25f;
        }

        @Override
        public void modifyProperties(HashSet<Char.Property> properties) {
            properties.add(Char.Property.LARGE);
        }

        @Override
        public boolean canAttackWithExtraReach(Char enemy) {
            if (Dungeon.level.distance(target.pos(), enemy.pos()) > 2) {
                return false;
            } else {
                boolean[] passable = BArray.not(Dungeon.level.solid, null);
                for (Char ch : Actor.chars()) {
                    if (ch != target) passable[ch.pos()] = false;
                }

                PathFinder.buildDistanceMap(enemy.pos(), passable, 2);

                return PathFinder.distance[target.pos()] <= 2;
            }
        }
    }

    public static class Blessed extends ChampionEnemy {

        {
            color = 0xFFFF00;
        }

        @Override
        public float evasionAndAccuracyFactor() {
            return 3f;
        }
    }

    public static class Growing extends ChampionEnemy {

        private static final String MULTIPLIER = "multiplier";
        private float multiplier = 1.19f;

        {
            color = 0xFF0000;
        }

        @Override
        public boolean act() {
            multiplier += 0.01f;
            spend(3 * TICK);
            return true;
        }

        @Override
        public float meleeDamageFactor() {
            return multiplier;
        }

        @Override
        public float damageTakenFactor() {
            return 1f / multiplier;
        }

        @Override
        public float evasionAndAccuracyFactor() {
            return multiplier;
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", (int) (100 * (multiplier - 1)), (int) (100 * (1 - 1f / multiplier)));
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(MULTIPLIER, multiplier);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            multiplier = bundle.getFloat(MULTIPLIER);
        }
    }

    public static class Flowing extends ChampionEnemy {
        {
            color = 0xb7f5ff;
        }

        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo(target)) {
                ((Mob) target).instantWaterMovement = true;
                return true;
            }
            return false;
        }

        @Override
        public void detach() {
            ((Mob) target).instantWaterMovement = false;
            super.detach();
        }
    }

    public static class Stone extends ChampionEnemy {
        {
            color = 0x727272;
        }

        @Override
        public float damageTakenFactor() {
            return Math.max(0.01f, (target.HP * 1f / target.HT));
        }
    }
    //endregion

    //region Elite
    public static class EliteChampion extends ChampionEnemy {

        public static final String GUARDIANS_COOLDOWN = "guardians_cooldown";
        private static final int GUARDS_SUMMON_COOLDOWN = 30;
        public int guardiansCooldown = 0;

        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo(target)) {
                target.viewDistance = 8;
                return true;
            }
            return false;
        }

        protected HashSet<Mob> mobsInFov() {
            HashSet<Mob> mobs = new HashSet<>();
            if (target.fieldOfView == null) {
                target.fieldOfView = new boolean[Dungeon.level.length()];
                Dungeon.level.updateFieldOfView(target, target.fieldOfView);
            }
            for (Mob m : Dungeon.level.mobs) {
                if (m instanceof NPC) continue;
                if (m.alignment == Char.Alignment.ALLY) continue;
                if (target.fieldOfView[m.pos()]) {
                    mobs.add(m);
                }
            }
            return mobs;
        }

        protected int guardsSummonCooldown() {
            return GUARDS_SUMMON_COOLDOWN;
        }

        protected int guardsNumber() {
            if (Challenges.DUNGEON_OF_CHAMPIONS.enabled()) return 2;
            return 1;
        }

        @Override
        public boolean act() {
            if (target instanceof Mob) {
                Mob mob = (Mob) target;
                if (mob.state == mob.HUNTING) {
                    if (guardiansCooldown <= 0) {
                        SummoningTrap.summonMobs(target.pos(), guardsNumber(), 3);
                    }
                    guardiansCooldown = Math.max(guardsSummonCooldown(), guardiansCooldown);
                } else {
                    if (Challenges.DUNGEON_OF_CHAMPIONS.enabled()) {
                        if (mob.state == mob.WANDERING) {
                            guardiansCooldown--;
                        }
                    }
                }
            }
            spend(1f);
            return true;
        }

        @Override
        public void fx(boolean on) {
            if (on) {
                target.sprite.aura(getClass(), color, 1.5f, false);
            } else {
                target.sprite.clearAura(getClass());
            }
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(GUARDIANS_COOLDOWN, guardiansCooldown);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            guardiansCooldown = bundle.getInt(GUARDIANS_COOLDOWN);
        }
    }

    public static class Sacrificial extends EliteChampion {
        {
            color = 0xFCC200;
        }

        @Override
        public float damageTakenFactor() {
            return 4;
        }

        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo(target)) {
                if (Random.Int(3) == 0) {
                    Buff.affect(target, ChampionEnemy.randomChampion());
                } else {
                    Buff.affect(target, ChampionEnemy.randomElite());
                }
                return true;
            }
            return false;
        }

        @Override
        public void onDeathProc(Object src) {
            super.onDeathProc(src);

            HashSet<Class<? extends ChampionEnemy>> buffs = new HashSet<>();

            for (ChampionEnemy buff : target.buffs(ChampionEnemy.class)) {
                if (buff != this) {
                    buffs.add(buff.getClass());
                }
            }

            for (Mob mob : mobsInFov()) {
                PotionOfHealing.cure(mob);
                Buff.detach(mob, Paralysis.class);

                Corruption cor = mob.buff(Corruption.class);
                if (cor != null) cor.detach();

                mob.HP = mob.HT;
                new Flare(8, 32).color(0xFFFF66, true).show(mob.sprite, 2f);
                CellEmitter.get(mob.pos()).start(Speck.factory(Speck.LIGHT), 0.2f, 3);

                for (Class<? extends ChampionEnemy> buff : buffs) {
                    ChampionEnemy enemy = Buff.affect(mob, buff);
                    if (enemy instanceof EliteChampion) {
                        //Newly-spawned elites can't use guardians
                        ((EliteChampion) enemy).guardiansCooldown = Integer.MAX_VALUE;
                    }
                }
            }
        }
    }

    public static class Summoning extends EliteChampion {
        private static final String SUMMONED = "summoned";
        private static final String ALARMED = "alarmed";
        private int timer = 0;
        private boolean alarmed = false;

        {
            color = 0x4B0082;
        }

        @Override
        public float damageTakenFactor() {
            return 0.25f;
        }

        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo(target)) {
                if (target instanceof Mob) {
                    ((Mob) target).state = ((Mob) target).SLEEPING;
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean act() {
            if (++timer % 15 == 0 && target.fieldOfView != null) {
                HashSet<Integer> suitableCells = new HashSet<>();
                for (int i = 0; i < target.fieldOfView.length; i++) {
                    if (target.fieldOfView[i] &&
                            (Dungeon.level.passable[i] || Dungeon.level.avoid[i]) && !Dungeon.level.pit[i] &&
                            Actor.findChar(i) == null) suitableCells.add(i);
                }
                if (suitableCells.size() > 0) {
                    int cell = Random.element(suitableCells);
                    timer++;

                    Mob mob = SummoningTrap.summonMob(cell, 1f);
                    mob.state = mob.SLEEPING;

                    if (timer % 150 == 0) {
                        Buff.affect(mob, ChampionEnemy.randomElite());
                    } else if (timer % 50 == 0) {
                        Buff.affect(mob, ChampionEnemy.randomChampion());
                    }

                    SummoningTrap.placeMob(Collections.singletonList(mob));
                }
            }
            if (!alarmed) {
                for (Mob mob : mobsInFov()) {
                    if (mob.state == mob.WANDERING) {
                        mob.state = mob.SLEEPING;
                    } else if (mob.state == mob.HUNTING) {
                        alert();
                    }
                }

                if (target instanceof Mob) {
                    Mob mob = (Mob) target;
                    if (mob.state == mob.WANDERING) {
                        mob.state = mob.SLEEPING;
                    } else if (mob.state == mob.HUNTING) {
                        alert();
                    }
                }
            }

            return super.act();
        }

        private void alert() {
            for (Mob mob : mobsInFov()) {
                mob.beckon(target.pos());
            }
            alarmed = true;
        }

        @Override
        public void modifyProperties(HashSet<Char.Property> properties) {
            properties.add(Char.Property.ALWAYS_VISIBLE);
            properties.add(Char.Property.IMMOVABLE);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(SUMMONED, timer);
            bundle.put(ALARMED, alarmed);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            timer = bundle.getInt(SUMMONED);
            alarmed = bundle.getBoolean(ALARMED);
        }
    }

    public static class Timebending extends EliteChampion {
        {
            color = 0x00879F;
        }

        @Override
        public boolean act() {
            if (target.fieldOfView != null) {
                for (Char character : Actor.chars()) {
                    if (target.fieldOfView[character.pos()]) {
                        if (character.alignment == Char.Alignment.ALLY) {
                            Buff.prolong(character, Sluggish.class, 1.1f);
                        } else {
                            Buff.prolong(character, Acceleration.class, 1.1f);
                        }
                    }
                }
            }
            return super.act();
        }

        @Override
        public void onAttackProc(Char enemy) {
            Buff.prolong(enemy, Slow.class, 2f);
        }

        public static class Acceleration extends TimescaleBuff {
            @Override
            public float speedFactor() {
                return 1.25f;
            }

            @Override
            public void fx(boolean on) {
                if (on) {
                    target.sprite.aura(getClass(), 0x009e86, 0.5f, true);
                } else {
                    target.sprite.clearAura(getClass());
                }
            }
        }

        public static class Sluggish extends TimescaleBuff {
            {
                type = buffType.NEGATIVE;
            }

            @Override
            public int icon() {
                return BuffIndicator.TIME;
            }

            public String toString() {
                return Messages.get(this, "name");
            }

            public String desc() {
                return Messages.get(this, "desc");
            }

            @Override
            public void tintIcon(Image icon) {
                icon.hardlight(0, 1, 2);
            }

            @Override
            public float speedFactor() {
                return 0.75f;
            }
        }
    }

    public static class Restoring extends EliteChampion {
        {
            color = 0xffffff;
        }

        @Override
        public float damageTakenFactor() {
            return 1.25f;
        }

        @Override
        public HashSet<Class> immunities() {
            HashSet<Class> immunities = super.immunities();
            immunities.add(Ascension.ForcedAscension.class);
            return immunities;
        }

        @Override
        public boolean act() {
            for (Mob mob : mobsInFov()) {
                Buff.prolong(mob, Ascension.ForcedAscension.class, 1.1f);
            }
            return super.act();
        }
    }

    public static class Infectious extends EliteChampion {
        {
            color = 0x663100;
        }

        @Override
        public float damageTakenFactor() {
            return 0.8f;
        }

        @Override
        public float meleeDamageFactor() {
            return 1.2f;
        }

        @Override
        public boolean act() {
            for (Mob mob : mobsInFov()) {
                Buff.affect(mob, Infectious.class);
            }
            return super.act();
        }

        @Override
        protected int guardsNumber() {
            return 0;
        }

        @Override
        public void fx(boolean on) {
            if (on) target.sprite.aura(getClass(), color, 1, false);
            else target.sprite.clearAura(getClass());
        }
    }

    public static class Toxic extends EliteChampion {
        {
            color = 0x808000;
        }

        @Override
        public void onAttackProc(Char enemy) {
            Buff.affect(enemy, Intoxication.class).extend(Intoxication.POION_INTOXICATION / 2f);
        }

        private void seed(int pos, int amount) {
            GameScene.add(Blob.seed(pos, amount, ToxicGas.class));
            GameScene.add(Blob.seed(pos, amount, BlobImmunityGas.class));
        }

        @Override
        public void onDeathProc(Object src) {
            seed(target.pos(), 20);
        }

        @Override
        public void onDamageProc(int damage) {
            seed(target.pos(), 8);
        }

        @Override
        public boolean act() {
            for (Mob mob : mobsInFov()) {
                seed(mob.pos(), 4);
            }
            return super.act();
        }

        public static class BlobImmunityGas extends Blob {
            {
                //Act before other blobs to give defence before other blobs proc
                actPriority = BLOB_PRIO + 1;
            }

            @Override
            protected void evolve() {
                super.evolve();

                Char ch;
                int cell;

                for (int i = area.left; i < area.right; i++) {
                    for (int j = area.top; j < area.bottom; j++) {
                        cell = i + j * Dungeon.level.width();
                        if (cur[cell] > 0 && (ch = Actor.findChar(cell)) != null && ch.alignment != Char.Alignment.ALLY) {
                            Buff.prolong(ch, BlobImmunity.class, 10);
                        }
                    }
                }
            }
        }
    }

    public static class Seeking extends EliteChampion {
        {
            color = 0x2900FF;
        }

        @Override
        public boolean act() {
            Mob targ = ((Mob) target);
            if (!target.fieldOfView[Dungeon.hero.pos()]) {
                if (targ.state != targ.HUNTING)
                    targ.state = targ.HUNTING;
                targ.beckon(Dungeon.hero.pos());
                for (Mob mob : Dungeon.level.mobs) {
                    if (Dungeon.level.distance(target.pos(), mob.pos()) <= 1) {
                        mob.beckon(Dungeon.hero.pos());
                        if (mob.state != mob.HUNTING) {
                            mob.state = mob.HUNTING;
                        }
                    }
                }
                return super.act();
            } else {
                super.act();
                Class<? extends Buff> buff = randomChampion();
                for (Mob mob : mobsInFov()) {
                    Buff.append(mob, buff);
                    Buff.affect(mob, Stamina.class, 8);
                    detach();
                }
                return true;
            }
        }
    }

    public static class Swarming extends EliteChampion implements ISwarm {
        private static final String GENERATION = "generation";
        int generation = 0;

        {
            color = 0xbfba72;
        }

        @Override
        public void onDamageProc(int damage) {
            if (target.HP >= damage + 2) {
                ArrayList<Integer> candidates = new ArrayList<>();

                int[] neighbours = PathFinder.NEIGHBOURS4;
                for (int n : neighbours) {
                    if (!Dungeon.level.solid[n] && Actor.findChar(n) == null
                            && (!target.properties().contains(Char.Property.LARGE) || Dungeon.level.openSpace[n])) {
                        candidates.add(n);
                    }
                }

                if (candidates.size() > 0) {
                    Mob clone = ISwarm.split((Mob) target,
                            (Mob) Reflection.newInstance(target.getClass()),
                            this,
                            Swarming.class
                    );
                    clone.HP = (target.HP - damage) / 2;
                    clone.pos(Random.element(candidates));
                    clone.state = clone.HUNTING;

                    Dungeon.level.occupyCell(clone);

                    GameScene.add(clone, SPLIT_DELAY);
                    Actor.addDelayed(new Pushing(clone, target.pos(), clone.pos()), -1);

                    target.HP -= clone.HP;
                }
            }
        }

        @Override
        public int generation() {
            return generation;
        }

        @Override
        public void setGeneration(int generation) {
            this.generation = generation;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(GENERATION, generation);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            generation = bundle.getInt(GENERATION);
            if (generation > 0) ((Mob) target).EXP = 0;
        }
    }

    public static class Citadel extends EliteChampion {
        {
            color = 0XFFF2AA;
        }

        @Override
        protected int guardsNumber() {
            return super.guardsNumber() + 7;
        }

        @Override
        protected int guardsSummonCooldown() {
            return super.guardsSummonCooldown() * 5;
        }

        @Override
        public void modifyProperties(HashSet<Char.Property> properties) {
            properties.add(Char.Property.IMMOVABLE);
        }

        @Override
        public float damageTakenFactor() {
            return 0.25f;
        }

        @Override
        public boolean act() {
            super.act();
            Buff.affect(target, Roots.class, Roots.DURATION);
            for (Mob mob : mobsInFov()) {
                if (mob.buff(Citadel.class) == null) {
                    Buff.prolong(mob, Invulnerability.class, 1.1f);
                }
            }
            return true;
        }
    }
    //endregion
}
