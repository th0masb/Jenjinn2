/**
 *
 */
package jenjinn.engine.movesearch;

/**
 * @author ThomasB
 *
 */
public final class TranspositionTable
{
	private final int size;
	private final long indexer;
	private final Entry[] table;

	TranspositionTable(int twoPower)
	{
		if (twoPower < 0) {
			throw new IllegalArgumentException();
		}
		this.size = 1 << twoPower;
		this.indexer = size - 1;
		this.table = new Entry[size];
	}

	Entry get(long positionHash)
	{
		return table[(int) (positionHash & indexer)];
	}

	void set(long positionHash, Entry newEntry)
	{
		table[(int) (positionHash & indexer)] = newEntry;
	}

	public static class Entry
	{
		long positionHash;
		TreeNodeType type;
		int score;
		int notableMoveIndex;
		int depthSearched;

		boolean isPVEntry()
		{
			return type == TreeNodeType.PRINCIPLE_VALUE;
		}

		boolean matches(long queryHash)
		{
			return positionHash == queryHash;
		}
	}
}
