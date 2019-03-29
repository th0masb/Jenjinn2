/**
 *
 */
package com.github.maumay.jenjinn.bitboards;

import static com.github.maumay.jenjinn.bitboards.Bitboard.intersects;

import java.util.NoSuchElementException;

import com.github.maumay.jenjinn.base.Square;
import com.github.maumay.jflow.impl.AbstractRichIterator;
import com.github.maumay.jflow.impl.KnownSize;
import com.github.maumay.jflow.iterators.RichIterator;

/**
 * @author ThomasB
 *
 */
public final class BitboardIterator extends AbstractRichIterator<Square>
{
	private final long source;
	private final int initialsize;
	private int cached = -1, elementsReturned = 0;

	private BitboardIterator(long source, int initialsize)
	{
		super(KnownSize.of(initialsize));
		this.source = source;
		this.initialsize = initialsize;
	}

	public static RichIterator<Square> from(long bitboard)
	{
		int bitcount = Long.bitCount(bitboard);
		return new BitboardIterator(bitboard, bitcount);
	}

	@Override
	public boolean hasNext()
	{
		return elementsReturned < initialsize;
	}

	@Override
	public Square nextImpl()
	{
		int loopStart = cached + 1;
		for (int i = loopStart; i < 64; i++) {
			if (intersects(1L << i, source)) {
				cached = i;
				elementsReturned++;
				return Square.of(cached);
			}
		}
		throw new NoSuchElementException();
	}

	@Override
	public void skipImpl()
	{
		nextImpl();
	}
}
