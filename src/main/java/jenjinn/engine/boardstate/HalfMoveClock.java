/**
 *
 */
package jenjinn.engine.boardstate;

/**
 * @author ThomasB
 *
 */
public final class HalfMoveClock {

	private int halfMoveClock;

	public HalfMoveClock() {
	}

	public int getValue()
	{
		return halfMoveClock;
	}

	public void setValue(final int value)
	{
		halfMoveClock = value;
	}

	public void incrementValue()
	{
		halfMoveClock++;
	}

	public void resetValue()
	{
		halfMoveClock = 0;
	}


}
