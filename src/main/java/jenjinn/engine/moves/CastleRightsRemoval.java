/**
 *
 */
package jenjinn.engine.moves;

import static jenjinn.engine.enums.CastleZone.BLACK_KINGSIDE;
import static jenjinn.engine.enums.CastleZone.BLACK_QUEENSIDE;
import static jenjinn.engine.enums.CastleZone.WHITE_KINGSIDE;
import static jenjinn.engine.enums.CastleZone.WHITE_QUEENSIDE;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import jenjinn.engine.enums.BoardSquare;
import jenjinn.engine.enums.CastleZone;

/**
 * @author ThomasB
 *
 */
public final class CastleRightsRemoval
{
	private CastleRightsRemoval() {
	}

	private static final Map<BoardSquare, EnumSet<CastleZone>> RIGHTS_REMOVAL_MAP = initRemovalMap();

	public static EnumSet<CastleZone> getRightsRemovedByMovesInvolving(final BoardSquare square)
	{
		return RIGHTS_REMOVAL_MAP.containsKey(square)? RIGHTS_REMOVAL_MAP.get(square) : EnumSet.noneOf(CastleZone.class);
	}

	private static Map<BoardSquare, EnumSet<CastleZone>> initRemovalMap()
	{
		final Map<BoardSquare, EnumSet<CastleZone>> removalMap = new HashMap<>();
		removalMap.put(BoardSquare.A1, EnumSet.of(WHITE_QUEENSIDE));
		removalMap.put(BoardSquare.E1, EnumSet.of(WHITE_QUEENSIDE, WHITE_KINGSIDE));
		removalMap.put(BoardSquare.H1, EnumSet.of(WHITE_KINGSIDE));

		removalMap.put(BoardSquare.A8, EnumSet.of(BLACK_QUEENSIDE));
		removalMap.put(BoardSquare.E8, EnumSet.of(BLACK_QUEENSIDE, BLACK_KINGSIDE));
		removalMap.put(BoardSquare.H8, EnumSet.of(BLACK_KINGSIDE));

		return Collections.unmodifiableMap(removalMap);
	}
}
