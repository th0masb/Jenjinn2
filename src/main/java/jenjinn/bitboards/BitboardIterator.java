/**
 *
 */
package jenjinn.bitboards;

import static java.lang.Long.bitCount;
import static jenjinn.bitboards.BitboardUtils.bitboardsIntersect;

import java.util.NoSuchElementException;
import java.util.OptionalInt;

import com.github.maumay.jflow.iterators.AbstractEnhancedIterator;
import com.github.maumay.jflow.iterators.EnhancedIterator;

import jenjinn.base.Square;

/**
 * @author ThomasB
 *
 */
public final class BitboardIterator extends AbstractEnhancedIterator<Square>
{
	private final long source;
	private int cached = -1, elementsReturned = 0;

	public BitboardIterator(long source)
	{
		super(OptionalInt.of(bitCount(source)));
		this.source = source;
	}

	@Override
	public boolean hasNext()
	{
		return elementsReturned < size.getAsInt();
	}

	@Override
	public Square next()
	{
		if (hasNext()) {
			int loopStart = cached < 0 ? 0 : cached + 1;
			for (int i = loopStart; i < 64; i++) {
				if (bitboardsIntersect(1L << i, source)) {
					cached = i;
					elementsReturned++;
					return Square.of(cached);
				}
			}
			throw new AssertionError();
		} else {
			throw new NoSuchElementException();
		}
	}

	@Override
	public void skip()
	{
		next();
	}

	public static EnhancedIterator<Square> from(long bitboard)
	{
		return new BitboardIterator(bitboard);
	}
}
