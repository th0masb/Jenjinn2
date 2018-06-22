/**
 *
 */
package jenjinn.engine.eval;

/**
 * @author ThomasB
 *
 */
public final class PawnTable
{
	private final int size;
	private final long indexer;
	private final Entry[] table;


	public PawnTable(int twoPower)
	{
		if (twoPower < 0) {
			throw new IllegalArgumentException();
		}
		this.size = 1 << twoPower;
		this.indexer = size - 1;
		this.table = new Entry[size];
	}

	Entry get(long hash)
	{
		return table[(int) (hash & indexer)];
	}

	void set(Entry newEntry)
	{
		table[(int) (newEntry.hash & indexer)] = newEntry;
	}

	public static class Entry
	{
		long hash;
		int eval;

		public Entry(long hash, int eval)
		{
			this.hash = hash;
			this.eval = eval;
		}
	}
}
