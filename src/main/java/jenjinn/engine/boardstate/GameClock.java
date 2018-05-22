/**
 *
 */
package jenjinn.engine.boardstate;

/**
 * @author ThomasB
 *
 */
public final class GameClock {

	private int overallClock, halfMoveClock;

	public GameClock() {
	}

	public int getHalfMoveClockValue()
	{
		return halfMoveClock;
	}

	public int getOverallClockValue()
	{
		return overallClock;
	}

	public void incrementOverallClockValue()
	{
		overallClock++;
	}

	public void decrementOverallClockValue()
	{
		overallClock--;
	}

	public void incrementHalfMoveClockValue()
	{
		halfMoveClock++;
	}

	public void resetHalfMoveClockValue()
	{
		halfMoveClock = 0;
	}
}
