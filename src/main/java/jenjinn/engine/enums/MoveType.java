package jenjinn.engine.enums;

/**
 * @author ThomasB
 * @since 20 Jul 2017
 */
public enum MoveType 
{
	STANDARD(0), CASTLE(1), ENPASSANT(2), PROMOTION(3);

	public final int id;

	private MoveType(final int id)
	{
		this.id = id;
	}

	public static MoveType getFromId(final int id)
	{
		for (final MoveType mt : MoveType.values()) {
			if (id == mt.id) {
				return mt;
			}
		}
		throw new AssertionError();
	}
}
