/**
 *
 */
package jenjinn.engine.movesearch;

import jenjinn.engine.enums.TreeNodeType;

/**
 * @author ThomasB
 *
 */
public final class TranspositionTable
{
	private final int size;
	private final Entry[] table;

	TranspositionTable(int size)
	{
		if (size < 0) {
			throw new IllegalArgumentException();
		}
		this.size = size;
		this.table = new Entry[size];
	}

	Entry get(long positionHash)
	{
		return table[(int) (positionHash % size)];
	}

	void set(long positionHash, Entry newEntry)
	{
		table[(int) (positionHash % size)] = newEntry;
	}

	public static class Entry
	{
		long positionHash;
		TreeNodeType type;
		int score;
		int notableMoveIndex;
		int depthSearched;
	}
}
