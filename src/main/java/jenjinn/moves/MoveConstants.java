/**
 *
 */
package jenjinn.moves;

import static java.util.Collections.unmodifiableSet;
import static jenjinn.base.CastleZone.BLACK_KINGSIDE;
import static jenjinn.base.CastleZone.BLACK_QUEENSIDE;
import static jenjinn.base.CastleZone.WHITE_KINGSIDE;
import static jenjinn.base.CastleZone.WHITE_QUEENSIDE;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jenjinn.base.CastleZone;
import jenjinn.base.Square;

/**
 * @author ThomasB
 *
 */
public final class MoveConstants {

	private MoveConstants() {}

	static final Set<CastleZone> EMPTY_RIGHTS_SET = unmodifiableSet(EnumSet.noneOf(CastleZone.class));

	static final Map<Square, Set<CastleZone>> STANDARDMOVE_RIGHTS_SETS;
	static
	{
		final Map<Square, Set<CastleZone>> removalMap = new HashMap<>();
		removalMap.put(Square.A1, unmodifiableSet(EnumSet.of(WHITE_QUEENSIDE)));
		removalMap.put(Square.E1, unmodifiableSet(EnumSet.of(WHITE_QUEENSIDE, WHITE_KINGSIDE)));
		removalMap.put(Square.H1, unmodifiableSet(EnumSet.of(WHITE_KINGSIDE)));

		removalMap.put(Square.A8, unmodifiableSet(EnumSet.of(BLACK_QUEENSIDE)));
		removalMap.put(Square.E8, unmodifiableSet(EnumSet.of(BLACK_QUEENSIDE, BLACK_KINGSIDE)));
		removalMap.put(Square.H8, unmodifiableSet(EnumSet.of(BLACK_KINGSIDE)));

		STANDARDMOVE_RIGHTS_SETS = Collections.unmodifiableMap(removalMap);
	}

	static final Set<CastleZone> WHITE_CASTLE_REMOVALS = unmodifiableSet(EnumSet.of(WHITE_QUEENSIDE, WHITE_KINGSIDE));
	static final Set<CastleZone> BLACK_CASTLE_REMOVALS = unmodifiableSet(EnumSet.of(BLACK_QUEENSIDE, BLACK_KINGSIDE));
}
