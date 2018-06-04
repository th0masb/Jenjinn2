/**
 *
 */
package jenjinn.engine.bitboards;

import static java.lang.Long.bitCount;
import static jenjinn.engine.bitboards.BitboardUtils.bitboardsIntersect;

import java.util.NoSuchElementException;

import jenjinn.engine.enums.BoardSquare;
import xawd.jflow.iterators.AbstractFlow;
import xawd.jflow.iterators.Flow;

/**
 * @author ThomasB
 *
 */
public final class BitboardIterator extends AbstractFlow<BoardSquare>
{
	private final long source;
	private final int cardinality;
	private int cached = -1, elementsReturned = 0;

	public BitboardIterator(final long source) {
		this.source = source;
		this.cardinality = bitCount(source);
	}

	@Override
	public boolean hasNext()
	{
		return elementsReturned < cardinality;
	}

	@Override
	public BoardSquare next()
	{
		if (hasNext()) {
			final int loopStart = cached < 0 ? 0 : cached + 1;
			for (int i = loopStart; i < 64; i++) {
				if (bitboardsIntersect(1L << i, source)) {
					cached = i;
					elementsReturned++;
					return BoardSquare.of(cached);
				}
			}
			throw new AssertionError();
		}
		else {
			throw new NoSuchElementException();
		}
	}

	@Override
	public void skip()
	{
		next();
	}

	public static Flow<BoardSquare> from(final long bitboard)
	{
		return new BitboardIterator(bitboard);
	}
}
