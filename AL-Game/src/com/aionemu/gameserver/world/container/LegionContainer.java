package com.aionemu.gameserver.world.container;

import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.world.exceptions.DuplicateAionObjectException;
import javolution.util.FastMap;

import java.util.Iterator;
import java.util.Map;

/**
 * Container for storing Legions by legionId and name.
 * 
 * @author Simple
 */
public class LegionContainer implements Iterable<Legion> {

	/**
	 * Map<LegionId, Legion>
	 */
	private final Map<Integer, Legion> legionsById = new FastMap<Integer, Legion>().shared();
	/**
	 * Map<LegionName, Legion>
	 */
	private final Map<String, Legion> legionsByName = new FastMap<String, Legion>().shared();

	/**
	 * Add Legion to this Container.
	 * 
	 * @param legion
	 */
	public void add(Legion legion) {
		if (legion == null || legion.getLegionName() == null)
			return;

		if (legionsById.put(legion.getLegionId(), legion) != null)
			throw new DuplicateAionObjectException();
		if (legionsByName.put(legion.getLegionName().toLowerCase(), legion) != null)
			throw new DuplicateAionObjectException();
	}

	/**
	 * Remove Legion from this Container.
	 * 
	 * @param legion
	 */
	public void remove(Legion legion) {
		legionsById.remove(legion.getLegionId());
		legionsByName.remove(legion.getLegionName().toLowerCase());
	}

	/**
	 * Get Legion object by objectId.
	 * 
	 * @param legionId
	 *          - legionId of legion.
	 * @return Legion with given ojectId or null if Legion with given legionId is not logged.
	 */
	public Legion get(int legionId) {
		return legionsById.get(legionId);
	}

	/**
	 * Get Legion object by name.
	 * 
	 * @param name
	 *          - name of legion
	 * @return Legion with given name or null if Legion with given name is not logged.
	 */
	public Legion get(String name) {
		return legionsByName.get(name.toLowerCase());
	}

	/**
	 * Returns true if legion is in cached by id
	 * 
	 * @param legionId
	 * @return true or false
	 */
	public boolean contains(int legionId) {
		return legionsById.containsKey(legionId);
	}

	/**
	 * Returns true if legion is in cached by name
	 * 
	 * @param name
	 * @return true or false
	 */
	public boolean contains(String name) {
		return legionsByName.containsKey(name.toLowerCase());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Legion> iterator() {
		return legionsById.values().iterator();
	}

	public void clear() {
		legionsById.clear();
		legionsByName.clear();
	}
}