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
	private final Entry[] table;


	public PawnTable(int size)
	{
		if (size < 0) {
			throw new IllegalArgumentException();
		}
		this.size = size;
		this.table = new Entry[size];
	}

	Entry get(long hash)
	{
		return table[(int) (hash % size)];
	}

	void set(Entry newEntry)
	{
		table[(int) (newEntry.hash % size)] = newEntry;
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
