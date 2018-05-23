/**
 *
 */
package jenjinn.engine.moves;

import static java.util.Collections.unmodifiableSet;
import static jenjinn.engine.enums.CastleZone.BLACK_KINGSIDE;
import static jenjinn.engine.enums.CastleZone.BLACK_QUEENSIDE;
import static jenjinn.engine.enums.CastleZone.WHITE_KINGSIDE;
import static jenjinn.engine.enums.CastleZone.WHITE_QUEENSIDE;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

	private static final Map<BoardSquare, Set<CastleZone>> RIGHTS_MOVE_REMOVAL_MAP = initMoveRemovalMap();
	private static final Map<CastleZone, Set<CastleZone>> RIGHTS_CASTLE_REMOVAL_MAP = initCastleRemovalMap();

	private static Map<BoardSquare, Set<CastleZone>> initMoveRemovalMap()
	{
		final Map<BoardSquare, Set<CastleZone>> removalMap = new HashMap<>();
		removalMap.put(BoardSquare.A1, unmodifiableSet(EnumSet.of(WHITE_QUEENSIDE)));
		removalMap.put(BoardSquare.E1, unmodifiableSet(EnumSet.of(WHITE_QUEENSIDE, WHITE_KINGSIDE)));
		removalMap.put(BoardSquare.H1, unmodifiableSet(EnumSet.of(WHITE_KINGSIDE)));

		removalMap.put(BoardSquare.A8, unmodifiableSet(EnumSet.of(BLACK_QUEENSIDE)));
		removalMap.put(BoardSquare.E8, unmodifiableSet(EnumSet.of(BLACK_QUEENSIDE, BLACK_KINGSIDE)));
		removalMap.put(BoardSquare.H8, unmodifiableSet(EnumSet.of(BLACK_KINGSIDE)));

		return Collections.unmodifiableMap(removalMap);
	}

	private static Map<CastleZone, Set<CastleZone>> initCastleRemovalMap()
	{
		final Map<CastleZone, Set<CastleZone>> removalMap = new HashMap<>();
		removalMap.put(CastleZone.WHITE_KINGSIDE, unmodifiableSet(EnumSet.of(WHITE_QUEENSIDE, WHITE_KINGSIDE)));
		removalMap.put(CastleZone.WHITE_QUEENSIDE, unmodifiableSet(EnumSet.of(WHITE_QUEENSIDE, WHITE_KINGSIDE)));
		removalMap.put(CastleZone.BLACK_KINGSIDE, unmodifiableSet(EnumSet.of(BLACK_QUEENSIDE, BLACK_KINGSIDE)));
		removalMap.put(CastleZone.BLACK_QUEENSIDE, unmodifiableSet(EnumSet.of(BLACK_QUEENSIDE, BLACK_KINGSIDE)));
		return Collections.unmodifiableMap(removalMap);
	}

	public static Set<CastleZone> getRightsRemovedBy(final StandardMove move)
	{
		final Set<CastleZone> rightsRemoved = getRightsRemovedByMovesInvolving(move.getSource());
		rightsRemoved.addAll(getRightsRemovedByMovesInvolving(move.getTarget()));
		return rightsRemoved;
	}

	static Set<CastleZone> getRightsRemovedByMovesInvolving(final BoardSquare square)
	{
		return RIGHTS_MOVE_REMOVAL_MAP.containsKey(square)? RIGHTS_MOVE_REMOVAL_MAP.get(square) : EnumSet.noneOf(CastleZone.class);
	}

}
