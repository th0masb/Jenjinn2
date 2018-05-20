/**
 *
 */
package jenjinn.engine.misc;

import static java.lang.Long.toHexString;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ThomasB
 */
public final class PieceLocations
{
	private static final Pattern LOCATION_PATTERN = Pattern.compile(":[abcdef0-9]+");

	private final long whiteLocations, blackLocations;

	public PieceLocations(final long whiteLocations, final long blackLocations)
	{
		this.whiteLocations = whiteLocations;
		this.blackLocations = blackLocations;
	}

	public long getWhiteLocations()
	{
		return whiteLocations;
	}

	public long getBlackLocations()
	{
		return blackLocations;
	}

	@Override
	public String toString()
	{
		return new StringBuilder("PieceLocations[white:")
				.append(toHexString(whiteLocations))
				.append("|black:")
				.append(toHexString(blackLocations))
				.append("]")
				.toString();
	}

	public static PieceLocations reconstructFrom(final String stringRepresentation)
	{
		final Matcher matcher = LOCATION_PATTERN.matcher(stringRepresentation);
		try {
			matcher.find();
			final long whiteLocations = Long.parseUnsignedLong(matcher.group().substring(1), 16);
			matcher.find();
			final long blackLocations = Long.parseUnsignedLong(matcher.group().substring(1), 16);
			return new PieceLocations(whiteLocations, blackLocations);
		}
		catch (final IllegalStateException ex) {
			System.err.println("error parsing: " + stringRepresentation);
			throw new AssertionError(ex);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (blackLocations ^ (blackLocations >>> 32));
		result = prime * result + (int) (whiteLocations ^ (whiteLocations >>> 32));
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final PieceLocations other = (PieceLocations) obj;
		if (blackLocations != other.blackLocations)
			return false;
		if (whiteLocations != other.whiteLocations)
			return false;
		return true;
	}
}
