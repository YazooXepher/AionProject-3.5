package com.aionemu.gameserver.model.templates.spawns;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.siege.SiegeModType;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.templates.spawns.riftspawns.RiftSpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.vortexspawns.VortexSpawnTemplate;
import com.aionemu.gameserver.model.vortex.VortexStateType;
import com.aionemu.gameserver.spawnengine.SpawnHandlerType;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xTz
 * @modified Rolandas
 */
public class SpawnGroup2 {

	private static final Logger log = LoggerFactory.getLogger(SpawnGroup2.class);
	
	private int worldId;
	private int npcId;
	private int pool;
	private byte difficultId;
	private TemporarySpawn temporarySpawn;
	private int respawnTime;
	private SpawnHandlerType handlerType;
	private List<SpawnTemplate> spots = new ArrayList<SpawnTemplate>();

	public SpawnGroup2(int worldId, Spawn spawn) {
		this.worldId = worldId;
		initializing(spawn);
		for (SpawnSpotTemplate template : spawn.getSpawnSpotTemplates()) {
			SpawnTemplate spawnTemplate = new SpawnTemplate(this, template);
			if (spawn.isEventSpawn())
				spawnTemplate.setEventTemplate(spawn.getEventTemplate());
			spots.add(spawnTemplate);
		}
	}

	public SpawnGroup2(int worldId, Spawn spawn, int id) {
		this.worldId = worldId;
		initializing(spawn);
		for (SpawnSpotTemplate template : spawn.getSpawnSpotTemplates()) {
			RiftSpawnTemplate spawnTemplate = new RiftSpawnTemplate(this, template);
			spawnTemplate.setId(id);
			spots.add(spawnTemplate);
		}
	}

	public SpawnGroup2(int worldId, Spawn spawn, int id, VortexStateType type) {
		this.worldId = worldId;
		initializing(spawn);
		for (SpawnSpotTemplate template : spawn.getSpawnSpotTemplates()) {
			VortexSpawnTemplate spawnTemplate = new VortexSpawnTemplate(this, template);
			spawnTemplate.setId(id);
			spawnTemplate.setStateType(type);
			spots.add(spawnTemplate);
		}
	}

	public SpawnGroup2(int worldId, Spawn spawn, int siegeId, SiegeRace race, SiegeModType mod) {
		this.worldId = worldId;
		initializing(spawn);
		for (SpawnSpotTemplate template : spawn.getSpawnSpotTemplates()) {
			SiegeSpawnTemplate spawnTemplate = new SiegeSpawnTemplate(this, template);
			spawnTemplate.setSiegeId(siegeId);
			spawnTemplate.setSiegeRace(race);
			spawnTemplate.setSiegeModType(mod);
			spots.add(spawnTemplate);
		}
	}

	private void initializing(Spawn spawn) {
		temporarySpawn = spawn.getTemporarySpawn();
		respawnTime = spawn.getRespawnTime();
		pool = spawn.getPool();
		npcId = spawn.getNpcId();
		handlerType = spawn.getSpawnHandlerType();
		difficultId = spawn.getDifficultId();
	}

	public SpawnGroup2(int worldId, int npcId) {
		this.worldId = worldId;
		this.npcId = npcId;
	}

	public List<SpawnTemplate> getSpawnTemplates() {
		return spots;
	}

	public void addSpawnTemplate(SpawnTemplate spawnTemplate) {
		spots.add(spawnTemplate);
	}

	public int getWorldId() {
		return worldId;
	}

	public int getNpcId() {
		return npcId;
	}

	public TemporarySpawn geTemporarySpawn() {
		return temporarySpawn;
	}

	public int getPool() {
		return pool;
	}

	public boolean hasPool() {
		return pool > 0;
	}

	public int getRespawnTime() {
		return respawnTime;
	}

	public void setRespawnTime(int respawnTime) {
		this.respawnTime = respawnTime;
	}

	public boolean isTemporarySpawn() {
		return temporarySpawn != null;
	}

	public SpawnHandlerType getHandlerType() {
		return handlerType;
	}

	public synchronized SpawnTemplate getRndTemplate() {
		List<SpawnTemplate> templates = new ArrayList<SpawnTemplate>();
		for (SpawnTemplate template : spots) {
			if (!template.isUsed()) {
				templates.add(template);
			}
		}
		if(templates.size() == 0) {
			log.warn("Pool size more then spots, npcId: " + npcId + ", worldId: " + worldId);
			return null;
		}
		SpawnTemplate spawnTemplate = templates.get(Rnd.get(0, templates.size() - 1));
		spawnTemplate.setUse(true);
		return spawnTemplate;
	}

	public byte getDifficultId() {
		return difficultId;
	}
}